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
import android.widget.Space;
import android.widget.TableLayout;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class PreviewWorkReport extends AppCompatActivity {

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

    TableRow tbl_helper;
    Space spc_helper;

    protected SimpleDateFormat dateFormat;
    protected Date date_tglInput, date_TimeStamp;
    protected String tgl_input_Fixed, timestamp_Fixed;

    protected String id_pegawai, id_survei;

    //-------------- Bagian I : Jenis Pekerjaan --------------
    private TextView txt_Rev_JenisPengerjaan, txt_Rev_IDClient_WorkReport, txt_Rev_NamaClient_WorkReport, txt_Rev_Alamat_WorkReport, txt_Rev_Pekerjaan_WorkReport;
    //-------------- Bagian I : Jenis Pekerjaan --------------

    //-------------- Bagian II : Pest Control --------------
    private TextView txt_Rev_JenisHama_WorkReport, txt_Rev_MetodeKendaliHama_WorkReport;
    //-------------- Bagian II : Pest Control --------------

    //-------------- Bagian III : Termite Control --------------
    private TextView txt_Rev_Rayap_WorkReport, txt_Rev_MetodeKendaliRayap_WorkReport, txt_Rev_ChemicalTermite_WorkReport;
    //-------------- Bagian III : Termite Control --------------

    //-------------- Bagian IV : Fumigasi Control --------------
    private TextView txt_Rev_GasFumigasi_WorkReport;
    //-------------- Bagian IV : Fumigasi Control --------------

    //-------------- Bagian V : Waktu Mulai --------------
    private TextView txt_Rev_Worker_WorkReport;
    private TextView txt_Rev_WaktuMulai_WorkReport, txt_Rev_WaktuSelesai_WorkReport;

    private String dateNow;
    private String nominalValue, ketNominalValue, kendaraanValue;
    //-------------- Bagian V : Waktu Mulai --------------

    //-------------- Bagian VI : TTD --------------
    private TextView txt_Rev_NamaConsultant_WorkReport, txt_Rev_NamaPelanggan_WorkReport, txt_Rev_Keterangan_WorkReport, txt_nominal, txt_ket_nominal, txt_kendaraan;
    private ImageView ttd_Rev_Pelanggan_WorkReport, ttd_Rev_Consultant_WorkReport;
    //-------------- Bagian VI : TTD --------------

    //-------------- Bagian VII : Upload Foto --------------
    private ImageView img_FotoSatu, img_FotoDua, img_FotoTiga, img_FotoEmpat;
    //-------------- Bagian VII : Upload Foto --------------

    //getData from DB ----------------------------------------------------------->
    protected String jenisPengerjaan, idClient, NamaPelanggan, alamatPelanggan, pekerjaan, totalHama, hamaLain, metodeKendaliHama,
            qtyLemTikus, qtyPerangkapTikus, qtyUmpanTikusOutdoor, qtyUmpanTikusIndoor, qtyPohonLalat, qtyBlackhole,
            jenisFumigasi, qtyFlycatcher, metodeLain, totalRayap, metodeKendaliRayap, chemicalTermite, qtyFipronil, qtyImidaclporid,
            qtyCypermethrin, qtyDicholorphos, qtyBaitingAG, qtyBaitingIG, gasFumigasi, waktuMulai, waktuSelesai, worker, GetTglInput,
            GetTimeStamp, pathFotoSatu, pathFotoDua, pathFotoTiga, pathFotoEmpat, pathTTDClient, pathTTDConsultant;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_work_report);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateNow = new SimpleDateFormat("dd-MM-yy_hh.mm.ss", Locale.getDefault()).format(new Date());

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        pDialog = new ProgressDialog(PreviewWorkReport.this);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(true);

        Intent a = getIntent();
        id_pegawai = a.getStringExtra(Konfigurasi.KEY_TAG_ID);
        id_survei = a.getStringExtra(Konfigurasi.KEY_SURVEI_ID);

        InitData();

        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
            getDataWorkReport();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection",
                    Toast.LENGTH_LONG).show();
        }

    }

    private void InitData() {
        file_workReport = new File(DIR_WORKREPORT);
        if (!file_workReport.exists()) {
            file_workReport.mkdir();
        }

        generate_pdf = findViewById(R.id.btn_Generate);

        generate_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PreviewWorkReport.this);

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
        tbr_Fumigasi = findViewById(R.id.TableRow_GasFumigasi);
        txt_Rev_GasFumigasi_WorkReport = findViewById(R.id.textView_GasFumigasi_WorkReport);
        txt_nominal = findViewById(R.id.txtview_pembayaran_value);
        txt_ket_nominal = findViewById(R.id.txtView_ket_bayar_value);
        txt_kendaraan = findViewById(R.id.txtview_kendaraan_value);

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

        //Bagian VII
        img_FotoSatu = findViewById(R.id.imageView_FotoSatu_Lamp);
        img_FotoDua = findViewById(R.id.imageView_FotoDua_Lamp);
        img_FotoTiga = findViewById(R.id.imageView_FotoTiga_Lamp);
        img_FotoEmpat = findViewById(R.id.imageView_FotoEmpat_Lamp);
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
            NamaPelanggan = c.getString(Konfigurasi.WORKREPORT_KEY_GET_TAG_NAMA_PELANGGAN);
            alamatPelanggan = c.getString(Konfigurasi.WORKREPORT_KEY_GET_TAG_ALAMAT);
            pekerjaan = c.getString(Konfigurasi.WORKREPORT_KEY_GET_TAG_PEKERJAAN);

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
            waktuSelesai = c.getString(Konfigurasi.WORKREPORT_KEY_GET_TAG_WAKTU_SELESAI);
            worker = c.getString(Konfigurasi.WORKREPORT_KEY_GET_TAG_NAMA_TEKNISI);

            GetTglInput = c.getString("tgl_input");
            GetTimeStamp = c.getString("timestamp");

            //Bagian VI : Foto
            pathFotoSatu = c.getString(Konfigurasi.WORKREPORT_KEY_GET_TAG_IMG_FOTO_SATU);
            pathFotoDua = c.getString(Konfigurasi.WORKREPORT_KEY_GET_TAG_IMG_FOTO_DUA);
            pathFotoTiga = c.getString(Konfigurasi.WORKREPORT_KEY_GET_TAG_FOTO_TIGA);
            pathFotoEmpat = c.getString(Konfigurasi.WORKREPORT_KEY_GET_TAG_FOTO_EMPAT);

            nominalValue = c.getString("pembayaran");
            ketNominalValue = c.getString("ket_bayar");
            kendaraanValue = c.getString("kendaraan");

            //Bagian VI : TTD
            pathTTDClient = c.getString(Konfigurasi.WORKREPORT_KEY_GET_TAG_TTD_PELANGGAN);
            pathTTDConsultant = c.getString(Konfigurasi.WORKREPORT_KEY_GET_TAG_TTD_CONSULTANT);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showData();
    }

    @SuppressLint("SetTextI18n")
    private void showData() {
        txt_Rev_JenisPengerjaan.setText(jenisPengerjaan);
        txt_Rev_IDClient_WorkReport.setText(idClient);
        txt_Rev_NamaClient_WorkReport.setText(NamaPelanggan);
        txt_Rev_Alamat_WorkReport.setText(alamatPelanggan);
        txt_nominal.setText(nominalValue);
        txt_ket_nominal.setText(ketNominalValue);
        txt_kendaraan.setText(kendaraanValue);
        //Jenis Pekerjaan
        txt_Rev_Pekerjaan_WorkReport.setText(pekerjaan);

        switch (pekerjaan) {
            case "Pest Control": {
                tbl_PestControl.setVisibility(View.VISIBLE);
                tbl_TermiteControl.setVisibility(View.GONE);
                tbr_Fumigasi.setVisibility(View.GONE);

                StringBuilder parse_jenis_hama = new StringBuilder();
                String show_hama;

                //Show Jenis Hama
                String[] JenisHama_Arr = totalHama.split(",");
                for (String list_hama : JenisHama_Arr) {
                    if (list_hama.equalsIgnoreCase("Lainnya")) {
                        parse_jenis_hama.append(list_hama).append(" : ").append(hamaLain);
                    } else {
                        parse_jenis_hama.append(list_hama).append(", ");
                    }
                }

                //Delete Comma Left Behind
                show_hama = parse_jenis_hama.toString();
                StringBuilder sb = new StringBuilder(show_hama);
                sb.deleteCharAt(sb.length() - 2);
                txt_Rev_JenisHama_WorkReport.setText(sb + "\n");

                //Show Metode Kendali
                String[] MetodeKendaliHama_Arr = metodeKendaliHama.split(",");
                StringBuilder parse_metode_kendali_hama = new StringBuilder();
                for (String list_metode_hama : MetodeKendaliHama_Arr) {
                    if (list_metode_hama.equalsIgnoreCase("Lem Tikus (Rodent Glue Trap)")) {
                        parse_metode_kendali_hama.append("- ").append(list_metode_hama).append(" : ").append(qtyLemTikus).append(" Buah").append("\n");
                    } else if (list_metode_hama.equalsIgnoreCase("Perangkap Tikus Massal (Rodent Trap)")) {
                        parse_metode_kendali_hama.append("- ").append(list_metode_hama).append(" : ").append(qtyPerangkapTikus).append(" Buah").append("\n");
                    } else if (list_metode_hama.equalsIgnoreCase("Umpan Tikus (Rodent Baiting Outdoor)")) {
                        parse_metode_kendali_hama.append("- ").append(list_metode_hama).append(" : ").append(qtyUmpanTikusOutdoor).append(" Buah").append("\n");
                    } else if (list_metode_hama.equalsIgnoreCase("Umpan Tikus (Rodent Baiting Indoor)")) {
                        parse_metode_kendali_hama.append("- ").append(list_metode_hama).append(" : ").append(qtyUmpanTikusIndoor).append(" Buah").append("\n");
                    } else if (list_metode_hama.equalsIgnoreCase("Pohon Lalat")) {
                        parse_metode_kendali_hama.append("- ").append(list_metode_hama).append(" : ").append(qtyPohonLalat).append(" Buah").append("\n");
                    } else if (list_metode_hama.equalsIgnoreCase("Instalasi Black Hole")) {
                        parse_metode_kendali_hama.append("- ").append(list_metode_hama).append(" : ").append(qtyBlackhole).append(" Buah").append("\n");
                    } else if (list_metode_hama.equalsIgnoreCase("Fumigasi")) {
                        parse_metode_kendali_hama.append("- ").append(list_metode_hama).append(" : ").append(jenisFumigasi).append("\n");
                    } else if (list_metode_hama.equalsIgnoreCase("Instalasi Fly Catcher")) {
                        parse_metode_kendali_hama.append("- ").append(list_metode_hama).append(" : ").append(qtyFlycatcher).append(" Buah").append("\n");
                    } else if (list_metode_hama.equalsIgnoreCase("Metode Lainnya")) {
                        parse_metode_kendali_hama.append("- ").append(list_metode_hama).append(" : ").append(metodeLain).append("\n");
                        Log.e("tag", metodeLain);
                    } else {
                        parse_metode_kendali_hama.append("- ").append(list_metode_hama).append("\n");
                    }
                }
                txt_Rev_MetodeKendaliHama_WorkReport.setText(parse_metode_kendali_hama.toString());
                break;
            }

            case "Termite Control": {
                tbl_TermiteControl.setVisibility(View.VISIBLE);
                tbr_Fumigasi.setVisibility(View.GONE);
                tbl_PestControl.setVisibility(View.GONE);

                StringBuilder parse_metode_kendali_rayap = new StringBuilder();
                String show_metode;

                //Show Jenis Rayap
                txt_Rev_Rayap_WorkReport.setText(totalRayap);

                //Show Metode Kendali rayap
                String[] MetodeKendaliRayap_Arr = metodeKendaliRayap.split(",");
                for (String list_metode : MetodeKendaliRayap_Arr) {
                    parse_metode_kendali_rayap.append(list_metode).append(", ");
                }

                //Delete Comma Left Behind
                show_metode = parse_metode_kendali_rayap.toString();
                StringBuilder sb = new StringBuilder(show_metode);
                sb.deleteCharAt(sb.length() - 2);
                txt_Rev_MetodeKendaliRayap_WorkReport.setText(sb + "\n");

                //Show Chemical
                StringBuilder parse_chemical_rayap = new StringBuilder();

                String[] ChemicalRayap_Arr = chemicalTermite.split(",");
                for (String list_chemical : ChemicalRayap_Arr) {
                    if (list_chemical.equalsIgnoreCase("Fipronil 50 g/l")) {
                        parse_chemical_rayap.append("- ").append(list_chemical).append(" : ").append(qtyFipronil).append(" Unit").append("\n");
                    }
                    if (list_chemical.equalsIgnoreCase("Imidacloprid 200 g/l")) {
                        parse_chemical_rayap.append("- ").append(list_chemical).append(" : ").append(qtyImidaclporid).append(" Unit").append("\n");
                    }
                    if (list_chemical.equalsIgnoreCase("Cypermethrin 100 g/l")) {
                        parse_chemical_rayap.append("- ").append(list_chemical).append(" : ").append(qtyCypermethrin).append(" Unit").append("\n");
                    }
                    if (list_chemical.equalsIgnoreCase("Dichlorphos 200 g/l")) {
                        parse_chemical_rayap.append("- ").append(list_chemical).append(" : ").append(qtyDicholorphos).append(" Unit").append("\n");
                    }
                    if (list_chemical.equalsIgnoreCase("Baiting AG")) {
                        parse_chemical_rayap.append("- ").append(list_chemical).append(" : ").append(qtyBaitingAG).append(" Unit").append("\n");
                    }
                    if (list_chemical.equalsIgnoreCase("Baiting IG")) {
                        parse_chemical_rayap.append("- ").append(list_chemical).append(" : ").append(qtyBaitingIG).append(" Unit").append("\n");
                    }
                }
                txt_Rev_ChemicalTermite_WorkReport.setText(parse_chemical_rayap.toString());
                break;
            }

            case "Fumigasi":
                tbr_Fumigasi.setVisibility(View.VISIBLE);

                tbl_TermiteControl.setVisibility(View.GONE);
                tbl_PestControl.setVisibility(View.GONE);

                txt_Rev_GasFumigasi_WorkReport.setText(gasFumigasi);
                spc_helper.getLayoutParams().height = getResources().getDimensionPixelSize(R.dimen.tbl_helper_dimens);
                break;
        }

        txt_Rev_Worker_WorkReport.setText(worker);

        txt_Rev_WaktuMulai_WorkReport.setText(waktuMulai + " WIB");
        txt_Rev_WaktuSelesai_WorkReport.setText(waktuSelesai + " WIB");
        txt_Rev_NamaConsultant_WorkReport.setText(TampilanMenuUtama.namaLengkap);
        txt_Rev_NamaPelanggan_WorkReport.setText(NamaPelanggan);


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

            //Image TTD Client
            Glide.with(getApplicationContext())
                    .load(Konfigurasi.url_image + pathTTDClient)
                    .error(Glide.with(ttd_Rev_Pelanggan_WorkReport).load(R.drawable.ic_ttd_not_found))
                    .into(ttd_Rev_Pelanggan_WorkReport);

            //Image TTD Consultant
            Glide.with(getApplicationContext())
                    .load(Konfigurasi.url_image + pathTTDConsultant)
                    .error(Glide.with(ttd_Rev_Consultant_WorkReport).load(R.drawable.ic_ttd_not_found))
                    .into(ttd_Rev_Consultant_WorkReport);
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
            txt_Rev_Keterangan_WorkReport.setText(keterangan);

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
            txt_Rev_Keterangan_WorkReport.setText(keterangan + revisi);
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

        simpan_workReport = DIR_WORKREPORT + "WorkReport_" + NamaPelanggan + "_" + dateNow + ".pdf";

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
        AlertDialog.Builder builder = new AlertDialog.Builder(PreviewWorkReport.this);

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
        File file = new File(simpan_workReport);
        if (file.exists()) {
            //Uri path = FileProvider.getUriForFile(file);
            Uri path = FileProvider.getUriForFile(PreviewWorkReport.this, BuildConfig.APPLICATION_ID + ".provider", file);
            Intent intent_view_pdf = new Intent(Intent.ACTION_VIEW);
            intent_view_pdf.setDataAndType(path, "application/pdf");
            intent_view_pdf.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent_view_pdf.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(intent_view_pdf);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(PreviewWorkReport.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
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
