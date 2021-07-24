package com.example.aditmail.fumida.PestControl;

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
import android.widget.TableRow;
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

public class PreviewSurveiPestControl extends AppCompatActivity {

    ConnectivityManager conMgr;
    ProgressDialog pDialog;

    File file_pest;
    String DIR_PEST = Environment.getExternalStorageDirectory().getPath() + "/Fumida_PestControl/";
    String simpan_pest;

    String id_pegawai, id_survei;

    protected Button generate_pdf, btnSaveToDB;
    protected LinearLayout linearLayout_pdf, linearLayout_foto;
    private Bitmap bitmap, bitmap_dua;

    protected String NamaPelanggan;

    //Bagian 1: Data Diri
    private TextView txt_Rev_IDClient_Pest, txt_Rev_NamaClient_Pest, txt_Rev_Kategori_Pest, txt_Rev_Alamat_Pest,
            txt_Rev_Hp_Pest, txt_Rev_Email_Pest;

    //Bagian 2: Jenis Hama dan Penanganan
    private TextView txt_Hama_Pest, txt_KategoriPenanganan_Pest;

    //Bagian 3: Chemical Hama dan Metode Kendali
    private TextView txt_Chemical_Pest, txt_Rev_MetodeKendali_Pest;

    //Bagian 4: Luas Indoor Outdoor
    private TextView txt_Rev_Indoor_Pest, txt_Rev_Outdoor_Pest;

    //Bagian 5: Kontrak dan Harga
    private TextView txt_Rev_JenisKerja_Pest, txt_Rev_Penawaran_Pest,
            txt_Rev_Durasi_Pest, txt_Rev_TotalHarga_Pest;
    private TextView txt_PenawaranHargaNonKontrak, txt_PenawaranHargaKontrak;
    private TableRow tbl_DurasiKontrak_Pest;

    //Bagian 6: Status Kerjasama, Catatan dan TTD
    private String dateNow;
    private TextView txt_Rev_Status_Pest, txt_Catatan_Pest;
    private TextView txt_Rev_NamaPelanggan_Pest, txt_NamaConsultant_Pest, txt_Rev_Keterangan_Pest;
    private ImageView ttd_Rev_Pelanggan_Pest, ttd_Rev_Consultant_Pest;

    //Bagian 7 : Foto Lampiran
    private ImageView img_FotoSatu, img_FotoDua, img_FotoTiga, img_FotoEmpat;

    protected SimpleDateFormat dateFormat;
    protected Date date_tglInput, date_TimeStamp;
    protected String tgl_input_Fixed, timestamp_Fixed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_survei_pest_control);

        Intent a = getIntent();
        id_pegawai = a.getStringExtra(Konfigurasi.KEY_TAG_ID);
        id_survei = a.getStringExtra(Konfigurasi.KEY_SURVEI_ID);

        InitData();

        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
            getDataSurveiPest();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection",
                    Toast.LENGTH_LONG).show();
        }

    }

    @SuppressLint("SimpleDateFormat")
    private void InitData(){
        pDialog = new ProgressDialog(PreviewSurveiPestControl.this);
        pDialog.setCancelable(true);
        pDialog.setCanceledOnTouchOutside(false);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        dateNow = new SimpleDateFormat("dd-MM-yy_hh.mm.ss", Locale.getDefault()).format(new Date());
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        file_pest = new File(DIR_PEST);
        if (!file_pest.exists()) {
            file_pest.mkdir();
        }

        generate_pdf = findViewById(R.id.btn_Generate);
        generate_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Tampilin Alert Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(PreviewSurveiPestControl.this);

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

        //Image lampiran
        img_FotoSatu = findViewById(R.id.imageView_FotoSatu_Lamp_Pest);
        img_FotoDua = findViewById(R.id.imageView_FotoDua_Lamp_Pest);
        img_FotoTiga = findViewById(R.id.imageView_FotoTiga_Lamp_Pest);
        img_FotoEmpat = findViewById(R.id.imageView_FotoEmpat_Lamp_Pest);
    }

    private void getDataSurveiPest(){
        @SuppressLint("StaticFieldLeak")
        class GetDataSurveiPest extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {
                pDialog.setMessage("Mengambil Data, Harap Menunggu..");
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

    @SuppressLint("SetTextI18n")
    private void showDataSurveiPest(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray(Konfigurasi.TAG_JSON_ARRAY);
            JSONObject c = result.getJSONObject(0);

            String ClientID = c.getString(Konfigurasi.PEST_KEY_GET_TAG_CLIENT_ID);
            NamaPelanggan = c.getString(Konfigurasi.PEST_KEY_GET_TAG_NAMA_PELANGGAN);
            String KategoriTempat = c.getString(Konfigurasi.PEST_KEY_GET_TAG_KATEGORI_TEMPAT);
            String Alamat = c.getString(Konfigurasi.PEST_KEY_GET_TAG_ALAMAT);
            String NoHP= c.getString(Konfigurasi.PEST_KEY_GET_TAG_HP);
            String Email = c.getString(Konfigurasi.PEST_KEY_GET_TAG_EMAIL);

            String JenisHama = c.getString(Konfigurasi.PEST_KEY_GET_TAG_JENIS_HAMA);
            String HamaLainnya = c.getString(Konfigurasi.PEST_KEY_GET_TAG_HAMA_LAINNYA);
            String KategoriPenanganan = c.getString(Konfigurasi.PEST_KEY_GET_TAG_KATEGORI_PENANGANAN);
            String QtyPenanganan = c.getString(Konfigurasi.PEST_KEY_GET_TAG_QTY_PENANGANAN);
            String JenisChemical = c.getString(Konfigurasi.PEST_KEY_GET_TAG_CHEMICAL_PEST);
            String MetodeKendali = c.getString(Konfigurasi.PEST_KEY_GET_TAG_METODE_KENDALI);
            String QtyLemTikus = c.getString(Konfigurasi.PEST_KEY_GET_TAG_QTY_LEMTIKUS);
            String QtyPerangkapTikus = c.getString(Konfigurasi.PEST_KEY_GET_TAG_QTY_PERANGKAPTIKUS);
            String QtyUmpanTikus = c.getString(Konfigurasi.PEST_KEY_GET_TAG_QTY_UMPANOUTDOOR);
            String QtyUmpanTikusIndoor = c.getString(Konfigurasi.PEST_KEY_GET_TAG_QTY_UMPANINDOOR);
            String QtyPohonLalat = c.getString(Konfigurasi.PEST_KEY_GET_TAG_QTY_POHONLALAT);
            String QtyBlackHole = c.getString(Konfigurasi.PEST_KEY_GET_TAG_QTY_BLACKHOLE);
            String JenisFumigasi = c.getString(Konfigurasi.PEST_KEY_GET_TAG_JENIS_FUMIGASI);
            String QtyFlyCatcher = c.getString(Konfigurasi.PEST_KEY_GET_TAG_QTY_FLYCATCHER);
            String MetodeLain = c.getString(Konfigurasi.PEST_KEY_GET_TAG_METODE_LAIN);
            String LuasIndoor = c.getString(Konfigurasi.PEST_KEY_GET_TAG_LUAS_INDOOR);
            String LuasOutdoor = c.getString(Konfigurasi.PEST_KEY_GET_TAG_LUAS_OUTDOOR);
            String JenisKontrak = c.getString(Konfigurasi.PEST_KEY_GET_JENIS_KONTRAK);
            String DurasiKontrak = c.getString(Konfigurasi.PEST_KEY_GET_TAG_DURASI_KONTRAK);
            String PenawaranHarga = c.getString(Konfigurasi.PEST_KEY_GET_TAG_PENAWARAN_HARGA);
            String TotalHarga = c.getString(Konfigurasi.PEST_KEY_GET_TAG_TOTAL_HARGA);
            String StatusKerjasama = c.getString(Konfigurasi.PEST_KEY_GET_TAG_STATUS_KERJASAMA);
            String CatatanTambahan = c.getString(Konfigurasi.PEST_KEY_GET_TAG_CATATAN);
            String GetImgPelanggan_TTD = c.getString(Konfigurasi.PEST_KEY_GET_IMG_PELANGGAN);
            String GetImgConsultant_TTD = c.getString(Konfigurasi.PEST_KEY_GET_IMG_CONSULTANT);

            String GetTglInput = c.getString("tgl_input");
            String GetTimeStamp = c.getString("timestamp");

            Log.e("tag", GetTglInput + "," + GetTimeStamp);

            //Bagian VI : Foto
            String pathFotoSatu = c.getString(Konfigurasi.PEST_KEY_GET_TAG_IMG_FOTO_SATU);
            String pathFotoDua = c.getString(Konfigurasi.PEST_KEY_GET_TAG_IMG_FOTO_DUA);
            String pathFotoTiga = c.getString(Konfigurasi.PEST_KEY_GET_TAG_FOTO_TIGA);
            String pathFotoEmpat = c.getString(Konfigurasi.PEST_KEY_GET_TAG_FOTO_EMPAT);

            Log.e("tag", ClientID);

            txt_Rev_IDClient_Pest.setText(ClientID);
            txt_Rev_NamaClient_Pest.setText(NamaPelanggan);
            txt_Rev_Alamat_Pest.setText(Alamat);
            txt_Rev_Hp_Pest.setText(NoHP);
            txt_Rev_Kategori_Pest.setText(KategoriTempat);

            if (Email.equals("")) {
                txt_Rev_Email_Pest.setText("-");
            } else {
                txt_Rev_Email_Pest.setText(Email);
            }

            //get List Jenis Hama
            String parse_jenis_hama = "";
            StringBuffer sb_hama = new StringBuffer(parse_jenis_hama);

            String[] JenisHama_Arr = JenisHama.split(",");
            for (String list_hama : JenisHama_Arr) {
                if (list_hama.equalsIgnoreCase("Lainnya")) {
                    sb_hama.append(list_hama).append(" : ").append(HamaLainnya).append(", ");
                } else {
                    //parse_jenis_hama += list_hama + ", ";
                    sb_hama.append(list_hama).append(", ");
                }
            }
            sb_hama.deleteCharAt(sb_hama.length() - 2);
            txt_Hama_Pest.setText(sb_hama);

            //Kategori Penanganan
            if (KategoriPenanganan.equalsIgnoreCase("Sistem Mobile")) {
                String mobile = KategoriPenanganan + " : " + QtyPenanganan + " x Kunjungan/Bulan";
                txt_KategoriPenanganan_Pest.setText(mobile);
            } else if (KategoriPenanganan.equalsIgnoreCase("Standby/Station")) {
                String standby = KategoriPenanganan + " : " + QtyPenanganan + " Orang";
                txt_KategoriPenanganan_Pest.setText(standby);
            } else if (KategoriPenanganan.equalsIgnoreCase("One Time Service")) {
                txt_KategoriPenanganan_Pest.setText(KategoriPenanganan);
            }

            //getChemical List
            String parse_chemical_hama = "";
            StringBuffer sb_chemical = new StringBuffer(parse_chemical_hama);

            String[] JenisChemical_Arr = JenisChemical.split(",");
            for (String list_chemical : JenisChemical_Arr) {
                sb_chemical.append(list_chemical).append(", ");
            }
            sb_chemical.deleteCharAt(sb_chemical.length() - 2);
            txt_Chemical_Pest.setText(sb_chemical);

            //Show Metode Kendali
            String[] MetodeKendaliHama_Arr = MetodeKendali.split(",");
            String parse_metode_kendali_hama = "";
            StringBuffer sb_metode = new StringBuffer(parse_metode_kendali_hama);
            for (String list_metode_hama : MetodeKendaliHama_Arr) {
                if (list_metode_hama.equalsIgnoreCase("Lem Tikus (Rodent Glue Trap)")) {
                    sb_metode.append("- ").append(list_metode_hama).append(" : ").append(QtyLemTikus).append(" Buah").append("\n");
                }
                else if (list_metode_hama.equalsIgnoreCase("Perangkap Tikus Massal (Rodent Trap)")) {
                    sb_metode.append("- ").append(list_metode_hama).append(" : ").append(QtyPerangkapTikus).append(" Buah").append("\n");
                }
                else if (list_metode_hama.equalsIgnoreCase("Umpan Tikus (Rodent Baiting Outdoor)")) {
                    sb_metode.append("- ").append(list_metode_hama).append(" : ").append(QtyUmpanTikus).append(" Buah").append("\n");
                }
                else if (list_metode_hama.equalsIgnoreCase("Umpan Tikus (Rodent Baiting Indoor)")) {
                    sb_metode.append("- ").append(list_metode_hama).append(" : ").append(QtyUmpanTikusIndoor).append(" Buah").append("\n");
                }
                else if (list_metode_hama.equalsIgnoreCase("Pohon Lalat")) {
                    sb_metode.append("- ").append(list_metode_hama).append(" : ").append(QtyPohonLalat).append(" Buah").append("\n");
                }
                else if (list_metode_hama.equalsIgnoreCase("Instalasi Black Hole")) {
                    sb_metode.append("- ").append(list_metode_hama).append(" : ").append(QtyBlackHole).append(" Buah").append("\n");
                }
                else if (list_metode_hama.equalsIgnoreCase("Fumigasi")) {
                    sb_metode.append("- ").append(list_metode_hama).append(" : ").append(JenisFumigasi).append("\n");
                }
                else if (list_metode_hama.equalsIgnoreCase("Instalasi Fly Catcher")) {
                    sb_metode.append("- ").append(list_metode_hama).append(" : ").append(QtyFlyCatcher).append(" Buah").append("\n");
                }
                else if (list_metode_hama.equalsIgnoreCase("Metode Lainnya")) {
                    sb_metode.append("- ").append(list_metode_hama).append(" : ").append(MetodeLain).append("\n");
                    Log.e("tag", MetodeLain);
                } else {
                    sb_metode.append("- ").append(list_metode_hama).append("\n");
                }
            }
            txt_Rev_MetodeKendali_Pest.setText(sb_metode);

            //Luas Indoor dan Outdoor
            txt_Rev_Indoor_Pest.setText(LuasIndoor + getString(R.string.text_m2));
            if (LuasOutdoor.equals("")) {
                txt_Rev_Outdoor_Pest.setText("-");
            } else {
                txt_Rev_Outdoor_Pest.setText(LuasOutdoor + getString(R.string.text_m2));
            }

            //Untuk Kontrak / Non Kontrak
            if (JenisKontrak.equals("Non-Kontrak")) {
                tbl_DurasiKontrak_Pest.setVisibility(View.GONE);
                txt_PenawaranHargaKontrak.setVisibility(View.GONE);
                txt_PenawaranHargaNonKontrak.setVisibility(View.VISIBLE);
            } else if (JenisKontrak.equals("Kontrak")) {
                tbl_DurasiKontrak_Pest.setVisibility(View.VISIBLE);
                txt_PenawaranHargaKontrak.setVisibility(View.VISIBLE);
                txt_PenawaranHargaNonKontrak.setVisibility(View.GONE);

                txt_Rev_Durasi_Pest.setText(DurasiKontrak + " Bulan");
            }
            txt_Rev_JenisKerja_Pest.setText(JenisKontrak);

            Locale localeID = new Locale("in", "ID");
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

            String penawaran_harga_convert = formatRupiah.format(Double.valueOf(PenawaranHarga));
            String total_harga_convert = formatRupiah.format(Double.valueOf(TotalHarga));

            txt_Rev_Penawaran_Pest.setText(penawaran_harga_convert);
            txt_Rev_TotalHarga_Pest.setText(total_harga_convert);

            txt_Rev_Status_Pest.setText(StatusKerjasama);

            //Execute Data....
            switch (StatusKerjasama) {
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

            if (CatatanTambahan.equals("")) {
                txt_Catatan_Pest.setText("-");
            } else {
                txt_Catatan_Pest.setText(CatatanTambahan);
            }

            txt_Rev_NamaPelanggan_Pest.setText(NamaPelanggan);
            txt_NamaConsultant_Pest.setText(TampilanMenuUtama.namaLengkap);

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
                        .error(Glide.with(ttd_Rev_Pelanggan_Pest).load(R.drawable.ic_ttd_not_found))
                        .into(ttd_Rev_Pelanggan_Pest);

                Glide.with(getApplicationContext())
                        .load(Konfigurasi.url_image + GetImgConsultant_TTD)
                        .error(Glide.with(ttd_Rev_Consultant_Pest).load(R.drawable.ic_ttd_not_found))
                        .into(ttd_Rev_Consultant_Pest);
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
                txt_Rev_Keterangan_Pest.setText(keterangan);

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
                txt_Rev_Keterangan_Pest.setText(keterangan+revisi);
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

        simpan_pest = DIR_PEST + "Pest_" + NamaPelanggan + "_" + dateNow + ".pdf";

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
        AlertDialog.Builder builder = new AlertDialog.Builder(PreviewSurveiPestControl.this);

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
            Uri path = FileProvider.getUriForFile(PreviewSurveiPestControl.this, BuildConfig.APPLICATION_ID + ".provider", file);
            Intent intent_view_pdf = new Intent(Intent.ACTION_VIEW);
            intent_view_pdf.setDataAndType(path, "application/pdf");
            intent_view_pdf.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent_view_pdf.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(intent_view_pdf);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(PreviewSurveiPestControl.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
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
