package com.example.aditmail.fumida.PestControl;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;

import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import android.widget.TableLayout;
import android.widget.TableRow;
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

public class UpdateSurveiPestControl extends AppCompatActivity implements View.OnClickListener {
    ConnectivityManager conMgr;

    //Get To Signature Activity
    private final static String path_fumida = "com.example.aditmail.fumida";
    public final static String Pesan_Extra = path_fumida + ".Pesan";

    private double total_harga;

    //Tindakan
    protected TextView txt_View, txt_Edit, txt_Delete;

    //Linear Parent
    private LinearLayout linear_Pest;

    //Kumpulan TableLayout dan TableRow
    protected TableLayout tbl_Identity, tbl_Hama, tbl_Chemical, tbl_MetodePenanganan, tbl_Area, tbl_HargaStatus, tbl_TTD;
    protected TableRow tbr_QtyPenanganan;

    //Get Data From Another Activity
    private final String id_pegawai = TampilanMenuUtama.id_pegawai;
    protected String tgl_input, id_survei;

    //Set TextView
    private TextView txt_idSurvei, txt_tglInput;

    //Bagian Identitas Pelanggan
    private EditText edt_ClientID_PestUpdate, edt_NamaPelanggan_PestUpdate, edt_Alamat_PestUpdate,
            edt_HP_PestUpdate, edt_Email_PestUpdate;
    private ImageView imgPinCircle;
    protected Double lat, lng;

    //Bagian Jenis Hama
    private CheckBox chk_Kecoa, chk_Kutu, chk_Lalat, chk_Cicak, chk_Semut, chk_Tikus, chk_Tawon, chk_Nyamuk, chk_HamaLainnya;
    private EditText edt_HamaLainnya;

    private Spinner spr_Kategori_TempatPest, spr_Kategori_PenangananPest;
    private EditText edt_QtyPenanganan_PestUpdate;
    private TextView txt_Penanganan;

    //Bagian Chemical
    private CheckBox chk_BoricAcid, chk_Cypermethrin, chk_ZetaCypermethrin, chk_Dichlorvos, chk_Fipronil, chk_Thiamethoxam;

    //Bagian Metode Pengendalian
    private EditText edt_QtyPerangkapTikus, edt_QtyLemTikus, edt_QtyUmpanIndoor, edt_QtyUmpanOutdoor, edt_QtyPohonLalat,
            edt_QtyBlackHole, edt_QtyFlyCatcher, edt_MetodeLainnya;
    private Spinner spr_Fumigasi;
    private CheckBox chk_Penyemprotan, chk_Pengembunan, chk_PengasapanThermal, chk_PengasapanMini, chk_PerangkapTikus, chk_LemTikus, chk_UmpanTikusIndoor,
            chk_UmpanTikusOutdoor, chk_UmpanSemut, chk_GelKecoa, chk_LemKecoa, chk_BlackHole, chk_FlyCatcher, chk_PohonLalat, chk_UmpanLalat, chk_MetodeLainnya, chk_Fumigasi;

    //Bagian Area Lokasi dan Kontrak
    private EditText edt_LuasBangunan, edt_LuasOutdoor;

    private EditText edt_DurasiKontrak, edt_PenawaranHarga, edt_GrandTotal;
    private TextView txt_DurasiKontrak, txt_PenawaranHarga;

    private RadioGroup rdg_Kontrak, rdg_Status;
    private RadioButton rdb_Kontrak, rdb_NonKontrak, rdb_Berhasil, rdb_Menunggu, rdb_Gagal;

    //Bagian Catatan Tambahan
    private EditText edt_CatatanTambahan;

    //Bagian TTD - Signature
    private Button btn_DialogTTD_Pelanggan, btn_DialogTTD_Consultant;

    @SuppressLint("StaticFieldLeak")
    protected static ImageView img_ttdPelanggan, img_ttdConsultant;

    private CheckBox chk_PernyataanPelanggan, chk_PernyataanConsultant;

    private String ConvertImage_Pelanggan = "", ConvertImage_Consultant = "";
    private String GetImageNameFromEditText_Pelanggan = "", GetImageNameFromEditText_Consultant = "";

    protected Bitmap getTTD_Consultant, getTTD_Pelanggan;
    protected String dateNow;

    //Untuk upload image ttd
    protected Bitmap bitmap_PestConsultant, bitmap_PestPelanggan;

    //Cek Status TTD
    private String image_path = "null";
    private String imagepath_PestConsultant = "null";

    //Untuk Upload Image Foto...
    protected Bitmap getFotoSatu, getFotoDua, getFotoTiga, getFotoEmpat;
    private String ConvertImage_FotoSatu = "", ConvertImage_FotoDua = "", ConvertImage_FotoTiga = "", ConvertImage_FotoEmpat = "";
    private String GetImageNameFromEditText_FotoSatu = "", GetImageNameFromEditText_FotoDua = "", GetImageNameFromEditText_FotoTiga = "",
            GetImageNameFromEditText_FotoEmpat = "";

    //Button Foto
    private Button btn_PilihFotoSatu, btn_PilihFotoDua, btn_PilihFotoTiga, btn_PilihFotoEmpat;

    @SuppressLint("StaticFieldLeak")
    protected static ImageView img_FotoSatu, img_FotoDua, img_FotoTiga, img_FotoEmpat;

    private Space spc_Satu, spc_Dua, spc_Tiga;

    //Button Update
    private Button btn_UpdateSurvey;

    private String kontrak = "";
    private String status_kerjasama = "";

    ProgressDialog pDialog;

    //Data getString from JSON ---------------------------------------------------->
    private String ClientID, NamaPelanggan, KategoriTempat, Alamat, NoHP, Email, JenisHama, HamaLainnya, KategoriPenanganan,
            QtyPenanganan, JenisChemical, MetodeKendali, QtyLemTikus, QtyPerangkapTikus, QtyUmpanTikus,
            QtyUmpanTikusIndoor, QtyPohonLalat, QtyBlackHole, JenisFumigasi, QtyFlyCatcher, MetodeLain, LuasIndoor,
            LuasOutdoor, JenisKontrak, DurasiKontrak, PenawaranHarga, TotalHarga, StatusKerjasama,
            CatatanTambahan, GetImgPelanggan_TTD, GetImgConsultant_TTD, pathFotoSatu, pathFotoDua, pathFotoTiga, pathFotoEmpat;
    private String latLong_AlamatCustomer, latLong_Consultant;
    //Data getString from JSON ---------------------------------------------------->

    //Data Save to DB ------------------------------------------------------------->
    private String IDClient, namaPelanggan, kategoriTempatPelanggan, alamatPelanggan, hpPelanggan, emailPelanggan,
            Jenis_Hama, Hama_Lainnya, Penanganan, Qty_Penanganan, Jenis_Chemical, metode_kendali_DB, qty_lemTikus_DB,
            qty_perangkapTikus_DB, qty_umpanTikus_DB, qty_umpanTikusIndoor_DB, qty_pohonLalat_DB, qty_blackHole_DB,
            jenis_fumigasi_DB, qty_FlyCatcher_DB, metodeLain_DB, indoor_DB, outdoor_DB, jenis_kontrak, durasi_kontrak_DB,
            penawaran_harga_DB, status_kerjasama_kontrak, catatan_tambahan;
    //Data Save to DB ------------------------------------------------------------->

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
        setContentView(R.layout.activity_update_survei_pest_control);

        InitData();

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);
        pDialog.setCanceledOnTouchOutside(false);

        //Get Intent to Activity
        Intent intent = getIntent();
        tgl_input = intent.getStringExtra(Konfigurasi.KEY_TANGGAL_INPUT);
        id_survei = intent.getStringExtra(Konfigurasi.KEY_SURVEI_ID);

        txt_idSurvei.setText(id_survei);
        txt_tglInput.setText(tgl_input);

        if (conMgr != null) {
            if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
                getDataSurveiPest();
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }
        }

        //Function Tindakan
        EditData();

        //Call Function
        TextWatcher();
        CheckButtonHama();
        CheckButtonMetodeKendali();
        CekRadioGroup();
        CekSpinner();
        CekTandaTangan();
    }

    @Override
    protected void onStart() {
        OpenPhoto();
        OpenMaps();
        super.onStart();
    }

    private void InitData() {
        txt_idSurvei = findViewById(R.id.textView_idSurvei_Pest);
        txt_tglInput = findViewById(R.id.textView_Tgl_Input);

        //Tindakan
        txt_Edit = findViewById(R.id.textView_Update);
        txt_Delete = findViewById(R.id.textView_Delete);
        txt_View = findViewById(R.id.textView_View);

        linear_Pest = findViewById(R.id.linearLayout_UpdatePest);

        //Kumpulan TableLayout dan TableRow
        tbl_Identity = findViewById(R.id.tableLayout_Identity);
        tbl_Hama = findViewById(R.id.tableLayout_Hama);
        tbl_Chemical = findViewById(R.id.tableLayout_Chemical);
        tbl_MetodePenanganan = findViewById(R.id.tableLayout_MetodeKendali);
        tbl_Area = findViewById(R.id.tableLayout_Area);
        tbl_HargaStatus = findViewById(R.id.tableLayout_HargaStatus);
        tbl_TTD = findViewById(R.id.tableLayout_TTD);
        tbr_QtyPenanganan = findViewById(R.id.tableRow_QtyPenanganan);

        //Bagian Identitas Pelanggan
        edt_ClientID_PestUpdate = findViewById(R.id.editText_ClientID_Pest_Update);
        edt_NamaPelanggan_PestUpdate = findViewById(R.id.editText_NamaPelanggan_Update);
        edt_Alamat_PestUpdate = findViewById(R.id.editText_Alamat_Update);
        edt_HP_PestUpdate = findViewById(R.id.editText_NoHP_Update);
        edt_Email_PestUpdate = findViewById(R.id.editText_Email_Update);

        imgPinCircle = findViewById(R.id.imgPin);
        spr_Kategori_TempatPest = findViewById(R.id.spinner_Kategori_Update);

        //Bagian Jenis Hama
        chk_Semut = findViewById(R.id.checkBox_Semut_Update);
        chk_Tikus = findViewById(R.id.checkBox_Tikus_Update);
        chk_Kecoa = findViewById(R.id.checkBox_Kecoa_Update);
        chk_Nyamuk = findViewById(R.id.checkBox_Nyamuk_Update);
        chk_Lalat = findViewById(R.id.checkBox_Lalat_Update);
        chk_Kutu = findViewById(R.id.checkBox_Kutu_Update);
        chk_Tawon = findViewById(R.id.checkBox_Tawon_Update);
        chk_Cicak = findViewById(R.id.checkBox_Cicak_Update);
        chk_HamaLainnya = findViewById(R.id.checkBox_HamaLainnya_Update);
        edt_HamaLainnya = findViewById(R.id.editText_HamaLainnya_Update);

        //Bagian Kategori Penanganan dan Chemical
        spr_Kategori_PenangananPest = findViewById(R.id.spinner_KategoriPenanganan_Update);
        edt_QtyPenanganan_PestUpdate = findViewById(R.id.editText_QtyPenanganan_Update);
        txt_Penanganan = findViewById(R.id.textView_Penanganan);

        //Bagian Chemical
        chk_BoricAcid = findViewById(R.id.checkBox_BoricAcid_Update);
        chk_Cypermethrin = findViewById(R.id.checkBox_Cypermethrin_Update);
        chk_ZetaCypermethrin = findViewById(R.id.checkBox_ZetaCypermethrin_Update);
        chk_Dichlorvos = findViewById(R.id.checkBox_Dichlorvos_Update);
        chk_Fipronil = findViewById(R.id.checkBox_Fipronil_Update);
        chk_Thiamethoxam = findViewById(R.id.checkBox_Thiamethoxam_Update);

        //Bagian Metode Pengendalian
        chk_Penyemprotan = findViewById(R.id.checkBox_Penyemprotan_Update);
        chk_Pengembunan = findViewById(R.id.checkBox_Pengembunan_Update);
        chk_PengasapanThermal = findViewById(R.id.checkBox_PengasapanThermal_Update);
        chk_PengasapanMini = findViewById(R.id.checkBox_PengasapanMini_Update);
        chk_PerangkapTikus = findViewById(R.id.checkBox_PerangkapTikus_Update);
        chk_LemTikus = findViewById(R.id.checkBox_LemTikus_Update);
        chk_UmpanTikusIndoor = findViewById(R.id.checkBox_UmpanTikusIndoor_Update);
        chk_UmpanTikusOutdoor = findViewById(R.id.checkBox_UmpanTikusOutdoor_Update);
        chk_UmpanSemut = findViewById(R.id.checkBox_UmpanSemut_Update);
        chk_GelKecoa = findViewById(R.id.checkBox_UmpanGelKecoa_Update);
        chk_LemKecoa = findViewById(R.id.checkBox_PerangkapLemKecoa_Update);
        chk_BlackHole = findViewById(R.id.checkBox_InstalasiBlackHole_Update);
        chk_FlyCatcher = findViewById(R.id.checkBox_InstalasiFlyCatcher_Update);
        chk_PohonLalat = findViewById(R.id.checkBox_PohonLalat_Update);
        chk_UmpanLalat = findViewById(R.id.checkBox_UmpanLalat_Update);
        chk_Fumigasi = findViewById(R.id.checkBox_Fumigasi_Update);
        chk_MetodeLainnya = findViewById(R.id.checkBox_MetodeLainnya_Update);

        edt_QtyPerangkapTikus = findViewById(R.id.editText_QtyPerangkapTikus_Update);
        edt_QtyLemTikus = findViewById(R.id.editText_QtyLemTikus_Update);
        edt_QtyUmpanIndoor = findViewById(R.id.editText_QtyUmpanIndoor_Update);
        edt_QtyUmpanOutdoor = findViewById(R.id.editText_QtyUmpanOutdoor_Update);
        edt_QtyPohonLalat = findViewById(R.id.editText_QtyPohonLalat_Update);
        edt_QtyBlackHole = findViewById(R.id.editText_QtyBlackHole_Update);
        edt_QtyFlyCatcher = findViewById(R.id.editText_QtyFlyCatcher_Update);
        edt_MetodeLainnya = findViewById(R.id.editText_MetodeLainnya_Update);
        spr_Fumigasi = findViewById(R.id.spinner_Fumigasi_Update);

        //Bagian Area Lokasi dan Kontrak
        edt_LuasBangunan = findViewById(R.id.editText_LuasBangunan_Update);
        edt_LuasOutdoor = findViewById(R.id.editText_LuasOutdoor_Update);

        rdg_Status = findViewById(R.id.RadioGroup_Status);
        edt_DurasiKontrak = findViewById(R.id.editText_DurasiKontrak_Update);
        edt_PenawaranHarga = findViewById(R.id.editText_PenawaranHarga_Update);
        edt_GrandTotal = findViewById(R.id.editText_GrandTotal_Update);
        txt_DurasiKontrak = findViewById(R.id.textView_DurasiKontrak);
        txt_PenawaranHarga = findViewById(R.id.textView_PenawaranHarga);

        rdg_Kontrak = findViewById(R.id.RadioGroup_Kontrak);
        rdb_Kontrak = findViewById(R.id.radioButton_Kontrak_Update);
        rdb_NonKontrak = findViewById(R.id.radioButton_NonKontrak_Update);

        rdb_Berhasil = findViewById(R.id.radioButton_Berhasil_Update);
        rdb_Menunggu = findViewById(R.id.radioButton_Menunggu_Update);
        rdb_Gagal = findViewById(R.id.radioButton_Gagal_Update);

        //Bagian Catatan Tambahan
        edt_CatatanTambahan = findViewById(R.id.editText_CatatanTambahan_Update);

        //Button Tanda Tangan
        btn_DialogTTD_Pelanggan = findViewById(R.id.button_GetTTD_Pelanggan);
        btn_DialogTTD_Consultant = findViewById(R.id.button_GetTTD_Consultant);

        //Check Pernyataan
        chk_PernyataanPelanggan = findViewById(R.id.checkBox_PernyataanCustomer);
        chk_PernyataanConsultant = findViewById(R.id.checkBox_PernyataanPestConsultant);

        //Image TTD
        img_ttdPelanggan = findViewById(R.id.imageView_TTD_Pelanggan_Update);
        img_ttdConsultant = findViewById(R.id.imageView_TTD_Consultant_Update);

        //Button Foto
        btn_PilihFotoSatu = findViewById(R.id.button_FotoSatu_UpdatePest);
        img_FotoSatu = findViewById(R.id.imageView_FotoSatu_UpdatePest);
        btn_PilihFotoDua = findViewById(R.id.button_FotoDua_UpdatePest);
        img_FotoDua = findViewById(R.id.imageView_FotoDua_UpdatePest);
        btn_PilihFotoTiga = findViewById(R.id.button_FotoTiga_UpdatePest);
        img_FotoTiga = findViewById(R.id.imageView_FotoTiga_UpdatePest);
        btn_PilihFotoEmpat = findViewById(R.id.button_FotoEmpat_UpdatePest);
        img_FotoEmpat = findViewById(R.id.imageView_FotoEmpat_UpdatePest);

        spc_Satu = findViewById(R.id.Space_Satu);
        spc_Dua = findViewById(R.id.Space_Dua);
        spc_Tiga = findViewById(R.id.Space_Tiga);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //Button Update
        btn_UpdateSurvey = findViewById(R.id.button_UpdatePest);
        btn_UpdateSurvey.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_UpdateSurvey) {
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
        setViewAndChildrenEnabled(linear_Pest, false);
        txt_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setViewAndChildrenEnabled(linear_Pest, true);
                btn_DialogTTD_Consultant.setEnabled(false);
                btn_DialogTTD_Pelanggan.setEnabled(false);
            }
        });

        txt_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UpdateSurveiPestControl.this);

                    builder.setCancelable(true);
                    builder.setTitle("Apakah Anda Yakin Menghapus Survei Pest Control Tersebut?");
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
                Intent a = new Intent(UpdateSurveiPestControl.this, PreviewSurveiPestControl.class);
                a.putExtra(Konfigurasi.KEY_TAG_ID, id_pegawai);
                a.putExtra(Konfigurasi.KEY_SURVEI_ID, id_survei);
                startActivity(a);
            }
        });
    }

    private void getDataSurveiPest() {
        @SuppressLint("StaticFieldLeak")
        class GetDataSurveiPest extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                pDialog.setMessage("Mengambil Data... Harap Menunggu...");
                pDialog.show();
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                pDialog.dismiss();
                showDataSurveiPest(s);
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();
                params.put(Konfigurasi.KEY_TAG_ID, id_pegawai);
                params.put(Konfigurasi.KEY_SURVEI_ID, id_survei);
                RequestHandler rh = new RequestHandler();
                return rh.sendPostRequest(Konfigurasi.URL_VIEW_SELECTED_PEST, params);
            }
        }
        GetDataSurveiPest ge = new GetDataSurveiPest();
        ge.execute();
    }

    private void showDataSurveiPest(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray(Konfigurasi.TAG_JSON_ARRAY);
            JSONObject c = result.getJSONObject(0);

            ClientID = c.getString(Konfigurasi.PEST_KEY_GET_TAG_CLIENT_ID);
            NamaPelanggan = c.getString(Konfigurasi.PEST_KEY_GET_TAG_NAMA_PELANGGAN);
            KategoriTempat = c.getString(Konfigurasi.PEST_KEY_GET_TAG_KATEGORI_TEMPAT);
            Alamat = c.getString(Konfigurasi.PEST_KEY_GET_TAG_ALAMAT);
            NoHP = c.getString(Konfigurasi.PEST_KEY_GET_TAG_HP);
            Email = c.getString(Konfigurasi.PEST_KEY_GET_TAG_EMAIL);

            latLong_AlamatCustomer = c.getString(Konfigurasi.PEST_KEY_GET_LATLONG_ALAMAT_PELANGGAN);
            latLong_Consultant = c.getString(Konfigurasi.PEST_KEY_GET_LATLONG_CONSULTANT);

            JenisHama = c.getString(Konfigurasi.PEST_KEY_GET_TAG_JENIS_HAMA);
            HamaLainnya = c.getString(Konfigurasi.PEST_KEY_GET_TAG_HAMA_LAINNYA);
            KategoriPenanganan = c.getString(Konfigurasi.PEST_KEY_GET_TAG_KATEGORI_PENANGANAN);
            QtyPenanganan = c.getString(Konfigurasi.PEST_KEY_GET_TAG_QTY_PENANGANAN);
            JenisChemical = c.getString(Konfigurasi.PEST_KEY_GET_TAG_CHEMICAL_PEST);
            MetodeKendali = c.getString(Konfigurasi.PEST_KEY_GET_TAG_METODE_KENDALI);
            QtyLemTikus = c.getString(Konfigurasi.PEST_KEY_GET_TAG_QTY_LEMTIKUS);
            QtyPerangkapTikus = c.getString(Konfigurasi.PEST_KEY_GET_TAG_QTY_PERANGKAPTIKUS);
            QtyUmpanTikus = c.getString(Konfigurasi.PEST_KEY_GET_TAG_QTY_UMPANOUTDOOR);
            QtyUmpanTikusIndoor = c.getString(Konfigurasi.PEST_KEY_GET_TAG_QTY_UMPANINDOOR);
            QtyPohonLalat = c.getString(Konfigurasi.PEST_KEY_GET_TAG_QTY_POHONLALAT);
            QtyBlackHole = c.getString(Konfigurasi.PEST_KEY_GET_TAG_QTY_BLACKHOLE);
            JenisFumigasi = c.getString(Konfigurasi.PEST_KEY_GET_TAG_JENIS_FUMIGASI);
            QtyFlyCatcher = c.getString(Konfigurasi.PEST_KEY_GET_TAG_QTY_FLYCATCHER);
            MetodeLain = c.getString(Konfigurasi.PEST_KEY_GET_TAG_METODE_LAIN);
            LuasIndoor = c.getString(Konfigurasi.PEST_KEY_GET_TAG_LUAS_INDOOR);
            LuasOutdoor = c.getString(Konfigurasi.PEST_KEY_GET_TAG_LUAS_OUTDOOR);
            JenisKontrak = c.getString(Konfigurasi.PEST_KEY_GET_JENIS_KONTRAK);
            DurasiKontrak = c.getString(Konfigurasi.PEST_KEY_GET_TAG_DURASI_KONTRAK);
            PenawaranHarga = c.getString(Konfigurasi.PEST_KEY_GET_TAG_PENAWARAN_HARGA);
            TotalHarga = c.getString(Konfigurasi.PEST_KEY_GET_TAG_TOTAL_HARGA);
            StatusKerjasama = c.getString(Konfigurasi.PEST_KEY_GET_TAG_STATUS_KERJASAMA);
            CatatanTambahan = c.getString(Konfigurasi.PEST_KEY_GET_TAG_CATATAN);
            GetImgPelanggan_TTD = c.getString(Konfigurasi.PEST_KEY_GET_IMG_PELANGGAN);
            GetImgConsultant_TTD = c.getString(Konfigurasi.PEST_KEY_GET_IMG_CONSULTANT);

            //Bagian VI : Foto
            pathFotoSatu = c.getString(Konfigurasi.PEST_KEY_GET_TAG_IMG_FOTO_SATU);
            pathFotoDua = c.getString(Konfigurasi.PEST_KEY_GET_TAG_IMG_FOTO_DUA);
            pathFotoTiga = c.getString(Konfigurasi.PEST_KEY_GET_TAG_FOTO_TIGA);
            pathFotoEmpat = c.getString(Konfigurasi.PEST_KEY_GET_TAG_FOTO_EMPAT);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        showData();
    }

    private void showData() {
        edt_ClientID_PestUpdate.setText(ClientID);
        edt_NamaPelanggan_PestUpdate.setText(NamaPelanggan);
        edt_Alamat_PestUpdate.setText(Alamat);
        edt_HP_PestUpdate.setText(NoHP);
        edt_Email_PestUpdate.setText(Email);
        edt_QtyPenanganan_PestUpdate.setText(QtyPenanganan);
        edt_QtyLemTikus.setText(QtyLemTikus);
        edt_QtyPerangkapTikus.setText(QtyPerangkapTikus);
        edt_QtyUmpanOutdoor.setText(QtyUmpanTikus);
        edt_QtyUmpanIndoor.setText(QtyUmpanTikusIndoor);
        edt_QtyPohonLalat.setText(QtyPohonLalat);
        edt_QtyBlackHole.setText(QtyBlackHole);
        edt_QtyFlyCatcher.setText(QtyFlyCatcher);
        edt_MetodeLainnya.setText(MetodeLain);
        edt_LuasBangunan.setText(LuasIndoor);
        edt_LuasOutdoor.setText(LuasOutdoor);
        edt_DurasiKontrak.setText(DurasiKontrak);
        edt_PenawaranHarga.setText(PenawaranHarga);
        edt_GrandTotal.setText(TotalHarga);

        edt_CatatanTambahan.setText(CatatanTambahan);

        Log.e("tag", "latlong : " + latLong_AlamatCustomer + "|" + latLong_Consultant);

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

        //Spinner Kategori Tempat Client
        if (KategoriTempat.equalsIgnoreCase("Kantor")) {
            spr_Kategori_TempatPest.setSelection(0);
        } else if (KategoriTempat.equalsIgnoreCase("Pabrik Makanan")) {
            spr_Kategori_TempatPest.setSelection(1);
        } else if (KategoriTempat.equalsIgnoreCase("Pabrik Non Makanan")) {
            spr_Kategori_TempatPest.setSelection(2);
        } else if (KategoriTempat.equalsIgnoreCase("Restoran")) {
            spr_Kategori_TempatPest.setSelection(3);
        } else {
            spr_Kategori_TempatPest.setSelection(4);
        }

        //CheckBox Jenis Hama yang Dikendalikan
        String[] JenisHama_Arry = JenisHama.split(",");
        for (String list_hama : JenisHama_Arry) {
            if (list_hama.equalsIgnoreCase("Kecoa")) {
                chk_Kecoa.setChecked(true);
            }
            if (list_hama.equalsIgnoreCase("Kutu")) {
                chk_Kutu.setChecked(true);
            }
            if (list_hama.equalsIgnoreCase("Lalat")) {
                chk_Lalat.setChecked(true);
            }
            if (list_hama.equalsIgnoreCase("Cicak")) {
                chk_Cicak.setChecked(true);
            }
            if (list_hama.equalsIgnoreCase("Semut")) {
                chk_Semut.setChecked(true);
            }
            if (list_hama.equalsIgnoreCase("Tikus")) {
                chk_Tikus.setChecked(true);
            }
            if (list_hama.equalsIgnoreCase("Tawon")) {
                chk_Tawon.setChecked(true);
            }
            if (list_hama.equalsIgnoreCase("Nyamuk")) {
                chk_Nyamuk.setChecked(true);
            }
            if (list_hama.equalsIgnoreCase("Lainnya")) {
                chk_HamaLainnya.setChecked(true);
                edt_HamaLainnya.setText(HamaLainnya);
            }
        }

        //Untuk Spinner Kategori Penanganan
        if (KategoriPenanganan.equalsIgnoreCase("Sistem Mobile")) {
            spr_Kategori_PenangananPest.setSelection(0);
        } else if (KategoriPenanganan.equalsIgnoreCase("Standby/Station")) {
            spr_Kategori_PenangananPest.setSelection(1);
        } else {
            spr_Kategori_PenangananPest.setSelection(2);
        }

        //CheckBox Jenis Chemical yang Digunakan
        String[] JenisChemical_Arry = JenisChemical.split(",");
        for (String list_chemical : JenisChemical_Arry) {
            if (list_chemical.equalsIgnoreCase("Boric Acid")) {
                chk_BoricAcid.setChecked(true);
            }
            if (list_chemical.equalsIgnoreCase("Cypermethrin")) {
                chk_Cypermethrin.setChecked(true);
            }
            if (list_chemical.equalsIgnoreCase("Zeta-Cypermethrin")) {
                chk_ZetaCypermethrin.setChecked(true);
            }
            if (list_chemical.equalsIgnoreCase("Dichlorvos")) {
                chk_Dichlorvos.setChecked(true);
            }
            if (list_chemical.equalsIgnoreCase("Fipronil")) {
                chk_Fipronil.setChecked(true);
            }
            if (list_chemical.equalsIgnoreCase("Thiamethoxam")) {
                chk_Thiamethoxam.setChecked(true);
            }
        }

        //CheckBox MetodeKendali yang Digunakan
        String[] MetodeKendali_Arry = MetodeKendali.split(",");
        for (String list_metode : MetodeKendali_Arry) {
            if (list_metode.equalsIgnoreCase("Penyemprotan (Spraying)")) {
                chk_Penyemprotan.setChecked(true);
            }
            if (list_metode.equalsIgnoreCase("Pengembunan (Cold Fogging)")) {
                chk_Pengembunan.setChecked(true);
            }
            if (list_metode.equalsIgnoreCase("Pengasapan (Mini Fogging)")) {
                chk_PengasapanMini.setChecked(true);
            }
            if (list_metode.equalsIgnoreCase("Pengasapan (Thermal Fogging)")) {
                chk_PengasapanThermal.setChecked(true);
            }
            if (list_metode.equalsIgnoreCase("Lem Tikus (Rodent Glue Trap)")) {
                chk_LemTikus.setChecked(true);
            }
            if (list_metode.equalsIgnoreCase("Perangkap Tikus Massal (Rodent Trap)")) {
                chk_PerangkapTikus.setChecked(true);
            }
            if (list_metode.equalsIgnoreCase("Umpan Tikus (Rodent Baiting Outdoor)")) {
                chk_UmpanTikusOutdoor.setChecked(true);
            }
            if (list_metode.equalsIgnoreCase("Umpan Tikus (Rodent Baiting Indoor)")) {
                chk_UmpanTikusIndoor.setChecked(true);
            }
            if (list_metode.equalsIgnoreCase("Umpan Gel Kecoa (Roach Gelling)")) {
                chk_GelKecoa.setChecked(true);
            }
            if (list_metode.equalsIgnoreCase("Umpan Semut (Ant Baiting)")) {
                chk_UmpanSemut.setChecked(true);
            }
            if (list_metode.equalsIgnoreCase("Perangkap Lem Kecoa (Hoy-hoy)")) {
                chk_LemKecoa.setChecked(true);
            }
            if (list_metode.equalsIgnoreCase("Umpan Lalat (Fly Baiting)")) {
                chk_UmpanLalat.setChecked(true);
            }
            if (list_metode.equalsIgnoreCase("Pohon Lalat")) {
                chk_PohonLalat.setChecked(true);
            }
            if (list_metode.equalsIgnoreCase("Instalasi Black Hole")) {
                chk_BlackHole.setChecked(true);
            }
            if (list_metode.equalsIgnoreCase("Fumigasi")) {
                chk_Fumigasi.setChecked(true);
            }
            if (list_metode.equalsIgnoreCase("Instalasi Fly Catcher")) {
                chk_FlyCatcher.setChecked(true);
            }
            if (list_metode.equalsIgnoreCase("Metode Lainnya")) {
                chk_MetodeLainnya.setChecked(true);
            }
        }

        //Untuk Spinner Jenis Fumigasi
        if (JenisFumigasi.equalsIgnoreCase("Phosphine (PH3)")) {
            spr_Fumigasi.setSelection(0);
        } else {
            spr_Fumigasi.setSelection(1);
        }

        //Untuk Radio Button Status Kontrak
        if (JenisKontrak.equalsIgnoreCase("Kontrak")) {
            rdb_Kontrak.setChecked(true);
        } else {
            rdb_NonKontrak.setChecked(true);
        }

        //Untuk Radio Button Status Kerjasama
        if (StatusKerjasama.equalsIgnoreCase("Berhasil")) {
            rdb_Berhasil.setChecked(true);
        } else if (StatusKerjasama.equalsIgnoreCase("Menunggu")) {
            rdb_Menunggu.setChecked(true);
        } else {
            rdb_Gagal.setChecked(true);
        }


    }

    private void OpenMaps() {
        imgPinCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenPlacePicker();
            }
        });
    }

    private void OpenPlacePicker() {
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent openMaps = new Intent(this, OpenMaps.class);
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

    private String kalkulasi_total() {
        double bulan, total, a;

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        if (!edt_DurasiKontrak.getText().toString().equals("") && edt_DurasiKontrak.getText().length() > 0) {
            bulan = Double.valueOf(edt_DurasiKontrak.getText().toString());
        } else {
            bulan = 1;
        }
        if (!edt_PenawaranHarga.getText().toString().equals("") && edt_PenawaranHarga.getText().length() > 0) {
            String penawaran = edt_PenawaranHarga.getText().toString();
            a = Double.parseDouble(penawaran);
            formatRupiah.format(a);

        } else {
            a = 0;
        }

        total = bulan * a;
        total_harga = bulan * a;

        return (formatRupiah.format(total));
    }

    private void TextWatcher() {
        //TextWatcher for Penawaran Harga
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

        //TextWatcher for Durasi Kontrak
        edt_DurasiKontrak.addTextChangedListener(new TextWatcher() {
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

        edt_HP_PestUpdate.addTextChangedListener(new TextWatcher() {

            int keyDel;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edt_HP_PestUpdate.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                        if (keyCode == KeyEvent.KEYCODE_DEL) {
                            keyDel = 1;
                        }
                        return false;
                    }
                });

                String currentString = edt_HP_PestUpdate.getText().toString();
                int currentLength = edt_HP_PestUpdate.getText().length();

                if (currentLength == 5 || currentLength == 10) {
                    keyDel = 1;
                }

                if (keyDel == 0) {
                    if (currentLength == 4 || currentLength == 9) {
                        edt_HP_PestUpdate.setText(edt_HP_PestUpdate.getText() + "-");
                        edt_HP_PestUpdate.setSelection(edt_HP_PestUpdate.getText().length());
                    }
                } else {
                    if (currentLength != 5 && currentLength != 10) {
                        keyDel = 0;
                    } else if ((currentLength == 5 || currentLength == 10)
                            && !"-".equals(currentString.substring(currentLength - 1, currentLength))) {
                        edt_HP_PestUpdate.setText(currentString.substring(0, currentLength - 1) + "-"
                                + currentString.substring(currentLength - 1, currentLength));
                        edt_HP_PestUpdate.setSelection(edt_HP_PestUpdate.getText().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void CheckButtonHama() {
        // <--- CheckButton for Jenis Hama --->
        chk_Tikus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    show_Tikus();
                } else {
                    hide_Tikus();
                }
            }
        });

        chk_Kecoa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    show_Kecoa();
                } else if (chk_Semut.isChecked() || chk_Kutu.isChecked() || chk_Tawon.isChecked()) {
                    chk_LemKecoa.setVisibility(View.GONE);
                    chk_LemKecoa.setChecked(false);
                    chk_GelKecoa.setVisibility(View.GONE);
                    chk_GelKecoa.setChecked(false);
                } else {
                    hide_Kecoa();
                }
            }
        });

        chk_Semut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    show_Semut();
                } else if (chk_Kecoa.isChecked() || chk_Kutu.isChecked() || chk_Tawon.isChecked()) {
                    chk_UmpanSemut.setVisibility(View.GONE);
                    chk_UmpanSemut.setChecked(false);
                } else {
                    hide_Semut();
                }
            }
        });

        chk_Nyamuk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    show_Nyamuk();
                } else if (chk_Lalat.isChecked() || chk_Kutu.isChecked()) {
                    chk_BlackHole.setVisibility(View.GONE);
                    chk_BlackHole.setChecked(false);
                } else {
                    hide_Nyamuk();
                }
            }
        });

        chk_Lalat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    show_Lalat();
                } else if (chk_Nyamuk.isChecked() || chk_Kutu.isChecked()) {
                    chk_FlyCatcher.setVisibility(View.GONE);
                    chk_FlyCatcher.setChecked(false);
                    chk_PohonLalat.setVisibility(View.GONE);
                    chk_PohonLalat.setChecked(false);
                    chk_UmpanLalat.setVisibility(View.GONE);
                    chk_UmpanLalat.setChecked(false);
                } else {
                    hide_Lalat();
                }
            }
        });

        chk_Kutu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    show_Kutu();
                } else if (chk_Semut.isChecked() || chk_Kecoa.isChecked() || chk_Tawon.isChecked()) {
                    chk_Penyemprotan.setVisibility(View.VISIBLE);
                    chk_Pengembunan.setVisibility(View.GONE);
                } else if (chk_Nyamuk.isChecked() || chk_Lalat.isChecked()) {
                    chk_Penyemprotan.setVisibility(View.GONE);
                    chk_Pengembunan.setVisibility(View.VISIBLE);
                } else {
                    hide_Kutu();
                }
            }
        });

        chk_Tawon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    show_Tawon();
                } else if (chk_Semut.isChecked() || chk_Kecoa.isChecked() || chk_Kutu.isChecked()) {
                    chk_Penyemprotan.setVisibility(View.VISIBLE);
                } else {
                    hide_Tawon();
                }
            }
        });

        chk_Cicak.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    show_Cicak();
                } else {
                    hide_Cicak();
                }
            }
        });

        chk_HamaLainnya.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_HamaLainnya.setVisibility(View.VISIBLE);
                    show_Lainnya();
                } else {
                    edt_HamaLainnya.setVisibility(View.GONE);
                    //hide_Lainnya();
                }
            }
        });
        // <--- CheckButton for Jenis Hama --->
    }

    private void CheckButtonMetodeKendali() {
        // <--- CheckButton for Jenis Pengendalian Hama --->
        chk_PerangkapTikus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_QtyPerangkapTikus.setVisibility(View.VISIBLE);
                } else {
                    edt_QtyPerangkapTikus.setVisibility(View.GONE);
                }
            }
        });

        chk_LemTikus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_QtyLemTikus.setVisibility(View.VISIBLE);
                } else {
                    edt_QtyLemTikus.setVisibility(View.GONE);
                }
            }
        });
        chk_UmpanTikusIndoor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_QtyUmpanIndoor.setVisibility(View.VISIBLE);
                } else {
                    edt_QtyUmpanIndoor.setVisibility(View.GONE);
                }
            }
        });
        chk_UmpanTikusOutdoor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_QtyUmpanOutdoor.setVisibility(View.VISIBLE);
                } else {
                    edt_QtyUmpanOutdoor.setVisibility(View.GONE);
                }
            }
        });
        chk_BlackHole.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_QtyBlackHole.setVisibility(View.VISIBLE);
                } else {
                    edt_QtyBlackHole.setVisibility(View.GONE);
                }
            }
        });
        chk_FlyCatcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_QtyFlyCatcher.setVisibility(View.VISIBLE);
                } else {
                    edt_QtyFlyCatcher.setVisibility(View.GONE);
                }
            }
        });
        chk_PohonLalat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_QtyPohonLalat.setVisibility(View.VISIBLE);
                } else {
                    edt_QtyPohonLalat.setVisibility(View.GONE);
                }
            }
        });
        chk_Fumigasi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    spr_Fumigasi.setVisibility(View.VISIBLE);
                } else {
                    spr_Fumigasi.setVisibility(View.GONE);
                }
            }
        });
        chk_MetodeLainnya.setOnCheckedChangeListener((new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_MetodeLainnya.setVisibility(View.VISIBLE);
                } else {
                    edt_MetodeLainnya.setVisibility(View.GONE);
                }
            }
        }));
        // <--- CheckButton for Jenis Pengendalian Hama --->
    }

    private void CekRadioGroup() {
        // <--- Radio Group for Kontrak --->
        rdg_Kontrak.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton_Kontrak_Update:
                        txt_DurasiKontrak.setVisibility(View.VISIBLE);
                        edt_DurasiKontrak.setVisibility(View.VISIBLE);
                        txt_PenawaranHarga.setVisibility(View.VISIBLE);
                        txt_PenawaranHarga.setText("Penawaran Harga/Bulan");
                        edt_PenawaranHarga.setVisibility(View.VISIBLE);
                        kontrak = "Kontrak";
                        break;
                    case R.id.radioButton_NonKontrak_Update:
                        txt_PenawaranHarga.setVisibility(View.VISIBLE);
                        txt_PenawaranHarga.setText("Penawaran Harga");
                        txt_DurasiKontrak.setVisibility(View.GONE);
                        edt_DurasiKontrak.setVisibility(View.GONE);
                        edt_DurasiKontrak.setText("");
                        edt_PenawaranHarga.setVisibility(View.VISIBLE);
                        kontrak = "Non-Kontrak";
                        break;
                }
            }
        });
        // <--- Radio Group for Kontrak --->

        // <--- Radio Group for Status --->
        rdg_Status.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton_Berhasil_Update:
                        status_kerjasama = "Berhasil";
                        break;
                    case R.id.radioButton_Gagal_Update:
                        status_kerjasama = "Gagal";
                        break;
                    case R.id.radioButton_Menunggu_Update:
                        status_kerjasama = "Menunggu";
                        Toast.makeText(UpdateSurveiPestControl.this, "Catatan:\n" +
                                "Pastikan untuk Melakukan Konfirmasi Kembali Dalam Beberapa Waktu Kedepan Kepada Pelanggan/Perusahaan", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
        // <--- Radio Group for Status --->
    }

    private void CekSpinner() {
        // <--- Spinner for Kategori Penanganan --->
        spr_Kategori_PenangananPest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spr_Kategori_PenangananPest.getSelectedItem().equals("Sistem Mobile")) {
                    edt_QtyPenanganan_PestUpdate.setVisibility(View.VISIBLE);
                    // edt_QtyPenanganan_PestUpdate.setText("");
                    txt_Penanganan.setText("x Kunjungan/Bulan");
                    txt_Penanganan.setVisibility(View.VISIBLE);
                } else if (spr_Kategori_PenangananPest.getSelectedItem().equals("Standby/Station")) {
                    edt_QtyPenanganan_PestUpdate.setVisibility(View.VISIBLE);
                    //  edt_QtyPenanganan_PestUpdate.setText("");
                    txt_Penanganan.setText("Orang");
                    txt_Penanganan.setVisibility(View.VISIBLE);
                } else {
                    edt_QtyPenanganan_PestUpdate.setVisibility(View.GONE);
                    edt_QtyPenanganan_PestUpdate.setText("");
                    txt_Penanganan.setText("");
                    txt_Penanganan.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // <--- Spinner for Kategori Penanganan --->
    }

    private void CekTandaTangan() {
        // <--- CheckButton Pernyataan --->
        chk_PernyataanPelanggan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btn_DialogTTD_Pelanggan.setEnabled(true);
                } else {
                    btn_DialogTTD_Pelanggan.setEnabled(false);
                }
            }
        });

        chk_PernyataanConsultant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btn_DialogTTD_Consultant.setEnabled(true);
                } else {
                    btn_DialogTTD_Consultant.setEnabled(false);
                }
            }
        });
        // <--- CheckButton Pernyataan --->

        //Button for Signature Pelanggan
        btn_DialogTTD_Pelanggan.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SignatureActivity.class);
                i.putExtra(Pesan_Extra, "TTD_PelangganPestControl");
                startActivityForResult(i, REQ_TTD_PELANGGAN);
            }
        });

        //Button for Signature Consultant
        btn_DialogTTD_Consultant.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(getApplicationContext(), SignatureActivity.class);
                //startActivityForResult(a,2);
                a.putExtra(Pesan_Extra, "TTD_ConsultantPestControl");
                startActivityForResult(a, REQ_TTD_CONSULTANT);
            }
        });
    }

    private void OpenPhoto() {
        //Open galeri
        btn_PilihFotoSatu.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_FotoSatu.setVisibility(View.VISIBLE);
                spc_Satu.setVisibility(View.VISIBLE);
                img_FotoDua.setVisibility(View.VISIBLE);
                btn_PilihFotoDua.setVisibility(View.VISIBLE);
                showPictureDialog_Satu();
            }
        });

        btn_PilihFotoDua.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_FotoTiga.setVisibility(View.VISIBLE);
                spc_Dua.setVisibility(View.VISIBLE);
                btn_PilihFotoTiga.setVisibility(View.VISIBLE);
                showPictureDialog_Dua();
            }
        });

        btn_PilihFotoTiga.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_FotoEmpat.setVisibility(View.VISIBLE);
                spc_Tiga.setVisibility(View.VISIBLE);
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

    private void showPictureDialog_Satu() {
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(this);
        pictureDialog.setTitle("Pilih Aksi");

        String[] pictureDialogItems = {
                "Pilih Foto dari Galeri", "Ambil Foto dari Kamera"};

        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        if (Build.VERSION.SDK_INT >= 23) {
                            openGaleri();
                        }
                        break;

                    case 1:
                        if (Build.VERSION.SDK_INT >= 23) {
                            openKamera();
                        }
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

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_GALERI_SATU);
    }

    private void openKamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQ_KAMERA_SATU);
    }

    private void showPictureDialog_Dua() {
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(this);
        pictureDialog.setTitle("Pilih Aksi");

        String[] pictureDialogItems = {
                "Pilih Foto dari Galeri", "Ambil Foto dari Kamera"};

        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_GALERI_DUA);
                        break;

                    case 1:
                        Intent intent_cameraDua = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent_cameraDua, REQ_KAMERA_DUA);
                        break;
                }
            }
        });
        pictureDialog.show();
    }

    private void showPictureDialog_Tiga() {
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(this);
        pictureDialog.setTitle("Pilih Aksi");

        String[] pictureDialogItems = {
                "Pilih Foto dari Galeri", "Ambil Foto dari Kamera"};

        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_GALERI_TIGA);
                        break;

                    case 1:
                        Intent intent_cameraDua = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent_cameraDua, REQ_KAMERA_TIGA);
                        break;
                }
            }
        });
        pictureDialog.show();
    }

    private void showPictureDialog_Empat() {
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(this);
        pictureDialog.setTitle("Pilih Aksi");

        String[] pictureDialogItems = {
                "Pilih Foto dari Galeri", "Ambil Foto dari Kamera"};

        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_GALERI_EMPAT);
                        break;

                    case 1:
                        Intent intent_cameraDua = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent_cameraDua, REQ_KAMERA_EMPAT);
                        break;
                }
            }
        });
        pictureDialog.show();
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
        dateNow = new SimpleDateFormat("dd-MM-yy_hh.mm.ss", Locale.getDefault()).format(new Date());
        String getNamaPelanggan = edt_NamaPelanggan_PestUpdate.getText().toString();

        if (requestCode == REQ_TTD_PELANGGAN) {
            if (resultCode == RESULT_OK) {
                //Nampilin hasil ttd untuk Pest Pelanggan
                image_path = data.getStringExtra("imagePath_PestPelanggan");
                bitmap_PestPelanggan = BitmapFactory.decodeFile(image_path);
                img_ttdPelanggan.setImageBitmap(bitmap_PestPelanggan);
                //getBitmap
                getTTD_Pelanggan = ((BitmapDrawable) UpdateSurveiPestControl.img_ttdPelanggan.getDrawable()).getBitmap();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                getTTD_Pelanggan.compress(Bitmap.CompressFormat.PNG, 50, stream);
                byte[] imageInByte = stream.toByteArray();
                ConvertImage_Pelanggan = Base64.encodeToString(imageInByte, Base64.DEFAULT);
                GetImageNameFromEditText_Pelanggan = "PestControl_" + getNamaPelanggan + "_" + dateNow + ".png";
            }
        }
        if (requestCode == REQ_TTD_CONSULTANT) {
            if (resultCode == RESULT_OK) {
                imagepath_PestConsultant = data.getStringExtra("imagePath_PestConsultant");
                //imagepath_PestConsultant = data.getStringExtra("imagePath_PestConsultant");
                bitmap_PestConsultant = BitmapFactory.decodeFile(imagepath_PestConsultant);
                img_ttdConsultant.setImageBitmap(bitmap_PestConsultant);
                //getBitmap
                getTTD_Consultant = ((BitmapDrawable) UpdateSurveiPestControl.img_ttdConsultant.getDrawable()).getBitmap();

                ByteArrayOutputStream stream_consultant = new ByteArrayOutputStream();
                getTTD_Consultant.compress(Bitmap.CompressFormat.PNG, 50, stream_consultant);
                byte[] imageInByte_consultant = stream_consultant.toByteArray();
                ConvertImage_Consultant = Base64.encodeToString(imageInByte_consultant, Base64.DEFAULT);
                GetImageNameFromEditText_Consultant = "PestControl_" + TampilanMenuUtama.username + "_" + dateNow + ".png";
            }
        }

        //Untuk Picture atau Kamera
        if (requestCode == REQ_KAMERA_SATU) {
            if (resultCode == RESULT_OK) {
                Bitmap bitmap_PickFoto = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                if (bitmap_PickFoto != null) {
                    bitmap_PickFoto.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
                    img_FotoSatu.setImageBitmap(bitmap_PickFoto);
                }

                GetImageNameFromEditText_FotoSatu = "PestControl_Foto_Satu_" + getNamaPelanggan + "_" + dateNow + ".png";
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

                    GetImageNameFromEditText_FotoSatu = "PestControl_Foto_Satu_" + getNamaPelanggan + "_" + dateNow + ".png";

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == REQ_KAMERA_DUA) {
            if (resultCode == RESULT_OK) {
                Bitmap bitmap_PickFoto = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                if (bitmap_PickFoto != null) {
                    bitmap_PickFoto.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
                    img_FotoDua.setImageBitmap(bitmap_PickFoto);
                }

                GetImageNameFromEditText_FotoDua = "PestControl_Foto_Dua_" + getNamaPelanggan + "_" + dateNow + ".png";
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

                    GetImageNameFromEditText_FotoDua = "PestControl_Foto_Dua_" + getNamaPelanggan + "_" + dateNow + ".png";

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == REQ_KAMERA_TIGA) {
            if (resultCode == RESULT_OK) {
                Bitmap bitmap_PickFoto = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                if (bitmap_PickFoto != null) {
                    bitmap_PickFoto.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
                    img_FotoTiga.setImageBitmap(bitmap_PickFoto);
                }

                GetImageNameFromEditText_FotoTiga = "PestControl_Foto_Tiga_" + getNamaPelanggan + "_" + dateNow + ".png";
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

                    GetImageNameFromEditText_FotoTiga = "PestControl_Foto_Tiga_" + getNamaPelanggan + "_" + dateNow + ".png";

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == REQ_KAMERA_EMPAT) {
            if (resultCode == RESULT_OK) {
                Bitmap bitmap_PickFoto = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                if (bitmap_PickFoto != null) {
                    bitmap_PickFoto.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
                    img_FotoEmpat.setImageBitmap(bitmap_PickFoto);
                }

                GetImageNameFromEditText_FotoEmpat = "WorkReport_Foto_Empat_" + getNamaPelanggan + "_" + dateNow + ".png";
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

                    GetImageNameFromEditText_FotoEmpat = "WorkReport_Foto_Tiga_" + getNamaPelanggan + "_" + dateNow + ".png";

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == REQ_PLACE_PICKER) {
            if (resultCode == RESULT_OK) {
                String lokasi = data.getStringExtra("Alamat_Lokasi");
                edt_Alamat_PestUpdate.setText(lokasi);

                latLong_AlamatCustomer = data.getStringExtra("LatLong_Lokasi");
                Log.e("tag", latLong_AlamatCustomer);
            }
        }
    }

    protected void OnClickUpdate() {
        // <---- Untuk Bagian 1 - Data Diri Pelanggan ---->
         IDClient = edt_ClientID_PestUpdate.getText().toString().toUpperCase();
         namaPelanggan = edt_NamaPelanggan_PestUpdate.getText().toString();
         kategoriTempatPelanggan = spr_Kategori_TempatPest.getSelectedItem().toString();
         alamatPelanggan = edt_Alamat_PestUpdate.getText().toString();
         hpPelanggan = edt_HP_PestUpdate.getText().toString();
         emailPelanggan = edt_Email_PestUpdate.getText().toString();

        if (TextUtils.isEmpty(IDClient)) {
            edt_ClientID_PestUpdate.setError("Harap Masukkan ID Client");
            edt_ClientID_PestUpdate.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(namaPelanggan)) {
            edt_NamaPelanggan_PestUpdate.setError("Harap Masukkan Nama Pelanggan/Perusahaan");
            edt_NamaPelanggan_PestUpdate.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(alamatPelanggan)) {
            edt_Alamat_PestUpdate.setError("Harap Masukkan Alamat Pelanggan/Perusahaan");
            edt_Alamat_PestUpdate.requestFocus();
            return;
        }


        if (TextUtils.isEmpty(hpPelanggan)) {
            edt_HP_PestUpdate.setError("Harap Masukkan No.HP atau Telepon Pelanggan/Perusahaan");
            edt_HP_PestUpdate.requestFocus();
            return;
        }

        if (hpPelanggan.trim().length() < 5) {
            edt_HP_PestUpdate.setError("Harap Masukkan Nomor Handphone/Telepon yang Valid");
            edt_HP_PestUpdate.requestFocus();
            return;
        }

        // <---- Untuk Bagian 2 - Kategori Penanganan ---->
         Penanganan = spr_Kategori_PenangananPest.getSelectedItem().toString();

        String Qty_Penanganan_tmp = "";
        if (Penanganan.equals("Sistem Mobile")) {
            Qty_Penanganan_tmp = edt_QtyPenanganan_PestUpdate.getText().toString();
        } else if (Penanganan.equals("Standby/Station")) {
            Qty_Penanganan_tmp = edt_QtyPenanganan_PestUpdate.getText().toString();
        }

         Qty_Penanganan = Qty_Penanganan_tmp;

        if (edt_QtyPenanganan_PestUpdate.getVisibility() == View.VISIBLE && TextUtils.isEmpty(Qty_Penanganan_tmp)) {
            edt_QtyPenanganan_PestUpdate.setError("Harap Masukkan Jumlah Kuantitas yang Disepakati");
            edt_QtyPenanganan_PestUpdate.requestFocus();
            return;
        }

        // <---- Untuk Bagian 3 - Jenis Hama ---->
        String jenis_hama_tmp = "";
        String hama_lainnya_tmp = "";

        if (!chk_Kecoa.isChecked() && !chk_Kutu.isChecked() && !chk_Lalat.isChecked() && !chk_Cicak.isChecked() && !chk_Semut.isChecked() && !chk_Tikus.isChecked()
                && !chk_Tawon.isChecked() && !chk_Nyamuk.isChecked() && !chk_HamaLainnya.isChecked()) {
            chk_Kecoa.setError("Harap Memilih Minimal Satu Jenis Hama yang Akan Dikendalikan");
            chk_Kecoa.requestFocusFromTouch();
            return;
        }

        if (chk_Kecoa.isChecked()) {
            jenis_hama_tmp += "Kecoa,";
        }

        if (chk_Kutu.isChecked()) {
            jenis_hama_tmp += "Kutu,";
        }

        if (chk_Lalat.isChecked()) {
            jenis_hama_tmp += "Lalat,";
        }

        if (chk_Cicak.isChecked()) {
            jenis_hama_tmp += "Cicak,";
        }

        if (chk_Semut.isChecked()) {
            jenis_hama_tmp += "Semut,";
        }

        if (chk_Tikus.isChecked()) {
            jenis_hama_tmp += "Tikus,";
        }

        if (chk_Tawon.isChecked()) {
            jenis_hama_tmp += "Tawon,";
        }

        if (chk_Nyamuk.isChecked()) {
            jenis_hama_tmp += "Nyamuk,";
        }

        if (chk_HamaLainnya.isChecked()) {
            hama_lainnya_tmp = edt_HamaLainnya.getText().toString();

            if (TextUtils.isEmpty(hama_lainnya_tmp)) {
                edt_HamaLainnya.setError(("Harap Masukkan Jenis Hama Lainnya"));
                edt_HamaLainnya.requestFocus();
                return;
            }
            jenis_hama_tmp += "Lainnya,";
        }

         Jenis_Hama = jenis_hama_tmp;
         Hama_Lainnya = hama_lainnya_tmp;

        // <---- Untuk Bagian 4 - Chemical yang Digunakan ---->
        String jenis_chemical_tmp = "";

        if (!chk_BoricAcid.isChecked() && !chk_Cypermethrin.isChecked() && !chk_ZetaCypermethrin.isChecked() && !chk_Dichlorvos.isChecked()
                && !chk_Fipronil.isChecked() && !chk_Thiamethoxam.isChecked()) {
            chk_BoricAcid.setError("Harap Memilih Minimal Satu Jenis Chemical yang Digunakan");
            chk_BoricAcid.requestFocusFromTouch();
            return;
        }

        if (chk_BoricAcid.isChecked()) {
            jenis_chemical_tmp += "Boric Acid,";
        }

        if (chk_Cypermethrin.isChecked()) {
            jenis_chemical_tmp += "Cypermethrin,";
        }

        if (chk_ZetaCypermethrin.isChecked()) {
            jenis_chemical_tmp += "Zeta-Cypermethrin,";
        }

        if (chk_Dichlorvos.isChecked()) {
            jenis_chemical_tmp += "Dichlorvos,";
        }

        if (chk_Fipronil.isChecked()) {
            jenis_chemical_tmp += "Fipronil,";
        }

        if (chk_Thiamethoxam.isChecked()) {
            jenis_chemical_tmp += "Thiamethoxam,";
        }

         Jenis_Chemical = jenis_chemical_tmp;

        // <---- Untuk Bagian 5 - Metode Kendali Hama ---->
        String metode_kendali_tmp = "";
        String qty_lemTikus_tmp = "";
        String qty_perangkapTikus_tmp = "";
        String qty_umpanTikus_tmp = "";
        String qty_umpanTikusIndoor_tmp = "";
        String qty_pohonLalat_tmp = "";
        String qty_blackHole_tmp = "";
        String jenis_fumigasi_tmp = "";
        String qty_FlyCatcher_tmp = "";
        String metodeLain_tmp = "";

        if (chk_Penyemprotan.isChecked()) {
            metode_kendali_tmp += "Penyemprotan (Spraying),";
        }

        if (chk_Pengembunan.isChecked()) {
            metode_kendali_tmp += "Pengembunan (Cold Fogging),";
        }

        if (chk_PengasapanMini.isChecked()) {
            metode_kendali_tmp += "Pengasapan (Mini Fogging),";
        }

        if (chk_PengasapanThermal.isChecked()) {
            metode_kendali_tmp += "Pengasapan (Thermal Fogging),";
        }

        if (chk_LemTikus.isChecked()) {

            qty_lemTikus_tmp = edt_QtyLemTikus.getText().toString();

            if (TextUtils.isEmpty(qty_lemTikus_tmp)) {
                edt_QtyLemTikus.setError(("Harap Masukkan Banyak Perangkap yang Dibutuhkan"));
                edt_QtyLemTikus.requestFocus();
                return;
            }
            metode_kendali_tmp += "Lem Tikus (Rodent Glue Trap),";
        }

        if (chk_PerangkapTikus.isChecked()) {

            qty_perangkapTikus_tmp = edt_QtyPerangkapTikus.getText().toString();

            if (TextUtils.isEmpty(qty_perangkapTikus_tmp)) {
                edt_QtyPerangkapTikus.setError(("Harap Masukkan Banyak Perangkap yang Dibutuhkan"));
                edt_QtyPerangkapTikus.requestFocus();
                return;
            }
            metode_kendali_tmp += "Perangkap Tikus Massal (Rodent Trap),";
        }

        if (chk_UmpanTikusOutdoor.isChecked()) {

            qty_umpanTikus_tmp = edt_QtyUmpanOutdoor.getText().toString();

            if (TextUtils.isEmpty(qty_umpanTikus_tmp)) {
                edt_QtyUmpanOutdoor.setError(("Harap Masukkan Banyak Perangkap yang Dibutuhkan"));
                edt_QtyUmpanOutdoor.requestFocus();
                return;
            }
            metode_kendali_tmp += "Umpan Tikus (Rodent Baiting Outdoor),";
        }

        if (chk_UmpanTikusIndoor.isChecked()) {
            qty_umpanTikusIndoor_tmp = edt_QtyUmpanIndoor.getText().toString();

            if (TextUtils.isEmpty(qty_umpanTikusIndoor_tmp)) {
                edt_QtyUmpanIndoor.setError(("Harap Masukkan Banyak Perangkap yang Dibutuhkan"));
                edt_QtyUmpanIndoor.requestFocus();
                return;
            }
            metode_kendali_tmp += "Umpan Tikus (Rodent Baiting Indoor),";
        }

        if (chk_GelKecoa.isChecked()) {
            metode_kendali_tmp += "Umpan Gel Kecoa (Roach Gelling),";
        }

        if (chk_UmpanSemut.isChecked()) {
            metode_kendali_tmp += "Umpan Semut (Ant Baiting),";
        }

        if (chk_LemKecoa.isChecked()) {
            metode_kendali_tmp += "Perangkap Lem Kecoa (Hoy-hoy),";
        }

        if (chk_UmpanLalat.isChecked()) {
            metode_kendali_tmp += "Umpan Lalat (Fly Baiting),";
        }

        if (chk_PohonLalat.isChecked()) {
            qty_pohonLalat_tmp = edt_QtyPohonLalat.getText().toString();

            if (TextUtils.isEmpty(qty_pohonLalat_tmp)) {
                edt_QtyPohonLalat.setError(("Harap Masukkan Banyak Perangkap yang Dibutuhkan"));
                edt_QtyPohonLalat.requestFocus();
                return;
            }
            metode_kendali_tmp += "Pohon Lalat,";
        }

        if (chk_BlackHole.isChecked()) {
            qty_blackHole_tmp = edt_QtyBlackHole.getText().toString();

            if (TextUtils.isEmpty(qty_blackHole_tmp)) {
                edt_QtyBlackHole.setError(("Harap Masukkan Banyak Perangkap yang Dibutuhkan"));
                edt_QtyBlackHole.requestFocus();
                return;
            }
            metode_kendali_tmp += "Instalasi Black Hole,";
        }

        if (chk_Fumigasi.isChecked()) {
            jenis_fumigasi_tmp = spr_Fumigasi.getSelectedItem().toString();
            metode_kendali_tmp += "Fumigasi,";
        }

        if (chk_FlyCatcher.isChecked()) {
            qty_FlyCatcher_tmp = edt_QtyFlyCatcher.getText().toString();

            if (TextUtils.isEmpty(qty_FlyCatcher_tmp)) {
                edt_QtyFlyCatcher.setError(("Harap Masukkan Banyak Perangkap yang Dibutuhkan"));
                edt_QtyFlyCatcher.requestFocus();
                return;
            }
            metode_kendali_tmp += "Instalasi Fly Catcher,";
        }

        if (chk_MetodeLainnya.isChecked()) {
            metodeLain_tmp = edt_MetodeLainnya.getText().toString();

            if (TextUtils.isEmpty(metodeLain_tmp)) {
                edt_MetodeLainnya.setError(("Harap Masukkan Metode Lainnya"));
                edt_MetodeLainnya.requestFocus();
                return;
            }
            metode_kendali_tmp += "Metode Lainnya,";
        }

        //Jenis Jenis Metode Kendali
         metode_kendali_DB = metode_kendali_tmp;

        //Kuantitasnya
         qty_lemTikus_DB = qty_lemTikus_tmp;
         qty_perangkapTikus_DB = qty_perangkapTikus_tmp;
         qty_umpanTikus_DB = qty_umpanTikus_tmp;
         qty_umpanTikusIndoor_DB = qty_umpanTikusIndoor_tmp;
         qty_pohonLalat_DB = qty_pohonLalat_tmp;
         qty_blackHole_DB = qty_blackHole_tmp;
         jenis_fumigasi_DB = jenis_fumigasi_tmp;
         qty_FlyCatcher_DB = qty_FlyCatcher_tmp;
         metodeLain_DB = metodeLain_tmp;

        // <---- Untuk Bagian 6 - Area Lokasi ---->
         indoor_DB = edt_LuasBangunan.getText().toString();
         outdoor_DB = edt_LuasOutdoor.getText().toString();

        if (TextUtils.isEmpty(indoor_DB)) {
            edt_LuasBangunan.setError(("Harap Masukkan Luas Bangunan"));
            edt_LuasBangunan.requestFocus();
            return;
        }

        // <---- Untuk Bagian 7 - Kontrak atau Non Kontrak---->
         jenis_kontrak = kontrak;
         status_kerjasama_kontrak = status_kerjasama;

        if (jenis_kontrak.isEmpty()) {
            rdb_Kontrak.setError("Harap Memilih Jenis Kontrak");
            rdb_Kontrak.requestFocusFromTouch();
            return;
        }

        if (status_kerjasama_kontrak.isEmpty()) {
            rdb_Berhasil.setError("Harap Memilih Status Kerjasama");
            rdb_Berhasil.requestFocusFromTouch();
            return;
        }

        String durasi_kontrak_tmp = "";
        String penawaran_harga_tmp = "";
        if (jenis_kontrak.equals("Kontrak")) {
            penawaran_harga_tmp = edt_PenawaranHarga.getText().toString();
            durasi_kontrak_tmp = edt_DurasiKontrak.getText().toString();

            if (TextUtils.isEmpty(penawaran_harga_tmp)) {
                edt_PenawaranHarga.setError(("Harap Masukkan Penawaran Harga"));
                edt_PenawaranHarga.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(durasi_kontrak_tmp)) {
                edt_DurasiKontrak.setError(("Harap Masukkan Durasi Kontrak"));
                edt_DurasiKontrak.requestFocus();
                return;
            }
        } else if (jenis_kontrak.equals("Non-Kontrak")) {
            penawaran_harga_tmp = edt_PenawaranHarga.getText().toString();

            if (TextUtils.isEmpty(penawaran_harga_tmp)) {
                edt_PenawaranHarga.setError(("Harap Masukkan Penawaran Harga"));
                edt_PenawaranHarga.requestFocus();
                return;
            }
        }

         durasi_kontrak_DB = durasi_kontrak_tmp;
         penawaran_harga_DB = penawaran_harga_tmp;

         catatan_tambahan = edt_CatatanTambahan.getText().toString();

        if (!chk_PernyataanPelanggan.isChecked()) {
            chk_PernyataanPelanggan.setError("Harap Mencentang Pernyataan Berikut dan Menandatanganinya!");
            chk_PernyataanPelanggan.requestFocusFromTouch();
            return;
        }

        if (!chk_PernyataanConsultant.isChecked()) {
            chk_PernyataanConsultant.setError("Harap Mencentang Pernyataan Berikut dan Menandatanganinya!");
            chk_PernyataanConsultant.requestFocusFromTouch();
            return;
        }

        if (img_ttdConsultant.getDrawable() == null) {
            btn_DialogTTD_Consultant.setError("Harap Melakukan Proses Tanda Tangan Digital!");
            btn_DialogTTD_Consultant.requestFocusFromTouch();
            return;
        }

        if (img_ttdPelanggan.getDrawable() == null) {
            btn_DialogTTD_Pelanggan.setError("Harap Melakukan Proses Tanda Tangan Digital!");
            btn_DialogTTD_Pelanggan.requestFocusFromTouch();
            return;
        }

        //Untuk Upload Foto-Foto Pekerjaan
        if (img_FotoSatu.getVisibility() == View.GONE) {
            btn_PilihFotoSatu.setError("Harap Mengupload Foto Pekerjaan #1!");
            btn_PilihFotoSatu.requestFocusFromTouch();
            return;
        }

        if (img_FotoSatu.getDrawable() == null) {
            btn_PilihFotoSatu.setError("Harap Mengupload Foto Pekerjaan #1!");
            btn_PilihFotoSatu.requestFocusFromTouch();
            return;
        }

        if (img_FotoDua.getDrawable() == null) {
            btn_PilihFotoDua.setError("Harap Mengupload Foto Pekerjaan #2!");
            btn_PilihFotoDua.requestFocusFromTouch();
            return;
        }

        if (img_FotoTiga.getDrawable() == null) {
            btn_PilihFotoTiga.setError("Harap Mengupload Foto Pekerjaan #3!");
            btn_PilihFotoTiga.requestFocusFromTouch();
            return;
        }

        if (img_FotoEmpat.getDrawable() == null) {
            btn_PilihFotoEmpat.setError("Harap Mengupload Foto Pekerjaan #4!");
            btn_PilihFotoEmpat.requestFocusFromTouch();
            return;
        }

        //Menampilkan Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateSurveiPestControl.this);

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
                saveToDB();
            }
        });
        builder.show();
    }

    private void saveToDB(){
        @SuppressLint("StaticFieldLeak")
        class simpan extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                pDialog.setMessage("Menyimpan Data Survei Pest Control ke Database, Harap Tunggu...");
                pDialog.show();
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String s) {
                pDialog.dismiss();
                Toast.makeText(UpdateSurveiPestControl.this, s, Toast.LENGTH_LONG).show();

                if (!GetImageNameFromEditText_FotoEmpat.isEmpty() || !GetImageNameFromEditText_FotoTiga.isEmpty() ||
                        !GetImageNameFromEditText_FotoDua.isEmpty() || !GetImageNameFromEditText_FotoSatu.isEmpty()) {
                    uploadFoto();
                }
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(Void... v) {
                //String nya musti sama dengan yang ada di konfigurasi...

                HashMap<String, String> params = new HashMap<>();

                //Bagian I : Data Kostumer
                params.put(Konfigurasi.KEY_SAVE_ID_CLIENT, IDClient);
                params.put(Konfigurasi.KEY_SAVE_NAMA_PELANGGAN, namaPelanggan);
                params.put(Konfigurasi.KEY_SAVE_KATEGORITEMPAT, kategoriTempatPelanggan);
                params.put(Konfigurasi.KEY_SAVE_ALAMAT_PELANGGAN, alamatPelanggan);
                params.put(Konfigurasi.KEY_SAVE_HP_PELANGGAN, hpPelanggan);
                params.put(Konfigurasi.KEY_SAVE_EMAIL_PELANGGAN, emailPelanggan);

                params.put(Konfigurasi.KEY_SAVE_LATLONG_ALAMAT_PELANGGAN, latLong_AlamatCustomer);
                params.put(Konfigurasi.KEY_SAVE_LATLONG_CONSULTANT, latLong_Consultant);

                //Bagian II : Kendali Hama
                params.put(Konfigurasi.KEY_SAVE_JENIS_HAMA, Jenis_Hama);
                params.put(Konfigurasi.KEY_SAVE_JENIS_HAMA_LAINNYA, Hama_Lainnya);

                params.put(Konfigurasi.KEY_SAVE_KATEGORI_PENANGANAN, Penanganan);
                params.put(Konfigurasi.KEY_SAVE_KUANTITAS_PENANGANAN, Qty_Penanganan);

                //Bagian III : Metode Kendali Hama
                params.put(Konfigurasi.KEY_SAVE_CHEMICAL, Jenis_Chemical);
                params.put(Konfigurasi.KEY_SAVE_METODE_KENDALI_HAMA, metode_kendali_DB);

                params.put(Konfigurasi.KEY_SAVE_QTY_LEM_TIKUS, qty_lemTikus_DB);
                params.put(Konfigurasi.KEY_SAVE_QTY_PERANGKAP_TIKUS, qty_perangkapTikus_DB);
                params.put(Konfigurasi.KEY_SAVE_QTY_UMPAN_TIKUS_OUTDOOR, qty_umpanTikus_DB);
                params.put(Konfigurasi.KEY_SAVE_QTY_UMPAN_TIKUS_INDOOR, qty_umpanTikusIndoor_DB);
                params.put(Konfigurasi.KEY_SAVE_QTY_POHON_LALAT, qty_pohonLalat_DB);
                params.put(Konfigurasi.KEY_SAVE_QTY_BLACK_HOLE, qty_blackHole_DB);
                params.put(Konfigurasi.KEY_SAVE_JENIS_FUMIGASI, jenis_fumigasi_DB);
                params.put(Konfigurasi.KEY_SAVE_QTY_FLY_CATCHER, qty_FlyCatcher_DB);
                params.put(Konfigurasi.KEY_SAVE_METODE_LAIN, metodeLain_DB);

                //Bagian IV : Luas Indoor Outdoor
                params.put(Konfigurasi.KEY_SAVE_LUAS_INDOOR, indoor_DB);
                params.put(Konfigurasi.KEY_SAVE_LUAS_OUTDOOR, outdoor_DB);

                //Bagian V : Kontrak
                params.put(Konfigurasi.KEY_SAVE_JENIS_KONTRAK, jenis_kontrak);
                params.put(Konfigurasi.KEY_SAVE_DURASI_KONTRAK, durasi_kontrak_DB);
                params.put(Konfigurasi.KEY_SAVE_PENAWARAN_HARGA, penawaran_harga_DB);
                params.put(Konfigurasi.KEY_SAVE_TOTAL_HARGA, String.valueOf(total_harga));

                //Bagian VI : Catatan
                params.put(Konfigurasi.KEY_SAVE_STATUS_KERJASAMA, status_kerjasama_kontrak);
                params.put(Konfigurasi.KEY_SAVE_CATATAN_TAMBAHAN, catatan_tambahan);

                params.put(Konfigurasi.KEY_CEK_IMG_TTD_PELANGGAN, image_path);
                params.put(Konfigurasi.KEY_CEK_IMG_TTD_CONSULTANT, imagepath_PestConsultant);

                //untuk Pelanggan
                Log.e("tag", "ttd pelanggan");
                params.put(Konfigurasi.KEY_SAVE_IMAGE_TAG_PELANGGAN, GetImageNameFromEditText_Pelanggan);
                params.put(Konfigurasi.KEY_SAVE_IMAGE_NAME_PELANGGAN, ConvertImage_Pelanggan);
                Log.e("tagz", image_path);

                //untuk Consultant
                Log.e("tag", "ttd consultant");
                params.put(Konfigurasi.KEY_SAVE_IMAGE_TAG_CONSULTANT, GetImageNameFromEditText_Consultant);
                params.put(Konfigurasi.KEY_SAVE_IMAGE_NAME_CONSULTANT, ConvertImage_Consultant);
                Log.e("tagz", imagepath_PestConsultant);
                Log.e("tagz", "name null " + GetImageNameFromEditText_Consultant);
                Log.e("tagz", "convert null " + ConvertImage_Consultant);

                params.put(Konfigurasi.KEY_TAG_ID, TampilanMenuUtama.id_pegawai);
                params.put(Konfigurasi.PEST_KEY_GET_LIST_SURVEI_ID, id_survei);
                Log.e("tag", id_survei);

                RequestHandler rh = new RequestHandler();
                return rh.sendPostRequest(Konfigurasi.URL_UPDATE_PEST_SURVEI, params);
            }
        }

        simpan save = new simpan();
        save.execute();
    }

    private void uploadFoto() {
        if(GetImageNameFromEditText_FotoSatu != null){
            getFotoSatu = ((BitmapDrawable) UpdateSurveiPestControl.img_FotoSatu.getDrawable()).getBitmap();
            ByteArrayOutputStream stream_foto_satu = new ByteArrayOutputStream();
            getFotoSatu.compress(Bitmap.CompressFormat.PNG, 70, stream_foto_satu);
            byte[] imageInByte_foto_satu = stream_foto_satu.toByteArray();
            ConvertImage_FotoSatu = Base64.encodeToString(imageInByte_foto_satu, Base64.DEFAULT);
        }

        if(GetImageNameFromEditText_FotoDua != null){
            getFotoDua = ((BitmapDrawable) UpdateSurveiPestControl.img_FotoDua.getDrawable()).getBitmap();
            ByteArrayOutputStream stream_foto_dua = new ByteArrayOutputStream();
            getFotoDua.compress(Bitmap.CompressFormat.PNG, 70, stream_foto_dua);
            byte[] imageInByte_foto_dua = stream_foto_dua.toByteArray();
            ConvertImage_FotoDua = Base64.encodeToString(imageInByte_foto_dua, Base64.DEFAULT);
        }

        if(GetImageNameFromEditText_FotoTiga != null){
            getFotoTiga = ((BitmapDrawable) UpdateSurveiPestControl.img_FotoTiga.getDrawable()).getBitmap();
            ByteArrayOutputStream stream_foto_tiga = new ByteArrayOutputStream();
            getFotoTiga.compress(Bitmap.CompressFormat.PNG, 70, stream_foto_tiga);
            byte[] imageInByte_foto_tiga = stream_foto_tiga.toByteArray();
            ConvertImage_FotoTiga = Base64.encodeToString(imageInByte_foto_tiga, Base64.DEFAULT);
        }

        if(GetImageNameFromEditText_FotoEmpat != null){
            getFotoEmpat = ((BitmapDrawable) UpdateSurveiPestControl.img_FotoEmpat.getDrawable()).getBitmap();
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
                Toast.makeText(UpdateSurveiPestControl.this, s, Toast.LENGTH_LONG).show();
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
                return rh.sendPostRequest(Konfigurasi.URL_UPDATE_PICTURE_SURVEI_PEST, params);
            }
        }

        upload save = new upload();
        save.execute();
    }

    private void deleteSurvei() {
        @SuppressLint("StaticFieldLeak")
        class deleteSurvei extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                pDialog.setMessage("Menghapus Data Survei Terpilih, Harap Tunggu...");
                pDialog.show();
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                pDialog.dismiss();
                Toast.makeText(UpdateSurveiPestControl.this, s, Toast.LENGTH_LONG).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateSurveiPestControl.this);
                builder.setMessage(s)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent del_intent = new Intent(UpdateSurveiPestControl.this, TabSurveiPestControl.class);
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
                params.put(Konfigurasi.PEST_KEY_GET_LIST_SURVEI_ID, id_survei);

                RequestHandler rh = new RequestHandler();
                return rh.sendPostRequest(Konfigurasi.URL_DELETE_SURVEI_PEST, params);
            }
        }

        deleteSurvei Delete = new deleteSurvei();
        Delete.execute();
    }

    protected void show_Tikus() {
        chk_PerangkapTikus.setVisibility(View.VISIBLE);
        chk_LemTikus.setVisibility(View.VISIBLE);
        chk_UmpanTikusIndoor.setVisibility(View.VISIBLE);
        chk_UmpanTikusOutdoor.setVisibility(View.VISIBLE);
        chk_Fumigasi.setVisibility(View.VISIBLE);
        chk_MetodeLainnya.setVisibility(View.VISIBLE);
    }

    protected void hide_Tikus() {
        chk_PerangkapTikus.setVisibility(View.GONE);
        chk_LemTikus.setVisibility(View.GONE);
        chk_UmpanTikusIndoor.setVisibility(View.GONE);
        chk_UmpanTikusOutdoor.setVisibility(View.GONE);
        chk_Fumigasi.setVisibility(View.GONE);
        chk_MetodeLainnya.setVisibility(View.GONE);

        chk_PerangkapTikus.setChecked(false);
        chk_LemTikus.setChecked(false);
        chk_UmpanTikusIndoor.setChecked(false);
        chk_UmpanTikusOutdoor.setChecked(false);
        chk_Fumigasi.setChecked(false);
        chk_MetodeLainnya.setChecked(false);
    }

    protected void show_Kecoa() {
        chk_Penyemprotan.setVisibility(View.VISIBLE);
        chk_LemKecoa.setVisibility(View.VISIBLE);
        chk_GelKecoa.setVisibility(View.VISIBLE);
        chk_Fumigasi.setVisibility(View.VISIBLE);
        chk_MetodeLainnya.setVisibility(View.VISIBLE);
    }

    protected void hide_Kecoa() {
        chk_Penyemprotan.setVisibility(View.GONE);
        chk_LemKecoa.setVisibility(View.GONE);
        chk_GelKecoa.setVisibility(View.GONE);
        chk_Fumigasi.setVisibility(View.GONE);
        chk_MetodeLainnya.setVisibility(View.GONE);
        chk_GelKecoa.setChecked(false);
        chk_LemKecoa.setChecked(false);
        chk_MetodeLainnya.setChecked(false);
        chk_Fumigasi.setChecked(false);
    }

    protected void show_Semut() {
        chk_Penyemprotan.setVisibility(View.VISIBLE);
        chk_UmpanSemut.setVisibility(View.VISIBLE);
        chk_Fumigasi.setVisibility(View.VISIBLE);
        chk_MetodeLainnya.setVisibility(View.VISIBLE);
    }

    protected void hide_Semut() {
        chk_Penyemprotan.setVisibility(View.GONE);
        chk_UmpanSemut.setVisibility(View.GONE);
        chk_Fumigasi.setVisibility(View.GONE);
        chk_MetodeLainnya.setVisibility(View.GONE);

        chk_UmpanSemut.setChecked(false);
        chk_Fumigasi.setChecked(false);
        chk_MetodeLainnya.setChecked(false);
    }

    protected void show_Nyamuk() {
        chk_Pengembunan.setVisibility(View.VISIBLE);
        chk_PengasapanThermal.setVisibility(View.VISIBLE);
        chk_PengasapanMini.setVisibility(View.VISIBLE);
        chk_BlackHole.setVisibility(View.VISIBLE);
        chk_Fumigasi.setVisibility(View.VISIBLE);
        chk_MetodeLainnya.setVisibility(View.VISIBLE);
    }

    protected void hide_Nyamuk() {
        chk_Pengembunan.setVisibility(View.GONE);
        chk_PengasapanThermal.setVisibility(View.GONE);
        chk_PengasapanMini.setVisibility(View.GONE);
        chk_BlackHole.setVisibility(View.GONE);
        chk_Fumigasi.setVisibility(View.GONE);
        chk_MetodeLainnya.setVisibility(View.GONE);

        chk_BlackHole.setChecked(false);
        chk_Fumigasi.setChecked(false);
        chk_MetodeLainnya.setChecked(false);

    }

    protected void show_Lalat() {
        chk_Pengembunan.setVisibility(View.VISIBLE);
        chk_PengasapanThermal.setVisibility(View.VISIBLE);
        chk_PengasapanMini.setVisibility(View.VISIBLE);
        chk_FlyCatcher.setVisibility(View.VISIBLE);
        chk_PohonLalat.setVisibility(View.VISIBLE);
        chk_UmpanLalat.setVisibility(View.VISIBLE);
        chk_Fumigasi.setVisibility(View.VISIBLE);
        chk_MetodeLainnya.setVisibility(View.VISIBLE);
    }

    protected void hide_Lalat() {
        chk_Pengembunan.setVisibility(View.GONE);
        chk_PengasapanThermal.setVisibility(View.GONE);
        chk_PengasapanMini.setVisibility(View.GONE);
        chk_FlyCatcher.setVisibility(View.GONE);
        chk_PohonLalat.setVisibility(View.GONE);
        chk_UmpanLalat.setVisibility(View.GONE);
        chk_Fumigasi.setVisibility(View.GONE);
        chk_MetodeLainnya.setVisibility(View.GONE);

        chk_Fumigasi.setChecked(false);
        chk_PohonLalat.setChecked(false);
        chk_FlyCatcher.setChecked(false);
        chk_UmpanLalat.setChecked(false);
        chk_MetodeLainnya.setChecked(false);
    }

    protected void show_Kutu() {
        chk_Penyemprotan.setVisibility(View.VISIBLE);
        chk_Fumigasi.setVisibility(View.VISIBLE);
        chk_MetodeLainnya.setVisibility(View.VISIBLE);
        chk_Pengembunan.setVisibility(View.VISIBLE);
    }

    protected void hide_Kutu() {
        chk_Penyemprotan.setVisibility(View.GONE);
        chk_Fumigasi.setVisibility(View.GONE);
        chk_MetodeLainnya.setVisibility(View.GONE);
        chk_Pengembunan.setVisibility(View.GONE);

        chk_Fumigasi.setChecked(false);
        chk_MetodeLainnya.setChecked(false);
    }

    protected void show_Tawon() {
        chk_Penyemprotan.setVisibility(View.VISIBLE);
        chk_Fumigasi.setVisibility(View.VISIBLE);
        chk_MetodeLainnya.setVisibility(View.VISIBLE);
    }

    protected void hide_Tawon() {
        chk_Penyemprotan.setVisibility(View.GONE);
        chk_Fumigasi.setVisibility(View.GONE);
        chk_MetodeLainnya.setVisibility(View.GONE);

        chk_Fumigasi.setChecked(false);
        chk_MetodeLainnya.setChecked(false);
    }

    protected void show_Cicak() {
        chk_Fumigasi.setVisibility(View.VISIBLE);
        chk_MetodeLainnya.setVisibility(View.VISIBLE);
    }

    protected void hide_Cicak() {
        chk_Fumigasi.setVisibility(View.GONE);
        chk_MetodeLainnya.setVisibility(View.GONE);
        chk_Fumigasi.setChecked(false);
        chk_MetodeLainnya.setChecked(false);
    }

    protected void show_Lainnya() {
        chk_Penyemprotan.setVisibility(View.VISIBLE);
        chk_Pengembunan.setVisibility(View.VISIBLE);
        chk_PengasapanThermal.setVisibility(View.VISIBLE);
        chk_PengasapanMini.setVisibility(View.VISIBLE);
        chk_PerangkapTikus.setVisibility(View.VISIBLE);
        chk_LemTikus.setVisibility(View.VISIBLE);
        chk_UmpanTikusIndoor.setVisibility(View.VISIBLE);
        chk_UmpanTikusOutdoor.setVisibility(View.VISIBLE);
        chk_UmpanSemut.setVisibility(View.VISIBLE);
        chk_GelKecoa.setVisibility(View.VISIBLE);
        chk_LemKecoa.setVisibility(View.VISIBLE);
        chk_BlackHole.setVisibility(View.VISIBLE);
        chk_FlyCatcher.setVisibility(View.VISIBLE);
        chk_PohonLalat.setVisibility(View.VISIBLE);
        chk_UmpanLalat.setVisibility(View.VISIBLE);
        chk_Fumigasi.setVisibility(View.VISIBLE);
        chk_MetodeLainnya.setVisibility(View.VISIBLE);
    }

    /*protected void hide_Lainnya(){
        chk_Penyemprotan.setVisibility(View.GONE);          chk_Pengembunan.setVisibility(View.GONE);
        chk_PengasapanThermal.setVisibility(View.GONE);     chk_PengasapanMini.setVisibility(View.GONE);
        chk_PerangkapTikus.setVisibility(View.GONE);        chk_LemTikus.setVisibility(View.GONE);
        chk_UmpanTikusIndoor.setVisibility(View.GONE);      chk_UmpanTikusOutdoor.setVisibility(View.GONE);
        chk_UmpanSemut.setVisibility(View.GONE);            chk_GelKecoa.setVisibility(View.GONE);
        chk_LemKecoa.setVisibility(View.GONE);              chk_BlackHole.setVisibility(View.GONE);
        chk_FlyCatcher.setVisibility(View.GONE);            chk_PohonLalat.setVisibility(View.GONE);
        chk_UmpanLalat.setVisibility(View.GONE);            chk_Fumigasi.setVisibility(View.GONE);
        chk_MetodeLainnya.setVisibility(View.GONE);

        chk_Penyemprotan.setChecked(false);          chk_Pengembunan.setChecked(false);
        chk_PengasapanThermal.setChecked(false);     chk_PengasapanMini.setChecked(false);
        chk_PerangkapTikus.setChecked(false);        chk_LemTikus.setChecked(false);
        chk_UmpanTikusIndoor.setChecked(false);      chk_UmpanTikusOutdoor.setChecked(false);
        chk_UmpanSemut.setChecked(false);            chk_GelKecoa.setChecked(false);
        chk_LemKecoa.setChecked(false);              chk_BlackHole.setChecked(false);
        chk_FlyCatcher.setChecked(false);            chk_PohonLalat.setChecked(false);
        chk_UmpanLalat.setChecked(false);            chk_Fumigasi.setChecked(false);
        chk_MetodeLainnya.setChecked(false);
    }*/
}
