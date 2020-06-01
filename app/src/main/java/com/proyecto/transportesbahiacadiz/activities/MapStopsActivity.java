package com.proyecto.transportesbahiacadiz.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.proyecto.transportesbahiacadiz.R;
import com.proyecto.transportesbahiacadiz.adapters.TimeStopAdapter;
import com.proyecto.transportesbahiacadiz.model.TimeStopsItem;

import java.io.IOException;
import java.util.ArrayList;

import static com.proyecto.transportesbahiacadiz.activities.MainActivity.dataIn;

public class MapStopsActivity extends AppCompatActivity /*implements OnMapReadyCallback*/ {
    private String latitud;
    private String longitud;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_stops);
        try {
            String direccion = dataIn.readUTF().trim();
            //latitud = direccion.split("/")[0];
            //longitud = direccion.split("/")[1];
            System.out.println(direccion);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_stops);
        //mapFragment.getMapAsync(this);
    }

    /*@Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng latLng = new LatLng(Double.parseDouble(latitud), Double.parseDouble(longitud));

        float zoom = 40;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        map.addMarker(new MarkerOptions().position(latLng));
    }*/
}
