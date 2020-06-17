package com.proyecto.transportesbahiacadiz.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.proyecto.transportesbahiacadiz.R;
import com.proyecto.transportesbahiacadiz.model.Stop;
import com.proyecto.transportesbahiacadiz.util.ConnectionClass;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static com.proyecto.transportesbahiacadiz.fragments.SalePointFragment.COURSE_LOCATION;
import static com.proyecto.transportesbahiacadiz.fragments.SalePointFragment.FINE_LOCATION;
import static com.proyecto.transportesbahiacadiz.fragments.SalePointFragment.LOCATION_PERMISSION_REQUEST_CODE;

public class StopsUserPositionActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private ConnectionClass connectionClass;
    private Boolean locationPermissionsGranted = false;
    private Stop[] stops;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stops_user_position);
        connectionClass = new ConnectionClass(this);
        new getParadasTask().execute();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.clear();

        if (locationPermissionsGranted) {
            //getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(false);
            getDeviceLocation();
        }

        for(int i = 0; i < stops.length; i++){
            String latitud = stops[i].getLatitud();
            String longitud = stops[i].getLongitud();
            if(!latitud.isEmpty() && !longitud.isEmpty()){
                LatLng punto = new LatLng(Double.parseDouble(latitud), Double.parseDouble(longitud));
                map.addMarker(new MarkerOptions().position(punto));
            }
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

    private void getDeviceLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(locationPermissionsGranted){
                final Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            currentLocation = (Location) task.getResult();

                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 12f));

                        }else{
                            Toast.makeText(StopsUserPositionActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            e.getMessage();
        }
    }

    class getParadasTask extends AsyncTask<Void, Void, Void> {
        Socket cliente;
        DataInputStream dataIn;
        DataOutputStream dataOut;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                cliente = new Socket(connectionClass.getConnection().get(0).getAddress(), connectionClass.getConnection().get(0).getPort());
                dataIn = new DataInputStream(cliente.getInputStream());
                dataOut = new DataOutputStream(cliente.getOutputStream());

                dataOut.writeUTF("paradas");
                dataOut.flush();

                int size = dataIn.readInt();
                stops = new Stop[size];
                String parada;
                String[] datos;
                for(int i = 0; i < size; i++){
                    parada = dataIn.readUTF();
                    datos = parada.split("¬");
                    Stop stop = new Stop(datos[3], datos[4]);
                    stops[i] = stop;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            checkPermission();
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_stops_user_position);
            mapFragment.getMapAsync(StopsUserPositionActivity.this);
        }
    }
}
