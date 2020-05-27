package com.proyecto.tucca.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
import com.proyecto.tucca.R;
import com.proyecto.tucca.model.Centre;
import com.proyecto.tucca.viewmodel.LiveDataCentre;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;
import static com.proyecto.tucca.activities.MainActivity.dataIn;
import static com.proyecto.tucca.activities.MainActivity.dataOut;

public class SalePointFragment extends Fragment implements OnMapReadyCallback {
    private View view;
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Spinner spinner;
    private ArrayAdapter<String> adapterCentre;
    private Centre[] nucleos;
    private String[] nombreNucleos;
    private int idNucleo;
    private List<String> latitudes;
    private List<String> longitudes;
    private String latitud;
    private String longitud;
    private LiveDataCentre liveDataCentre;
    private Button button;
    private Boolean locationPermissionsGranted = false;

    public static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    public static final float ZOOM = 16f;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sale_point, container, false);
        button = view.findViewById(R.id.btn_buscar);
        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getContext(), MapsActivity.class));
                checkPermission();
                mapFragment.getMapAsync(SalePointFragment.this);
            }
        });
        try {
            dataOut.writeUTF("puntos_venta");
            dataOut.flush();
            int size = dataIn.readInt();
            nucleos = new Centre[size];
            nombreNucleos = new String[size];
            String datos;
            String[] newDatos;
            for(int i = 0; i < size; i++){
                datos = dataIn.readUTF();
                newDatos = datos.split("/");
                nucleos[i] = new Centre(Integer.parseInt(newDatos[1]), newDatos[0]);
                nombreNucleos[i] = newDatos[0];
                System.out.println(nombreNucleos[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        spinner = view.findViewById(R.id.spinner_sale_point);
        setSpinner();
        return view;
    }

    private void setSpinner(){
        adapterCentre = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, nombreNucleos);
        adapterCentre.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterCentre);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                liveDataCentre = new ViewModelProvider((ViewModelStoreOwner) getContext()).get(LiveDataCentre.class);
                String nucleo = (String) parent.getSelectedItem();
                String[] direccionMap;
                latitudes = new ArrayList<String>();
                longitudes = new ArrayList<String>();
                for(int i = 0; i < nucleos.length; i++){
                    if(nucleo.equals(nucleos[i].getNombreNucleo())){
                        idNucleo = nucleos[i].getIdNucleo();
                        try {
                            dataOut.writeUTF("puntos_venta_mapa");
                            dataOut.writeInt(idNucleo);
                            dataOut.flush();
                            System.out.println(idNucleo);
                            int puntosSize = dataIn.readInt();
                            //direccionMap = new String[puntosSize];
                            String direcciones;
                            for(int x = 0; x < puntosSize; x++){
                                direcciones = dataIn.readUTF();
                                String[] newDireccion = direcciones.split("/");
                                latitudes.add(newDireccion[0]);
                                longitudes.add(newDireccion[1]);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                liveDataCentre.getCentreList().observe((LifecycleOwner) getContext(), new Observer<List<Centre>>() {
                    @Override
                    public void onChanged(List<Centre> centres) {
                        for (Centre centre : centres) {
                            centres.add(centre);
                            liveDataCentre.addCentre(centre);
                        }
                        adapterCentre.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

   @Override
    public void onMapReady(GoogleMap googleMap) {
       //LatLng punto = new LatLng(36.527082, -6.288597);
       map = googleMap;
       map.clear();
       if (locationPermissionsGranted) {
           getDeviceLocation();
           if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                   != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                   Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
               return;
           }
           map.setMyLocationEnabled(true);
           map.getUiSettings().setMyLocationButtonEnabled(false);

       }
        if(idNucleo != 0){
            //System.out.println("entra");
            for(int i = 0, j = 0; i < latitudes.size() && j < longitudes.size(); i++, j++){
                latitud = latitudes.get(i);
                longitud = longitudes.get(j);
                //System.out.println("Latitud " + latitud + "Longitud " + longitud);
                LatLng punto = new LatLng(Double.parseDouble(latitud), Double.parseDouble(longitud));
                //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(punto, 16f));
                map.addMarker(new MarkerOptions().position(punto));
            }
            latitudes.clear();
            longitudes.clear();
            /*System.out.println("Latitud " + latitudes.get(0) + "Longitud " + longitudes.get(0));
            LatLng punto = new LatLng(Double.parseDouble(latitud), Double.parseDouble(longitud));
            map.addMarker(new MarkerOptions().position(punto));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(punto, 400.0f));*/
        }
    }

    private void getDeviceLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        try{
            if(locationPermissionsGranted){
                final Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    ZOOM);

                        }else{
                            Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            System.out.println("getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom){
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void checkPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(getContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(getContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationPermissionsGranted = true;
            }else{
                ActivityCompat.requestPermissions(getActivity(),
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(getActivity(),
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
}
