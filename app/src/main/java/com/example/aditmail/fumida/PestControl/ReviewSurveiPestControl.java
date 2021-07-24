package com.example.aditmail.fumida.PestControl;

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
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import android.app.ProgressDialog;
import android.net.ConnectivityManager;

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

public class ReviewSurveiPestControl extends AppCompatActivity {

    ConnectivityManager conMgr;
    ProgressDialog pDialog;

    File file_pest;
    String DIR_PEST = Environment.getExternalStorageDirectory().getPath() + "/Fumida_PestControl/";
    String simpan_pest;

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
    private String IDClient, namaPelanggan, kategoriTempatPelanggan, alamatPelanggan, hpPelanggan, Email_DB;
    private String LatLong_AlamatPelanggan, LatLong_Consultant;

    private TextView txt_Rev_IDClient_Pest, txt_Rev_NamaClient_Pest, txt_Rev_Kategori_Pest, txt_Rev_Alamat_Pest,
            txt_Rev_Hp_Pest, txt_Rev_Email_Pest;

    //Bagian 2: Jenis Hama dan Penanganan
    private String Penanganan_DB, Qty_Penanganan_DB, Hama_DB, HamaLainnya_DB;
    private TextView txt_Hama_Pest, txt_KategoriPenanganan_Pest;

    //Bagian 3: Chemical Hama dan Metode Kendali
    private String Chemical_DB, MetodeKendali_DB, QtyLemTikus_DB, PerangkapTikus_DB, UmpanTikus_DB, UmpanTikusIndoor_DB,
            QtyPohonLalat_DB, BlackHole_DB, JenisFumigasi_DB, FlyCatcher_DB, MetodeLain_DB;
    private TextView txt_Chemical_Pest, txt_Rev_MetodeKendali_Pest;

    //Bagian 4: Luas Indoor Outdoor
    private String LuasIndoor_DB, LuasOutdoor_DB;
    private TextView txt_Rev_Indoor_Pest, txt_Rev_Outdoor_Pest;

    //Bagian 5: Kontrak dan Harga
    private String jenis_kontrak, DurasiKontrak_DB, PenawaranHarga_DB, total_harga_penawaran;
    private TextView txt_Rev_JenisKerja_Pest, txt_Rev_Penawaran_Pest,
            txt_Rev_Durasi_Pest, txt_Rev_TotalHarga_Pest;
    private TextView txt_PenawaranHargaNonKontrak, txt_PenawaranHargaKontrak;
    private TableRow tbl_DurasiKontrak_Pest;

    //Bagian 6: Status Kerjasama, Catatan dan TTD
    private String status_kerjasama_kontrak, keterangan, CatatanTambahan_DB;
    private String dateNow;
    private TextView txt_Rev_Status_Pest, txt_Catatan_Pest;
    private TextView txt_Rev_NamaPelanggan_Pest, txt_NamaConsultant_Pest, txt_Rev_Keterangan_Pest;
    private ImageView ttd_Rev_Pelanggan_Pest, ttd_Rev_Consultant_Pest;
    private Bitmap getTTD_Consultant, getTTD_Pelanggan;

    private String ConvertImage_Pelanggan, ConvertImage_Consultant;
    private String GetImageNameFromEditText_Pelanggan, GetImageNameFromEditText_Consultant;

    //Bagian 7 : Foto Lampiran
    private ImageView img_FotoSatu, img_FotoDua, img_FotoTiga, img_FotoEmpat;
    private Bitmap getFotoSatu, getFotoDua, getFotoTiga, getFotoEmpat;

    private String ConvertImage_FotoSatu, ConvertImage_FotoDua, ConvertImage_FotoTiga, ConvertImage_FotoEmpat;
    private String GetImageNameFromEditText_FotoSatu, GetImageNameFromEditText_FotoDua, GetImageNameFromEditText_FotoTiga,
            GetImageNameFromEditText_FotoEmpat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_survei_pest_control);

        pDialog = new ProgressDialog(ReviewSurveiPestControl.this);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(true);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        dateNow = new SimpleDateFormat("dd-MM-yy_hh.mm.ss", Locale.getDefault()).format(new Date());

        file_pest = new File(DIR_PEST);
        if (!file_pest.exists()) {
            file_pest.mkdir();
        }

        generate_pdf = findViewById(R.id.btn_Generate);
        btnSaveToDB = findViewById(R.id.btn_SaveToDB);

        linearLayout_pdf = findViewById(R.id.LinearLayout_PDF);
        linearLayout_foto = findViewById(R.id.LinearLayout_FotoPest);

        tbl_DurasiKontrak_Pest = findViewById(R.id.TableRow_DurasiKontrak_Pest);

        //Bagian I
        txt_Rev_IDClient_Pest = findViewById(R.id.textView_ReviewID_Pest);
        txt_Rev_NamaClient_Pest = findViewById(R.id.textView_ReviewNamaClient_Pest);
        txt_Rev_Kategori_Pest = findViewById(R.id.textView_Kategori_Pest);
        txt_Rev_Alamat_Pest = findViewById(R.id.textView_Alamat_Pest);
        txt_Rev_Hp_Pest = findViewById(R.id.textView_Hp_Pest);
        txt_Rev_Email_Pest = findViewById(R.id.textView_Email_Pest);

        //Bagian II
        txt_Hama_Pest = findViewById(R.id.textView_Hama_Pest);
        txt_KategoriPenanganan_Pest = findViewById(R.id.textView_KategoriPenanganan_Pest);
        txt_Chemical_Pest = findViewById(R.id.textView_Chemical_Pest);
        txt_Rev_MetodeKendali_Pest = findViewById(R.id.textView_MetodeKendali_Pest);

        //Bagian III
        txt_Rev_Indoor_Pest = findViewById(R.id.textView_Indoor_Pest);
        txt_Rev_Outdoor_Pest = findViewById(R.id.textView_Outdoor_Pest);

        //Bagian IV
        txt_Rev_JenisKerja_Pest = findViewById(R.id.textView_JenisKerja_Pest);
        txt_Rev_Penawaran_Pest = findViewById(R.id.textView_Penawaran_Pest);
        txt_Rev_Durasi_Pest = findViewById(R.id.textView_Durasi_Pest);
        txt_Rev_TotalHarga_Pest = findViewById(R.id.textView_TotalHarga_Pest);
        txt_Rev_Status_Pest = findViewById(R.id.textView_Status_Pest);
        txt_Catatan_Pest = findViewById(R.id.textView_Catatan_Pest);

        //Bagian V
        txt_Rev_NamaPelanggan_Pest = findViewById(R.id.textView_NamaPelanggan_Pest);
        txt_NamaConsultant_Pest = findViewById(R.id.textView_NamaConsultant_Pest);
        txt_Rev_Keterangan_Pest = findViewById(R.id.textView_Keterangan_Teks);

        //Bagian VI
        txt_PenawaranHargaNonKontrak = findViewById(R.id.textView_PenawaranNonKontrak);
        txt_PenawaranHargaKontrak = findViewById(R.id.textView_PenawaranKontrak);

        //Bagian VII
        ttd_Rev_Consultant_Pest = findViewById(R.id.imageView_TTD_Consultant_Pest);
        ttd_Rev_Pelanggan_Pest = findViewById(R.id.imageView_TTD_Pelanggan_Pest);

        getTTD_Consultant = ((BitmapDrawable) FragmentFormPest.img_ttdConsultant.getDrawable()).getBitmap();
        getTTD_Pelanggan = ((BitmapDrawable) FragmentFormPest.img_ttdPelanggan.getDrawable()).getBitmap();

        //Image lampiran
        img_FotoSatu = findViewById(R.id.imageView_FotoSatu_Lamp_Pest);
        img_FotoDua = findViewById(R.id.imageView_FotoDua_Lamp_Pest);
        img_FotoTiga = findViewById(R.id.imageView_FotoTiga_Lamp_Pest);
        img_FotoEmpat = findViewById(R.id.imageView_FotoEmpat_Lamp_Pest);

        //<--------------------------- Get Intent from Previous Activity --------------------------->
        Intent intent = getIntent();
        Bundle Extra_Rev_Pest = intent.getExtras();

        //Bagian 1: Data Diri
        IDClient = Extra_Rev_Pest.getString(FragmentFormPest.PestRev_ID_Client);
        namaPelanggan = Extra_Rev_Pest.getString(FragmentFormPest.PestRev_Nama_Client);
        kategoriTempatPelanggan = Extra_Rev_Pest.getString(FragmentFormPest.PestRev_Kategori_Client);
        alamatPelanggan = Extra_Rev_Pest.getString(FragmentFormPest.PestRev_Alamat_Client);
        hpPelanggan = Extra_Rev_Pest.getString(FragmentFormPest.PestRev_Hp_Client);
        Email_DB = Extra_Rev_Pest.getString(FragmentFormPest.PestRev_Email_DB);

        LatLong_Consultant = Extra_Rev_Pest.getString(FragmentFormPest.PestRev_LatLong_Consultant);
        LatLong_AlamatPelanggan = Extra_Rev_Pest.getString(FragmentFormPest.PestRev_LatLong_Alamat_Pelanggan);

        //Bagian 2: Jenis Hama dan Penanganan
        Penanganan_DB = Extra_Rev_Pest.getString(FragmentFormPest.PestRev_Penanganan_DB);
        Qty_Penanganan_DB = Extra_Rev_Pest.getString(FragmentFormPest.PestRev_QtyPenanganan_DB);
        Hama_DB = Extra_Rev_Pest.getString(FragmentFormPest.PestRev_Hama_DB);
        HamaLainnya_DB = Extra_Rev_Pest.getString(FragmentFormPest.PestRev_HamaLainnya_DB);

        //Bagian 3: Chemical Hama dan Metode Kendali
        Chemical_DB = Extra_Rev_Pest.getString(FragmentFormPest.PestRev_Chemical_DB);

        MetodeKendali_DB = Extra_Rev_Pest.getString(FragmentFormPest.PestRev_MetodeKendali_DB);
        QtyLemTikus_DB = Extra_Rev_Pest.getString(FragmentFormPest.PestRev_QtyLemTikus_DB);
        PerangkapTikus_DB = Extra_Rev_Pest.getString(FragmentFormPest.PestRev_QtyPerangkapTikus_DB);
        UmpanTikus_DB = Extra_Rev_Pest.getString(FragmentFormPest.PestRev_QtyUmpanTikus_DB);
        UmpanTikusIndoor_DB = Extra_Rev_Pest.getString(FragmentFormPest.PestRev_QtyUmpanTikusIndoor_DB);
        QtyPohonLalat_DB = Extra_Rev_Pest.getString(FragmentFormPest.PestRev_QtyPohonLalat_DB);
        BlackHole_DB = Extra_Rev_Pest.getString(FragmentFormPest.PestRev_QtyBlackHole_DB);
        JenisFumigasi_DB = Extra_Rev_Pest.getString(FragmentFormPest.PestRev_JenisFumigasi_DB);
        FlyCatcher_DB = Extra_Rev_Pest.getString(FragmentFormPest.PestRev_QtyFlyCatcher_DB);
        MetodeLain_DB = Extra_Rev_Pest.getString(FragmentFormPest.PestRev_MetodeLain_DB);

        //Bagian 4: Luas Indoor Outdoor
        LuasIndoor_DB = Extra_Rev_Pest.getString(FragmentFormPest.PestRev_LuasIndoor_DB);
        LuasOutdoor_DB = Extra_Rev_Pest.getString(FragmentFormPest.PestRev_LuasOutdoor_DB);

        //Bagian 5: Kontrak dan Harga
        jenis_kontrak = Extra_Rev_Pest.getString(FragmentFormPest.PestRev_JenisKerja_Client);
        DurasiKontrak_DB = Extra_Rev_Pest.getString(FragmentFormPest.PestRev_DurasiKontrak_DB);
        PenawaranHarga_DB = Extra_Rev_Pest.getString(FragmentFormPest.PestRev_PenawaranHarga_DB);
        total_harga_penawaran = Extra_Rev_Pest.getString(FragmentFormPest.PestRev_GrandTotal_Client);

        //Bagian 6: Status Kerjasama, Catatan dan TTD
        status_kerjasama_kontrak = Extra_Rev_Pest.getString(FragmentFormPest.PestRev_Status_Client);
        CatatanTambahan_DB = Extra_Rev_Pest.getString(FragmentFormPest.PestRev_CatatanTambahan_DB);

        //Bagian 7: Keterangan PDF
        keterangan = Extra_Rev_Pest.getString(FragmentFormPest.PestRev_Keterangan_Client);

        //Bagian 8 : Foto Lampiran
        getFotoSatu = ((BitmapDrawable) FragmentFormPest.img_FotoSatu.getDrawable()).getBitmap();
        getFotoDua = ((BitmapDrawable) FragmentFormPest.img_FotoDua.getDrawable()).getBitmap();
        getFotoTiga = ((BitmapDrawable) FragmentFormPest.img_FotoTiga.getDrawable()).getBitmap();
        getFotoEmpat = ((BitmapDrawable) FragmentFormPest.img_FotoEmpat.getDrawable()).getBitmap();
        //<--------------------------- Get Intent from Previous Activity --------------------------->

        //Execute Data....
        switch (status_kerjasama_kontrak) {
            case "Berhasil":
                txt_Rev_Status_Pest.setBackgroundColor(getResources().getColor(R.color.Berhasil));
                break;
            case "Menunggu":
                txt_Rev_Status_Pest.setBackgroundColor(getResources().getColor(R.color.Menunggu));
                break;
            case "Gagal":
                txt_Rev_Status_Pest.setBackgroundColor(getResources().getColor(R.color.Gagal));
                break;
        }

        TampilReviewPest();

        generate_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Tampilin Alert Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ReviewSurveiPestControl.this);

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
                    AlertDialog.Builder builder = new AlertDialog.Builder(ReviewSurveiPestControl.this);

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
                            //saveToDB();
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
    protected void TampilReviewPest() {
        txt_Rev_IDClient_Pest.setText(IDClient);
        txt_Rev_NamaClient_Pest.setText(namaPelanggan);
        txt_Rev_Kategori_Pest.setText(kategoriTempatPelanggan);
        txt_Rev_Alamat_Pest.setText(alamatPelanggan);
        txt_Rev_Hp_Pest.setText(hpPelanggan);

        if (Email_DB.equals("")) {
            txt_Rev_Email_Pest.setText("-");
        } else {
            txt_Rev_Email_Pest.setText(Email_DB);
        }

        //get List Jenis Hama
        String parse_jenis_hama = "";
        StringBuffer sb_hama = new StringBuffer(parse_jenis_hama);

        String[] JenisHama_Arr = Hama_DB.split(",");
        for (String list_hama : JenisHama_Arr) {
            if (list_hama.equalsIgnoreCase("Lainnya")) {
                sb_hama.append(list_hama).append(" : ").append(HamaLainnya_DB).append(", ");
            } else {
                //parse_jenis_hama += list_hama + ", ";
                sb_hama.append(list_hama).append(", ");
            }
        }
        sb_hama.deleteCharAt(sb_hama.length() - 2);
        txt_Hama_Pest.setText(sb_hama);

        //Kategori Penanganan
        if (Penanganan_DB.equalsIgnoreCase("Sistem Mobile")) {
            String mobile = Penanganan_DB + " : " + Qty_Penanganan_DB + " x Kunjungan/Bulan";
            txt_KategoriPenanganan_Pest.setText(mobile);
        } else if (Penanganan_DB.equalsIgnoreCase("Standby/Station")) {
            String standby = Penanganan_DB + " : " + Qty_Penanganan_DB + " Orang";
            txt_KategoriPenanganan_Pest.setText(standby);
        } else if (Penanganan_DB.equalsIgnoreCase("One Time Service")) {
            txt_KategoriPenanganan_Pest.setText(Penanganan_DB);
        }

        //getChemical List
        String parse_chemical_hama = "";
        StringBuffer sb_chemical = new StringBuffer(parse_chemical_hama);

        String[] JenisChemical_Arr = Chemical_DB.split(",");
        for (String list_chemical : JenisChemical_Arr) {
            sb_chemical.append(list_chemical).append(", ");
        }

        sb_chemical.deleteCharAt(sb_chemical.length() - 2);
        txt_Chemical_Pest.setText(sb_chemical);

        //Show Metode Kendali
        String[] MetodeKendaliHama_Arr = MetodeKendali_DB.split(",");
        String parse_metode_kendali_hama = "";
        StringBuffer sb_metode = new StringBuffer(parse_metode_kendali_hama);
        for (String list_metode_hama : MetodeKendaliHama_Arr) {
            if (list_metode_hama.equalsIgnoreCase("Lem Tikus (Rodent Glue Trap)")) {
                sb_metode.append("- ").append(list_metode_hama).append(" : ").append(QtyLemTikus_DB).append(" Buah").append("\n");
            } else if (list_metode_hama.equalsIgnoreCase("Perangkap Tikus Massal (Rodent Trap)")) {
                sb_metode.append("- ").append(list_metode_hama).append(" : ").append(PerangkapTikus_DB).append(" Buah").append("\n");
            } else if (list_metode_hama.equalsIgnoreCase("Umpan Tikus (Rodent Baiting Outdoor)")) {
                sb_metode.append("- ").append(list_metode_hama).append(" : ").append(UmpanTikus_DB).append(" Buah").append("\n");
            } else if (list_metode_hama.equalsIgnoreCase("Umpan Tikus (Rodent Baiting Indoor)")) {
                sb_metode.append("- ").append(list_metode_hama).append(" : ").append(UmpanTikusIndoor_DB).append(" Buah").append("\n");
            } else if (list_metode_hama.equalsIgnoreCase("Pohon Lalat")) {
                sb_metode.append("- ").append(list_metode_hama).append(" : ").append(QtyPohonLalat_DB).append(" Buah").append("\n");
            } else if (list_metode_hama.equalsIgnoreCase("Instalasi Black Hole")) {
                sb_metode.append("- ").append(list_metode_hama).append(" : ").append(BlackHole_DB).append(" Buah").append("\n");
            } else if (list_metode_hama.equalsIgnoreCase("Fumigasi")) {
                sb_metode.append("- ").append(list_metode_hama).append(" : ").append(JenisFumigasi_DB).append("\n");
            } else if (list_metode_hama.equalsIgnoreCase("Instalasi Fly Catcher")) {
                sb_metode.append("- ").append(list_metode_hama).append(" : ").append(FlyCatcher_DB).append(" Buah").append("\n");
            } else if (list_metode_hama.equalsIgnoreCase("Metode Lainnya")) {
                sb_metode.append("- ").append(list_metode_hama).append(" : ").append(MetodeLain_DB).append("\n");
            } else {
                sb_metode.append("- ").append(list_metode_hama).append("\n");
            }
        }

        txt_Rev_MetodeKendali_Pest.setText(sb_metode);

        //Luas Indoor dan Outdoor
        txt_Rev_Indoor_Pest.setText(LuasIndoor_DB + getString(R.string.text_m2));
        if (LuasOutdoor_DB.equals("")) {
            txt_Rev_Outdoor_Pest.setText("-");
        } else {
            txt_Rev_Outdoor_Pest.setText(LuasOutdoor_DB + getString(R.string.text_m2));
        }

        //Untuk Kontrak / Non Kontrak
        if (jenis_kontrak.equals("Non-Kontrak")) {
            tbl_DurasiKontrak_Pest.setVisibility(View.GONE);
            txt_PenawaranHargaKontrak.setVisibility(View.GONE);
            txt_PenawaranHargaNonKontrak.setVisibility(View.VISIBLE);
        } else if (jenis_kontrak.equals("Kontrak")) {
            tbl_DurasiKontrak_Pest.setVisibility(View.VISIBLE);
            txt_PenawaranHargaKontrak.setVisibility(View.VISIBLE);
            txt_PenawaranHargaNonKontrak.setVisibility(View.GONE);

            txt_Rev_Durasi_Pest.setText(DurasiKontrak_DB + " Bulan");
        }

        txt_Rev_JenisKerja_Pest.setText(jenis_kontrak);

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        String penawaran_harga_convert = formatRupiah.format(Double.valueOf(PenawaranHarga_DB));
        String total_harga_convert = formatRupiah.format(Double.valueOf(total_harga_penawaran));

        txt_Rev_Penawaran_Pest.setText(penawaran_harga_convert);
        txt_Rev_TotalHarga_Pest.setText(total_harga_convert);

        txt_Rev_Status_Pest.setText(status_kerjasama_kontrak);

        if (CatatanTambahan_DB.equals("")) {
            txt_Catatan_Pest.setText("-");
        } else {
            txt_Catatan_Pest.setText(CatatanTambahan_DB);
        }

        txt_Rev_NamaPelanggan_Pest.setText(namaPelanggan);
        txt_NamaConsultant_Pest.setText(TampilanMenuUtama.namaLengkap);
        txt_Rev_Keterangan_Pest.setText(keterangan);

        ttd_Rev_Pelanggan_Pest.setImageBitmap(getTTD_Pelanggan);
        ttd_Rev_Consultant_Pest.setImageBitmap(getTTD_Consultant);

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

        simpan_pest = DIR_PEST + "Pest_" + IDClient + "_" + dateNow + ".pdf";

        File filePath;
        filePath = new File(simpan_pest);
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
        Toast.makeText(this, "PDF Form Survei Pest Control Berhasil Dibuat", Toast.LENGTH_SHORT).show();

        //Tampilin Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(ReviewSurveiPestControl.this);

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
        File file = new File(simpan_pest);
        if (file.exists()) {
            Uri path = FileProvider.getUriForFile(ReviewSurveiPestControl.this, BuildConfig.APPLICATION_ID + ".provider", file);
            Intent intent_view_pdf = new Intent(Intent.ACTION_VIEW);
            intent_view_pdf.setDataAndType(path, "application/pdf");
            intent_view_pdf.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent_view_pdf.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(intent_view_pdf);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(ReviewSurveiPestControl.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveToDB() {

        //Untuk Ambil Bitmap dari TTD Pelanggan dan Diupload
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        getTTD_Pelanggan.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] imageInByte = stream.toByteArray();
        ConvertImage_Pelanggan = Base64.encodeToString(imageInByte, Base64.DEFAULT);
        GetImageNameFromEditText_Pelanggan = "PestControl_" + namaPelanggan + "_" + dateNow + ".png";

        //Untuk Ambil Bitmap dari TTD Consultant dan Diupload
        ByteArrayOutputStream stream_consultant = new ByteArrayOutputStream();
        getTTD_Consultant.compress(Bitmap.CompressFormat.PNG, 50, stream_consultant);
        byte[] imageInByte_consultant = stream_consultant.toByteArray();
        ConvertImage_Consultant = Base64.encodeToString(imageInByte_consultant, Base64.DEFAULT);
        GetImageNameFromEditText_Consultant = "PestControl_" + TampilanMenuUtama.username + "_" + dateNow + ".png";

        //Untuk Upload Foto #1
        ByteArrayOutputStream stream_foto_satu = new ByteArrayOutputStream();
        getFotoSatu.compress(Bitmap.CompressFormat.PNG, 70, stream_foto_satu);
        byte[] imageInByte_foto_satu = stream_foto_satu.toByteArray();
        ConvertImage_FotoSatu = Base64.encodeToString(imageInByte_foto_satu, Base64.DEFAULT);
        GetImageNameFromEditText_FotoSatu = "PestControl_Foto_Satu_" + namaPelanggan + "_" + dateNow + ".png";

        //Untuk Upload Foto #2
        ByteArrayOutputStream stream_foto_dua = new ByteArrayOutputStream();
        getFotoDua.compress(Bitmap.CompressFormat.PNG, 70, stream_foto_dua);
        byte[] imageInByte_foto_dua = stream_foto_dua.toByteArray();
        ConvertImage_FotoDua = Base64.encodeToString(imageInByte_foto_dua, Base64.DEFAULT);
        GetImageNameFromEditText_FotoDua = "PestControl_Foto_Dua_" + namaPelanggan + "_" + dateNow + ".png";

        //Untuk Upload Foto #3
        ByteArrayOutputStream stream_foto_tiga = new ByteArrayOutputStream();
        getFotoTiga.compress(Bitmap.CompressFormat.PNG, 70, stream_foto_tiga);
        byte[] imageInByte_foto_tiga = stream_foto_tiga.toByteArray();
        ConvertImage_FotoTiga = Base64.encodeToString(imageInByte_foto_tiga, Base64.DEFAULT);
        GetImageNameFromEditText_FotoTiga = "PestControl_Foto_Tiga_" + namaPelanggan + "_" + dateNow + ".png";

        //Untuk Upload Foto #4
        ByteArrayOutputStream stream_foto_empat = new ByteArrayOutputStream();
        getFotoEmpat.compress(Bitmap.CompressFormat.PNG, 70, stream_foto_empat);
        byte[] imageInByte_foto_empat = stream_foto_empat.toByteArray();
        ConvertImage_FotoEmpat = Base64.encodeToString(imageInByte_foto_empat, Base64.DEFAULT);
        GetImageNameFromEditText_FotoEmpat = "PestControl_Foto_Empat_" + namaPelanggan + "_" + dateNow + ".png";

        @SuppressLint("StaticFieldLeak")
        class simpan extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                pDialog.setMessage("Menyimpan Data Survei Pest Control kedalam Database...");
                showDialog();
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                hideDialog();

                AlertDialog.Builder builder = new AlertDialog.Builder(ReviewSurveiPestControl.this);
                builder.setMessage(s)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }

            @Override
            protected String doInBackground(Void... v) {
                //String nya musti sama dengan yang ada di konfigurasi...

                //try {
                HashMap<String, String> params = new HashMap<>();

                //Bagian I : Data Kostumer
                params.put(Konfigurasi.KEY_SAVE_ID_CLIENT, IDClient);
                params.put(Konfigurasi.KEY_SAVE_NAMA_PELANGGAN, namaPelanggan);
                params.put(Konfigurasi.KEY_SAVE_KATEGORITEMPAT, kategoriTempatPelanggan);
                params.put(Konfigurasi.KEY_SAVE_ALAMAT_PELANGGAN, alamatPelanggan);
                params.put(Konfigurasi.KEY_SAVE_HP_PELANGGAN, hpPelanggan);
                params.put(Konfigurasi.KEY_SAVE_EMAIL_PELANGGAN, Email_DB);

                //Bagian II : Kendali Hama
                params.put(Konfigurasi.KEY_SAVE_JENIS_HAMA, Hama_DB);
                params.put(Konfigurasi.KEY_SAVE_JENIS_HAMA_LAINNYA, HamaLainnya_DB);

                params.put(Konfigurasi.KEY_SAVE_KATEGORI_PENANGANAN, Penanganan_DB);
                params.put(Konfigurasi.KEY_SAVE_KUANTITAS_PENANGANAN, Qty_Penanganan_DB);

                //Bagian III : Metode Kendali Hama
                params.put(Konfigurasi.KEY_SAVE_CHEMICAL, Chemical_DB);
                params.put(Konfigurasi.KEY_SAVE_METODE_KENDALI_HAMA, MetodeKendali_DB);

                params.put(Konfigurasi.KEY_SAVE_QTY_LEM_TIKUS, QtyLemTikus_DB);
                params.put(Konfigurasi.KEY_SAVE_QTY_PERANGKAP_TIKUS, PerangkapTikus_DB);
                params.put(Konfigurasi.KEY_SAVE_QTY_UMPAN_TIKUS_OUTDOOR, UmpanTikus_DB);
                params.put(Konfigurasi.KEY_SAVE_QTY_UMPAN_TIKUS_INDOOR, UmpanTikusIndoor_DB);
                params.put(Konfigurasi.KEY_SAVE_QTY_POHON_LALAT, QtyPohonLalat_DB);
                params.put(Konfigurasi.KEY_SAVE_QTY_BLACK_HOLE, BlackHole_DB);
                params.put(Konfigurasi.KEY_SAVE_JENIS_FUMIGASI, JenisFumigasi_DB);
                params.put(Konfigurasi.KEY_SAVE_QTY_FLY_CATCHER, FlyCatcher_DB);
                params.put(Konfigurasi.KEY_SAVE_METODE_LAIN, MetodeLain_DB);

                //Bagian IV : Luas Indoor Outdoor
                params.put(Konfigurasi.KEY_SAVE_LUAS_INDOOR, LuasIndoor_DB);
                params.put(Konfigurasi.KEY_SAVE_LUAS_OUTDOOR, LuasOutdoor_DB);

                //Bagian V : Kontrak
                params.put(Konfigurasi.KEY_SAVE_JENIS_KONTRAK, jenis_kontrak);
                params.put(Konfigurasi.KEY_SAVE_DURASI_KONTRAK, DurasiKontrak_DB);
                params.put(Konfigurasi.KEY_SAVE_PENAWARAN_HARGA, PenawaranHarga_DB);
                params.put(Konfigurasi.KEY_SAVE_TOTAL_HARGA, total_harga_penawaran);

                //Bagian VI : Catatan
                params.put(Konfigurasi.KEY_SAVE_STATUS_KERJASAMA, status_kerjasama_kontrak);
                params.put(Konfigurasi.KEY_SAVE_CATATAN_TAMBAHAN, CatatanTambahan_DB);

                //Bagian VII : Untuk TTD - Image Path dan Image
                //untuk Pelanggan
                params.put(Konfigurasi.KEY_SAVE_IMAGE_TAG_PELANGGAN, GetImageNameFromEditText_Pelanggan);
                params.put(Konfigurasi.KEY_SAVE_IMAGE_NAME_PELANGGAN, ConvertImage_Pelanggan);

                //untuk Consultant
                params.put(Konfigurasi.KEY_SAVE_IMAGE_TAG_CONSULTANT, GetImageNameFromEditText_Consultant);
                params.put(Konfigurasi.KEY_SAVE_IMAGE_NAME_CONSULTANT, ConvertImage_Consultant);

                //Bagian IX : Untuk Get Latlong...
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
                return rh.sendPostRequest(Konfigurasi.URL_SIMPAN_PEST_SURVEI, params);
            }
        }

        hideDialog();

        simpan save = new simpan();
        save.execute();
    }

    private void checkIDPelanggan(final String idPelanggan) {
        pDialog.setMessage("Melakukan Validasi ID Pelanggan, Harap Menunggu...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, Konfigurasi.URL_CHECK_ID_PELANGGAN, new Response.Listener<String>() {
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(ReviewSurveiPestControl.this);
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
                } catch (JSONException e) {
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

            //seleksi dan lihat kedalam php...
            //php bakal liat kedalam database
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
