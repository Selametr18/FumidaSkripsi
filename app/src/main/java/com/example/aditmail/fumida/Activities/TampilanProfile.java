package com.example.aditmail.fumida.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.aditmail.fumida.Settings.Konfigurasi;
import com.example.aditmail.fumida.R;
import com.example.aditmail.fumida.Settings.PrefManager;
import com.example.aditmail.fumida.Settings.RequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class TampilanProfile extends AppCompatActivity implements View.OnClickListener {

    private ConnectivityManager conMgr;

    private final String id_pegawai = TampilanMenuUtama.id_pegawai;

    private EditText edt_NamaLengkap_Profil, edt_NIK_Profil, edt_Email_Profil, edt_HP_Profil, edt_Username_Profil;

    private Button btn_UpdateProfil, btn_UpdatePassword;
    private CardView cdProfil, cdPassword;

    @SuppressLint("StaticFieldLeak")
    protected ImageView imgPicture;
    protected Bitmap getFoto;
    private String ConvertImage = "";
    private String GetImageNameFromEditText = "";

    private TextView txt_Edit;

    ProgressDialog loading;

    //SharedPreferences
    protected PrefManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tampilan_profile);

        //Sharedpreferences
        session = new PrefManager(getApplicationContext());

        //sharedPreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);

        edt_NamaLengkap_Profil = findViewById(R.id.editText_NamaLengkap_Update);
        edt_NIK_Profil = findViewById(R.id.editText_NIK_Update);
        edt_Email_Profil = findViewById(R.id.editText_Email_Update);
        edt_HP_Profil = findViewById(R.id.editText_HP_Update);
        edt_Username_Profil = findViewById(R.id.editText_Username_Update);

        btn_UpdateProfil = findViewById(R.id.button_UpdateProfil);
        btn_UpdatePassword = findViewById(R.id.button_UpdatePassword);
        cdPassword = findViewById(R.id.cd_Password);
        cdProfil = findViewById(R.id.cd_Profil);

        imgPicture = findViewById(R.id.imgPicture_Update);

        txt_Edit = findViewById(R.id.textView_EditDataDiri);

        btn_UpdateProfil.setOnClickListener(this);
        btn_UpdatePassword.setOnClickListener(this);

        imgPicture.setEnabled(false);

        txt_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edt_NamaLengkap_Profil.setFocusableInTouchMode(true);   edt_NamaLengkap_Profil.setCursorVisible(true);
                edt_NIK_Profil.setFocusableInTouchMode(true);           edt_NIK_Profil.setCursorVisible(true);
                edt_Email_Profil.setFocusableInTouchMode(true);         edt_Email_Profil.setCursorVisible(true);
                edt_HP_Profil.setFocusableInTouchMode(true);            edt_HP_Profil.setCursorVisible(true);
                edt_Username_Profil.setFocusableInTouchMode(true);      edt_Username_Profil.setCursorVisible(true);

                edt_NamaLengkap_Profil.requestFocus();

                cdProfil.setVisibility(View.VISIBLE);           cdPassword.setVisibility(View.VISIBLE);
                txt_Edit.setVisibility(View.GONE);
                imgPicture.setEnabled(true);
            }
        });

        imgPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });

        loading = new ProgressDialog(this);
        loading.setCanceledOnTouchOutside(false);
        loading.setCancelable(true);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conMgr != null) {
            if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
                getDataProfil();
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection! Pastikan Anda Terhubung dengan Internet",
                        Toast.LENGTH_LONG).show();
            }
        }

    }

    private void showPictureDialog(){
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(TampilanProfile.this);
        pictureDialog.setTitle("Pilih Aksi");

        String[]pictureDialogItems = {
                "Pilih Foto dari Galeri", "Ambil Foto dari Kamera"};

        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
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
    private void openGaleri(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    private void openKamera(){
        Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);
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

        String getNama;
        getNama = edt_NamaLengkap_Profil.getText().toString();

        if (requestCode == 2){
            if (resultCode == RESULT_OK){
                Bitmap bitmap_PickFoto = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                Log.e("bitmap", String.valueOf(bitmap_PickFoto));
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                if(bitmap_PickFoto != null) {
                    bitmap_PickFoto.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
                    imgPicture.setImageBitmap(bitmap_PickFoto);
                }

                GetImageNameFromEditText = "_Pegawai_" + getNama + "_" + dateNow + ".png";
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
                    GetImageNameFromEditText = "_Pegawai_" + getNama + "_" + dateNow + ".png";

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void update() {
        final String namaLengkap = edt_NamaLengkap_Profil.getText().toString();
        final String nik = edt_NIK_Profil.getText().toString();
        final String email = edt_Email_Profil.getText().toString();
        final String hp = edt_HP_Profil.getText().toString();
        final String username = edt_Username_Profil.getText().toString();

        if (TextUtils.isEmpty(namaLengkap)) {
            edt_NamaLengkap_Profil.setError("Harap Masukkan Nama Lengkap Anda");
            edt_NamaLengkap_Profil.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(nik)) {
            edt_NIK_Profil.setError("Harap Masukkan NIK Anda");
            edt_NIK_Profil.requestFocus();
            return;
        }

        if (nik.contains(" ")) {
            edt_NIK_Profil.setError("Tanda Spasi Tidak Boleh Digunakan!");
            edt_NIK_Profil.requestFocus();
            return;
        }

        /*if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edt_Email_Profil.setError("Masukkan Email yang Valid!");
            edt_Email_Profil.requestFocus();
            return;
        }*/

        if (hp.trim().length() <= 5) {
            edt_HP_Profil.setError("Harap Masukkan Nomor Handphone yang Valid");
            edt_HP_Profil.requestFocus();
            return;
        }

        if (hp.contains(" ")) {
            edt_HP_Profil.setError("Tanda Spasi Tidak Boleh Digunakan!");
            edt_HP_Profil.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(username)) {
            edt_Username_Profil.setError("Harap Masukkan Username Anda");
            edt_Username_Profil.requestFocus();
            return;
        }

        if (username.contains(" ")) {
            edt_Username_Profil.setError("Tanda Spasi Tidak Boleh Digunakan!");
            edt_Username_Profil.requestFocus();
            return;
        }

        if (GetImageNameFromEditText != null && imgPicture.getDrawable() != null) {
            getFoto = ((BitmapDrawable)imgPicture.getDrawable()).getBitmap();
            ByteArrayOutputStream stream_foto_satu = new ByteArrayOutputStream();
            getFoto.compress(Bitmap.CompressFormat.PNG, 60, stream_foto_satu);
            byte[] imageInByte_foto_satu = stream_foto_satu.toByteArray();

            ConvertImage = Base64.encodeToString(imageInByte_foto_satu, Base64.DEFAULT);
        }

        if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {

            @SuppressLint("StaticFieldLeak")
            class updateProfil extends AsyncTask<Void, Void, String> {

                @Override
                protected void onPreExecute() {
                    loading.setMessage("Melakukan Perbaruan Data Diri, Harap Tunggu...");
                    loading.show();
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(String s) {
                    loading.dismiss();
                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

                    AlertDialog.Builder builder = new AlertDialog.Builder(TampilanProfile.this);
                    builder.setMessage(s)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    session.updateProfil(namaLengkap, username, nik, "uploads_Picture/" +GetImageNameFromEditText);
                                    recreate();
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
                    params.put(Konfigurasi.KEY_TAG_ID, id_pegawai);
                    params.put(Konfigurasi.KEY_UPDATE_NAMA, namaLengkap);
                    params.put(Konfigurasi.KEY_UPDATE_NIK, nik);
                    params.put(Konfigurasi.KEY_UPDATE_EMAIL, email);
                    params.put(Konfigurasi.KEY_UPDATE_HP, hp);
                    params.put(Konfigurasi.KEY_UPDATE_USERNAME, username);

                    params.put(Konfigurasi.KEY_REG_IMAGE_TAG, GetImageNameFromEditText);
                    params.put(Konfigurasi.KEY_REG_IMAGE_NAME, ConvertImage);

                    RequestHandler rh = new RequestHandler();
                    return rh.sendPostRequest(Konfigurasi.URL_UPDATE_PROFIL, params);
                }
            }

            updateProfil update = new updateProfil();
            update.execute();

            loading.dismiss();

        } else{
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        if(v == btn_UpdateProfil){
            AlertDialog.Builder builder = new AlertDialog.Builder(TampilanProfile.this);

            builder.setCancelable(true);
            builder.setTitle("Apakah Anda Yakin Melakukan Perubahan Data Diri? ");
            builder.setMessage("Apabila Yakin Tekan 'OK'!");

            builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    update();
                }
            });
            builder.show();
        }

        else if (v == btn_UpdatePassword){
            Intent ubah_pass = new Intent(TampilanProfile.this, TampilanUpdatePassword.class);
            startActivity(ubah_pass);
        }
    }

    private void getDataProfil(){
        @SuppressLint("StaticFieldLeak")
        class GetDataProfil extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {
                loading.setMessage("Mengambil Data");
                loading.show();
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                loading.dismiss();
                showDataProfil(s);
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();
                params.put(Konfigurasi.KEY_TAG_ID, id_pegawai);
                RequestHandler rh = new RequestHandler();
                return rh.sendPostRequest(Konfigurasi.URL_TAMPIL_DATA_PROFIL, params);
            }
        }
        GetDataProfil ge = new GetDataProfil();
        ge.execute();
    }

    private void showDataProfil(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray(Konfigurasi.TAG_JSON_ARRAY);
            JSONObject c = result.getJSONObject(0);

            String NamaLengkap = c.getString(Konfigurasi.TAG_GET_NAMA);
            String NIK = c.getString(Konfigurasi.TAG_GET_NIK);
            String Email = c.getString(Konfigurasi.TAG_GET_EMAIL);
            String NoHP= c.getString(Konfigurasi.TAG_GET_HP);
            String Username = c.getString(Konfigurasi.TAG_GET_USERNAME);
            String imgPath = c.getString(Konfigurasi.KEY_GET_IMG);

            edt_NamaLengkap_Profil.setText(NamaLengkap);
            edt_NIK_Profil.setText(NIK);
            edt_Email_Profil.setText(Email);
            edt_HP_Profil.setText(NoHP);
            edt_Username_Profil.setText(Username);

            try {
                Glide.with(this)
                        .load(Konfigurasi.url_image + imgPath)
                        //.error(Glide.with(imgPicture).load(R.drawable.ic_person))
                        .into(imgPicture);
            }catch (NullPointerException e){
                e.printStackTrace();
                Log.e("tag", String.valueOf(e));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
