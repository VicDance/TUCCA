package com.proyecto.transportesbahiacadiz.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.proyecto.transportesbahiacadiz.R;
import com.proyecto.transportesbahiacadiz.model.Places;

import java.util.ArrayList;
import java.util.List;

import static com.proyecto.transportesbahiacadiz.fragments.SalePointFragment.COURSE_LOCATION;
import static com.proyecto.transportesbahiacadiz.fragments.SalePointFragment.FINE_LOCATION;
import static com.proyecto.transportesbahiacadiz.fragments.SalePointFragment.LOCATION_PERMISSION_REQUEST_CODE;

public class MapInterestingPlacesActivity extends AppCompatActivity implements OnMapReadyCallback {
    private List<Places> extra;
    private GoogleMap map;
    private Boolean locationPermissionsGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_interesting_places);
        extra = new ArrayList<>();
        Intent intent = getIntent();
        extra = (List<Places>) intent.getSerializableExtra("extra");
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_interesting_places);
        checkPermission();
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.clear();
        String latitud;
        String longitud;
        LatLng punto = null;
        if (locationPermissionsGranted) {
            //getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);

        }
        for(int i = 0; i < extra.size(); i++){
            latitud = extra.get(i).getLatitud();
            longitud = extra.get(i).getLongitud();
            if(!latitud.isEmpty() && !longitud.isEmpty()){
                punto = new LatLng(Double.parseDouble(latitud), Double.parseDouble(longitud));
                map.addMarker(new MarkerOptions().position(punto));
            }
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(punto, 12f));
        }
    }

    private void checkPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this,
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this,
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationPermissionsGranted = true;
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
}
