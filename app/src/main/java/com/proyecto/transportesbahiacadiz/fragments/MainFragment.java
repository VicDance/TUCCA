package com.proyecto.transportesbahiacadiz.fragments;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
import androidx.annotation.RequiresApi;
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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import serializable.Zona;

import static com.proyecto.transportesbahiacadiz.activities.MainActivity.login;
import static com.proyecto.transportesbahiacadiz.activities.RegisterActivity.usuario;

public class MainFragment extends Fragment {
    private TextView title;
    private int size;
    private Zona[] zonas;
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
    private List<Zona> zones;
    private List<String> viajesARealizar;
    private List<String> viajesRealizados;
    private String content;
    private ConnectionClass connectionClass;

    @RequiresApi(api = Build.VERSION_CODES.M)
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
        if (login) {
            title.setText("Historial de viajes");
            recyclerViewNextTrip.setHasFixedSize(true);
            layoutManagerNextTrip = new LinearLayoutManager(getContext());
            layoutManagerDone = new LinearLayoutManager(getContext());
            recyclerViewNextTrip.setLayoutManager(layoutManagerNextTrip);
            recyclerViewDone.setLayoutManager(layoutManagerDone);
            getViajes();
        } else {
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

    private void getViajes() {
        new getViajesTask().execute();
    }

    private void getZonas() {
        new getZonasTask().execute();
    }

    private void buildRecyclerZonas() {
        zoneAdapter = new ZoneAdapter((ArrayList<Zona>) zones);
        recyclerViewNextTrip.setAdapter(zoneAdapter);
    }

    private void buildRecyclerProximosViajes() {
        tripAdapter = new TripAdapter((ArrayList<String>) viajesARealizar);
        recyclerViewNextTrip.setAdapter(tripAdapter);
    }

    private void buildRecyclerViajesRealizados() {
        tripAdapter = new TripAdapter((ArrayList<String>) viajesRealizados);
        recyclerViewDone.setAdapter(tripAdapter);
    }

    private boolean compruebaFecha(String fechaViaje, String hoy) {
        boolean igual = false;
        String[] fechaViajeSplit = fechaViaje.split("-");
        String[] hoySplit = hoy.split("-");
        if (Integer.parseInt(fechaViajeSplit[0]) == Integer.parseInt(hoySplit[0]) && Integer.parseInt(fechaViajeSplit[1]) == Integer.parseInt(hoySplit[1])
                && Integer.parseInt(fechaViajeSplit[2]) == Integer.parseInt(hoySplit[2])) {
            igual = true;
        }

        return igual;
    }

    private String compruebaHora(String horaViaje, String nuevaHora) {
        String cad = "";
        String[] fechaViajeSplit = horaViaje.split(":");
        String[] hoySplit = nuevaHora.split(":");
        if (Integer.parseInt(fechaViajeSplit[0]) > Integer.parseInt(hoySplit[0])) {
            cad = "mayor";
        } else if (Integer.parseInt(fechaViajeSplit[0]) == Integer.parseInt(hoySplit[0])) {
            if (Integer.parseInt(fechaViajeSplit[1]) > Integer.parseInt(hoySplit[1])) {
                cad = "mayor";
            } else {
                cad = "menor";
            }
        } else {
            cad = "menor";
        }
        return cad;
    }

    private void showDialog() {
        new LocationDialog().show(getFragmentManager(), "Location Dialog");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.location_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_location:
                showDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class getViajesTask extends AsyncTask<Void, Void, Void> {
        Socket cliente;
        ObjectOutputStream outputStream;
        ObjectInputStream inputStream;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                cliente = new Socket(connectionClass.getConnection().get(0).getAddress(), connectionClass.getConnection().get(0).getPort());
                outputStream = new ObjectOutputStream(cliente.getOutputStream());
                inputStream = new ObjectInputStream(cliente.getInputStream());

                outputStream.writeUTF("viajes");
                outputStream.flush();
                outputStream.reset();

                outputStream.writeInt(usuario.getId());
                outputStream.flush();
                outputStream.reset();
                int size = inputStream.readInt();
                trips = new Trip[size];
                for (int i = 0; i < size; i++) {
                    String texto = inputStream.readUTF();
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
                    String fechaActual = sdf.format(date);
                    String fechaViaje = sdf.format(trips[i].getFechaViaje());
                    if (compruebaFecha(fechaViaje, fechaActual)) {
                        String horaActual = sdfHora.format(date);
                        String respuesta = compruebaHora(trips[i].getHoraSalida(), horaActual);
                        if (respuesta.equals("mayor")) {
                            viajesARealizar.add(content);
                        } else {
                            viajesRealizados.add(content);
                        }
                    } else {
                        viajesRealizados.add(content);
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

    class getZonasTask extends AsyncTask<ArrayList<Zona>, Void, List<Zona>> {
        Socket cliente;
        ObjectOutputStream outputStream;
        ObjectInputStream inputStream;

        @Override
        protected List<Zona> doInBackground(ArrayList<Zona>... arrayLists) {
            try {
                cliente = new Socket(connectionClass.getConnection().get(0).getAddress(), connectionClass.getConnection().get(0).getPort());
                outputStream = new ObjectOutputStream(cliente.getOutputStream());
                inputStream = new ObjectInputStream(cliente.getInputStream());

                outputStream.writeUTF("zonas");
                outputStream.flush();
                outputStream.reset();

                size = inputStream.readInt();
                zonas = new Zona[size];
                for (int i = 0; i < size; i++) {
                    try {
                        Zona zona = (Zona) inputStream.readObject();
                        zones.add(zona);
                    } catch (IOException ex) {
                        Logger.getLogger(TripFragment.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return zones;
        }

        @Override
        protected void onPostExecute(List<serializable.Zona> zones) {
            super.onPostExecute(zones);
            buildRecyclerZonas();
        }
    }
}
