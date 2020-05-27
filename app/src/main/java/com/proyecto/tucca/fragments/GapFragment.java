package com.proyecto.tucca.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.proyecto.tucca.FareSystemAPI;
import com.proyecto.tucca.R;
import com.proyecto.tucca.model.Centre;
import com.proyecto.tucca.model.Fare;
import com.proyecto.tucca.model.FareList;
import com.proyecto.tucca.model.Gap;
import com.proyecto.tucca.model.GapList;
import com.proyecto.tucca.viewmodel.LiveDataCentre;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.proyecto.tucca.activities.MainActivity.dataIn;
import static com.proyecto.tucca.activities.MainActivity.dataOut;

public class GapFragment extends Fragment {
    private View view;
    private Gap[] gaps;
    private Spinner spinnerOrigin;
    private Spinner spinnerDestiny;
    private Centre[] centres;
    private String[] centresNames;
    private ArrayAdapter<String> adapterDestinyCentre;
    private ArrayAdapter<String> adapterOriginCentre;
    private LiveDataCentre liveDataCentre;
    private String zonaOrigen;
    private String zonaDestino;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_gap, container, false);
        spinnerDestiny = view.findViewById(R.id.spinner_gap_destiny_centre);
        spinnerOrigin = view.findViewById(R.id.spinner_gap_origin_centre);
        cogeDatosAPI();
        return view;
    }

    private void cogeDatosAPI() {
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
                    //System.out.println(gaps[i]);
                }
                cogeDatosBbdd();
                setSpinnerOrigin();
                setSpinnerDestiny();
            }

            @Override
            public void onFailure(Call<GapList> call, Throwable t) {

            }
        });
    }

    private void cogeDatosBbdd(){
        try {
            dataOut.writeUTF("nucleos");
            dataOut.flush();
            int size = dataIn.readInt();
            centres = new Centre[size];
            centresNames = new String[size];
            String datos;
            String[] newDatos;
            for(int i = 0; i < size; i++){
                datos = dataIn.readUTF();
                newDatos = datos.split("/");
                Centre centre = new Centre(Integer.parseInt(newDatos[0]), Integer.parseInt(newDatos[1]), newDatos[2], newDatos[3]);
                centres[i] = centre;
                centresNames[i] = newDatos[3];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setSpinnerOrigin(){
        adapterOriginCentre = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, centresNames);
        adapterOriginCentre.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrigin.setAdapter(adapterOriginCentre);
        spinnerOrigin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                liveDataCentre = new ViewModelProvider((ViewModelStoreOwner) getContext()).get(LiveDataCentre.class);
                String centre = (String) parent.getSelectedItem();
                for(int i = 0; i < centres.length; i++){
                    if(centre == centres[i].getNombreNucleo()){
                        zonaOrigen = centres[i].getIdZona();
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

    private void setSpinnerDestiny(){
        adapterDestinyCentre = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, centresNames);
        adapterDestinyCentre.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDestiny.setAdapter(adapterDestinyCentre);
        spinnerDestiny.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                liveDataCentre = new ViewModelProvider((ViewModelStoreOwner) getContext()).get(LiveDataCentre.class);
                String centre = (String) parent.getSelectedItem();
                for(int i = 0; i < centres.length; i++){
                    if(centre == centres[i].getNombreNucleo()){
                        zonaDestino = centres[i].getIdZona();
                        obtieneSaltos();
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

    private void obtieneSaltos(){
        for(int i = 0; i < gaps.length; i++){
            if(zonaDestino.equalsIgnoreCase(gaps[i].getZonaOrigen()) && zonaOrigen.equalsIgnoreCase(gaps[i].getZonaDestino())){
                TextView textViewSaltos = view.findViewById(R.id.text_view_gap);
                textViewSaltos.setText(gaps[i].getSaltos() + "");
            }
        }
    }
}
