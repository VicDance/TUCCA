package com.proyecto.transportesbahiacadiz.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.proyecto.transportesbahiacadiz.R;

public class MapStopsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private String latitud;
    private String longitud;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_stops);
        Intent intent = getIntent();
        String direccion = intent.getStringExtra("direccion");
        latitud = direccion.split("/")[0];
        longitud = direccion.split("/")[1];

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_stops);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng latLng = new LatLng(Double.parseDouble(latitud), Double.parseDouble(longitud));

        float zoom = 16;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        map.addMarker(new MarkerOptions().position(latLng));
    }
}
