package com.example.aditmail.fumida.PestControl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.Intent;
import android.text.TextUtils;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.example.aditmail.fumida.Settings.Konfigurasi;
import com.example.aditmail.fumida.R;
import com.example.aditmail.fumida.Settings.RequestHandler;
import com.example.aditmail.fumida.Activities.TampilanMenuUtama;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FragmentDataPest extends Fragment implements ListView.OnItemClickListener {

    //private static final String TAG = "TabFormPest";

    public static String survei_id, tanggal;

    private String JSON_STRING;
    private ListView listView_SurveiPest;
    protected Button btn_CariPest;
    private EditText edt_CariPest;
    private Spinner spr_CariPest;

    protected View view;

    protected ListAdapter adapter;

    protected ConnectivityManager conMgr;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected ProgressDialog loading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_data_pest, container, false);

        listView_SurveiPest = view.findViewById(R.id.listView_PestControl);
        listView_SurveiPest.setOnItemClickListener(this);

        loading = new ProgressDialog(getContext());
        loading.setCanceledOnTouchOutside(false);
        loading.setCancelable(true);

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
                    Toast.makeText(getContext(), "Tidak Ada Koneksi Internet. Mohon Periksa Koneksi Anda",
                            Toast.LENGTH_LONG).show();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        btn_CariPest = view.findViewById(R.id.button_CariPest);
        edt_CariPest = view.findViewById(R.id.editText_CariDataPest);
        spr_CariPest = view.findViewById(R.id.spinner_KategoriCariPest);

        btn_CariPest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSearchPestData();
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

                String id_survei = jo.getString(Konfigurasi.PEST_KEY_GET_LIST_SURVEI_ID);
                String idClient = jo.getString(Konfigurasi.PEST_KEY_GET_LIST_CLIENT_ID);
                String namaPelanggan = jo.getString(Konfigurasi.PEST_KEY_GET_LIST_NAMA_PELANGGAN);
                String noHP = jo.getString(Konfigurasi.PEST_KEY_GET_LIST_HP);
                String jenisHama = jo.getString(Konfigurasi.PEST_KEY_GET_LIST_JENIS_HAMA);
                String statusKerjasama = jo.getString(Konfigurasi.PEST_KEY_GET_LIST_STATUS_KERJASAMA);
                String tanggal = jo.getString(Konfigurasi.PEST_KEY_GET_LIST_TANGGAL);

                HashMap<String, String> dataListPest = new HashMap<>();
                //menyimpan data dari database
                dataListPest.put(Konfigurasi.PEST_KEY_GET_LIST_SURVEI_ID, id_survei);
                dataListPest.put(Konfigurasi.PEST_KEY_GET_LIST_CLIENT_ID, idClient);
                dataListPest.put(Konfigurasi.PEST_KEY_GET_LIST_NAMA_PELANGGAN, namaPelanggan);
                dataListPest.put(Konfigurasi.PEST_KEY_GET_LIST_HP, noHP);
                dataListPest.put(Konfigurasi.PEST_KEY_GET_LIST_JENIS_HAMA, jenisHama);
                dataListPest.put(Konfigurasi.PEST_KEY_GET_LIST_STATUS_KERJASAMA, statusKerjasama);
                dataListPest.put(Konfigurasi.PEST_KEY_GET_LIST_TANGGAL, tanggal);

                //menaruh informasi kedalam list
                list.add(dataListPest);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("tag", String.valueOf(e));
        }


        if (getActivity()!=null) {
            adapter = new SimpleAdapter(
                    getContext(), list, R.layout.list_pest_control,
                    new String[]{Konfigurasi.PEST_KEY_GET_LIST_SURVEI_ID, Konfigurasi.PEST_KEY_GET_LIST_CLIENT_ID, Konfigurasi.PEST_KEY_GET_LIST_NAMA_PELANGGAN, Konfigurasi.PEST_KEY_GET_LIST_HP, Konfigurasi.PEST_KEY_GET_LIST_JENIS_HAMA, Konfigurasi.PEST_KEY_GET_LIST_STATUS_KERJASAMA,
                            Konfigurasi.PEST_KEY_GET_LIST_TANGGAL},
                    new int[]{R.id.textView_IDSurvei_Data, R.id.textView_IDClient_Data, R.id.textView_NamaPelanggan_Data, R.id.textView_Hp_Data, R.id.textView_JenisHama_Data, R.id.textView_Status_Data,
                            R.id.textView_Tanggal_Data}) {
            };
            listView_SurveiPest.setAdapter(adapter);
        }

/*        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object o, String s) {
                if (view.getId() == R.id.textView_JenisHama_Data){
                    StringBuffer a = new StringBuffer(s);
                    TextView tv = (TextView)view;
                    tv.setText(a.deleteCharAt(a.length()-2));
                    return true;
                }
                return false;
            }
        });*/


    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String, String> map = (HashMap) parent.getItemAtPosition(position);
        survei_id = map.get(Konfigurasi.PEST_KEY_GET_LIST_SURVEI_ID);
        tanggal = map.get(Konfigurasi.PEST_KEY_GET_LIST_TANGGAL);

        Intent intent = new Intent(getContext(), UpdateSurveiPestControl.class);
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
                return rh.sendGetRequestParam(Konfigurasi.URL_VIEW_ALL_PEST_SURVEI, TampilanMenuUtama.id_pegawai);
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
                loading.setMessage("Mengambil Data," + " Mohon Tunggu");
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
                return rh.sendGetRequestParam(Konfigurasi.URL_VIEW_ALL_PEST_SURVEI, TampilanMenuUtama.id_pegawai);
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    private void getSearchPestData() {

        final String cariDataPest, kategoriCariPest;

        cariDataPest = edt_CariPest.getText().toString();
        kategoriCariPest = spr_CariPest.getSelectedItem().toString();

        if (kategoriCariPest.equals("ID Client") && TextUtils.isEmpty(cariDataPest)) {
            Toast.makeText(getContext(), "Harap Masukkan Data Pencarian", Toast.LENGTH_SHORT).show();
            //edt_CariPest.setError("Harap Masukkan Data Pencarian");
            edt_CariPest.requestFocus();
            return;
        } else if (kategoriCariPest.equals("Client") && TextUtils.isEmpty(cariDataPest)) {
            Toast.makeText(getContext(), "Harap Masukkan Data Pencarian", Toast.LENGTH_SHORT).show();
            edt_CariPest.requestFocus();
            return;
        } else if (kategoriCariPest.equals("No HP") && TextUtils.isEmpty(cariDataPest)) {
            Toast.makeText(getContext(), "Harap Masukkan Data Pencarian", Toast.LENGTH_SHORT).show();
            edt_CariPest.requestFocus();
            return;
        } else if (kategoriCariPest.equals("Status") && TextUtils.isEmpty(cariDataPest)) {
            Toast.makeText(getContext(), "Harap Masukkan Data Pencarian", Toast.LENGTH_SHORT).show();
            edt_CariPest.requestFocus();
            return;
        }

        @SuppressLint("StaticFieldLeak")
        class cari extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                loading.setMessage("Mengambil Data," + " Mohon Tunggu");
                loading.show();
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                loading.dismiss();
                JSON_STRING = s;
                showSearchPestData();
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                return rh.sendGetRequestParam_CariDataPest(Konfigurasi.URL_VIEW_SEARCH_PEST_SURVEI, TampilanMenuUtama.id_pegawai, cariDataPest, kategoriCariPest);
            }
        }
        cari search = new cari();
        search.execute();
    }

    private void showSearchPestData() {
        JSONObject jsonObject;
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(Konfigurasi.TAG_JSON_ARRAY);

            //untuk melakukan looping
            //untuk mengetahui apabila seluruh transaksi telah dimasukkan
            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);

                String id_survei = jo.getString(Konfigurasi.PEST_KEY_GET_LIST_SURVEI_ID);
                String idClient = jo.getString(Konfigurasi.PEST_KEY_GET_LIST_CLIENT_ID);
                String namaPelanggan = jo.getString(Konfigurasi.PEST_KEY_GET_LIST_NAMA_PELANGGAN);
                String noHP = jo.getString(Konfigurasi.PEST_KEY_GET_LIST_HP);
                String jenisHama = jo.getString(Konfigurasi.PEST_KEY_GET_LIST_JENIS_HAMA);
                String statusKerjasama = jo.getString(Konfigurasi.PEST_KEY_GET_LIST_STATUS_KERJASAMA);
                String tanggal = jo.getString(Konfigurasi.PEST_KEY_GET_LIST_TANGGAL);

                HashMap<String, String> dataListPest = new HashMap<>();
                //menyimpan data dari database
                dataListPest.put(Konfigurasi.PEST_KEY_GET_LIST_SURVEI_ID, id_survei);
                dataListPest.put(Konfigurasi.PEST_KEY_GET_LIST_CLIENT_ID, idClient);
                dataListPest.put(Konfigurasi.PEST_KEY_GET_LIST_NAMA_PELANGGAN, namaPelanggan);
                dataListPest.put(Konfigurasi.PEST_KEY_GET_LIST_HP, noHP);
                dataListPest.put(Konfigurasi.PEST_KEY_GET_LIST_JENIS_HAMA, jenisHama);
                dataListPest.put(Konfigurasi.PEST_KEY_GET_LIST_STATUS_KERJASAMA, statusKerjasama);
                dataListPest.put(Konfigurasi.PEST_KEY_GET_LIST_TANGGAL, tanggal);

                //menaruh informasi kedalam list
                list.add(dataListPest);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(getActivity()!=null) {
            adapter = new SimpleAdapter(

                    getContext(), list, R.layout.list_pest_control,
                    new String[]{Konfigurasi.PEST_KEY_GET_LIST_SURVEI_ID, Konfigurasi.PEST_KEY_GET_LIST_CLIENT_ID, Konfigurasi.PEST_KEY_GET_LIST_NAMA_PELANGGAN, Konfigurasi.PEST_KEY_GET_LIST_HP, Konfigurasi.PEST_KEY_GET_LIST_JENIS_HAMA, Konfigurasi.PEST_KEY_GET_LIST_STATUS_KERJASAMA,
                            Konfigurasi.PEST_KEY_GET_LIST_TANGGAL},
                    new int[]{R.id.textView_IDSurvei_Data, R.id.textView_IDClient_Data, R.id.textView_NamaPelanggan_Data, R.id.textView_Hp_Data, R.id.textView_JenisHama_Data, R.id.textView_Status_Data,
                            R.id.textView_Tanggal_Data});

            listView_SurveiPest.setAdapter(adapter);
        }
    }
}
