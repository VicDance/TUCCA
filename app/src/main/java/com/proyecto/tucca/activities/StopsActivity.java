package com.proyecto.tucca.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.proyecto.tucca.FareSystemAPI;
import com.proyecto.tucca.R;
import com.proyecto.tucca.model.Horario;
import com.proyecto.tucca.model.HorarioList;
import com.proyecto.tucca.model.Stop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.proyecto.tucca.activities.MainActivity.dataIn;
import static com.proyecto.tucca.activities.MainActivity.dataOut;

public class StopsActivity extends AppCompatActivity  {
    private int nucleoOrigen;
    private int nucleoDestino;
    private int ciudadOrigen;
    private int ciudadDestino;
    private String destino;
    private String origen;
    private Horario[] listaHorarios;
    //String[][] tableRow;
    //String[] tableHeader;
    private List<Stop> stopList = new ArrayList<Stop>();
    TableLayout tableLayout;

    public StopsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stops);
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            nucleoOrigen = 0;
            nucleoDestino = 0;
            ciudadOrigen = 0;
            ciudadDestino = 0;
        } else {
            ciudadOrigen = extras.getInt("ciudadOrigen");
            ciudadDestino = extras.getInt("ciudadDestino");
            nucleoOrigen = extras.getInt("nucleoOrigen");
            nucleoDestino = extras.getInt("nucleoDestino");
            origen = extras.getString("nombreNucleoOrigen");
            destino = extras.getString("nombreNucleoDestino");
            /*System.out.println("Municipio: " + ciudadOrigen + "\n" + "Nucleo: " + nucleoOrigen);
            System.out.println("Municipio: " + ciudadDestino + "\n" + "Nucleo: " + nucleoDestino);*/
        }
        tableLayout = findViewById(R.id.tlGridTable);
        TableRow tableRowOrigenDestino = new TableRow(this);
        TableRow tableRowLineas = new TableRow(this);
        //Creando columnas para origen - destino
        TextView textViewOrigen = new TextView(this);
        TextView textViewDestino = new TextView(this);
        textViewOrigen.setText(origen);
        textViewOrigen.setTextSize(20f);
        textViewDestino.setText(destino);
        textViewDestino.setTextSize(20f);
        textViewDestino.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        //Creando columnas para lineas
        TextView textViewLineas = new TextView(this);
        //Añadiendo columnas
        tableRowOrigenDestino.addView(textViewOrigen);
        tableRowOrigenDestino.addView(textViewDestino);
        //Añadiendo a la tabla
        tableLayout.addView(tableRowOrigenDestino);
        /*for(int i = 0; i < 4; i++){


            tableLayout.addView(tableRow);
        }*/
        //final TableView<String[]> tableView = findViewById(R.id.table_view);
        try {
            dataOut.writeUTF("paradas_viaje");
            dataOut.flush();
            dataOut.writeUTF(ciudadOrigen + "/" + nucleoOrigen + "/" + ciudadDestino + "/" + nucleoDestino);
            dataOut.flush();

            listarPlanificador(nucleoDestino, nucleoOrigen);

            int lineasSize = dataIn.readInt();
            String datos;
            String[] newDatos = new String[0];
            String paradas = "";
            for(int i = 0; i < lineasSize; i++) {
                String linea = dataIn.readUTF();
                //System.out.println("Linea " + linea);
                int paradasSize = dataIn.readInt();
                for(int j = 0; j < paradasSize; j++){
                    datos = dataIn.readUTF();
                    newDatos = datos.split("/");
                    //System.out.println(datos);
                    paradas += newDatos[2] + " ";
                    /*TextView textView = new TextView(this);
                    textView.setText(newDatos[2]);*/
                    //tableRow[i][j] = newDatos[2];
                    //tableRow[i][j] = linea + newDatos[2] + listaHorarios[0].getDias() + listaHorarios[0].getObservaciones();
                    //populateData(newDatos, paradasSize, linea);
                }
                System.out.println("Linea " + linea + " parada " + paradas);
            }

            /*for(int x = 0; x < tableRow.length; x++){
                for(int y = 0; y < tableRow[x].length; y++){
                    System.out.println(tableRow[x][y]);
                }
            }*/
            /*tableView.setDataAdapter(new SimpleTableDataAdapter(this, tableRow));

            tableView.addDataClickListener(new TableDataClickListener() {
                @Override
                public void onDataClicked(int rowIndex, Object clickedData) {
                    Toast.makeText(StopsActivity.this, ((String[])clickedData)[1], Toast.LENGTH_SHORT).show();
                }
            });*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*private void populateData(String[] paradas, int paradasSize, String linea){
        Stop stop;
        for(int i = 0; i < paradasSize; i++){
            stop = new Stop();
            stop.setNombre(paradas[2]);
            stop.setLatitud(paradas[3]);
            stop.setLongitud(paradas[4]);
            stopList.add(stop);
        }
        tableRow = new String[stopList.size()][tableHeader.length];

        for(int x = 0; x < stopList.size(); x++){
            tableRow[x][0] = linea;
            tableRow[x][1] = stopList.get(x).getNombre();
        }
    }*/

    public void listarPlanificador(int idNucleoDestino, int idNucleoOrigen) {
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.ctan.es/v1/Consorcios/2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        FareSystemAPI fareSystemAPI = retrofit.create(FareSystemAPI.class);
        Call<HorarioList> horarioCall = fareSystemAPI.getHorarios(idNucleoDestino, idNucleoOrigen);
        horarioCall.enqueue(new Callback<HorarioList>() {
            @Override
            public void onResponse(Call<HorarioList> call, Response<HorarioList> response) {
                if (!response.isSuccessful()) {
                    System.out.println("Code: " + response.code());
                    return;
                }
                HorarioList horarioList = response.body();
                listaHorarios = new Horario[horarioList.getHorarioList().size()];
                //System.out.println(listaHorarios.length);
                String cadena = "";
                for(int i = 0; i < horarioList.getHorarioList().size(); i++) {
                    listaHorarios[i] = horarioList.getHorarioList().get(i);
                    System.out.println(horarioList.getHorarioList().get(i) + "\n");
                }
            }

            @Override
            public void onFailure(Call<HorarioList> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }
        });
    }
}
