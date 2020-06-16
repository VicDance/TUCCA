package com.proyecto.transportesbahiacadiz.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.proyecto.transportesbahiacadiz.R;
import com.proyecto.transportesbahiacadiz.activities.MapInterestingPlacesActivity;
import com.proyecto.transportesbahiacadiz.adapters.PlacesAdapter;
import com.proyecto.transportesbahiacadiz.model.Places;
import com.proyecto.transportesbahiacadiz.util.ConnectionClass;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class PlacesFragment extends Fragment implements PlacesAdapter.OnItemClickListener {
    private View view;
    private RecyclerView recyclerView;
    private PlacesAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Places> placesArrayList;
    private ArrayList<String> cityNames;
    private ArrayList<Places> extras;
    private ConnectionClass connectionClass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_places, container, false);
        connectionClass = new ConnectionClass(getContext());
        placesArrayList = new ArrayList<Places>();
        cityNames = new ArrayList<>();
        new getLugaresInteresTask().execute();
        return view;
    }

    private void buildRecycler() {
        recyclerView = view.findViewById(R.id.recycler_view_places);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PlacesAdapter(cityNames, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {
        extras = new ArrayList<>();
        Places places;
        switch (cityNames.get(position)) {
            case "Cádiz":
                for (int j = 0; j < placesArrayList.size(); j++) {
                    if (placesArrayList.get(j).getIdMunicipio() == 1) {
                        //System.out.println(placesArrayList.get(j));
                        places = new Places(placesArrayList.get(j).getLatitud(), placesArrayList.get(j).getLongitud(), placesArrayList.get(j).getNombre());
                        extras.add(places);
                    }
                }
                break;
            case "San Fernando":
                for (int j = 0; j < placesArrayList.size(); j++) {
                    if (placesArrayList.get(j).getIdMunicipio() == 2) {
                        //System.out.println(placesArrayList.get(j));
                        places = new Places(placesArrayList.get(j).getLatitud(), placesArrayList.get(j).getLongitud(), placesArrayList.get(j).getNombre());
                        extras.add(places);
                    }
                }
                break;
            case "Chiclana":
                for (int j = 0; j < placesArrayList.size(); j++) {
                    if (placesArrayList.get(j).getIdMunicipio() == 3) {
                        //System.out.println(placesArrayList.get(j));
                        places = new Places(placesArrayList.get(j).getLatitud(), placesArrayList.get(j).getLongitud(), placesArrayList.get(j).getNombre());
                        extras.add(places);
                    }
                }
                break;
            case "Puerto Real":
                for (int j = 0; j < placesArrayList.size(); j++) {
                    if (placesArrayList.get(j).getIdMunicipio() == 4) {
                        //System.out.println(placesArrayList.get(j));
                        places = new Places(placesArrayList.get(j).getLatitud(), placesArrayList.get(j).getLongitud(), placesArrayList.get(j).getNombre());
                        extras.add(places);
                    }
                }
                break;
            case "El Puerto de Santa María":
                for (int j = 0; j < placesArrayList.size(); j++) {
                    if (placesArrayList.get(j).getIdMunicipio() == 5) {
                        //System.out.println(placesArrayList.get(j));
                        places = new Places(placesArrayList.get(j).getLatitud(), placesArrayList.get(j).getLongitud(), placesArrayList.get(j).getNombre());
                        extras.add(places);
                    }
                }
                break;
            case "Jerez de la Frontera":
                for (int j = 0; j < placesArrayList.size(); j++) {
                    if (placesArrayList.get(j).getIdMunicipio() == 6) {
                        //System.out.println(placesArrayList.get(j));
                        places = new Places(placesArrayList.get(j).getLatitud(), placesArrayList.get(j).getLongitud(), placesArrayList.get(j).getNombre());
                        extras.add(places);
                    }
                }
                break;
            case "Rota":
                for (int j = 0; j < placesArrayList.size(); j++) {
                    if (placesArrayList.get(j).getIdMunicipio() == 7) {
                        //System.out.println(placesArrayList.get(j));
                        places = new Places(placesArrayList.get(j).getLatitud(), placesArrayList.get(j).getLongitud(), placesArrayList.get(j).getNombre());
                        extras.add(places);
                    }
                }
                break;
        }
        cambiaActivity(extras);
    }

    private void getIdsMunicipios() {
        int cont = 1;
        for (int i = 0; i < placesArrayList.size(); i++) {
            if (placesArrayList.get(i).getIdMunicipio() > cont) {
                cont++;
            }
        }

        for (int j = 0; j < cont; j++) {
            switch (j + 1) {
                case 1:
                    //places = new Places("Cádiz", placesArrayList.get(0).getLatitud(), placesArrayList.get(0).getLongitud());
                    cityNames.add("Cádiz");
                    break;
                case 2:
                    //places = new Places("San Fernando", placesArrayList.get(0).getLatitud(), placesArrayList.get(0).getLongitud());
                    cityNames.add("San Fernando");
                    break;
                case 3:
                    //places = new Places("San Fernando", placesArrayList.get(0).getLatitud(), placesArrayList.get(0).getLongitud());
                    cityNames.add("Chiclana");
                    break;
                case 4:
                    cityNames.add("Puerto Real");
                    break;
                case 5:
                    cityNames.add("El Puerto de Santa María");
                    break;
                case 6:
                    cityNames.add("Jerez de la Frontera");
                    break;
                case 7:
                    cityNames.add("Rota");
                    break;
            }
        }
    }

    private void cambiaActivity(ArrayList<Places> extras) {
        Intent intent = new Intent(getContext(), MapInterestingPlacesActivity.class);
        intent.putExtra("extra", extras);
        startActivity(intent);
    }

    class getLugaresInteresTask extends AsyncTask<Void, Void, Void> {
        Socket cliente;
        DataInputStream dataIn;
        DataOutputStream dataOut;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                cliente = new Socket(connectionClass.getConnection().get(0).getAddress(), connectionClass.getConnection().get(0).getPort());
                dataIn = new DataInputStream(cliente.getInputStream());
                dataOut = new DataOutputStream(cliente.getOutputStream());

                dataOut.writeUTF("lugares_interes");
                dataOut.flush();
                int size = dataIn.readInt();
                String texto;
                String[] datos;
                for (int i = 0; i < size; i++) {
                    texto = dataIn.readUTF();
                    datos = texto.split("/");
                    Places places = new Places(Integer.parseInt(datos[0]), datos[1], datos[2], datos[3]);
                    placesArrayList.add(places);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            getIdsMunicipios();
            buildRecycler();
        }
    }
}
