package com.example.aditmail.fumida.WorkReport;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.aditmail.fumida.Settings.Konfigurasi;
import com.example.aditmail.fumida.R;
import com.example.aditmail.fumida.Settings.RequestHandler;
import com.example.aditmail.fumida.Activities.TampilanMenuUtama;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FragmentDataWorkReport extends Fragment implements ListView.OnItemClickListener {

    //private static final String TAG = "TabFormPest";

    public String survei_id, tanggal;

    private String JSON_STRING;
    private ListView listView_WorkReport;
    protected Button btn_WorkReport;
    private EditText edt_WorkReport;
    private Spinner spr_WorkReport;

    ListAdapter adapter;

    ConnectivityManager conMgr;
    SwipeRefreshLayout mSwipeRefreshLayout;

    //Untuk Create View Fragment
    protected View view;

    private ProgressDialog loading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_data_work_report, container, false);

        listView_WorkReport = view.findViewById(R.id.listView_WorkReport);
        listView_WorkReport.setOnItemClickListener(this);

        loading = new ProgressDialog(getContext());
        loading.setCancelable(true);
        loading.setCanceledOnTouchOutside(false);

        mSwipeRefreshLayout = view.findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        if (getActivity() != null) {
            conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
                    getJSON();

                } else {
                    Toast.makeText(getContext(), "No Internet Connection",
                            Toast.LENGTH_LONG).show();
                }

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        btn_WorkReport = view.findViewById(R.id.button_CariWorkReport);
        edt_WorkReport = view.findViewById(R.id.editText_CariDataWorkReport);
        spr_WorkReport = view.findViewById(R.id.spinner_KategoriCariWorkReport);

        btn_WorkReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSearchWorkReport();
            }
        });

        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
            getJSON();

        } else {
            Toast.makeText(getContext(), "No Internet Connection",
                    Toast.LENGTH_LONG).show();
        }


        return view;
    }

    @Override
    public void onResume() {
        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
            getJSON_NoLoad();
        } else {
            Toast.makeText(getContext(), "No Internet Connection",
                    Toast.LENGTH_LONG).show();
        }
        super.onResume();
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

                String id_survei = jo.getString(Konfigurasi.WORKREPORT_KEY_GET_LIST_SURVEI_ID);
                String idClient = jo.getString(Konfigurasi.WORKREPORT_KEY_GET_LIST_CLIENT_ID);
                String namaPelanggan = jo.getString(Konfigurasi.WORKREPORT_KEY_GET_LIST_NAMA_PELANGGAN);
                String jenisPengerjaan = jo.getString(Konfigurasi.WORKREPORT_KEY_GET_LIST_JENIS_PENGERJAAN);
                String pekerjaan = jo.getString(Konfigurasi.WORKREPORT_KEY_GET_LIST_PEKERJAAN);
                String tanggal = jo.getString(Konfigurasi.WORKREPORT_KEY_GET_LIST_TANGGAL);

                HashMap<String, String> dataListWorkReport = new HashMap<>();
                //menyimpan data dari database
                dataListWorkReport.put(Konfigurasi.WORKREPORT_KEY_GET_LIST_SURVEI_ID, id_survei);
                dataListWorkReport.put(Konfigurasi.WORKREPORT_KEY_GET_LIST_CLIENT_ID, idClient);
                dataListWorkReport.put(Konfigurasi.WORKREPORT_KEY_GET_LIST_NAMA_PELANGGAN, namaPelanggan);
                dataListWorkReport.put(Konfigurasi.WORKREPORT_KEY_GET_LIST_JENIS_PENGERJAAN, jenisPengerjaan);
                dataListWorkReport.put(Konfigurasi.WORKREPORT_KEY_GET_LIST_PEKERJAAN, pekerjaan);
                dataListWorkReport.put(Konfigurasi.WORKREPORT_KEY_GET_LIST_TANGGAL, tanggal);

                //menaruh informasi kedalam list
                list.add(dataListWorkReport);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (getActivity() != null) {
            adapter = new SimpleAdapter(
                    getContext(), list, R.layout.list_work_report,
                    new String[]{Konfigurasi.WORKREPORT_KEY_GET_LIST_SURVEI_ID, Konfigurasi.WORKREPORT_KEY_GET_LIST_CLIENT_ID, Konfigurasi.WORKREPORT_KEY_GET_LIST_NAMA_PELANGGAN,
                            Konfigurasi.WORKREPORT_KEY_GET_LIST_JENIS_PENGERJAAN, Konfigurasi.WORKREPORT_KEY_GET_LIST_PEKERJAAN, Konfigurasi.WORKREPORT_KEY_GET_LIST_TANGGAL},
                    new int[]{R.id.textView_IDSurvei_Data, R.id.textView_IDClient_Data, R.id.textView_NamaPelanggan_Data,
                            R.id.textView_JenisPengerjaan_Data, R.id.textView_Pekerjaan_Data, R.id.textView_Tanggal_Data}) {

            };

            listView_WorkReport.setAdapter(adapter);
        }
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String, String> map = (HashMap) parent.getItemAtPosition(position);
        survei_id = map.get(Konfigurasi.WORKREPORT_KEY_GET_LIST_SURVEI_ID);
        tanggal = map.get(Konfigurasi.WORKREPORT_KEY_GET_LIST_TANGGAL);

        Intent intent = new Intent(getContext(), UpdateWorkReport.class);
        intent.putExtra(Konfigurasi.KEY_TANGGAL_INPUT, tanggal);
        intent.putExtra(Konfigurasi.KEY_SURVEI_ID, survei_id);
        startActivity(intent);
    }

    private void getJSON() {
        @SuppressLint("StaticFieldLeak")
        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                loading.setMessage("Mengambil Data, Mohon Tunggu...");
                loading.show();
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                loading.dismiss();
                JSON_STRING = s;
                showDataWorkReport();
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                return rh.sendGetRequestParam(Konfigurasi.URL_VIEW_ALL_WORK_REPORT, TampilanMenuUtama.id_pegawai);
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    private void getJSON_NoLoad() {
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
                return rh.sendGetRequestParam(Konfigurasi.URL_VIEW_ALL_WORK_REPORT, TampilanMenuUtama.id_pegawai);
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    private void getSearchWorkReport() {

        final String kategoriWorkReport, cariDataWorkReport;

        cariDataWorkReport = edt_WorkReport.getText().toString();
        kategoriWorkReport = spr_WorkReport.getSelectedItem().toString();

        if (kategoriWorkReport.equals("ID Client") && TextUtils.isEmpty(cariDataWorkReport)) {
            Toast.makeText(getContext(), "Harap Masukkan Data Pencarian", Toast.LENGTH_SHORT).show();
            //edt_CariPest.setError("Harap Masukkan Data Pencarian");
            edt_WorkReport.requestFocus();
            return;
        } else if (kategoriWorkReport.equals("Client") && TextUtils.isEmpty(cariDataWorkReport)) {
            Toast.makeText(getContext(), "Harap Masukkan Data Pencarian", Toast.LENGTH_SHORT).show();
            edt_WorkReport.requestFocus();
            return;
        } else if (kategoriWorkReport.equals("Jenis Pengerjaan") && TextUtils.isEmpty(cariDataWorkReport)) {
            Toast.makeText(getContext(), "Harap Masukkan Data Pencarian", Toast.LENGTH_SHORT).show();
            edt_WorkReport.requestFocus();
            return;
        } else if (kategoriWorkReport.equals("Pekerjaan") && TextUtils.isEmpty(cariDataWorkReport)) {
            Toast.makeText(getContext(), "Harap Masukkan Data Pencarian", Toast.LENGTH_SHORT).show();
            edt_WorkReport.requestFocus();
            return;
        }

        @SuppressLint("StaticFieldLeak")
        class cari extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading.setMessage("Mencari Data di dalam Database, Harap Tunggu...");
                loading.show();
            }

            @Override
            protected void onPostExecute(String s) {
                loading.dismiss();
                JSON_STRING = s;
                showSearchWorkReport();
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                //String nya musti sama dengan yang ada di konfigurasi...

                RequestHandler rh = new RequestHandler();
                return rh.sendGetRequestParam_CariDataWorkReport(Konfigurasi.URL_VIEW_SEARCH_WORK_REPORT, TampilanMenuUtama.id_pegawai, cariDataWorkReport, kategoriWorkReport);
            }
        }
        cari search = new cari();
        search.execute();
    }

    private void showSearchWorkReport() {
        JSONObject jsonObject;
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(Konfigurasi.TAG_JSON_ARRAY);

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);

                String id_survei = jo.getString(Konfigurasi.PEST_KEY_GET_LIST_SURVEI_ID);
                String idClient = jo.getString(Konfigurasi.PEST_KEY_GET_LIST_CLIENT_ID);
                String namaPelanggan = jo.getString(Konfigurasi.PEST_KEY_GET_LIST_NAMA_PELANGGAN);
                String jenisPengerjaan = jo.getString(Konfigurasi.WORKREPORT_KEY_GET_LIST_JENIS_PENGERJAAN);
                String pekerjaan = jo.getString(Konfigurasi.WORKREPORT_KEY_GET_LIST_PEKERJAAN);
                String tanggal = jo.getString(Konfigurasi.WORKREPORT_KEY_GET_LIST_TANGGAL);

                HashMap<String, String> dataListWorkReport = new HashMap<>();
                //menyimpan data dari database
                dataListWorkReport.put(Konfigurasi.WORKREPORT_KEY_GET_LIST_SURVEI_ID, id_survei);
                dataListWorkReport.put(Konfigurasi.WORKREPORT_KEY_GET_LIST_CLIENT_ID, idClient);
                dataListWorkReport.put(Konfigurasi.WORKREPORT_KEY_GET_LIST_NAMA_PELANGGAN, namaPelanggan);
                dataListWorkReport.put(Konfigurasi.WORKREPORT_KEY_GET_LIST_JENIS_PENGERJAAN, jenisPengerjaan);
                dataListWorkReport.put(Konfigurasi.WORKREPORT_KEY_GET_LIST_PEKERJAAN, pekerjaan);
                dataListWorkReport.put(Konfigurasi.WORKREPORT_KEY_GET_LIST_TANGGAL, tanggal);

                //menaruh informasi kedalam list
                list.add(dataListWorkReport);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (getActivity() != null) {
            adapter = new SimpleAdapter(
                    getContext(), list, R.layout.list_work_report,
                    new String[]{Konfigurasi.WORKREPORT_KEY_GET_LIST_SURVEI_ID, Konfigurasi.WORKREPORT_KEY_GET_LIST_CLIENT_ID, Konfigurasi.WORKREPORT_KEY_GET_LIST_NAMA_PELANGGAN,
                            Konfigurasi.WORKREPORT_KEY_GET_LIST_JENIS_PENGERJAAN, Konfigurasi.WORKREPORT_KEY_GET_LIST_PEKERJAAN, Konfigurasi.WORKREPORT_KEY_GET_LIST_TANGGAL},
                    new int[]{R.id.textView_IDSurvei_Data, R.id.textView_IDClient_Data, R.id.textView_NamaPelanggan_Data,
                            R.id.textView_JenisPengerjaan_Data, R.id.textView_Pekerjaan_Data, R.id.textView_Tanggal_Data});

            listView_WorkReport.setAdapter(adapter);
        }
    }
}
