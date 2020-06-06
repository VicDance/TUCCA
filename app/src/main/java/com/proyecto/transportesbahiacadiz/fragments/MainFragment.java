package com.proyecto.transportesbahiacadiz.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.proyecto.transportesbahiacadiz.dialogs.LocationDialog;
import com.proyecto.transportesbahiacadiz.R;
import com.proyecto.transportesbahiacadiz.model.Trip;
import com.proyecto.transportesbahiacadiz.model.Zone;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.proyecto.transportesbahiacadiz.activities.MainActivity.dataIn;
import static com.proyecto.transportesbahiacadiz.activities.MainActivity.dataOut;
import static com.proyecto.transportesbahiacadiz.activities.MainActivity.login;
import static com.proyecto.transportesbahiacadiz.activities.RegisterActivity.usuario;

/*import static com.proyecto.tucca.activities.MainActivity.cliente;
import static com.proyecto.tucca.activities.MainActivity.dataIn;
import static com.proyecto.tucca.activities.MainActivity.dataOut;*/

public class MainFragment extends Fragment {
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    private TextView zone;
    private TextView zoneTitle;
    private int size;
    private Zone[] zonas;
    private Trip[] trips;
    private String[] nombreZonas;
    private String[] newDatos;
    private NestedScrollView scrollViewNextTrip;
    private NestedScrollView scrollViewDone;
    private TextView textViewNextTrip;
    private TextView textViewDone;
    private TextView textViewTrips;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        zone = view.findViewById(R.id.text_view_zone);
        scrollViewNextTrip = view.findViewById(R.id.scroll_view_zone);
        scrollViewDone= view.findViewById(R.id.scroll_view_done);
        textViewDone = view.findViewById(R.id.text_view_done);
        textViewNextTrip = view.findViewById(R.id.text_view_next_trip);
        textViewTrips = view.findViewById(R.id.text_view_trip);
        String content;
        if(login){
            zoneTitle = view.findViewById(R.id.text_view_fare_system_title);
            zoneTitle.setText("Historial de viajes");
            try{
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
                content = "";
                java.util.Date d = new java.util.Date();
                java.sql.Date date = new java.sql.Date(d.getTime());
                System.out.println("Cantidad viajes " + trips.length);
                for (int i = 0; i < trips.length; i++) {
                    content += "- Línea: " + trips[i].getLinea() + " Municipio de destino: " + trips[i].getMunicipio() + " Hora de salida: " +
                            trips[i].getHoraSalida() + "\n";
                    String fechaActual = sdf.format(date);
                    String fechaViaje = sdf.format(trips[i].getFechaViaje());
                    if(compruebaFecha(fechaViaje, fechaActual)){
                        String horaActual = sdfHora.format(date);
                        /*System.out.println("fecha viaje: " + fechaViaje);
                        System.out.println("fecha hoy: " + fechaActual);
                        System.out.println("hora viaje: " + trips[i].getHoraSalida());
                        System.out.println("hora hoy: " + horaActual);*/
                        String respuesta = compruebaHora(trips[i].getHoraSalida(), horaActual);
                        //System.out.println("Respuesta " + respuesta);
                        if(respuesta.equals("mayor")){
                            zone.append(content);
                        }else {
                            textViewTrips.append(content);
                        }
                    }else{
                        textViewTrips.append(content);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //param.setMargins(20, 20, 20, 20);
            scrollViewNextTrip.setLayoutParams(param);
            textViewDone.setVisibility(View.INVISIBLE);
            textViewNextTrip.setVisibility(View.INVISIBLE);
            //zone = new TextView(getContext());
            //scrollViewNextTrip.addView(zone);
            try {
                dataOut.writeUTF("zonas");
                dataOut.flush();
                //System.out.println(dataIn.readUTF());
                size = dataIn.readInt();
                //System.out.println(size);
                zonas = new Zone[size];
                for (int i = 0; i < size; i++) {
                    String datos;
                    try {
                        datos = dataIn.readUTF();
                        newDatos = datos.split("/");
                        Zone zona = new Zone(newDatos[0], newDatos[1]);
                        zonas[i] = zona;
                    } catch (IOException ex) {
                        Logger.getLogger(TripFragment.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                content = "";
                for (int i = 0; i < zonas.length; i++) {
                    content += zonas[i].getIdZona() + ": " + zonas[i].getNombreZona() + "\n";
                }
                zone.append(content);
                //scrollViewNextTrip.addView(zone);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return view;
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

    /*private void conectar(){
        final int PUERTO = 6000;
        final String HOST = "192.168.1.13";
        //"localhost";
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            try {
                cliente = new Socket(HOST, PUERTO);
                dataOut = new DataOutputStream(cliente.getOutputStream());
                dataIn = new DataInputStream(cliente.getInputStream());
            } catch (IOException ex) {
                Logger.getLogger(LoginFragment.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }*/

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

    /*class TaskConectar extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            System.out.println("Conectando...");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final int PUERTO = 6000;
            final String HOST = "192.168.1.13";
            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT > 8)
            {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
                try {
                    System.out.println(PUERTO + " " + HOST);
                    cliente = new Socket(HOST, PUERTO);
                    System.out.println(cliente);
                    dataOut = new DataOutputStream(cliente.getOutputStream());
                    dataIn = new DataInputStream(cliente.getInputStream());
                    //System.out.println(dataIn);
                } catch (IOException ex) {
                    Logger.getLogger(LoginFragment.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
            return null;
        }
    }*/
}
