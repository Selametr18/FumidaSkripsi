package com.example.aditmail.fumida.Activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.ProgressDialog;

import android.widget.Toast;

import com.example.aditmail.fumida.Settings.Konfigurasi;
import com.example.aditmail.fumida.R;
import com.example.aditmail.fumida.Settings.RequestHandler;

import java.util.HashMap;

public class TampilanUpdatePassword extends AppCompatActivity implements View.OnClickListener {

    EditText edt_UpdatePass, edt_CekPass;
    Button btn_UpdatePass;

    ConnectivityManager conMgr;
    ProgressDialog loading;

    String id_pegawai = TampilanMenuUtama.id_pegawai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tampilan_update_password);

        edt_UpdatePass = findViewById(R.id.editText_Password_Update);
        edt_CekPass = findViewById(R.id.editText_CekPassword_Update);

        btn_UpdatePass = findViewById(R.id.button_UpdatePassword_Save);
        btn_UpdatePass.setOnClickListener(this);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        loading = new ProgressDialog(this);
        loading.setCancelable(true);
        loading.setCanceledOnTouchOutside(false);
    }

    private void update_pass() {
        final String password_Update = edt_UpdatePass.getText().toString();
        final String cekPassword = edt_CekPass.getText().toString();

        if (TextUtils.isEmpty(password_Update)) {
            edt_UpdatePass.setError("Harap Masukkan Password Anda");
            edt_UpdatePass.requestFocus();
            return;
        }

        if (password_Update.contains(" ")) {
            edt_UpdatePass.setError("Tanda Spasi Tidak Boleh Digunakan!");
            edt_UpdatePass.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(cekPassword)) {
            edt_CekPass.setError("Harap Masukkan Password Anda");
            edt_CekPass.requestFocus();
            return;
        }

        if (cekPassword.contains(" ")) {
            edt_CekPass.setError("Tanda Spasi Tidak Boleh Digunakan!");
            edt_CekPass.requestFocus();
            return;
        }

        if (!password_Update.equals(cekPassword)) {
            edt_CekPass.setError("Password Tidak Sesuai! Harap Cek Kembali");
            return;
        }

        if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {

            @SuppressLint("StaticFieldLeak")
            class updatePassword extends AsyncTask<Void, Void, String> {

                @Override
                protected void onPreExecute() {
                    loading.setMessage("Melakukan Perbaruan Password, Harap Tunggu...");
                    loading.show();
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(String s) {
                    loading.dismiss();
                    Toast.makeText(TampilanUpdatePassword.this, s, Toast.LENGTH_LONG).show();
                    super.onPostExecute(s);
                }

                @Override
                protected String doInBackground(Void... v) {
                    //String nya musti sama dengan yang ada di konfigurasi...
                    HashMap<String, String> params = new HashMap<>();
                    params.put(Konfigurasi.KEY_TAG_ID, id_pegawai);
                    params.put(Konfigurasi.KEY_UPDATE_PASSWORD, password_Update);

                    RequestHandler rh = new RequestHandler();
                    return rh.sendPostRequest(Konfigurasi.URL_UPDATE_PASSWORD, params);
                }
            }

            updatePassword update = new updatePassword();
            update.execute();

        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onClick(View v) {
        if (v == btn_UpdatePass) {
            AlertDialog.Builder builder = new AlertDialog.Builder(TampilanUpdatePassword.this);

            builder.setCancelable(true);
            builder.setTitle("Apakah Anda Yakin Melakukan Perubahan Data Diri? ");
            builder.setMessage("Apabila Yakin Tekan 'OK'!");

            builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    update_pass();
                }
            });
            builder.show();
        }
    }
}
