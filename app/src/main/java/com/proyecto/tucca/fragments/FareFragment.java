package com.proyecto.tucca.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.proyecto.tucca.FareSystemAPI;
import com.proyecto.tucca.R;
import com.proyecto.tucca.model.Fare;
import com.proyecto.tucca.model.FareList;
import com.proyecto.tucca.model.Line;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FareFragment extends Fragment {
    private View view;
    private TableLayout tableLayout;
    private LinearLayout linearLayout;
    String[] headers = {"N. Saltos", "Billete Sencillo", "T. Estándar", "T. Estudiante", "T. Jubilado"};
    private Fare[] fares;
    private TableLayout.LayoutParams layoutParams;
    private List<String> lista;

    public static final double DESCUENTO_ESTU = 0.7;
    public static final double DESCUENTO_JU = 0.5;
    public static final double DESCUENTO_ESTANDAR = 0.9;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fare, container, false);
        tableLayout = view.findViewById(R.id.tlGridTable);
        cogeDatosAPI();
        return view;
    }

    private void creaColumnas(String[] columnas) {
        TableRow columna1 = new TableRow(getContext());
        for(int i = 0; i < columnas.length; i++){
            TableRow.LayoutParams lp = new TableRow.LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT);
            columna1.setLayoutParams(lp);
            TextView textViewCol1 = new TextView(getContext());
            textViewCol1.setText("   " + columnas[i] + "   ");
            textViewCol1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            columna1.addView(textViewCol1);
        }
        tableLayout.addView(columna1);
        TableRow tableRow = null;
        DecimalFormat twoDForm = new DecimalFormat("0.00");
        for(int i = 0; i < fares.length; i++){
            tableRow = new TableRow(getContext());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(lp);
            for(int j = 0; j < columnas.length; j++){
                TextView textView = new TextView(getContext());
                switch (j){
                    case 0:
                        textView.setText(fares[i].getSaltos() + "");
                        break;
                    case 1:
                        textView.setText(fares[i].getBs() + "");
                        break;
                    case 2:
                        textView.setText(twoDForm.format(fares[i].getBs() * DESCUENTO_ESTANDAR) + "");
                        break;
                    case 3:
                        textView.setText(twoDForm.format(fares[i].getBs() * DESCUENTO_ESTU) + "");
                        break;
                    case 4:
                        textView.setText(twoDForm.format(fares[i].getBs() * DESCUENTO_JU) + "");
                        break;
                }
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                tableRow.addView(textView);
            }
            tableLayout.addView(tableRow);
        }
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
                lista = new ArrayList<String>();
                for (int i = 0; i < fareList.getFareList().size(); i++) {
                    fares[i] = fareList.getFareList().get(i);
                }
                creaColumnas(headers);
            }

            @Override
            public void onFailure(Call<FareList> call, Throwable t) {

            }
        });
    }
}
