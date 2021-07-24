package com.example.aditmail.fumida.Fumigasi;

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

public class FragmentDataFumigasi extends Fragment implements ListView.OnItemClickListener {

    //private static final String TAG = "TabFormPest";

    public String survei_id, tanggal;

    private String JSON_STRING;
    private ListView listView_SurveiFumigasi;
    protected Button btn_CariFumigasi;
    private EditText edt_CariFumigasi;
    private Spinner spr_CariFumigasi;

    protected View view;

    protected ListAdapter adapter;

    protected ConnectivityManager conMgr;
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    private ProgressDialog loading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_data_fumigasi, container, false);

        loading = new ProgressDialog(getContext());
        loading.setCanceledOnTouchOutside(false);
        loading.setCancelable(true);

        listView_SurveiFumigasi = view.findViewById(R.id.listView_Fumigasi);
        listView_SurveiFumigasi.setOnItemClickListener(this);

        mSwipeRefreshLayout = view.findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        if (getActivity() != null) {
            conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (conMgr != null) {
                    if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
                        getJSON();
                    } else {
                        Toast.makeText(getContext(), "Tidak Ada Koneksi Internet. Mohon Periksa Koneksi Anda",
                                Toast.LENGTH_LONG).show();
                    }

                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        btn_CariFumigasi = view.findViewById(R.id.button_CariFumigasi);
        edt_CariFumigasi = view.findViewById(R.id.editText_CariDataFumigasi);
        spr_CariFumigasi = view.findViewById(R.id.spinner_KategoriCariFumigasi);

        btn_CariFumigasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSearchFumigasiData();
            }
        });


        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
            getJSON();
        } else {
            Toast.makeText(getContext(), "Tidak Ada Koneksi Internet. Mohon Periksa Koneksi Anda",
                    Toast.LENGTH_LONG).show();
        }
        return view;
    }

    @Override
    public void onResume() {
            if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
                getJSON_NoLoad();

            } else {
                Toast.makeText(getContext(), "Tidak Ada Koneksi Internet. Mohon Periksa Koneksi Anda",
                        Toast.LENGTH_LONG).show();
            }
        super.onResume();
    }

    private void showDataSurvei() {
        JSONObject jsonObject;
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(Konfigurasi.TAG_JSON_ARRAY);

            //untuk melakukan looping
            //untuk mengetahui apabila seluruh transaksi telah dimasukkan
            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);

                String id_survei = jo.getString(Konfigurasi.FUMIGASI_KEY_GET_LIST_SURVEI_ID);
                String idClient = jo.getString(Konfigurasi.FUMIGASI_KEY_GET_LIST_CLIENT_ID);
                String namaPelanggan = jo.getString(Konfigurasi.FUMIGASI_KEY_GET_LIST_NAMA_PELANGGAN);
                String noHP = jo.getString(Konfigurasi.FUMIGASI_KEY_GET_LIST_HP);
                String gasFumigasi = jo.getString(Konfigurasi.FUMIGASI_KEY_GET_LIST_GAS_FUMIGASI);
                String statusKerjasama = jo.getString(Konfigurasi.FUMIGASI_KEY_GET_LIST_STATUS_KERJASAMA);
                String tanggal = jo.getString(Konfigurasi.FUMIGASI_KEY_GET_LIST_TANGGAL);

                HashMap<String, String> dataListFumigasi = new HashMap<>();
                //menyimpan data dari database
                dataListFumigasi.put(Konfigurasi.FUMIGASI_KEY_GET_LIST_SURVEI_ID, id_survei);
                dataListFumigasi.put(Konfigurasi.FUMIGASI_KEY_GET_LIST_CLIENT_ID, idClient);
                dataListFumigasi.put(Konfigurasi.FUMIGASI_KEY_GET_LIST_NAMA_PELANGGAN, namaPelanggan);
                dataListFumigasi.put(Konfigurasi.FUMIGASI_KEY_GET_LIST_HP, noHP);
                dataListFumigasi.put(Konfigurasi.FUMIGASI_KEY_GET_LIST_GAS_FUMIGASI, gasFumigasi);
                dataListFumigasi.put(Konfigurasi.FUMIGASI_KEY_GET_LIST_STATUS_KERJASAMA, statusKerjasama);
                dataListFumigasi.put(Konfigurasi.FUMIGASI_KEY_GET_LIST_TANGGAL, tanggal);

                //menaruh informasi kedalam list
                list.add(dataListFumigasi);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("tag", String.valueOf(e));
        }

        if(getActivity()!= null) {
            adapter = new SimpleAdapter(
                    getContext(), list, R.layout.list_fumigasi,
                    new String[]{Konfigurasi.FUMIGASI_KEY_GET_LIST_SURVEI_ID, Konfigurasi.FUMIGASI_KEY_GET_LIST_CLIENT_ID, Konfigurasi.FUMIGASI_KEY_GET_LIST_NAMA_PELANGGAN, Konfigurasi.FUMIGASI_KEY_GET_LIST_HP, Konfigurasi.FUMIGASI_KEY_GET_LIST_GAS_FUMIGASI, Konfigurasi.FUMIGASI_KEY_GET_LIST_STATUS_KERJASAMA,
                            Konfigurasi.FUMIGASI_KEY_GET_LIST_TANGGAL},
                    new int[]{R.id.textView_IDSurvei_Data, R.id.textView_IDClient_Data, R.id.textView_NamaPelanggan_Data, R.id.textView_Hp_Data, R.id.textView_GasFumigasi_Data, R.id.textView_Status_Data,
                            R.id.textView_Tanggal_Data}) {
            };
            listView_SurveiFumigasi.setAdapter(adapter);
        }
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String, String> map = (HashMap) parent.getItemAtPosition(position);
        survei_id = map.get(Konfigurasi.FUMIGASI_KEY_GET_LIST_SURVEI_ID);
        tanggal = map.get(Konfigurasi.FUMIGASI_KEY_GET_LIST_TANGGAL);

        Intent intent = new Intent(getContext(), UpdateSurveiFumigasi.class);
        intent.putExtra(Konfigurasi.KEY_TANGGAL_INPUT, tanggal);
        intent.putExtra(Konfigurasi.KEY_SURVEI_ID, survei_id);
        startActivity(intent);
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
                super.onPostExecute(s);
                JSON_STRING = s;
                showDataSurvei();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();

                return rh.sendGetRequestParam(Konfigurasi.URL_VIEW_ALL_FUMIGASI_SURVEI, TampilanMenuUtama.id_pegawai);
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    private void getJSON() {
        @SuppressLint("StaticFieldLeak")
        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                loading.setMessage("Mengambil Data, Harap Menunggu..");
                loading.show();
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                loading.dismiss();
                JSON_STRING = s;
                showDataSurvei();
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                return rh.sendGetRequestParam(Konfigurasi.URL_VIEW_ALL_FUMIGASI_SURVEI, TampilanMenuUtama.id_pegawai);
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    private void getSearchFumigasiData() {

        final String cariDataFumigasi, kategoriCariFumigasi;

        cariDataFumigasi = edt_CariFumigasi.getText().toString();
        kategoriCariFumigasi = spr_CariFumigasi.getSelectedItem().toString();

        if (kategoriCariFumigasi.equals("ID Client") && TextUtils.isEmpty(cariDataFumigasi)) {
            Toast.makeText(getContext(), "Harap Masukkan Data Pencarian", Toast.LENGTH_SHORT).show();
            //edt_CariPest.setError("Harap Masukkan Data Pencarian");
            edt_CariFumigasi.requestFocus();
            return;
        } else if (kategoriCariFumigasi.equals("Client") && TextUtils.isEmpty(cariDataFumigasi)) {
            Toast.makeText(getContext(), "Harap Masukkan Data Pencarian", Toast.LENGTH_SHORT).show();
            edt_CariFumigasi.requestFocus();
            return;
        } else if (kategoriCariFumigasi.equals("No HP") && TextUtils.isEmpty(cariDataFumigasi)) {
            Toast.makeText(getContext(), "Harap Masukkan Data Pencarian", Toast.LENGTH_SHORT).show();
            edt_CariFumigasi.requestFocus();
            return;
        } else if (kategoriCariFumigasi.equals("Status") && TextUtils.isEmpty(cariDataFumigasi)) {
            Toast.makeText(getContext(), "Harap Masukkan Data Pencarian", Toast.LENGTH_SHORT).show();
            edt_CariFumigasi.requestFocus();
            return;
        }

        @SuppressLint("StaticFieldLeak")
        class cari extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                loading.setMessage("Mencari Data di Dalam Database...");
                loading.show();
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                loading.dismiss();
                JSON_STRING = s;
                showSearchFumigasiData();
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();

                return rh.sendGetRequestParam_CariDataFumigasi(Konfigurasi.URL_VIEW_SEARCH_FUMIGASI_SURVEI, TampilanMenuUtama.id_pegawai, cariDataFumigasi, kategoriCariFumigasi);
            }
        }
        cari search = new cari();
        search.execute();
    }

    private void showSearchFumigasiData() {
        JSONObject jsonObject;
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(Konfigurasi.TAG_JSON_ARRAY);

            //untuk melakukan looping
            //untuk mengetahui apabila seluruh transaksi telah dimasukkan
            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);

                String id_survei = jo.getString(Konfigurasi.FUMIGASI_KEY_GET_LIST_SURVEI_ID);
                String idClient = jo.getString(Konfigurasi.FUMIGASI_KEY_GET_LIST_CLIENT_ID);
                String namaPelanggan = jo.getString(Konfigurasi.FUMIGASI_KEY_GET_LIST_NAMA_PELANGGAN);
                String noHP = jo.getString(Konfigurasi.FUMIGASI_KEY_GET_LIST_HP);
                String gasFumigasi = jo.getString(Konfigurasi.FUMIGASI_KEY_GET_LIST_GAS_FUMIGASI);
                String statusKerjasama = jo.getString(Konfigurasi.FUMIGASI_KEY_GET_LIST_STATUS_KERJASAMA);
                String tanggal = jo.getString(Konfigurasi.FUMIGASI_KEY_GET_LIST_TANGGAL);

                HashMap<String, String> dataListFumigasi = new HashMap<>();
                //menyimpan data dari database
                dataListFumigasi.put(Konfigurasi.FUMIGASI_KEY_GET_LIST_SURVEI_ID, id_survei);
                dataListFumigasi.put(Konfigurasi.FUMIGASI_KEY_GET_LIST_CLIENT_ID, idClient);
                dataListFumigasi.put(Konfigurasi.FUMIGASI_KEY_GET_LIST_NAMA_PELANGGAN, namaPelanggan);
                dataListFumigasi.put(Konfigurasi.FUMIGASI_KEY_GET_LIST_HP, noHP);
                dataListFumigasi.put(Konfigurasi.FUMIGASI_KEY_GET_LIST_GAS_FUMIGASI, gasFumigasi);
                dataListFumigasi.put(Konfigurasi.FUMIGASI_KEY_GET_LIST_STATUS_KERJASAMA, statusKerjasama);
                dataListFumigasi.put(Konfigurasi.FUMIGASI_KEY_GET_LIST_TANGGAL, tanggal);

                //menaruh informasi kedalam list
                list.add(dataListFumigasi);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(getActivity()!= null) {
            adapter = new SimpleAdapter(
                    getContext(), list, R.layout.list_fumigasi,
                    new String[]{Konfigurasi.FUMIGASI_KEY_GET_LIST_SURVEI_ID, Konfigurasi.FUMIGASI_KEY_GET_LIST_CLIENT_ID, Konfigurasi.FUMIGASI_KEY_GET_LIST_NAMA_PELANGGAN, Konfigurasi.FUMIGASI_KEY_GET_LIST_HP, Konfigurasi.FUMIGASI_KEY_GET_LIST_GAS_FUMIGASI, Konfigurasi.FUMIGASI_KEY_GET_LIST_STATUS_KERJASAMA,
                            Konfigurasi.FUMIGASI_KEY_GET_LIST_TANGGAL},
                    new int[]{R.id.textView_IDSurvei_Data, R.id.textView_IDClient_Data, R.id.textView_NamaPelanggan_Data, R.id.textView_Hp_Data, R.id.textView_GasFumigasi_Data, R.id.textView_Status_Data,
                            R.id.textView_Tanggal_Data});
            listView_SurveiFumigasi.setAdapter(adapter);
        }
    }
}
