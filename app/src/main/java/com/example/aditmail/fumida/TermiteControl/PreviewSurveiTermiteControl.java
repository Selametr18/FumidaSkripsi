package com.example.aditmail.fumida.TermiteControl;

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
import android.graphics.pdf.PdfDocument;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.bumptech.glide.Glide;
import com.example.aditmail.fumida.Activities.TampilanMenuUtama;
import com.example.aditmail.fumida.BuildConfig;
import com.example.aditmail.fumida.R;
import com.example.aditmail.fumida.Settings.Konfigurasi;
import com.example.aditmail.fumida.Settings.RequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class PreviewSurveiTermiteControl extends AppCompatActivity {

    ConnectivityManager conMgr;
    ProgressDialog pDialog;

    File file_termite;
    String DIR_TERMITE = Environment.getExternalStorageDirectory().getPath() + "/Fumida_TermiteControl/";
    String simpan_termite;

    protected String id_pegawai, id_survei;

    protected Button generate_pdf, btnSaveToDB;
    protected LinearLayout linearLayout_pdf, linearLayout_foto;
    protected Bitmap bitmap, bitmap_dua;

    //Bagian 1: Data Diri
    protected TextView txt_Rev_IDClient_Termite, txt_Rev_NamaClient_Termite, txt_Rev_Kategori_Termite, txt_Rev_Alamat_Termite,
            txt_Rev_Hp_Termite, txt_Rev_Email_Termite;

    //Bagian 2: Rayap dan Kategori, Chemical
    protected TextView txt_Rayap_Termite, txt_KategoriPenanganan_Termite, txt_Chemical_Termite;

    //Bagian 3: Metode Kendali dan Luas
    protected TextView txt_Rev_MetodeKendali_Termite, txt_Rev_Indoor_Termite, txt_Rev_Outdoor_Termite;

    //Bagian 4: Lantai...
    protected TextView txt_Rev_LantaiBangunan_Termite, txt_Tulisan_LantaiBangunan;

    //Bagian 5: Penawaran, Status dan Catatan
    protected TextView txt_Rev_Penawaran_Termite, txt_Rev_TotalHarga_Termite;
    protected TextView txt_Rev_Status_Termite, txt_Catatan_Termite, txt_Rev_NamaPelanggan_Termite, txt_NamaConsultant_Termite,
            txt_Rev_Keterangan_Termite;
    protected String dateNow, keterangan;

    //Bagian 6: TTD
    protected ImageView ttd_Rev_Pelanggan_Termite, ttd_Rev_Consultant_Termite;

    //Bagian 7 : Foto Lampiran
    protected ImageView img_FotoSatu, img_FotoDua, img_FotoTiga, img_FotoEmpat;

    protected SimpleDateFormat dateFormat;
    protected Date date_tglInput, date_TimeStamp;
    protected String tgl_input_Fixed, timestamp_Fixed;

    //Get data from DB--------------------------------------------->
    private String ClientID, NamaPelanggan, KategoriTempat, Alamat, NoHP, Email, JenisRayap, KategoriPenanganan,
            MetodeKendaliRayap, JenisChemical, QtyFipronil, QtyImidaclporid, QtyCypermethrin, QtyDichlorphos, QtyBaitingAG, QtyBaitingIG,
            LuasIndoor, LuasOutdoor, JenisLantaiBangunan, QtyKeramik, KeramikLain, QtyGranito, GranitoLain,
            QtyMarmer, MarmerLain, QtyTeraso, TerasoLain, QtyGranit, GranitLain, PenawaranHarga, TotalHarga,
            StatusKerjasama, CatatanTambahan, GetImgPelanggan_TTD, GetImgConsultant_TTD, GetTglInput,
            GetTimeStamp, pathFotoSatu, pathFotoDua, pathFotoTiga, pathFotoEmpat;
    //Get data from DB--------------------------------------------->

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_survei_termite_control);

        Intent a = getIntent();
        id_pegawai = a.getStringExtra(Konfigurasi.KEY_TAG_ID);
        id_survei = a.getStringExtra(Konfigurasi.KEY_SURVEI_ID);

        InitData();

        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
            getDataSurveiTermite();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection",
                    Toast.LENGTH_LONG).show();
        }

        dateNow = new SimpleDateFormat("dd-MM-yy_hh.mm.ss", Locale.getDefault()).format(new Date());
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    private void InitData() {
        pDialog = new ProgressDialog(PreviewSurveiTermiteControl.this);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(true);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        file_termite = new File(DIR_TERMITE);
        if (!file_termite.exists()) {
            file_termite.mkdir();
        }

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        generate_pdf = findViewById(R.id.btn_Generate_Termite);
        generate_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Tampilin Alert Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(PreviewSurveiTermiteControl.this);

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

        btnSaveToDB = findViewById(R.id.btn_SaveToDB);
        btnSaveToDB.setVisibility(View.GONE);

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

        //Bagian VIII
        img_FotoSatu = findViewById(R.id.imageView_FotoSatu_Lamp_Termite);
        img_FotoDua = findViewById(R.id.imageView_FotoDua_Lamp_Termite);
        img_FotoTiga = findViewById(R.id.imageView_FotoTiga_Lamp_Termite);
        img_FotoEmpat = findViewById(R.id.imageView_FotoEmpat_Lamp_Termite);
    }

    private void getDataSurveiTermite() {
        @SuppressLint("StaticFieldLeak")
        class GetDataSurveiTermite extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                pDialog.setMessage("Mengambil Data, Harap Menunggu...");
                showDialog();
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                hideDialog();
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

            GetTglInput = c.getString("tgl_input");
            GetTimeStamp = c.getString("timestamp");

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

    @SuppressLint("SetTextI18n")
    private void showData() {

        txt_Rev_IDClient_Termite.setText(ClientID);
        txt_Rev_NamaClient_Termite.setText(NamaPelanggan);
        txt_Rev_Alamat_Termite.setText(Alamat);
        txt_Rev_Hp_Termite.setText(NoHP);
        txt_Rev_Kategori_Termite.setText(KategoriTempat);

        if (Email.equals("")) {
            txt_Rev_Email_Termite.setText("-");
        } else {
            txt_Rev_Email_Termite.setText(Email);
        }

        txt_Rayap_Termite.setText(JenisRayap);
        txt_KategoriPenanganan_Termite.setText(KategoriPenanganan);

        //Show Metode Kendali
        String parse_metode_kendali_rayap = "";
        StringBuilder sb_metode = new StringBuilder(parse_metode_kendali_rayap);

        String[] MetodeKendaliRayap_Arr = MetodeKendaliRayap.split(",");
        for (String list_metode : MetodeKendaliRayap_Arr) {
            sb_metode.append(list_metode).append(", ");
        }
        sb_metode.deleteCharAt(sb_metode.length() - 2);
        txt_Rev_MetodeKendali_Termite.setText(sb_metode + "\n");

        //Show Chemical
        String parse_chemical_rayap = "";
        StringBuffer sb_chemical = new StringBuffer(parse_chemical_rayap);

        String[] ChemicalRayap_Arr = JenisChemical.split(",");
        for (String list_chemical : ChemicalRayap_Arr) {
            if (list_chemical.equalsIgnoreCase("Fipronil 50 g/l")) {
                sb_chemical.append("- ").append(list_chemical).append(" : ").append(QtyFipronil).append(" Unit").append("\n");
            }
            if (list_chemical.equalsIgnoreCase("Imidacloprid 200 g/l")) {
                sb_chemical.append("- ").append(list_chemical).append(" : ").append(QtyImidaclporid).append(" Unit").append("\n");
            }
            if (list_chemical.equalsIgnoreCase("Cypermethrin 100 g/l")) {
                sb_chemical.append("- ").append(list_chemical).append(" : ").append(QtyCypermethrin).append(" Unit").append("\n");
            }
            if (list_chemical.equalsIgnoreCase("Dichlorphos 200 g/l")) {
                sb_chemical.append("- ").append(list_chemical).append(" : ").append(QtyDichlorphos).append(" Unit").append("\n");
            }
            if (list_chemical.equalsIgnoreCase("Baiting AG")) {
                sb_chemical.append("- ").append(list_chemical).append(" : ").append(QtyBaitingAG).append(" Unit").append("\n");
            }
            if (list_chemical.equalsIgnoreCase("Baiting IG")) {
                sb_chemical.append("- ").append(list_chemical).append(" : ").append(QtyBaitingIG).append(" Unit").append("\n");
            }
        }
        txt_Chemical_Termite.setText(sb_chemical);

        txt_Rev_Indoor_Termite.setText(LuasIndoor + getString(R.string.text_m2));
        if (LuasOutdoor.equals("")) {
            txt_Rev_Outdoor_Termite.setText("-");
        } else {
            txt_Rev_Outdoor_Termite.setText(LuasOutdoor + getString(R.string.text_m2));
        }

        //Untuk Lantai Bangunan
        String parse_lantai_bangunan = "";
        StringBuffer sb_lantai = new StringBuffer(parse_lantai_bangunan);

        //Parse Ukuran Keramik
        String parse_keramik = "";
        StringBuilder sb_keramik = new StringBuilder(parse_keramik);
        String[] Keramik_Arr = QtyKeramik.split(",");
        for (String list_keramik : Keramik_Arr) {
            sb_keramik.append(list_keramik).append(", ");
        }
        sb_keramik.deleteCharAt(sb_keramik.length() - 2);

        //Parse Ukuran Keramik
        String parse_granito = "";
        StringBuilder sb_granito = new StringBuilder(parse_granito);
        String[] Granito_Arr = QtyGranito.split(",");
        for (String list_granito : Granito_Arr) {
            sb_granito.append(list_granito).append(", ");
        }
        sb_granito.deleteCharAt(sb_granito.length() - 2);

        //Parse Ukuran Marmer
        String parse_marmer = "";
        StringBuilder sb_marmer = new StringBuilder(parse_marmer);
        String[] Marmer_Arr = QtyMarmer.split(",");
        for (String list_marmer : Marmer_Arr) {
            sb_marmer.append(list_marmer).append(", ");
        }
        sb_marmer.deleteCharAt(sb_marmer.length() - 2);

        //Parse Ukuran Teraso
        String parse_teraso = "";
        StringBuilder sb_teraso = new StringBuilder(parse_teraso);
        String[] Teraso_Arr = QtyTeraso.split(",");
        for (String list_teraso : Teraso_Arr) {
            sb_teraso.append(list_teraso).append(", ");
        }
        sb_teraso.deleteCharAt(sb_teraso.length() - 2);

        //Parse Ukuran Granit
        String parse_granit = "";
        StringBuilder sb_granit = new StringBuilder(parse_granit);
        String[] Granit_Arr = QtyGranit.split(",");
        for (String list_granit : Granit_Arr) {
            sb_granit.append(list_granit).append(", ");
        }
        sb_granit.deleteCharAt(sb_granit.length() - 2);

        String[] LantaiBangunan_Arr = JenisLantaiBangunan.split(",");
        for (String list_lantai : LantaiBangunan_Arr) {
            if (list_lantai.equalsIgnoreCase("Keramik")) {
                if (!KeramikLain.isEmpty()) {
                    sb_lantai.append("- ").append(list_lantai).append("\t: ( ").append(sb_keramik).append(",").append(KeramikLain).append(" )").append("\n");
                } else {
                    sb_lantai.append("- ").append(list_lantai).append("\t: ( ").append(sb_keramik).append(")").append("\n");
                }
            } else if (list_lantai.equalsIgnoreCase("Granit Tile")) {
                if (!GranitoLain.isEmpty()) {
                    sb_lantai.append("- ").append(list_lantai).append("\t: ( ").append(sb_granito).append(",").append(GranitoLain).append(" )").append("\n");
                } else {
                    sb_lantai.append("- ").append(list_lantai).append("\t\t: ( ").append(sb_granito).append(")").append("\n");
                }
            } else if (list_lantai.equalsIgnoreCase("Marmer")) {
                if (!MarmerLain.isEmpty()) {
                    sb_lantai.append("- ").append(list_lantai).append("\t: ( ").append(sb_marmer).append(",").append(MarmerLain).append(" )").append("\n");
                } else {
                    sb_lantai.append("- ").append(list_lantai).append("\t: ( ").append(sb_marmer).append(")").append("\n");
                }
            } else if (list_lantai.equalsIgnoreCase("Teraso")) {
                if (!TerasoLain.isEmpty()) {
                    sb_lantai.append("- ").append(list_lantai).append("\t: ( ").append(sb_teraso).append(",").append(TerasoLain).append(" )").append("\n");
                } else {
                    sb_lantai.append("- ").append(list_lantai).append("\t\t: ( ").append(sb_teraso).append(")").append("\n");
                }
            } else if (list_lantai.equalsIgnoreCase("Granit")) {
                if (!GranitLain.isEmpty()) {
                    sb_lantai.append("- ").append(list_lantai).append("\t: ( ").append(sb_granit).append(",").append(GranitLain).append(" )").append("\n");
                } else {
                    sb_lantai.append("- ").append(list_lantai).append("\t\t\t: ( ").append(sb_granit).append(")").append("\n");
                }
            } else {
                sb_lantai.append("- ").append(list_lantai).append("\n");
            }
        }
        txt_Rev_LantaiBangunan_Termite.setText(sb_lantai);

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        double penawaran;
        penawaran = Double.parseDouble(PenawaranHarga);

        txt_Rev_Penawaran_Termite.setText(formatRupiah.format(penawaran));
        txt_Rev_TotalHarga_Termite.setText(TotalHarga);

        txt_Rev_Status_Termite.setText(StatusKerjasama);
        //Execute Data....
        switch (StatusKerjasama) {
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

        if (KategoriPenanganan.equals("Anti Rayap Pasca-Konstruksi GARANSI 3 TAHUN")) {
            txt_Rev_LantaiBangunan_Termite.setVisibility(View.VISIBLE);
            txt_Tulisan_LantaiBangunan.setVisibility(View.VISIBLE);
        } else if (KategoriPenanganan.equals("Anti Rayap Pra-Konstruksi GARANSI 5 TAHUN")) {
            txt_Rev_LantaiBangunan_Termite.setVisibility(View.GONE);
            txt_Tulisan_LantaiBangunan.setVisibility(View.GONE);
        }

        txt_Catatan_Termite.setText(CatatanTambahan);

        txt_Rev_NamaPelanggan_Termite.setText(NamaPelanggan);
        txt_NamaConsultant_Termite.setText(TampilanMenuUtama.namaLengkap);
        txt_Rev_Keterangan_Termite.setText(keterangan);

        try {
            Glide.with(getApplicationContext())
                    .load(Konfigurasi.url_image + pathFotoSatu)
                    .error(Glide.with(img_FotoSatu).load(R.drawable.ic_no_image))
                    .into(img_FotoSatu);

            Glide.with(getApplicationContext())
                    .load(Konfigurasi.url_image + pathFotoDua)
                    .error(Glide.with(img_FotoDua).load(R.drawable.ic_no_image))
                    .into(img_FotoDua);

            Glide.with(getApplicationContext())
                    .load(Konfigurasi.url_image + pathFotoTiga)
                    .error(Glide.with(img_FotoTiga).load(R.drawable.ic_no_image))
                    .into(img_FotoTiga);

            Glide.with(getApplicationContext())
                    .load(Konfigurasi.url_image + pathFotoEmpat)
                    .error(Glide.with(img_FotoEmpat).load(R.drawable.ic_no_image))
                    .into(img_FotoEmpat);

            Glide.with(getApplicationContext())
                    .load(Konfigurasi.url_image + GetImgPelanggan_TTD)
                    .error(Glide.with(ttd_Rev_Pelanggan_Termite).load(R.drawable.ic_ttd_not_found))
                    .into(ttd_Rev_Pelanggan_Termite);

            Glide.with(getApplicationContext())
                    .load(Konfigurasi.url_image + GetImgConsultant_TTD)
                    .error(Glide.with(ttd_Rev_Consultant_Termite).load(R.drawable.ic_ttd_not_found))
                    .into(ttd_Rev_Consultant_Termite);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (GetTglInput.equalsIgnoreCase(GetTimeStamp)) {
            try {
                date_tglInput = dateFormat.parse(GetTglInput);
                dateFormat.applyPattern("dd/MM/yyyy HH:mm:ss");

                tgl_input_Fixed = dateFormat.format(date_tglInput);
            } catch (ParseException e) {
                e.printStackTrace();
                Log.e("tag", String.valueOf(e));
            }

            final String keterangan = "*Berkas ini Dibuat Secara Otomatis oleh Sistem Mobile Apps Fumida pada Tanggal " + tgl_input_Fixed;
            txt_Rev_Keterangan_Termite.setText(keterangan);

        } else if (!GetTglInput.equalsIgnoreCase(GetTimeStamp)) {
            try {
                date_tglInput = dateFormat.parse(GetTglInput);
                date_TimeStamp = dateFormat.parse(GetTimeStamp);

                dateFormat.applyPattern("dd/MM/yyyy HH:mm:ss");

                tgl_input_Fixed = dateFormat.format(date_tglInput);
                timestamp_Fixed = dateFormat.format(date_TimeStamp);
            } catch (ParseException e) {
                e.printStackTrace();
                Log.e("tag", String.valueOf(e));
            }

            final String keterangan = "*Berkas ini Dibuat Secara Otomatis oleh Sistem Mobile Apps Fumida pada Tanggal " + tgl_input_Fixed + "\n";
            final String revisi = "**Telah Direvisi pada Tanggal " + timestamp_Fixed;
            txt_Rev_Keterangan_Termite.setText(keterangan + revisi);
        }
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

        simpan_termite = DIR_TERMITE + "Termite_" + NamaPelanggan + "_" + dateNow + ".pdf";

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
        AlertDialog.Builder builder = new AlertDialog.Builder(PreviewSurveiTermiteControl.this);

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
            Uri path = FileProvider.getUriForFile(PreviewSurveiTermiteControl.this, BuildConfig.APPLICATION_ID + ".provider", file);
            Intent intent_view_pdf = new Intent(Intent.ACTION_VIEW);
            intent_view_pdf.setDataAndType(path, "application/pdf");
            intent_view_pdf.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent_view_pdf.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(intent_view_pdf);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(PreviewSurveiTermiteControl.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
            }
        }
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
