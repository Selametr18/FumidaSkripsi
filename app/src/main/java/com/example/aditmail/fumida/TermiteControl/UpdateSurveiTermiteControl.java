package com.example.aditmail.fumida.TermiteControl;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
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
import android.widget.TableLayout;
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

public class UpdateSurveiTermiteControl extends AppCompatActivity implements View.OnClickListener {

    //Get To Signature Activity
    private final static String path_fumida = "com.example.aditmail.fumida";
    public final static String Pesan_Extra = path_fumida + ".Pesan";

    private final String id_pegawai = TampilanMenuUtama.id_pegawai;

    //Tindakan
    private TextView txt_View, txt_Edit, txt_Delete;
    //Linear Parent
    private LinearLayout linear_Termite;

    protected ConnectivityManager conMgr;
    protected ProgressDialog pDialog;

    protected String tgl_input, id_survei;
    protected TextView txt_idSurvei, txt_tglInput;

    //--------------- Bagian I: Identitas Pelanggan ---------------
    private EditText edt_ClientID_TermiteUpdate, edt_NamaPelanggan_TermiteUpdate, edt_Alamat_TermiteUpdate,
            edt_HP_TermiteUpdate, edt_Email_TermiteUpdate;
    private Spinner spr_KategoriTempat_Termite_Update;
    private ImageView imgPinCircle;
    protected Double lat, lng;
    protected String latLong_AlamatCustomer, latLong_Consultant;
    //--------------- Bagian I: Identitas Pelanggan ---------------

    //--------------- Bagian II: Jenis Rayap dan Penanganan---------------
    private Spinner spr_JenisRayap_Update;
    private RadioGroup rdg_KategoriPenanganan_Update;
    private RadioButton rdb_PraKonstruksi_Update, rdb_PascaKonstruksi_Update;
    //--------------- Bagian II: Jenis Rayap dan Penanganan---------------

    //--------------- Bagian III: Metode Pengendalian Rayap dan Chemical---------------
    private CheckBox chk_Spraying_Update, chk_Drill_Update, chk_BaitingAG_Update, chk_BaitingIG_Update, chk_Fumigasi_Update;
    private CheckBox chk_Fipronil_Update, chk_Imidaclporid_Update, chk_Cypermethrin_Update, chk_Dichlorphos_Update,
            chk_ChemicalBaitingAG_Update, chk_ChemicalBaitingIG_Update;
    private EditText edt_QtyFipronil_Update, edt_QtyImidaclporid_Update, edt_QtyCypermethrin_Update,
            edt_QtyDichlorphos_Update, edt_QtyChemicalBaitingAG_Update, edt_QtyChemicalBaitingIG_Update;
    //--------------- Bagian III: Metode Pengendalian Rayap dan Chemical---------------

    //--------------- Bagian IV: Area Lokasi dan Lantai---------------
    private EditText edt_LuasBangunan, edt_LuasOutdoor;

    private TableLayout tbl_LantaiBangunan;
    private CheckBox chk_Keramik, chk_Granito, chk_Marmer, chk_Granit, chk_Teraso, chk_SolidParquet,
            chk_VinylParquet, chk_LaminatedParquet, chk_LantaiBeton;

    //Untuk Keramik
    private TableLayout tbl_Keramik;
    private CheckBox chk_Keramik30, chk_Keramik40, chk_Keramik60, chk_KeramikLainnya;
    private EditText edt_KeramikLainnya;

    //Untuk Granito
    private TableLayout tbl_Granito;
    private CheckBox chk_Granito30, chk_Granito40, chk_Granito60, chk_GranitoLainnya;
    private EditText edt_GranitoLainnya;

    //Untuk Marmer
    private TableLayout tbl_Marmer;
    private CheckBox chk_Marmer30, chk_Marmer40, chk_Marmer60, chk_MarmerLainnya;
    private EditText edt_MarmerLainnya;

    //Untuk Granit
    private TableLayout tbl_Granit;
    private CheckBox chk_Granit30, chk_Granit40, chk_Granit60, chk_GranitLainnya;
    private EditText edt_GranitLainnya;

    //Untuk Teraso
    private TableLayout tbl_Teraso;
    private CheckBox chk_Teraso30, chk_Teraso40, chk_Teraso60, chk_TerasoLainnya;
    private EditText edt_TerasoLainnya;
    //--------------- Bagian IV: Area Lokasi dan Lantai---------------

    //--------------- Bagian V: Kontrak, Kerjasama dan Catatan ---------------
    private EditText edt_PenawaranHarga, edt_GrandTotal_Termite;
    private RadioGroup rdg_StatusKerjaSama;
    private RadioButton rdb_Berhasil, rdb_Menunggu, rdb_Gagal;

    //Bagian Catatan Tambahan
    private EditText edt_CatatanTambahan;

    private String penanganan = "";
    private String status_kerjasama = "";
    //--------------- Bagian V: Kontrak, Kerjasama dan Catatan ---------------

    //--------------- Bagian VI: Tanda Tangan ---------------
    private Button btn_DialogTTD_Pelanggan, btn_DialogTTD_Consultant;

    @SuppressLint("StaticFieldLeak")
    public static ImageView img_ttdPelanggan, img_ttdConsultant;

    private CheckBox chk_PernyataanPelanggan, chk_PernyataanConsultant;
    protected String dateNow;

    private String image_path = "null";
    private String imagepath_TermiteConsultant = "null";

    //Untuk upload image ttd
    protected Bitmap bitmap_TermiteConsultant, bitmap_TermitePelanggan;
    protected Bitmap getTTD_Consultant, getTTD_Pelanggan;
    //--------------- Bagian VI: Tanda Tangan ---------------

    //Button Simpan
    Button btn_UpdateSurvey;

    //--------------- Bagian VII: Upload Image---------------
    private String ConvertImage_Pelanggan = "", ConvertImage_Consultant = "";
    private String GetImageNameFromEditText_Pelanggan = "", GetImageNameFromEditText_Consultant = "";

    //Untuk Upload Image Foto...
    protected Bitmap getFotoSatu, getFotoDua, getFotoTiga, getFotoEmpat;
    private String ConvertImage_FotoSatu = "", ConvertImage_FotoDua = "", ConvertImage_FotoTiga = "", ConvertImage_FotoEmpat = "";
    private String GetImageNameFromEditText_FotoSatu = "", GetImageNameFromEditText_FotoDua = "", GetImageNameFromEditText_FotoTiga = "",
            GetImageNameFromEditText_FotoEmpat = "";

    private Button btn_PilihFotoSatu, btn_PilihFotoDua, btn_PilihFotoTiga, btn_PilihFotoEmpat;

    @SuppressLint("StaticFieldLeak")
    public static ImageView img_FotoSatu, img_FotoDua, img_FotoTiga, img_FotoEmpat;

    private Space spc_Satu, spc_Dua, spc_Tiga;
    //--------------- Bagian VII: Upload Image---------------

    //Send data to DB ------------------------------------------------------------------>
    private String IDClient, namaPelanggan, kategoriTempatPelanggan, alamatPelanggan, hpPelanggan, email,
            jenis_rayap, kategori_penanganan, metode_kendali, total_chemical_DB, qty_fipronil_DB,
            qty_imidaclporid_DB, qty_cypermethrin_DB, qty_dichlorphos_DB, qty_baitingAG_DB, qty_baitingIG_DB,
            luas_indoor, luas_outdoor, lantai_bangunan_final, ukuran_keramik_final, ukuran_keramik_lainnya_final,
            ukuran_granito_final, ukuran_granito_lainnya_final, ukuran_marmer_final, ukuran_marmer_lainnya_final,
            ukuran_teraso_final, ukuran_teraso_lainnya_final, ukuran_granit_final, ukuran_granit_lainnya_final,
            penawaran_harga, total_harga_penawaran, status, catatan_tambahan;
    //Send data to DB ------------------------------------------------------------------>

    //Get data from DB--------------------------------------------->
    private String ClientID, NamaPelanggan, KategoriTempat, Alamat, NoHP, Email, JenisRayap, KategoriPenanganan,
            MetodeKendaliRayap, JenisChemical, QtyFipronil, QtyImidaclporid, QtyCypermethrin, QtyDichlorphos, QtyBaitingAG, QtyBaitingIG,
            LuasIndoor, LuasOutdoor, JenisLantaiBangunan, QtyKeramik, KeramikLain, QtyGranito, GranitoLain,
            QtyMarmer, MarmerLain, QtyTeraso, TerasoLain, QtyGranit, GranitLain, PenawaranHarga, TotalHarga,
            StatusKerjasama, CatatanTambahan, GetImgPelanggan_TTD, GetImgConsultant_TTD,
            GetTimeStamp, pathFotoSatu, pathFotoDua, pathFotoTiga, pathFotoEmpat;
    //Get data from DB--------------------------------------------->

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
        setContentView(R.layout.activity_update_survei_termite_control);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        pDialog = new ProgressDialog(this);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(true);

        InitData();

        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
            getDataSurveiTermite();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection",
                    Toast.LENGTH_LONG).show();
        }

        //Get Data
        Intent intent = getIntent();
        tgl_input = intent.getStringExtra(Konfigurasi.KEY_TANGGAL_INPUT);
        id_survei = intent.getStringExtra(Konfigurasi.KEY_SURVEI_ID);

        //Set Data
        txt_idSurvei.setText(id_survei);
        txt_tglInput.setText(tgl_input);

        //Edit Button
        EditData();

        //Call Function
        TextWatcher();
        JenisChemical();
        JenisLantaiBangunan();
        CekRadioGroup();
        CekTandaTangan();
    }

    @Override
    protected void onStart() {
        OpenPhoto();
        OpenMaps();
        super.onStart();
    }

    private void InitData() {
        txt_idSurvei = findViewById(R.id.textView_idSurvei_Termite);
        txt_tglInput = findViewById(R.id.textView_Tgl_Input);

        //Tindakan
        txt_Edit = findViewById(R.id.textView_Update_Termite);
        txt_Delete = findViewById(R.id.textView_Delete_Termite);
        txt_View = findViewById(R.id.textView_View_Termite);

        linear_Termite = findViewById(R.id.LinearLayout_UpdateTermite);

        edt_ClientID_TermiteUpdate = findViewById(R.id.editText_ClientID_Termite_Update);
        edt_NamaPelanggan_TermiteUpdate = findViewById(R.id.editText_NamaPelanggan_Termite_Update);
        edt_Alamat_TermiteUpdate = findViewById(R.id.editText_Alamat_Termite_Update);
        edt_HP_TermiteUpdate = findViewById(R.id.editText_NoHP_Termite_Update);
        edt_Email_TermiteUpdate = findViewById(R.id.editText_Email_Termite_Update);

        imgPinCircle = findViewById(R.id.imgPin);
        spr_KategoriTempat_Termite_Update = findViewById(R.id.spinner_Kategori_Termite_Update);

        spr_JenisRayap_Update = findViewById(R.id.spinner_JenisRayap_Update);

        rdg_KategoriPenanganan_Update = findViewById(R.id.RadioGroup_PenangananRayap);
        rdb_PraKonstruksi_Update = findViewById(R.id.radioButton_PraKonstruksi_Update);
        rdb_PascaKonstruksi_Update = findViewById(R.id.radioButton_PascaKonstruksi_Update);

        chk_Spraying_Update = findViewById(R.id.checkBox_Spraying_Update);
        chk_Drill_Update = findViewById(R.id.checkBox_InjectDrill_Update);
        chk_BaitingAG_Update = findViewById(R.id.checkBox_BaitingAG_Update);
        chk_BaitingIG_Update = findViewById(R.id.checkBox_BaitingIG_Update);
        chk_Fumigasi_Update = findViewById(R.id.checkBox_Fumigasi_Update);

        //Bagian Chemical yg Digunakan
        chk_Fipronil_Update = findViewById(R.id.checkBox_Fipronil_Update);
        chk_Imidaclporid_Update = findViewById(R.id.checkBox_Imidaclporid_Update);
        chk_Cypermethrin_Update = findViewById(R.id.checkBox_Cypermethrin_Update);
        chk_Dichlorphos_Update = findViewById(R.id.checkBox_Dichlorphos_Update);
        chk_ChemicalBaitingAG_Update = findViewById(R.id.checkBox_BaitingAG_Chemical_Update);
        chk_ChemicalBaitingIG_Update = findViewById(R.id.checkBox_BaitingIG_Chemical_Update);

        edt_QtyFipronil_Update = findViewById(R.id.editText_QtyFipronil_Update);
        edt_QtyImidaclporid_Update = findViewById(R.id.editText_QtyImidaclporid_Update);
        edt_QtyCypermethrin_Update = findViewById(R.id.editText_QtyCypermethrin_Update);
        edt_QtyDichlorphos_Update = findViewById(R.id.editText_QtyDichlorphos_Update);
        edt_QtyChemicalBaitingAG_Update = findViewById(R.id.editText_QtyBaitingAG_Update);
        edt_QtyChemicalBaitingIG_Update = findViewById(R.id.editText_QtyBaitingIG_Update);

        //Bagian Area Lokasi
        edt_LuasBangunan = findViewById(R.id.editText_LuasBangunan_Termite_Update);
        edt_LuasOutdoor = findViewById(R.id.editText_LuasOutdoor_Termite_Update);

        //Bagian Lantai Bangunan
        tbl_LantaiBangunan = findViewById(R.id.TableLayout_LantaiBangunan);
        chk_Keramik = findViewById(R.id.checkBox_Keramik_Update);
        chk_Granito = findViewById(R.id.checkBox_Granito_Update);
        chk_Marmer = findViewById(R.id.checkBox_Marmer_Update);
        chk_Granit = findViewById(R.id.checkBox_Granit_Update);
        chk_Teraso = findViewById(R.id.checkBox_Teraso_Update);
        chk_SolidParquet = findViewById(R.id.checkBox_SolidParquet_Update);
        chk_VinylParquet = findViewById(R.id.checkBox_VinylParquet_Update);
        chk_LaminatedParquet = findViewById(R.id.checkBox_LaminatedParquet_Update);
        chk_LantaiBeton = findViewById(R.id.checkBox_LantaiBeton_Update);

        //Untuk Keramik
        tbl_Keramik = findViewById(R.id.TableLayout_Keramik);
        chk_Keramik30 = findViewById(R.id.checkBox_Keramik30_Update);
        chk_Keramik40 = findViewById(R.id.checkBox_Keramik40_Update);
        chk_Keramik60 = findViewById(R.id.checkBox_Keramik60_Update);
        chk_KeramikLainnya = findViewById(R.id.checkBox_KeramikLainnya_Update);
        edt_KeramikLainnya = findViewById(R.id.editText_KeramikLainnya_Update);

        //Untuk Granito
        tbl_Granito = findViewById(R.id.TableLayout_Granito);
        chk_Granito30 = findViewById(R.id.checkBox_Granito30_Update);
        chk_Granito40 = findViewById(R.id.checkBox_Granito40_Update);
        chk_Granito60 = findViewById(R.id.checkBox_Granito60_Update);
        chk_GranitoLainnya = findViewById(R.id.checkBox_GranitoLainnya_Update);
        edt_GranitoLainnya = findViewById(R.id.EditText_GranitoLainnya_Update);

        //Untuk Marmer
        tbl_Marmer = findViewById(R.id.TableLayout_Marmer);
        chk_Marmer30 = findViewById(R.id.checkBox_Marmer30_Update);
        chk_Marmer40 = findViewById(R.id.checkBox_Marmer40_Update);
        chk_Marmer60 = findViewById(R.id.checkBox_Marmer60_Update);
        chk_MarmerLainnya = findViewById(R.id.checkBox_MarmerLainnya_Update);
        edt_MarmerLainnya = findViewById(R.id.EditText_MarmerLainnya_Update);

        //Untuk Granit
        tbl_Granit = findViewById(R.id.TableLayout_Granit);
        chk_Granit30 = findViewById(R.id.checkBox_Granit30_Update);
        chk_Granit40 = findViewById(R.id.checkBox_Granit40_Update);
        chk_Granit60 = findViewById(R.id.checkBox_Granit60_Update);
        chk_GranitLainnya = findViewById(R.id.checkBox_GranitLainnya_Update);
        edt_GranitLainnya = findViewById(R.id.EditText_GranitLainnya_Update);

        //Untuk Teraso
        tbl_Teraso = findViewById(R.id.TableLayout_Teraso);
        chk_Teraso30 = findViewById(R.id.checkBox_Teraso30_Update);
        chk_Teraso40 = findViewById(R.id.checkBox_Teraso40_Update);
        chk_Teraso60 = findViewById(R.id.checkBox_Teraso60_Update);
        chk_TerasoLainnya = findViewById(R.id.checkBox_TerasoLainnya_Update);
        edt_TerasoLainnya = findViewById(R.id.EditText_TerasoLainnya_Update);

        edt_PenawaranHarga = findViewById(R.id.editText_PenawaranHarga_Termite_Update);
        edt_GrandTotal_Termite = findViewById(R.id.editText_GrandTotal_Termite_Update);
        rdg_StatusKerjaSama = findViewById(R.id.RadioGroup_Status);
        rdb_Berhasil = findViewById(R.id.radioButton_Berhasil_Update);
        rdb_Menunggu = findViewById(R.id.radioButton_Menunggu_Update);
        rdb_Gagal = findViewById(R.id.radioButton_Gagal_Update);

        edt_CatatanTambahan = findViewById(R.id.editText_CatatanTambahan_Termite_Update);

        //Tanda Tangan
        btn_DialogTTD_Pelanggan = findViewById(R.id.button_GetTTD_Pelanggan_Termite_Update);
        btn_DialogTTD_Consultant = findViewById(R.id.button_GetTTD_Consultant_Termite_Update);

        chk_PernyataanPelanggan = findViewById(R.id.checkBox_PernyataanCustomer_Termite_Update);
        chk_PernyataanConsultant = findViewById(R.id.checkBox_PernyataanPestConsultant_Termite_Update);

        img_ttdPelanggan = findViewById(R.id.imageView_TTD_Pelanggan_Termite_Update);
        img_ttdConsultant = findViewById(R.id.imageView_TTD_Consultant_Termite_Update);

        //Button Foto
        btn_PilihFotoSatu = findViewById(R.id.button_FotoSatu_UpdateTermite);
        img_FotoSatu = findViewById(R.id.imageView_FotoSatu_UpdateTermite);
        btn_PilihFotoDua = findViewById(R.id.button_FotoDua_UpdateTermite);
        img_FotoDua = findViewById(R.id.imageView_FotoDua_UpdateTermite);
        btn_PilihFotoTiga = findViewById(R.id.button_FotoTiga_UpdateTermite);
        img_FotoTiga = findViewById(R.id.imageView_FotoTiga_UpdateTermite);
        btn_PilihFotoEmpat = findViewById(R.id.button_FotoEmpat_UpdateTermite);
        img_FotoEmpat = findViewById(R.id.imageView_FotoEmpat_UpdateTermite);

        spc_Satu = findViewById(R.id.Space_Satu);
        spc_Dua = findViewById(R.id.Space_Dua);
        spc_Tiga = findViewById(R.id.Space_Tiga);

        btn_UpdateSurvey = findViewById(R.id.button_SimpanSurvey_Termite_Update);
        btn_UpdateSurvey.setOnClickListener(this);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
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
        setViewAndChildrenEnabled(linear_Termite, false);

        txt_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setViewAndChildrenEnabled(linear_Termite, true);
                btn_DialogTTD_Consultant.setEnabled(false);
                btn_DialogTTD_Pelanggan.setEnabled(false);
            }
        });

        txt_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UpdateSurveiTermiteControl.this);

                    builder.setCancelable(true);
                    builder.setTitle("Apakah Anda Yakin Menghapus Survei Termite Control Tersebut?");
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
                Intent a = new Intent(UpdateSurveiTermiteControl.this, PreviewSurveiTermiteControl.class);
                a.putExtra(Konfigurasi.KEY_TAG_ID, id_pegawai);
                a.putExtra(Konfigurasi.KEY_SURVEI_ID, id_survei);
                startActivity(a);
            }
        });
    }

    private void getDataSurveiTermite() {
        @SuppressLint("StaticFieldLeak")
        class GetDataSurveiTermite extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                pDialog.setMessage("Mengambil Data, Harap Menunggu...");
                pDialog.show();
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                pDialog.dismiss();
                showDataSurveiTermite(s);
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();
                params.put(Konfigurasi.KEY_TAG_ID, id_pegawai);
                params.put(Konfigurasi.KEY_SURVEI_ID, id_survei);
                RequestHandler rh = new RequestHandler();
                return rh.sendPostRequest(Konfigurasi.URL_VIEW_SELECTED_TERMITE, params);
            }
        }
        GetDataSurveiTermite ge = new GetDataSurveiTermite();
        ge.execute();
    }

    private void showDataSurveiTermite(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray(Konfigurasi.TAG_JSON_ARRAY);
            JSONObject c = result.getJSONObject(0);

            ClientID = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_CLIENT_ID);
            NamaPelanggan = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_NAMA_PELANGGAN);
            KategoriTempat = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_KATEGORI_TEMPAT);
            Alamat = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_ALAMAT);
            NoHP = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_HP);
            Email = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_EMAIL);

            latLong_AlamatCustomer = c.getString(Konfigurasi.TERMITE_KEY_GET_LATLONG_ALAMAT_PELANGGAN);
            latLong_Consultant = c.getString(Konfigurasi.TERMITE_KEY_GET_LATLONG_CONSULTANT);

            JenisRayap = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_JENIS_RAYAP);
            KategoriPenanganan = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_KATEGORI_PENANGANAN);
            MetodeKendaliRayap = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_METODE_KENDALI_RAYAP);
            JenisChemical = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_JENIS_CHEMICAL);
            QtyFipronil = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_QTY_FIPRONIL);
            QtyImidaclporid = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_QTY_IMIDACLPORID);
            QtyCypermethrin = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_QTY_CYPERMETHRIN);
            QtyDichlorphos = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_QTY_DICHLORPHOS);
            QtyBaitingAG = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_QTY_BAITING_AG);
            QtyBaitingIG = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_QTY_BAITING_IG);
            LuasIndoor = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_LUAS_INDOOR);
            LuasOutdoor = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_LUAS_OUTDOOR);
            JenisLantaiBangunan = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_LANTAI_BANGUNAN);

            QtyKeramik = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_QTY_KERAMIK);
            KeramikLain = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_KERAMIK_LAIN);

            QtyGranito = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_QTY_GRANITO);
            GranitoLain = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_GRANITO_LAIN);

            QtyMarmer = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_QTY_MARMER);
            MarmerLain = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_MARMER_LAIN);

            QtyTeraso = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_QTY_TERASO);
            TerasoLain = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_TERASO_LAIN);

            QtyGranit = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_QTY_GRANIT);
            GranitLain = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_GRANIT_LAIN);

            PenawaranHarga = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_PENAWARAN_HARGA);
            TotalHarga = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_TOTAL_HARGA);
            StatusKerjasama = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_STATUS_KERJASAMA);
            CatatanTambahan = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_CATATAN);
            GetImgPelanggan_TTD = c.getString(Konfigurasi.TERMITE_KEY_GET_IMG_PELANGGAN);
            GetImgConsultant_TTD = c.getString(Konfigurasi.TERMITE_KEY_GET_IMG_CONSULTANT);

            //Bagian VI : Foto
            pathFotoSatu = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_IMG_FOTO_SATU);
            pathFotoDua = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_IMG_FOTO_DUA);
            pathFotoTiga = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_FOTO_TIGA);
            pathFotoEmpat = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_FOTO_EMPAT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        showData();
    }

    private void showData(){

            edt_ClientID_TermiteUpdate.setText(ClientID);
            edt_NamaPelanggan_TermiteUpdate.setText(NamaPelanggan);
            edt_Alamat_TermiteUpdate.setText(Alamat);
            edt_HP_TermiteUpdate.setText(NoHP);
            edt_Email_TermiteUpdate.setText(Email);
            edt_QtyFipronil_Update.setText(QtyFipronil);
            edt_QtyImidaclporid_Update.setText(QtyImidaclporid);
            edt_QtyCypermethrin_Update.setText(QtyCypermethrin);
            edt_QtyDichlorphos_Update.setText(QtyDichlorphos);
            edt_QtyChemicalBaitingAG_Update.setText(QtyBaitingAG);
            edt_QtyChemicalBaitingIG_Update.setText(QtyBaitingIG);
            edt_LuasBangunan.setText(LuasIndoor);
            edt_LuasOutdoor.setText(LuasOutdoor);
            edt_PenawaranHarga.setText(PenawaranHarga);
            edt_GrandTotal_Termite.setText(TotalHarga);
            edt_CatatanTambahan.setText(CatatanTambahan);

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
                    .error(Glide.with(img_ttdConsultant).load(R.drawable.ic_ttd_not_found))
                    .into(img_ttdPelanggan);

            Glide.with(getApplicationContext())
                    .load(Konfigurasi.url_image + GetImgConsultant_TTD)
                    .error(Glide.with(img_ttdConsultant).load(R.drawable.ic_ttd_not_found))
                    .into(img_ttdConsultant);

            //Spinner Kategori Tempat Client
            if (KategoriTempat.equalsIgnoreCase("Kantor")) {
                spr_KategoriTempat_Termite_Update.setSelection(0);
            } else if (KategoriTempat.equalsIgnoreCase("Pabrik Makanan")) {
                spr_KategoriTempat_Termite_Update.setSelection(1);
            } else if (KategoriTempat.equalsIgnoreCase("Pabrik Non Makanan")) {
                spr_KategoriTempat_Termite_Update.setSelection(2);
            } else if (KategoriTempat.equalsIgnoreCase("Restoran")) {
                spr_KategoriTempat_Termite_Update.setSelection(3);
            } else {
                spr_KategoriTempat_Termite_Update.setSelection(4);
            }

            //Spinner Jenis Rayap
            if (JenisRayap.equalsIgnoreCase("Cryptotermes")) {
                spr_JenisRayap_Update.setSelection(0);
            } else if (JenisRayap.equalsIgnoreCase("Glyptotermes")) {
                spr_JenisRayap_Update.setSelection(1);
            } else if (JenisRayap.equalsIgnoreCase("Coptotermes")) {
                spr_JenisRayap_Update.setSelection(2);
            } else if (JenisRayap.equalsIgnoreCase("Schedorhinotermes")) {
                spr_JenisRayap_Update.setSelection(3);
            } else if (JenisRayap.equalsIgnoreCase("Odontotermes")) {
                spr_JenisRayap_Update.setSelection(4);
            } else {
                spr_JenisRayap_Update.setSelection(5);
            }

            //Radio Button Kategori Penanganan
            if (KategoriPenanganan.equalsIgnoreCase("Anti Rayap Pasca-Konstruksi GARANSI 3 TAHUN")) {
                rdb_PascaKonstruksi_Update.setChecked(true);
            } else if (KategoriPenanganan.equalsIgnoreCase("Anti Rayap Pra-Konstruksi GARANSI 5 TAHUN")) {
                rdb_PraKonstruksi_Update.setChecked(true);
            }

            //CheckBox Metode Kendali Rayap
            String[] JenisMetodeKendali_Arry = MetodeKendaliRayap.split(",");
            for (String list_metode : JenisMetodeKendali_Arry) {
                if (list_metode.equalsIgnoreCase("Spraying")) {
                    chk_Spraying_Update.setChecked(true);
                }
                if (list_metode.equalsIgnoreCase("Drill Inject")) {
                    chk_Drill_Update.setChecked(true);
                }
                if (list_metode.equalsIgnoreCase("Termite Baiting AG")) {
                    chk_BaitingAG_Update.setChecked(true);
                }
                if (list_metode.equalsIgnoreCase("Termite Baiting IG")) {
                    chk_BaitingIG_Update.setChecked(true);
                }
                if (list_metode.equalsIgnoreCase("Fumigasi")) {
                    chk_Fumigasi_Update.setChecked(true);
                }
            }

            //CheckBox Jenis Chemical
            String[] JenisChemical_Arry = JenisChemical.split(",");
            for (String list_chemical : JenisChemical_Arry) {
                if (list_chemical.equalsIgnoreCase("Fipronil 50 g/l")) {
                    chk_Fipronil_Update.setChecked(true);
                }
                if (list_chemical.equalsIgnoreCase("Imidacloprid 200 g/l")) {
                    chk_Imidaclporid_Update.setChecked(true);
                }
                if (list_chemical.equalsIgnoreCase("Cypermethrin 100 g/l")) {
                    chk_Cypermethrin_Update.setChecked(true);
                }
                if (list_chemical.equalsIgnoreCase("Dichlorphos 200 g/l")) {
                    chk_Dichlorphos_Update.setChecked(true);
                }
                if (list_chemical.equalsIgnoreCase("Baiting AG")) {
                    chk_ChemicalBaitingAG_Update.setChecked(true);
                }
                if (list_chemical.equalsIgnoreCase("Baiting IG")) {
                    chk_ChemicalBaitingIG_Update.setChecked(true);
                }
            }

            //CheckBox Lantai Bangunan
            String[] JenisLantai_Arry = JenisLantaiBangunan.split(",");
            for (String list_lantai : JenisLantai_Arry) {
                if (list_lantai.equalsIgnoreCase("Keramik")) {
                    chk_Keramik.setChecked(true);
                }
                if (list_lantai.equalsIgnoreCase("Granit Tile")) {
                    chk_Granito.setChecked(true);
                }
                if (list_lantai.equalsIgnoreCase("Marmer")) {
                    chk_Marmer.setChecked(true);
                }
                if (list_lantai.equalsIgnoreCase("Teraso")) {
                    chk_Teraso.setChecked(true);
                }
                if (list_lantai.equalsIgnoreCase("Granit")) {
                    chk_Granit.setChecked(true);
                }
                if (list_lantai.equalsIgnoreCase("Lantai Beton")) {
                    chk_LantaiBeton.setChecked(true);
                }
                if (list_lantai.equalsIgnoreCase("Solid Parquet")) {
                    chk_SolidParquet.setChecked(true);
                }
                if (list_lantai.equalsIgnoreCase("Vinyl Parquet")) {
                    chk_VinylParquet.setChecked(true);
                }
                if (list_lantai.equalsIgnoreCase("Laminated Parquet")) {
                    chk_LaminatedParquet.setChecked(true);
                }
            }

            //CheckBox Keramik
            String[] QtyKeramik_Arry = QtyKeramik.split(",");
            for (String list_keramik : QtyKeramik_Arry) {
                if (list_keramik.equalsIgnoreCase("30x30")) {
                    chk_Keramik30.setChecked(true);
                }
                if (list_keramik.equalsIgnoreCase("40x40")) {
                    chk_Keramik40.setChecked(true);
                }
                if (list_keramik.equalsIgnoreCase("60x60")) {
                    chk_Keramik60.setChecked(true);
                }
            }

            if (!KeramikLain.isEmpty()) {
                chk_KeramikLainnya.setChecked(true);
                edt_KeramikLainnya.setText(KeramikLain);
            }

            //CheckBox Granito
            String[] QtyGranito_Arry = QtyGranito.split(",");
            for (String list_granito : QtyGranito_Arry) {
                if (list_granito.equalsIgnoreCase("30x30")) {
                    chk_Granito30.setChecked(true);
                }
                if (list_granito.equalsIgnoreCase("40x40")) {
                    chk_Granito40.setChecked(true);
                }
                if (list_granito.equalsIgnoreCase("60x60")) {
                    chk_Granito60.setChecked(true);
                }
            }

            if (!GranitoLain.isEmpty()) {
                chk_GranitoLainnya.setChecked(true);
                edt_GranitoLainnya.setText(GranitoLain);
            }

            //CheckBox Marmer
            String[] QtyMarmer_Arry = QtyMarmer.split(",");
            for (String list_marmer : QtyMarmer_Arry) {
                if (list_marmer.equalsIgnoreCase("30x30")) {
                    chk_Marmer30.setChecked(true);
                }
                if (list_marmer.equalsIgnoreCase("40x40")) {
                    chk_Marmer40.setChecked(true);
                }
                if (list_marmer.equalsIgnoreCase("60x60")) {
                    chk_Marmer60.setChecked(true);
                }
            }

            if (!MarmerLain.isEmpty()) {
                chk_MarmerLainnya.setChecked(true);
                edt_MarmerLainnya.setText(MarmerLain);
            }

            //CheckBox Teraso
            String[] QtyTeraso_Arry = QtyTeraso.split(",");
            for (String list_teraso : QtyTeraso_Arry) {
                if (list_teraso.equalsIgnoreCase("30x30")) {
                    chk_Teraso30.setChecked(true);
                }
                if (list_teraso.equalsIgnoreCase("40x40")) {
                    chk_Teraso40.setChecked(true);
                }
                if (list_teraso.equalsIgnoreCase("60x60")) {
                    chk_Teraso60.setChecked(true);
                }
            }

            if (!TerasoLain.isEmpty()) {
                chk_TerasoLainnya.setChecked(true);
                edt_TerasoLainnya.setText(TerasoLain);
            }

            //CheckBox Granit
            String[] QtyGranit_Arry = QtyGranit.split(",");
            for (String list_granit : QtyGranit_Arry) {
                if (list_granit.equalsIgnoreCase("30x30")) {
                    chk_Granit30.setChecked(true);
                }
                if (list_granit.equalsIgnoreCase("40x40")) {
                    chk_Granit40.setChecked(true);
                }
                if (list_granit.equalsIgnoreCase("60x60")) {
                    chk_Granit60.setChecked(true);
                }
            }

            if (!GranitLain.isEmpty()) {
                chk_GranitLainnya.setChecked(true);
                edt_GranitLainnya.setText(GranitLain);
            }

            //Radio Button Status Kerjasama
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
            Intent openMaps = new Intent(UpdateSurveiTermiteControl.this, OpenMaps.class);
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
        double total, a;

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        if (!edt_PenawaranHarga.getText().toString().equals("") && edt_PenawaranHarga.getText().length() > 0) {
            String penawaran = edt_PenawaranHarga.getText().toString();
            a = Double.parseDouble(penawaran);
            formatRupiah.format(a);
        } else {
            a = 0;
        }

        total = a;

        return (formatRupiah.format(total));
    }

    private void TextWatcher() {
        edt_PenawaranHarga.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edt_GrandTotal_Termite.setText(kalkulasi_total());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edt_HP_TermiteUpdate.addTextChangedListener(new TextWatcher() {

            int keyDel;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edt_HP_TermiteUpdate.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                        if (keyCode == KeyEvent.KEYCODE_DEL) {
                            keyDel = 1;
                        }
                        return false;
                    }
                });

                String currentString = edt_HP_TermiteUpdate.getText().toString();
                int currentLength = edt_HP_TermiteUpdate.getText().length();

                if (currentLength == 5 || currentLength == 10) {
                    keyDel = 1;
                }

                if (keyDel == 0) {
                    if (currentLength == 4 || currentLength == 9) {
                        edt_HP_TermiteUpdate.setText(edt_HP_TermiteUpdate.getText() + "-");
                        edt_HP_TermiteUpdate.setSelection(edt_HP_TermiteUpdate.getText().length());
                    }
                } else {
                    if (currentLength != 5 && currentLength != 10) {
                        keyDel = 0;
                    } else if ((currentLength == 5 || currentLength == 10)
                            && !"-".equals(currentString.substring(currentLength - 1, currentLength))) {
                        edt_HP_TermiteUpdate.setText(currentString.substring(0, currentLength - 1) + "-"
                                + currentString.substring(currentLength - 1, currentLength));
                        edt_HP_TermiteUpdate.setSelection(edt_HP_TermiteUpdate.getText().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

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
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(UpdateSurveiTermiteControl.this);
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

    private void showPictureDialog_Dua() {
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(UpdateSurveiTermiteControl.this);
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
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(UpdateSurveiTermiteControl.this);
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
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(UpdateSurveiTermiteControl.this);
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

    private void JenisChemical() {
        chk_Fipronil_Update.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_QtyFipronil_Update.setVisibility(View.VISIBLE);
                    edt_QtyFipronil_Update.requestFocus();
                } else {
                    edt_QtyFipronil_Update.setVisibility(View.GONE);
                    edt_QtyFipronil_Update.setText("");
                }
            }
        });

        chk_Imidaclporid_Update.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_QtyImidaclporid_Update.setVisibility(View.VISIBLE);
                    edt_QtyImidaclporid_Update.requestFocus();
                } else {
                    edt_QtyImidaclporid_Update.setVisibility(View.GONE);
                    edt_QtyImidaclporid_Update.setText("");
                }
            }
        });

        chk_Cypermethrin_Update.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_QtyCypermethrin_Update.setVisibility(View.VISIBLE);
                    edt_QtyCypermethrin_Update.requestFocus();
                } else {
                    edt_QtyCypermethrin_Update.setVisibility(View.GONE);
                    edt_QtyCypermethrin_Update.setText("");
                }
            }
        });

        chk_Dichlorphos_Update.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_QtyDichlorphos_Update.setVisibility(View.VISIBLE);
                    edt_QtyDichlorphos_Update.requestFocus();
                } else {
                    edt_QtyDichlorphos_Update.setVisibility(View.GONE);
                    edt_QtyDichlorphos_Update.setText("");
                }
            }
        });

        chk_ChemicalBaitingAG_Update.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_QtyChemicalBaitingAG_Update.setVisibility(View.VISIBLE);
                    edt_QtyChemicalBaitingAG_Update.requestFocus();
                } else {
                    edt_QtyChemicalBaitingAG_Update.setVisibility(View.GONE);
                    edt_QtyChemicalBaitingAG_Update.setText("");
                }
            }
        });

        chk_ChemicalBaitingIG_Update.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_QtyChemicalBaitingIG_Update.setVisibility(View.VISIBLE);
                    edt_QtyChemicalBaitingIG_Update.requestFocus();
                } else {
                    edt_QtyChemicalBaitingIG_Update.setVisibility(View.GONE);
                    edt_QtyChemicalBaitingIG_Update.setText("");
                }
            }
        });
    }

    private void JenisLantaiBangunan() {
        chk_Keramik.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tbl_Keramik.setVisibility(View.VISIBLE);
                } else {
                    tbl_Keramik.setVisibility(View.GONE);
                    chk_Keramik30.setChecked(false);
                    chk_Keramik40.setChecked(false);
                    chk_Keramik60.setChecked(false);
                    chk_Keramik60.setChecked(false);
                    chk_KeramikLainnya.setChecked(false);
                }
            }
        });

        chk_KeramikLainnya.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_KeramikLainnya.setVisibility(View.VISIBLE);
                    edt_KeramikLainnya.requestFocus();
                } else {
                    edt_KeramikLainnya.setVisibility(View.GONE);
                    edt_KeramikLainnya.setText("");
                }
            }
        });

        chk_Granito.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tbl_Granito.setVisibility(View.VISIBLE);
                } else {
                    tbl_Granito.setVisibility(View.GONE);
                    chk_Granito30.setChecked(false);
                    chk_Granito40.setChecked(false);
                    chk_Granito60.setChecked(false);
                    chk_Granito60.setChecked(false);
                    chk_GranitoLainnya.setChecked(false);
                }
            }
        });

        chk_GranitoLainnya.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_GranitoLainnya.setVisibility(View.VISIBLE);
                    edt_GranitoLainnya.requestFocus();
                } else {
                    edt_GranitoLainnya.setVisibility(View.GONE);
                    edt_GranitoLainnya.setText("");
                }
            }
        });

        chk_Marmer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tbl_Marmer.setVisibility(View.VISIBLE);
                } else {
                    tbl_Marmer.setVisibility(View.GONE);
                    chk_Marmer30.setChecked(false);
                    chk_Marmer40.setChecked(false);
                    chk_Marmer60.setChecked(false);
                    chk_Marmer60.setChecked(false);
                    chk_MarmerLainnya.setChecked(false);
                }
            }
        });

        chk_MarmerLainnya.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_MarmerLainnya.setVisibility(View.VISIBLE);
                    edt_MarmerLainnya.requestFocus();
                } else {
                    edt_MarmerLainnya.setVisibility(View.GONE);
                    edt_MarmerLainnya.setText("");
                }
            }
        });

        chk_Granit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tbl_Granit.setVisibility(View.VISIBLE);
                } else {
                    tbl_Granit.setVisibility(View.GONE);
                    chk_Granit30.setChecked(false);
                    chk_Granit40.setChecked(false);
                    chk_Granit60.setChecked(false);
                    chk_Granit60.setChecked(false);
                    chk_GranitLainnya.setChecked(false);
                }
            }
        });

        chk_GranitLainnya.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_GranitLainnya.setVisibility(View.VISIBLE);
                    edt_GranitLainnya.requestFocus();
                } else {
                    edt_GranitLainnya.setVisibility(View.GONE);
                    edt_GranitLainnya.setText("");
                }
            }
        });

        chk_Teraso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tbl_Teraso.setVisibility(View.VISIBLE);
                } else {
                    tbl_Teraso.setVisibility(View.GONE);
                    chk_Teraso30.setChecked(false);
                    chk_Teraso40.setChecked(false);
                    chk_Teraso60.setChecked(false);
                    chk_Teraso60.setChecked(false);
                    chk_TerasoLainnya.setChecked(false);
                }
            }
        });

        chk_TerasoLainnya.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_TerasoLainnya.setVisibility(View.VISIBLE);
                    edt_TerasoLainnya.requestFocus();
                } else {
                    edt_TerasoLainnya.setVisibility(View.GONE);
                    edt_TerasoLainnya.setText("");
                }
            }
        });
    }

    private void CekRadioGroup() {
        rdg_StatusKerjaSama.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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
                        Toast.makeText(UpdateSurveiTermiteControl.this, "Catatan:\n" +
                                "Pastikan untuk Melakukan Konfirmasi Kembali Dalam Beberapa Waktu Kedepan Kepada Pelanggan/Perusahaan", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });

        rdg_KategoriPenanganan_Update.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton_PraKonstruksi_Update:
                        tbl_LantaiBangunan.setVisibility(View.GONE);
                        chk_Keramik.setChecked(false);
                        chk_Granito.setChecked(false);
                        chk_Granit.setChecked(false);
                        chk_Teraso.setChecked(false);
                        chk_Marmer.setChecked(false);
                        chk_LantaiBeton.setChecked(false);
                        chk_SolidParquet.setChecked(false);
                        chk_VinylParquet.setChecked(false);
                        chk_LaminatedParquet.setChecked(false);
                        penanganan = "Anti Rayap Pra-Konstruksi GARANSI 5 TAHUN";
                        break;
                    case R.id.radioButton_PascaKonstruksi_Update:
                        tbl_LantaiBangunan.setVisibility(View.VISIBLE);
                        penanganan = "Anti Rayap Pasca-Konstruksi GARANSI 3 TAHUN";
                }
            }
        });
    }

    private void CekTandaTangan() {
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

        //Button for Signature Pelanggan
        btn_DialogTTD_Pelanggan.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SignatureActivity.class);
                i.putExtra(Pesan_Extra, "TTD_PelangganTermiteControl");
                startActivityForResult(i, REQ_TTD_PELANGGAN);
            }
        });

        //Button for Signature Consultant
        btn_DialogTTD_Consultant.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(getApplicationContext(), SignatureActivity.class);
                //startActivityForResult(a,2);
                a.putExtra(Pesan_Extra, "TTD_ConsultantTermiteControl");
                startActivityForResult(a, REQ_TTD_CONSULTANT);
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
        dateNow = new SimpleDateFormat("dd-MM-yy_hh.mm.ss", Locale.getDefault()).format(new Date());
        String getNamaPelanggan = edt_NamaPelanggan_TermiteUpdate.getText().toString();

        if (requestCode == REQ_TTD_PELANGGAN) {
            if (resultCode == RESULT_OK) {
                //Nampilin hasil ttd untuk Termite Pelanggan
                image_path = data.getStringExtra("imagePath_TermitePelanggan");
                bitmap_TermitePelanggan = BitmapFactory.decodeFile(image_path);
                img_ttdPelanggan.setImageBitmap(bitmap_TermitePelanggan);
                //getBitmap
                getTTD_Pelanggan = ((BitmapDrawable) UpdateSurveiTermiteControl.img_ttdPelanggan.getDrawable()).getBitmap();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                getTTD_Pelanggan.compress(Bitmap.CompressFormat.PNG, 50, stream);
                byte[] imageInByte = stream.toByteArray();
                ConvertImage_Pelanggan = Base64.encodeToString(imageInByte, Base64.DEFAULT);
                GetImageNameFromEditText_Pelanggan = "TermiteControl_" + getNamaPelanggan + "_" + dateNow + ".png";
            }
        }
        if (requestCode == REQ_TTD_CONSULTANT) {
            if (resultCode == RESULT_OK) {
                imagepath_TermiteConsultant = data.getStringExtra("imagePath_TermiteConsultant");
                bitmap_TermiteConsultant = BitmapFactory.decodeFile(imagepath_TermiteConsultant);
                img_ttdConsultant.setImageBitmap(bitmap_TermiteConsultant);
                //getBitmap
                getTTD_Consultant = ((BitmapDrawable) UpdateSurveiTermiteControl.img_ttdConsultant.getDrawable()).getBitmap();

                ByteArrayOutputStream stream_consultant = new ByteArrayOutputStream();
                getTTD_Consultant.compress(Bitmap.CompressFormat.PNG, 50, stream_consultant);
                byte[] imageInByte_consultant = stream_consultant.toByteArray();
                ConvertImage_Consultant = Base64.encodeToString(imageInByte_consultant, Base64.DEFAULT);
                GetImageNameFromEditText_Consultant = "TermiteControl_" + TampilanMenuUtama.username + "_" + dateNow + ".png";

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

                GetImageNameFromEditText_FotoSatu = "TermiteControl_Foto_Satu_" + getNamaPelanggan + "_" + dateNow + ".png";
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

                    GetImageNameFromEditText_FotoSatu = "TermiteControl_Foto_Satu_" + getNamaPelanggan + "_" + dateNow + ".png";

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

                GetImageNameFromEditText_FotoDua = "TermiteControl_Foto_Dua_" + getNamaPelanggan + "_" + dateNow + ".png";
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

                    GetImageNameFromEditText_FotoDua = "TermiteControl_Foto_Dua_" + getNamaPelanggan + "_" + dateNow + ".png";

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

                GetImageNameFromEditText_FotoTiga = "TermiteControl_Foto_Tiga_" + getNamaPelanggan + "_" + dateNow + ".png";
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

                    GetImageNameFromEditText_FotoTiga = "TermiteControl_Foto_Tiga_" + getNamaPelanggan + "_" + dateNow + ".png";

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

                GetImageNameFromEditText_FotoEmpat = "TermiteControl_Foto_Empat_" + getNamaPelanggan + "_" + dateNow + ".png";
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

                    GetImageNameFromEditText_FotoEmpat = "TermiteControl_Foto_Empat_" + getNamaPelanggan + "_" + dateNow + ".png";

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == REQ_PLACE_PICKER) {
            if (resultCode == RESULT_OK) {
                String lokasi = data.getStringExtra("Alamat_Lokasi");
                edt_Alamat_TermiteUpdate.setText(lokasi);

                latLong_AlamatCustomer = data.getStringExtra("LatLong_Lokasi");
                Log.e("tag", latLong_AlamatCustomer);
            }
        }
    }

    protected void OnClickUpdate() {
        // <---- Untuk Bagian 1 - Data Diri Pelanggan ---->
        IDClient = edt_ClientID_TermiteUpdate.getText().toString().toUpperCase();
        namaPelanggan = edt_NamaPelanggan_TermiteUpdate.getText().toString();
        kategoriTempatPelanggan = spr_KategoriTempat_Termite_Update.getSelectedItem().toString();
        alamatPelanggan = edt_Alamat_TermiteUpdate.getText().toString();
        hpPelanggan = edt_HP_TermiteUpdate.getText().toString();
        email = edt_Email_TermiteUpdate.getText().toString();

        if (TextUtils.isEmpty(IDClient)) {
            edt_ClientID_TermiteUpdate.setError("Harap Masukkan ID Client");
            edt_ClientID_TermiteUpdate.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(namaPelanggan)) {
            edt_NamaPelanggan_TermiteUpdate.setError("Harap Masukkan Nama Pelanggan/Perusahaan");
            edt_NamaPelanggan_TermiteUpdate.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(alamatPelanggan)) {
            edt_Alamat_TermiteUpdate.setError("Harap Masukkan Alamat Pelanggan/Perusahaan");
            edt_Alamat_TermiteUpdate.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(hpPelanggan)) {
            edt_HP_TermiteUpdate.setError("Harap Masukkan No.HP atau Telepon Pelanggan/Perusahaan");
            edt_HP_TermiteUpdate.requestFocus();
            return;
        }

        if (hpPelanggan.trim().length() < 5) {
            edt_HP_TermiteUpdate.setError("Harap Masukkan Nomor Handphone/Telepon yang Valid");
            edt_HP_TermiteUpdate.requestFocus();
            return;
        }

        //Untuk Bagian II - Rayap
        //Ikut disimpan ke DB
        jenis_rayap = spr_JenisRayap_Update.getSelectedItem().toString();
        kategori_penanganan = penanganan;

        if (kategori_penanganan.isEmpty()) {
            rdb_PraKonstruksi_Update.setError("Harap Memilih Kategori Penanganan");
            rdb_PraKonstruksi_Update.requestFocusFromTouch();
            return;
        }

        //Untuk Bagian III - Metode Kendali Rayap
        if (!chk_Spraying_Update.isChecked() && !chk_Drill_Update.isChecked() && !chk_BaitingAG_Update.isChecked()
                && !chk_BaitingIG_Update.isChecked() && !chk_Fumigasi_Update.isChecked()) {
            chk_Spraying_Update.setError("Harap Memilih Minimal Satu Jenis Metode Pengendalian Rayap");
            chk_Spraying_Update.requestFocusFromTouch();
            return;
        }

        String kendali_tmp = "";

        if (chk_Spraying_Update.isChecked()) {
            kendali_tmp += "Spraying,";
        }

        if (chk_Drill_Update.isChecked()) {
            kendali_tmp += "Drill Inject,";
        }

        if (chk_BaitingAG_Update.isChecked()) {
            kendali_tmp += "Termite Baiting AG,";
        }

        if (chk_BaitingIG_Update.isChecked()) {
            kendali_tmp += "Termite Baiting IG,";
        }

        if (chk_Fumigasi_Update.isChecked()) {
            kendali_tmp += "Fumigasi,";
        }

        metode_kendali = kendali_tmp;


        //Untuk Bagian IV - Chemical yang Digunakan
        //Tmp ke DB
        String jenis_chemical_tmp = "";
        String qty_fipronil_tmp = "";
        String qty_imidaclporid_tmp = "";
        String qty_cypermethrin_tmp = "";
        String qty_dichlorphos_tmp = "";
        String qty_baitingAG_tmp = "";
        String qty_baitingIG_tmp = "";

        if (chk_Fipronil_Update.isChecked()) {
            String kuantitas = edt_QtyFipronil_Update.getText().toString();

            if (TextUtils.isEmpty(kuantitas)) {
                edt_QtyFipronil_Update.setError("Harap Masukkan Banyak Botol yang Dibutuhkan");
                edt_QtyFipronil_Update.requestFocus();
                return;
            }
            //Simpan ke tmp
            jenis_chemical_tmp += "Fipronil 50 g/l,";
            qty_fipronil_tmp = kuantitas;
        }

        if (chk_Imidaclporid_Update.isChecked()) {
            String kuantitas = edt_QtyImidaclporid_Update.getText().toString();

            if (TextUtils.isEmpty(kuantitas)) {
                edt_QtyImidaclporid_Update.setError("Harap Masukkan Banyak Botol yang Dibutuhkan");
                edt_QtyImidaclporid_Update.requestFocus();
                return;
            }
            //Simpan ke tmp
            jenis_chemical_tmp += "Imidacloprid 200 g/l,";
            qty_imidaclporid_tmp += kuantitas;
        }

        if (chk_Cypermethrin_Update.isChecked()) {
            String kuantitas = edt_QtyCypermethrin_Update.getText().toString();

            if (TextUtils.isEmpty(kuantitas)) {
                edt_QtyCypermethrin_Update.setError("Harap Masukkan Banyak Botol yang Dibutuhkan");
                edt_QtyCypermethrin_Update.requestFocus();
                return;
            }
            //Simpan ke tmp
            jenis_chemical_tmp += "Cypermethrin 100 g/l,";
            qty_cypermethrin_tmp = kuantitas;
        }

        if (chk_Dichlorphos_Update.isChecked()) {
            String kuantitas = edt_QtyDichlorphos_Update.getText().toString();

            if (TextUtils.isEmpty(kuantitas)) {
                edt_QtyDichlorphos_Update.setError("Harap Masukkan Banyak Botol yang Dibutuhkan");
                edt_QtyDichlorphos_Update.requestFocus();
                return;
            }
            //Simpan ke tmp
            jenis_chemical_tmp += "Dichlorphos 200 g/l,";
            qty_dichlorphos_tmp = kuantitas;
        }

        if (chk_ChemicalBaitingAG_Update.isChecked()) {
            String kuantitas = edt_QtyChemicalBaitingAG_Update.getText().toString();

            if (TextUtils.isEmpty(kuantitas)) {
                edt_QtyChemicalBaitingAG_Update.setError("Harap Masukkan Banyak Unit yang Dibutuhkan");
                edt_QtyChemicalBaitingAG_Update.requestFocus();
                return;
            }
            //Simpan ke tmp
            jenis_chemical_tmp += "Baiting AG,";
            qty_baitingAG_tmp = kuantitas;
        }

        if (chk_ChemicalBaitingIG_Update.isChecked()) {
            String kuantitas = edt_QtyChemicalBaitingIG_Update.getText().toString();

            if (TextUtils.isEmpty(kuantitas)) {
                edt_QtyChemicalBaitingIG_Update.setError("Harap Masukkan Banyak Unit yang Dibutuhkan");
                edt_QtyChemicalBaitingIG_Update.requestFocus();
                return;
            }
            //Simpan ke tmp
            jenis_chemical_tmp += "Baiting IG,";
            qty_baitingIG_tmp = kuantitas;
        }

        total_chemical_DB = jenis_chemical_tmp;

        qty_fipronil_DB = qty_fipronil_tmp;
        qty_imidaclporid_DB = qty_imidaclporid_tmp;
        qty_cypermethrin_DB = qty_cypermethrin_tmp;
        qty_dichlorphos_DB = qty_dichlorphos_tmp;
        qty_baitingAG_DB = qty_baitingAG_tmp;
        qty_baitingIG_DB = qty_baitingIG_tmp;

        //Untuk Bagian V - Luas Outdoor Indoor
        luas_indoor = edt_LuasBangunan.getText().toString();
        luas_outdoor = edt_LuasOutdoor.getText().toString();

        if (TextUtils.isEmpty(luas_indoor)) {
            edt_LuasBangunan.setError("Harap Masukkan Luas Bangunan");
            edt_LuasBangunan.requestFocus();
            return;
        }

        //Untuk Bagian VI - Pasca Konstruksi
        String bangunan_lantai_tmp = "";

        String qty_keramik_tmp = "";
        String ukuran_keramik_lainnya = "";

        String qty_granito_tmp = "";
        String ukuran_granito_lainnya = "";

        String qty_marmer_tmp = "";
        String ukuran_marmer_lainnya = "";

        String qty_teraso_tmp = "";
        String ukuran_teraso_lainnya = "";

        String qty_granit_tmp = "";
        String ukuran_granit_lainnya = "";

        if (penanganan.equals("Anti Rayap Pasca-Konstruksi GARANSI 3 TAHUN")) {
            //Untuk Keramik
            if (chk_Keramik.isChecked()) {
                bangunan_lantai_tmp += "Keramik,";

                if (!chk_Keramik30.isChecked() && !chk_Keramik40.isChecked() && !chk_Keramik60.isChecked() &&
                        !chk_KeramikLainnya.isChecked()) {
                    chk_Keramik30.setError("Harap Memilih Minimal Satu Jenis Ukuran Lantai Bangunan");
                    chk_Keramik30.requestFocusFromTouch();
                    return;
                }

                if (chk_Keramik30.isChecked()) {
                    qty_keramik_tmp += "30x30,";
                }
                if (chk_Keramik40.isChecked()) {
                    qty_keramik_tmp += "40x40,";
                }
                if (chk_Keramik60.isChecked()) {
                    qty_keramik_tmp += "60x60,";
                }
                if (chk_KeramikLainnya.isChecked()) {
                    qty_keramik_tmp += "Lainnya,";
                    ukuran_keramik_lainnya += edt_KeramikLainnya.getText().toString();

                    if (TextUtils.isEmpty(ukuran_keramik_lainnya)) {
                        edt_KeramikLainnya.setError("Harap Memasukkan Ukuran Lantai");
                        edt_KeramikLainnya.requestFocus();
                        return;
                    }
                }
            }

            //Untuk Granito
            if (chk_Granito.isChecked()) {
                //tmp ke DB
                bangunan_lantai_tmp += "Granit Tile,";

                if (!chk_Granito30.isChecked() && !chk_Granito40.isChecked() && !chk_Granito60.isChecked() &&
                        !chk_GranitoLainnya.isChecked()) {
                    chk_Granito30.setError("Harap Memilih Minimal Satu Jenis Ukuran Lantai Bangunan");
                    chk_Granito30.requestFocusFromTouch();
                    return;
                }

                if (chk_Granito30.isChecked()) {
                    qty_granito_tmp += "30x30,";
                }
                if (chk_Granito40.isChecked()) {
                    qty_granito_tmp += "40x40,";
                }
                if (chk_Granito60.isChecked()) {
                    qty_granito_tmp += "60x60,";
                }
                if (chk_GranitoLainnya.isChecked()) {
                    qty_granito_tmp += "Lainnya,";
                    ukuran_granito_lainnya += edt_GranitoLainnya.getText().toString();

                    if (TextUtils.isEmpty(ukuran_granito_lainnya)) {
                        edt_GranitoLainnya.setError("Harap Memasukkan Ukuran Lantai");
                        edt_GranitoLainnya.requestFocus();
                        return;
                    }
                }
            }

            //Untuk Marmer
            if (chk_Marmer.isChecked()) {
                bangunan_lantai_tmp += "Marmer,";

                if (!chk_Marmer30.isChecked() && !chk_Marmer40.isChecked() && !chk_Marmer60.isChecked() &&
                        !chk_MarmerLainnya.isChecked()) {
                    chk_Marmer30.setError("Harap Memilih Minimal Satu Jenis Ukuran Lantai Bangunan");
                    chk_Marmer30.requestFocusFromTouch();
                    return;
                }

                if (chk_Marmer30.isChecked()) {
                    qty_marmer_tmp += "30x30,";
                }
                if (chk_Marmer40.isChecked()) {
                    qty_marmer_tmp += "40x40,";
                }
                if (chk_Marmer60.isChecked()) {
                    qty_marmer_tmp += "60x60,";
                }
                if (chk_MarmerLainnya.isChecked()) {
                    qty_marmer_tmp += "Lainnya,";
                    ukuran_marmer_lainnya += edt_MarmerLainnya.getText().toString();

                    if (TextUtils.isEmpty(ukuran_marmer_lainnya)) {
                        edt_MarmerLainnya.setError("Harap Memasukkan Ukuran Lantai");
                        edt_MarmerLainnya.requestFocus();
                        return;
                    }
                }
            }

            //Untuk Teraso
            if (chk_Teraso.isChecked()) {
                bangunan_lantai_tmp += "Teraso,";

                if (!chk_Teraso30.isChecked() && !chk_Teraso40.isChecked() && !chk_Teraso60.isChecked() &&
                        !chk_TerasoLainnya.isChecked()) {
                    chk_Teraso30.setError("Harap Memilih Minimal Satu Jenis Ukuran Lantai Bangunan");
                    chk_Teraso30.requestFocusFromTouch();
                    return;
                }

                if (chk_Teraso30.isChecked()) {
                    qty_teraso_tmp += "30x30,";
                }
                if (chk_Teraso40.isChecked()) {
                    qty_teraso_tmp += "40x40,";
                }
                if (chk_Teraso60.isChecked()) {
                    qty_teraso_tmp += "60x60,";
                }
                if (chk_TerasoLainnya.isChecked()) {
                    qty_teraso_tmp += "Lainnya,";
                    ukuran_teraso_lainnya += edt_TerasoLainnya.getText().toString();

                    if (TextUtils.isEmpty(ukuran_teraso_lainnya)) {
                        edt_TerasoLainnya.setError("Harap Memasukkan Ukuran Lantai");
                        edt_TerasoLainnya.requestFocus();
                        return;
                    }
                }
            }

            //Untuk Granit
            if (chk_Granit.isChecked()) {
                bangunan_lantai_tmp += "Granit,";

                if (!chk_Granit30.isChecked() && !chk_Granit40.isChecked() && !chk_Granit60.isChecked() &&
                        !chk_GranitLainnya.isChecked()) {
                    chk_Granit30.setError("Harap Memilih Minimal Satu Jenis Ukuran Lantai Bangunan");
                    chk_Granit30.requestFocusFromTouch();
                    return;
                }

                if (chk_Granit30.isChecked()) {
                    qty_granit_tmp += "30x30,";
                }
                if (chk_Granit40.isChecked()) {
                    qty_granit_tmp += "40x40,";
                }
                if (chk_Granit60.isChecked()) {
                    qty_granit_tmp += "60x60,";
                }
                if (chk_GranitLainnya.isChecked()) {
                    qty_granit_tmp += "Lainnya,";
                    ukuran_granit_lainnya += edt_GranitLainnya.getText().toString();

                    if (TextUtils.isEmpty(ukuran_granit_lainnya)) {
                        edt_GranitLainnya.setError("Harap Memasukkan Ukuran Lantai");
                        edt_GranitLainnya.requestFocus();
                        return;
                    }
                }
            }

            if (chk_LantaiBeton.isChecked()) {
                bangunan_lantai_tmp += "Lantai Beton,";
            }

            if (chk_SolidParquet.isChecked()) {
                bangunan_lantai_tmp += "Solid Parquet,";
            }

            if (chk_VinylParquet.isChecked()) {
                bangunan_lantai_tmp += "Vinyl Parquet,";
            }

            if (chk_LaminatedParquet.isChecked()) {
                bangunan_lantai_tmp += "Laminated Parquet,";
            }
            Log.e("tag", bangunan_lantai_tmp);

            if (bangunan_lantai_tmp.isEmpty()) {
                chk_Keramik.setError("Harap Memilih Minimal Satu Jenis Lantai Bangunan");
                chk_Keramik.requestFocusFromTouch();
                return;
            }
        }

        //Simpan ke DB
        lantai_bangunan_final = bangunan_lantai_tmp;

        ukuran_keramik_final = qty_keramik_tmp;
        ukuran_keramik_lainnya_final = ukuran_keramik_lainnya;

        ukuran_granito_final = qty_granito_tmp;
        ukuran_granito_lainnya_final = ukuran_granito_lainnya;

        ukuran_marmer_final = qty_marmer_tmp;
        ukuran_marmer_lainnya_final = ukuran_marmer_lainnya;

        ukuran_teraso_final = qty_teraso_tmp;
        ukuran_teraso_lainnya_final = ukuran_teraso_lainnya;

        ukuran_granit_final = qty_granit_tmp;
        ukuran_granit_lainnya_final = ukuran_granit_lainnya;

        //Untuk kontrak dan Penawaran Harga
        penawaran_harga = edt_PenawaranHarga.getText().toString();
        total_harga_penawaran = edt_GrandTotal_Termite.getText().toString();
        status = status_kerjasama;
        catatan_tambahan = edt_CatatanTambahan.getText().toString();

        if (TextUtils.isEmpty(penawaran_harga)) {
            edt_PenawaranHarga.setError("Harap Memasukkan Penawaran Harga");
            edt_PenawaranHarga.requestFocus();
            return;
        }

        if (status.isEmpty()) {
            rdb_Berhasil.setError("Harap Memilih Status Kerjasama");
            rdb_Berhasil.requestFocusFromTouch();
            return;
        }

        //Untuk Proses Tanda Tangan
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

        //Untuk Foto
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
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateSurveiTermiteControl.this);

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

    private void saveToDB() {

        @SuppressLint("StaticFieldLeak")
        class simpan extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                pDialog.setMessage("Menyimpan Data Survei Termite Control ke Database, Harap Tunggu...");
                pDialog.show();
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                pDialog.dismiss();
                Toast.makeText(UpdateSurveiTermiteControl.this, s, Toast.LENGTH_LONG).show();

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
                params.put(Konfigurasi.KEY_SAVE_ID_CLIENT_TERMITE, IDClient);
                params.put(Konfigurasi.KEY_SAVE_NAMA_PELANGGAN_TERMITE, namaPelanggan);
                params.put(Konfigurasi.KEY_SAVE_KATEGORITEMPAT_TERMITE, kategoriTempatPelanggan);
                params.put(Konfigurasi.KEY_SAVE_ALAMAT_PELANGGAN_TERMITE, alamatPelanggan);
                params.put(Konfigurasi.KEY_SAVE_HP_PELANGGAN_TERMITE, hpPelanggan);
                params.put(Konfigurasi.KEY_SAVE_EMAIL_PELANGGAN_TERMITE, email);

                params.put(Konfigurasi.KEY_SAVE_LATLONG_ALAMAT_PELANGGAN, latLong_AlamatCustomer);
                params.put(Konfigurasi.KEY_SAVE_LATLONG_CONSULTANT, latLong_Consultant);

                //Bagian II : Rayap
                params.put(Konfigurasi.KEY_SAVE_JENIS_RAYAP_TERMITE, jenis_rayap);
                params.put(Konfigurasi.KEY_SAVE_KATEGORI_PENANGANAN_TERMITE, kategori_penanganan);

                //Bagian III : Metode Kendali
                params.put(Konfigurasi.KEY_SAVE_METODE_KENDALI_TERMITE, metode_kendali);

                //Bagian IV : Bagian Chemical
                params.put(Konfigurasi.KEY_SAVE_CHEMICAL_TERMITE, total_chemical_DB);
                params.put(Konfigurasi.KEY_SAVE_QTY_FIPRONIL_TERMITE, qty_fipronil_DB);
                params.put(Konfigurasi.KEY_SAVE_QTY_IMIDACLPORID_TERMITE, qty_imidaclporid_DB);
                params.put(Konfigurasi.KEY_SAVE_QTY_CYPERMETHRIN_TERMITE, qty_cypermethrin_DB);
                params.put(Konfigurasi.KEY_SAVE_QTY_DICHLORPHOS_TERMITE, qty_dichlorphos_DB);
                params.put(Konfigurasi.KEY_SAVE_QTY_BAITING_AG_TERMITE, qty_baitingAG_DB);
                params.put(Konfigurasi.KEY_SAVE_QTY_BAITING_IG_TERMITE, qty_baitingIG_DB);

                //Bagian V : Bagian Luas Outdoor Indoor
                params.put(Konfigurasi.KEY_SAVE_LUAS_INDOOR_TERMITE, luas_indoor);
                params.put(Konfigurasi.KEY_SAVE_LUAS_OUTDOOR_TERMITE, luas_outdoor);

                //Bagian VI : Bagian Pasca Konstruksi
                params.put(Konfigurasi.KEY_SAVE_LANTAI_BANGUNAN_TERMITE, lantai_bangunan_final);

                params.put(Konfigurasi.KEY_SAVE_QTY_KERAMIK_TERMITE, ukuran_keramik_final);
                params.put(Konfigurasi.KEY_SAVE_KERAMIK_LAINNYA_TERMITE, ukuran_keramik_lainnya_final);

                params.put(Konfigurasi.KEY_SAVE_QTY_GRANITO_TERMITE, ukuran_granito_final);
                params.put(Konfigurasi.KEY_SAVE_GRANITO_LAINNYA_TERMITE, ukuran_granito_lainnya_final);

                params.put(Konfigurasi.KEY_SAVE_QTY_MARMER_TERMITE, ukuran_marmer_final);
                params.put(Konfigurasi.KEY_SAVE_MARMER_LAINNYA_TERMITE, ukuran_marmer_lainnya_final);

                params.put(Konfigurasi.KEY_SAVE_QTY_TERASO_TERMITE, ukuran_teraso_final);
                params.put(Konfigurasi.KEY_SAVE_TERASO_LAINNYA_TERMITE, ukuran_teraso_lainnya_final);

                params.put(Konfigurasi.KEY_SAVE_QTY_GRANIT_TERMITE, ukuran_granit_final);
                params.put(Konfigurasi.KEY_SAVE_GRANIT_LAINNYA_TERMITE, ukuran_granit_lainnya_final);

                //Bagian VII : Kontrak dan Penawaran Harga
                params.put(Konfigurasi.KEY_SAVE_PENAWARAN_HARGA_TERMITE, penawaran_harga);
                params.put(Konfigurasi.KEY_SAVE_TOTAL_HARGA_TERMITE, total_harga_penawaran);
                params.put(Konfigurasi.KEY_SAVE_STATUS_KERJASAMA_TERMITE, status);
                params.put(Konfigurasi.KEY_SAVE_CATATAN_TAMBAHAN_TERMITE, catatan_tambahan);

                //Bagian VIII : Tanda Tangan
                //Cek if Null or Not -->
                params.put(Konfigurasi.KEY_CEK_IMG_TTD_PELANGGAN, image_path);
                params.put(Konfigurasi.KEY_CEK_IMG_TTD_CONSULTANT, imagepath_TermiteConsultant);

                //untuk Pelanggan
                params.put(Konfigurasi.KEY_SAVE_IMAGE_TAG_PELANGGAN_TERMITE, GetImageNameFromEditText_Pelanggan);
                params.put(Konfigurasi.KEY_SAVE_IMAGE_NAME_PELANGGAN_TERMITE, ConvertImage_Pelanggan);
                Log.e("tagz", image_path);
                //}

                //untuk Consultant
                params.put(Konfigurasi.KEY_SAVE_IMAGE_TAG_CONSULTANT_TERMITE, GetImageNameFromEditText_Consultant);
                params.put(Konfigurasi.KEY_SAVE_IMAGE_NAME_CONSULTANT_TERMITE, ConvertImage_Consultant);
                Log.e("tagz", imagepath_TermiteConsultant);
                //}

                params.put(Konfigurasi.KEY_TAG_ID, TampilanMenuUtama.id_pegawai);
                params.put(Konfigurasi.TERMITE_KEY_GET_LIST_SURVEI_ID, id_survei);
                Log.e("tag", id_survei);

                RequestHandler rh = new RequestHandler();
                return rh.sendPostRequest(Konfigurasi.URL_UPDATE_TERMITE_SURVEI, params);
            }
        }

        simpan save = new simpan();
        save.execute();

    }

    private void uploadFoto() {
        if(GetImageNameFromEditText_FotoSatu != null){
            getFotoSatu = ((BitmapDrawable) UpdateSurveiTermiteControl.img_FotoSatu.getDrawable()).getBitmap();
            ByteArrayOutputStream stream_foto_satu = new ByteArrayOutputStream();
            getFotoSatu.compress(Bitmap.CompressFormat.PNG, 70, stream_foto_satu);
            byte[] imageInByte_foto_satu = stream_foto_satu.toByteArray();
            ConvertImage_FotoSatu = Base64.encodeToString(imageInByte_foto_satu, Base64.DEFAULT);
        }

        if(GetImageNameFromEditText_FotoDua != null){
            getFotoDua = ((BitmapDrawable) UpdateSurveiTermiteControl.img_FotoDua.getDrawable()).getBitmap();
            ByteArrayOutputStream stream_foto_dua = new ByteArrayOutputStream();
            getFotoDua.compress(Bitmap.CompressFormat.PNG, 70, stream_foto_dua);
            byte[] imageInByte_foto_dua = stream_foto_dua.toByteArray();
            ConvertImage_FotoDua = Base64.encodeToString(imageInByte_foto_dua, Base64.DEFAULT);
        }

        if(GetImageNameFromEditText_FotoTiga != null){
            getFotoTiga = ((BitmapDrawable) UpdateSurveiTermiteControl.img_FotoTiga.getDrawable()).getBitmap();
            ByteArrayOutputStream stream_foto_tiga = new ByteArrayOutputStream();
            getFotoTiga.compress(Bitmap.CompressFormat.PNG, 70, stream_foto_tiga);
            byte[] imageInByte_foto_tiga = stream_foto_tiga.toByteArray();
            ConvertImage_FotoTiga = Base64.encodeToString(imageInByte_foto_tiga, Base64.DEFAULT);
        }

        if(GetImageNameFromEditText_FotoEmpat != null){
            getFotoEmpat = ((BitmapDrawable) UpdateSurveiTermiteControl.img_FotoEmpat.getDrawable()).getBitmap();
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
                Toast.makeText(UpdateSurveiTermiteControl.this, s, Toast.LENGTH_LONG).show();
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
                return rh.sendPostRequest(Konfigurasi.URL_UPDATE_PICTURE_TERMITE_PEST, params);
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
                Toast.makeText(UpdateSurveiTermiteControl.this, s, Toast.LENGTH_LONG).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateSurveiTermiteControl.this);
                builder.setMessage(s)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent del_intent = new Intent(UpdateSurveiTermiteControl.this, TabSurveiTermiteControl.class);
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
                return rh.sendPostRequest(Konfigurasi.URL_DELETE_SURVEI_TERMITE, params);
            }
        }

        deleteSurvei Delete = new deleteSurvei();
        Delete.execute();
    }
}
