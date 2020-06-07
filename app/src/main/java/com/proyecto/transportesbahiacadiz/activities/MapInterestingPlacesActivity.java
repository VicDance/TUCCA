package com.proyecto.transportesbahiacadiz.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.proyecto.transportesbahiacadiz.R;
import com.proyecto.transportesbahiacadiz.model.Places;

import java.util.ArrayList;
import java.util.List;

public class MapInterestingPlacesActivity extends AppCompatActivity implements OnMapReadyCallback {
    private List<Places> extra;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_interesting_places);
        extra = new ArrayList<>();
        Intent intent = getIntent();
        extra = (List<Places>) intent.getSerializableExtra("extra");
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_interesting_places);
        mapFragment.getMapAsync(this);
        /*for(int i = 0; i < extra.size(); i++){
            System.out.println(extra.get(i));
        }*/
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.clear();
        String latitud;
        String longitud;
        //map.setMinZoomPreference(16f);
        //map.animateCamera(CameraUpdateFactory.zoomTo(17.0f ));
        //if(extra.get(0).getIdMunicipio())
        LatLng punto = null;
        for(int i = 0; i < extra.size(); i++){
            latitud = extra.get(i).getLatitud();
            longitud = extra.get(i).getLongitud();
            /*if((latitud != null && longitud != null) || (!latitud.equalsIgnoreCase(" ") && !longitud.equalsIgnoreCase(" "))
            || !latitud.equalsIgnoreCase("null")) {
                LatLng punto = new LatLng(Double.parseDouble(latitud), Double.parseDouble(longitud));
                map.addMarker(new MarkerOptions().position(punto));
            }*/
            if(!latitud.isEmpty()){
                punto = new LatLng(Double.parseDouble(latitud), Double.parseDouble(longitud));
                map.addMarker(new MarkerOptions().position(punto));
            }
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(punto, 12f));
        }
    }
}
