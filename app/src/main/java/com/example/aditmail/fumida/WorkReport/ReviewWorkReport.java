package com.example.aditmail.fumida.WorkReport;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.aditmail.fumida.BuildConfig;
import com.example.aditmail.fumida.Settings.Konfigurasi;
import com.example.aditmail.fumida.R;
import com.example.aditmail.fumida.Settings.RequestHandler;
import com.example.aditmail.fumida.Activities.TampilanMenuUtama;
import com.example.aditmail.fumida.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReviewWorkReport extends AppCompatActivity {

    private LinearLayout linearLayout_pdf, linearLayout_foto;
    private Bitmap bitmap, bitmap_dua;

    protected Button generate_pdf, btnSaveToDB;
    protected TableLayout tbl_PestControl, tbl_TermiteControl;
    protected TableRow tbr_Fumigasi;

    ConnectivityManager conMgr;
    ProgressDialog pDialog;

    File file_workReport;
    String DIR_WORKREPORT = Environment.getExternalStorageDirectory().getPath() + "/Fumida_WorkReport/";
    String simpan_workReport;

    int success;
    //inisiasi jika berhasil
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    //untuk proses pertukaran data
    String tag_json_obj = "json_obj_req";

    TableRow tbl_helper;
    Space spc_helper;

    //-------------- Bagian I : Jenis Pekerjaan --------------
    private TextView txt_Rev_JenisPengerjaan, txt_Rev_IDClient_WorkReport, txt_Rev_NamaClient_WorkReport, txt_Rev_Alamat_WorkReport, txt_Rev_Pekerjaan_WorkReport;
    private String IDClient,namaPelanggan,pekerjaan_WorkReport,alamatPelanggan,jenisPengerjaan_WorkReport;
    private String LatLong_AlamatPelanggan, LatLong_Consultant;
    //-------------- Bagian I : Jenis Pekerjaan --------------

    //-------------- Bagian II : Pest Control --------------
    private TextView txt_Rev_JenisHama_WorkReport, txt_Rev_MetodeKendaliHama_WorkReport;
    private String total_hama,metode_kendali_hamaPest, hama_lain;
    private String qty_lemTikus, qty_perangkapTikus, qty_umpanTikusOutdoor, qty_umpanTikusIndoor, qty_pohonLalat, qty_blackhole,
            jenis_fumigasi, qty_flyCatcher, metodeLain;
    //-------------- Bagian II : Pest Control --------------

    //-------------- Bagian III : Termite Control --------------
    private TextView txt_Rev_Rayap_WorkReport, txt_Rev_MetodeKendaliRayap_WorkReport, txt_Rev_ChemicalTermite_WorkReport;
    private String total_rayap = "", total_chemical_termite = "", metode_kendali_rayap;
    private String qty_fipronil, qty_imidaclporid, qty_cypermethrin, qty_dicholorphos, qty_baitingAG, qty_baitingIG;

    //-------------- Bagian III : Termite Control --------------

    //-------------- Bagian IV : Fumigasi Control --------------
    private TextView txt_Rev_GasFumigasi_WorkReport;
    private String gasFumigasi;
    //-------------- Bagian IV : Fumigasi Control --------------

    //-------------- Bagian V : Waktu Mulai --------------
    private TextView txt_Rev_Worker_WorkReport;
    private TextView txt_Rev_WaktuMulai_WorkReport, txt_Rev_WaktuSelesai_WorkReport;
    private String worker;
    private String waktu_mulai,waktu_selesai,keterangan;

    private String dateNow;
    //-------------- Bagian V : Waktu Mulai --------------

    //-------------- Bagian VI : TTD --------------
    private TextView txt_Rev_NamaConsultant_WorkReport, txt_Rev_NamaPelanggan_WorkReport, txt_Rev_Keterangan_WorkReport;
    private ImageView ttd_Rev_Pelanggan_WorkReport, ttd_Rev_Consultant_WorkReport;
    private Bitmap getTTD_Consultant, getTTD_Pelanggan;
    private String ConvertImage_Pelanggan, ConvertImage_Consultant;
    private String GetImageNameFromEditText_Pelanggan, GetImageNameFromEditText_Consultant;
    //-------------- Bagian VI : TTD --------------

    //-------------- Bagian VII : Upload Foto --------------
    private ImageView img_FotoSatu, img_FotoDua, img_FotoTiga, img_FotoEmpat;
    private Bitmap getFotoSatu, getFotoDua, getFotoTiga, getFotoEmpat;
    private String ConvertImage_FotoSatu, ConvertImage_FotoDua, ConvertImage_FotoTiga, ConvertImage_FotoEmpat;
    private String GetImageNameFromEditText_FotoSatu, GetImageNameFromEditText_FotoDua, GetImageNameFromEditText_FotoTiga,
            GetImageNameFromEditText_FotoEmpat;
    //-------------- Bagian VII : Upload Foto --------------

    //Pembayaran
    private TextView valueTxtNominal, valueTxtKetNominal, valueKendaraan;

    //Kendaraan
    private String txtNominalPembayaran, txtKetBayaran;

    //kendaraan
    private String txtKendaraan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_work_report);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        pDialog = new ProgressDialog(ReviewWorkReport.this);
        pDialog.setCancelable(true);
        pDialog.setCanceledOnTouchOutside(false);

        dateNow = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());

        file_workReport = new File(DIR_WORKREPORT);
        if (!file_workReport.exists()){
            file_workReport.mkdir();
        }
        initData();
        getExtraIntent();
        TampilanReviewWorkReport();

        generate_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ReviewWorkReport.this);

                builder.setCancelable(true);
                builder.setTitle("Apakah Anda Yakin Data Yang Dimasukkan Telah Benar dan Tepat?");
                builder.setMessage("Apabila Yakin Tekan Tombol 'OK'!");

                builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("size", " " + linearLayout_pdf.getWidth() + "  " + linearLayout_pdf.getWidth());
                        bitmap = loadBitmapFromView(linearLayout_pdf, linearLayout_pdf.getWidth(), linearLayout_pdf.getHeight());

                        //Untuk Page 2
                        Log.e("size", " " + linearLayout_foto.getWidth() + "  " + linearLayout_foto.getWidth());
                        bitmap_dua = loadBitmapFromView_Dua(linearLayout_foto, linearLayout_foto.getWidth(), linearLayout_foto.getHeight());
                        createPdf();
                    }
                });
                builder.show();
            }
        });

        btnSaveToDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
                    //Tampilin Alert Dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(ReviewWorkReport.this);

                    builder.setCancelable(true);
                    builder.setTitle("Apakah Anda Yakin Data Yang Dimasukkan Telah Benar dan Tepat?");
                    builder.setMessage("Apabila Yakin Tekan Tombol 'OK' untuk Disimpan ke Database?!");

                    builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkIDPelanggan(IDClient);
                        }
                    });
                    builder.show();

                } else {
                    Toast.makeText(getApplicationContext(), "Tidak Ada Koneksi Internet. Mohon Coba Lagi Nanti.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initData(){
        generate_pdf = findViewById(R.id.btn_Generate);
        btnSaveToDB = findViewById(R.id.btn_SaveToDB);

        linearLayout_pdf = findViewById(R.id.LinearLayout_PDF);
        linearLayout_foto = findViewById(R.id.LinearLayout_FotoFoto);

        spc_helper = findViewById(R.id.spc_Fumigasi);
        tbl_helper = findViewById(R.id.tbl_helper);

        //Bagian I
        txt_Rev_JenisPengerjaan = findViewById(R.id.textView_JenisPengerjaan);
        txt_Rev_IDClient_WorkReport = findViewById(R.id.textView_ReviewID_WorkReport);
        txt_Rev_NamaClient_WorkReport = findViewById(R.id.textView_ReviewNamaClient_WorkReport);
        txt_Rev_Alamat_WorkReport = findViewById(R.id.textView_Alamat_WorkReport);
        txt_Rev_Pekerjaan_WorkReport = findViewById(R.id.textView_Pekerjaan_WorkReport);

        //Bagian II
        tbl_PestControl = findViewById(R.id.TableLayout_PestControl);
        txt_Rev_JenisHama_WorkReport = findViewById(R.id.textView_JenisHama_WorkReport);
        txt_Rev_MetodeKendaliHama_WorkReport = findViewById(R.id.textView_MetodeKendaliHama_WorkReport);

        //Bagian III
        tbl_TermiteControl = findViewById(R.id.TableLayout_TermiteControl);
        txt_Rev_Rayap_WorkReport = findViewById(R.id.textView_Rayap_WorkReport);
        txt_Rev_MetodeKendaliRayap_WorkReport = findViewById(R.id.textView_RayapKendali_WorkReport);
        txt_Rev_ChemicalTermite_WorkReport = findViewById(R.id.textView_Chemical_WorkReport);

        //Bagian IV
        tbr_Fumigasi =findViewById(R.id.TableRow_GasFumigasi);
        txt_Rev_GasFumigasi_WorkReport = findViewById(R.id.textView_GasFumigasi_WorkReport);

        //Bagian V
        txt_Rev_Worker_WorkReport = findViewById(R.id.textView_Nama_Worker_WorkReport);
        txt_Rev_WaktuMulai_WorkReport = findViewById(R.id.textView_WaktuMulai_WorkReport);
        txt_Rev_WaktuSelesai_WorkReport = findViewById(R.id.textView_WaktuSelesai_WorkReport);

        //Bagian VI
        txt_Rev_NamaConsultant_WorkReport = findViewById(R.id.textView_NamaConsultant_WorkReport);
        txt_Rev_NamaPelanggan_WorkReport = findViewById(R.id.textView_NamaPelanggan_WorkReport);
        txt_Rev_Keterangan_WorkReport = findViewById(R.id.textView_Keterangan_WorkReport);

        ttd_Rev_Pelanggan_WorkReport = findViewById(R.id.imageView_TTD_Pelanggan_WorkReport);
        ttd_Rev_Consultant_WorkReport = findViewById(R.id.imageView_TTD_Consultant_WorkReport);

        getTTD_Consultant = ((BitmapDrawable)FragmentFormWorkReport.img_ttdConsultant.getDrawable()).getBitmap();
        getTTD_Pelanggan = ((BitmapDrawable)FragmentFormWorkReport.img_ttdPelanggan.getDrawable()).getBitmap();

        //Bagian VII
        img_FotoSatu = findViewById(R.id.imageView_FotoSatu_Lamp);
        img_FotoDua = findViewById(R.id.imageView_FotoDua_Lamp);
        img_FotoTiga = findViewById(R.id.imageView_FotoTiga_Lamp);
        img_FotoEmpat = findViewById(R.id.imageView_FotoEmpat_Lamp);

        valueTxtNominal = findViewById(R.id.txtview_pembayaran_value);
        valueTxtKetNominal = findViewById(R.id.txtView_ket_bayar_value);
        valueKendaraan = findViewById(R.id.txtview_kendaraan_value);

        getFotoSatu = ((BitmapDrawable) FragmentFormWorkReport.img_FotoSatu.getDrawable()).getBitmap();
        getFotoDua = ((BitmapDrawable)FragmentFormWorkReport.img_FotoDua.getDrawable()).getBitmap();
        getFotoTiga = ((BitmapDrawable)FragmentFormWorkReport.img_FotoTiga.getDrawable()).getBitmap();
        getFotoEmpat = ((BitmapDrawable)FragmentFormWorkReport.img_FotoEmpat.getDrawable()).getBitmap();
    }

    private void getExtraIntent(){
        Intent intent = getIntent();
        Bundle Extra_Rev_WorkReport = intent.getExtras();

        jenisPengerjaan_WorkReport = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Rev_JenisPengerjaan);
        IDClient = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Rev_ID_Client);
        namaPelanggan = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Rev_NamaClient);
        alamatPelanggan = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Rev_Alamat);
        pekerjaan_WorkReport = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Rev_Pekerjaan);

        LatLong_Consultant = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Rev_LatLong_Consultant);
        LatLong_AlamatPelanggan = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Rev_LatLong_Alamat_Pelanggan);

        //Pest
        total_hama = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Rev_JenisHamaPest);
        hama_lain = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Rev_HamaLainnyaPest);
        metode_kendali_hamaPest = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Rev_KategoriPenangananPest);

        qty_lemTikus = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Rev_QtyLemTikus);
        qty_perangkapTikus = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Rev_QtyPerangkapTikus);
        qty_umpanTikusOutdoor = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Rev_QtyUmpanTikusOutdoor);
        qty_umpanTikusIndoor = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Rev_QtyUmpanTikusIndoor);
        qty_pohonLalat = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Rev_QtyPohonLalat);
        qty_blackhole = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Rev_QtyBlackHole);
        jenis_fumigasi = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Rev_JenisFumigasi);
        qty_flyCatcher = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Rev_QtyFlyCathcer);
        metodeLain = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Rev_MetodeLain);

        //Termite
        total_rayap = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Rev_JenisRayapTermite);
        total_chemical_termite = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Rev_ChemicalTermite);
        metode_kendali_rayap = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Rev_MetodeKendaliRayap);

        qty_fipronil = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Rev_QtyFipronil);
        qty_imidaclporid = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Rev_QtyImidaclporid);
        qty_cypermethrin = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Rev_QtyCypermethrin);
        qty_dicholorphos = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Rev_QtyDichlorphos);
        qty_baitingAG = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Rev_QtyBaitingAG);
        qty_baitingIG = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Rev_QtyBaitingIG);

        //Fumigasi
        gasFumigasi = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Rev_GasFumigasi);
        worker = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Rev_Pekerja);

        waktu_mulai = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Rev_WaktuMulai);
        waktu_selesai = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Rev_WaktuSelesai);
        keterangan= Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Rev_Keterangan);

        //pembayaran
        txtNominalPembayaran = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Nominal);
        txtKetBayaran = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Keterangan_Nominal);

        //kendaraan
        txtKendaraan = Extra_Rev_WorkReport.getString(FragmentFormWorkReport.WorkReport_Kendaraan);

        switch (pekerjaan_WorkReport){
            case "Pest Control":
                tbl_PestControl.setVisibility(View.VISIBLE);
                tbl_TermiteControl.setVisibility(View.GONE);
                tbr_Fumigasi.setVisibility(View.GONE);
                show_Pest();
                break;

            case "Termite Control":
                tbl_TermiteControl.setVisibility(View.VISIBLE);
                tbr_Fumigasi.setVisibility(View.GONE);
                tbl_PestControl.setVisibility(View.GONE);
                show_Termite();
                break;

            case "Fumigasi":
                tbr_Fumigasi.setVisibility(View.VISIBLE);
                tbl_TermiteControl.setVisibility(View.GONE);
                tbl_PestControl.setVisibility(View.GONE);
                spc_helper.getLayoutParams().height = getResources().getDimensionPixelSize(R.dimen.tbl_helper_dimens);
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void show_Pest(){
        StringBuilder parse_jenis_hama = new StringBuilder();
        String show_hama;

        //Show Jenis Hama
        String[] JenisHama_Arr = total_hama.split(",");
        for(String list_hama: JenisHama_Arr){
            if (list_hama.equalsIgnoreCase("Lainnya")){
                parse_jenis_hama.append(list_hama).append(" : ").append(hama_lain);
            } else {
                parse_jenis_hama.append(list_hama).append(", ");
            }
        }

        //Delete Comma Left Behind
        show_hama = parse_jenis_hama.toString();
        StringBuilder sb = new StringBuilder(show_hama);
        sb.deleteCharAt(sb.length()-2);
        txt_Rev_JenisHama_WorkReport.setText(sb + "\n");

        //Show Metode Kendali
        String[] MetodeKendaliHama_Arr = metode_kendali_hamaPest.split(",");
        StringBuilder parse_metode_kendali_hama = new StringBuilder();
        for(String list_metode_hama: MetodeKendaliHama_Arr){
            if (list_metode_hama.equalsIgnoreCase("Lem Tikus (Rodent Glue Trap)")){
                parse_metode_kendali_hama.append("- ").append(list_metode_hama).append(" : ").append(qty_lemTikus).append(" Buah").append("\n");
            }
            else if (list_metode_hama.equalsIgnoreCase("Perangkap Tikus Massal (Rodent Trap)")){
                parse_metode_kendali_hama.append("- ").append(list_metode_hama).append(" : ").append(qty_perangkapTikus).append(" Buah").append("\n");
            }
            else if (list_metode_hama.equalsIgnoreCase("Umpan Tikus (Rodent Baiting Outdoor)")){
                parse_metode_kendali_hama.append("- ").append(list_metode_hama).append(" : ").append(qty_umpanTikusOutdoor).append(" Buah").append("\n");
            }
            else if (list_metode_hama.equalsIgnoreCase("Umpan Tikus (Rodent Baiting Indoor)")){
                parse_metode_kendali_hama.append("- ").append(list_metode_hama).append(" : ").append(qty_umpanTikusIndoor).append(" Buah").append("\n");
            }
            else if (list_metode_hama.equalsIgnoreCase("Pohon Lalat")){
                parse_metode_kendali_hama.append("- ").append(list_metode_hama).append(" : ").append(qty_pohonLalat).append(" Buah").append("\n");
            }
            else if (list_metode_hama.equalsIgnoreCase("Instalasi Black Hole")){
                parse_metode_kendali_hama.append("- ").append(list_metode_hama).append(" : ").append(qty_blackhole).append(" Buah").append("\n");
            }
            else if (list_metode_hama.equalsIgnoreCase("Fumigasi")){
                parse_metode_kendali_hama.append("- ").append(list_metode_hama).append(" : ").append(jenis_fumigasi).append("\n");
            }
            else if (list_metode_hama.equalsIgnoreCase("Instalasi Fly Catcher")){
                parse_metode_kendali_hama.append("- ").append(list_metode_hama).append(" : ").append(qty_flyCatcher).append(" Buah").append("\n");
            }
            else if (list_metode_hama.equalsIgnoreCase("Metode Lainnya")){
                parse_metode_kendali_hama.append("- ").append(list_metode_hama).append(" : ").append(metodeLain).append("\n");
                Log.e("tag", metodeLain);
            } else {
                parse_metode_kendali_hama.append("- ").append(list_metode_hama).append("\n");
            }
        }
        txt_Rev_MetodeKendaliHama_WorkReport.setText(parse_metode_kendali_hama.toString());
    }

    @SuppressLint("SetTextI18n")
    private void show_Termite(){
        StringBuilder parse_metode_kendali_rayap = new StringBuilder();
        String show_metode;

        //Show Jenis Rayap
        txt_Rev_Rayap_WorkReport.setText(total_rayap);

        //Show Metode Kendali rayap
        String[] MetodeKendaliRayap_Arr = metode_kendali_rayap.split(",");
        for(String list_metode : MetodeKendaliRayap_Arr){
            parse_metode_kendali_rayap.append(list_metode).append(", ");
        }

        //Delete Comma Left Behind
        show_metode = parse_metode_kendali_rayap.toString();
        StringBuilder sb = new StringBuilder(show_metode);
        sb.deleteCharAt(sb.length()-2);
        txt_Rev_MetodeKendaliRayap_WorkReport.setText(sb + "\n");

        //Show Chemical
        StringBuilder parse_chemical_rayap = new StringBuilder();

        String[] ChemicalRayap_Arr = total_chemical_termite.split(",");
        for(String list_chemical : ChemicalRayap_Arr){
            if (list_chemical.equalsIgnoreCase("Fipronil 50 g/l")){
                parse_chemical_rayap.append("- ").append(list_chemical).append(" : ").append(qty_fipronil).append(" Unit").append("\n");
            }
            if (list_chemical.equalsIgnoreCase("Imidacloprid 200 g/l")){
                parse_chemical_rayap.append("- ").append(list_chemical).append(" : ").append(qty_imidaclporid).append(" Unit").append("\n");
            }
            if (list_chemical.equalsIgnoreCase("Cypermethrin 100 g/l")){
                parse_chemical_rayap.append("- ").append(list_chemical).append(" : ").append(qty_cypermethrin).append(" Unit").append("\n");
            }
            if (list_chemical.equalsIgnoreCase("Dichlorphos 200 g/l")){
                parse_chemical_rayap.append("- ").append(list_chemical).append(" : ").append(qty_dicholorphos).append(" Unit").append("\n");
            }
            if (list_chemical.equalsIgnoreCase("Baiting AG")){
                parse_chemical_rayap.append("- ").append(list_chemical).append(" : ").append(qty_baitingAG).append(" Unit").append("\n");
            }
            if (list_chemical.equalsIgnoreCase("Baiting IG")){
                parse_chemical_rayap.append("- ").append(list_chemical).append(" : ").append(qty_baitingIG).append(" Unit").append("\n");
            }
        }
        txt_Rev_ChemicalTermite_WorkReport.setText(parse_chemical_rayap.toString());
    }

    @SuppressLint("SetTextI18n")
    protected void TampilanReviewWorkReport(){
        txt_Rev_JenisPengerjaan.setText(jenisPengerjaan_WorkReport); txt_Rev_IDClient_WorkReport.setText(IDClient);
        txt_Rev_NamaClient_WorkReport.setText(namaPelanggan); txt_Rev_Alamat_WorkReport.setText(alamatPelanggan);
        txt_Rev_Pekerjaan_WorkReport.setText(pekerjaan_WorkReport);

        txt_Rev_GasFumigasi_WorkReport.setText(gasFumigasi);
        txt_Rev_Worker_WorkReport.setText(worker);

        txt_Rev_WaktuMulai_WorkReport.setText(waktu_mulai + " WIB"); txt_Rev_WaktuSelesai_WorkReport.setText(waktu_selesai + " WIB");
        txt_Rev_NamaConsultant_WorkReport.setText(TampilanMenuUtama.namaLengkap);
        txt_Rev_NamaPelanggan_WorkReport.setText(namaPelanggan); txt_Rev_Keterangan_WorkReport.setText(keterangan);

        ttd_Rev_Consultant_WorkReport.setImageBitmap(getTTD_Consultant);
        ttd_Rev_Pelanggan_WorkReport.setImageBitmap(getTTD_Pelanggan);

        //set Pembayaran
        valueTxtNominal.setText(txtNominalPembayaran);
        valueTxtKetNominal.setText(txtKetBayaran);

        valueKendaraan.setText(txtKendaraan);

        img_FotoSatu.setImageBitmap(getFotoSatu);
        img_FotoDua.setImageBitmap(getFotoDua);
        img_FotoTiga.setImageBitmap(getFotoTiga);
        img_FotoEmpat.setImageBitmap(getFotoEmpat);

    }

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);

        return b;
    }

    public static Bitmap loadBitmapFromView_Dua (View v, int width, int height){
        Bitmap a = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(a);
        v.draw(c);

        return a;
    }

    private void createPdf(){

        pDialog.setMessage("Mengkonversi Data Menjadi PDF, Harap Menunggu...");
        showDialog();

        //WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        //  Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float height = displaymetrics.heightPixels ;
        float width = displaymetrics.widthPixels ;

        int convertHeight = (int) height, convertWidth = (int) width;

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        canvas.drawPaint(paint);

        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHeight, true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0 , null);
        document.finishPage(page);

        //Halaman Dua
        PdfDocument.PageInfo pageInfo_dua = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 2).create();
        PdfDocument.Page page_dua = document.startPage(pageInfo_dua);

        Canvas canvas_dua = page_dua.getCanvas();

        Paint paint_dua = new Paint();
        canvas_dua.drawPaint(paint_dua);

        bitmap_dua = Bitmap.createScaledBitmap(bitmap_dua, convertWidth, convertHeight, true);

        paint_dua.setColor(Color.BLUE);
        canvas_dua.drawBitmap(bitmap_dua, 0, 0, null);
        document.finishPage(page_dua);

        simpan_workReport = DIR_WORKREPORT + "WorkReport_"+IDClient+"_"+dateNow+".pdf";

        File filePath;
        filePath = new File(simpan_workReport);
        try {
            hideDialog();
            document.writeTo(new FileOutputStream(filePath));

        } catch (IOException e) {
            e.printStackTrace();
            hideDialog();
            Toast.makeText(this, "Terjadi Kesalahan! " + e.toString(), Toast.LENGTH_LONG).show();
        }

        hideDialog();
        // close the document
        document.close();
        Toast.makeText(this, "PDF Form Work Report Berhasil Dibuat", Toast.LENGTH_SHORT).show();

        //Tampilin Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(ReviewWorkReport.this);

        builder.setCancelable(true);
        builder.setTitle("Apakah Anda Ingin Membuka PDF Tersebut?");
        builder.setMessage("Tekan Ya jika ingin Melihat, Jika Tidak Anda dapat melihat didalam File Manager!");

        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openGeneratedPDF();
            }
        });
        builder.show();

    }

    private void openGeneratedPDF(){
        File file = new File(simpan_workReport);
        if (file.exists()) {
            //Uri path = FileProvider.getUriForFile(file);
            Uri path = FileProvider.getUriForFile(ReviewWorkReport.this, BuildConfig.APPLICATION_ID + ".provider", file);
            Intent intent_view_pdf = new Intent(Intent.ACTION_VIEW);
            intent_view_pdf.setDataAndType(path, "application/pdf");
            intent_view_pdf.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent_view_pdf.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(intent_view_pdf);
            }
            catch(ActivityNotFoundException e) {
                Toast.makeText(ReviewWorkReport.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveToDB(){
        dateNow = new SimpleDateFormat("dd-MM-yy_hh.mm.ss", Locale.getDefault()).format(new Date());

        //Untuk Upload Foto #1
        ByteArrayOutputStream stream_foto_satu = new ByteArrayOutputStream();
        getFotoSatu.compress(Bitmap.CompressFormat.PNG, 70, stream_foto_satu);
        byte[] imageInByte_foto_satu = stream_foto_satu.toByteArray();
        ConvertImage_FotoSatu = Base64.encodeToString(imageInByte_foto_satu, Base64.DEFAULT);
        GetImageNameFromEditText_FotoSatu = "WorkReport_Foto_Satu_" + namaPelanggan + "_" + dateNow + ".png";

        Log.e("tag 1",   " " + GetImageNameFromEditText_FotoSatu);

        //Untuk Upload Foto #2
        ByteArrayOutputStream stream_foto_dua = new ByteArrayOutputStream();
        getFotoDua.compress(Bitmap.CompressFormat.PNG, 70, stream_foto_dua);
        byte[] imageInByte_foto_dua = stream_foto_dua.toByteArray();
        ConvertImage_FotoDua = Base64.encodeToString(imageInByte_foto_dua, Base64.DEFAULT);
        GetImageNameFromEditText_FotoDua = "WorkReport_Foto_Dua_" + namaPelanggan + "_" + dateNow + ".png";

        Log.e("tag 2",  " " + GetImageNameFromEditText_FotoDua);

        //Untuk Upload Foto #3
        ByteArrayOutputStream stream_foto_tiga = new ByteArrayOutputStream();
        getFotoTiga.compress(Bitmap.CompressFormat.PNG, 70, stream_foto_tiga);
        byte[] imageInByte_foto_tiga = stream_foto_tiga.toByteArray();
        ConvertImage_FotoTiga = Base64.encodeToString(imageInByte_foto_tiga, Base64.DEFAULT);
        GetImageNameFromEditText_FotoTiga = "WorkReport_Foto_Tiga_" + namaPelanggan + "_" + dateNow + ".png";

        Log.e("tag 3",  " " + GetImageNameFromEditText_FotoTiga);

        //Untuk Upload Foto #4
        ByteArrayOutputStream stream_foto_empat = new ByteArrayOutputStream();
        getFotoEmpat.compress(Bitmap.CompressFormat.PNG, 70, stream_foto_empat);
        byte[] imageInByte_foto_empat = stream_foto_empat.toByteArray();
        ConvertImage_FotoEmpat = Base64.encodeToString(imageInByte_foto_empat, Base64.DEFAULT);
        GetImageNameFromEditText_FotoEmpat = "WorkReport_Foto_Empat_" + namaPelanggan + "_" + dateNow + ".png";

        Log.e("tag 4",  " " + GetImageNameFromEditText_FotoEmpat);

        //Untuk Ambil Bitmap dari TTD Pelanggan dan Diupload
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        getTTD_Pelanggan.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] imageInByte = stream.toByteArray();
        ConvertImage_Pelanggan = Base64.encodeToString(imageInByte, Base64.DEFAULT);
        GetImageNameFromEditText_Pelanggan = "WorkReport_" + namaPelanggan + "_" + dateNow + ".png";

        //Untuk Ambil Bitmap dari TTD Consultant dan Diupload
        ByteArrayOutputStream stream_consultant = new ByteArrayOutputStream();
        getTTD_Consultant.compress(Bitmap.CompressFormat.PNG, 50, stream_consultant);
        byte[] imageInByte_consultant = stream_consultant.toByteArray();
        ConvertImage_Consultant = Base64.encodeToString(imageInByte_consultant, Base64.DEFAULT);
        GetImageNameFromEditText_Consultant = "WorkReport_" + TampilanMenuUtama.username + "_" + dateNow + ".png";


        @SuppressLint("StaticFieldLeak")
        class simpan extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                pDialog.setMessage("Menyimpan Data ke Database, Harap Tunggu...");
                showDialog();
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                hideDialog();
                Toast.makeText(ReviewWorkReport.this, s, Toast.LENGTH_LONG).show();
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();

                //Bagian I : Data Kostumer
                params.put(Konfigurasi.KEY_SAVE_PENGERJAAN_REPORT, jenisPengerjaan_WorkReport);
                params.put(Konfigurasi.KEY_SAVE_ID_CLIENT_REPORT, IDClient);
                params.put(Konfigurasi.KEY_SAVE_NAMA_PELANGGAN_REPORT, namaPelanggan);
                params.put(Konfigurasi.KEY_SAVE_ALAMAT_REPORT, alamatPelanggan);
                params.put(Konfigurasi.KEY_SAVE_PEKERJAAN_REPORT, pekerjaan_WorkReport);

                //Bagian Pest Control
                params.put(Konfigurasi.KEY_SAVE_JENIS_HAMA, total_hama);
                params.put(Konfigurasi.KEY_SAVE_JENIS_HAMA_LAINNYA, hama_lain);
                params.put(Konfigurasi.KEY_SAVE_METODE_KENDALI_HAMA, metode_kendali_hamaPest);
                params.put(Konfigurasi.KEY_SAVE_QTY_LEM_TIKUS, qty_lemTikus);
                params.put(Konfigurasi.KEY_SAVE_QTY_PERANGKAP_TIKUS, qty_perangkapTikus);
                params.put(Konfigurasi.KEY_SAVE_QTY_UMPAN_TIKUS_OUTDOOR, qty_umpanTikusOutdoor);
                params.put(Konfigurasi.KEY_SAVE_QTY_UMPAN_TIKUS_INDOOR, qty_umpanTikusIndoor);
                params.put(Konfigurasi.KEY_SAVE_QTY_POHON_LALAT, qty_pohonLalat);
                params.put(Konfigurasi.KEY_SAVE_QTY_BLACK_HOLE, qty_blackhole);
                params.put(Konfigurasi.KEY_SAVE_JENIS_FUMIGASI, jenis_fumigasi);
                params.put(Konfigurasi.KEY_SAVE_QTY_FLY_CATCHER, qty_flyCatcher);
                params.put(Konfigurasi.KEY_SAVE_METODE_LAIN, metodeLain);

                //Bagian Termite Control
                params.put(Konfigurasi.KEY_SAVE_JENIS_RAYAP_TERMITE, total_rayap);
                params.put(Konfigurasi.KEY_SAVE_METODE_KENDALI_TERMITE, metode_kendali_rayap);
                params.put(Konfigurasi.KEY_SAVE_CHEMICAL_TERMITE, total_chemical_termite);
                params.put(Konfigurasi.KEY_SAVE_QTY_FIPRONIL_TERMITE, qty_fipronil);
                params.put(Konfigurasi.KEY_SAVE_QTY_IMIDACLPORID_TERMITE, qty_imidaclporid);
                params.put(Konfigurasi.KEY_SAVE_QTY_CYPERMETHRIN_TERMITE, qty_cypermethrin);
                params.put(Konfigurasi.KEY_SAVE_QTY_DICHLORPHOS_TERMITE, qty_dicholorphos);
                params.put(Konfigurasi.KEY_SAVE_QTY_BAITING_AG_TERMITE, qty_baitingAG);
                params.put(Konfigurasi.KEY_SAVE_QTY_BAITING_IG_TERMITE, qty_baitingIG);

                //Bagian Fumigasi
                params.put(Konfigurasi.KEY_SAVE_FUMIGASI_REPORT, gasFumigasi);

                //Bagian Waktu
                params.put(Konfigurasi.KEY_SAVE_WAKTU_MULAI_REPORT, waktu_mulai);
                params.put(Konfigurasi.KEY_SAVE_WAKTU_SELESAI_REPORT, waktu_selesai);
                params.put(Konfigurasi.KEY_SAVE_NAMA_TEKNISI_REPORT, worker);

                //Bagian Foto
                //Issues in HERE...
                params.put(Konfigurasi.KEY_SAVE_TAG_FOTO_PERTAMA_REPORT, GetImageNameFromEditText_FotoSatu);
                params.put(Konfigurasi.KEY_SAVE_NAME_FOTO_PERTAMA_REPORT, ConvertImage_FotoSatu);

                params.put(Konfigurasi.KEY_SAVE_TAG_FOTO_KEDUA_REPORT, GetImageNameFromEditText_FotoDua);
                params.put(Konfigurasi.KEY_SAVE_NAME_FOTO_KEDUA_REPORT, ConvertImage_FotoDua);

                params.put(Konfigurasi.KEY_SAVE_TAG_FOTO_KETIGA_REPORT, GetImageNameFromEditText_FotoTiga);
                params.put(Konfigurasi.KEY_SAVE_NAME_FOTO_KETIGA_REPORT, ConvertImage_FotoTiga);

                params.put(Konfigurasi.KEY_SAVE_TAG_FOTO_KEEMPAT_REPORT, GetImageNameFromEditText_FotoEmpat);
                params.put(Konfigurasi.KEY_SAVE_NAME_FOTO_KEEMPAT_REPORT, ConvertImage_FotoEmpat);

                //Bagian TTD
                //untuk Pelanggan
                params.put(Konfigurasi.KEY_SAVE_IMAGE_TAG_PELANGGAN_REPORT, GetImageNameFromEditText_Pelanggan);
                params.put(Konfigurasi.KEY_SAVE_IMAGE_NAME_PELANGGAN_REPORT, ConvertImage_Pelanggan);

                //untuk Consultant
                params.put(Konfigurasi.KEY_SAVE_IMAGE_TAG_CONSULTANT_REPORT, GetImageNameFromEditText_Consultant);
                params.put(Konfigurasi.KEY_SAVE_IMAGE_NAME_CONSULTANT_REPORT, ConvertImage_Consultant);

                params.put(Konfigurasi.KEY_SAVE_LATLONG_ALAMAT_PELANGGAN, LatLong_AlamatPelanggan);
                params.put(Konfigurasi.KEY_SAVE_LATLONG_CONSULTANT, LatLong_Consultant);

                params.put(Konfigurasi.KEY_TAG_ID, TampilanMenuUtama.id_pegawai);
                params.put(Konfigurasi.KEY_GET_NAMA, TampilanMenuUtama.namaLengkap);

                params.put(Konfigurasi.KEY_PEMBAYARAN, txtNominalPembayaran);
                params.put(Konfigurasi.KEY_PEMBAYARAN_KET, txtKetBayaran);
                params.put(Konfigurasi.KEY_KENDARAAN, txtKendaraan);

                RequestHandler rh = new RequestHandler();
                return rh.sendPostRequest(Konfigurasi.URL_SIMPAN_WORK_REPORTS, params);
            }
        }
        simpan save = new simpan();
        save.execute();
    }

    private void checkIDPelanggan(final String idPelanggan){
        pDialog.setMessage("Melakukan Validasi ID Pelanggan, Harap Menunggu...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, Konfigurasi.URL_CHECK_ID_PELANGGAN_WORK_REPORT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("tag", "Check Response: " + response);
                hideDialog();

                try {
                    //select untuk nyimpen data yg ingin di retrieve
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        hideDialog();
                        AlertDialog.Builder builder = new AlertDialog.Builder(ReviewWorkReport.this);
                        builder.setMessage(jObj.getString(TAG_MESSAGE))
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        hideDialog();
                        saveToDB();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            //jika error
            public void onErrorResponse(VolleyError error) {
                Log.e("tag", "Check Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put(Konfigurasi.KEY_CHECK_ID_PELANGGAN, idPelanggan);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
        hideDialog();
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
