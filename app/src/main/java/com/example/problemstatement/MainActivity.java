package com.example.problemstatement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    TextView tvLat, tvLong;
    private GoogleMap map;
    Button btnLocationUpdate, btnRemoveUpdate;
    LocationRequest mLocationRequest;
    LocationCallback mLocationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //====================UIMatch==================
        tvLong = findViewById(R.id.tvLong);
        tvLat = findViewById(R.id.tvLat);

        btnLocationUpdate = findViewById(R.id.btnLocationUpdate);
        btnRemoveUpdate = findViewById(R.id.btnRemoveUpdate);
        //====================UIMatch==================

        checkPermission();

        //=================ConnectGPlayService===================
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        Task<Location> task = client.getLastLocation();
        //=================ConnectGPlayService===================

        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment)
                fm.findFragmentById(R.id.map);


        //========GetReferenceToGoogleMapObject========
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            //-----------RunOnceLoaded----------
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null){
                            //------------------PointToLocation---------------
                            LatLng lastLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            //------------------PointToLocation---------------

                            //-----------------Display----------------
                            tvLat.setText("Latitude: " + location.getLatitude());
                            tvLong.setText("Longitude: " + location.getLongitude());

                            Marker cp = map.addMarker(new
                                    MarkerOptions()
                                    .position(lastLocation)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            //-----------------Display----------------
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation,
                                    15));


                            UiSettings ui = map.getUiSettings();
                            ui.setZoomControlsEnabled(true);
                        }

                        else{
                            Toast.makeText(MainActivity.this, "No Last Known Location found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            //-----------RunOnceLoaded----------
        });
        //========GetReferenceToGoogleMapObject========


        //====================LocationUpdate===================
        btnLocationUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                mLocationRequest = LocationRequest.create();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setInterval(30000);
                mLocationRequest.setSmallestDisplacement(500);

                mLocationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult != null) {
                            Location data = locationResult.getLastLocation();
                            double lat = data.getLatitude();
                            double lng = data.getLongitude();

                            //------------------PointToLocation---------------
                            LatLng lastLocation = new LatLng(lat, lng);
                            //------------------PointToLocation---------------

                            //-----------------Display----------------
                            map.clear();
                            map.addMarker(new
                                    MarkerOptions()
                                    .position(lastLocation)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            //-----------------Display----------------
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation,
                                    15));

                        }
                    };
                };
                client.requestLocationUpdates(mLocationRequest, mLocationCallback, null);

            }
        });
        //====================LocationUpdate===================


        //====================RemoveUpdate===================
        btnRemoveUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                client.removeLocationUpdates(mLocationCallback);
            }
        });
        //====================RemoveUpdate===================
    }




    //===============-------FuncPermissionChecker------==================
    private boolean checkPermission(){
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        }
        else {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            return false;
        }
    }
    //===============-------FuncPermissionChecker------==================
}