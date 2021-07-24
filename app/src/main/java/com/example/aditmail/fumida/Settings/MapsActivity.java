package com.example.aditmail.fumida.Settings;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.aditmail.fumida.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener,
        View.OnClickListener {

    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;

    private double latitude, longitude;
    private double lat, longt;
    private String strNameLocation;

    Button btnClearSearch;
    EditText etSearchLocation;
    Button btSaveLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map);

        btnClearSearch = findViewById(R.id.btnClear);

        mapFragment.getMapAsync(this);
        etSearchLocation = findViewById(R.id.etSearchLocation);
        btSaveLocation = findViewById(R.id.btnSaveLocation);

        btnClearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearchLocation.setText("");
            }
        });

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void searchLocation (View view){
        strNameLocation = etSearchLocation.getText().toString();
        List<Address> alamatList = null;

        if (strNameLocation != null || !strNameLocation.equals("")){
            Geocoder geocoder = new Geocoder(this);
            try {
                alamatList = geocoder.getFromLocationName(strNameLocation, 1);
            } catch (IOException e){
                e.printStackTrace();
            }
            Address address = alamatList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title(strNameLocation));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            Toast.makeText(getApplicationContext(), address.getLatitude() + "," + address.getLongitude(),Toast.LENGTH_SHORT).show();

        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            // TODO: Consider Calling
            return;
        }
        mMap.setMyLocationEnabled(true);

        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                lat = cameraPosition.target.latitude;
                longt = cameraPosition.target.longitude;

                strNameLocation = convertLocation(lat, longt);
                etSearchLocation.setText(strNameLocation);

                Log.i("CenterLat", String.valueOf(cameraPosition.target.latitude));
                Log.i("CenterLongt", String.valueOf(cameraPosition.target.longitude));
            }
        });

        btSaveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_loc = new Intent();
                intent_loc.putExtra("alamat", strNameLocation);
                intent_loc.putExtra("lat", lat);
                intent_loc.putExtra("lot", longt);
                setResult(RESULT_OK, intent_loc);
                finish();
            }
        });
    }

    private String convertLocation(double lat, double longt){
        strNameLocation = null;
        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());

        try {
            List<Address> list = geocoder.getFromLocation(lat, longt, 1);
            Toast.makeText(this,  "Koordinat Lokasi : " + lat + ", " + longt, Toast.LENGTH_SHORT).show();
            if (list != null && list.size() > 0) {
                strNameLocation = list.get(0).getAddressLine(0) + "" + list.get(0).getCountryName();
            } else {
                Toast.makeText(this, "Tidak Ada Alamat", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return strNameLocation;

    }

    private void getCurrentLocation(){
        mMap.clear();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            // TODO: Consider Calling
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null){
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            moveMap();
        }
    }

    private void moveMap(){
        /**
         * Creating the latlng object to store lat, long coordinates
         * adding marker to map
         * move the camera with animation
         */
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title("Saya Disini"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    public void onClick(View view) {
        Log.v(TAG,"view click event");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Toast.makeText(MapsActivity.this, "onMarkerDragStart", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        Toast.makeText(MapsActivity.this, "onMarkerDrag", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        // getting the Co-ordinates
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;

        //move to current position
        moveMap();
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast.makeText(MapsActivity.this, "onMarkerClick", Toast.LENGTH_SHORT).show();
        return true;
    }
}
