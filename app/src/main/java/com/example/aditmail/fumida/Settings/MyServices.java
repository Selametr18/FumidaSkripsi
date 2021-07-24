package com.example.aditmail.fumida.Settings;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.aditmail.fumida.Activities.AbsensiAtEight;
import com.example.aditmail.fumida.Activities.TampilanMenuUtama;
import com.example.aditmail.fumida.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MyServices extends Service {

    private boolean isRunning;
    protected Context context;
    private Thread backgroundThread;

    int success;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    String tag_json_obj = "json_obj_req";

    /*FOR GPS*/
    public String latlong_Consultant = "0,0";
    public String alamat_six;
    protected TrackGPS gps;
    double longitude;
    double latitude;

    protected String nama_lokasi;

    String dateNow;

    public static String Status = "";
    String res;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getCurrentLocationRoute();

        if(!this.isRunning){
            this.isRunning = true;
            this.backgroundThread.start();
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        this.context = this;
        this.isRunning = false;
        this.backgroundThread = new Thread(myTask);


    }

    private Runnable myTask = new Runnable() {
        public void run() {
            sendLocation();
            saveLocToDB();
            stopSelf();
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        //alarmAtSix();
        this.isRunning = false;
        Log.e("tag3", Status);
    }

    private void getCurrentLocationRoute() {
        gps = new TrackGPS(this);
        if (gps.canGetLocation()) {
            longitude = gps.getLongitude();
            latitude = gps.getLatitude();

            latlong_Consultant = latitude + "," + longitude;
            alamat_six = convertLocation(latitude, longitude);

            Log.e("tag", latlong_Consultant);
        } else {
            Log.e("tag", "Error");
        }
    }

    private void sendLocation() {
        @SuppressLint("StaticFieldLeak")
        class simpan extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.e("tag2", s);
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();

                params.put(Konfigurasi.KEY_SAVE_LATLONG_CONSULTANT, String.valueOf(latlong_Consultant));
                params.put(Konfigurasi.KEY_TAG_ID, TampilanMenuUtama.id_pegawai);

                RequestHandler rh = new RequestHandler();
                res = rh.sendPostRequest(Konfigurasi.URL_SEND_LOCATION_SIX, params);

                return res;

            }
        }
        simpan save = new simpan();
        save.execute();
    }

    private void saveLocToDB(){
        dateNow = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());

        StringRequest strReq = new StringRequest(Request.Method.POST, Konfigurasi.URL_CEK_ABSENSI_AT_SIX, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("tag", "Check Response: " + response);

                try {
                    //select untuk nyimpen data yg ingin di retrieve
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    if (success == 0) {
                        Log.d("tag save Six", jObj.getString(TAG_MESSAGE));
                    }
                    else {
                        Log.d("tag update Six", jObj.getString(TAG_MESSAGE));
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
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(Konfigurasi.KEY_GET_ID, TampilanMenuUtama.id_pegawai);
                params.put(Konfigurasi.KEY_GET_ALAMAT, alamat_six);
                params.put(Konfigurasi.KEY_GET_LATLONG, latlong_Consultant);
                params.put(Konfigurasi.KEY_GET_TANGGAL, dateNow);
                params.put(Konfigurasi.KEY_GET_NAMA, TampilanMenuUtama.namaLengkap);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private String convertLocation(double lat, double longth){
        nama_lokasi = null;
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try{
            List<Address> list = geocoder.getFromLocation(lat, longth, 1);

            if(list != null & list.size() > 0){
                nama_lokasi = list.get(0).getAddressLine(0) + "" + list.get(0).getCountryName();
            }else{
                //Toast.makeText(getApplicationContext(), "Maaf Alamat Tidak Ditemukan", Toast.LENGTH_SHORT).show();
                nama_lokasi = "Alamat Tidak Ditemukan, Silakan Cek Koordinat Secara Manual";
                Log.e("tag_maps", "Alamat Tidak Ditemukan");
            }
        }catch (IOException e){
            e.printStackTrace();
            nama_lokasi = "Alamat Tidak Ditemukan, Silakan Cek Koordinat Secara Manual";
            Log.e("tag_maps", e.toString());
        }
        return nama_lokasi;
    }
}
