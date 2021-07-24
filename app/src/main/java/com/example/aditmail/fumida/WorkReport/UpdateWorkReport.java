package com.example.aditmail.fumida.WorkReport;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
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
import android.widget.TimePicker;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class UpdateWorkReport extends AppCompatActivity implements View.OnClickListener {

    ConnectivityManager conMgr;
    ProgressDialog pDialog;

    //Get To Signature Activity
    private final static String path_fumida = "com.example.aditmail.fumida";
    public final static String Pesan_Extra = path_fumida + ".Pesan";

    protected TextView txt_View, txt_Edit, txt_Delete;

    private TextView txt_idSurvei, txt_tglInput;
    protected String tgl_input, id_survei;

    private LinearLayout linear_workReport;

    //Get Data From Another Activity
    private final String id_pegawai = TampilanMenuUtama.id_pegawai;

    protected TimePickerDialog timePickerDialog;

    //--------------- Bagian I : Jenis Pekerjaan ---------------
    private RadioGroup rdg_JenisKerja;
    private RadioButton rdb_Treatment, rdb_Complaint, rdb_Supervisi;

    private EditText edt_Client, edt_ClientID, edt_AlamatClient;
    private ImageView imgPinCircle;
    protected Double lat, lng;

    private String latLong_AlamatCustomer, latLong_Consultant;

    //Untuk Simpan Nama TTD
    protected String namaPelanggan = "";
    //--------------- Bagian I : Jenis Pekerjaan ---------------

    //--------------- Bagian II : Klasifikasi ---------------
    private RadioGroup rdg_Pekerjaan;
    private RadioButton rdb_Pest, rdb_Termite, rdb_Fumigasi;
    private String pengerjaan = "", pilihan_kerja = "";

    private TableLayout tbl_KategoriPest, tbl_KategoriTermite, tbl_KategoriFumigasi;
    //--------------- Bagian II : Klasifikasi ---------------


    //--------------- Bagian III : Pest Control ---------------
    protected TableLayout tbl_PestControl;
    protected TextView img_JenisHama, img_MetodePengendalianHama_Pest;
    private CheckBox chk_Kecoa, chk_Kutu, chk_Lalat, chk_Cicak, chk_Semut, chk_Tikus, chk_Tawon, chk_Nyamuk, chk_HamaLainnya;
    private EditText edt_HamaLainnya;

    //Bagian Metode Treatment Pest Control
    private TableLayout tbl_TreatmentPestControl;
    private EditText edt_QtyPerangkapTikus, edt_QtyLemTikus, edt_QtyUmpanIndoor, edt_QtyUmpanOutdoor, edt_QtyPohonLalat,
            edt_QtyBlackHole, edt_QtyFlyCatcher, edt_MetodeLainnya;

    private CheckBox chk_Penyemprotan, chk_Pengembunan, chk_PengasapanThermal, chk_PengasapanMini, chk_PerangkapTikus, chk_LemTikus, chk_UmpanTikusIndoor,
            chk_UmpanTikusOutdoor, chk_UmpanSemut, chk_GelKecoa, chk_LemKecoa, chk_BlackHole, chk_FlyCatcher, chk_PohonLalat, chk_UmpanLalat, chk_MetodeLainnya, chk_Fumigasi;
    //--------------- Bagian III : Pest Control ---------------

    //--------------- Bagian IV : Termite Control ---------------
    protected TableLayout tbl_TreatmentTermiteControl;
    private Spinner spr_JenisRayap, spr_GasFumigasi;

    //Bagian Metode Pengendalian Rayap
    protected CheckBox chk_Spraying, chk_Drill, chk_BaitingAG, chk_BaitingIG, chk_FumigasiTermiteControl;

    //Bagian Chemical yg Digunakan
    protected TextView img_Chemical;
    protected TableLayout tbl_ChemicalTermite;
    private CheckBox chk_Fipronil, chk_Imidaclporid, chk_Cypermethrin, chk_Dichlorphos, chk_ChemicalBaitingAG, chk_ChemicalBaitingIG;
    private EditText edt_QtyFipronil, edt_QtyImidaclporid, edt_QtyCypermethrin, edt_QtyDichlorphos, edt_QtyChemicalBaitingAG, edt_QtyChemicalBaitingIG;
    //--------------- Bagian IV : Termite Control ---------------

    //--------------- Bagian V : Fumigasi Control ---------------
    private Spinner spr_Fumigasi;
    protected TextView img_GasFumigasi;
    //--------------- Bagian V : Fumigasi Control ---------------

    //--------------- Bagian VI : Waktu Mulai ---------------
    private EditText edt_Pekerja;
    private EditText edt_WaktuMulai;
    //--------------- Bagian VI : Waktu Mulai ---------------

    //--------------- Bagian VII : Upload Foto---------------
    private Button btn_PilihFotoSatu, btn_PilihFotoDua, btn_PilihFotoTiga, btn_PilihFotoEmpat;

    @SuppressLint("StaticFieldLeak")
    protected static ImageView img_FotoSatu, img_FotoDua, img_FotoTiga, img_FotoEmpat;

    private Space spc_Satu, spc_Dua, spc_Tiga;

    //Untuk Upload Image Foto...
    protected Bitmap getFotoSatu, getFotoDua, getFotoTiga, getFotoEmpat;
    private String ConvertImage_FotoSatu = "", ConvertImage_FotoDua = "", ConvertImage_FotoTiga = "",
            ConvertImage_FotoEmpat = "";
    private String GetImageNameFromEditText_FotoSatu = "", GetImageNameFromEditText_FotoDua = "", GetImageNameFromEditText_FotoTiga = "",
            GetImageNameFromEditText_FotoEmpat = "";
    //--------------- Bagian VII : Upload Foto---------------

    //--------------- Bagian VIII : Upload TTD ---------------
    private Button btn_DialogTTD_Pelanggan, btn_DialogTTD_Consultant;

    @SuppressLint("StaticFieldLeak")
    protected static ImageView img_ttdPelanggan, img_ttdConsultant;

    private CheckBox chk_PernyataanPelanggan, chk_PernyataanConsultant;

    //Untuk upload image ttd
    private String imagepath_WorkReport_Pelanggan = "null", imagepath_WorkReport_Consultant = "null";
    protected Bitmap bitmap_WorkReport_Pelanggan, bitmap_WorkReport_Consultant;
    protected Bitmap getTTD_Consultant, getTTD_Pelanggan;

    private String ConvertImage_Pelanggan = "", ConvertImage_Consultant = "";
    private String GetImageNameFromEditText_Pelanggan = "", GetImageNameFromEditText_Consultant = "";
    //--------------- Bagian VIII : Upload TTD ---------------

    //Button Simpan
    Button btn_SimpanWorkReport;

    //getData from DB ----------------------------------------------------------->
    protected String jenisPengerjaan, idClient, NamaPelanggan, alamatPelanggan, pekerjaan, totalHama, hamaLain, metodeKendaliHama,
            qtyLemTikus, qtyPerangkapTikus, qtyUmpanTikusOutdoor, qtyUmpanTikusIndoor, qtyPohonLalat, qtyBlackhole,
            jenisFumigasi, qtyFlycatcher, metodeLain, totalRayap, metodeKendaliRayap, chemicalTermite, qtyFipronil, qtyImidaclporid,
            qtyCypermethrin, qtyDicholorphos, qtyBaitingAG, qtyBaitingIG, gasFumigasi, waktuMulai, worker,
            pathFotoSatu, pathFotoDua, pathFotoTiga, pathFotoEmpat, pathTTDClient, pathTTDConsultant;
    //getData from DB ----------------------------------------------------------->

    //Save to DB ------------------------------------------------------------------>
    protected String jenis_pengerjaan, clientID_WorkReport, namaClient_WorkReport, alamat_WorkReport, pekerjaan_workReport,
            jenis_hama_final, hama_lain_final, metode_kendali_hama_final, qty_lemTikus_final, qty_perangkapTikus_final,
            qty_umpanTikus_final, qty_umpanTikusIndoor_final, qty_pohonLalat_final, qty_blackhole_final, jenis_fumigasi_final, qty_flyCatcher_final,
            metodeLain_final, jenis_rayap_final, metode_kendali_rayap_final, jenis_chemical_final, qty_fipronil_final,
            qty_imidaclporid_final, qty_cypermethrin_final, qty_dichlorphos_final, qty_baitingAG_final, qty_baitingIG_final,
            final_fumigasi, waktu_mulai, waktu_selesai, pekerja;
    //Save to DB ------------------------------------------------------------------>

    LocationManager locationManager;

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
        setContentView(R.layout.activity_update_work_report);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);
        pDialog.setCanceledOnTouchOutside(false);

        InitData();

        //Get Intent to Activity
        Intent intent = getIntent();
        tgl_input = intent.getStringExtra(Konfigurasi.KEY_TANGGAL_INPUT);
        id_survei = intent.getStringExtra(Konfigurasi.KEY_SURVEI_ID);

        txt_idSurvei.setText(id_survei);
        txt_tglInput.setText(tgl_input);

        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
            getDataWorkReport();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection",
                    Toast.LENGTH_LONG).show();
        }

        //Function Tindakan
        EditData();
        OpenTTD();
        ShowTimeAndWork();
        ForHamaClicked();
        ChemicalTermite();

    }

    @Override
    protected void onStart() {
        OpenPhoto();
        OpenMaps();
        super.onStart();
    }

    private void InitData() {
        linear_workReport = findViewById(R.id.linearLayout_UpdateWorkReport);

        txt_idSurvei = findViewById(R.id.textView_idSurvei_WorkReport);
        txt_tglInput = findViewById(R.id.textView_Tgl_Input);

        //Tindakan
        txt_Edit = findViewById(R.id.textView_Update);
        txt_Delete = findViewById(R.id.textView_Delete);
        txt_View = findViewById(R.id.textView_View);

        rdg_JenisKerja = findViewById(R.id.RadioGroup_JenisKerja_Update);
        rdb_Treatment = findViewById(R.id.radioButton_Treatment_Update);
        rdb_Complaint = findViewById(R.id.radioButton_Complaint_Update);
        rdb_Supervisi = findViewById(R.id.radioButton_Supervisi_Update);

        edt_Client = findViewById(R.id.editText_Client_Update);
        edt_ClientID = findViewById(R.id.editText_ClientID_Update);
        edt_AlamatClient = findViewById(R.id.editText_AlamatClient_Update);
        imgPinCircle = findViewById(R.id.imgPin);

        rdg_Pekerjaan = findViewById(R.id.RadioGroup_Pekerjaan_Update);
        rdb_Pest = findViewById(R.id.radioButton_PestControl_Update);
        rdb_Termite = findViewById(R.id.radioButton_TermiteControl_Update);
        rdb_Fumigasi = findViewById(R.id.radioButton_Fumigasi_Update);

        //Jenis Pekerjaan...
        tbl_KategoriPest = findViewById(R.id.TableLayout_KategoriPestControl_Update);
        tbl_KategoriTermite = findViewById(R.id.TableLayout_KategoriTermite_Update);
        tbl_KategoriFumigasi = findViewById(R.id.TableLayout_KategoriFumigasi_Update);

        img_JenisHama = findViewById(R.id.imageView_JenisHama);
        img_MetodePengendalianHama_Pest = findViewById(R.id.imageView_MetodeTreatment_Pest);

        img_GasFumigasi = findViewById(R.id.imageView_GasFumigasi);

        spr_GasFumigasi = findViewById(R.id.spinner_GasFumigasi_Update);
        //Bagian Jenis Hama Termite
        spr_JenisRayap = findViewById(R.id.spinner_JenisRayap_Update);

        //Bagian Jenis Hama Pest
        tbl_PestControl = findViewById(R.id.TableLayout_PestControl_Update);
        chk_Semut = findViewById(R.id.checkBox_Semut_Update);
        chk_Tikus = findViewById(R.id.checkBox_Tikus_Update);
        chk_Kecoa = findViewById(R.id.checkBox_Kecoa_Update);
        chk_Nyamuk = findViewById(R.id.checkBox_Nyamuk_Update);
        chk_Lalat = findViewById(R.id.checkBox_Lalat_Update);
        chk_Kutu = findViewById(R.id.checkBox_Kutu_Update);
        chk_Tawon = findViewById(R.id.checkBox_Tawon_Update);
        chk_Cicak = findViewById(R.id.checkBox_Cicak_Update);
        chk_Fumigasi = findViewById(R.id.checkBox_Fumigasi_Update);
        chk_HamaLainnya = findViewById(R.id.checkBox_HamaLainnya_Update);
        edt_HamaLainnya = findViewById(R.id.editText_HamaLainnya_Update);

        //Bagian Metode Pengendalian Rayap
        tbl_TreatmentTermiteControl = findViewById(R.id.TableLayout_TreatmentTermiteControl);
        chk_Spraying = findViewById(R.id.checkBox_Spraying_Update);
        chk_Drill = findViewById(R.id.checkBox_InjectDrill_Update);
        chk_BaitingAG = findViewById(R.id.checkBox_BaitingAG_Update);
        chk_BaitingIG = findViewById(R.id.checkBox_BaitingIG_Update);
        chk_FumigasiTermiteControl = findViewById(R.id.checkBox_FumigasiTermite_Update);

        //Bagian Chemical yg Digunakan
        img_Chemical = findViewById(R.id.imageView_ChemicalTermite);
        tbl_ChemicalTermite = findViewById(R.id.TableLayout_ChemicalTermite);
        chk_Fipronil = findViewById(R.id.checkBox_Fipronil_Update);
        chk_Imidaclporid = findViewById(R.id.checkBox_Imidaclporid_Update);
        chk_Cypermethrin = findViewById(R.id.checkBox_Cypermethrin_Update);
        chk_Dichlorphos = findViewById(R.id.checkBox_Dichlorphos_Update);
        chk_ChemicalBaitingAG = findViewById(R.id.checkBox_BaitingAG_Chemical_Update);
        chk_ChemicalBaitingIG = findViewById(R.id.checkBox_BaitingIG_Chemical_Update);

        edt_QtyFipronil = findViewById(R.id.editText_QtyFipronil_Update);
        edt_QtyImidaclporid = findViewById(R.id.editText_QtyImidaclporid_Update);
        edt_QtyCypermethrin = findViewById(R.id.editText_QtyCypermethrin_Update);
        edt_QtyDichlorphos = findViewById(R.id.editText_QtyDichlorphos_Update);
        edt_QtyChemicalBaitingAG = findViewById(R.id.editText_QtyBaitingAG_Update);
        edt_QtyChemicalBaitingIG = findViewById(R.id.editText_QtyBaitingIG_Update);

        //Bagian Metode Pengendalian
        tbl_TreatmentPestControl = findViewById(R.id.TableLayout_TreatmentPestControl_Update);
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

        edt_Pekerja = findViewById(R.id.editText_PersonWorker_Update);
        edt_WaktuMulai = findViewById(R.id.editText_WaktuMulai_Update);
        // edt_WaktuSelesai = findViewById(R.id.editText_WaktuSelesai);

        //Button Foto
        btn_PilihFotoSatu = findViewById(R.id.button_FotoSatu_Update);
        img_FotoSatu = findViewById(R.id.imageView_FotoSatu_Update);
        btn_PilihFotoDua = findViewById(R.id.button_FotoDua_Update);
        img_FotoDua = findViewById(R.id.imageView_FotoDua_Update);
        btn_PilihFotoTiga = findViewById(R.id.button_FotoTiga_Update);
        img_FotoTiga = findViewById(R.id.imageView_FotoTiga_Update);
        btn_PilihFotoEmpat = findViewById(R.id.button_FotoEmpat_Update);
        img_FotoEmpat = findViewById(R.id.imageView_FotoEmpat_Update);

        spc_Satu = findViewById(R.id.Space_Satu);
        spc_Dua = findViewById(R.id.Space_Dua);
        spc_Tiga = findViewById(R.id.Space_Tiga);
        btn_DialogTTD_Pelanggan = findViewById(R.id.button_GetTTD_Pelanggan_Update);
        btn_DialogTTD_Consultant = findViewById(R.id.button_GetTTD_Consultant_Update);
        img_ttdPelanggan = findViewById(R.id.imageView_TTD_Pelanggan_Update);
        img_ttdConsultant = findViewById(R.id.imageView_TTD_Consultant_Update);
        chk_PernyataanPelanggan = findViewById(R.id.checkBox_PernyataanCustomer_WorkReport_Update);
        chk_PernyataanConsultant = findViewById(R.id.checkBox_PernyataanPestConsultant_WorkReport_Update);

        //Button Simpan
        btn_SimpanWorkReport = findViewById(R.id.button_SimpanWorkReport_Update);
        btn_SimpanWorkReport.setOnClickListener(this);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_SimpanWorkReport) {
            simpan_workReport();
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
        setViewAndChildrenEnabled(linear_workReport, false);

        txt_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setViewAndChildrenEnabled(linear_workReport, true);
                btn_DialogTTD_Consultant.setEnabled(false);
                btn_DialogTTD_Pelanggan.setEnabled(false);
            }
        });

        txt_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(UpdateWorkReport.this);
                    builder.setCancelable(true);
                    builder.setTitle("Apakah Anda Yakin Menghapus Work Report Tersebut?");
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
                Intent a = new Intent(UpdateWorkReport.this, PreviewWorkReport.class);
                a.putExtra(Konfigurasi.KEY_TAG_ID, id_pegawai);
                a.putExtra(Konfigurasi.KEY_SURVEI_ID, id_survei);
                startActivity(a);
            }
        });
    }

    private void getDataWorkReport() {
        @SuppressLint("StaticFieldLeak")
        class GetDataWorkReport extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                pDialog.setMessage("Mengambil Data, Harap Menunggu...");
                pDialog.show();
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                pDialog.dismiss();
                ShowDataWorkReport(s);
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();
                params.put(Konfigurasi.KEY_TAG_ID, id_pegawai);
                params.put(Konfigurasi.KEY_SURVEI_ID, id_survei);
                RequestHandler rh = new RequestHandler();
                return rh.sendPostRequest(Konfigurasi.URL_VIEW_SELECTED_WORK_REPORT, params);
            }
        }
        GetDataWorkReport ge = new GetDataWorkReport();
        ge.execute();
    }

    private void ShowDataWorkReport(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray(Konfigurasi.TAG_JSON_ARRAY);
            JSONObject c = result.getJSONObject(0);

            //Bagian I : Data Kostumer
            jenisPengerjaan = c.getString(Konfigurasi.WORKREPORT_KEY_GET_TAG_JENIS_PENGERJAAN);
            idClient = c.getString(Konfigurasi.WORKREPORT_KEY_GET_TAG_CLIENT_ID);
            namaPelanggan = c.getString(Konfigurasi.WORKREPORT_KEY_GET_TAG_NAMA_PELANGGAN);
            alamatPelanggan = c.getString(Konfigurasi.WORKREPORT_KEY_GET_TAG_ALAMAT);
            pekerjaan = c.getString(Konfigurasi.WORKREPORT_KEY_GET_TAG_PEKERJAAN);

            latLong_AlamatCustomer = c.getString(Konfigurasi.WORKREPORT_KEY_GET_LATLONG_ALAMAT_PELANGGAN);
            latLong_Consultant = c.getString(Konfigurasi.WORKREPORT_KEY_GET_LATLONG_CONSULTANT);

            //Bagian II : Pest Control
            totalHama = c.getString(Konfigurasi.PEST_KEY_GET_TAG_JENIS_HAMA);
            hamaLain = c.getString(Konfigurasi.PEST_KEY_GET_TAG_HAMA_LAINNYA);
            metodeKendaliHama = c.getString(Konfigurasi.PEST_KEY_GET_TAG_METODE_KENDALI);
            qtyLemTikus = c.getString(Konfigurasi.PEST_KEY_GET_TAG_QTY_LEMTIKUS);
            qtyPerangkapTikus = c.getString(Konfigurasi.PEST_KEY_GET_TAG_QTY_PERANGKAPTIKUS);
            qtyUmpanTikusOutdoor = c.getString(Konfigurasi.PEST_KEY_GET_TAG_QTY_UMPANOUTDOOR);
            qtyUmpanTikusIndoor = c.getString(Konfigurasi.PEST_KEY_GET_TAG_QTY_UMPANINDOOR);
            qtyPohonLalat = c.getString(Konfigurasi.PEST_KEY_GET_TAG_QTY_POHONLALAT);
            qtyBlackhole = c.getString(Konfigurasi.PEST_KEY_GET_TAG_QTY_BLACKHOLE);
            jenisFumigasi = c.getString(Konfigurasi.PEST_KEY_GET_TAG_JENIS_FUMIGASI);
            qtyFlycatcher = c.getString(Konfigurasi.PEST_KEY_GET_TAG_QTY_FLYCATCHER);
            metodeLain = c.getString(Konfigurasi.PEST_KEY_GET_TAG_METODE_LAIN);

            //Bagian III : Termite Control
            totalRayap = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_JENIS_RAYAP);
            metodeKendaliRayap = c.getString(Konfigurasi.WORKREPORT_KEY_GET_TAG_METODE_KENDALI_RAYAP);
            chemicalTermite = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_JENIS_CHEMICAL);
            qtyFipronil = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_QTY_FIPRONIL);
            qtyImidaclporid = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_QTY_IMIDACLPORID);
            qtyCypermethrin = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_QTY_CYPERMETHRIN);
            qtyDicholorphos = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_QTY_DICHLORPHOS);
            qtyBaitingAG = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_QTY_BAITING_AG);
            qtyBaitingIG = c.getString(Konfigurasi.TERMITE_KEY_GET_TAG_QTY_BAITING_IG);

            //Bagian IV : Fumigasi
            gasFumigasi = c.getString(Konfigurasi.WORKREPORT_KEY_GET_TAG_GAS_FUMIGASI);

            //Bagian V : Waktu
            waktuMulai = c.getString(Konfigurasi.WORKREPORT_KEY_GET_TAG_WAKTU_MULAI);
            // waktuSelesai = c.getString(Konfigurasi.WORKREPORT_KEY_GET_TAG_WAKTU_SELESAI);
            worker = c.getString(Konfigurasi.WORKREPORT_KEY_GET_TAG_NAMA_TEKNISI);

            //Bagian VI : Foto
            pathFotoSatu = c.getString(Konfigurasi.WORKREPORT_KEY_GET_TAG_IMG_FOTO_SATU);
            pathFotoDua = c.getString(Konfigurasi.WORKREPORT_KEY_GET_TAG_IMG_FOTO_DUA);
            pathFotoTiga = c.getString(Konfigurasi.WORKREPORT_KEY_GET_TAG_FOTO_TIGA);
            pathFotoEmpat = c.getString(Konfigurasi.WORKREPORT_KEY_GET_TAG_FOTO_EMPAT);

            //Bagian VI : TTD
            pathTTDClient = c.getString(Konfigurasi.WORKREPORT_KEY_GET_TAG_TTD_PELANGGAN);
            pathTTDConsultant = c.getString(Konfigurasi.WORKREPORT_KEY_GET_TAG_TTD_CONSULTANT);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showData();
    }

    private void showData() {
        if (jenisPengerjaan.equalsIgnoreCase("Treatment")) {
            rdb_Treatment.setChecked(true);
        } else if (jenisPengerjaan.equalsIgnoreCase("Complaint")) {
            rdb_Complaint.setChecked(true);
        } else if (jenisPengerjaan.equalsIgnoreCase("Supervisi")) {
            rdb_Supervisi.setChecked(true);
        }

        if (pekerjaan.equalsIgnoreCase("Pest Control")) {
            rdb_Pest.setChecked(true);
            tbl_KategoriPest.setVisibility(View.VISIBLE);

            //CheckBox Jenis Hama
            String[] JenisHama_Arr = totalHama.split(",");
            for (String list_hama : JenisHama_Arr) {
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
                    edt_HamaLainnya.setText(hamaLain);
                }
            }

            //CheckBox MetodeKendali yang Digunakan
            String[] MetodeKendali_Arry = metodeKendaliHama.split(",");
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
                    edt_QtyLemTikus.setText(qtyLemTikus);
                }
                if (list_metode.equalsIgnoreCase("Perangkap Tikus Massal (Rodent Trap)")) {
                    chk_PerangkapTikus.setChecked(true);
                    edt_QtyPerangkapTikus.setText(qtyPerangkapTikus);
                }
                if (list_metode.equalsIgnoreCase("Umpan Tikus (Rodent Baiting Outdoor)")) {
                    chk_UmpanTikusOutdoor.setChecked(true);
                    edt_QtyUmpanOutdoor.setText(qtyUmpanTikusOutdoor);
                }
                if (list_metode.equalsIgnoreCase("Umpan Tikus (Rodent Baiting Indoor)")) {
                    chk_UmpanTikusIndoor.setChecked(true);
                    edt_QtyUmpanIndoor.setText(qtyUmpanTikusIndoor);
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
                    edt_QtyPohonLalat.setText(qtyPohonLalat);
                }
                if (list_metode.equalsIgnoreCase("Instalasi Black Hole")) {
                    chk_BlackHole.setChecked(true);
                    edt_QtyBlackHole.setText(qtyBlackhole);
                }
                if (list_metode.equalsIgnoreCase("Fumigasi")) {
                    chk_Fumigasi.setChecked(true);
                    //Untuk Spinner Jenis Fumigasi
                    if (jenisFumigasi.equalsIgnoreCase("Phosphine (PH3)")) {
                        spr_Fumigasi.setSelection(0);
                    } else {
                        spr_Fumigasi.setSelection(1);
                    }
                }
                if (list_metode.equalsIgnoreCase("Instalasi Fly Catcher")) {
                    chk_FlyCatcher.setChecked(true);
                    edt_QtyFlyCatcher.setText(qtyFlycatcher);
                }
                if (list_metode.equalsIgnoreCase("Lainnya")) {
                    chk_MetodeLainnya.setChecked(true);
                    edt_MetodeLainnya.setText(metodeLain);
                }
            }
        } else if (pekerjaan.equalsIgnoreCase("Termite Control")) {
            rdb_Termite.setChecked(true);
            tbl_KategoriTermite.setVisibility(View.VISIBLE);

            //Spinner Jenis Rayap
            if (totalRayap.equalsIgnoreCase("Cryptotermes")) {
                spr_JenisRayap.setSelection(0);
            } else if (totalRayap.equalsIgnoreCase("Glyptotermes")) {
                spr_JenisRayap.setSelection(1);
            } else if (totalRayap.equalsIgnoreCase("Coptotermes")) {
                spr_JenisRayap.setSelection(2);
            } else if (totalRayap.equalsIgnoreCase("Schedorhinotermes")) {
                spr_JenisRayap.setSelection(3);
            } else if (totalRayap.equalsIgnoreCase("Odontotermes")) {
                spr_JenisRayap.setSelection(4);
            } else if (totalRayap.equalsIgnoreCase("Macrotermes")) {
                spr_JenisRayap.setSelection(5);
            }

            //CheckBox Metode Kendali Rayap
            String[] JenisMetodeKendali_Arry = metodeKendaliRayap.split(",");
            for (String list_metode : JenisMetodeKendali_Arry) {
                if (list_metode.equalsIgnoreCase("Spraying")) {
                    chk_Spraying.setChecked(true);
                }
                if (list_metode.equalsIgnoreCase("Drill Inject")) {
                    chk_Drill.setChecked(true);
                }
                if (list_metode.equalsIgnoreCase("Termite Baiting AG")) {
                    chk_BaitingAG.setChecked(true);
                }
                if (list_metode.equalsIgnoreCase("Termite Baiting IG")) {
                    chk_BaitingIG.setChecked(true);
                }
                if (list_metode.equalsIgnoreCase("Fumigasi")) {
                    chk_FumigasiTermiteControl.setChecked(true);
                }
            }

            //CheckBox Jenis Chemical
            String[] JenisChemical_Arry = chemicalTermite.split(",");
            for (String list_chemical : JenisChemical_Arry) {
                if (list_chemical.equalsIgnoreCase("Fipronil 50 g/l")) {
                    chk_Fipronil.setChecked(true);
                    edt_QtyFipronil.setText(qtyFipronil);
                }
                if (list_chemical.equalsIgnoreCase("Imidacloprid 200 g/l")) {
                    chk_Imidaclporid.setChecked(true);
                    edt_QtyImidaclporid.setText(qtyImidaclporid);
                }
                if (list_chemical.equalsIgnoreCase("Cypermethrin 100 g/l")) {
                    chk_Cypermethrin.setChecked(true);
                    edt_QtyCypermethrin.setText(qtyCypermethrin);
                }
                if (list_chemical.equalsIgnoreCase("Dichlorphos 200 g/l")) {
                    chk_Dichlorphos.setChecked(true);
                    edt_QtyDichlorphos.setText(qtyDicholorphos);

                }
                if (list_chemical.equalsIgnoreCase("Baiting AG")) {
                    chk_ChemicalBaitingAG.setChecked(true);
                    edt_QtyChemicalBaitingAG.setText(qtyBaitingAG);
                }
                if (list_chemical.equalsIgnoreCase("Baiting IG")) {
                    chk_ChemicalBaitingIG.setChecked(true);
                    edt_QtyChemicalBaitingIG.setText(qtyBaitingIG);
                }
            }
        } else if (pekerjaan.equalsIgnoreCase("Fumigasi")) {
            rdb_Fumigasi.setChecked(true);
            tbl_KategoriFumigasi.setVisibility(View.VISIBLE);

            //Spinner Jenis Rayap
            if (gasFumigasi.equalsIgnoreCase("Methyl Bromide")) {
                spr_GasFumigasi.setSelection(0);
            } else if (gasFumigasi.equalsIgnoreCase("Sulfur Flouride")) {
                spr_GasFumigasi.setSelection(1);
            } else if (gasFumigasi.equalsIgnoreCase("Phosphine")) {
                spr_GasFumigasi.setSelection(2);
            }
        }

        edt_ClientID.setText(idClient);
        edt_Client.setText(namaPelanggan);
        edt_AlamatClient.setText(alamatPelanggan);
        edt_Pekerja.setText(worker);
        edt_WaktuMulai.setText(waktuMulai);

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

        //Image TTD Client
        Glide.with(getApplicationContext())
                .load(Konfigurasi.url_image + pathTTDClient)
                .error(Glide.with(img_ttdPelanggan).load(R.drawable.ic_ttd_not_found))
                .into(img_ttdPelanggan);

        //Image TTD Consultant
        Glide.with(getApplicationContext())
                .load(Konfigurasi.url_image + pathTTDConsultant)
                .error(Glide.with(img_ttdConsultant).load(R.drawable.ic_ttd_not_found))
                .into(img_ttdConsultant);


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
            Intent openMaps = new Intent(UpdateWorkReport.this, OpenMaps.class);
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

    private void OpenTTD() {
        btn_DialogTTD_Pelanggan.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SignatureActivity.class);
                i.putExtra(Pesan_Extra, "TTD_PelangganWorksReport");
                startActivityForResult(i, 1);
            }
        });

        btn_DialogTTD_Consultant.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(getApplicationContext(), SignatureActivity.class);
                a.putExtra(Pesan_Extra, "TTD_ConsultantWorksReport");
                startActivityForResult(a, 2);
            }
        });

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
    }

    private void ShowTimeAndWork() {
        edt_WaktuMulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog_mulai();
            }
        });

        /*edt_WaktuSelesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog_selesai();
            }
        });*/

        rdg_JenisKerja.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton_Treatment_Update:
                        pengerjaan = "Treatment";
                        break;
                    case R.id.radioButton_Complaint_Update:
                        pengerjaan = "Complaint";
                        break;
                    case R.id.radioButton_Supervisi_Update:
                        pengerjaan = "Supervisi";
                        break;
                }
            }
        });

        rdg_Pekerjaan.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton_PestControl_Update:
                        tbl_KategoriPest.setVisibility(View.VISIBLE);
                        tbl_KategoriFumigasi.setVisibility(View.GONE);
                        tbl_KategoriTermite.setVisibility(View.GONE);
                        pilihan_kerja = "Pest Control";
                        break;

                    case R.id.radioButton_TermiteControl_Update:
                        tbl_KategoriPest.setVisibility(View.GONE);
                        tbl_KategoriFumigasi.setVisibility(View.GONE);
                        tbl_KategoriTermite.setVisibility(View.VISIBLE);
                        pilihan_kerja = "Termite Control";
                        break;

                    case R.id.radioButton_Fumigasi_Update:
                        tbl_KategoriPest.setVisibility(View.GONE);
                        tbl_KategoriFumigasi.setVisibility(View.VISIBLE);
                        tbl_KategoriTermite.setVisibility(View.GONE);
                        pilihan_kerja = "Fumigasi";
                        break;
                }
            }
        });
    }

    private void ChemicalTermite() {
        chk_Fipronil.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_QtyFipronil.setVisibility(View.VISIBLE);
                    edt_QtyFipronil.requestFocus();
                } else {
                    edt_QtyFipronil.setVisibility(View.GONE);
                    edt_QtyFipronil.setText("");
                }
            }
        });

        chk_Imidaclporid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_QtyImidaclporid.setVisibility(View.VISIBLE);
                    edt_QtyImidaclporid.requestFocus();
                } else {
                    edt_QtyImidaclporid.setVisibility(View.GONE);
                    edt_QtyImidaclporid.setText("");
                }
            }
        });

        chk_Cypermethrin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_QtyCypermethrin.setVisibility(View.VISIBLE);
                    edt_QtyCypermethrin.requestFocus();
                } else {
                    edt_QtyCypermethrin.setVisibility(View.GONE);
                    edt_QtyCypermethrin.setText("");
                }
            }
        });

        chk_Dichlorphos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_QtyDichlorphos.setVisibility(View.VISIBLE);
                    edt_QtyDichlorphos.requestFocus();
                } else {
                    edt_QtyDichlorphos.setVisibility(View.GONE);
                    edt_QtyDichlorphos.setText("");
                }
            }
        });

        chk_ChemicalBaitingAG.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_QtyChemicalBaitingAG.setVisibility(View.VISIBLE);
                    edt_QtyChemicalBaitingAG.requestFocus();
                } else {
                    edt_QtyChemicalBaitingAG.setVisibility(View.GONE);
                    edt_QtyChemicalBaitingAG.setText("");
                }
            }
        });

        chk_ChemicalBaitingIG.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_QtyChemicalBaitingIG.setVisibility(View.VISIBLE);
                    edt_QtyChemicalBaitingIG.requestFocus();
                } else {
                    edt_QtyChemicalBaitingIG.setVisibility(View.GONE);
                    edt_QtyChemicalBaitingIG.setText("");
                }
            }
        });
    }

    private void ForHamaClicked() {
        chk_Tikus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    img_MetodePengendalianHama_Pest.setVisibility(View.VISIBLE);
                    tbl_TreatmentPestControl.setVisibility(View.VISIBLE);
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
                    img_MetodePengendalianHama_Pest.setVisibility(View.VISIBLE);
                    tbl_TreatmentPestControl.setVisibility(View.VISIBLE);
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
                    img_MetodePengendalianHama_Pest.setVisibility(View.VISIBLE);
                    tbl_TreatmentPestControl.setVisibility(View.VISIBLE);
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
                    img_MetodePengendalianHama_Pest.setVisibility(View.VISIBLE);
                    tbl_TreatmentPestControl.setVisibility(View.VISIBLE);
                    show_Nyamuk();
                } else if (chk_Lalat.isChecked()) {
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
                    img_MetodePengendalianHama_Pest.setVisibility(View.VISIBLE);
                    tbl_TreatmentPestControl.setVisibility(View.VISIBLE);
                    show_Lalat();
                } else if (chk_Nyamuk.isChecked()) {
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
                    img_MetodePengendalianHama_Pest.setVisibility(View.VISIBLE);
                    tbl_TreatmentPestControl.setVisibility(View.VISIBLE);
                    show_Kutu();
                } else if (chk_Semut.isChecked() || chk_Kecoa.isChecked() || chk_Tawon.isChecked()) {
                    chk_Penyemprotan.setVisibility(View.VISIBLE);
                } else {
                    hide_Kutu();
                }
            }
        });

        chk_Tawon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    img_MetodePengendalianHama_Pest.setVisibility(View.VISIBLE);
                    tbl_TreatmentPestControl.setVisibility(View.VISIBLE);
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
                    img_MetodePengendalianHama_Pest.setVisibility(View.VISIBLE);
                    tbl_TreatmentPestControl.setVisibility(View.VISIBLE);
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
                    img_MetodePengendalianHama_Pest.setVisibility(View.VISIBLE);
                    tbl_TreatmentPestControl.setVisibility(View.VISIBLE);
                    edt_HamaLainnya.setVisibility(View.VISIBLE);
                    edt_HamaLainnya.requestFocus();
                    show_Lainnya();
                } else {
                    edt_HamaLainnya.setVisibility(View.GONE);
                    //hide_Lainnya();
                }
            }
        });

        chk_PerangkapTikus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_QtyPerangkapTikus.setVisibility(View.VISIBLE);
                    edt_QtyPerangkapTikus.requestFocus();
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
                    edt_QtyLemTikus.requestFocus();
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
                    edt_QtyUmpanIndoor.requestFocus();
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
                    edt_QtyUmpanOutdoor.requestFocus();
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
                    edt_QtyBlackHole.requestFocus();
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
                    edt_QtyFlyCatcher.requestFocus();
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
                    edt_QtyPohonLalat.requestFocus();
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
                    edt_MetodeLainnya.requestFocus();
                } else {
                    edt_MetodeLainnya.setVisibility(View.GONE);
                }
            }
        }));
    }

    private void showPictureDialog_Satu() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Pilih Aksi");

        String[] pictureDialogItems = {
                "Pilih Foto dari Galeri", "Ambil Foto dari Kamera"};

        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        if (Build.VERSION.SDK_INT >= 23) {
                            isStoragePermissionGranted();
                        } else {
                            openGaleri();
                        }
                        break;

                    case 1:
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                openKamera();
                            } else {
                                String[] permissionRequest = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                                requestPermissions(permissionRequest, 3);
                            }
                        } else {
                            openKamera();
                        }
                        break;
                }
            }
        });
        pictureDialog.show();
    }

    private void showPictureDialog_Dua() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
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
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
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
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(UpdateWorkReport.this);
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

    public void isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openGaleri();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissons, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissons, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGaleri();
        } else {
            Toast.makeText(getApplicationContext(), "Aplikasi tidak diberikan izin untuk menyimpan data kedalam penyimpanan. Sehingga aplikasi tidak dapat berjalan normal." +
                    "Mohon pertimbangkan untuk memberikan Izin akses.", Toast.LENGTH_LONG).show();
        }

        if (requestCode == 3) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                openKamera();
            } else {
                Toast.makeText(getApplicationContext(), "Aplikasi tidak diberikan izin untuk menyimpan data kedalam penyimpanan. Sehingga aplikasi tidak dapat berjalan normal." +
                        "Mohon pertimbangkan untuk memberikan Izin akses.", Toast.LENGTH_LONG).show();
            }
        }
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

        //Get Nama Pelanggan
        namaPelanggan = edt_Client.getText().toString();
        String dateNow = new SimpleDateFormat("dd-MM-yy_hh.mm.ss", Locale.getDefault()).format(new Date());

        //Untuk ttd Pelanggan dan Consultant
        if (requestCode == REQ_TTD_PELANGGAN) {
            if (resultCode == RESULT_OK) {
                //Nampilin hasil ttd untuk Pest Pelanggan
                imagepath_WorkReport_Pelanggan = data.getStringExtra("imagePath_WorkReportPelanggan");
                bitmap_WorkReport_Pelanggan = BitmapFactory.decodeFile(imagepath_WorkReport_Pelanggan);
                img_ttdPelanggan.setImageBitmap(bitmap_WorkReport_Pelanggan);

                getTTD_Pelanggan = ((BitmapDrawable) UpdateWorkReport.img_ttdPelanggan.getDrawable()).getBitmap();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                getTTD_Pelanggan.compress(Bitmap.CompressFormat.PNG, 50, stream);
                byte[] imageInByte = stream.toByteArray();
                ConvertImage_Pelanggan = Base64.encodeToString(imageInByte, Base64.DEFAULT);
                GetImageNameFromEditText_Pelanggan = "WorkReport_" + namaPelanggan + "_" + dateNow + ".png";

            }
        }

        if (requestCode == REQ_TTD_CONSULTANT) {
            if (resultCode == RESULT_OK) {
                imagepath_WorkReport_Consultant = data.getStringExtra("imagePath_WorkReportConsultant");
                bitmap_WorkReport_Consultant = BitmapFactory.decodeFile(imagepath_WorkReport_Consultant);
                img_ttdConsultant.setImageBitmap(bitmap_WorkReport_Consultant);

                getTTD_Consultant = ((BitmapDrawable) UpdateWorkReport.img_ttdConsultant.getDrawable()).getBitmap();
                ByteArrayOutputStream stream_consultant = new ByteArrayOutputStream();
                getTTD_Consultant.compress(Bitmap.CompressFormat.PNG, 50, stream_consultant);
                byte[] imageInByte_consultant = stream_consultant.toByteArray();
                ConvertImage_Consultant = Base64.encodeToString(imageInByte_consultant, Base64.DEFAULT);
                GetImageNameFromEditText_Consultant = "WorkReport_" + TampilanMenuUtama.username + "_" + dateNow + ".png";
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

                GetImageNameFromEditText_FotoSatu = "WorkReport_Foto_Satu_" + namaPelanggan + "_" + dateNow + ".png";
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

                    GetImageNameFromEditText_FotoSatu = "WorkReport_Foto_Satu_" + namaPelanggan + "_" + dateNow + ".png";

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

                GetImageNameFromEditText_FotoDua = "WorkReport_Foto_Dua_" + namaPelanggan + "_" + dateNow + ".png";
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

                    GetImageNameFromEditText_FotoDua = "WorkReport_Foto_Dua_" + namaPelanggan + "_" + dateNow + ".png";

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

               GetImageNameFromEditText_FotoTiga = "WorkReport_Foto_Tiga_" + namaPelanggan + "_" + dateNow + ".png";
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

                    GetImageNameFromEditText_FotoTiga = "WorkReport_Foto_Tiga_" + namaPelanggan + "_" + dateNow + ".png";

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

                GetImageNameFromEditText_FotoEmpat = "WorkReport_Foto_Empat_" + namaPelanggan + "_" + dateNow + ".png";
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

                    GetImageNameFromEditText_FotoEmpat = "WorkReport_Foto_Empat_" + namaPelanggan + "_" + dateNow + ".png";

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //REQUEST MAPS
        if (requestCode == REQ_PLACE_PICKER) {
            if (resultCode == RESULT_OK) {
                String lokasi = data.getStringExtra("Alamat_Lokasi");
                edt_AlamatClient.setText(lokasi);

                latLong_AlamatCustomer = data.getStringExtra("LatLong_Lokasi");
                Log.e("tag", latLong_AlamatCustomer);
            }
        }
    }

    private void showTimeDialog_mulai() {

        Calendar calendar = Calendar.getInstance();

        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                edt_WaktuMulai.setText(String.format("%02d:%02d", hourOfDay, minute));
            }
        },

                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(getApplicationContext()));

        timePickerDialog.show();
    }

    private void simpan_workReport() {
        //Bagian I - Jenis Pengerjaan
        jenis_pengerjaan = pengerjaan;
        clientID_WorkReport = edt_ClientID.getText().toString();
        namaClient_WorkReport = edt_Client.getText().toString();
        alamat_WorkReport = edt_AlamatClient.getText().toString();

        if (jenis_pengerjaan.isEmpty()) {
            rdb_Treatment.setError("Harap Memilih Jenis Pengerjaan pada Work Report");
            rdb_Treatment.requestFocusFromTouch();
            return;
        }

        if (clientID_WorkReport.isEmpty()) {
            edt_ClientID.setError("Mohon Memasukkan ID Client");
            edt_ClientID.requestFocus();
            return;
        }

        if (namaClient_WorkReport.isEmpty()) {
            edt_Client.setError("Mohon Memasukkan Nama Client");
            edt_Client.requestFocus();
            return;
        }

        if (alamat_WorkReport.isEmpty()) {
            edt_AlamatClient.setError("Harap Memasukkan Alamat");
            edt_AlamatClient.requestFocus();
            return;
        }

        //Bagian II - Pekerjaan
        pekerjaan_workReport = pilihan_kerja;

        if (pekerjaan_workReport.isEmpty()) {
            rdb_Pest.setError("Harap Memilih Jenis Pekerjaan pada Work Report");
            rdb_Pest.requestFocusFromTouch();
            return;
        }

        //<-- Bagian Pest Control -->
        //Temporary before DB
        String jenis_hama_tmp = "";
        String hama_lainnya_tmp = "";
        String metode_kendali_tmp = "";

        //For Quantity
        String qty_lemTikus_tmp = "";
        String qty_perangkapTikus_tmp = "";
        String qty_umpanTikus_tmp = "";
        String qty_umpanTikusIndoor_tmp = "";
        String qty_pohonLalat_tmp = "";
        String qty_blackHole_tmp = "";
        String jenis_fumigasi_tmp = "";
        String qty_FlyCatcher_tmp = "";
        String metodeLain_tmp = "";

        if (pekerjaan_workReport.equals("Pest Control")) {
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
                    edt_HamaLainnya.setError(("Harap Masukkan Hama Lainnya"));
                    edt_HamaLainnya.requestFocus();
                    return;
                }

                jenis_hama_tmp += "Lainnya,";
            }

            if (jenis_hama_tmp.isEmpty()) {
                chk_Kecoa.setError("Harap Memilih Minimal Satu Jenis Hama yang Dikendalikan");
                chk_Kecoa.requestFocusFromTouch();
                return;
            }

            //Untuk Metode Kendali Hama
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
        }

        //Simpan Ke DB
        jenis_hama_final = jenis_hama_tmp;
        hama_lain_final = hama_lainnya_tmp;
        metode_kendali_hama_final = metode_kendali_tmp;

        qty_lemTikus_final = qty_lemTikus_tmp;
        qty_perangkapTikus_final = qty_perangkapTikus_tmp;
        qty_umpanTikus_final = qty_umpanTikus_tmp;
        qty_umpanTikusIndoor_final = qty_umpanTikusIndoor_tmp;
        qty_pohonLalat_final = qty_pohonLalat_tmp;
        qty_blackhole_final = qty_blackHole_tmp;
        jenis_fumigasi_final = jenis_fumigasi_tmp;
        qty_flyCatcher_final = qty_FlyCatcher_tmp;
        metodeLain_final = metodeLain_tmp;

        //<-- Bagian Pest Control -->


        //<-- Bagian Termite Control -->
        String jenis_rayap_tmp = "";
        String metode_kendali_rayap_tmp = "";
        String jenis_chemical_rayap_tmp = "";

        String qty_fipronil_tmp = "";
        String qty_imidaclporid_tmp = "";
        String qty_cypermethrin_tmp = "";
        String qty_dichlorphos_tmp = "";
        String qty_baitingAG_tmp = "";
        String qty_baitingIG_tmp = "";

        if (pilihan_kerja.equals("Termite Control")) {
            //Untuk pilih Rayap
            //Save to DB
            jenis_rayap_tmp = spr_JenisRayap.getSelectedItem().toString();

            //Untuk pilih metode kendali Rayap
            if (chk_Spraying.isChecked()) {
                metode_kendali_rayap_tmp += "Spraying,";
            }

            if (chk_Drill.isChecked()) {
                metode_kendali_rayap_tmp += "Drill Inject,";
            }

            if (chk_BaitingAG.isChecked()) {
                metode_kendali_rayap_tmp += "Termite Baiting AG,";
            }

            if (chk_BaitingIG.isChecked()) {
                metode_kendali_rayap_tmp += "Termite Baiting IG,";
            }

            if (chk_FumigasiTermiteControl.isChecked()) {
                metode_kendali_rayap_tmp += "Fumigasi,";
            }

            if (metode_kendali_rayap_tmp.isEmpty()) {
                chk_Spraying.setError("Harap Memilih Minimal Satu Jenis Metode Kendali Hama Rayap");
                chk_Spraying.requestFocusFromTouch();
                return;
            }

            //Untuk Chemical
            if (chk_Fipronil.isChecked()) {
                qty_fipronil_tmp = edt_QtyFipronil.getText().toString();

                if (TextUtils.isEmpty(qty_fipronil_tmp)) {
                    edt_QtyFipronil.setError("Harap Masukkan Banyak Botol yang Dibutuhkan");
                    edt_QtyFipronil.requestFocus();
                    return;
                }
                jenis_chemical_rayap_tmp += "Fipronil 50 g/l,";
            }

            if (chk_Imidaclporid.isChecked()) {
                qty_imidaclporid_tmp = edt_QtyImidaclporid.getText().toString();

                if (TextUtils.isEmpty(qty_imidaclporid_tmp)) {
                    edt_QtyImidaclporid.setError("Harap Masukkan Banyak Botol yang Dibutuhkan");
                    edt_QtyImidaclporid.requestFocus();
                    return;
                }
                jenis_chemical_rayap_tmp += "Imidacloprid 200 g/l,";
            }

            if (chk_Cypermethrin.isChecked()) {
                qty_cypermethrin_tmp = edt_QtyCypermethrin.getText().toString();

                if (TextUtils.isEmpty(qty_cypermethrin_tmp)) {
                    edt_QtyCypermethrin.setError("Harap Masukkan Banyak Botol yang Dibutuhkan");
                    edt_QtyCypermethrin.requestFocus();
                    return;
                }
                jenis_chemical_rayap_tmp += "Cypermethrin 100 g/l,";
            }

            if (chk_Dichlorphos.isChecked()) {
                qty_dichlorphos_tmp = edt_QtyDichlorphos.getText().toString();

                if (TextUtils.isEmpty(qty_dichlorphos_tmp)) {
                    edt_QtyDichlorphos.setError("Harap Masukkan Banyak Botol yang Dibutuhkan");
                    edt_QtyDichlorphos.requestFocus();
                    return;
                }
                jenis_chemical_rayap_tmp += "Dichlorphos 200 g/l,";
            }

            if (chk_ChemicalBaitingAG.isChecked()) {
                qty_baitingAG_tmp = edt_QtyChemicalBaitingAG.getText().toString();

                if (TextUtils.isEmpty(qty_baitingAG_tmp)) {
                    edt_QtyChemicalBaitingAG.setError("Harap Masukkan Banyak Unit yang Dibutuhkan");
                    edt_QtyChemicalBaitingAG.requestFocus();
                    return;
                }
                jenis_chemical_rayap_tmp += "Baiting AG,";
            }

            if (chk_ChemicalBaitingIG.isChecked()) {
                qty_baitingIG_tmp = edt_QtyChemicalBaitingIG.getText().toString();

                if (TextUtils.isEmpty(qty_baitingIG_tmp)) {
                    edt_QtyChemicalBaitingIG.setError("Harap Masukkan Banyak Unit yang Dibutuhkan");
                    edt_QtyChemicalBaitingIG.requestFocus();
                    return;
                }
                jenis_chemical_rayap_tmp += "Baiting IG,";
            }

            if (jenis_chemical_rayap_tmp.isEmpty()) {
                chk_Fipronil.setError("Harap Memilih Minimal Satu Jenis Chemical");
                chk_Fipronil.requestFocusFromTouch();
                return;
            }
        }

        //Untuk simpan Nilai Jenis Rayap, Metode Kendali dan Chemical - Termite Control

        //Save to DB
        jenis_rayap_final = jenis_rayap_tmp;
        metode_kendali_rayap_final = metode_kendali_rayap_tmp;
        jenis_chemical_final = jenis_chemical_rayap_tmp;

        qty_fipronil_final = qty_fipronil_tmp;
        qty_imidaclporid_final = qty_imidaclporid_tmp;
        qty_cypermethrin_final = qty_cypermethrin_tmp;
        qty_dichlorphos_final = qty_dichlorphos_tmp;
        qty_baitingAG_final = qty_baitingAG_tmp;
        qty_baitingIG_final = qty_baitingIG_tmp;

        //<-- Bagian Termite Control -->

        //<-- Bagian Fumigasi -->
        String fumigasi_tmp = "";
        if (pilihan_kerja.equals("Fumigasi")) {
            fumigasi_tmp = spr_GasFumigasi.getSelectedItem().toString();
        }

        //Untuk simpan Nilai Fumigasi - Fumigasi
        final_fumigasi = fumigasi_tmp;

        //untuk Nama Pekerja
        pekerja = edt_Pekerja.getText().toString();
        if (pekerja.isEmpty()) {
            edt_Pekerja.setError("Harap Masukkan Nama Pekerja");
            edt_Pekerja.requestFocus();
            return;
        }

        //Untuk Bagian Waktu Pengerjaan
        String str_WaktuSelesai = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        waktu_mulai = edt_WaktuMulai.getText().toString();
        waktu_selesai = str_WaktuSelesai;

        if (waktu_mulai.isEmpty()) {
            edt_WaktuMulai.setError("Harap Memasukkan Waktu Mulai");
            edt_WaktuMulai.requestFocus();
            return;
        }

        /*if (waktu_selesai.isEmpty()){
            edt_WaktuSelesai.setError("Harap Memasukkan Waktu Selesai");
            edt_WaktuSelesai.requestFocus();
            return;
        }*/

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


        //Untuk Tanda Tangan
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

        //Menampilkan dialog Alert
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(true);
        builder.setTitle("Apakah Anda Yakin Data Yang Dimasukkan Telah Benar dan Tepat?");
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
                SaveToDB();
            }
        });
        builder.show();
    }

    private void SaveToDB() {
        @SuppressLint("StaticFieldLeak")
        class simpan extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog.setMessage("Menyimpan Data Survei Pest Control ke Database, Harap Tunggu...");
                pDialog.show();
            }

            @Override
            protected void onPostExecute(String s) {
                pDialog.dismiss();
                Toast.makeText(UpdateWorkReport.this, s, Toast.LENGTH_LONG).show();

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
                params.put(Konfigurasi.KEY_SAVE_PENGERJAAN_REPORT, jenis_pengerjaan);
                params.put(Konfigurasi.KEY_SAVE_ID_CLIENT_REPORT, clientID_WorkReport);
                params.put(Konfigurasi.KEY_SAVE_NAMA_PELANGGAN_REPORT, namaClient_WorkReport);
                params.put(Konfigurasi.KEY_SAVE_ALAMAT_REPORT, alamat_WorkReport);
                params.put(Konfigurasi.KEY_SAVE_PEKERJAAN_REPORT, pekerjaan_workReport);

                params.put(Konfigurasi.KEY_SAVE_LATLONG_ALAMAT_PELANGGAN, latLong_AlamatCustomer);
                params.put(Konfigurasi.KEY_SAVE_LATLONG_CONSULTANT, latLong_Consultant);

                //Bagian Pest Control
                params.put(Konfigurasi.KEY_SAVE_JENIS_HAMA, jenis_hama_final);
                params.put(Konfigurasi.KEY_SAVE_JENIS_HAMA_LAINNYA, hama_lain_final);
                params.put(Konfigurasi.KEY_SAVE_METODE_KENDALI_HAMA, metode_kendali_hama_final);
                params.put(Konfigurasi.KEY_SAVE_QTY_LEM_TIKUS, qty_lemTikus_final);
                params.put(Konfigurasi.KEY_SAVE_QTY_PERANGKAP_TIKUS, qty_perangkapTikus_final);
                params.put(Konfigurasi.KEY_SAVE_QTY_UMPAN_TIKUS_OUTDOOR, qty_umpanTikus_final);
                params.put(Konfigurasi.KEY_SAVE_QTY_UMPAN_TIKUS_INDOOR, qty_umpanTikusIndoor_final);
                params.put(Konfigurasi.KEY_SAVE_QTY_POHON_LALAT, qty_pohonLalat_final);
                params.put(Konfigurasi.KEY_SAVE_QTY_BLACK_HOLE, qty_blackhole_final);
                params.put(Konfigurasi.KEY_SAVE_JENIS_FUMIGASI, jenis_fumigasi_final);
                params.put(Konfigurasi.KEY_SAVE_QTY_FLY_CATCHER, qty_flyCatcher_final);
                params.put(Konfigurasi.KEY_SAVE_METODE_LAIN, metodeLain_final);

                //Bagian Termite Control
                params.put(Konfigurasi.KEY_SAVE_JENIS_RAYAP_TERMITE, jenis_rayap_final);
                params.put(Konfigurasi.KEY_SAVE_METODE_KENDALI_TERMITE, metode_kendali_rayap_final);
                params.put(Konfigurasi.KEY_SAVE_CHEMICAL_TERMITE, jenis_chemical_final);
                params.put(Konfigurasi.KEY_SAVE_QTY_FIPRONIL_TERMITE, qty_fipronil_final);
                params.put(Konfigurasi.KEY_SAVE_QTY_IMIDACLPORID_TERMITE, qty_imidaclporid_final);
                params.put(Konfigurasi.KEY_SAVE_QTY_CYPERMETHRIN_TERMITE, qty_cypermethrin_final);
                params.put(Konfigurasi.KEY_SAVE_QTY_DICHLORPHOS_TERMITE, qty_dichlorphos_final);
                params.put(Konfigurasi.KEY_SAVE_QTY_BAITING_AG_TERMITE, qty_baitingAG_final);
                params.put(Konfigurasi.KEY_SAVE_QTY_BAITING_IG_TERMITE, qty_baitingIG_final);

                //Bagian Fumigasi
                params.put(Konfigurasi.KEY_SAVE_FUMIGASI_REPORT, final_fumigasi);

                //Bagian Waktu
                params.put(Konfigurasi.KEY_SAVE_WAKTU_MULAI_REPORT, waktu_mulai);
                params.put(Konfigurasi.KEY_SAVE_WAKTU_SELESAI_REPORT, waktu_selesai);
                params.put(Konfigurasi.KEY_SAVE_NAMA_TEKNISI_REPORT, pekerja);

                params.put(Konfigurasi.KEY_CEK_IMG_TTD_PELANGGAN, imagepath_WorkReport_Pelanggan);
                params.put(Konfigurasi.KEY_CEK_IMG_TTD_CONSULTANT, imagepath_WorkReport_Consultant);

                //Bagian TTD
                params.put(Konfigurasi.KEY_SAVE_IMAGE_TAG_PELANGGAN_REPORT, GetImageNameFromEditText_Pelanggan);
                params.put(Konfigurasi.KEY_SAVE_IMAGE_NAME_PELANGGAN_REPORT, ConvertImage_Pelanggan);

                params.put(Konfigurasi.KEY_SAVE_IMAGE_TAG_CONSULTANT_REPORT, GetImageNameFromEditText_Consultant);
                params.put(Konfigurasi.KEY_SAVE_IMAGE_NAME_CONSULTANT_REPORT, ConvertImage_Consultant);

                params.put(Konfigurasi.KEY_TAG_ID, TampilanMenuUtama.id_pegawai);
                params.put(Konfigurasi.WORKREPORT_KEY_GET_LIST_SURVEI_ID, id_survei);

                RequestHandler rh = new RequestHandler();
                return rh.sendPostRequest(Konfigurasi.URL_UPDATE_WORK_REPORT, params);
            }
        }

        simpan save = new simpan();
        save.execute();
    }

    private void uploadFoto() {
        if (GetImageNameFromEditText_FotoSatu != null){
            getFotoSatu = ((BitmapDrawable) UpdateWorkReport.img_FotoSatu.getDrawable()).getBitmap();
            ByteArrayOutputStream stream_foto_satu = new ByteArrayOutputStream();
            getFotoSatu.compress(Bitmap.CompressFormat.PNG, 70, stream_foto_satu);
            byte[] imageInByte_foto_satu = stream_foto_satu.toByteArray();
            ConvertImage_FotoSatu = Base64.encodeToString(imageInByte_foto_satu, Base64.DEFAULT);
        }

        if(GetImageNameFromEditText_FotoDua != null){
            getFotoDua = ((BitmapDrawable) UpdateWorkReport.img_FotoDua.getDrawable()).getBitmap();
            ByteArrayOutputStream stream_foto_dua = new ByteArrayOutputStream();
            getFotoDua.compress(Bitmap.CompressFormat.PNG, 70, stream_foto_dua);
            byte[] imageInByte_foto_dua = stream_foto_dua.toByteArray();
            ConvertImage_FotoDua = Base64.encodeToString(imageInByte_foto_dua, Base64.DEFAULT);
        }

        if(GetImageNameFromEditText_FotoTiga != null){
            getFotoTiga = ((BitmapDrawable) UpdateWorkReport.img_FotoTiga.getDrawable()).getBitmap();
            ByteArrayOutputStream stream_foto_tiga = new ByteArrayOutputStream();
            getFotoTiga.compress(Bitmap.CompressFormat.PNG, 70, stream_foto_tiga);
            byte[] imageInByte_foto_tiga = stream_foto_tiga.toByteArray();
            ConvertImage_FotoTiga = Base64.encodeToString(imageInByte_foto_tiga, Base64.DEFAULT);
        }

        if(GetImageNameFromEditText_FotoEmpat != null){
            getFotoEmpat = ((BitmapDrawable) UpdateWorkReport.img_FotoEmpat.getDrawable()).getBitmap();
            ByteArrayOutputStream stream_foto_empat = new ByteArrayOutputStream();
            getFotoEmpat.compress(Bitmap.CompressFormat.PNG, 70, stream_foto_empat);
            byte[] imageInByte_foto_empat = stream_foto_empat.toByteArray();
            ConvertImage_FotoEmpat = Base64.encodeToString(imageInByte_foto_empat, Base64.DEFAULT);
        }



        @SuppressLint("StaticFieldLeak")
        class upload extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                pDialog.setMessage("Menyimpan Data Survei Pest Control ke Database, Harap Tunggu...");
                pDialog.show();
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                pDialog.dismiss();
                Toast.makeText(UpdateWorkReport.this, s, Toast.LENGTH_LONG).show();
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
                params.put(Konfigurasi.WORKREPORT_KEY_GET_LIST_SURVEI_ID, id_survei);

                RequestHandler rh = new RequestHandler();
                return rh.sendPostRequest(Konfigurasi.URL_UPDATE_PICTURE_WORK_REPORT, params);
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
                super.onPreExecute();
                pDialog.setMessage("Menghapus Data Survei Terpilih, Harap Tunggu...");
                pDialog.show();
            }

            @Override
            protected void onPostExecute(String s) {
                pDialog.dismiss();
                Toast.makeText(UpdateWorkReport.this, s, Toast.LENGTH_LONG).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateWorkReport.this);
                builder.setMessage(s)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent del_intent = new Intent(UpdateWorkReport.this, TabWorkReport.class);
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
                params.put(Konfigurasi.PEST_KEY_GET_LIST_SURVEI_ID, id_survei);

                RequestHandler rh = new RequestHandler();
                return rh.sendPostRequest(Konfigurasi.URL_DELETE_SURVEI_WORK_REPORT, params);
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
        chk_MetodeLainnya.setChecked(false);
        chk_MetodeLainnya.setVisibility(View.GONE);

        chk_PerangkapTikus.setChecked(false);
        chk_LemTikus.setChecked(false);
        chk_UmpanTikusIndoor.setChecked(false);
        chk_UmpanTikusOutdoor.setChecked(false);
        chk_Fumigasi.setChecked(false);
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
        chk_GelKecoa.setChecked(false);
        chk_LemKecoa.setChecked(false);
        chk_Fumigasi.setChecked(false);
        chk_MetodeLainnya.setChecked(false);
        chk_MetodeLainnya.setVisibility(View.GONE);
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
        chk_MetodeLainnya.setChecked(false);
        chk_MetodeLainnya.setVisibility(View.GONE);

        chk_UmpanSemut.setChecked(false);
        chk_Fumigasi.setChecked(false);
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
        chk_MetodeLainnya.setChecked(false);
        chk_MetodeLainnya.setVisibility(View.GONE);

        chk_BlackHole.setChecked(false);
        chk_Fumigasi.setChecked(false);

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
        chk_MetodeLainnya.setChecked(false);
        chk_MetodeLainnya.setVisibility(View.GONE);

        chk_Fumigasi.setChecked(false);
        chk_PohonLalat.setChecked(false);
        chk_FlyCatcher.setChecked(false);
        chk_UmpanLalat.setChecked(false);
    }

    protected void show_Kutu() {
        chk_Penyemprotan.setVisibility(View.VISIBLE);
        chk_Fumigasi.setVisibility(View.VISIBLE);
        chk_MetodeLainnya.setVisibility(View.VISIBLE);
    }

    protected void hide_Kutu() {
        chk_Penyemprotan.setVisibility(View.GONE);
        chk_Fumigasi.setVisibility(View.GONE);
        chk_MetodeLainnya.setChecked(false);
        chk_MetodeLainnya.setVisibility(View.GONE);
        chk_Fumigasi.setChecked(false);
    }

    protected void show_Tawon() {
        chk_Penyemprotan.setVisibility(View.VISIBLE);
        chk_Fumigasi.setVisibility(View.VISIBLE);
        chk_MetodeLainnya.setVisibility(View.VISIBLE);
    }

    protected void hide_Tawon() {
        chk_Penyemprotan.setVisibility(View.GONE);
        chk_Fumigasi.setVisibility(View.GONE);
        chk_MetodeLainnya.setChecked(false);
        chk_MetodeLainnya.setVisibility(View.GONE);

        chk_Fumigasi.setChecked(false);
    }

    protected void show_Cicak() {
        chk_Fumigasi.setVisibility(View.VISIBLE);
        chk_MetodeLainnya.setVisibility(View.VISIBLE);
    }

    protected void hide_Cicak() {
        chk_Fumigasi.setVisibility(View.GONE);
        chk_MetodeLainnya.setChecked(false);
        chk_MetodeLainnya.setVisibility(View.GONE);
        chk_Fumigasi.setChecked(false);
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

  /*  protected void hide_Lainnya(){
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
