package com.proyecto.transportesbahiacadiz.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.proyecto.transportesbahiacadiz.interfaces.FareSystemAPI;
import com.proyecto.transportesbahiacadiz.activities.StopsActivity;
import com.proyecto.transportesbahiacadiz.model.Centre;
import com.proyecto.transportesbahiacadiz.model.City;
import com.proyecto.transportesbahiacadiz.R;
import com.proyecto.transportesbahiacadiz.model.Fare;
import com.proyecto.transportesbahiacadiz.model.FareList;
import com.proyecto.transportesbahiacadiz.model.Gap;
import com.proyecto.transportesbahiacadiz.model.GapList;
import com.proyecto.transportesbahiacadiz.util.ConnectionClass;
import com.proyecto.transportesbahiacadiz.viewmodel.LiveDataCentre;
import com.proyecto.transportesbahiacadiz.viewmodel.LiveDataCity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import serializable.Municipio;
import serializable.Nucleo;

import static com.proyecto.transportesbahiacadiz.util.Settings.saltos_billete;

public class TripFragment extends Fragment {
    private View view;
    private Spinner spinnerOriginCity;
    private Spinner spinnerDestinyCity;
    private Spinner spinnerOriginCentre;
    private Spinner spinnerDestinyCentre;
    private Municipio[] listaMunicipios;
    private String[] listaNombreMunicipios;
    private Nucleo[] listaNucleos;
    private String[] listaNombreNucleos;
    private ArrayAdapter<String> adapterOriginCities;
    private ArrayAdapter<String> adapterOriginCentre;
    private ArrayAdapter<String> adapterDestinyCities;
    private ArrayAdapter<String> adapterDestinyCentre;
    private Button btnSearch;
    private Button btnPay;
    private TextView textViewLines;
    private TextView textViewPrice;
    private int size;
    private LiveDataCity liveDataCity;
    private LiveDataCentre liveDataCentre;
    int idCiudadOrigen;
    int idCiudadDestino;
    int idNucleoOrigen;
    int idNucleoDestino;
    String nucleoOrigen;
    String nucleoDestino;
    String zonaDestino;
    String zonaOrigen;
    private Fare[] fares;
    private Gap[] gaps;
    private int saltos;
    private double bs;
    private ConnectionClass connectionClass;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_trip, container, false);
        textViewPrice = view.findViewById(R.id.text_view_price);
        connectionClass = new ConnectionClass(getContext());
        listarMunicipios();
        btnSearch = view.findViewById(R.id.btn_search_trip);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), StopsActivity.class)
                        .putExtra("ciudadOrigen", idCiudadOrigen)
                        .putExtra("ciudadDestino", idCiudadDestino)
                        .putExtra("nucleoOrigen", idNucleoOrigen)
                        .putExtra("nucleoDestino", idNucleoDestino)
                        .putExtra("nombreNucleoOrigen", nucleoOrigen)
                        .putExtra("nombreNucleoDestino", nucleoDestino)
                        .putExtra("precio", bs));
            }
        });
        return view;
    }

    private void listarMunicipios() {
        new getMunicipiosTask().execute();
    }

    private void setSpinnerOriginCity() {
        spinnerOriginCity = view.findViewById(R.id.spinner_origin_city);
        adapterOriginCities = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, listaNombreMunicipios);
        adapterOriginCities.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOriginCity.setAdapter(adapterOriginCities);
        spinnerOriginCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                liveDataCity = new ViewModelProvider((ViewModelStoreOwner) getContext()).get(LiveDataCity.class);
                String ciudad = (String) parent.getSelectedItem();
                for (int i = 0; i < listaMunicipios.length; i++) {
                    if (ciudad.equalsIgnoreCase(listaMunicipios[i].getNombreMunicipio())) {
                        idCiudadOrigen = listaMunicipios[i].getIdMunicipio();
                        break;
                    }
                }
                liveDataCity.getCityList().observe((LifecycleOwner) getContext(), new Observer<List<City>>() {
                    @Override
                    public void onChanged(List<City> cities) {
                        for (City city : cities) {
                            cities.add(city);
                        }
                        adapterOriginCentre.notifyDataSetChanged();
                    }
                });

                new getNucleosTask(idCiudadOrigen, 1).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setSpinnerDestinyCity() {
        spinnerDestinyCity = view.findViewById(R.id.spinner_destiny_city);
        adapterDestinyCities = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, listaNombreMunicipios);
        adapterDestinyCities.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDestinyCity.setAdapter(adapterDestinyCities);
        spinnerDestinyCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                liveDataCity = new ViewModelProvider((ViewModelStoreOwner) getContext()).get(LiveDataCity.class);
                String ciudad = (String) parent.getSelectedItem();
                for (int i = 0; i < listaMunicipios.length; i++) {
                    if (ciudad.equalsIgnoreCase(listaMunicipios[i].getNombreMunicipio())) {
                        idCiudadDestino = listaMunicipios[i].getIdMunicipio();
                        break;
                    }
                }
                liveDataCity.getCityList().observe((LifecycleOwner) getContext(), new Observer<List<City>>() {
                    @Override
                    public void onChanged(List<City> cities) {
                        for (City city : cities) {
                            cities.add(city);
                            liveDataCity.addCity(city);
                        }
                        adapterOriginCentre.notifyDataSetChanged();
                    }
                });

                new getNucleosTask(idCiudadDestino, 2).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setSpinnerOriginCentre() {
        spinnerOriginCentre = view.findViewById(R.id.spinner_origin_centre);
        adapterOriginCentre = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listaNombreNucleos);
        adapterOriginCentre.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOriginCentre.setAdapter(adapterOriginCentre);
        spinnerOriginCentre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                liveDataCentre = new ViewModelProvider((ViewModelStoreOwner) getContext()).get(LiveDataCentre.class);
                nucleoOrigen = (String) parent.getSelectedItem();
                for (int i = 0; i < listaNucleos.length; i++) {
                    if (nucleoOrigen.equalsIgnoreCase(listaNucleos[i].getNombreNucleo())) {
                        idNucleoOrigen = listaNucleos[i].getIdNucleo();
                        zonaOrigen = listaNucleos[i].getIdZona();
                        break;
                    }
                }
                liveDataCentre.getCentreList().observe((LifecycleOwner) getContext(), new Observer<List<Centre>>() {
                    @Override
                    public void onChanged(List<Centre> centres) {
                        for (Centre centre : centres) {
                            centres.add(centre);
                            liveDataCentre.addCentre(centre);
                        }
                        adapterOriginCentre.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setSpinnerDestinyCentre() {
        spinnerDestinyCentre = view.findViewById(R.id.spinner_destiny_centre);
        adapterDestinyCentre = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listaNombreNucleos);
        adapterDestinyCentre.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDestinyCentre.setAdapter(adapterDestinyCentre);
        spinnerDestinyCentre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nucleoDestino = (String) parent.getSelectedItem();
                for (int i = 0; i < listaNucleos.length; i++) {
                    if (nucleoDestino.equalsIgnoreCase(listaNucleos[i].getNombreNucleo())) {
                        idNucleoDestino = listaNucleos[i].getIdNucleo();
                        zonaDestino = listaNucleos[i].getIdZona();
                        break;
                    }
                }
                liveDataCentre = new ViewModelProvider((ViewModelStoreOwner) getContext()).get(LiveDataCentre.class);
                liveDataCentre.getCentreList().observe((LifecycleOwner) getContext(), new Observer<List<Centre>>() {
                    @Override
                    public void onChanged(List<Centre> centres) {
                        for (Centre centre : centres) {
                            centres.add(centre);
                            liveDataCentre.addCentre(centre);
                        }
                        adapterOriginCentre.notifyDataSetChanged();
                    }
                });

                if (saltos_billete.isEmpty()) {
                    cogeDatosAPI();
                    cogeSaltosAPI();
                } else {
                    cogeSaltosAPI();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void cogeDatosAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.ctan.es/v1/Consorcios/2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        FareSystemAPI fareSystemAPI = retrofit.create(FareSystemAPI.class);
        Call<FareList> fareListCall = fareSystemAPI.getFareList();
        fareListCall.enqueue(new Callback<FareList>() {
            @Override
            public void onResponse(Call<FareList> call, Response<FareList> response) {
                if (!response.isSuccessful()) {
                    System.out.println("Code: " + response.code());
                    return;
                }
                FareList fareList = response.body();
                fares = new Fare[fareList.getFareList().size()];
                for (int i = 0; i < fareList.getFareList().size(); i++) {
                    fares[i] = fareList.getFareList().get(i);
                }
                for (int i = 0; i < fares.length; i++) {
                    saltos_billete.put(fares[i].getSaltos(), fares[i].getBs());
                }
            }

            @Override
            public void onFailure(Call<FareList> call, Throwable t) {

            }
        });
    }

    private void cogeSaltosAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.ctan.es/v1/Consorcios/2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        FareSystemAPI fareSystemAPI = retrofit.create(FareSystemAPI.class);
        Call<GapList> gapListCall = fareSystemAPI.getGapList();
        gapListCall.enqueue(new Callback<GapList>() {
            @Override
            public void onResponse(Call<GapList> call, Response<GapList> response) {
                if (!response.isSuccessful()) {
                    System.out.println("Code: " + response.code());
                    return;
                }
                GapList gapList = response.body();
                gaps = new Gap[gapList.getGapList().size()];
                for (int i = 0; i < gapList.getGapList().size(); i++) {
                    gaps[i] = gapList.getGapList().get(i);
                }
                obtieneSaltos();
                cogePrecioBillete(saltos);
            }

            @Override
            public void onFailure(Call<GapList> call, Throwable t) {

            }
        });
    }

    private void cogePrecioBillete(int salto) {
        for (Map.Entry<Integer, Double> entry : saltos_billete.entrySet()) {
            if (entry.getKey() == salto) {
                bs = entry.getValue();
                System.out.println("Bs " + bs);
                textViewPrice.setText(getString(R.string.price) + " " + bs + "€");
            }
        }
    }

    private void obtieneSaltos() {
        for (int i = 0; i < gaps.length; i++) {
            if (zonaDestino.equalsIgnoreCase(gaps[i].getZonaOrigen()) && zonaOrigen.equalsIgnoreCase(gaps[i].getZonaDestino())) {
                saltos = gaps[i].getSaltos();
                System.out.println("Saltos: " + saltos);
            }
        }
    }

    class getMunicipiosTask extends AsyncTask<Void, Void, Void> {
        Socket cliente;
        ObjectOutputStream outputStream;
        ObjectInputStream inputStream;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                cliente = new Socket(connectionClass.getConnection().get(0).getAddress(), connectionClass.getConnection().get(0).getPort());
                outputStream = new ObjectOutputStream(cliente.getOutputStream());
                inputStream = new ObjectInputStream(cliente.getInputStream());

                outputStream.writeUTF("municipios");
                outputStream.flush();
                size = inputStream.readInt();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT > 8) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
                listaMunicipios = new Municipio[size];
                listaNombreMunicipios = new String[size];
                for (int i = 0; i < size; i++) {
                    try {
                        Municipio municipio = (Municipio) inputStream.readObject();
                        listaMunicipios[i] = municipio;
                        listaNombreMunicipios[i] = municipio.getNombreMunicipio();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                setSpinnerOriginCity();
                setSpinnerDestinyCity();
            }
        }
    }

    class getNucleosTask extends AsyncTask<Void, Void, Void> {
        Socket cliente;
        ObjectOutputStream outputStream;
        ObjectInputStream inputStream;
        private int idMun;
        private int nucleo;

        public getNucleosTask(int idMunicipio, int nucleo){
            this.idMun = idMunicipio;
            this.nucleo = nucleo;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                cliente = new Socket(connectionClass.getConnection().get(0).getAddress(), connectionClass.getConnection().get(0).getPort());
                outputStream = new ObjectOutputStream(cliente.getOutputStream());
                inputStream = new ObjectInputStream(cliente.getInputStream());

                outputStream.writeUTF("nucleos");
                outputStream.flush();

                size = inputStream.readInt();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT > 8) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
                listaNucleos = new Nucleo[size];
                for (int i = 0; i < size; i++) {
                    try {
                        Nucleo nucleo = (Nucleo) inputStream.readObject();
                        listaNucleos[i] = nucleo;
                    } catch (IOException | ClassNotFoundException ex) {
                        Logger.getLogger(TripFragment.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                int contNucleos = 0;
                for (int j = 0; j < listaNucleos.length; j++) {
                    if (idMun == listaNucleos[j].getIdMunicipio()) {
                        contNucleos++;
                    }
                }
                listaNombreNucleos = new String[contNucleos];
                int ind = 0;
                for (int j = 0; j < listaNucleos.length; j++) {
                    if (idMun == listaNucleos[j].getIdMunicipio()) {
                        listaNombreNucleos[ind] = listaNucleos[j].getNombreNucleo();
                        ind++;
                    }
                }

                if(nucleo == 1){
                    setSpinnerOriginCentre();
                }else{
                    setSpinnerDestinyCentre();
                }
            }
        }
    }
}
