package com.example.aditmail.fumida.Fumigasi;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;

import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.aditmail.fumida.Activities.OpenMaps;
import com.example.aditmail.fumida.Settings.Konfigurasi;
import com.example.aditmail.fumida.R;
import com.example.aditmail.fumida.Settings.RequestHandler;
import com.example.aditmail.fumida.Activities.SignatureActivity;
import com.example.aditmail.fumida.Activities.TampilanMenuUtama;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class UpdateSurveiFumigasi extends AppCompatActivity implements View.OnClickListener  {

    //Get To Signature Activity
    private final static String path_fumida = "com.example.aditmail.fumida";
    public final static String Pesan_Extra = path_fumida + ".Pesan" ;

    private final String id_pegawai = TampilanMenuUtama.id_pegawai;

    //Tindakan
    private TextView txt_View, txt_Edit, txt_Delete;
    //Linear Parent
    private LinearLayout linear_Fumigasi;

    protected ConnectivityManager conMgr;
    protected ProgressDialog pDialog;

    protected String tgl_input, id_survei;
    protected TextView txt_idSurvei, txt_tglInput;

    //--------------- Bagian I: Identitas Pelanggan ---------------
    private EditText edt_ClientID, edt_NamaPelanggan, edt_Alamat, edt_NoHP, edt_Email;
    private Spinner spr_Kategori;

    private ImageView imgPinCircle;
    protected Double lat,lng;
    protected String latLong_AlamatCustomer, latLong_Consultant;
    protected double getDefaultLat, getDefaultLon;
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
    private RadioButton rdb_Berhasil, rdb_Menunggu, rdb_Gagal;
    private String status_kerjasama="";
    //--------------- Bagian iV: Kontrak, Kerjasama dan Catatan ---------------

    //--------------- Bagian V: Tanda Tangan ---------------
    private Button btn_DialogTTD_Pelanggan, btn_DialogTTD_Consultant;
    private CheckBox chk_PernyataanPelanggan, chk_PernyataanConsultant;

    @SuppressLint("StaticFieldLeak")
    public static ImageView img_ttdPelanggan, img_ttdConsultant;

    protected Bitmap bitmap_FumigasiConsultant, bitmap_FumigasiPelanggan;
    protected Bitmap getTTD_Consultant, getTTD_Pelanggan;

    private String ConvertImage_Pelanggan = "", ConvertImage_Consultant = "";
    private String GetImageNameFromEditText_Pelanggan = "", GetImageNameFromEditText_Consultant = "";
    //--------------- Bagian V: Tanda Tangan ---------------

    //--------------- Bagian VI: Upload Image---------------
    private Button btn_PilihFotoSatu, btn_PilihFotoDua, btn_PilihFotoTiga, btn_PilihFotoEmpat;
    private String image_path = "null";
    private String imagepath_FumigasiConsultant = "null";

    //Untuk Upload Image Foto...
    protected Bitmap getFotoSatu, getFotoDua, getFotoTiga, getFotoEmpat;
    private String ConvertImage_FotoSatu = "", ConvertImage_FotoDua = "", ConvertImage_FotoTiga = "", ConvertImage_FotoEmpat = "";
    private String GetImageNameFromEditText_FotoSatu = "", GetImageNameFromEditText_FotoDua = "", GetImageNameFromEditText_FotoTiga = "",
            GetImageNameFromEditText_FotoEmpat = "";

    @SuppressLint("StaticFieldLeak")
    public static ImageView img_FotoSatu, img_FotoDua, img_FotoTiga, img_FotoEmpat;

    private Space spc_Satu, spc_Dua, spc_Tiga;
    //--------------- Bagian VI: Upload Image---------------

    //Button Simpan
    private Button btn_UpdateSurvey;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_survei_fumigasi);

        InitData();

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);
        pDialog.setCanceledOnTouchOutside(false);

        //Get Data
        Intent intent = getIntent();
        tgl_input = intent.getStringExtra(Konfigurasi.KEY_TANGGAL_INPUT);
        id_survei = intent.getStringExtra(Konfigurasi.KEY_SURVEI_ID);

        //Set Data
        txt_idSurvei.setText(id_survei);
        txt_tglInput.setText(tgl_input);

        if(conMgr!=null) {
            if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
                getDataSurveiFumigasi();
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }
        }

        //Edit Button
        EditData();

        //Call Function
        TextWatcher();
        CekRadioGroup();
        CekTandaTangan();
    }

    private void InitData(){
        txt_idSurvei = findViewById(R.id.textView_idSurvei_Fumigasi);
        txt_tglInput = findViewById(R.id.textView_Tgl_Input);

        //Tindakan
        txt_Edit = findViewById(R.id.textView_Update_Fumigasi);
        txt_Delete = findViewById(R.id.textView_Delete_Fumigasi);
        txt_View = findViewById(R.id.textView_View_Fumigasi);

        linear_Fumigasi = findViewById(R.id.LinearLayout_UpdateFumigasi);

        //Bagian Identitas Pelanggan
        edt_ClientID = findViewById(R.id.editText_ClientID);
        edt_NamaPelanggan = findViewById(R.id.editText_NamaPelanggan);
        edt_Alamat = findViewById(R.id.editText_Alamat);
        edt_NoHP= findViewById(R.id.editText_NoHP);
        edt_Email= findViewById(R.id.editText_Email);
        spr_Kategori= findViewById(R.id.spinner_Kategori);
        imgPinCircle = findViewById(R.id.imgPin);

        //Fumigasi
        spr_GasFumigasi = findViewById(R.id.spinner_GasFumigasi);

        //Bagian Area Lokasi
        edt_LuasBangunan = findViewById(R.id.editText_LuasBangunan);  edt_LuasOutdoor = findViewById(R.id.editText_LuasOutdoor);

        //Kontrak dan Penawaran Harga
        edt_PenawaranHarga = findViewById(R.id.editText_PenawaranHarga_Fumigasi);
        edt_GrandTotal = findViewById(R.id.editText_GrandTotal_Fumigasi);
        rdg_StatusKerjaSama = findViewById(R.id.RadioGroup_Status);
        rdb_Berhasil = findViewById(R.id.radioButton_Berhasil);
        rdb_Menunggu = findViewById(R.id.radioButton_Menunggu);
        rdb_Gagal = findViewById(R.id.radioButton_Gagal);

        //Bagian Catatan Tambahan
        edt_CatatanTambahan = findViewById(R.id.editText_CatatanTambahan_Fumigasi);

        //Button Simpan
        btn_UpdateSurvey = findViewById(R.id.button_SimpanSurvey_Fumigasi);
        btn_UpdateSurvey.setOnClickListener(this);

        //Bagian TTD - Signature
        btn_DialogTTD_Pelanggan = findViewById(R.id.button_GetTTD_Pelanggan_Fumigasi); btn_DialogTTD_Consultant = findViewById(R.id.button_GetTTD_Consultant_Fumigasi);
        img_ttdPelanggan = findViewById(R.id.imageView_TTD_Pelanggan_Fumigasi);  img_ttdConsultant = findViewById(R.id.imageView_TTD_Consultant_Fumigasi);
        chk_PernyataanPelanggan = findViewById(R.id.checkBox_PernyataanCustomer_Fumigasi);  chk_PernyataanConsultant = findViewById(R.id.checkBox_PernyataanConsultant_Fumigasi);

        //Bagian upload Foto
        btn_PilihFotoSatu = findViewById(R.id.button_FotoSatu);    img_FotoSatu = findViewById(R.id.imageView_FotoSatu);
        btn_PilihFotoDua = findViewById(R.id.button_FotoDua);    img_FotoDua = findViewById(R.id.imageView_FotoDua);
        btn_PilihFotoTiga = findViewById(R.id.button_FotoTiga);    img_FotoTiga = findViewById(R.id.imageView_FotoTiga);
        btn_PilihFotoEmpat = findViewById(R.id.button_FotoEmpat);    img_FotoEmpat = findViewById(R.id.imageView_FotoEmpat);

        spc_Satu = findViewById(R.id.Space_Satu);    spc_Dua = findViewById(R.id.Space_Dua);
        spc_Tiga = findViewById(R.id.Space_Tiga);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onStart(){
        OpenPhoto();
        OpenMaps();
        super.onStart();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if(v == btn_UpdateSurvey){
            OnClickUpdate();
        }
    }

    //Disable or Enable LinearLayout
    private static void setViewAndChildrenEnabled(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                setViewAndChildrenEnabled(child, enabled);
            }
        }
    }

    private void EditData() {
        setViewAndChildrenEnabled(linear_Fumigasi, false);

        txt_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setViewAndChildrenEnabled(linear_Fumigasi, true);
                btn_DialogTTD_Consultant.setEnabled(false);
                btn_DialogTTD_Pelanggan.setEnabled(false);
            }
        });

        txt_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UpdateSurveiFumigasi.this);

                    builder.setCancelable(true);
                    builder.setTitle("Apakah Anda Yakin Menghapus Survei Fumigasi Tersebut?");
                    builder.setMessage("Apabila Yakin Tekan Tombol 'OK' untuk Menghapus?!");

                    builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteSurvei();
                        }
                    });
                    builder.show();
                } else {
                    Toast.makeText(getApplicationContext(), "Tidak Ada Koneksi Internet. Mohon Coba Lagi Nanti.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        txt_View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(UpdateSurveiFumigasi.this, PreviewSurveiFumigasi.class);
                a.putExtra(Konfigurasi.KEY_TAG_ID, id_pegawai);
                a.putExtra(Konfigurasi.KEY_SURVEI_ID, id_survei);
                startActivity(a);
            }
        });
    }

    private void getDataSurveiFumigasi() {
        @SuppressLint("StaticFieldLeak")
        class GetDataSurveiFumigasi extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                pDialog.setMessage("Mengambil Data, Harap Menunggu..");
                pDialog.show();
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                pDialog.dismiss();
                showDataSurveiFumigasi(s);
                super.onPostExecute(s);
            }


            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();
                params.put(Konfigurasi.KEY_TAG_ID, id_pegawai);
                params.put(Konfigurasi.KEY_SURVEI_ID, id_survei);
                RequestHandler rh = new RequestHandler();
                return rh.sendPostRequest(Konfigurasi.URL_VIEW_SELECTED_FUMIGASI, params);
            }
        }
        GetDataSurveiFumigasi ge = new GetDataSurveiFumigasi();
        ge.execute();
    }

    private void showDataSurveiFumigasi(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray(Konfigurasi.TAG_JSON_ARRAY);
            JSONObject c = result.getJSONObject(0);

            String ClientID = c.getString(Konfigurasi.FUMIGASI_KEY_GET_TAG_CLIENT_ID);
            String NamaPelanggan = c.getString(Konfigurasi.FUMIGASI_KEY_GET_TAG_NAMA_PELANGGAN);
            String KategoriTempat = c.getString(Konfigurasi.FUMIGASI_KEY_GET_TAG_KATEGORI_TEMPAT);
            String Alamat = c.getString(Konfigurasi.FUMIGASI_KEY_GET_TAG_ALAMAT);
            String NoHP = c.getString(Konfigurasi.FUMIGASI_KEY_GET_TAG_HP);
            String Email = c.getString(Konfigurasi.FUMIGASI_KEY_GET_TAG_EMAIL);

            latLong_AlamatCustomer = c.getString(Konfigurasi.FUMIGASI_KEY_GET_LATLONG_ALAMAT_PELANGGAN);
            latLong_Consultant = c.getString(Konfigurasi.FUMIGASI_KEY_GET_LATLONG_CONSULTANT);

            String GasFumigasi = c.getString(Konfigurasi.FUMIGASI_KEY_GET_GAS_FUMIGASI);

            String LuasIndoor = c.getString(Konfigurasi.FUMIGASI_KEY_GET_TAG_LUAS_INDOOR);
            String LuasOutdoor = c.getString(Konfigurasi.FUMIGASI_KEY_GET_TAG_LUAS_OUTDOOR);

            String PenawaranHarga = c.getString(Konfigurasi.FUMIGASI_KEY_GET_TAG_PENAWARAN_HARGA);
            String TotalHarga = c.getString(Konfigurasi.FUMIGASI_KEY_GET_TAG_TOTAL_HARGA);
            String StatusKerjasama = c.getString(Konfigurasi.FUMIGASI_KEY_GET_TAG_STATUS_KERJASAMA);
            String CatatanTambahan = c.getString(Konfigurasi.FUMIGASI_KEY_GET_TAG_CATATAN);
            String GetImgPelanggan_TTD = c.getString(Konfigurasi.FUMIGASI_KEY_GET_IMG_PELANGGAN);
            String GetImgConsultant_TTD = c.getString(Konfigurasi.FUMIGASI_KEY_GET_IMG_CONSULTANT);

            //Bagian VI : Foto
            String pathFotoSatu = c.getString(Konfigurasi.FUMIGASI_KEY_GET_TAG_IMG_FOTO_SATU);
            String pathFotoDua = c.getString(Konfigurasi.FUMIGASI_KEY_GET_TAG_IMG_FOTO_DUA);
            String pathFotoTiga = c.getString(Konfigurasi.FUMIGASI_KEY_GET_TAG_FOTO_TIGA);
            String pathFotoEmpat = c.getString(Konfigurasi.FUMIGASI_KEY_GET_TAG_FOTO_EMPAT);

            edt_ClientID.setText(ClientID);
            edt_NamaPelanggan.setText(NamaPelanggan);
            edt_Alamat.setText(Alamat);
            edt_NoHP.setText(NoHP);
            edt_Email.setText(Email);

            //Split Lat Long
            String[]LatLong = latLong_AlamatCustomer.split(",");
            getDefaultLat = Double.valueOf(LatLong[0]);
            getDefaultLon = Double.valueOf(LatLong[1]);

            //Spinner Jenis Rayap
            if (GasFumigasi.equalsIgnoreCase("Methyl Bromide")){
                spr_GasFumigasi.setSelection(0);
            } else if (GasFumigasi.equalsIgnoreCase("Sulfur Flouride")){
                spr_GasFumigasi.setSelection(1);
            } else if (GasFumigasi.equalsIgnoreCase("Phosphine")){
                spr_GasFumigasi.setSelection(2);
            }

            edt_LuasBangunan.setText(LuasIndoor);
            edt_LuasOutdoor.setText(LuasOutdoor);
            edt_PenawaranHarga.setText(PenawaranHarga);

            edt_GrandTotal.setText(String.valueOf(TotalHarga));
            edt_CatatanTambahan.setText(CatatanTambahan);

            if (getApplicationContext()!= null) {
                if (!pathFotoSatu.isEmpty()) {
                    img_FotoSatu.setVisibility(View.VISIBLE);
                    btn_PilihFotoSatu.setVisibility(View.VISIBLE);

                    Glide.with(getApplicationContext())
                            .load(Konfigurasi.url_image + pathFotoSatu)
                            .error(Glide.with(img_FotoSatu).load(R.drawable.ic_no_image))
                            .into(img_FotoSatu);
                }

                if (!pathFotoDua.isEmpty()) {
                    img_FotoDua.setVisibility(View.VISIBLE);
                    btn_PilihFotoDua.setVisibility(View.VISIBLE);
                    Glide.with(getApplicationContext())
                            .load(Konfigurasi.url_image + pathFotoDua)
                            .error(Glide.with(img_FotoDua).load(R.drawable.ic_no_image))
                            .into(img_FotoDua);
                }

                if (!pathFotoTiga.isEmpty()) {
                    img_FotoTiga.setVisibility(View.VISIBLE);
                    btn_PilihFotoTiga.setVisibility(View.VISIBLE);
                    Glide.with(getApplicationContext())
                            .load(Konfigurasi.url_image + pathFotoTiga)
                            .error(Glide.with(img_FotoTiga).load(R.drawable.ic_no_image))
                            .into(img_FotoTiga);
                }

                if (!pathFotoEmpat.isEmpty()) {
                    img_FotoEmpat.setVisibility(View.VISIBLE);
                    btn_PilihFotoEmpat.setVisibility(View.VISIBLE);
                    Glide.with(getApplicationContext())
                            .load(Konfigurasi.url_image + pathFotoEmpat)
                            .error(Glide.with(img_FotoEmpat).load(R.drawable.ic_no_image))
                            .into(img_FotoEmpat);
                }

                Glide.with(getApplicationContext())
                        .load(Konfigurasi.url_image + GetImgPelanggan_TTD)
                        .error(Glide.with(img_ttdPelanggan).load(R.drawable.ic_ttd_not_found))
                        .into(img_ttdPelanggan);

                Glide.with(getApplicationContext())
                        .load(Konfigurasi.url_image + GetImgConsultant_TTD)
                        .error(Glide.with(img_ttdConsultant).load(R.drawable.ic_ttd_not_found))
                        .into(img_ttdConsultant);
            }

            //Spinner Kategori Tempat Client
            if (KategoriTempat.equalsIgnoreCase("Kantor")){
                spr_Kategori.setSelection(0);
            } else if (KategoriTempat.equalsIgnoreCase("Pabrik Makanan")){
                spr_Kategori.setSelection(1);
            } else if (KategoriTempat.equalsIgnoreCase("Pabrik Non Makanan")){
                spr_Kategori.setSelection(2);
            } else if (KategoriTempat.equalsIgnoreCase("Restoran")){
                spr_Kategori.setSelection(3);
            } else {
                spr_Kategori.setSelection(4);
            }

            //Radio Button Status Kerjasama
            if (StatusKerjasama.equalsIgnoreCase("Berhasil")){
                rdb_Berhasil.setChecked(true);
            } else if (StatusKerjasama.equalsIgnoreCase("Menunggu")){
                rdb_Menunggu.setChecked(true);
            } else{
                rdb_Gagal.setChecked(true);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void OpenMaps(){
        imgPinCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenPlacePicker();
            }
        });
    }

    //Opening Maps...
    private void OpenPlacePicker(){
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent openMaps = new Intent(UpdateSurveiFumigasi.this, OpenMaps.class);
            startActivityForResult(openMaps, REQ_PLACE_PICKER);
        }else{
            showGPSDisabledAlertToUser();
        }
    }

    private void showGPSDisabledAlertToUser(){
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
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
        android.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();
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

        return (formatRupiah.format(total));
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
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(UpdateSurveiFumigasi.this);
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
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(UpdateSurveiFumigasi.this);
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
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(UpdateSurveiFumigasi.this);
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
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(UpdateSurveiFumigasi.this);
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
                        Toast.makeText(getApplicationContext(), "Catatan:\n" +
                                "Pastikan untuk Melakukan Konfirmasi Kembali Dalam Beberapa Waktu Kedepan Kepada Pelanggan/Perusahaan", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }

    public void CekTandaTangan(){
        btn_DialogTTD_Pelanggan.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SignatureActivity.class);
                i.putExtra(Pesan_Extra, "TTD_PelangganFumigasi");
                startActivityForResult(i,REQ_TTD_PELANGGAN);
            }
        });

        btn_DialogTTD_Consultant.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(getApplicationContext(), SignatureActivity.class);
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

        //get Date Now
        String dateNow;
        dateNow = new SimpleDateFormat("dd-MM-yy_hh.mm.ss", Locale.getDefault()).format(new Date());
        String getNamaPelanggan = edt_NamaPelanggan.getText().toString();

        if (requestCode == REQ_TTD_PELANGGAN) {
            if (resultCode == RESULT_OK) {
                //Nampilin hasil ttd untuk Pest Pelanggan
                image_path = data.getStringExtra("imagePath_FumigasiPelanggan");
                bitmap_FumigasiPelanggan = BitmapFactory.decodeFile(image_path);
                img_ttdPelanggan.setImageBitmap(bitmap_FumigasiPelanggan);

                getTTD_Pelanggan = ((BitmapDrawable)UpdateSurveiFumigasi.img_ttdPelanggan.getDrawable()).getBitmap();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                getTTD_Pelanggan.compress(Bitmap.CompressFormat.PNG,50,stream);
                byte[] imageInByte = stream.toByteArray();
                ConvertImage_Pelanggan = Base64.encodeToString(imageInByte,Base64.DEFAULT);
                GetImageNameFromEditText_Pelanggan = "Fumigasi_" + getNamaPelanggan +"_" + dateNow+".png";

            }
        }
        if (requestCode == REQ_TTD_CONSULTANT) {
            if (resultCode == RESULT_OK) {
                imagepath_FumigasiConsultant = data.getStringExtra("imagePath_FumigasiConsultant");
                bitmap_FumigasiConsultant = BitmapFactory.decodeFile(imagepath_FumigasiConsultant);
                img_ttdConsultant.setImageBitmap(bitmap_FumigasiConsultant);

                getTTD_Consultant = ((BitmapDrawable)UpdateSurveiFumigasi.img_ttdConsultant.getDrawable()).getBitmap();

                ByteArrayOutputStream stream_consultant = new ByteArrayOutputStream();
                getTTD_Consultant.compress(Bitmap.CompressFormat.PNG,50,stream_consultant);
                byte[] imageInByte_consultant = stream_consultant.toByteArray();
                ConvertImage_Consultant = Base64.encodeToString(imageInByte_consultant,Base64.DEFAULT);
                GetImageNameFromEditText_Consultant = "Fumigasi_" + TampilanMenuUtama.username + "_" + dateNow +".png";

            }
        }

        //Untuk Picture atau Kamera
        if (requestCode == REQ_KAMERA_SATU){
            if (resultCode == RESULT_OK){
                Bitmap bitmap_PickFoto = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                if(bitmap_PickFoto!= null) {
                    bitmap_PickFoto.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
                    img_FotoSatu.setImageBitmap(bitmap_PickFoto);
                }

                GetImageNameFromEditText_FotoSatu = "Fumigasi_Foto_Satu_" + getNamaPelanggan + "_" + dateNow + ".png";

            }
        }

        if (requestCode == REQ_GALERI_SATU) {
            if (resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                try {
                    Bitmap bitmap_Foto1 = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    //untuk decompress size foto
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap_Foto1, 300, 400, false);
                    scaled = getResizedBitmap(scaled, 300);
                    img_FotoSatu.setImageBitmap(scaled);

                    GetImageNameFromEditText_FotoSatu = "Fumigasi_Foto_Satu_" + getNamaPelanggan + "_" + dateNow + ".png";

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == REQ_KAMERA_DUA){
            if (resultCode == RESULT_OK){
                Bitmap bitmap_PickFoto = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                if(bitmap_PickFoto!=null) {
                    bitmap_PickFoto.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
                    img_FotoDua.setImageBitmap(bitmap_PickFoto);
                }

                GetImageNameFromEditText_FotoDua = "Fumigasi_Foto_Dua_" + getNamaPelanggan + "_" + dateNow + ".png";
            }
        }

        if (requestCode == REQ_GALERI_DUA) {
            if (resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                try {
                    Bitmap bitmap_Foto1 = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    //untuk decompress size foto
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap_Foto1, 300, 400, false);
                    scaled = getResizedBitmap(scaled, 300);
                    img_FotoDua.setImageBitmap(scaled);

                    GetImageNameFromEditText_FotoDua = "Fumigasi_Foto_Dua_" + getNamaPelanggan + "_" + dateNow + ".png";

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == REQ_KAMERA_TIGA){
            if (resultCode == RESULT_OK){
                Bitmap bitmap_PickFoto = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                if(bitmap_PickFoto!=null) {
                    bitmap_PickFoto.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    img_FotoTiga.setImageBitmap(bitmap_PickFoto);
                }

                GetImageNameFromEditText_FotoTiga = "Fumigasi_Foto_Tiga_" + getNamaPelanggan + "_" + dateNow + ".png";
            }
        }

        if (requestCode == REQ_GALERI_TIGA) {
            if (resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                try {
                    Bitmap bitmap_Foto1 = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    //untuk decompress size foto
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap_Foto1, 300, 400, false);
                    scaled = getResizedBitmap(scaled, 300);
                    img_FotoTiga.setImageBitmap(scaled);

                    GetImageNameFromEditText_FotoTiga = "Fumigasi_Foto_Tiga_" + getNamaPelanggan + "_" + dateNow + ".png";

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == REQ_KAMERA_EMPAT){
            if (resultCode == RESULT_OK){
                Bitmap bitmap_PickFoto = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                if(bitmap_PickFoto!=null) {
                    bitmap_PickFoto.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    img_FotoEmpat.setImageBitmap(bitmap_PickFoto);
                }

                GetImageNameFromEditText_FotoEmpat = "Fumigasi_Foto_Empat_" + getNamaPelanggan + "_" + dateNow + ".png";
            }
        }

        if (requestCode == REQ_GALERI_EMPAT) {
            if (resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                try {
                    Bitmap bitmap_Foto1 = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    //untuk decompress size foto
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap_Foto1, 300, 400, false);
                    scaled = getResizedBitmap(scaled, 300);
                    img_FotoEmpat.setImageBitmap(scaled);

                    GetImageNameFromEditText_FotoEmpat = "Fumigasi_Foto_Empat_" + getNamaPelanggan + "_" + dateNow + ".png";

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //REQUEST MAPS
        if (requestCode == REQ_PLACE_PICKER) {
            String lokasi = data.getStringExtra("Alamat_Lokasi");
            edt_Alamat.setText(lokasi);

            latLong_AlamatCustomer = data.getStringExtra("LatLong_Lokasi");
            Log.e("tag", latLong_AlamatCustomer);
        }
    }

    protected void OnClickUpdate(){
        // <---- Untuk Bagian 1 - Data Diri Pelanggan ---->
        final String IDClient = edt_ClientID.getText().toString().toUpperCase();
        final String namaPelanggan = edt_NamaPelanggan.getText().toString();
        final String kategoriTempatPelanggan = spr_Kategori.getSelectedItem().toString();
        final String alamatPelanggan = edt_Alamat.getText().toString();
        final String hpPelanggan = edt_NoHP.getText().toString();
        final String email = edt_Email.getText().toString();

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

        final String gasFumigasi = spr_GasFumigasi.getSelectedItem().toString();

        //Untuk Bagian III - Luas Outdoor Indoor
        final String luas_indoor = edt_LuasBangunan.getText().toString();
        final String luas_outdoor = edt_LuasOutdoor.getText().toString();

        if(TextUtils.isEmpty(luas_indoor)){
            edt_LuasBangunan.setError("Harap Masukkan Luas Bangunan");
            edt_LuasBangunan.requestFocus();
            return;
        }

        //Untuk kontrak dan Penawaran Harga
        final String penawaran_harga = edt_PenawaranHarga.getText().toString();
        final String total_harga_penawaran = edt_GrandTotal.getText().toString();
        final String status = status_kerjasama;
        final String catatan_tambahan = edt_CatatanTambahan.getText().toString();

        if (TextUtils.isEmpty(penawaran_harga)){
            edt_PenawaranHarga.setError("Harap Memasukkan Penawaran Harga");
            edt_PenawaranHarga.requestFocus();
            return;
        }

        if (status.isEmpty()){
            rdb_Berhasil.setError("Harap Memilih Status Kerjasama");
            rdb_Berhasil.requestFocusFromTouch();
            return;
        }

        //Untuk Proses Tanda Tangan
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

        //Untuk Foto
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

        //Menampilkan Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateSurveiFumigasi.this);

        builder.setCancelable(true);
        builder.setTitle("Apakah Anda Yakin Data Yang Dimasukkan Telah Benar dan Tepat?");
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
                class simpan extends AsyncTask<Void, Void, String> {

                    @Override
                    protected void onPreExecute() {
                        pDialog.setMessage("Menyimpan Data Survei Fumigasi ke Database, Harap Tunggu...");
                        pDialog.show();
                        super.onPreExecute();
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        pDialog.dismiss();
                        Toast.makeText(UpdateSurveiFumigasi.this, s, Toast.LENGTH_LONG).show();

                        if (!GetImageNameFromEditText_FotoEmpat.isEmpty() || !GetImageNameFromEditText_FotoTiga.isEmpty() ||
                                !GetImageNameFromEditText_FotoDua.isEmpty() || !GetImageNameFromEditText_FotoSatu.isEmpty()) {
                            uploadFoto();
                        }
                        super.onPostExecute(s);
                    }

                    @Override
                    protected String doInBackground(Void... v) {

                        HashMap<String, String> params = new HashMap<>();

                        //Bagian I : Data Kostumer
                        params.put(Konfigurasi.KEY_SAVE_ID_CLIENT_FUMIGASI, IDClient);
                        params.put(Konfigurasi.KEY_SAVE_NAMA_PELANGGAN_FUMIGASI, namaPelanggan);
                        params.put(Konfigurasi.KEY_SAVE_KATEGORITEMPAT_FUMIGASI, kategoriTempatPelanggan);
                        params.put(Konfigurasi.KEY_SAVE_ALAMAT_PELANGGAN_FUMIGASI, alamatPelanggan);
                        params.put(Konfigurasi.KEY_SAVE_HP_PELANGGAN_FUMIGASI, hpPelanggan);
                        params.put(Konfigurasi.KEY_SAVE_EMAIL_PELANGGAN_FUMIGASI, email);

                        params.put(Konfigurasi.KEY_SAVE_LATLONG_ALAMAT_PELANGGAN, latLong_AlamatCustomer);
                        params.put(Konfigurasi.KEY_SAVE_LATLONG_CONSULTANT, latLong_Consultant);

                        params.put(Konfigurasi.KEY_SAVE_GASFUMIGASI_FUMIGASI, gasFumigasi);

                        //Bagian V : Bagian Luas Outdoor Indoor
                        params.put(Konfigurasi.KEY_SAVE_LUAS_INDOOR_FUMIGASI, luas_indoor);
                        params.put(Konfigurasi.KEY_SAVE_LUAS_OUTDOOR_FUMIGASI, luas_outdoor);

                        //Bagian VII : Kontrak dan Penawaran Harga
                        params.put(Konfigurasi.KEY_SAVE_PENAWARAN_HARGA_FUMIGASI, penawaran_harga);
                        params.put(Konfigurasi.KEY_SAVE_TOTAL_HARGA_FUMIGASI, total_harga_penawaran);
                        params.put(Konfigurasi.KEY_SAVE_STATUS_KERJASAMA_FUMIGASI, status);
                        params.put(Konfigurasi.KEY_SAVE_CATATAN_TAMBAHAN_FUMIGASI, catatan_tambahan);

                        //Bagian VIII : Tanda Tangan
                        //Cek if Null or Not -->
                        params.put(Konfigurasi.KEY_CEK_IMG_TTD_PELANGGAN, image_path);
                        params.put(Konfigurasi.KEY_CEK_IMG_TTD_CONSULTANT, imagepath_FumigasiConsultant);

                        //untuk Pelanggan
                        params.put(Konfigurasi.KEY_SAVE_IMAGE_TAG_PELANGGAN_FUMIGASI, GetImageNameFromEditText_Pelanggan);
                        params.put(Konfigurasi.KEY_SAVE_IMAGE_NAME_PELANGGAN_FUMIGASI, ConvertImage_Pelanggan);
                        Log.e("tagz", image_path);
                        //}

                        //untuk Consultant
                        params.put(Konfigurasi.KEY_SAVE_IMAGE_TAG_CONSULTANT_FUMIGASI, GetImageNameFromEditText_Consultant);
                        params.put(Konfigurasi.KEY_SAVE_IMAGE_NAME_CONSULTANT_FUMIGASI, ConvertImage_Consultant);
                        Log.e("tagz", imagepath_FumigasiConsultant);
                        //}

                        params.put(Konfigurasi.KEY_TAG_ID, TampilanMenuUtama.id_pegawai);
                        params.put(Konfigurasi.TERMITE_KEY_GET_LIST_SURVEI_ID, id_survei);
                        Log.e("tag", id_survei);

                        RequestHandler rh = new RequestHandler();
                        return rh.sendPostRequest(Konfigurasi.URL_UPDATE_FUMIGASI_SURVEI, params);
                    }
                }

                simpan save = new simpan();
                save.execute();
            }
        });
        builder.show();
    }

    private void uploadFoto(){
        if(GetImageNameFromEditText_FotoSatu != null) {
            getFotoSatu = ((BitmapDrawable) UpdateSurveiFumigasi.img_FotoSatu.getDrawable()).getBitmap();
            ByteArrayOutputStream stream_foto_satu = new ByteArrayOutputStream();
            getFotoSatu.compress(Bitmap.CompressFormat.PNG, 70, stream_foto_satu);
            byte[] imageInByte_foto_satu = stream_foto_satu.toByteArray();
            ConvertImage_FotoSatu = Base64.encodeToString(imageInByte_foto_satu, Base64.DEFAULT);
        }

        if(GetImageNameFromEditText_FotoDua != null){
            getFotoDua = ((BitmapDrawable)UpdateSurveiFumigasi.img_FotoDua.getDrawable()).getBitmap();
            ByteArrayOutputStream stream_foto_dua = new ByteArrayOutputStream();
            getFotoDua.compress(Bitmap.CompressFormat.PNG, 70, stream_foto_dua);
            byte[] imageInByte_foto_dua = stream_foto_dua.toByteArray();
            ConvertImage_FotoDua = Base64.encodeToString(imageInByte_foto_dua, Base64.DEFAULT);
        }

        if(GetImageNameFromEditText_FotoTiga != null){
            getFotoTiga = ((BitmapDrawable)UpdateSurveiFumigasi.img_FotoTiga.getDrawable()).getBitmap();
            ByteArrayOutputStream stream_foto_tiga = new ByteArrayOutputStream();
            getFotoTiga.compress(Bitmap.CompressFormat.PNG, 70, stream_foto_tiga);
            byte[] imageInByte_foto_tiga = stream_foto_tiga.toByteArray();
            ConvertImage_FotoTiga = Base64.encodeToString(imageInByte_foto_tiga, Base64.DEFAULT);
        }

        if(GetImageNameFromEditText_FotoTiga != null){
            getFotoTiga = ((BitmapDrawable)UpdateSurveiFumigasi.img_FotoTiga.getDrawable()).getBitmap();
            ByteArrayOutputStream stream_foto_tiga = new ByteArrayOutputStream();
            getFotoTiga.compress(Bitmap.CompressFormat.PNG, 70, stream_foto_tiga);
            byte[] imageInByte_foto_tiga = stream_foto_tiga.toByteArray();
            ConvertImage_FotoTiga = Base64.encodeToString(imageInByte_foto_tiga, Base64.DEFAULT);
        }

        if(GetImageNameFromEditText_FotoEmpat != null){
            getFotoEmpat = ((BitmapDrawable)UpdateSurveiFumigasi.img_FotoEmpat.getDrawable()).getBitmap();
            ByteArrayOutputStream stream_foto_empat = new ByteArrayOutputStream();
            getFotoEmpat.compress(Bitmap.CompressFormat.PNG, 70, stream_foto_empat);
            byte[] imageInByte_foto_empat = stream_foto_empat.toByteArray();
            ConvertImage_FotoEmpat = Base64.encodeToString(imageInByte_foto_empat, Base64.DEFAULT);
        }

        @SuppressLint("StaticFieldLeak")
        class upload extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                pDialog.setMessage("Mengunggah Foto, Harap Tunggu...");
                pDialog.show();
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                pDialog.dismiss();
                Toast.makeText(UpdateSurveiFumigasi.this, s, Toast.LENGTH_LONG).show();
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();

                params.put(Konfigurasi.KEY_SAVE_TAG_FOTO_PERTAMA_REPORT, GetImageNameFromEditText_FotoSatu);
                params.put(Konfigurasi.KEY_SAVE_NAME_FOTO_PERTAMA_REPORT, ConvertImage_FotoSatu);

                params.put(Konfigurasi.KEY_SAVE_TAG_FOTO_KEDUA_REPORT, GetImageNameFromEditText_FotoDua);
                params.put(Konfigurasi.KEY_SAVE_NAME_FOTO_KEDUA_REPORT, ConvertImage_FotoDua);

                params.put(Konfigurasi.KEY_SAVE_TAG_FOTO_KETIGA_REPORT, GetImageNameFromEditText_FotoTiga);
                params.put(Konfigurasi.KEY_SAVE_NAME_FOTO_KETIGA_REPORT, ConvertImage_FotoTiga);

                params.put(Konfigurasi.KEY_SAVE_TAG_FOTO_KEEMPAT_REPORT, GetImageNameFromEditText_FotoEmpat);
                params.put(Konfigurasi.KEY_SAVE_NAME_FOTO_KEEMPAT_REPORT, ConvertImage_FotoEmpat);

                params.put(Konfigurasi.KEY_TAG_ID, TampilanMenuUtama.id_pegawai);
                params.put(Konfigurasi.PEST_KEY_GET_LIST_SURVEI_ID, id_survei);

                RequestHandler rh = new RequestHandler();
                return rh.sendPostRequest(Konfigurasi.URL_UPDATE_PICTURE_FUMIGASI, params);
            }
        }

        upload save = new upload();
        save.execute();
    }

    private void deleteSurvei(){
        @SuppressLint("StaticFieldLeak")
        class deleteSurvei extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog.setMessage("Menghapus Data Survei Terpilih, Harap Tunggu...");
                pDialog.show();
            }

            @Override
            protected void onPostExecute(String s) {
                pDialog.dismiss();
                Toast.makeText(UpdateSurveiFumigasi.this, s, Toast.LENGTH_LONG).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateSurveiFumigasi.this);
                builder.setMessage(s)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent del_intent = new Intent(UpdateSurveiFumigasi.this, TabFumigasi.class);
                                del_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(del_intent);
                                finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();

                params.put(Konfigurasi.KEY_TAG_ID, TampilanMenuUtama.id_pegawai);
                params.put(Konfigurasi.FUMIGASI_KEY_GET_LIST_SURVEI_ID, id_survei);

                RequestHandler rh = new RequestHandler();
                return rh.sendPostRequest(Konfigurasi.URL_DELETE_SURVEI_FUMIGASI, params);
            }
        }

        deleteSurvei Delete = new deleteSurvei();
        Delete.execute();
    }
}
