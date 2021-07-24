package com.example.aditmail.fumida.Settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.Intent;

import com.example.aditmail.fumida.Activities.TampilanLogin;

import java.util.HashMap;

public class PrefManager {

    private SharedPreferences pref;
    private Editor editor;
    private Context _context;

    // shared pref mode
    protected int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "fumidaRefs";

    private static final String IS_LOGIN = "isLoggedIn";

    public static final String KEY_SHARED_ID = Konfigurasi.KEY_GET_ID;
    public static final String KEY_SHARED_NAMA = Konfigurasi.KEY_GET_NAMA;
    public static final String KEY_SHARED_USERNAME = Konfigurasi.KEY_GET_USERNAME;
    public static final String KEY_SHARED_NIK = Konfigurasi.KEY_GET_NIK;
    public static final String KEY_SHARED_IMG = Konfigurasi.KEY_GET_IMG;

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //Create Login Session
    public void createLoginSession(String shared_id, String shared_nama, String shared_username,
                                   String shared_nik, String shared_img){
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(Konfigurasi.KEY_GET_ID, shared_id);
        editor.putString(Konfigurasi.KEY_GET_NAMA, shared_nama);
        editor.putString(Konfigurasi.KEY_GET_USERNAME, shared_username);
        editor.putString(Konfigurasi.KEY_GET_NIK, shared_nik);
        editor.putString(Konfigurasi.KEY_GET_IMG, shared_img);
        editor.commit();
    }

    public void updateProfil(String shared_nama, String shared_username, String shared_nik, String shared_img){
        editor.putString(Konfigurasi.KEY_GET_NAMA, shared_nama);
        editor.putString(Konfigurasi.KEY_GET_USERNAME, shared_username);
        editor.putString(Konfigurasi.KEY_GET_NIK, shared_nik);
        editor.putString(Konfigurasi.KEY_GET_IMG, shared_img);
        editor.commit();
    }

    //Check if User Status is Login Or Not
    public void checkLogin(){
        if(!this.isLoggedIn()){
            Intent intent_login = new Intent (_context, TampilanLogin.class);
            intent_login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent_login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(intent_login);
        }
    }

    //Stored Session Data
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<>();

        user.put(Konfigurasi.KEY_GET_NAMA, pref.getString(Konfigurasi.KEY_GET_NAMA, null));
        user.put(Konfigurasi.KEY_GET_ID, pref.getString(Konfigurasi.KEY_GET_ID, null));
        user.put(Konfigurasi.KEY_GET_USERNAME, pref.getString(Konfigurasi.KEY_GET_USERNAME, null));
        user.put(Konfigurasi.KEY_GET_NIK, pref.getString(Konfigurasi.KEY_GET_NIK, null));
        user.put(Konfigurasi.KEY_GET_IMG, pref.getString(Konfigurasi.KEY_GET_IMG, null));
        return user;
    }

    //Do Logout Activity
    public void logoutUser(Activity activity){
        editor.clear();
        editor.commit();

        Intent intent_logout = new Intent(_context, TampilanLogin.class);
        intent_logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent_logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.finish();
        _context.startActivity(intent_logout);
    }

    //Checking for Login
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }





}
