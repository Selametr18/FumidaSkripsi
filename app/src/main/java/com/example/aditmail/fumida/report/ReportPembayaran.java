package com.example.aditmail.fumida.report;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.aditmail.fumida.Activities.TampilanMenuUtama;
import com.example.aditmail.fumida.R;
import com.example.aditmail.fumida.Settings.Konfigurasi;
import com.example.aditmail.fumida.Settings.RequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ReportPembayaran extends AppCompatActivity {

    private ListView listView;
    private String JSON_STRING;
    ListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_pembayaran);
        listView = findViewById(R.id.listView_data_pembayaran);
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
                return rh.sendGetRequestParam(Konfigurasi.URL_VIEW_PEMBAYARAN, TampilanMenuUtama.id_pegawai);
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

            //untuk melakukan looping
            //untuk mengetahui apabila seluruh transaksi telah dimasukkan
            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);

                String idClient = jo.getString("id_pelanggan");
                String namaPelanggan = jo.getString("nama_pelanggan");
                String nominalBayar = jo.getString("nominal_pembayaran");
                String ketBayar = jo.getString("keterangan_pembayaran");
                String tanggal = jo.getString(Konfigurasi.WORKREPORT_KEY_GET_LIST_TANGGAL);

                HashMap<String, String> dataListWorkReport = new HashMap<>();
                //menyimpan data dari database
                dataListWorkReport.put("id_bayar", idClient);
                dataListWorkReport.put("nama", namaPelanggan);
                dataListWorkReport.put("nominal", nominalBayar);
                dataListWorkReport.put("ketbayar", ketBayar);
                dataListWorkReport.put(Konfigurasi.WORKREPORT_KEY_GET_LIST_TANGGAL, tanggal);

                //menaruh informasi kedalam list
                list.add(dataListWorkReport);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (this != null) {
            adapter = new SimpleAdapter(
                    this, list, R.layout.list_pembayaran,
                    new String[]{"id_bayar", "nama", "nominal","ketbayar", Konfigurasi.WORKREPORT_KEY_GET_LIST_TANGGAL},
                    new int[]{R.id.textView_IDClient_Data, R.id.textView_NamaPelanggan_Data,
                            R.id.textView_nominal_Data, R.id.textView_ketBayar_Data, R.id.textView_Tanggal_Data}) {

            };

            listView.setAdapter(adapter);
        }
    }

}