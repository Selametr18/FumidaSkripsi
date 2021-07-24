package com.example.aditmail.fumida.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.aditmail.fumida.Settings.Konfigurasi;
import com.example.aditmail.fumida.R;
import com.example.aditmail.fumida.Settings.RequestHandler;
import com.example.aditmail.fumida.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ActivityLupaPassword extends AppCompatActivity {

    protected Button btnAutentikasi;

    protected ProgressDialog pDialog;

    int success;
    ConnectivityManager conMgr;
    protected Dialog myDialog;
    protected String id_pegawai;

    protected EditText etPassword, etKonfirmasiPassword;
    protected Button btnUpdate;

    private static final String TAG = TampilanLogin.class.getSimpleName();

    //inisiasi jika berhasil
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    //untuk proses pertukaran data
    protected String tag_json_obj = "json_obj_req";
    protected EditText etNIK, etUsername, etNoHP;
    protected String strPwd, strPwdKonfirmasi;
    protected TextView tvLupaPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lupa_password);

        myDialog = new Dialog(ActivityLupaPassword.this);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);
        pDialog.setCanceledOnTouchOutside(false);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        btnAutentikasi = findViewById(R.id.btnAutentikasi);

        etNIK = findViewById(R.id.editText_NIK);
        etUsername = findViewById(R.id.editText_Username);
        etNoHP = findViewById(R.id.editText_NoHP);

        btnAutentikasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validasiAkun();
            }
        });
    }

    private void validasiAkun() {

        final String strNIK = etNIK.getText().toString();
        final String strUsername = etUsername.getText().toString();
        final String strNoHP = etNoHP.getText().toString();

        if (strNIK.isEmpty()) {
            etNIK.setError("Harap Masukkan NIK Anda Sesuai yang Telah Didaftarkan");
            etNIK.requestFocus();
            return;
        }
        if (strUsername.isEmpty()) {
            etUsername.setError("Harap Masukkan Nama Pengguna Anda Sesuai yang Telah Didaftarkan");
            etUsername.requestFocus();
            return;
        }
        if (strNoHP.isEmpty()) {
            etNoHP.setError("Harap Masukkan No HP Anda Sesuai yang Telah Didaftarkan");
            etNoHP.requestFocus();
            return;
        }

        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {
            DoResetPassword(strNIK, strUsername, strNoHP);
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    private void DoResetPassword(final String NIK, final String Username, final String NoHP) {

        pDialog.setMessage("Memvalidasi Akun...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, Konfigurasi.URL_LUPA_PASSWORD, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "VALIDASI Response: " + response);
                hideDialog();

                try {
                    //select untuk nyimpen data yg ingin di retrieve
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        id_pegawai = jObj.getString("id");

                        showPopUpForget();
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                        tvLupaPassword.setText(jObj.getString(TAG_MESSAGE));

                        Log.e("id", id_pegawai);

                    } else {
                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Log.e("tag", String.valueOf(e));
                }

            }
        }, new Response.ErrorListener() {

            @Override
            //jika error
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Validasi Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            //seleksi dan lihat kedalam php...
            //php bakal liat kedalam database
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("nik_pegawai", NIK);
                params.put("username_pegawai", Username);
                params.put("noHp_pegawai", NoHP);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    public void showPopUpForget() {

        myDialog.setContentView(R.layout.pop_up_lupa_password);
        myDialog.setCanceledOnTouchOutside(false);

        tvLupaPassword = myDialog.findViewById(R.id.tvLupaPassword);

        etPassword = myDialog.findViewById(R.id.editText_Password_PopUp);
        etKonfirmasiPassword = myDialog.findViewById(R.id.editText_KonfirmasiPassword_PopUp);
        btnUpdate = myDialog.findViewById(R.id.btnUpdatePassword_PopUp);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidatePassword();
            }
        });

        Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        myDialog.show();
    }

    private void ValidatePassword() {
        strPwd = etPassword.getText().toString();
        strPwdKonfirmasi = etKonfirmasiPassword.getText().toString();

        if (strPwd.isEmpty()) {
            etPassword.setError("Harap Masukkan Kata Sandi Anda");
            etPassword.requestFocus();
        } else if (strPwdKonfirmasi.isEmpty()) {
            etKonfirmasiPassword.setError("Harap Konfirmasi Kata Sandi Anda");
            etKonfirmasiPassword.requestFocus();
        } else if (!strPwd.equals(strPwdKonfirmasi)) {
            etPassword.setError("Kata Sandi Tidak Sesuai, Harap Periksa Kembali");
            etPassword.requestFocus();
        } else {
            //Menampilkan Alert Dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityLupaPassword.this);

            builder.setCancelable(true);
            builder.setTitle("Apakah Anda Yakin Data Diri Yang Anda Masukkan Benar dan Tepat?");
            builder.setMessage("Apabila Yakin Tekan 'OK'!");
            Log.e("tag", "test");

            builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    UbahPassword(strPwd);
                }
            });
            builder.show();
        }
    }

    private void UbahPassword(final String pwd) {

        if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {

            @SuppressLint("StaticFieldLeak")
            class updatePassword extends AsyncTask<Void, Void, String> {

                @Override
                protected void onPreExecute() {
                    pDialog.setMessage("Memperbarui Kata Sandi, Harap Tunggu...");
                    showDialog();
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(String s) {
                    hideDialog();

                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityLupaPassword.this);
                    builder.setMessage(s)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    myDialog.dismiss();

                                    Intent intent = new Intent(ActivityLupaPassword.this, TampilanLogin.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                    super.onPostExecute(s);
                }

                @Override
                protected String doInBackground(Void... v) {
                    //String nya musti sama dengan yang ada di konfigurasi...
                    HashMap<String, String> params = new HashMap<>();
                    params.put("id_pegawai", id_pegawai);
                    params.put("password", pwd);

                    RequestHandler rh = new RequestHandler();
                    return rh.sendPostRequest(Konfigurasi.URL_RESET_PASSWORD, params);
                }
            }

            updatePassword update = new updatePassword();
            update.execute();

        } else {
            hideDialog();
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    //buat nampilin dialog box nyaa
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    //buat ngilangin dialog box nya
    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


}


