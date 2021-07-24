package com.example.aditmail.fumida.TermiteControl;

import android.annotation.SuppressLint;
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
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.os.AsyncTask;

import android.util.Base64;

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

import java.text.NumberFormat;
import java.util.HashMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class ReviewSurveiTermiteControl extends AppCompatActivity {

    ConnectivityManager conMgr;
    ProgressDialog pDialog;

    File file_termite;
    String DIR_TERMITE = Environment.getExternalStorageDirectory().getPath() + "/Fumida_TermiteControl/";
    String simpan_termite;

    int success;
    //inisiasi jika berhasil
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    //untuk proses pertukaran data
    String tag_json_obj = "json_obj_req";

    protected Button generate_pdf, btnSaveToDB;
    private LinearLayout linearLayout_pdf, linearLayout_foto;
    private Bitmap bitmap, bitmap_dua;

    //Bagian 1: Data Diri
    private TextView txt_Rev_IDClient_Termite, txt_Rev_NamaClient_Termite, txt_Rev_Kategori_Termite, txt_Rev_Alamat_Termite,
            txt_Rev_Hp_Termite, txt_Rev_Email_Termite;

    private String IDClient, namaPelanggan, kategoriTempatPelanggan, alamatPelanggan, hpPelanggan, Email_DB;
    private String LatLong_AlamatPelanggan, LatLong_Consultant;

    //Bagian 2: Rayap dan Kategori, Chemical
    private TextView txt_Rayap_Termite, txt_KategoriPenanganan_Termite, txt_Chemical_Termite;
    private String total_rayap, kategori_penanganan, Chemical_DB;
    private String QtyFipronil_DB, QtyImidaclporid_DB, QtyCypermethrin_DB,
            QtyDichlorphos_DB, QtyBaitingAG_DB, QtyBaitingIG_DB;

    //Bagian 3: Metode Kendali dan Luas
    private TextView txt_Rev_MetodeKendali_Termite, txt_Rev_Indoor_Termite, txt_Rev_Outdoor_Termite;
    String MetodeKendali_DB, Indoor_DB, Outdoor_DB;

    //Bagian 4: Lantai...
    protected TextView txt_Rev_LantaiBangunan_Termite, txt_Tulisan_LantaiBangunan;
    private String LantaiBangunan_DB, QtyKeramik_DB, Keramik_Lain_DB, QtyGranito_DB, Granito_Lain_DB,
            QtyMarmer_DB, Marmer_Lain_DB, QtyTeraso_DB, Teraso_Lain_DB, QtyGranit_DB, Granit_Lain_DB;

    //Bagian 5: Penawaran, Status dan Catatan
    private TextView txt_Rev_Penawaran_Termite, txt_Rev_TotalHarga_Termite;
    private TextView txt_Rev_Status_Termite, txt_Catatan_Termite, txt_Rev_NamaPelanggan_Termite, txt_NamaConsultant_Termite,
            txt_Rev_Keterangan_Termite;

    private String PenawaranHarga_DB, TotalHarga_DB;
    private String status_kerjasama_kontrak, Catatan_DB, keterangan;
    private String dateNow;

    //Bagian 6: TTD
    private ImageView ttd_Rev_Pelanggan_Termite, ttd_Rev_Consultant_Termite;
    protected Bitmap getTTD_Consultant, getTTD_Pelanggan;

    private String ConvertImage_Pelanggan, ConvertImage_Consultant;
    private String GetImageNameFromEditText_Pelanggan, GetImageNameFromEditText_Consultant;

    //Bagian 7 : Foto Lampiran
    private ImageView img_FotoSatu, img_FotoDua, img_FotoTiga, img_FotoEmpat;
    protected Bitmap getFotoSatu, getFotoDua, getFotoTiga, getFotoEmpat;

    private String ConvertImage_FotoSatu, ConvertImage_FotoDua, ConvertImage_FotoTiga, ConvertImage_FotoEmpat;
    private String GetImageNameFromEditText_FotoSatu, GetImageNameFromEditText_FotoDua, GetImageNameFromEditText_FotoTiga,
            GetImageNameFromEditText_FotoEmpat;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_survei_termite_control);

        pDialog = new ProgressDialog(ReviewSurveiTermiteControl.this);
        pDialog.setCancelable(true);
        pDialog.setCanceledOnTouchOutside(false);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        file_termite = new File(DIR_TERMITE);
        if (!file_termite.exists()) {
            file_termite.mkdir();
        }

        generate_pdf = findViewById(R.id.btn_Generate_Termite);
        btnSaveToDB = findViewById(R.id.btn_SaveToDB);

        linearLayout_pdf = findViewById(R.id.LinearLayout_PDF);
        linearLayout_foto = findViewById(R.id.LinearLayout_FotoTermite);

        //Bagian I
        txt_Rev_IDClient_Termite = findViewById(R.id.textView_ReviewID_Termite);
        txt_Rev_NamaClient_Termite = findViewById(R.id.textView_ReviewNamaClient_Termite);
        txt_Rev_Kategori_Termite = findViewById(R.id.textView_Kategori_Termite);
        txt_Rev_Alamat_Termite = findViewById(R.id.textView_Alamat_Termite);
        txt_Rev_Hp_Termite = findViewById(R.id.textView_Hp_Termite);
        txt_Rev_Email_Termite = findViewById(R.id.textView_Email_Termite);

        //Bagian II
        txt_Rayap_Termite = findViewById(R.id.textView_Rayap_Termite);
        txt_KategoriPenanganan_Termite = findViewById(R.id.textView_KategoriPenanganan_Termite);
        txt_Chemical_Termite = findViewById(R.id.textView_Chemical_Termite);

        //Bagian III
        txt_Rev_MetodeKendali_Termite = findViewById(R.id.textView_RayapKendali_Termite);
        txt_Rev_Indoor_Termite = findViewById(R.id.textView_Indoor_Termite);
        txt_Rev_Outdoor_Termite = findViewById(R.id.textView_Outdoor_Termite);

        //Bagian IV
        txt_Rev_LantaiBangunan_Termite = findViewById(R.id.textView_LantaiBangunan_Termite);
        txt_Tulisan_LantaiBangunan = findViewById(R.id.textView_LantaiBangunan);

        //Bagian V
        txt_Rev_Penawaran_Termite = findViewById(R.id.textView_Penawaran_Termite);
        txt_Rev_TotalHarga_Termite = findViewById(R.id.textView_TotalHarga_Termite);
        txt_Rev_Status_Termite = findViewById(R.id.textView_Status_Termite);
        txt_Catatan_Termite = findViewById(R.id.textView_Catatan_Termite);

        //Bagian VI
        txt_Rev_NamaPelanggan_Termite = findViewById(R.id.textView_NamaPelanggan_Termite);
        txt_NamaConsultant_Termite = findViewById(R.id.textView_NamaConsultant_Termite);
        txt_Rev_Keterangan_Termite = findViewById(R.id.textView_Keterangan_Termite);

        //Bagian VII
        ttd_Rev_Consultant_Termite = findViewById(R.id.imageView_TTD_Consultant_Termite);
        ttd_Rev_Pelanggan_Termite = findViewById(R.id.imageView_TTD_Pelanggan_Termite);

        getTTD_Consultant = ((BitmapDrawable) FragmentFormTermite.img_ttdConsultant.getDrawable()).getBitmap();
        getTTD_Pelanggan = ((BitmapDrawable) FragmentFormTermite.img_ttdPelanggan.getDrawable()).getBitmap();

        //Bagian VIII
        img_FotoSatu = findViewById(R.id.imageView_FotoSatu_Lamp_Termite);
        img_FotoDua = findViewById(R.id.imageView_FotoDua_Lamp_Termite);
        img_FotoTiga = findViewById(R.id.imageView_FotoTiga_Lamp_Termite);
        img_FotoEmpat = findViewById(R.id.imageView_FotoEmpat_Lamp_Termite);

        getFotoSatu = ((BitmapDrawable) FragmentFormTermite.img_FotoSatu.getDrawable()).getBitmap();
        getFotoDua = ((BitmapDrawable)FragmentFormTermite.img_FotoDua.getDrawable()).getBitmap();
        getFotoTiga = ((BitmapDrawable)FragmentFormTermite.img_FotoTiga.getDrawable()).getBitmap();
        getFotoEmpat = ((BitmapDrawable)FragmentFormTermite.img_FotoEmpat.getDrawable()).getBitmap();

        Intent intent = getIntent();
        Bundle Extra_Rev_Termite = intent.getExtras();

        //Bagian 1: Data Diri
        IDClient = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_ID_Client_Termite);
        namaPelanggan = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_Nama_Client_Termite);
        kategoriTempatPelanggan = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_Kategori_Client_Termite);
        alamatPelanggan = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_Alamat_Client_Termite);
        hpPelanggan = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_Hp_Client_Termite);
        Email_DB = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_Email_DB);

        LatLong_Consultant = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_LatLong_Consultant);
        LatLong_AlamatPelanggan = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_LatLong_Alamat_Pelanggan);

        //Bagian 2: Jenis Rayap dan Kategori Penanganan
        total_rayap = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_Rayap_Client);
        kategori_penanganan = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_KategoriPenanganan_Termite);

        //Bagian 3: Metode Kendali dan Chemical
        MetodeKendali_DB = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_MetodeKendali_DB);
        Chemical_DB = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_Chemical_DB);

        QtyFipronil_DB = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_Qty_Fipronil_DB);
        QtyImidaclporid_DB = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_Qty_Imidaclporid_DB);
        QtyCypermethrin_DB = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_Qty_Cypermethrin_DB);
        QtyDichlorphos_DB = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_Qty_Dichlorphos_DB);
        QtyBaitingAG_DB = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_Qty_BaitingAG_DB);
        QtyBaitingIG_DB = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_Qty_BaitingIG_DB);

        //Bagian 4: Luas Indoor Outdoor
        Indoor_DB = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_Indoor_DB);
        Outdoor_DB = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_Outdoor_DB);

        //Bagian 5: Lantai Bangunan (Pasca Konstruksi)
        LantaiBangunan_DB = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_LantaiBangunan_DB);

        QtyKeramik_DB = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_Qty_Keramik_DB);
        Keramik_Lain_DB = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_Keramik_Lain_DB);

        QtyGranito_DB = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_Qty_Granito_DB);
        Granito_Lain_DB = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_Granito_Lain_DB);

        QtyMarmer_DB = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_Qty_Marmer_DB);
        Marmer_Lain_DB = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_Marmer_Lain_DB);

        QtyTeraso_DB = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_Qty_Teraso_DB);
        Teraso_Lain_DB = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_Teraso_Lain_DB);

        QtyGranit_DB = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_Qty_Granit_DB);
        Granit_Lain_DB = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_Granit_Lain_DB);

        //Bagian 6: Harga, Status dan Catatan
        PenawaranHarga_DB = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_PenawaranHarga_DB);
        TotalHarga_DB = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_TotalHarga_DB);
        Catatan_DB = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_Catatan_DB);
        status_kerjasama_kontrak = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_Status_Termite);

        //Bagian 7: Keterangan PDF
        keterangan = Extra_Rev_Termite.getString(FragmentFormTermite.TermiteRev_Keterangan_Termite);

        switch (status_kerjasama_kontrak) {
            case "Berhasil":
                txt_Rev_Status_Termite.setBackgroundColor(getResources().getColor(R.color.Berhasil));
                break;
            case "Menunggu":
                txt_Rev_Status_Termite.setBackgroundColor(getResources().getColor(R.color.Menunggu));
                break;
            case "Gagal":
                txt_Rev_Status_Termite.setBackgroundColor(getResources().getColor(R.color.Gagal));
                break;
        }

        if (kategori_penanganan.equals("Anti Rayap Pasca-Konstruksi GARANSI 3 TAHUN")) {
            txt_Rev_LantaiBangunan_Termite.setVisibility(View.VISIBLE);
            txt_Tulisan_LantaiBangunan.setVisibility(View.VISIBLE);
        } else if (kategori_penanganan.equals("Anti Rayap Pra-Konstruksi GARANSI 5 TAHUN")) {
            txt_Rev_LantaiBangunan_Termite.setVisibility(View.GONE);
            txt_Tulisan_LantaiBangunan.setVisibility(View.GONE);
        }

        dateNow = new SimpleDateFormat("dd-MM-yy_hh.mm.ss", Locale.getDefault()).format(new Date());
        TampilReviewTermite();

        generate_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Tampilin Alert Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ReviewSurveiTermiteControl.this);

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
                    AlertDialog.Builder builder = new AlertDialog.Builder(ReviewSurveiTermiteControl.this);

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

    @SuppressLint("SetTextI18n")
    protected void TampilReviewTermite() {
        txt_Rev_IDClient_Termite.setText(IDClient);
        txt_Rev_NamaClient_Termite.setText(namaPelanggan);
        txt_Rev_Kategori_Termite.setText(kategoriTempatPelanggan);
        txt_Rev_Alamat_Termite.setText(alamatPelanggan);
        txt_Rev_Hp_Termite.setText(hpPelanggan);

        if (Email_DB.equals("")){
            txt_Rev_Email_Termite.setText("-");
        }else {
            txt_Rev_Email_Termite.setText(Email_DB);
        }

        txt_Rayap_Termite.setText(total_rayap);
        txt_KategoriPenanganan_Termite.setText(kategori_penanganan);

        //Show Metode Kendali
        String parse_metode_kendali_rayap = "";
        StringBuilder sb_metode = new StringBuilder(parse_metode_kendali_rayap);

        String[] MetodeKendaliRayap_Arr = MetodeKendali_DB.split(",");
        for(String list_metode : MetodeKendaliRayap_Arr){
            sb_metode.append(list_metode).append(", ");
        }
        sb_metode.deleteCharAt(sb_metode.length()-2);
        txt_Rev_MetodeKendali_Termite.setText(sb_metode + "\n");

        //Show Chemical
        String parse_chemical_rayap = "";
        StringBuilder sb_chemical = new StringBuilder(parse_chemical_rayap);

        String[] ChemicalRayap_Arr = Chemical_DB.split(",");
        for(String list_chemical : ChemicalRayap_Arr){
            if (list_chemical.equalsIgnoreCase("Fipronil 50 g/l")){
                sb_chemical.append("- ").append(list_chemical).append(" : ").append(QtyFipronil_DB).append(" Unit").append("\n");
            }
            if (list_chemical.equalsIgnoreCase("Imidacloprid 200 g/l")){
                sb_chemical.append("- ").append(list_chemical).append(" : ").append(QtyImidaclporid_DB).append(" Unit").append("\n");
            }
            if (list_chemical.equalsIgnoreCase("Cypermethrin 100 g/l")){
                sb_chemical.append("- ").append(list_chemical).append(" : ").append(QtyCypermethrin_DB).append(" Unit").append("\n");
            }
            if (list_chemical.equalsIgnoreCase("Dichlorphos 200 g/l")){
                sb_chemical.append("- ").append(list_chemical).append(" : ").append(QtyDichlorphos_DB).append(" Unit").append("\n");
            }
            if (list_chemical.equalsIgnoreCase("Baiting AG")){
                sb_chemical.append("- ").append(list_chemical).append(" : ").append(QtyBaitingAG_DB).append(" Unit").append("\n");
            }
            if (list_chemical.equalsIgnoreCase("Baiting IG")){
                sb_chemical.append("- ").append(list_chemical).append(" : ").append(QtyBaitingIG_DB).append(" Unit").append("\n");
            }
        }
        txt_Chemical_Termite.setText(sb_chemical);

        txt_Rev_Indoor_Termite.setText(Indoor_DB + getString(R.string.text_m2));

        if(Outdoor_DB.equals("")){
            txt_Rev_Outdoor_Termite.setText("-");
        }else {
            txt_Rev_Outdoor_Termite.setText(Outdoor_DB + getString(R.string.text_m2));
        }

        //Untuk Lantai Bangunan
        String parse_lantai_bangunan = "";
        StringBuffer sb_lantai = new StringBuffer(parse_lantai_bangunan);

        //Parse Ukuran Keramik
        String parse_keramik = "";
        StringBuilder sb_keramik = new StringBuilder(parse_keramik);
        String[] Keramik_Arr = QtyKeramik_DB.split(",");
        for(String list_keramik : Keramik_Arr){
            sb_keramik.append(list_keramik).append(", ");
        }
        sb_keramik.deleteCharAt(sb_keramik.length()-2);

        //Parse Ukuran Keramik
        String parse_granito = "";
        StringBuilder sb_granito = new StringBuilder(parse_granito);
        String[] Granito_Arr = QtyGranito_DB.split(",");
        for(String list_granito : Granito_Arr){
            sb_granito.append(list_granito).append(", ");
        }
        sb_granito.deleteCharAt(sb_granito.length()-2);

        //Parse Ukuran Marmer
        String parse_marmer = "";
        StringBuilder sb_marmer = new StringBuilder(parse_marmer);
        String[] Marmer_Arr = QtyMarmer_DB.split(",");
        for(String list_marmer : Marmer_Arr){
            sb_marmer.append(list_marmer).append(", ");
        }
        sb_marmer.deleteCharAt(sb_marmer.length()-2);

        //Parse Ukuran Teraso
        String parse_teraso = "";
        StringBuilder sb_teraso = new StringBuilder(parse_teraso);
        String[] Teraso_Arr = QtyTeraso_DB.split(",");
        for(String list_teraso : Teraso_Arr){
            sb_teraso.append(list_teraso).append(", ");
        }
        sb_teraso.deleteCharAt(sb_teraso.length()-2);

        //Parse Ukuran Granit
        String parse_granit = "";
        StringBuilder sb_granit = new StringBuilder(parse_granit);
        String[] Granit_Arr = QtyGranit_DB.split(",");
        for(String list_granit : Granit_Arr){
            sb_granit.append(list_granit).append(", ");
        }
        sb_granit.deleteCharAt(sb_granit.length()-2);

        String[] LantaiBangunan_Arr = LantaiBangunan_DB.split(",");
        for(String list_lantai : LantaiBangunan_Arr){
            if (list_lantai.equalsIgnoreCase("Keramik")){
                if (!Keramik_Lain_DB.isEmpty()){
                    sb_lantai.append("- ").append(list_lantai).append("\t: ( ").append(sb_keramik).append(",").append(Keramik_Lain_DB).append(" )").append("\n");
                }else {
                    sb_lantai.append("- ").append(list_lantai).append("\t: ( ").append(sb_keramik).append(")").append("\n");
                }
            }
            else if (list_lantai.equalsIgnoreCase("Granit Tile")){
                if (!Granito_Lain_DB.isEmpty()){
                    sb_lantai.append("- ").append(list_lantai).append("\t: ( ").append(sb_granito).append(",").append(Granito_Lain_DB).append(" )").append("\n");
                }else {
                    sb_lantai.append("- ").append(list_lantai).append("\t\t: ( ").append(sb_granito).append(")").append("\n");
                }
            }
            else if (list_lantai.equalsIgnoreCase("Marmer")){
                if (!Marmer_Lain_DB.isEmpty()){
                    sb_lantai.append("- ").append(list_lantai).append("\t: ( ").append(sb_marmer).append(",").append(Marmer_Lain_DB).append(" )").append("\n");
                }else {
                    sb_lantai.append("- ").append(list_lantai).append("\t: ( ").append(sb_marmer).append(")").append("\n");
                }
            }
            else if (list_lantai.equalsIgnoreCase("Teraso")){
                if (!Teraso_Lain_DB.isEmpty()){
                    sb_lantai.append("- ").append(list_lantai).append("\t: ( ").append(sb_teraso).append(",").append(Teraso_Lain_DB).append(" )").append("\n");
                }else {
                    sb_lantai.append("- ").append(list_lantai).append("\t\t: ( ").append(sb_teraso).append(")").append("\n");
                }
            }
            else if (list_lantai.equalsIgnoreCase("Granit")){
                if (!Granit_Lain_DB.isEmpty()){
                    sb_lantai.append("- ").append(list_lantai).append("\t: ( ").append(sb_granit).append(",").append(Granit_Lain_DB).append(" )").append("\n");
                }else {
                    sb_lantai.append("- ").append(list_lantai).append("\t\t\t: ( ").append(sb_granit).append(")").append("\n");
                }
            }
            else{
                sb_lantai.append("- ").append(list_lantai).append("\n");
            }
        }
        txt_Rev_LantaiBangunan_Termite.setText(sb_lantai);

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        double penawaran;
        penawaran = Double.parseDouble(PenawaranHarga_DB);

        txt_Rev_Penawaran_Termite.setText(formatRupiah.format(penawaran));
        txt_Rev_TotalHarga_Termite.setText(TotalHarga_DB);

        txt_Rev_Status_Termite.setText(status_kerjasama_kontrak);
        txt_Catatan_Termite.setText(Catatan_DB);

        txt_Rev_NamaPelanggan_Termite.setText(namaPelanggan);
        txt_NamaConsultant_Termite.setText(TampilanMenuUtama.namaLengkap);
        txt_Rev_Keterangan_Termite.setText(keterangan);

        ttd_Rev_Pelanggan_Termite.setImageBitmap(getTTD_Pelanggan);
        ttd_Rev_Consultant_Termite.setImageBitmap(getTTD_Consultant);

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

    public static Bitmap loadBitmapFromView_Dua(View v, int width, int height) {
        Bitmap a = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(a);
        v.draw(c);

        return a;
    }

    private void createPdf() {
        pDialog.setMessage("Mengkonversi Data Menjadi PDF, Harap Menunggu...");
        showDialog();

        //WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        //  Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float height = displaymetrics.heightPixels;
        float width = displaymetrics.widthPixels;

        int convertHeight = (int) height, convertWidth = (int) width;

        PdfDocument document = new PdfDocument();

        //Halaman 1
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        canvas.drawPaint(paint);

        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHeight, true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);

        //Halaman Kedua...
        PdfDocument.PageInfo pageInfo_dua = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 2).create();
        PdfDocument.Page page_dua = document.startPage(pageInfo_dua);

        Canvas canvas_dua = page_dua.getCanvas();

        Paint paint_dua = new Paint();
        canvas_dua.drawPaint(paint_dua);

        bitmap_dua = Bitmap.createScaledBitmap(bitmap_dua, convertWidth, convertHeight, true);

        paint_dua.setColor(Color.BLUE);
        canvas_dua.drawBitmap(bitmap_dua, 0, 0, null);
        document.finishPage(page_dua);

        simpan_termite = DIR_TERMITE + "Termite_" + IDClient + "_" + dateNow + ".pdf";

        File filePath;
        filePath = new File(simpan_termite);
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
        Toast.makeText(this, "PDF Form Survei Termite Control Berhasil Dibuat", Toast.LENGTH_SHORT).show();


        //Tampilin Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(ReviewSurveiTermiteControl.this);

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

    private void openGeneratedPDF() {
        File file = new File(simpan_termite);
        if (file.exists()) {
            //Uri path = FileProvider.getUriForFile(file);
            Uri path = FileProvider.getUriForFile(ReviewSurveiTermiteControl.this, BuildConfig.APPLICATION_ID + ".provider", file);
            Intent intent_view_pdf = new Intent(Intent.ACTION_VIEW);
            intent_view_pdf.setDataAndType(path, "application/pdf");
            intent_view_pdf.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent_view_pdf.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(intent_view_pdf);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(ReviewSurveiTermiteControl.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveToDB() {
        //Untuk Ambil Bitmap dari TTD Pelanggan dan Diupload
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        getTTD_Pelanggan.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] imageInByte = stream.toByteArray();
        ConvertImage_Pelanggan = Base64.encodeToString(imageInByte, Base64.DEFAULT);
        GetImageNameFromEditText_Pelanggan = "TermiteControl_" + namaPelanggan + "_" + dateNow + ".png";

        //Untuk Ambil Bitmap dari TTD Consultant dan Diupload
        ByteArrayOutputStream stream_consultant = new ByteArrayOutputStream();
        getTTD_Consultant.compress(Bitmap.CompressFormat.PNG, 50, stream_consultant);
        byte[] imageInByte_consultant = stream_consultant.toByteArray();
        ConvertImage_Consultant = Base64.encodeToString(imageInByte_consultant, Base64.DEFAULT);
        GetImageNameFromEditText_Consultant = "TermiteControl_" + TampilanMenuUtama.username + "_" + dateNow + ".png";

        //Untuk Upload Foto #1
        ByteArrayOutputStream stream_foto_satu = new ByteArrayOutputStream();
        getFotoSatu.compress(Bitmap.CompressFormat.PNG, 70, stream_foto_satu);
        byte[] imageInByte_foto_satu = stream_foto_satu.toByteArray();
        ConvertImage_FotoSatu = Base64.encodeToString(imageInByte_foto_satu, Base64.DEFAULT);
        GetImageNameFromEditText_FotoSatu = "TermiteControl_Foto_Satu_" + namaPelanggan + "_" + dateNow + ".png";

        Log.e("tag 1",   " " + GetImageNameFromEditText_FotoSatu);

        //Untuk Upload Foto #2
        ByteArrayOutputStream stream_foto_dua = new ByteArrayOutputStream();
        getFotoDua.compress(Bitmap.CompressFormat.PNG, 70, stream_foto_dua);
        byte[] imageInByte_foto_dua = stream_foto_dua.toByteArray();
        ConvertImage_FotoDua = Base64.encodeToString(imageInByte_foto_dua, Base64.DEFAULT);
        GetImageNameFromEditText_FotoDua = "TermiteControl_Foto_Dua_" + namaPelanggan + "_" + dateNow + ".png";

        Log.e("tag 2",  " " + GetImageNameFromEditText_FotoDua);

        //Untuk Upload Foto #3
        ByteArrayOutputStream stream_foto_tiga = new ByteArrayOutputStream();
        getFotoTiga.compress(Bitmap.CompressFormat.PNG, 70, stream_foto_tiga);
        byte[] imageInByte_foto_tiga = stream_foto_tiga.toByteArray();
        ConvertImage_FotoTiga = Base64.encodeToString(imageInByte_foto_tiga, Base64.DEFAULT);
        GetImageNameFromEditText_FotoTiga = "TermiteControl_Foto_Tiga_" + namaPelanggan + "_" + dateNow + ".png";

        Log.e("tag 3",  " " + GetImageNameFromEditText_FotoTiga);

        //Untuk Upload Foto #4
        ByteArrayOutputStream stream_foto_empat = new ByteArrayOutputStream();
        getFotoEmpat.compress(Bitmap.CompressFormat.PNG, 70, stream_foto_empat);
        byte[] imageInByte_foto_empat = stream_foto_empat.toByteArray();
        ConvertImage_FotoEmpat = Base64.encodeToString(imageInByte_foto_empat, Base64.DEFAULT);
        GetImageNameFromEditText_FotoEmpat = "TermiteControl_Foto_Empat_" + namaPelanggan + "_" + dateNow + ".png";

        Log.e("tag 4",  " " + GetImageNameFromEditText_FotoEmpat);

        @SuppressLint("StaticFieldLeak")
        class simpan extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                pDialog.setMessage("Menyimpan Data Survei Termite Control ke Database, Harap Tunggu...");
                showDialog();
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                hideDialog();
                Toast.makeText(ReviewSurveiTermiteControl.this, s, Toast.LENGTH_LONG).show();
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();

                //Bagian I : Data Kostumer -->
                params.put(Konfigurasi.KEY_SAVE_ID_CLIENT_TERMITE, IDClient);
                params.put(Konfigurasi.KEY_SAVE_NAMA_PELANGGAN_TERMITE, namaPelanggan);
                params.put(Konfigurasi.KEY_SAVE_KATEGORITEMPAT_TERMITE, kategoriTempatPelanggan);
                params.put(Konfigurasi.KEY_SAVE_ALAMAT_PELANGGAN_TERMITE, alamatPelanggan);
                params.put(Konfigurasi.KEY_SAVE_HP_PELANGGAN_TERMITE, hpPelanggan);
                params.put(Konfigurasi.KEY_SAVE_EMAIL_PELANGGAN_TERMITE, Email_DB);

                //Bagian II : Kategori dan Metode -->
                params.put(Konfigurasi.KEY_SAVE_JENIS_RAYAP_TERMITE, total_rayap);
                params.put(Konfigurasi.KEY_SAVE_KATEGORI_PENANGANAN_TERMITE, kategori_penanganan);
                params.put(Konfigurasi.KEY_SAVE_METODE_KENDALI_TERMITE, MetodeKendali_DB);

                //Bagian III : Chemical -->
                params.put(Konfigurasi.KEY_SAVE_CHEMICAL_TERMITE, Chemical_DB);
                params.put(Konfigurasi.KEY_SAVE_QTY_FIPRONIL_TERMITE, QtyFipronil_DB);
                params.put(Konfigurasi.KEY_SAVE_QTY_IMIDACLPORID_TERMITE, QtyImidaclporid_DB);
                params.put(Konfigurasi.KEY_SAVE_QTY_CYPERMETHRIN_TERMITE, QtyCypermethrin_DB);
                params.put(Konfigurasi.KEY_SAVE_QTY_DICHLORPHOS_TERMITE, QtyDichlorphos_DB);
                params.put(Konfigurasi.KEY_SAVE_QTY_BAITING_AG_TERMITE, QtyBaitingAG_DB);
                params.put(Konfigurasi.KEY_SAVE_QTY_BAITING_IG_TERMITE, QtyBaitingIG_DB);

                //Bagian IV : Luas Indoor Outdoor -->
                params.put(Konfigurasi.KEY_SAVE_LUAS_INDOOR_TERMITE, Indoor_DB);
                params.put(Konfigurasi.KEY_SAVE_LUAS_OUTDOOR_TERMITE, Outdoor_DB);

                //Bagian V : Lantai Bangunan (Untuk Pasca Konstruksi -->
                //NOTE- MUNGKIN UNTUK UKURAN LAINNYA BISA DIBUAT FIELD SENDIRI
                params.put(Konfigurasi.KEY_SAVE_LANTAI_BANGUNAN_TERMITE, LantaiBangunan_DB);

                params.put(Konfigurasi.KEY_SAVE_QTY_KERAMIK_TERMITE, QtyKeramik_DB);
                params.put(Konfigurasi.KEY_SAVE_KERAMIK_LAINNYA_TERMITE, Keramik_Lain_DB);

                params.put(Konfigurasi.KEY_SAVE_QTY_GRANITO_TERMITE, QtyGranito_DB);
                params.put(Konfigurasi.KEY_SAVE_GRANITO_LAINNYA_TERMITE, Granito_Lain_DB);

                params.put(Konfigurasi.KEY_SAVE_QTY_MARMER_TERMITE, QtyMarmer_DB);
                params.put(Konfigurasi.KEY_SAVE_MARMER_LAINNYA_TERMITE, Marmer_Lain_DB);

                params.put(Konfigurasi.KEY_SAVE_QTY_TERASO_TERMITE, QtyTeraso_DB);
                params.put(Konfigurasi.KEY_SAVE_TERASO_LAINNYA_TERMITE, Teraso_Lain_DB);

                params.put(Konfigurasi.KEY_SAVE_QTY_GRANIT_TERMITE, QtyGranit_DB);
                params.put(Konfigurasi.KEY_SAVE_GRANIT_LAINNYA_TERMITE, Granit_Lain_DB);

                //Bagian VI : Kontrak -->
                params.put(Konfigurasi.KEY_SAVE_PENAWARAN_HARGA_TERMITE, PenawaranHarga_DB);
                params.put(Konfigurasi.KEY_SAVE_TOTAL_HARGA_TERMITE, TotalHarga_DB);

                //Bagian VII : Status dan Catatan -->
                params.put(Konfigurasi.KEY_SAVE_STATUS_KERJASAMA_TERMITE, status_kerjasama_kontrak);
                params.put(Konfigurasi.KEY_SAVE_CATATAN_TAMBAHAN_TERMITE, Catatan_DB);

                //Bagian VIII : Untuk TTD - Image Path dan Image -->
                //untuk Pelanggan
                params.put(Konfigurasi.KEY_SAVE_IMAGE_TAG_PELANGGAN_TERMITE, GetImageNameFromEditText_Pelanggan);
                params.put(Konfigurasi.KEY_SAVE_IMAGE_NAME_PELANGGAN_TERMITE, ConvertImage_Pelanggan);

                //untuk Consultant
                params.put(Konfigurasi.KEY_SAVE_IMAGE_TAG_CONSULTANT_TERMITE, GetImageNameFromEditText_Consultant);
                params.put(Konfigurasi.KEY_SAVE_IMAGE_NAME_CONSULTANT_TERMITE, ConvertImage_Consultant);

                params.put(Konfigurasi.KEY_SAVE_LATLONG_ALAMAT_PELANGGAN, LatLong_AlamatPelanggan);
                params.put(Konfigurasi.KEY_SAVE_LATLONG_CONSULTANT, LatLong_Consultant);

                //Bagian X : Foto Lampiran
                params.put(Konfigurasi.KEY_SAVE_TAG_FOTO_PERTAMA_REPORT, GetImageNameFromEditText_FotoSatu);
                params.put(Konfigurasi.KEY_SAVE_NAME_FOTO_PERTAMA_REPORT, ConvertImage_FotoSatu);

                params.put(Konfigurasi.KEY_SAVE_TAG_FOTO_KEDUA_REPORT, GetImageNameFromEditText_FotoDua);
                params.put(Konfigurasi.KEY_SAVE_NAME_FOTO_KEDUA_REPORT, ConvertImage_FotoDua);

                params.put(Konfigurasi.KEY_SAVE_TAG_FOTO_KETIGA_REPORT, GetImageNameFromEditText_FotoTiga);
                params.put(Konfigurasi.KEY_SAVE_NAME_FOTO_KETIGA_REPORT, ConvertImage_FotoTiga);

                params.put(Konfigurasi.KEY_SAVE_TAG_FOTO_KEEMPAT_REPORT, GetImageNameFromEditText_FotoEmpat);
                params.put(Konfigurasi.KEY_SAVE_NAME_FOTO_KEEMPAT_REPORT, ConvertImage_FotoEmpat);

                params.put(Konfigurasi.KEY_TAG_ID, TampilanMenuUtama.id_pegawai);
                params.put(Konfigurasi.KEY_GET_NAMA, TampilanMenuUtama.namaLengkap);

                RequestHandler rh = new RequestHandler();
                return rh.sendPostRequest(Konfigurasi.URL_SIMPAN_TERMITE_SURVEI, params);

            }
        }

        simpan save = new simpan();
        save.execute();
    }

    private void checkIDPelanggan(final String idPelanggan){
        pDialog.setMessage("Melakukan Validasi ID Pelanggan, Harap Menunggu...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, Konfigurasi.URL_CHECK_ID_PELANGGAN_TERMITE, new Response.Listener<String>() {
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(ReviewSurveiTermiteControl.this);
                        builder.setMessage(jObj.getString(TAG_MESSAGE))
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
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
                Map<String, String> params = new HashMap<>();
                params.put(Konfigurasi.KEY_CHECK_ID_PELANGGAN, idPelanggan);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
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
