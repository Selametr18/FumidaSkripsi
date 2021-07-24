package com.example.aditmail.fumida.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import android.app.ProgressDialog;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.example.aditmail.fumida.Settings.Konfigurasi;
import com.example.aditmail.fumida.R;
import com.example.aditmail.fumida.Settings.PrefManager;
import com.example.aditmail.fumida.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.widget.TextView;
import android.widget.Toast;

public class TampilanLogin extends AppCompatActivity implements View.OnClickListener {

    protected ProgressDialog pDialog;

    protected Button  btn_Login;
    //protected ImageView img_MenuRegistrasi;
    protected EditText edt_Username, edt_Password;
    protected TextView tvLupaPassword, tvRegistrasi;

    int success;
    ConnectivityManager conMgr;

    private static final String TAG = TampilanLogin.class.getSimpleName();

    //inisiasi jika berhasil
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    //untuk proses pertukaran data
    protected String tag_json_obj = "json_obj_req";

    protected SharedPreferences sharedpreferences;

    //Session Class
    protected PrefManager session;

    //get Username
    protected String pref_getName;

    //untuk deactivate session nya
    //protected Boolean session = false;
    protected String shared_id, shared_nama, shared_username, shared_nik, shared_img;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_tampilan_login);

        //PrefManager
        session = new PrefManager(getApplicationContext());

        conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);
        pDialog.setCanceledOnTouchOutside(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestPermissions();
        }

        //img_MenuRegistrasi = findViewById(R.id.imageView_Registrasi);
        btn_Login = findViewById(R.id.button_Login);

        edt_Username = findViewById(R.id.editText_Username);
        edt_Password = findViewById(R.id.editText_Password);

        tvLupaPassword = findViewById(R.id.txtLupaPassword);
        tvRegistrasi = findViewById(R.id.tvRegistrasi);

        tvLupaPassword.setOnClickListener(this);
        btn_Login.setOnClickListener(this);

        tvRegistrasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent_Registrasi = new Intent(TampilanLogin.this, TampilanRegistrasi.class);
                startActivity(intent_Registrasi);
            }
        });
    }

    @Override
    protected void onStart(){
        if(session.isLoggedIn()){
            HashMap<String,String> user = session.getUserDetails();
            pref_getName = user.get(PrefManager.KEY_SHARED_USERNAME);
            Toast.makeText(getApplicationContext(), "Halo, " + pref_getName + "!", Toast.LENGTH_LONG).show();
            Intent intent_session = new Intent(TampilanLogin.this, TampilanMenuUtama.class);
            startActivity(intent_session);
            finish();
        }

        super.onStart();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    private void LoginAkun(){
        String Username = edt_Username.getText().toString();
        String Password = edt_Password.getText().toString();

        if (TextUtils.isEmpty(Username)) {
            edt_Username.setError("Mohon Masukkan Username Anda");
            edt_Username.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(Password)) {
            edt_Password.setError("Mohon Masukkan Password Anda");
            edt_Password.requestFocus();
            return;
        }

        if (Username.contains(" ")) {
            edt_Username.setError("Tanda Spasi Tidak Boleh Digunakan!");
            edt_Username.requestFocus();
            return;
        }

        if (Password.contains(" ")) {
            edt_Password.setError("Tanda Spasi Tidak Boleh Digunakan!");
            edt_Password.requestFocus();
            return;
        }

        if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {

            checkLogin(Username, Password);

        }else {
            Toast.makeText(getApplicationContext() ,"No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    private void checkLogin(final String Username, final String Password){

        pDialog.setMessage("Logging in ...");
        showDialog();

        String url_Login = Konfigurasi.URL_LOGIN;
        StringRequest strReq = new StringRequest(Request.Method.POST, url_Login, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Login Response: " + response);
                hideDialog();

                try {
                    //select untuk nyimpen data yg ingin di retrieve
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        //jika sukses, akan ambil data nama lengkap dan id dari database/ sql
                        String id = jObj.getString(Konfigurasi.KEY_GET_ID);
                        String namaLengkap = jObj.getString(Konfigurasi.KEY_GET_NAMA);
                        String username = jObj.getString(Konfigurasi.KEY_GET_USERNAME);
                        String nik = jObj.getString(Konfigurasi.KEY_GET_NIK);
                        String imgPath = jObj.getString(Konfigurasi.KEY_GET_IMG);

                        Log.e("Successfully Login!", jObj.toString());

                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        session.createLoginSession(id, namaLengkap, username, nik, imgPath);

                        // Memanggil menu utama

                        Intent intent = new Intent(TampilanLogin.this, TampilanMenuUtama.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            //jika error
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
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
                params.put(Konfigurasi.KEY_LOGIN_USERNAME, Username);
                params.put(Konfigurasi.KEY_LOGIN_PASSWORD, Password);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
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

    @Override
    public void onClick(View v) {
        if(v == btn_Login){
            LoginAkun();
        }
        if(v == tvLupaPassword){
            //showPopUpForget(v);
            Intent a = new Intent(this, ActivityLupaPassword.class);
            startActivity(a);
            Log.e("tag", "test");
        }
    }

    private void checkAndRequestPermissions() {
        int camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int storage2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int loc = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int loc2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (storage2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (loc != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (loc2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray
                    (new String[0]),1);
        }
    }
}

