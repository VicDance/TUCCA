package com.proyecto.transportesbahiacadiz.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.proyecto.transportesbahiacadiz.adapters.TripAdapter;
import com.proyecto.transportesbahiacadiz.adapters.ZoneAdapter;
import com.proyecto.transportesbahiacadiz.dialogs.LocationDialog;
import com.proyecto.transportesbahiacadiz.R;
import com.proyecto.transportesbahiacadiz.model.Trip;
import com.proyecto.transportesbahiacadiz.model.Zone;
import com.proyecto.transportesbahiacadiz.util.ConnectionClass;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.proyecto.transportesbahiacadiz.activities.MainActivity.login;
import static com.proyecto.transportesbahiacadiz.activities.RegisterActivity.usuario;

public class MainFragment extends Fragment {
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    private TextView title;
    private int size;
    private Zone[] zonas;
    private Trip[] trips;
    private String[] newDatos;
    private RecyclerView recyclerViewNextTrip;
    private RecyclerView recyclerViewDone;
    private TextView textViewNextTrip;
    private TextView textViewDone;
    private ZoneAdapter zoneAdapter;
    private TripAdapter tripAdapter;
    private RecyclerView.LayoutManager layoutManagerNextTrip;
    private RecyclerView.LayoutManager layoutManagerDone;
    private List<Zone> zones;
    private List<String> viajesARealizar;
    private List<String> viajesRealizados;
    private String content;
    private ConnectionClass connectionClass;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        recyclerViewNextTrip = view.findViewById(R.id.recycler_view_next_trip);
        recyclerViewDone = view.findViewById(R.id.recycler_view_done);
        textViewNextTrip = view.findViewById(R.id.text_view_next_trip);
        textViewDone = view.findViewById(R.id.text_view_done);
        title = view.findViewById(R.id.text_view_fare_system_title);
        zones = new ArrayList<>();
        viajesARealizar = new ArrayList<>();
        viajesRealizados = new ArrayList<>();
        connectionClass = new ConnectionClass(getContext());
        if(login){
            title.setText("Historial de viajes");
            recyclerViewNextTrip.setHasFixedSize(true);
            layoutManagerNextTrip = new LinearLayoutManager(getContext());
            layoutManagerDone = new LinearLayoutManager(getContext());
            recyclerViewNextTrip.setLayoutManager(layoutManagerNextTrip);
            recyclerViewDone.setLayoutManager(layoutManagerDone);
            getViajes();
        }else {
            textViewNextTrip.setVisibility(View.GONE);
            textViewDone.setVisibility(View.GONE);
            recyclerViewDone.setVisibility(View.GONE);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            recyclerViewNextTrip.setLayoutParams(param);
            recyclerViewNextTrip.setHasFixedSize(true);
            layoutManagerNextTrip = new LinearLayoutManager(getContext());
            recyclerViewNextTrip.setLayoutManager(layoutManagerNextTrip);
            getZonas();
        }
        return view;
    }

    private void getViajes(){
        new getViajesTask().execute();
    }

    private void getZonas(){
        new getZonasTask().execute();
    }

    private void buildRecyclerZonas(){
        zoneAdapter = new ZoneAdapter((ArrayList<Zone>) zones);
        recyclerViewNextTrip.setAdapter(zoneAdapter);
    }

    private void buildRecyclerProximosViajes(){
        tripAdapter = new TripAdapter((ArrayList<String>) viajesARealizar);
        recyclerViewNextTrip.setAdapter(tripAdapter);
    }

    private void buildRecyclerViajesRealizados(){
        tripAdapter = new TripAdapter((ArrayList<String>) viajesRealizados);
        recyclerViewDone.setAdapter(tripAdapter);
    }

    private boolean compruebaFecha(String fechaViaje, String hoy){
        boolean igual = false;
        String[] fechaViajeSplit = fechaViaje.split("-");
        String[] hoySplit = hoy.split("-");
        if(Integer.parseInt(fechaViajeSplit[0]) == Integer.parseInt(hoySplit[0]) && Integer.parseInt(fechaViajeSplit[1]) == Integer.parseInt(hoySplit[1])
                && Integer.parseInt(fechaViajeSplit[2]) == Integer.parseInt(hoySplit[2])){
            igual = true;
        }

        return igual;
    }

    private String compruebaHora(String horaViaje, String nuevaHora){
        String cad = "";
        String[] fechaViajeSplit = horaViaje.split(":");
        String[] hoySplit = nuevaHora.split(":");
        if(Integer.parseInt(fechaViajeSplit[0]) > Integer.parseInt(hoySplit[0])){
            cad = "mayor";
        }else if(Integer.parseInt(fechaViajeSplit[0]) == Integer.parseInt(hoySplit[0])){
            if(Integer.parseInt(fechaViajeSplit[1]) > Integer.parseInt(hoySplit[1])){
                cad = "mayor";
            }else{
                cad = "menor";
            }
        }else{
            cad = "menor";
        }
        return cad;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search_menu, menu);
        inflater.inflate(R.menu.location_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setQueryHint("Introduzca nombre o código de parada");
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.i("onQueryTextChange", newText);

                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.i("onQueryTextSubmit", query);

                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        searchView.setOnQueryTextListener(queryTextListener);
        switch (item.getItemId()) {
            case R.id.action_search:
                // Not implemented here
                return false;
            case R.id.action_location:
                item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        showDialog();
                        return true;
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        //searchView.setOnQueryTextListener(queryTextListener);
        //return super.onOptionsItemSelected(item);
    }

    private void showDialog() {
        new LocationDialog().show(getFragmentManager(), "Location Dialog");
    }

    class getViajesTask extends AsyncTask<Void, Void, Void>{
        Socket cliente;
        DataInputStream dataIn;
        DataOutputStream dataOut;

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                cliente = new Socket(connectionClass.getConnection().get(0).getAddress(), connectionClass.getConnection().get(0).getPort());
                dataIn = new DataInputStream(cliente.getInputStream());
                dataOut = new DataOutputStream(cliente.getOutputStream());

                dataOut.writeUTF("viajes");
                dataOut.flush();
                dataOut.writeInt(usuario.getId());
                dataOut.flush();
                int size = dataIn.readInt();
                trips = new Trip[size];
                for(int i = 0; i < size; i++){
                    String texto = dataIn.readUTF();
                    String[] datos = texto.split("/");
                    java.util.Date date = new java.util.Date();
                    date.setTime(Long.parseLong(datos[3]));
                    Trip trip = new Trip(datos[0], datos[1], datos[2], new Date(date.getTime()));
                    trips[i] = trip;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYY");
                SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
                java.util.Date d = new java.util.Date();
                java.sql.Date date = new java.sql.Date(d.getTime());
                System.out.println("Cantidad viajes " + trips.length);
                for (int i = 0; i < trips.length; i++) {
                    content = "- Línea: " + trips[i].getLinea() + " Municipio de destino: " + trips[i].getMunicipio() + " Hora de salida: " +
                            trips[i].getHoraSalida() + " Fecha del viaje: " + sdf.format(trips[i].getFechaViaje()) + "\n";
                    //viajes.add(content);
                    String fechaActual = sdf.format(date);
                    String fechaViaje = sdf.format(trips[i].getFechaViaje());
                    if(compruebaFecha(fechaViaje, fechaActual)){
                        String horaActual = sdfHora.format(date);
                        String respuesta = compruebaHora(trips[i].getHoraSalida(), horaActual);
                        if(respuesta.equals("mayor")){
                            //viajes a realizar
                            viajesARealizar.add(content);
                        }else {
                            //ya realizados
                            viajesRealizados.add(content);
                        }
                    }else{
                        //poner en viajes ya realizados
                        viajesRealizados.add(content);
                        //System.out.println(viajesRealizados.get(0));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            buildRecyclerProximosViajes();
            buildRecyclerViajesRealizados();
        }
    }

    class getZonasTask extends AsyncTask<ArrayList<Zone>, Void, List<Zone>> {
        Socket cliente;
        DataInputStream dataIn;
        DataOutputStream dataOut;

        @Override
        protected List<Zone> doInBackground(ArrayList<Zone>... arrayLists) {
            try {
                cliente = new Socket(connectionClass.getConnection().get(0).getAddress(), connectionClass.getConnection().get(0).getPort());
                dataIn = new DataInputStream(cliente.getInputStream());
                dataOut = new DataOutputStream(cliente.getOutputStream());

                dataOut.writeUTF("zonas");
                dataOut.flush();
                size = dataIn.readInt();
                zonas = new Zone[size];
                for (int i = 0; i < size; i++) {
                    String datos;
                    try {
                        datos = dataIn.readUTF();
                        newDatos = datos.split("-");
                        Zone zona = new Zone(newDatos[0], newDatos[1], newDatos[2]);
                        //System.out.println(zona);
                        zones.add(zona);
                    } catch (IOException ex) {
                        Logger.getLogger(TripFragment.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return zones;
        }

        @Override
        protected void onPostExecute(List<Zone> zones) {
            super.onPostExecute(zones);
            buildRecyclerZonas();
        }
    }
}
