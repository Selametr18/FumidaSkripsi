package com.example.aditmail.fumida.report;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.aditmail.fumida.Activities.CetakKendaraanActivity;
import com.example.aditmail.fumida.Activities.CetakPelangganActivity;
import com.example.aditmail.fumida.Activities.CetakPengerjaanActivity;
import com.example.aditmail.fumida.Activities.TampilanMenuUtama;
import com.example.aditmail.fumida.R;
import com.example.aditmail.fumida.Settings.Konfigurasi;
import com.example.aditmail.fumida.Settings.RequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ReportPengerjaan extends AppCompatActivity {

    private ListView listView;
    private String JSON_STRING;
    ListAdapter adapter;
    private Button btnCetak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_pengerjaan);
        listView = findViewById(R.id.listView_data_kendaraan);
        btnCetak = findViewById(R.id.btn_cetak_report);
        btnCetak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoCetak();
            }
        });
        getJSON();
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
                showDataWorkReport();
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                return rh.sendGetRequestParam(Konfigurasi.URL_VIEW_Pengerjaan, TampilanMenuUtama.id_pegawai);
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    private void showDataWorkReport() {
        JSONObject jsonObject;
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(Konfigurasi.TAG_JSON_ARRAY);
            Log.e("RESULT NYA", result.toString());

            //untuk melakukan looping
            //untuk mengetahui apabila seluruh transaksi telah dimasukkan
            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);

                String idClient = jo.getString("id_pelanggan");
                String namaPelanggan = jo.getString("nama_pelanggan");
                String ketBayar = jo.getString("jenis_pengerjaan");
                String pekerjaan = jo.getString("pekerjaan");
                String hama = jo.getString("jenis_hama");
                String teknisi = jo.getString("nama_teknisi");
                String tanggal = jo.getString(Konfigurasi.WORKREPORT_KEY_GET_LIST_TANGGAL);

                HashMap<String, String> dataListWorkReport = new HashMap<>();
                //menyimpan data dari database
                dataListWorkReport.put("id_bayar", idClient);
                dataListWorkReport.put("nama_pelanggannya", namaPelanggan);
                dataListWorkReport.put("ketbayar", ketBayar);
                dataListWorkReport.put("kerja", pekerjaan);
                dataListWorkReport.put("teknik", teknisi);
                dataListWorkReport.put("hama", hama);
                dataListWorkReport.put(Konfigurasi.WORKREPORT_KEY_GET_LIST_TANGGAL, tanggal);

                //menaruh informasi kedalam list
                list.add(dataListWorkReport);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (this != null) {
            adapter = new SimpleAdapter(
                    this, list, R.layout.list_pengerjaan,
                    new String[]{"id_bayar", "nama_pelanggannya", "ketbayar", "kerja", "hama", "teknik", Konfigurasi.WORKREPORT_KEY_GET_LIST_TANGGAL},
                    new int[]{R.id.textView_IDClient_Data, R.id.nama_pelanggan_value, R.id.textView_JenisPengerjaan_Data,
                            R.id.textView_pekerja_data, R.id.textView_kendaraan_Data,
                            R.id.textView_teknisi_Data, R.id.textView_Tanggal_Data}) {

            };

            listView.setAdapter(adapter);
        }
    }

    private void gotoCetak() {
        Intent intent = new Intent(this, CetakPengerjaanActivity.class);
        startActivity(intent);
    }
}