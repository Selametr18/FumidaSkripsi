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

public class PreviewSurveiFumigasi extends AppCompatActivity {

    ConnectivityManager conMgr;
    ProgressDialog pDialog;

    File file_fumigasi;
    String DIR_FUMIGASI = Environment.getExternalStorageDirectory().getPath() + "/Fumida_Fumigasi/";
    String simpan_fumigasi;

    protected String id_pegawai, id_survei;

    protected Button generate_pdf, btnSaveToDB;
    protected LinearLayout linearLayout_pdf, linearLayout_foto;
    private Bitmap bitmap, bitmap_dua;

    //Bagian 1: Data Diri
    protected TextView txt_Rev_IDClient, txt_Rev_NamaClient, txt_Rev_Kategori, txt_Rev_Alamat,
            txt_Rev_Hp, txt_Rev_Email;

    //Bagian 3: Metode Kendali dan Luas
    protected TextView txt_Rev_Indoor, txt_Rev_Outdoor;

    //Bagian Fumigasi
    protected TextView txt_Rev_GasFumigasi;

    //Bagian Penawaran, Status dan Catatan
    protected TextView txt_Rev_Penawaran, txt_Rev_TotalHarga;
    protected TextView txt_Rev_Status, txt_Catatan, txt_Rev_NamaPelanggan, txt_NamaConsultant,
            txt_Rev_Keterangan;

    private String dateNow;

    //Bagian TTD
    protected ImageView ttd_Rev_Pelanggan, ttd_Rev_Consultant;

    //Bagian 7 : Foto Lampiran
    protected ImageView img_FotoSatu, img_FotoDua, img_FotoTiga, img_FotoEmpat;

    protected SimpleDateFormat dateFormat;
    protected Date date_tglInput, date_TimeStamp;
    protected String tgl_input_Fixed, timestamp_Fixed;

    protected String NamaPelanggan;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_survei_fumigasi);

        Intent a = getIntent();
        id_pegawai = a.getStringExtra(Konfigurasi.KEY_TAG_ID);
        id_survei = a.getStringExtra(Konfigurasi.KEY_SURVEI_ID);

        InitData();

        dateNow = new SimpleDateFormat("dd-MM-yy_hh.mm.ss", Locale.getDefault()).format(new Date());
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr!=null) {
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
                    getDataSurveiFumigasi();
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void InitData() {
        pDialog = new ProgressDialog(PreviewSurveiFumigasi.this);
        pDialog.setCancelable(true);
        pDialog.setCanceledOnTouchOutside(false);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        file_fumigasi = new File(DIR_FUMIGASI);
        if (!file_fumigasi.exists()) {
            file_fumigasi.mkdir();
        }

        generate_pdf = findViewById(R.id.btn_Generate);
        generate_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Tampilin Alert Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(PreviewSurveiFumigasi.this);

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

        //Bagian VIII
        img_FotoSatu = findViewById(R.id.imageView_FotoSatu_Lamp_Fumigasi);
        img_FotoDua = findViewById(R.id.imageView_FotoDua_Lamp_Fumigasi);
        img_FotoTiga = findViewById(R.id.imageView_FotoTiga_Lamp_Fumigasi);
        img_FotoEmpat = findViewById(R.id.imageView_FotoEmpat_Lamp_Fumigasi);
    }

    private void getDataSurveiFumigasi() {
        @SuppressLint("StaticFieldLeak")
        class GetDataSurveiFumigasi extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                pDialog.setMessage("Mengambil Data, Harap Menunggu...");
                showDialog();
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                hideDialog();
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

    @SuppressLint("SetTextI18n")
    private void showDataSurveiFumigasi(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray(Konfigurasi.TAG_JSON_ARRAY);
            JSONObject c = result.getJSONObject(0);

            String ClientID = c.getString(Konfigurasi.FUMIGASI_KEY_GET_TAG_CLIENT_ID);
            NamaPelanggan = c.getString(Konfigurasi.FUMIGASI_KEY_GET_TAG_NAMA_PELANGGAN);
            String KategoriTempat = c.getString(Konfigurasi.FUMIGASI_KEY_GET_TAG_KATEGORI_TEMPAT);
            String Alamat = c.getString(Konfigurasi.FUMIGASI_KEY_GET_TAG_ALAMAT);
            String NoHP = c.getString(Konfigurasi.FUMIGASI_KEY_GET_TAG_HP);
            String Email = c.getString(Konfigurasi.FUMIGASI_KEY_GET_TAG_EMAIL);

            String GasFumigasi = c.getString(Konfigurasi.FUMIGASI_KEY_GET_GAS_FUMIGASI);

            String GetTglInput = c.getString("tgl_input");
            String GetTimeStamp = c.getString("timestamp");

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

            txt_Rev_IDClient.setText(ClientID);
            txt_Rev_NamaClient.setText(NamaPelanggan);
            txt_Rev_Kategori.setText(KategoriTempat);
            txt_Rev_Alamat.setText(Alamat);
            txt_Rev_Hp.setText(NoHP);

            if (Email.equals("")){
                txt_Rev_Email.setText("-");
            }else {
                txt_Rev_Email.setText(Email);
            }

            txt_Rev_GasFumigasi.setText(GasFumigasi);

            txt_Rev_Indoor.setText(LuasIndoor + getString(R.string.text_m2));

            if(LuasOutdoor.equals("")){
                txt_Rev_Outdoor.setText("-");
            }else {
                txt_Rev_Outdoor.setText(LuasOutdoor + getString(R.string.text_m2));
            }

            Locale localeID = new Locale("in", "ID");
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
            double penawaran;
            penawaran = Double.parseDouble(PenawaranHarga);

            txt_Rev_Penawaran.setText(formatRupiah.format(penawaran));
            txt_Rev_TotalHarga.setText(TotalHarga);

            txt_Rev_Status.setText(StatusKerjasama);
            switch (StatusKerjasama) {
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

            txt_Catatan.setText(CatatanTambahan);

            txt_Rev_NamaPelanggan.setText(NamaPelanggan);
            txt_NamaConsultant.setText(TampilanMenuUtama.namaLengkap);

            try {
                Glide.with(this)
                        .load(Konfigurasi.url_image + GetImgPelanggan_TTD)
                        .error(Glide.with(ttd_Rev_Pelanggan).load(R.drawable.ic_ttd_not_found))
                        .into(ttd_Rev_Pelanggan);

                Glide.with(this)
                        .load(Konfigurasi.url_image + GetImgConsultant_TTD)
                        .error(Glide.with(ttd_Rev_Consultant).load(R.drawable.ic_ttd_not_found))
                        .into(ttd_Rev_Consultant);

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
            }catch (NullPointerException e){
                e.printStackTrace();
            }

            if (GetTglInput.equalsIgnoreCase(GetTimeStamp)) {
                try {
                    date_tglInput = dateFormat.parse(GetTglInput);
                    dateFormat.applyPattern("dd/MM/yyyy HH:mm:ss");

                    tgl_input_Fixed = dateFormat.format(date_tglInput);
                }catch (ParseException e){
                    e.printStackTrace();
                    Log.e("tag", String.valueOf(e));
                }

                final String keterangan = "*Berkas ini Dibuat Secara Otomatis oleh Sistem Mobile Apps Fumida pada Tanggal " + tgl_input_Fixed;
                txt_Rev_Keterangan.setText(keterangan);

            } else if(!GetTglInput.equalsIgnoreCase(GetTimeStamp)){
                try {
                    date_tglInput = dateFormat.parse(GetTglInput);
                    date_TimeStamp = dateFormat.parse(GetTimeStamp);

                    dateFormat.applyPattern("dd/MM/yyyy HH:mm:ss");

                    tgl_input_Fixed = dateFormat.format(date_tglInput);
                    timestamp_Fixed = dateFormat.format(date_TimeStamp);
                }catch (ParseException e){
                    e.printStackTrace();
                    Log.e("tag", String.valueOf(e));
                }

                final String keterangan = "*Berkas ini Dibuat Secara Otomatis oleh Sistem Mobile Apps Fumida pada Tanggal " + tgl_input_Fixed + "\n";
                final String revisi = "**Telah Direvisi pada Tanggal " + timestamp_Fixed;
                txt_Rev_Keterangan.setText(keterangan+revisi);
            }

        } catch (JSONException e) {
            e.printStackTrace();
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

        simpan_fumigasi = DIR_FUMIGASI + "Fumigasi_" + NamaPelanggan + "_" + dateNow + ".pdf";

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
        AlertDialog.Builder builder = new AlertDialog.Builder(PreviewSurveiFumigasi.this);

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
            Uri path = FileProvider.getUriForFile(PreviewSurveiFumigasi.this, BuildConfig.APPLICATION_ID + ".provider", file);
            Intent intent_view_pdf = new Intent(Intent.ACTION_VIEW);
            intent_view_pdf.setDataAndType(path, "application/pdf");
            intent_view_pdf.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent_view_pdf.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(intent_view_pdf);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(PreviewSurveiFumigasi.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
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
