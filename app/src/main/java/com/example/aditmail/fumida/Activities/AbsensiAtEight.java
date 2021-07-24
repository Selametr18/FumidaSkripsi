package com.example.aditmail.fumida.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextClock;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.aditmail.fumida.R;
import com.example.aditmail.fumida.Settings.Konfigurasi;
import com.example.aditmail.fumida.Settings.PrefManager;
import com.example.aditmail.fumida.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AbsensiAtEight extends AppCompatActivity {

    protected TextClock tcClock;
    protected EditText etName, etDate;
    protected CheckBox ckAbsensi;
    protected Button btnRekam;
    protected EditText etTimeDB, etTglDB, etNameDB;
    protected CardView cdAbsen;

    protected String dateNow, dateDB;

    protected ProgressDialog pDialog;
    protected ConnectivityManager conMgr;

    protected PrefManager session;
    protected String id_pegawai, namaLengkap, nik;

    int success;
    //inisiasi jika berhasil
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    //untuk proses pertukaran data
    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absensi_at_eight);

        session = new PrefManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        id_pegawai = user.get(PrefManager.KEY_SHARED_ID);
        namaLengkap = user.get(PrefManager.KEY_SHARED_NAMA);
        nik = user.get(PrefManager.KEY_SHARED_NIK);

        pDialog = new ProgressDialog(AbsensiAtEight.this);
        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        tcClock = findViewById(R.id.tvClock);
        etName = findViewById(R.id.etNama);

        etDate = findViewById(R.id.etDate);
        etDate.setEnabled(false);

        ckAbsensi = findViewById(R.id.checkBox_Absensi);
        btnRekam = findViewById(R.id.btnRekamAbsen);

        etNameDB = findViewById(R.id.etNameDB);
        etNameDB.setEnabled(false);
        etTglDB = findViewById(R.id.etTanggalDB);
        etTglDB.setEnabled(false);
        etTimeDB = findViewById(R.id.etTimeDB);
        etTimeDB.setEnabled(false);
        cdAbsen = findViewById(R.id.viewAbsen);
        cdAbsen.setEnabled(false);

        dateNow = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        dateDB = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());

        etName.setText(namaLengkap);
        etDate.setText(dateNow);

        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
            viewData(dateDB, id_pegawai);
        }else {
            Toast.makeText(this, "Tidak Ada Koneksi Internet", Toast.LENGTH_SHORT).show();
        }

            btnRekam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate(id_pegawai);
            }
        });
    }

    private void validate(final String id_pegawai) {
        final String nama = etName.getText().toString();
        final String time = tcClock.getText().toString();
        final String tanggal = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());

        if (nama.isEmpty()) {
            etName.setError("Harap Masukkan Nama");
            etName.requestFocus();
        } else if (!ckAbsensi.isChecked()) {
            Toast.makeText(this, "Harap Mengisi Centang untuk Melanjutkan Absensi", Toast.LENGTH_SHORT).show();
        } else {
            if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(AbsensiAtEight.this);
                builder.setMessage("Apakah Anda Yakin Melakukan Absensi?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                checkDB(nama, time, tanggal, id_pegawai);
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                Toast.makeText(getApplicationContext(), "Tidak Ada Koneksi Internet. Mohon Coba Lagi Nanti.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkDB(final String nama, final String time, final String tanggal, final String id_pegawai) {
        pDialog.setMessage("Memvalidasi, Harap Menunggu...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, Konfigurasi.URL_CEK_ABSENSI_HARIAN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("tag", "Check Response: " + response);
                hideDialog();

                try {
                    //select untuk nyimpen data yg ingin di retrieve
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    if (success == 0) {
                        hideDialog();
                        Toast.makeText(AbsensiAtEight.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                        recreate();
                    } else {
                        hideDialog();
                        Toast.makeText(AbsensiAtEight.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                        recreate();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            //jika error
            public void onErrorResponse(VolleyError error) {
                Log.e("tag", "Check Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(Konfigurasi.KEY_GET_ID, id_pegawai);
                params.put(Konfigurasi.KEY_GET_NAMA, nama);
                params.put(Konfigurasi.KEY_GET_TIME, time);
                params.put(Konfigurasi.KEY_GET_TANGGAL, tanggal);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void viewData(final String tanggal, final String id_pegawai) {
        String url_Login = Konfigurasi.URL_VIEW_ABSENSI_HARIAN;

        Log.e("taggal", tanggal);

        StringRequest strReq = new StringRequest(Request.Method.POST, url_Login, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("tag", "Login Response: " + response);

                try {
                    //select untuk nyimpen data yg ingin di retrieve
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        //jika sukses, akan ambil data nama lengkap dan id dari database/ sql
                        String namaLengkap = jObj.getString(Konfigurasi.KEY_GET_NAMA);
                        String time_absen = jObj.getString("time_absen");
                        String tanggal = jObj.getString("tanggal");

                        cdAbsen.setVisibility(View.VISIBLE);
                        etNameDB.setText(namaLengkap);
                        etTimeDB.setText(time_absen);
                        etTglDB.setText(tanggal);

                    } else {
                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                        cdAbsen.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            //jika error
            public void onErrorResponse(VolleyError error) {
                Log.e("tag", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put(Konfigurasi.KEY_GET_ID, id_pegawai);
                params.put(Konfigurasi.KEY_GET_TANGGAL, tanggal);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
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

