package com.example.aditmail.fumida.TermiteControl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.Intent;
import android.text.TextUtils;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ListAdapter;
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

public class FragmentDataTermite extends Fragment implements ListView.OnItemClickListener {

    //private static final String TAG = "TabFormTermite";

    public String survei_id, tanggal;

    private String JSON_STRING;
    private ListView listView_SurveiTermite;
    protected Button btn_CariTermite;
    private EditText edt_CariTermite;
    private Spinner spr_CariTermite;

    //Untuk Create View Fragment
    protected View view;

    ConnectivityManager conMgr;
    SwipeRefreshLayout mSwipeRefreshLayout;
    protected ProgressDialog loading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_data_termite, container, false);

        listView_SurveiTermite = view.findViewById(R.id.listView_TermiteControl);
        listView_SurveiTermite.setOnItemClickListener(this);

        loading = new ProgressDialog(getContext());
        loading.setCancelable(true);
        loading.setCanceledOnTouchOutside(false);

        mSwipeRefreshLayout = view.findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        if(getActivity()!=null) {
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

        btn_CariTermite = view.findViewById(R.id.button_CariTermite);
        edt_CariTermite = view.findViewById(R.id.editText_CariDataTermite);
        spr_CariTermite = view.findViewById(R.id.spinner_KategoriCariTermite);

            if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
                getJSON();

            } else {
                Toast.makeText(getContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }

        btn_CariTermite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSearchTermiteData();
            }
        });

        return view;
    }

    @Override
    public void onResume(){
            if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
                getJSON_NoLoad();

            } else {
                Toast.makeText(getContext(), "No Internet Connection",
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

                String id_survei = jo.getString(Konfigurasi.TERMITE_KEY_GET_LIST_SURVEI_ID);
                String idClient = jo.getString(Konfigurasi.TERMITE_KEY_GET_LIST_CLIENT_ID);
                String namaPelanggan = jo.getString(Konfigurasi.TERMITE_KEY_GET_LIST_NAMA_PELANGGAN);
                String noHP = jo.getString(Konfigurasi.TERMITE_KEY_GET_LIST_HP);
                String jenisRayap = jo.getString(Konfigurasi.TERMITE_KEY_GET_LIST_JENIS_RAYAP);
                String kategoriPenanganan = jo.getString(Konfigurasi.TERMITE_KEY_GET_LIST_KATEGORI_PENANGANAN);
                String statusKerjasama = jo.getString(Konfigurasi.TERMITE_KEY_GET_LIST_STATUS_KERJASAMA);
                String tanggal = jo.getString(Konfigurasi.TERMITE_KEY_GET_LIST_TANGGAL);

                HashMap<String, String> dataListTermite = new HashMap<>();
                //menyimpan data dari database
                dataListTermite.put(Konfigurasi.TERMITE_KEY_GET_LIST_SURVEI_ID, id_survei);
                dataListTermite.put(Konfigurasi.TERMITE_KEY_GET_LIST_CLIENT_ID, idClient);
                dataListTermite.put(Konfigurasi.TERMITE_KEY_GET_LIST_NAMA_PELANGGAN, namaPelanggan);
                dataListTermite.put(Konfigurasi.TERMITE_KEY_GET_LIST_HP, noHP);
                dataListTermite.put(Konfigurasi.TERMITE_KEY_GET_LIST_JENIS_RAYAP, jenisRayap);
                dataListTermite.put(Konfigurasi.TERMITE_KEY_GET_LIST_KATEGORI_PENANGANAN, kategoriPenanganan);
                dataListTermite.put(Konfigurasi.TERMITE_KEY_GET_LIST_STATUS_KERJASAMA, statusKerjasama);
                dataListTermite.put(Konfigurasi.TERMITE_KEY_GET_LIST_TANGGAL, tanggal);

                //menaruh informasi kedalam list
                list.add(dataListTermite);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(getActivity()!=null) {
            ListAdapter adapter = new SimpleAdapter(
                    getContext(), list, R.layout.list_termite_control,
                    new String[]{Konfigurasi.TERMITE_KEY_GET_LIST_SURVEI_ID, Konfigurasi.TERMITE_KEY_GET_LIST_CLIENT_ID, Konfigurasi.TERMITE_KEY_GET_LIST_NAMA_PELANGGAN, Konfigurasi.TERMITE_KEY_GET_LIST_HP, Konfigurasi.TERMITE_KEY_GET_LIST_JENIS_RAYAP,
                            Konfigurasi.TERMITE_KEY_GET_LIST_KATEGORI_PENANGANAN, Konfigurasi.TERMITE_KEY_GET_LIST_STATUS_KERJASAMA, Konfigurasi.TERMITE_KEY_GET_LIST_TANGGAL},
                    new int[]{R.id.textView_IDSurvei_Data, R.id.textView_IDClient_Data, R.id.textView_NamaPelanggan_Data, R.id.textView_Hp_Data, R.id.textView_JenisRayap_Data,
                            R.id.textView_KategoriPenanganan_Termite_List_Data, R.id.textView_Status_Data, R.id.textView_Tanggal_Data});
            listView_SurveiTermite.setAdapter(adapter);
        }
    }

    private void getJSON() {
        @SuppressLint("StaticFieldLeak")
        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                loading.setMessage("Mengambil Data, Mohon Tunggu..");
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
                return rh.sendGetRequestParam(Konfigurasi.URL_VIEW_ALL_TERMITE_SURVEI, TampilanMenuUtama.id_pegawai);
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
                showDataSurvei();
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                return rh.sendGetRequestParam(Konfigurasi.URL_VIEW_ALL_TERMITE_SURVEI, TampilanMenuUtama.id_pegawai);
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        HashMap<String, String> map = (HashMap) parent.getItemAtPosition(position);
        survei_id = map.get(Konfigurasi.PEST_KEY_GET_LIST_SURVEI_ID);
        tanggal = map.get(Konfigurasi.PEST_KEY_GET_LIST_TANGGAL);

        Intent intent = new Intent(getContext(), UpdateSurveiTermiteControl.class);
        intent.putExtra(Konfigurasi.KEY_TANGGAL_INPUT, tanggal);
        intent.putExtra(Konfigurasi.KEY_SURVEI_ID, survei_id);
        startActivity(intent);
    }

    private void getSearchTermiteData() {

        final String cariDataTermite, kategoriCariTermite;

        cariDataTermite = edt_CariTermite.getText().toString();
        kategoriCariTermite = spr_CariTermite.getSelectedItem().toString();

        if (kategoriCariTermite.equals("ID Client") && TextUtils.isEmpty(cariDataTermite)){
            Toast.makeText(getContext(),"Harap Masukkan Data Pencarian", Toast.LENGTH_SHORT ).show();
            edt_CariTermite.requestFocus();
            return;
        }
        else if (kategoriCariTermite.equals("Client") && TextUtils.isEmpty(cariDataTermite)){
            Toast.makeText(getContext(),"Harap Masukkan Data Pencarian", Toast.LENGTH_SHORT ).show();
            edt_CariTermite.requestFocus();
            return;
        }
        else if (kategoriCariTermite.equals("No HP") && TextUtils.isEmpty(cariDataTermite)){
            Toast.makeText(getContext(),"Harap Masukkan Data Pencarian", Toast.LENGTH_SHORT ).show();
            edt_CariTermite.requestFocus();
            return;
        }
        else if (kategoriCariTermite.equals("Status") && TextUtils.isEmpty(cariDataTermite)){
            Toast.makeText(getContext(),"Harap Masukkan Data Pencarian", Toast.LENGTH_SHORT ).show();
            edt_CariTermite.requestFocus();
            return;
        }

        @SuppressLint("StaticFieldLeak")
        class cari extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                loading.setMessage("Mencari Data di dalam Database, Harap Tunggu..");
                loading.show();
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                loading.dismiss();
                JSON_STRING = s;
                showSearchTermiteData();
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                return rh.sendGetRequestParam_CariDataTermite(Konfigurasi.URL_VIEW_SEARCH_TERMITE_SURVEI, TampilanMenuUtama.id_pegawai, cariDataTermite, kategoriCariTermite);
            }
        }
        cari search = new cari();
        search.execute();
    }

    private void showSearchTermiteData() {
        JSONObject jsonObject;
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(Konfigurasi.TAG_JSON_ARRAY);

            //untuk melakukan looping
            //untuk mengetahui apabila seluruh transaksi telah dimasukkan
            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);

                String id_survei = jo.getString(Konfigurasi.TERMITE_KEY_GET_LIST_SURVEI_ID);
                String idClient = jo.getString(Konfigurasi.TERMITE_KEY_GET_LIST_CLIENT_ID);
                String namaPelanggan = jo.getString(Konfigurasi.TERMITE_KEY_GET_LIST_NAMA_PELANGGAN);
                String noHP = jo.getString(Konfigurasi.TERMITE_KEY_GET_LIST_HP);
                String jenisRayap = jo.getString(Konfigurasi.TERMITE_KEY_GET_LIST_JENIS_RAYAP);
                String kategoriPenanganan = jo.getString(Konfigurasi.TERMITE_KEY_GET_LIST_KATEGORI_PENANGANAN);
                String statusKerjasama = jo.getString(Konfigurasi.TERMITE_KEY_GET_LIST_STATUS_KERJASAMA);
                String tanggal = jo.getString(Konfigurasi.TERMITE_KEY_GET_LIST_TANGGAL);

                HashMap<String, String> dataListTermite = new HashMap<>();
                //menyimpan data dari database
                dataListTermite.put(Konfigurasi.TERMITE_KEY_GET_LIST_SURVEI_ID, id_survei);
                dataListTermite.put(Konfigurasi.TERMITE_KEY_GET_LIST_CLIENT_ID, idClient);
                dataListTermite.put(Konfigurasi.TERMITE_KEY_GET_LIST_NAMA_PELANGGAN, namaPelanggan);
                dataListTermite.put(Konfigurasi.TERMITE_KEY_GET_LIST_HP, noHP);
                dataListTermite.put(Konfigurasi.TERMITE_KEY_GET_LIST_JENIS_RAYAP, jenisRayap);
                dataListTermite.put(Konfigurasi.TERMITE_KEY_GET_LIST_KATEGORI_PENANGANAN, kategoriPenanganan);
                dataListTermite.put(Konfigurasi.TERMITE_KEY_GET_LIST_STATUS_KERJASAMA, statusKerjasama);
                dataListTermite.put(Konfigurasi.TERMITE_KEY_GET_LIST_TANGGAL, tanggal);

                //menaruh informasi kedalam list
                list.add(dataListTermite);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(getActivity()!=null) {
            ListAdapter adapter = new SimpleAdapter(
                    getContext(), list, R.layout.list_termite_control,
                    new String[]{Konfigurasi.TERMITE_KEY_GET_LIST_SURVEI_ID, Konfigurasi.TERMITE_KEY_GET_LIST_CLIENT_ID, Konfigurasi.TERMITE_KEY_GET_LIST_NAMA_PELANGGAN, Konfigurasi.TERMITE_KEY_GET_LIST_HP, Konfigurasi.TERMITE_KEY_GET_LIST_JENIS_RAYAP,
                            Konfigurasi.TERMITE_KEY_GET_LIST_KATEGORI_PENANGANAN, Konfigurasi.TERMITE_KEY_GET_LIST_STATUS_KERJASAMA, Konfigurasi.TERMITE_KEY_GET_LIST_TANGGAL},
                    new int[]{R.id.textView_IDSurvei_Data, R.id.textView_IDClient_Data, R.id.textView_NamaPelanggan_Data, R.id.textView_Hp_Data, R.id.textView_JenisRayap_Data,
                            R.id.textView_KategoriPenanganan_Termite_List_Data, R.id.textView_Status_Data, R.id.textView_Tanggal_Data});
            listView_SurveiTermite.setAdapter(adapter);
        }
    }


}
