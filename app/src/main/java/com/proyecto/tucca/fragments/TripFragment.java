package com.proyecto.tucca.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.Toast;

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

import com.proyecto.tucca.activities.StopsActivity;
import com.proyecto.tucca.model.Centre;
import com.proyecto.tucca.model.City;
import com.proyecto.tucca.FareSystemAPI;
import com.proyecto.tucca.R;
import com.proyecto.tucca.model.Horario;
import com.proyecto.tucca.viewmodel.LiveDataCentre;
import com.proyecto.tucca.viewmodel.LiveDataCity;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.proyecto.tucca.activities.MainActivity.dataIn;
import static com.proyecto.tucca.activities.MainActivity.dataOut;

public class TripFragment extends Fragment {
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    private RecyclerView.LayoutManager layoutManager;
    private View view;
    private Spinner spinnerOriginCity;
    private Spinner spinnerDestinyCity;
    private Spinner spinnerOriginCentre;
    private Spinner spinnerDestinyCentre;
    private City[] listaMunicipios;
    private String[] listaNombreMunicipios;
    private Centre[] listaNucleos;
    private String[] listaNombreNucleos;
    private String[] listaHorarios;
    private ArrayAdapter<String> adapterOriginCities;
    private ArrayAdapter<String> adapterOriginCentre;
    private ArrayAdapter<String> adapterDestinyCities;
    private ArrayAdapter<String> adapterDestinyCentre;
    private Button btnSearch;
    private Button btnPay;
    private TextView textViewLines;
    private int size;
    private String[] newDatos;
    private LiveDataCity liveDataCity;
    private LiveDataCentre liveDataCentre;
    int idCiudadOrigen;
    int idCiudadDestino;
    int idNucleoOrigen;
    int idNucleoDestino;
    String nucleoOrigen;
    String nucleoDestino;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_trip, container, false);
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
                        .putExtra("nombreNucleoDestino", nucleoDestino));
            }
        });
        //btnPay = view.findViewById(R.id.btn_pay_trip);
        /*btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Pagar viaje", Toast.LENGTH_SHORT).show();
            }
        });*/
        return view;
    }

    private void listarMunicipios() {
        try {
            dataOut.writeUTF("municipios");
            dataOut.flush();
            size = dataIn.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
        listaMunicipios = new City[size];
        listaNombreMunicipios = new String[size];
        for (int i = 0; i < size; i++) {
            String datos;
            try {
                datos = dataIn.readUTF();
                newDatos = datos.split("/");
                City city = new City(Integer.parseInt(newDatos[0]), newDatos[1]);
                listaMunicipios[i] = city;
                listaNombreMunicipios[i] = city.getNombreMunicipio();
            } catch (IOException ex) {
                Logger.getLogger(TripFragment.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        setSpinnerOriginCity();
        setSpinnerDestinyCity();
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
                //liveDataCity.getCityList();
                //int idMunicipio = 0;
                String ciudad = (String) parent.getSelectedItem();
                for (int i = 0; i < listaMunicipios.length; i++) {
                    if (ciudad.equalsIgnoreCase(listaMunicipios[i].getNombreMunicipio())) {
                        idCiudadOrigen = listaMunicipios[i].getIdMunicipio();
                        //liveDataCity.addCity(new City(idMunicipio, ciudad));
                        //city.setIdMunicipio(idMunicipio);
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

                listarNucleos(idCiudadOrigen);
                setSpinnerOriginCentre();
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
                //int idMunicipio = 0;
                String ciudad = (String) parent.getSelectedItem();
                for (int i = 0; i < listaMunicipios.length; i++) {
                    if (ciudad.equalsIgnoreCase(listaMunicipios[i].getNombreMunicipio())) {
                        idCiudadDestino = listaMunicipios[i].getIdMunicipio();
                        //liveDataCity.addCity(new City(idMunicipio, ciudad));
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
                listarNucleos(idCiudadDestino);
                setSpinnerDestinyCentre();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void displayCityInfo(City city) {
        long id = city.getIdMunicipio();
        String name = city.getNombreMunicipio();

        String data = "Id: " + id + ". Nombre: " + name;
        Toast.makeText(getContext(), data, Toast.LENGTH_SHORT).show();
    }

    private void listarNucleos(int idMunicipio) {
        int contNucleos = 0;
        try {
            dataOut.writeUTF("nucleos");
            dataOut.flush();
            size = dataIn.readInt();
            listaNucleos = new Centre[size];
            for (int i = 0; i < size; i++) {
                try {
                    String datos = dataIn.readUTF();
                    newDatos = datos.split("/");
                    Centre centre = new Centre(Integer.parseInt(newDatos[0]), Integer.parseInt(newDatos[1]), newDatos[2], newDatos[3]);
                    listaNucleos[i] = centre;
                } catch (IOException ex) {
                    Logger.getLogger(TripFragment.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            for (int j = 0; j < listaNucleos.length; j++) {
                if (idMunicipio == listaNucleos[j].getIdMunicipio()) {
                    contNucleos++;
                }
            }
            listaNombreNucleos = new String[contNucleos];
            int ind = 0;
            for (int j = 0; j < listaNucleos.length; j++) {
                if (idMunicipio == listaNucleos[j].getIdMunicipio()) {
                    listaNombreNucleos[ind] = listaNucleos[j].getNombreNucleo();
                    ind++;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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
                //int idNucleo = 0;
                nucleoOrigen = (String) parent.getSelectedItem();
                for (int i = 0; i < listaNucleos.length; i++) {
                    if (nucleoOrigen.equalsIgnoreCase(listaNucleos[i].getNombreNucleo())) {
                        idNucleoOrigen = listaNucleos[i].getIdNucleo();
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
        //liveDataCentre.getCentreList();
        /*liveDataCentre.getCentreList().observe((LifecycleOwner) getContext(), new Observer<List<Centre>>() {
            @Override
            public void onChanged(List<Centre> centres) {
                for (Centre centre : centres) {
                    //centres.add(centre);
                    liveDataCentre.addCentre(centre);
                }
                adapterOriginCentre.notifyDataSetChanged();
            }
        });*/
        /*spinnerOriginCentre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                liveDataCity = new ViewModelProvider((ViewModelStoreOwner) getContext()).get(LiveDataCity.class);
                liveDataCity.getCityList();
                liveDataCentre = new ViewModelProvider((ViewModelStoreOwner) getContext()).get(LiveDataCentre.class);
                liveDataCentre.getCentreList();
                String nucleo = (String) parent.getSelectedItem();
                int idNucleo = 0;
                int idMunicipio = 0;
                char idZona;
                for (int i = 0; i < listaNucleos.length; i++) {
                    if (nucleo.equalsIgnoreCase(listaNucleos[i].getNombreNucleo())) {
                        System.out.println("entra");
                        idNucleo = listaNucleos[i].getIdNucleo();
                        idMunicipio = listaNucleos[i].getIdMunicipio();
                        //idZona = listaNucleos[i].getIdZona();
                        liveDataCentre.addCentre(new Centre(idNucleo));
                        liveDataCity.addCity(new City(idMunicipio));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
    }

    /*final Observer<List<Centre>> listObserverOriginCentre = new Observer<List<Centre>>() {
        @Override
        public void onChanged(@Nullable List<Centre> userList) {
            String texto = "";
            for(int i=0; i<userList.size(); i++){
                //texto += userList.get(i).getNombre() + " " + userList.get(i).getEdad() +"\n";
            }
            //tvLiveData.setText(texto);
        }
    };

        liveDataUserViewModel.getUserList().observe(this, listObserver);*/

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
                //listarPlanificador(idNucleoDestino, idNucleoOrigen);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
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
        switch (item.getItemId()) {
            case R.id.action_search:
                // Not implemented here
                return false;
            default:
                break;
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }
}
