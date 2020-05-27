package com.proyecto.tucca.activities;

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
import com.proyecto.tucca.R;
import com.proyecto.tucca.adapters.TimeStopAdapter;
import com.proyecto.tucca.model.TimeStopsItem;

import java.util.ArrayList;

public class MapStopsActivity extends AppCompatActivity/*Fragment*/ implements OnMapReadyCallback {
    BottomSheetBehavior bottomSheetBehavior;
    private RecyclerView recyclerView;
    private TimeStopAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<TimeStopsItem> listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map_stops);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        listItems = new ArrayList<>();
        listItems.add(new TimeStopsItem(1, "Cercano a la parada", "Destino:"));
        listItems.add(new TimeStopsItem(1, "2 minutos", "Destino:"));
        listItems.add(new TimeStopsItem(1, "5 miutos", "Destino:"));
        listItems.add(new TimeStopsItem(3, "5 minutos", "Destino:"));
        listItems.add(new TimeStopsItem(3, "7 minutos", "Destino:"));
        listItems.add(new TimeStopsItem(7, "15 minutos", "Destino:"));
        listItems.add(new TimeStopsItem(7, "18 minutos", "Destino:"));

        buildRecyclerView();
    }
    /*@Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map_stops, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        View bottomSheet = rootView.findViewById(R.id.bottom_sheet_stops);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        listItems = new ArrayList<>();
        listItems.add(new TimeStopsItem(1, "Cercano a la parada", "Destino:"));
        listItems.add(new TimeStopsItem(1, "2 minutos", "Destino:"));
        listItems.add(new TimeStopsItem(1, "5 miutos", "Destino:"));
        listItems.add(new TimeStopsItem(3, "5 minutos", "Destino:"));
        listItems.add(new TimeStopsItem(3, "7 minutos", "Destino:"));
        listItems.add(new TimeStopsItem(7, "15 minutos", "Destino:"));
        listItems.add(new TimeStopsItem(7, "18 minutos", "Destino:"));

        recyclerView = rootView.findViewById(R.id.recycler_view_time_stops);
        buildRecyclerView();
        return rootView;
    }*/

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(36.502927, -6.272037);

        float zoom = 40;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        googleMap.addMarker(new MarkerOptions().position(latLng));
    }

    private void buildRecyclerView(){
        recyclerView = findViewById(R.id.recycler_view_time_stops);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new TimeStopAdapter(listItems);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}
