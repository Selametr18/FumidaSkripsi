package com.example.aditmail.fumida.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.ProgressDialog;

import android.widget.ImageView;
import android.widget.Toast;

import com.example.aditmail.fumida.Settings.Konfigurasi;
import com.example.aditmail.fumida.R;
import com.example.aditmail.fumida.Settings.RequestHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class TampilanRegistrasi extends AppCompatActivity implements View.OnClickListener {

    ConnectivityManager conMgr;
    ProgressDialog loading;

    protected EditText edt_NamaLengkap, edt_NIK, edt_Email, edt_HP, edt_Username,
            edt_Password, edt_CekPassword;

    @SuppressLint("StaticFieldLeak")
    protected ImageView imgPicture;
    protected Bitmap getFoto;
    private String ConvertImage = "";
    private String GetImageNameFromEditText = "";

    protected Button btn_Registrasi;

    public final static String path_fumida = "com.example.aditmail.fumida";
    public final static String Pesan_Extra = path_fumida + ".Pesan";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tampilan_registrasi);

        edt_NamaLengkap = findViewById(R.id.editText_NamaLengkap);
        edt_NIK = findViewById(R.id.editText_NIK);
        edt_Email = findViewById(R.id.editText_Email);
        edt_HP = findViewById(R.id.editText_HP);
        edt_Username = findViewById(R.id.editText_Username);
        edt_Password = findViewById(R.id.editText_Password);
        edt_CekPassword = findViewById(R.id.editText_CekPassword);

        imgPicture = findViewById(R.id.imgPicture);
        imgPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });

        btn_Registrasi = findViewById(R.id.button_Registrasi);
        btn_Registrasi.setOnClickListener(this);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        loading = new ProgressDialog(this);
        loading.setCancelable(true);
        loading.setCanceledOnTouchOutside(false);

    }

    private void showPictureDialog() {
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(TampilanRegistrasi.this);
        pictureDialog.setTitle("Pilih Aksi");

        String[] pictureDialogItems = {
                "Pilih Foto dari Galeri", "Ambil Foto dari Kamera"};

        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        openGaleri();
                        break;

                    case 1:
                        openKamera();
                        break;
                }
            }
        });
        pictureDialog.show();
    }

    //Buka Foto Galeri
    private void openGaleri() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        intent.putExtra(Pesan_Extra, "Foto_Satu");

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    private void openKamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 2);
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //get Date Now
        String dateNow;
        dateNow = new SimpleDateFormat("dd-MM-yy_hh.mm.ss", Locale.getDefault()).format(new Date());

        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                Bitmap bitmap_PickFoto = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                if (bitmap_PickFoto != null) {
                    bitmap_PickFoto.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
                }
                imgPicture.setImageBitmap(bitmap_PickFoto);
                GetImageNameFromEditText = "_Pegawai_" + dateNow + ".png";
            }
        }

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                try {
                    Bitmap bitmap_Foto1 = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    //untuk decompress size foto
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap_Foto1, 300, 400, false);
                    scaled = getResizedBitmap(scaled, 300);
                    imgPicture.setImageBitmap(scaled);
                    GetImageNameFromEditText = "_Pegawai_" + dateNow + ".png";

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void registrasi() {
        final String namaLengkap = edt_NamaLengkap.getText().toString();
        final String nik = edt_NIK.getText().toString();
        final String email = edt_Email.getText().toString();
        final String hp = edt_HP.getText().toString();
        final String username = edt_Username.getText().toString();
        final String password = edt_Password.getText().toString();
        final String cekPassword = edt_CekPassword.getText().toString();

        if (TextUtils.isEmpty(namaLengkap)) {
            edt_NamaLengkap.setError("Harap Masukkan Nama Lengkap Anda");
            edt_NamaLengkap.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(nik)) {
            edt_NIK.setError("Harap Masukkan NIK Anda");
            edt_NIK.requestFocus();
            return;
        }

        if (nik.contains(" ")) {
            edt_NIK.setError("Tanda Spasi Tidak Boleh Digunakan!");
            edt_NIK.requestFocus();
            return;
        }

//        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            edt_Email.setError("Masukkan Email yang Valid!");
//            edt_Email.requestFocus();
//            return;
//        }

        if (hp.trim().length() <= 5) {
            edt_HP.setError("Harap Masukkan Nomor Handphone yang Valid");
            edt_HP.requestFocus();
            return;
        }

        if (hp.contains(" ")) {
            edt_HP.setError("Tanda Spasi Tidak Boleh Digunakan!");
            edt_HP.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(username)) {
            edt_Username.setError("Harap Masukkan Username Anda");
            edt_Username.requestFocus();
            return;
        }

        if (username.contains(" ")) {
            edt_Username.setError("Tanda Spasi Tidak Boleh Digunakan!");
            edt_Username.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            edt_Password.setError("Harap Masukkan Password Anda");
            edt_Password.requestFocus();
            return;
        }

        if (password.contains(" ")) {
            edt_Password.setError("Tanda Spasi Tidak Boleh Digunakan!");
            edt_Password.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(cekPassword)) {
            edt_CekPassword.setError("Harap Masukkan Password Anda");
            edt_CekPassword.requestFocus();
            return;
        }

        if (cekPassword.contains(" ")) {
            edt_CekPassword.setError("Tanda Spasi Tidak Boleh Digunakan!");
            edt_CekPassword.requestFocus();
            return;
        }

        if (!password.equals(cekPassword)) {
            edt_CekPassword.setError("Password Tidak Sesuai! Harap Cek Kembali");
            return;
        }

        if (GetImageNameFromEditText != null && imgPicture.getDrawable() != null){
            getFoto = ((BitmapDrawable)imgPicture.getDrawable()).getBitmap();
            ByteArrayOutputStream stream_foto_satu = new ByteArrayOutputStream();
            getFoto.compress(Bitmap.CompressFormat.PNG, 60, stream_foto_satu);
            byte[] imageInByte_foto_satu = stream_foto_satu.toByteArray();
            ConvertImage = Base64.encodeToString(imageInByte_foto_satu, Base64.DEFAULT);
        }

        if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {

            //Menampilkan Alert Dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(TampilanRegistrasi.this);

            builder.setCancelable(true);
            builder.setTitle("Apakah Anda Yakin Data Diri Yang Anda Masukkan Benar dan Tepat?");
            builder.setMessage("Apabila Yakin Tekan 'OK'!");
            Log.e("tag", "test");

            builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    @SuppressLint("StaticFieldLeak")
                    class registrasi extends AsyncTask<Void, Void, String> {

                        @Override
                        protected void onPreExecute() {
                            loading.setMessage("Melakukan Registrasi, Harap Tunggu...");
                            loading.show();
                            super.onPreExecute();
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            loading.dismiss();
                            Toast.makeText(TampilanRegistrasi.this, s, Toast.LENGTH_LONG).show();

                            AlertDialog.Builder builder = new AlertDialog.Builder(TampilanRegistrasi.this);
                            builder.setMessage(s)
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(TampilanRegistrasi.this, TampilanLogin.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                            super.onPostExecute(s);
                        }

                        @Override
                        protected String doInBackground(Void... v) {

                            //String nya musti sama dengan yang ada di konfigurasi...
                            HashMap<String, String> params = new HashMap<>();
                            params.put(Konfigurasi.KEY_REG_NAMA, namaLengkap);
                            params.put(Konfigurasi.KEY_REG_NIK, nik);
                            params.put(Konfigurasi.KEY_REG_EMAIL, email);
                            params.put(Konfigurasi.KEY_REG_HP, hp);
                            params.put(Konfigurasi.KEY_REG_USERNAME, username);
                            params.put(Konfigurasi.KEY_REG_PASSWORD, password);

                            params.put(Konfigurasi.KEY_REG_IMAGE_TAG, username + GetImageNameFromEditText);
                            params.put(Konfigurasi.KEY_REG_IMAGE_NAME, ConvertImage);

                            RequestHandler rh = new RequestHandler();
                            return rh.sendPostRequest(Konfigurasi.URL_REGISTRASI, params);
                        }


                    }
                    registrasi regis = new registrasi();
                    regis.execute();

                }
            });
            builder.show();

        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View v) {
        if (v == btn_Registrasi) {
            registrasi();

        }
    }
}
