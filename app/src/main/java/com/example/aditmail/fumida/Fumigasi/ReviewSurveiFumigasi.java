package com.example.aditmail.fumida.Fumigasi;

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
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReviewSurveiFumigasi extends AppCompatActivity {

    ConnectivityManager conMgr;
    ProgressDialog pDialog;

    File file_fumigasi;
    String DIR_FUMIGASI = Environment.getExternalStorageDirectory().getPath() + "/Fumida_Fumigasi/";
    String simpan_fumigasi;

    int success;
    //inisiasi jika berhasil
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    //untuk proses pertukaran data
    String tag_json_obj = "json_obj_req";

    protected Button generate_pdf, btnSaveToDB;
    protected LinearLayout linearLayout_pdf, linearLayout_foto;
    private Bitmap bitmap, bitmap_dua;

    //Bagian 1: Data Diri
    protected TextView txt_Rev_IDClient, txt_Rev_NamaClient, txt_Rev_Kategori, txt_Rev_Alamat,
            txt_Rev_Hp, txt_Rev_Email;

    private String IDClient, namaPelanggan, kategoriTempatPelanggan, alamatPelanggan, hpPelanggan, Email_DB;
    private String LatLong_AlamatPelanggan, LatLong_Consultant;

    //Bagian 3: Metode Kendali dan Luas
    protected TextView txt_Rev_Indoor, txt_Rev_Outdoor;
    private String Indoor_DB, Outdoor_DB;

    //Bagian Fumigasi
    protected TextView txt_Rev_GasFumigasi;
    private String gasFumigasi;

    //Bagian Penawaran, Status dan Catatan
    protected TextView txt_Rev_Penawaran, txt_Rev_TotalHarga;
    protected TextView txt_Rev_Status, txt_Catatan, txt_Rev_NamaPelanggan, txt_NamaConsultant,
            txt_Rev_Keterangan;

    private String PenawaranHarga_DB, TotalHarga_DB;
    private String status_kerjasama_kontrak, Catatan_DB, keterangan;
    private String dateNow;

    //Bagian TTD
    protected ImageView ttd_Rev_Pelanggan, ttd_Rev_Consultant;
    protected Bitmap getTTD_Consultant, getTTD_Pelanggan;

    private String ConvertImage_Pelanggan, ConvertImage_Consultant;
    private String GetImageNameFromEditText_Pelanggan, GetImageNameFromEditText_Consultant;

    //Bagian 7 : Foto Lampiran
    protected ImageView img_FotoSatu, img_FotoDua, img_FotoTiga, img_FotoEmpat;
    protected Bitmap getFotoSatu, getFotoDua, getFotoTiga, getFotoEmpat;

    private String ConvertImage_FotoSatu, ConvertImage_FotoDua, ConvertImage_FotoTiga, ConvertImage_FotoEmpat;
    private String GetImageNameFromEditText_FotoSatu, GetImageNameFromEditText_FotoDua, GetImageNameFromEditText_FotoTiga,
            GetImageNameFromEditText_FotoEmpat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_survei_fumigasi);

        pDialog = new ProgressDialog(ReviewSurveiFumigasi.this);
        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        file_fumigasi= new File(DIR_FUMIGASI);
        if (!file_fumigasi.exists()) {
            file_fumigasi.mkdir();
        }

        generate_pdf = findViewById(R.id.btn_Generate);
        btnSaveToDB = findViewById(R.id.btn_SaveToDB);

        linearLayout_pdf = findViewById(R.id.LinearLayout_PDF);
        linearLayout_foto = findViewById(R.id.linearLayout_FotoFumigasi);

        //Bagian I
        txt_Rev_IDClient = findViewById(R.id.textView_ReviewID);
        txt_Rev_NamaClient = findViewById(R.id.textView_ReviewNamaClient);
        txt_Rev_Kategori = findViewById(R.id.textView_Kategori);
        txt_Rev_Alamat = findViewById(R.id.textView_Alamat);
        txt_Rev_Hp = findViewById(R.id.textView_Hp);
        txt_Rev_Email = findViewById(R.id.textView_Email);

        //Bagian II
        txt_Rev_GasFumigasi = findViewById(R.id.textView_GasFumigasi);

        txt_Rev_Indoor = findViewById(R.id.textView_Indoor_Fumigasi);
        txt_Rev_Outdoor = findViewById(R.id.textView_Outdoor_Fumigasi);

        //Bagian V
        txt_Rev_Penawaran = findViewById(R.id.textView_Penawaran_Fumigasi);
        txt_Rev_TotalHarga = findViewById(R.id.textView_TotalHarga_Fumigasi);
        txt_Rev_Status = findViewById(R.id.textView_Status_Fumigasi);
        txt_Catatan = findViewById(R.id.textView_Catatan_Fumigasi);

        //Bagian VI
        txt_Rev_NamaPelanggan = findViewById(R.id.textView_NamaPelanggan_Fumigasi);
        txt_NamaConsultant = findViewById(R.id.textView_NamaConsultant_Fumigasi);
        txt_Rev_Keterangan = findViewById(R.id.textView_Keterangan_Fumigasi);

        //Bagian VII
        ttd_Rev_Consultant = findViewById(R.id.imageView_TTD_Consultant_Fumigasi);
        ttd_Rev_Pelanggan = findViewById(R.id.imageView_TTD_Pelanggan_Fumigasi);

        getTTD_Consultant = ((BitmapDrawable) FragmentFormFumigasi.img_ttdConsultant.getDrawable()).getBitmap();
        getTTD_Pelanggan = ((BitmapDrawable) FragmentFormFumigasi.img_ttdPelanggan.getDrawable()).getBitmap();

        //Bagian VIII
        img_FotoSatu = findViewById(R.id.imageView_FotoSatu_Lamp_Fumigasi);
        img_FotoDua = findViewById(R.id.imageView_FotoDua_Lamp_Fumigasi);
        img_FotoTiga = findViewById(R.id.imageView_FotoTiga_Lamp_Fumigasi);
        img_FotoEmpat = findViewById(R.id.imageView_FotoEmpat_Lamp_Fumigasi);

        getFotoSatu = ((BitmapDrawable) FragmentFormFumigasi.img_FotoSatu.getDrawable()).getBitmap();
        getFotoDua = ((BitmapDrawable)FragmentFormFumigasi.img_FotoDua.getDrawable()).getBitmap();
        getFotoTiga = ((BitmapDrawable)FragmentFormFumigasi.img_FotoTiga.getDrawable()).getBitmap();
        getFotoEmpat = ((BitmapDrawable)FragmentFormFumigasi.img_FotoEmpat.getDrawable()).getBitmap();

        Intent intent = getIntent();
        Bundle Extra_Rev_Fumigasi = intent.getExtras();

        //Bagian 1: Data Diri

        IDClient = Extra_Rev_Fumigasi.getString(FragmentFormFumigasi.FumigasiRev_ID_Client_Termite);
        namaPelanggan = Extra_Rev_Fumigasi.getString(FragmentFormFumigasi.FumigasiRev_Nama_Client_Termite);
        kategoriTempatPelanggan = Extra_Rev_Fumigasi.getString(FragmentFormFumigasi.FumigasiRev_Kategori_Client_Termite);
        alamatPelanggan = Extra_Rev_Fumigasi.getString(FragmentFormFumigasi.FumigasiRev_Alamat_Client_Termite);
        hpPelanggan = Extra_Rev_Fumigasi.getString(FragmentFormFumigasi.FumigasiRev_Hp_Client_Termite);
        Email_DB = Extra_Rev_Fumigasi.getString(FragmentFormFumigasi.FumigasiRev_Email_DB);

        LatLong_Consultant = Extra_Rev_Fumigasi.getString(FragmentFormFumigasi.FumigasiRev_LatLong_Consultant);
        LatLong_AlamatPelanggan = Extra_Rev_Fumigasi.getString(FragmentFormFumigasi.FumigasiRev_LatLong_Alamat_Pelanggan);

        gasFumigasi = Extra_Rev_Fumigasi.getString(FragmentFormFumigasi.FumigasiRev_GasFumigasi);

        //Bagian 4: Luas Indoor Outdoor
        Indoor_DB = Extra_Rev_Fumigasi.getString(FragmentFormFumigasi.FumigasiRev_Indoor_DB);
        Outdoor_DB = Extra_Rev_Fumigasi.getString(FragmentFormFumigasi.FumigasiRev_Outdoor_DB);

        //Bagian 6: Harga, Status dan Catatan
        PenawaranHarga_DB = Extra_Rev_Fumigasi.getString(FragmentFormFumigasi.FumigasiRev_PenawaranHarga_DB);
        TotalHarga_DB = Extra_Rev_Fumigasi.getString(FragmentFormFumigasi.FumigasiRev_TotalHarga_DB);
        Catatan_DB = Extra_Rev_Fumigasi.getString(FragmentFormFumigasi.FumigasiRev_Catatan_DB);
        status_kerjasama_kontrak = Extra_Rev_Fumigasi.getString(FragmentFormFumigasi.FumigasiRev_Status_Termite);

        //Bagian 7: Keterangan PDF
        keterangan = Extra_Rev_Fumigasi.getString(FragmentFormFumigasi.FumigasiRev_Keterangan_Termite);

        switch (status_kerjasama_kontrak) {
            case "Berhasil":
                txt_Rev_Status.setBackgroundColor(getResources().getColor(R.color.Berhasil));
                break;
            case "Menunggu":
                txt_Rev_Status.setBackgroundColor(getResources().getColor(R.color.Menunggu));
                break;
            case "Gagal":
                txt_Rev_Status.setBackgroundColor(getResources().getColor(R.color.Gagal));
                break;
        }

        dateNow = new SimpleDateFormat("dd-MM-yy_hh.mm.ss", Locale.getDefault()).format(new Date());
        TampilReviewFumigasi();

        generate_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ReviewSurveiFumigasi.this);

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
                    AlertDialog.Builder builder = new AlertDialog.Builder(ReviewSurveiFumigasi.this);

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
    protected void TampilReviewFumigasi() {
        txt_Rev_IDClient.setText(IDClient);
        txt_Rev_NamaClient.setText(namaPelanggan);
        txt_Rev_Kategori.setText(kategoriTempatPelanggan);
        txt_Rev_Alamat.setText(alamatPelanggan);
        txt_Rev_Hp.setText(hpPelanggan);

        if (Email_DB.equals("")){
            txt_Rev_Email.setText("-");
        }else {
            txt_Rev_Email.setText(Email_DB);
        }

        txt_Rev_GasFumigasi.setText(gasFumigasi);

        txt_Rev_Indoor.setText(Indoor_DB + getString(R.string.text_m2));

        if(Outdoor_DB.equals("")){
            txt_Rev_Outdoor.setText("-");
        }else {
            txt_Rev_Outdoor.setText(Outdoor_DB + getString(R.string.text_m2));
        }

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        double penawaran;
        penawaran = Double.parseDouble(PenawaranHarga_DB);

        txt_Rev_Penawaran.setText(formatRupiah.format(penawaran));
        txt_Rev_TotalHarga.setText(TotalHarga_DB);

        txt_Rev_Status.setText(status_kerjasama_kontrak);
        txt_Catatan.setText(Catatan_DB);

        txt_Rev_NamaPelanggan.setText(namaPelanggan);
        txt_NamaConsultant.setText(TampilanMenuUtama.namaLengkap);
        txt_Rev_Keterangan.setText(keterangan);

        ttd_Rev_Pelanggan.setImageBitmap(getTTD_Pelanggan);
        ttd_Rev_Consultant.setImageBitmap(getTTD_Consultant);

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

        simpan_fumigasi = DIR_FUMIGASI + "Fumigasi_" + IDClient + "_" + dateNow + ".pdf";

        File filePath;
        filePath = new File(simpan_fumigasi);
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
        Toast.makeText(this, "PDF Form Survei Fumigasi Berhasil Dibuat", Toast.LENGTH_SHORT).show();


        //Tampilin Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(ReviewSurveiFumigasi.this);

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
        File file = new File(simpan_fumigasi);
        if (file.exists()) {
            //Uri path = FileProvider.getUriForFile(file);
            Uri path = FileProvider.getUriForFile(ReviewSurveiFumigasi.this, BuildConfig.APPLICATION_ID + ".provider", file);
            Intent intent_view_pdf = new Intent(Intent.ACTION_VIEW);
            intent_view_pdf.setDataAndType(path, "application/pdf");
            intent_view_pdf.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent_view_pdf.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(intent_view_pdf);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(ReviewSurveiFumigasi.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveToDB() {
        //Untuk Ambil Bitmap dari TTD Pelanggan dan Diupload
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        getTTD_Pelanggan.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] imageInByte = stream.toByteArray();
        ConvertImage_Pelanggan = Base64.encodeToString(imageInByte, Base64.DEFAULT);
        GetImageNameFromEditText_Pelanggan = "Fumigasi_" + namaPelanggan + "_" + dateNow + ".png";

        //Untuk Ambil Bitmap dari TTD Consultant dan Diupload
        ByteArrayOutputStream stream_consultant = new ByteArrayOutputStream();
        getTTD_Consultant.compress(Bitmap.CompressFormat.PNG, 50, stream_consultant);
        byte[] imageInByte_consultant = stream_consultant.toByteArray();
        ConvertImage_Consultant = Base64.encodeToString(imageInByte_consultant, Base64.DEFAULT);
        GetImageNameFromEditText_Consultant = "Fumigasi_" + TampilanMenuUtama.username + "_" + dateNow + ".png";

        //Untuk Upload Foto #1
        ByteArrayOutputStream stream_foto_satu = new ByteArrayOutputStream();
        getFotoSatu.compress(Bitmap.CompressFormat.PNG, 70, stream_foto_satu);
        byte[] imageInByte_foto_satu = stream_foto_satu.toByteArray();
        ConvertImage_FotoSatu = Base64.encodeToString(imageInByte_foto_satu, Base64.DEFAULT);
        GetImageNameFromEditText_FotoSatu = "Fumigasi_Foto_Satu_" + namaPelanggan + "_" + dateNow + ".png";

        Log.e("tag 1",   " " + GetImageNameFromEditText_FotoSatu);

        //Untuk Upload Foto #2
        ByteArrayOutputStream stream_foto_dua = new ByteArrayOutputStream();
        getFotoDua.compress(Bitmap.CompressFormat.PNG, 70, stream_foto_dua);
        byte[] imageInByte_foto_dua = stream_foto_dua.toByteArray();
        ConvertImage_FotoDua = Base64.encodeToString(imageInByte_foto_dua, Base64.DEFAULT);
        GetImageNameFromEditText_FotoDua = "Fumigasi_Foto_Dua_" + namaPelanggan + "_" + dateNow + ".png";

        Log.e("tag 2",  " " + GetImageNameFromEditText_FotoDua);

        //Untuk Upload Foto #3
        ByteArrayOutputStream stream_foto_tiga = new ByteArrayOutputStream();
        getFotoTiga.compress(Bitmap.CompressFormat.PNG, 70, stream_foto_tiga);
        byte[] imageInByte_foto_tiga = stream_foto_tiga.toByteArray();
        ConvertImage_FotoTiga = Base64.encodeToString(imageInByte_foto_tiga, Base64.DEFAULT);
        GetImageNameFromEditText_FotoTiga = "Fumigasi_Foto_Tiga_" + namaPelanggan + "_" + dateNow + ".png";

        Log.e("tag 3",  " " + GetImageNameFromEditText_FotoTiga);

        //Untuk Upload Foto #4
        ByteArrayOutputStream stream_foto_empat = new ByteArrayOutputStream();
        getFotoEmpat.compress(Bitmap.CompressFormat.PNG, 70, stream_foto_empat);
        byte[] imageInByte_foto_empat = stream_foto_empat.toByteArray();
        ConvertImage_FotoEmpat = Base64.encodeToString(imageInByte_foto_empat, Base64.DEFAULT);
        GetImageNameFromEditText_FotoEmpat = "Fumigasi_Foto_Empat_" + namaPelanggan + "_" + dateNow + ".png";

        Log.e("tag 4",  " " + GetImageNameFromEditText_FotoEmpat);

        @SuppressLint("StaticFieldLeak")
        class simpan extends AsyncTask<Void, Void, String> {

            private ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ReviewSurveiFumigasi.this, "Menyimpan Data Survei Fumigasi ke Database", "Harap Tunggu...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(ReviewSurveiFumigasi.this, s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();

                //Bagian Data Kostumer -->
                params.put(Konfigurasi.KEY_SAVE_ID_CLIENT_FUMIGASI, IDClient);
                params.put(Konfigurasi.KEY_SAVE_NAMA_PELANGGAN_FUMIGASI, namaPelanggan);
                params.put(Konfigurasi.KEY_SAVE_KATEGORITEMPAT_FUMIGASI, kategoriTempatPelanggan);
                params.put(Konfigurasi.KEY_SAVE_ALAMAT_PELANGGAN_FUMIGASI, alamatPelanggan);
                params.put(Konfigurasi.KEY_SAVE_HP_PELANGGAN_FUMIGASI, hpPelanggan);
                params.put(Konfigurasi.KEY_SAVE_EMAIL_PELANGGAN_FUMIGASI, Email_DB);

                //Bagian Fumigasi
                params.put(Konfigurasi.KEY_SAVE_GASFUMIGASI_FUMIGASI, gasFumigasi);

                //Bagian IV : Luas Indoor Outdoor -->
                params.put(Konfigurasi.KEY_SAVE_LUAS_INDOOR_FUMIGASI, Indoor_DB);
                params.put(Konfigurasi.KEY_SAVE_LUAS_OUTDOOR_FUMIGASI, Outdoor_DB);

                //Bagian VI : Kontrak -->
                params.put(Konfigurasi.KEY_SAVE_PENAWARAN_HARGA_FUMIGASI, PenawaranHarga_DB);
                params.put(Konfigurasi.KEY_SAVE_TOTAL_HARGA_FUMIGASI, TotalHarga_DB);

                //Bagian VII : Status dan Catatan -->
                params.put(Konfigurasi.KEY_SAVE_STATUS_KERJASAMA_FUMIGASI, status_kerjasama_kontrak);
                params.put(Konfigurasi.KEY_SAVE_CATATAN_TAMBAHAN_FUMIGASI, Catatan_DB);

                //Bagian VIII : Untuk TTD - Image Path dan Image -->
                //untuk Pelanggan
                params.put(Konfigurasi.KEY_SAVE_IMAGE_TAG_PELANGGAN_FUMIGASI, GetImageNameFromEditText_Pelanggan);
                params.put(Konfigurasi.KEY_SAVE_IMAGE_NAME_PELANGGAN_FUMIGASI, ConvertImage_Pelanggan);

                //untuk Consultant
                params.put(Konfigurasi.KEY_SAVE_IMAGE_TAG_CONSULTANT_FUMIGASI, GetImageNameFromEditText_Consultant);
                params.put(Konfigurasi.KEY_SAVE_IMAGE_NAME_CONSULTANT_FUMIGASI, ConvertImage_Consultant);

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
                return rh.sendPostRequest(Konfigurasi.URL_SIMPAN_FUMIGASI_SURVEI, params);

            }
        }
        simpan save = new simpan();
        save.execute();

    }

    private void checkIDPelanggan(final String idPelanggan){
        pDialog.setMessage("Melakukan Validasi ID Pelanggan, Harap Menunggu...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, Konfigurasi.URL_CHECK_ID_PELANGGAN_FUMIGASI, new Response.Listener<String>() {
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(ReviewSurveiFumigasi.this);
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
