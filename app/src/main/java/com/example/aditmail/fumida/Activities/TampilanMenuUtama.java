package com.example.aditmail.fumida.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.aditmail.fumida.Fumigasi.TabFumigasi;
import com.example.aditmail.fumida.PestControl.TabSurveiPestControl;
import com.example.aditmail.fumida.Settings.AutoStart;
import com.example.aditmail.fumida.Settings.AutoStart_Eight;
import com.example.aditmail.fumida.Settings.Konfigurasi;
import com.example.aditmail.fumida.R;
import com.example.aditmail.fumida.Settings.PrefManager;
import com.example.aditmail.fumida.Settings.RequestHandler;
import com.example.aditmail.fumida.Settings.TrackGPS;
import com.example.aditmail.fumida.TermiteControl.TabSurveiTermiteControl;
import com.example.aditmail.fumida.WorkReport.TabWorkReport;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TampilanMenuUtama extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    GoogleApiClient mLocationClient;
    LocationRequest mLocationRequest = new LocationRequest();

    ImageView Image_FormSurvey, Image_WorkReport, Image_Picture;
    TextView txt_Username, txt_NamaLengkap, txt_NIK, txt_id;

    public static String id_pegawai;
    public static String username;
    public static String namaLengkap;
    public static String nik;
    public static String imgPath;

    private Context mContext;
    Dialog myDialog;

    //double longitude;
    //double latitude;
    String convert_lat, convert_longth;

    //Worker
    //Data data;

    /*FOR GPS*/
    public String koordinat = "0,0";
    protected TrackGPS gps;
    double longitude = 0.0;
    double latitude = 0.0;

    //Session Class
    PrefManager session;

   /* public PendingIntent pendingIntent_six, pendingIntent_eight;
    public AlarmManager alarmManager_six, alarmManager_eight;*/

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tampilan_menu_utama);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
/*
        alarmManager_six = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager_eight = (AlarmManager)getSystemService(Context.ALARM_SERVICE);*/

        //For Maps/LatLong
        mLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest.setInterval(90 * 60 * 1000);
        mLocationRequest.setFastestInterval(60 * 60 * 1000);

        int priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
        mLocationRequest.setPriority(priority);
        mLocationClient.connect();

        mContext = this;
        myDialog = new Dialog(TampilanMenuUtama.this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestPermissions();
        }

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header =  navigationView.getHeaderView(0);

        txt_Username = header.findViewById(R.id.textView_Username_Nav);
        txt_NIK = header.findViewById(R.id.textView_NIK_Nav);
        Image_Picture = header.findViewById(R.id.imgPicture_Menu);

        txt_NamaLengkap = findViewById(R.id.textView_NamaPegawai);
        txt_id = findViewById(R.id.textView_IdPegawai);

//        Image_FormSurvey = findViewById(R.id.imageView_FormSurvei);
        Image_WorkReport = findViewById(R.id.imageView_WorkReport);

        txt_NamaLengkap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Image_Picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpImage();
            }
        });

//        Image_FormSurvey.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                Intent intent_FormSurvei = new Intent(TampilanMenuUtama.this, TampilanSubMenuFormSurvey.class);
//                startActivity(intent_FormSurvei);
//            }
//        });

        Image_WorkReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent_WorkReport = new Intent(TampilanMenuUtama.this, TabWorkReport.class);
                startActivity(intent_WorkReport);
            }
        });

        txt_id.setText("ID:" + id_pegawai);

        //getCurrentLocationRoute();
    }

    @Override
    protected void onStart(){
        getCurrentLocationRoute();
        Log.e("tag", "onStart");

        //TEST BARU...
        serviceAtSix();
        serviceAtEight();
        //startWorkManager();

        session = new PrefManager(getApplicationContext());
        session.checkLogin();

        HashMap<String, String> user = session.getUserDetails();
        id_pegawai = user.get(PrefManager.KEY_SHARED_ID);
        namaLengkap = user.get(PrefManager.KEY_SHARED_NAMA);
        username = user.get(PrefManager.KEY_SHARED_USERNAME);
        nik = user.get(PrefManager.KEY_SHARED_NIK);
        imgPath = user.get(PrefManager.KEY_SHARED_IMG);

        super.onStart();
    }

    @Override
    protected void onResume(){
        Log.e("tag", "onResume");
        txt_NamaLengkap.setText("Halo, " + namaLengkap);

        txt_Username.setText(username);
        txt_NIK.setText("NIK :" + nik);

        Log.e("tags", imgPath);

        try {
            Glide.with(mContext)
                    .load(Konfigurasi.url_image + imgPath)
                    .error(Glide.with(Image_Picture).load(R.drawable.ic_person))
                    .into(Image_Picture);
        }catch (NullPointerException e){
            e.printStackTrace();
            Log.e("tag", String.valueOf(e));
        }


        super.onResume();
    }

    @Override
    protected void onDestroy(){
        Log.e("tag", "onDestroy");
        serviceAtSix();
        serviceAtEight();

        super.onDestroy();
    }

    @Override
    protected void onPause(){
        Log.e("tag", "onPause");
        serviceAtSix();
        serviceAtEight();

        super.onPause();
    }

    private void getCurrentLocationRoute() {
        gps = new TrackGPS(this);
        if (gps.canGetLocation()) {
            longitude = gps.getLongitude();
            latitude = gps.getLatitude();
            koordinat = latitude + "," + longitude;
        } else {
            Log.e("tag", "Error");
        }
    }

    public void serviceAtSix(){
        //Log.e("tag", "test running onstart");
        Intent autoStart = new Intent (mContext, AutoStart.class);
        boolean autoStart_Running = (PendingIntent.getBroadcast(mContext, 1, autoStart, PendingIntent.FLAG_NO_CREATE) != null);

        Log.e("tag bol", String.valueOf(autoStart_Running));

        if (!autoStart_Running){
            PendingIntent pendingIntent_six = PendingIntent.getBroadcast(mContext, 1, autoStart, 0);
            AlarmManager alarmManager_six= (AlarmManager)getSystemService(Context.ALARM_SERVICE);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 18);
            calendar.set(Calendar.MINUTE, 0);

            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1);
            }

            if(alarmManager_six!= null) {
                alarmManager_six.setInexactRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent_six
                );
            }

        }
    }

    public void serviceAtEight(){
        //Log.e("tag", "test running onstart");

        Intent autoStart_Eight = new Intent (mContext, AutoStart_Eight.class);
        boolean autoStart_Running_Eight = (PendingIntent.getBroadcast(mContext, 2, autoStart_Eight, PendingIntent.FLAG_NO_CREATE) != null);

        Log.e("tag bol", String.valueOf(autoStart_Running_Eight));

        if (!autoStart_Running_Eight){
            PendingIntent pendingIntent_eight = PendingIntent.getBroadcast(mContext, 2, autoStart_Eight, 0);
            AlarmManager alarmManager_eight = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 8);
            calendar.set(Calendar.MINUTE, 30);

            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1);
            }

            if(alarmManager_eight!=null) {
                alarmManager_eight.setInexactRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent_eight
                );
            }
        }
    }

    @Override
    public void onConnected(Bundle dataBundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            Log.d("tag", "== Error On onConnected() Permission not granted");
            //Permission not granted by user so cancel the further execution.

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);

        Log.d("tag", "Connected to Google API");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("tag", "Connection suspended");
    }

    //to get the location change
    @Override
    public void onLocationChanged(Location location) {
        Log.d("tag", "Location changed");
        if (location != null) {
            Log.d("tag", "== location != null");

            //Send result to activities
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            convert_lat = String.valueOf(latitude);
            convert_longth = String.valueOf(longitude);
            koordinat = convert_lat + "," + convert_longth;

            sendLocation(latitude, longitude);
            Log.e("tag", koordinat);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("tag", "Failed to connect to Google API");
    }

    private void sendLocation(final double latitude, final double longitude){
        @SuppressLint("StaticFieldLeak")
        class simpan extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.e("tag1" , s);
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();

                params.put(Konfigurasi.KEY_SAVE_LATLONG_CONSULTANT, String.valueOf(latitude+","+longitude));
                params.put(Konfigurasi.KEY_TAG_ID, TampilanMenuUtama.id_pegawai);

                RequestHandler rh = new RequestHandler();

                return rh.sendPostRequest(Konfigurasi.URL_SEND_LOCATION_ONE_HALF_HOUR, params);

            }
        }
        simpan save = new simpan();
        save.execute();
    }

    private void popUpImage(){
        myDialog.setContentView(R.layout.pop_up_image_profil);

        ImageView imgProfil = myDialog.findViewById(R.id.imgPicture_PopUp);

        try {
            Glide.with(mContext)
                    .load(Konfigurasi.url_image + imgPath)
                    .error(Glide.with(imgProfil).load(R.drawable.ic_person))
                    .into(imgProfil);
        }catch (NullPointerException e){
            e.printStackTrace();
            Log.e("tag", String.valueOf(e));
        }

        Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        myDialog.show();
    }

    boolean doubleBackToExit = false;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (doubleBackToExit){
            super.onBackPressed();
            return;
        }

        this.doubleBackToExit = true;
        Toast.makeText(this, "Tekan Tombol 'Back' Lagi untuk Keluar", Toast.LENGTH_LONG).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExit=false;
            }
        },2000);
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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent profil = new Intent(TampilanMenuUtama.this, TampilanProfile.class);
            startActivity(profil);
//        } else if (id == R.id.nav_pest) {
//            Intent profil = new Intent(TampilanMenuUtama.this, TabSurveiPestControl.class);
//            startActivity(profil);
//        } else if (id == R.id.nav_termite) {
//            Intent profil = new Intent(TampilanMenuUtama.this, TabSurveiTermiteControl.class);
//            startActivity(profil);
//        } else if (id == R.id.nav_fumigasi) {
//            Intent profil = new Intent(TampilanMenuUtama.this, TabFumigasi.class);
//            startActivity(profil);
        } else if (id == R.id.nav_work_report) {
            Intent profil = new Intent(TampilanMenuUtama.this, TabWorkReport.class);
            startActivity(profil);
//        } else if (id == R.id.nav_absensi) {
//            Intent absensi = new Intent(TampilanMenuUtama.this, AbsensiAtEight.class);
//            startActivity(absensi);
        }
        else if (id == R.id.nav_keluar) {
            Logout(TampilanMenuUtama.this);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void Logout(Activity activity){
        /*if(pendingIntent_six!=null) {
            alarmManager_six.cancel(pendingIntent_six);
        }
        if(pendingIntent_eight!=null) {
            alarmManager_eight.cancel(pendingIntent_eight);
        }*/
        session.logoutUser(activity);
    }

}
