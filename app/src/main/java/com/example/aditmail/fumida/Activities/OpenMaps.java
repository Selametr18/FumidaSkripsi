package com.example.aditmail.fumida.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.aditmail.fumida.R;
import com.example.aditmail.fumida.Settings.TrackGPS;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class OpenMaps extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks{

    PlacesClient placesClient;

    protected String latlong_koordinat = "0,0";
    protected String nama_lokasi;
    protected TrackGPS gps;
    double longitude;
    double latitude;

    GoogleApiClient googleApiClient;
    GoogleMap googleMap;

    protected ImageView ivmarker;
    protected Button btnSave;

    AutocompleteSupportFragment autocompleteSupportFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_maps);

        ivmarker = findViewById(R.id.ivmarker);
        btnSave = findViewById(R.id.btnSaveLocation);

        //For Maps Tampilin...
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        googleApiClient = new GoogleApiClient.Builder(OpenMaps.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        String apiKey = getString(R.string.google_maps_key);
        if(!Places.isInitialized()){
            Places.initialize(getApplicationContext(), apiKey);
        }

        placesClient = Places.createClient(this);

        autocompleteSupportFragment = (AutocompleteSupportFragment)getSupportFragmentManager()
                .findFragmentById(R.id.autocomplete_fragment);

        //To Return Value
        autocompleteSupportFragment.setTypeFilter(TypeFilter.GEOCODE);
        autocompleteSupportFragment.setCountry("ID");
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                googleMap.clear();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));
                Log.e("tag 1", place.toString());
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i("TAG", "An error occurred: " + status);
            }
        });

        List<Place.Field>fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        Intent autoComplete = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(this);
        startActivityForResult(autoComplete, 101);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent getLocation = getIntent();
                getLocation.putExtra("LatLong_Lokasi", latlong_koordinat);
                getLocation.putExtra("Alamat_Lokasi", nama_lokasi);
                setResult(RESULT_OK, getLocation);
                finish();
            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap mMap){
        googleMap = mMap;
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        gps = new TrackGPS(getApplicationContext());
        if(gps.canGetLocation()){
            longitude = gps.getLongitude();
            latitude = gps .getLatitude();

            latlong_koordinat = latitude + "," + longitude;

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude),15));
        }else{
            gps.showSettingsAlert();
        }

        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                CameraPosition cameraPosition = googleMap.getCameraPosition();
                double lat = cameraPosition.target.latitude;
                double longth = cameraPosition.target.longitude;

                latlong_koordinat = lat + "," + longth;
                String address_result;

                address_result = convertLocation(lat, longth);
                autocompleteSupportFragment.setText(address_result);
            }
        });
    }

    private String convertLocation(double lat, double longth){
        nama_lokasi= null;
        Geocoder geocoder = new Geocoder(OpenMaps.this, Locale.getDefault());

        try{
            List<Address>list = geocoder.getFromLocation(lat, longth, 1);

            if(list != null & list.size() > 0){
                nama_lokasi = list.get(0).getAddressLine(0) + "" + list.get(0).getCountryName();
            }else{
                Toast.makeText(getApplicationContext(), "Maaf Alamat Tidak Ditemukan", Toast.LENGTH_SHORT).show();
            }
        }catch (IOException e){
            e.printStackTrace();
            Log.e("tag_maps", e.toString());
        }
        return nama_lokasi;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 101) {

            if(resultCode == AutocompleteActivity.RESULT_OK){
                Place place = Autocomplete.getPlaceFromIntent(data);
                latlong_koordinat = (place.getLatLng().latitude + "," + place.getLatLng().longitude);

                double lat_result = place.getLatLng().latitude;
                double longth_result = place.getLatLng().longitude;

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat_result, longth_result), 15));
                autocompleteSupportFragment.setText(place.getName());
            }

        }else if(resultCode == AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);
            Log.e("tag", status.toString());
        }else if(resultCode == AutocompleteActivity.RESULT_CANCELED){

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
