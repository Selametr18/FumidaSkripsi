package com.example.aditmail.fumida.Fumigasi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.aditmail.fumida.Activities.OpenMaps;
import com.example.aditmail.fumida.R;
import com.example.aditmail.fumida.Activities.SignatureActivity;
import com.example.aditmail.fumida.Settings.TrackGPS;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class FragmentFormFumigasi extends Fragment implements View.OnClickListener {

    private static final String TAG = "TabFormFumigasi";

    public final static String path_fumida = "com.example.aditmail.fumida";
    public final static String Pesan_Extra = path_fumida + ".Pesan" ;

    //Bagian 1: Data Diri
    public final static String FumigasiRev_ID_Client_Termite = path_fumida + ".ClientID";
    public final static String FumigasiRev_Nama_Client_Termite = path_fumida + ".NamaClient";
    public final static String FumigasiRev_Kategori_Client_Termite = path_fumida + ".Kategori";
    public final static String FumigasiRev_Alamat_Client_Termite = path_fumida + ".Alamat";
    public final static String FumigasiRev_Hp_Client_Termite = path_fumida + ".Hp";
    public final static String FumigasiRev_Email_DB = path_fumida + ".Email_DB";

    //Bagian 2 : Jenis Kerja
    public final static String FumigasiRev_GasFumigasi = path_fumida + ".GasFumigasi";

    //Bagian 3 : Luas
    public final static String FumigasiRev_Indoor_DB = path_fumida + ".Indoor_DB";
    public final static String FumigasiRev_Outdoor_DB = path_fumida + ".Outdoor_DB";

    //Bagian 4: Harga, Status dan Catatan
    public final static String FumigasiRev_PenawaranHarga_DB = path_fumida + ".PenawaranHarga_DB";
    public final static String FumigasiRev_TotalHarga_DB = path_fumida + ".TotalHarga_DB";
    public final static String FumigasiRev_Status_Termite = path_fumida + ".Status_Termite";
    public final static String FumigasiRev_Catatan_DB = path_fumida + ".Catatan_DB";

    //Bagian 7: Keterangan PDF
    public final static String FumigasiRev_Keterangan_Termite = path_fumida + ".Keterangan_Termite";

    public final static String FumigasiRev_LatLong_Consultant = path_fumida + ".LatLong_Consultant";
    public final static String FumigasiRev_LatLong_Alamat_Pelanggan = path_fumida + ".LatLong_AlamatPelanggan";

    //--------------- Bagian I: Identitas Pelanggan ---------------
    private EditText edt_ClientID, edt_NamaPelanggan, edt_Alamat, edt_NoHP, edt_Email;
    private Spinner spr_Kategori;

    private ImageView imgPinCircle;
    protected Double lat,lng;

    /*FOR GPS*/
    protected String latlong_Consultant = "0,0";
    protected String latlong_AlamatPelanggan = "0,0";
    protected TrackGPS gps;
    double longitude;
    double latitude;
    //Untuk Simpan Nama TTD
    public static String namaPelanggan = "";
    //--------------- Bagian I: Identitas Pelanggan ---------------

    //-------------- Bagian II : Fumigasi Control --------------
    private Spinner spr_GasFumigasi;
    //-------------- Bagian II : Fumigasi Control --------------

    //--------------- Bagian III: Area Lokasi dan Lantai---------------
    private EditText edt_LuasBangunan, edt_LuasOutdoor;
    //--------------- Bagian III: Area Lokasi dan Lantai---------------

    //--------------- Bagian iV: Kontrak, Kerjasama dan Catatan ---------------
    private EditText edt_PenawaranHarga, edt_GrandTotal;
    private RadioGroup rdg_StatusKerjaSama;
    private EditText edt_CatatanTambahan;
    private RadioButton rdb_Berhasil;
    private String status_kerjasama="";
    //--------------- Bagian iV: Kontrak, Kerjasama dan Catatan ---------------

    //--------------- Bagian V: Tanda Tangan ---------------
    private Button btn_DialogTTD_Pelanggan, btn_DialogTTD_Consultant;
    private CheckBox chk_PernyataanPelanggan, chk_PernyataanConsultant;
    @SuppressLint("StaticFieldLeak")
    public static ImageView img_ttdPelanggan, img_ttdConsultant;
    protected Bitmap bitmap_FumigasiConsultant, bitmap_FumigasiPelanggan;
    //--------------- Bagian V: Tanda Tangan ---------------

    //--------------- Bagian VI: Upload Image---------------
    private Button btn_PilihFotoSatu, btn_PilihFotoDua, btn_PilihFotoTiga, btn_PilihFotoEmpat;

    @SuppressLint("StaticFieldLeak")
    public static ImageView img_FotoSatu, img_FotoDua, img_FotoTiga, img_FotoEmpat;
    private Space spc_Satu, spc_Dua, spc_Tiga;
    //--------------- Bagian VI: Upload Image---------------

    //Button Simpan
    private Button btn_SimpanSurvey;

    LocationManager locationManager;

    //Request Code..
    private final static int REQ_TTD_PELANGGAN = 1;
    private final static int REQ_TTD_CONSULTANT = 2;

    private final static int REQ_KAMERA_SATU = 3;
    private final static int REQ_KAMERA_DUA = 4;
    private final static int REQ_KAMERA_TIGA = 5;
    private final static int REQ_KAMERA_EMPAT = 6;

    private final static int REQ_GALERI_SATU = 7;
    private final static int REQ_GALERI_DUA = 8;
    private final static int REQ_GALERI_TIGA = 9;
    private final static int REQ_GALERI_EMPAT = 10;

    private final static int REQ_PLACE_PICKER = 11;

    //Untuk Create View Fragment
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_form_fumigasi, container, false);

        InitData();
        getCurrentLocationRoute();
        CekRadioGroup();
        TextWatcher();
        CekTandaTangan();
        return view;
    }

    @Override
    public void onStart(){
        OpenMaps();
        OpenPhoto();
        super.onStart();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if (v == btn_SimpanSurvey) {
            simpan_pelanggan();
        }
    }

    private void InitData() {
        //Bagian Identitas Pelanggan
        edt_ClientID = view.findViewById(R.id.editText_ClientID);
        edt_NamaPelanggan = view.findViewById(R.id.editText_NamaPelanggan);
        edt_Alamat = view.findViewById(R.id.editText_Alamat);
        edt_NoHP= view.findViewById(R.id.editText_NoHP);
        edt_Email= view.findViewById(R.id.editText_Email);
        spr_Kategori= view.findViewById(R.id.spinner_Kategori);
        imgPinCircle = view.findViewById(R.id.imgPin);

        //Get Nama Pelanggan
        namaPelanggan = edt_NamaPelanggan.getText().toString();

        //Fumigasi
        spr_GasFumigasi = view.findViewById(R.id.spinner_GasFumigasi);

        //Bagian Area Lokasi
        edt_LuasBangunan = view.findViewById(R.id.editText_LuasBangunan);  edt_LuasOutdoor = view.findViewById(R.id.editText_LuasOutdoor);

        //Kontrak dan Penawaran Harga
        edt_PenawaranHarga = view.findViewById(R.id.editText_PenawaranHarga_Fumigasi);
        edt_GrandTotal = view.findViewById(R.id.editText_GrandTotal_Fumigasi);
        rdg_StatusKerjaSama = view.findViewById(R.id.RadioGroup_Status);
        rdb_Berhasil = view.findViewById(R.id.radioButton_Berhasil);

        //Bagian Catatan Tambahan
        edt_CatatanTambahan = view.findViewById(R.id.editText_CatatanTambahan_Fumigasi);

        //Button Simpan
        btn_SimpanSurvey = view.findViewById(R.id.button_SimpanSurvey_Fumigasi);
        btn_SimpanSurvey.setOnClickListener(this);

        //Bagian TTD - Signature
        btn_DialogTTD_Pelanggan = view.findViewById(R.id.button_GetTTD_Pelanggan_Fumigasi); btn_DialogTTD_Consultant = view.findViewById(R.id.button_GetTTD_Consultant_Fumigasi);
        img_ttdPelanggan = view.findViewById(R.id.imageView_TTD_Pelanggan_Fumigasi);  img_ttdConsultant = view.findViewById(R.id.imageView_TTD_Consultant_Fumigasi);
        chk_PernyataanPelanggan = view.findViewById(R.id.checkBox_PernyataanCustomer_Fumigasi);  chk_PernyataanConsultant = view.findViewById(R.id.checkBox_PernyataanConsultant_Fumigasi);

        //Bagian upload Foto
        btn_PilihFotoSatu = view.findViewById(R.id.button_FotoSatu);    img_FotoSatu = view.findViewById(R.id.imageView_FotoSatu);
        btn_PilihFotoDua = view.findViewById(R.id.button_FotoDua);    img_FotoDua = view.findViewById(R.id.imageView_FotoDua);
        btn_PilihFotoTiga = view.findViewById(R.id.button_FotoTiga);    img_FotoTiga = view.findViewById(R.id.imageView_FotoTiga);
        btn_PilihFotoEmpat = view.findViewById(R.id.button_FotoEmpat);    img_FotoEmpat = view.findViewById(R.id.imageView_FotoEmpat);

        spc_Satu = view.findViewById(R.id.Space_Satu);    spc_Dua = view.findViewById(R.id.Space_Dua);
        spc_Tiga = view.findViewById(R.id.Space_Tiga);

        if(getActivity()!= null)
            locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    private void OpenMaps(){
        imgPinCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    isStoragePermissionGranted();
                } else {
                    OpenPlacePicker();
                }
            }
        });
    }

    //Opening Maps...
    private void OpenPlacePicker(){
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent openMaps = new Intent(getContext(), OpenMaps.class);
            startActivityForResult(openMaps, REQ_PLACE_PICKER);
        }else{
            showGPSDisabledAlertToUser();
        }
    }

    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void getCurrentLocationRoute(){
        gps = new TrackGPS(getContext());
        if(gps.canGetLocation()){
            longitude = gps.getLongitude();
            latitude = gps .getLatitude();

            latlong_Consultant = latitude + "," + longitude;
            latlong_AlamatPelanggan = latitude + "," + longitude;

            Log.e("tag", latlong_Consultant);
        }else{
            gps.showSettingsAlert();
        }
    }

    private void OpenPhoto(){
        //Open galeri
        btn_PilihFotoSatu.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_FotoSatu.setVisibility(View.VISIBLE);   spc_Satu.setVisibility(View.VISIBLE);
                img_FotoDua.setVisibility(View.VISIBLE);    btn_PilihFotoDua.setVisibility(View.VISIBLE);
                showPictureDialog_Satu();
            }
        });

        btn_PilihFotoDua.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_FotoTiga.setVisibility(View.VISIBLE);   spc_Dua.setVisibility(View.VISIBLE);
                btn_PilihFotoTiga.setVisibility(View.VISIBLE);
                showPictureDialog_Dua();
            }
        });

        btn_PilihFotoTiga.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_FotoEmpat.setVisibility(View.VISIBLE); spc_Tiga.setVisibility(View.VISIBLE);
                btn_PilihFotoEmpat.setVisibility(View.VISIBLE);
                showPictureDialog_Tiga();
            }
        });

        btn_PilihFotoEmpat.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog_Empat();
            }
        });
    }

    private void showPictureDialog_Satu(){
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(getContext());
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

    private void showPictureDialog_Dua(){
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Pilih Aksi");

        String[]pictureDialogItems = {
                "Pilih Foto dari Galeri", "Ambil Foto dari Kamera"};

        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_GALERI_DUA);
                        break;

                    case 1:
                        Intent intent_cameraDua = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent_cameraDua, REQ_KAMERA_DUA);
                        break;
                }
            }
        });
        pictureDialog.show();
    }

    private void showPictureDialog_Tiga(){
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Pilih Aksi");

        String[]pictureDialogItems = {
                "Pilih Foto dari Galeri", "Ambil Foto dari Kamera"};

        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_GALERI_TIGA);
                        break;

                    case 1:
                        Intent intent_cameraDua = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent_cameraDua, REQ_KAMERA_TIGA);
                        break;
                }
            }
        });
        pictureDialog.show();
    }

    private void showPictureDialog_Empat(){
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Pilih Aksi");

        String[]pictureDialogItems = {
                "Pilih Foto dari Galeri", "Ambil Foto dari Kamera"};

        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_GALERI_EMPAT);
                        break;

                    case 1:
                        Intent intent_cameraDua = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent_cameraDua, REQ_KAMERA_EMPAT);
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

        intent.putExtra(Pesan_Extra, "Foto_Satu");

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_GALERI_SATU);
    }

    private void openKamera(){
        Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQ_KAMERA_SATU);
    }

    //Request Location, External and Camera..
    public void isStoragePermissionGranted(){
        if (Build.VERSION.SDK_INT >=23){
            if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()),android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                OpenPlacePicker();
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},99);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]permissons, @NonNull int[]grantResults){
        super.onRequestPermissionsResult(requestCode,permissons,grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            OpenPlacePicker();
        } else{
            Toast.makeText(getContext(), "Aplikasi tidak diberikan izin untuk menyimpan data kedalam penyimpanan. Sehingga aplikasi tidak dapat berjalan normal." +
                    "Mohon pertimbangkan untuk memberikan Izin akses.", Toast.LENGTH_LONG).show();
        }
    }

    private void CekRadioGroup() {
        rdg_StatusKerjaSama.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton_Berhasil:
                        status_kerjasama = "Berhasil";
                        break;
                    case R.id.radioButton_Gagal:
                        status_kerjasama = "Gagal";
                        break;
                    case R.id.radioButton_Menunggu:
                        status_kerjasama = "Menunggu";
                        Toast.makeText(getContext(), "Catatan:\n" +
                                "Pastikan untuk Melakukan Konfirmasi Kembali Dalam Beberapa Waktu Kedepan Kepada Pelanggan/Perusahaan", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }

    private String kalkulasi_total(){
        double total, a;

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        if (!edt_PenawaranHarga.getText().toString().equals("") && edt_PenawaranHarga.getText().length() > 0){
            String penawaran = edt_PenawaranHarga.getText().toString();
            a = Double.parseDouble(penawaran);
            formatRupiah.format(a);
        } else {
            a = 0;
        }

        total = a;

        return(formatRupiah.format(total));
    }

    private void TextWatcher(){
        edt_PenawaranHarga.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edt_GrandTotal.setText(kalkulasi_total());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edt_NoHP.addTextChangedListener(new TextWatcher() {

            int keyDel;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edt_NoHP.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                        if (keyCode == KeyEvent.KEYCODE_DEL) {
                            keyDel = 1;
                        }
                        return false;
                    }
                });

                String currentString = edt_NoHP.getText().toString();
                int currentLength = edt_NoHP.getText().length();

                if (currentLength == 5 || currentLength == 10) {
                    keyDel = 1;
                }

                if (keyDel == 0) {
                    if (currentLength == 4 || currentLength == 9) {
                        edt_NoHP.setText(edt_NoHP.getText() + "-");
                        edt_NoHP.setSelection(edt_NoHP.getText().length());
                    }
                } else {
                    if (currentLength != 5 && currentLength != 10) {
                        keyDel = 0;
                    } else if ((currentLength == 5 || currentLength == 10)
                            && !"-".equals(currentString.substring(currentLength - 1, currentLength))) {
                        edt_NoHP.setText(currentString.substring(0, currentLength - 1) + "-"
                                + currentString.substring(currentLength - 1, currentLength));
                        edt_NoHP.setSelection(edt_NoHP.getText().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void CekTandaTangan(){
        btn_DialogTTD_Pelanggan.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), SignatureActivity.class);
                i.putExtra(Pesan_Extra, "TTD_PelangganFumigasi");
                startActivityForResult(i,REQ_TTD_PELANGGAN);
            }
        });

        btn_DialogTTD_Consultant.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(getContext(), SignatureActivity.class);
                a.putExtra(Pesan_Extra, "TTD_ConsultantFumigasi");
                startActivityForResult(a,REQ_TTD_CONSULTANT);
            }
        });


        chk_PernyataanPelanggan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    btn_DialogTTD_Pelanggan.setEnabled(true);
                }else{
                    btn_DialogTTD_Pelanggan.setEnabled(false);
                }
            }
        });

        chk_PernyataanConsultant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    btn_DialogTTD_Consultant.setEnabled(true);
                }else{
                    btn_DialogTTD_Consultant.setEnabled(false);
                }
            }
        });
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
        if (requestCode == REQ_TTD_PELANGGAN) {
            //getActivity();
            if (resultCode == Activity.RESULT_OK) {
                //Nampilin hasil ttd untuk Pest Pelanggan
                String image_path = data.getStringExtra("imagePath_FumigasiPelanggan");
                bitmap_FumigasiPelanggan = BitmapFactory.decodeFile(image_path);
                img_ttdPelanggan.setImageBitmap(bitmap_FumigasiPelanggan);
                //bitmap_test = ((BitmapDrawable)img_ttdPelanggan.getDrawable()).getBitmap();
            }
        }
        if (requestCode == REQ_TTD_CONSULTANT) {
            //getActivity();
            if (resultCode == Activity.RESULT_OK) {
                String image_path_termite = data.getStringExtra("imagePath_FumigasiConsultant");
                bitmap_FumigasiConsultant = BitmapFactory.decodeFile(image_path_termite);
                img_ttdConsultant.setImageBitmap(bitmap_FumigasiConsultant);
            }
        }

        //Untuk Picture atau Kamera
        if (requestCode == REQ_KAMERA_SATU){
            if (resultCode == Activity.RESULT_OK){
                Bitmap bitmap_PickFoto = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                if(bitmap_PickFoto!=null) {
                    bitmap_PickFoto.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
                    img_FotoSatu.setImageBitmap(bitmap_PickFoto);
                }
            }
        }

        if (requestCode == REQ_GALERI_SATU) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                try {
                    Bitmap bitmap_Foto1 = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), imageUri);
                    //untuk decompress size foto
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap_Foto1, 300, 400, false);
                    scaled = getResizedBitmap(scaled, 300);
                    img_FotoSatu.setImageBitmap(scaled);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == REQ_KAMERA_DUA){
            if (resultCode == Activity.RESULT_OK){
                Bitmap bitmap_PickFoto = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                if(bitmap_PickFoto!=null) {
                    bitmap_PickFoto.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    img_FotoDua.setImageBitmap(bitmap_PickFoto);
                }
            }
        }

        if (requestCode == REQ_GALERI_DUA) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                try {
                    Bitmap bitmap_Foto1 = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), imageUri);
                    //untuk decompress size foto
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap_Foto1, 300, 400, false);
                    scaled = getResizedBitmap(scaled, 300);
                    img_FotoDua.setImageBitmap(scaled);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == REQ_KAMERA_TIGA){
            if (resultCode == Activity.RESULT_OK){
                Bitmap bitmap_PickFoto = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                if(bitmap_PickFoto!= null) {
                    bitmap_PickFoto.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
                    img_FotoTiga.setImageBitmap(bitmap_PickFoto);
                }
            }
        }

        if (requestCode == REQ_GALERI_TIGA) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                try {
                    Bitmap bitmap_Foto1 = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), imageUri);
                    //untuk decompress size foto
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap_Foto1, 300, 400, false);
                    scaled = getResizedBitmap(scaled, 300);
                    img_FotoTiga.setImageBitmap(scaled);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == REQ_KAMERA_EMPAT){
            if (resultCode == Activity.RESULT_OK){
                Bitmap bitmap_PickFoto = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                if(bitmap_PickFoto!=null) {
                    bitmap_PickFoto.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
                    img_FotoEmpat.setImageBitmap(bitmap_PickFoto);
                }
            }
        }

        if (requestCode == REQ_GALERI_EMPAT) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                try {
                    Bitmap bitmap_Foto1 = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), imageUri);
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap_Foto1, 300, 400, false);
                    scaled = getResizedBitmap(scaled, 300);
                    img_FotoEmpat.setImageBitmap(scaled);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //REQUEST MAPS
        if (requestCode == REQ_PLACE_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                String lokasi = data.getStringExtra("Alamat_Lokasi");
                edt_Alamat.setText(lokasi);

                latlong_AlamatPelanggan = data.getStringExtra("LatLong_Lokasi");
                Log.e("tag", latlong_AlamatPelanggan);
            }
        }
    }

    private void simpan_pelanggan(){
        //Untuk Bagian I - Identitas Diri
        final String IDClient = edt_ClientID.getText().toString().toUpperCase();
        final String namaPelanggan = edt_NamaPelanggan.getText().toString();
        final String kategoriTempatPelanggan = spr_Kategori.getSelectedItem().toString();
        final String alamatPelanggan = edt_Alamat.getText().toString();
        final String hpPelanggan = edt_NoHP.getText().toString();
        final String email_DB = edt_Email.getText().toString();

        if (TextUtils.isEmpty(IDClient)) {
            edt_ClientID.setError("Harap Masukkan ID Client");
            edt_ClientID.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(namaPelanggan)) {
            edt_NamaPelanggan.setError("Harap Masukkan Nama Pelanggan/Perusahaan");
            edt_NamaPelanggan.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(alamatPelanggan)) {
            edt_Alamat.setError("Harap Masukkan Alamat Pelanggan/Perusahaan");
            edt_Alamat.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(hpPelanggan)) {
            edt_NoHP.setError("Harap Masukkan No.HP atau Telepon Pelanggan/Perusahaan");
            edt_NoHP.requestFocus();
            return;
        }

        if (hpPelanggan.trim().length() < 5) {
            edt_NoHP.setError("Harap Masukkan Nomor Handphone/Telepon yang Valid");
            edt_NoHP.requestFocus();
            return;
        }

        final String fumigasi = spr_GasFumigasi.getSelectedItem().toString();

        //Bagian 6: Kontrak dan Harga Penawaran dan Catatan
        //Untuk kontrak dan Penawaran Harga
        final String penawaran_harga_DB = edt_PenawaranHarga.getText().toString();
        final String total_harga_penawaran_DB = edt_GrandTotal.getText().toString();
        final String status = status_kerjasama;
        final String catatan_tambahan_DB = edt_CatatanTambahan.getText().toString();

        if (penawaran_harga_DB.isEmpty()){
            edt_PenawaranHarga.setError("Harap Masukkan Penawaran Harga");
            edt_PenawaranHarga.requestFocus();
        }

        if (status.isEmpty()){
            rdb_Berhasil.setError("Harap Memilih Status Kerjasama");
            rdb_Berhasil.requestFocusFromTouch();
            return;
        }

        //Bagian 4: Luas Outdoor Indoor
        final String indoor_DB = edt_LuasBangunan.getText().toString();
        final String outdoor_DB = edt_LuasOutdoor.getText().toString();

        if(TextUtils.isEmpty(indoor_DB)){
            edt_LuasBangunan.setError("Harap Masukkan Luas Bangunan");
            edt_LuasBangunan.requestFocus();
            return;
        }

        // Bagian 7 : TTD dan Keterangan
        if (!chk_PernyataanPelanggan.isChecked()){
            chk_PernyataanPelanggan.setError("Harap Mencentang Pernyataan Berikut dan Menandatanganinya!");
            chk_PernyataanPelanggan.requestFocusFromTouch();
            return;
        }

        if (!chk_PernyataanConsultant.isChecked()){
            chk_PernyataanConsultant.setError("Harap Mencentang Pernyataan Berikut dan Menandatanganinya!");
            chk_PernyataanConsultant.requestFocusFromTouch();
            return;
        }

        if (img_ttdConsultant.getDrawable() == null){
            btn_DialogTTD_Consultant.setError("Harap Melakukan Proses Tanda Tangan Digital!");
            btn_DialogTTD_Consultant.requestFocusFromTouch();
            return;
        }

        if (img_ttdPelanggan.getDrawable() == null ){
            btn_DialogTTD_Pelanggan.setError("Harap Melakukan Proses Tanda Tangan Digital!");
            btn_DialogTTD_Pelanggan.requestFocusFromTouch();
            return;
        }

        //Bagian Foto Validasi
        //Untuk Upload Foto-Foto Pekerjaan
        if(img_FotoSatu.getVisibility() == View.GONE){
            btn_PilihFotoSatu.setError("Harap Mengupload Foto Pekerjaan #1!");
            btn_PilihFotoSatu.requestFocusFromTouch();
            return;
        }

        if(img_FotoSatu.getDrawable() == null){
            btn_PilihFotoSatu.setError("Harap Mengupload Foto Pekerjaan #1!");
            btn_PilihFotoSatu.requestFocusFromTouch();
            return;
        }

        if(img_FotoDua.getDrawable() == null){
            btn_PilihFotoDua.setError("Harap Mengupload Foto Pekerjaan #2!");
            btn_PilihFotoDua.requestFocusFromTouch();
            return;
        }

        if (img_FotoTiga.getDrawable() == null){
            btn_PilihFotoTiga.setError("Harap Mengupload Foto Pekerjaan #3!");
            btn_PilihFotoTiga.requestFocusFromTouch();
            return;
        }

        if (img_FotoEmpat.getDrawable() == null){
            btn_PilihFotoEmpat.setError("Harap Mengupload Foto Pekerjaan #4!");
            btn_PilihFotoEmpat.requestFocusFromTouch();
            return;
        }

        String dateKeterangan = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault()).format(new Date());
        final String keterangan = "*Berkas ini Dibuat Secara Otomatis oleh Sistem Mobile Apps Fumida pada Tanggal " + dateKeterangan;

        //Menjalankan Fungsi Intent
        Intent Review_Intent = new Intent(getContext(), ReviewSurveiFumigasi.class);
        Bundle Extra_Rev_Fumigasi = new Bundle();

        //Bagian 1: Data Diri
        Extra_Rev_Fumigasi.putString(FumigasiRev_ID_Client_Termite, IDClient);
        Extra_Rev_Fumigasi.putString(FumigasiRev_Nama_Client_Termite, namaPelanggan );
        Extra_Rev_Fumigasi.putString(FumigasiRev_Kategori_Client_Termite, kategoriTempatPelanggan);
        Extra_Rev_Fumigasi.putString(FumigasiRev_Alamat_Client_Termite, alamatPelanggan);
        Extra_Rev_Fumigasi.putString(FumigasiRev_Hp_Client_Termite, hpPelanggan);
        Extra_Rev_Fumigasi.putString(FumigasiRev_Email_DB, email_DB);

        Extra_Rev_Fumigasi.putString(FumigasiRev_GasFumigasi, fumigasi);

        //Bagian 4: Luas Indoor Outdoor
        Extra_Rev_Fumigasi.putString(FumigasiRev_Indoor_DB, indoor_DB);
        Extra_Rev_Fumigasi.putString(FumigasiRev_Outdoor_DB, outdoor_DB);

        //Bagian 6: Harga, Status dan Catatan
        Extra_Rev_Fumigasi.putString(FumigasiRev_PenawaranHarga_DB, penawaran_harga_DB);
        Extra_Rev_Fumigasi.putString(FumigasiRev_TotalHarga_DB, total_harga_penawaran_DB);
        Extra_Rev_Fumigasi.putString(FumigasiRev_Status_Termite, status);
        Extra_Rev_Fumigasi.putString(FumigasiRev_Catatan_DB, catatan_tambahan_DB);

        //Bagian 7: Keterangan PDF
        Extra_Rev_Fumigasi.putString(FumigasiRev_Keterangan_Termite, keterangan);

        //Bagian 8 : LatLong
        Extra_Rev_Fumigasi.putString(FumigasiRev_LatLong_Consultant, latlong_AlamatPelanggan);
        Extra_Rev_Fumigasi.putString(FumigasiRev_LatLong_Alamat_Pelanggan, latlong_Consultant);

        Review_Intent.putExtras(Extra_Rev_Fumigasi);
        startActivity(Review_Intent);

    }



}
