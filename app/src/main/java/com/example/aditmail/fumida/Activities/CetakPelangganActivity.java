package com.example.aditmail.fumida.Activities;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CetakPelangganActivity extends AppCompatActivity {
    private TableLayout tableLayout;
    private Button simpanPdf;
    private LinearLayout layoutPdf;
    private TextView tglLaporan, ttdName;
    private String dateNow, dateLaporan;
    String DIR_PEST = Environment.getExternalStorageDirectory().getPath() + "/Fumida_Report/";
    String simpan_pest;
    private Bitmap bitmap;
    File file_pest;
    private String JSON_STRING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cetak_pelanggan);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        tableLayout = findViewById(R.id.dynamic_table_pest);
        simpanPdf = findViewById(R.id.btn_save_pdf);
        layoutPdf = findViewById(R.id.LinearLayout_PDF);
        tglLaporan = findViewById(R.id.txt_tanggal);
        ttdName = findViewById(R.id.ttd_name);
        dateNow = new SimpleDateFormat("dd-MM-yy_hh.mm.ss", Locale.getDefault()).format(new Date());
        dateLaporan = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date());
        tglLaporan.setText(String.format(getResources().getString(R.string.tgl_laporan), dateLaporan));
        ttdName.setText(TampilanMenuUtama.namaLengkap);
        file_pest = new File(DIR_PEST);
        if (!file_pest.exists()) {
            file_pest.mkdir();
        }
        getJSON();
        simpanPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bitmap = loadBitmapFromView(layoutPdf, layoutPdf.getWidth(), layoutPdf.getHeight(), layoutPdf.getPaddingLeft(), layoutPdf.getPaddingRight());
                createPdf();
            }
        });
    }
    private void getJSON() {
        @SuppressLint("StaticFieldLeak")
        class GetJSON extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                JSON_STRING = s;
                setData();
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                return rh.sendGetRequestParam(Konfigurasi.URL_VIEW_Pelanggan, TampilanMenuUtama.id_pegawai);
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }
    private void setData(){
        JSONObject jsonObject;
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        Drawable bgCell = ContextCompat.getDrawable(this, R.drawable.ic_cell_table);
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(Konfigurasi.TAG_JSON_ARRAY);

            //untuk melakukan looping
            //untuk mengetahui apabila seluruh transaksi telah dimasukkan
            for (int i = 0; i < result.length(); i++) {
                TableRow.LayoutParams params = new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                TableRow tableRow = new TableRow(this);
                tableRow.setLayoutParams(params);

                TextView IdClient = new TextView(this);
                TextView NamaPelanggan = new TextView(this);
                TextView JenisPengerjaan = new TextView(this);
                TextView Tanggal = new TextView(this);

                JSONObject jo = result.getJSONObject(i);

                String idClient = jo.getString("id_pelanggan");
                String namaPelanggan = jo.getString("nama_pelanggan");
                String jenisPengerjaan = jo.getString("jenis_pengerjaan");
                String tanggal = jo.getString(Konfigurasi.WORKREPORT_KEY_GET_LIST_TANGGAL);

                IdClient.setText(idClient);
                NamaPelanggan.setText(namaPelanggan);
                JenisPengerjaan.setText(jenisPengerjaan);
                Tanggal.setText(tanggal);

                IdClient.setBackground(bgCell);
                NamaPelanggan.setBackground(bgCell);
                JenisPengerjaan.setBackground(bgCell);
                Tanggal.setBackground(bgCell);

                IdClient.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
                NamaPelanggan.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
                JenisPengerjaan.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
                Tanggal.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);

                tableRow.addView(IdClient, (new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT,0.5f)));
                tableRow.addView(NamaPelanggan, (new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT,0.7f)));
                tableRow.addView(JenisPengerjaan, (new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT,0.5f)));
                tableRow.addView(Tanggal,(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT,1.0f)));
                tableLayout.addView(tableRow);
            }

        }  catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static Bitmap loadBitmapFromView(View v, int width, int height, int paddingLeft, int paddingRight) {
        Bitmap b = Bitmap.createBitmap( width+paddingLeft+paddingRight, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);
        return b;
    }
    private void openGeneratedPDF() {
        File file = new File(simpan_pest);
        if (file.exists()) {
            Uri path = FileProvider.getUriForFile(CetakPelangganActivity.this, BuildConfig.APPLICATION_ID + ".provider", file);
            Intent intent_view_pdf = new Intent(Intent.ACTION_VIEW);
            intent_view_pdf.setDataAndType(path, "application/pdf");
            intent_view_pdf.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent_view_pdf.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(intent_view_pdf);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(CetakPelangganActivity.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void createPdf() {
        //WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        //  Display display = wm.getDefaultDisplay();
        float height = this.getResources().getDisplayMetrics().heightPixels;
        float width = this.getResources().getDisplayMetrics().widthPixels;

        int convertHeight = (int) height;
        int convertWidth = (int) width;

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

        simpan_pest = DIR_PEST + "Cetak_"+ "Pelanggan_" + dateNow + ".pdf";

        File filePath;
        filePath = new File(simpan_pest);
        try {
            document.writeTo(new FileOutputStream(filePath));

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Terjadi Kesalahan! " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();
        Toast.makeText(this, "PDF Form Work Report Berhasil Dibuat", Toast.LENGTH_SHORT).show();

        //Tampilin Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(CetakPelangganActivity.this);

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
}