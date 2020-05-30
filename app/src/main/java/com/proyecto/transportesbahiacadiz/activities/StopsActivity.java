package com.proyecto.transportesbahiacadiz.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Session2CommandGroup;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.MonthDisplayHelper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.proyecto.transportesbahiacadiz.FareSystemAPI;
import com.proyecto.transportesbahiacadiz.R;
import com.proyecto.transportesbahiacadiz.fragments.CardsFragment;
import com.proyecto.transportesbahiacadiz.model.Horario;
import com.proyecto.transportesbahiacadiz.model.HorarioList;
import com.proyecto.transportesbahiacadiz.model.Segment;
import com.proyecto.transportesbahiacadiz.model.SegmentList;
import com.proyecto.transportesbahiacadiz.model.Stop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.proyecto.transportesbahiacadiz.activities.MainActivity.dataIn;
import static com.proyecto.transportesbahiacadiz.activities.MainActivity.dataOut;
import static com.proyecto.transportesbahiacadiz.activities.MainActivity.login;

public class StopsActivity extends AppCompatActivity {
    private int nucleoOrigen;
    private int nucleoDestino;
    private int ciudadOrigen;
    private int ciudadDestino;
    private double bs;
    private String destino;
    private String origen;
    private Horario[] listaHorarios;
    private Segment[] listaSegments;
    //String[][] tableRow;
    String[] tableHeader;
    private List<Stop> stopList = new ArrayList<Stop>();
    TableLayout tableLayout;
    Stop[] paradas;
    int length;
    String nombreLinea = "";

    private Button btnPay;

    public StopsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stops);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
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
            bs = extras.getDouble("precio");
            /*System.out.println("Municipio: " + ciudadOrigen + "\n" + "Nucleo: " + nucleoOrigen);
            System.out.println("Municipio: " + ciudadDestino + "\n" + "Nucleo: " + nucleoDestino);*/
        }
        tableLayout = findViewById(R.id.tlGridTable);
        btnPay = findViewById(R.id.pay);
        if(!login){
            btnPay.setVisibility(View.INVISIBLE);
        }
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(StopsActivity.this, "Click", Toast.LENGTH_SHORT).show();
                /*if (saldo <= 0) {
                    new AlertDialog.Builder(StopsActivity.this)
                            .setTitle(R.string.attention)
                            .setMessage(R.string.no_money)
                            .show();
                } else {
                    new AlertDialog.Builder(StopsActivity.this)
                            .setTitle(R.string.attention)
                            .setMessage(R.string.payment_instruction)
                            .show();
                    compruebaPosicion();
                }*/
                //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CardsFragment()).commit();
                startActivity(new Intent(StopsActivity.this, MenuActivity.class).putExtra("pagar", bs));
            }
        });

        try {
            dataOut.writeUTF("paradas_viaje");
            dataOut.flush();
            dataOut.writeUTF(ciudadOrigen + "/" + nucleoOrigen + "/" + ciudadDestino + "/" + nucleoDestino);
            dataOut.flush();

            int lineasSize = dataIn.readInt();
            String datos;
            String[] newDatos;
            for (int i = 0; i < lineasSize; i++) {
                String linea = dataIn.readUTF();
                nombreLinea += linea + "/";
                //System.out.println("Linea " + linea);
                int paradasSize = dataIn.readInt();
                //paradas = new Stop[paradasSize];
                for (int j = 0; j < paradasSize; j++) {
                    datos = dataIn.readUTF();
                    newDatos = datos.split("/");
                    //System.out.println(datos);
                    Stop stop = new Stop(Integer.parseInt(newDatos[0]), newDatos[1], newDatos[2], newDatos[3], newDatos[4]);
                    /*paradas[i].setIdParada(Integer.parseInt(newDatos[0]));
                    paradas[i].setIdZona(newDatos[1]);
                    paradas[i].setNombre(newDatos[2]);
                    paradas[i].setLatitud(newDatos[3]);
                    paradas[i].setLongitud(newDatos[4]);*/
                    stopList.add(stop);
                }
                //System.out.println("Linea " + linea + " parada " + paradas[0]);
            }

            listarBloques(nucleoDestino, nucleoOrigen);
            listarHorarios(nucleoDestino, nucleoOrigen);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void creaCabecera() {
        TableRow cabecera = new TableRow(this);
        length = tableHeader.length;
        for (int i = 0; i < tableHeader.length; i++) {
            TableRow.LayoutParams lp = new TableRow.LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = 20;
            cabecera.setLayoutParams(lp);
            TextView textView = new TextView(this);
            textView.setText("    " + tableHeader[i] + "    ");
            textView.setTextSize(20f);
            textView.setTextAppearance(R.style.Widget_MaterialComponents_TabLayout);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setBackgroundColor(Color.rgb(95, 173, 250));
            cabecera.addView(textView);
        }
        tableLayout.addView(cabecera);

        TableRow separador_cabecera = new TableRow(this);
        separador_cabecera.setLayoutParams(new TableLayout.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        FrameLayout linea_cabecera = new FrameLayout(this);
        TableRow.LayoutParams linea_cabecera_params =
                new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2);
        linea_cabecera_params.span = 10;
        linea_cabecera.setBackgroundColor(Color.rgb(37, 121, 204));
        separador_cabecera.addView(linea_cabecera, linea_cabecera_params);
        tableLayout.addView(separador_cabecera);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void creaTabla() {
        TableRow tableRow = null;
        TableRow separador_cabecera = null;
        String[] lineas = nombreLinea.split("/");
        int cont = 0;
        for (int i = 0; i < listaHorarios.length; i++) {
            tableRow = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(100, 70);
            lp.bottomMargin = 20;
            tableRow.setLayoutParams(lp);
            for (int x = 0; x < length; x++) {
                TextView textView = new TextView(this);
                for(int z = 0; z < lineas.length; z++) {
                    if (listaHorarios[i].getNameLinea().equalsIgnoreCase(lineas[z])) {
                        System.out.println(lineas[z]);
                        if (x == 0) {
                            textView.setText(listaHorarios[i].getNameLinea());
                        } else if (tableHeader[tableHeader.length-1].equalsIgnoreCase("observaciones")) {
                            textView.setText(listaHorarios[i].getObservaciones());
                            if (x > 0 && x < (tableHeader.length - 2)) {
                                textView.setText(listaHorarios[i].getHoras().get(cont));
                                cont++;
                                if (cont == tableHeader.length - 3) {
                                    cont = 0;
                                }
                            }
                        } else if (tableHeader[tableHeader.length-1].equalsIgnoreCase("frecuencia")) {
                            textView.setText(listaHorarios[i].getDias());
                            //System.out.println("entra frecuencia");
                            if (x > 0 && x < (tableHeader.length - 1)) {
                                textView.setText(listaHorarios[i].getHoras().get(cont));
                                cont++;
                                if (cont == tableHeader.length - 2) {
                                    cont = 0;
                                }
                            }
                        }
                        textView.setTextAppearance(R.style.Widget_MaterialComponents_TabLayout);
                        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        tableRow.addView(textView);

                        separador_cabecera = new TableRow(this);
                        separador_cabecera.setLayoutParams(new TableLayout.LayoutParams(
                                TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                        FrameLayout linea_cabecera = new FrameLayout(this);
                        TableRow.LayoutParams linea_cabecera_params =
                                new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2);
                        linea_cabecera_params.span = 10;
                        linea_cabecera.setBackgroundColor(Color.rgb(37, 121, 204));
                        separador_cabecera.addView(linea_cabecera, linea_cabecera_params);
                        tableLayout.addView(separador_cabecera);
                    }
                }
            }
            tableLayout.addView(tableRow);
        }
    }

    public void listarHorarios(int idNucleoDestino, int idNucleoOrigen) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
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
            @RequiresApi(api = Build.VERSION_CODES.M)
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
                for (int i = 0; i < horarioList.getHorarioList().size(); i++) {
                    listaHorarios[i] = horarioList.getHorarioList().get(i);
                    //System.out.println(horarioList.getHorarioList().get(i) + "\n");
                }
                if(nombreLinea == null || nombreLinea.equalsIgnoreCase("")){
                    for(int i = 0; i < listaHorarios.length; i++) {
                        nombreLinea += listaHorarios[i].getNameLinea();
                        //System.out.println(nombreLinea);
                    }
                }
                creaTabla();
            }

            @Override
            public void onFailure(Call<HorarioList> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }
        });
    }

    private void listarBloques(int idNucleoDestino, int idNucleoOrigen) {
        System.out.println("Destino " + idNucleoDestino);
        System.out.println("Origen " + idNucleoOrigen);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.ctan.es/v1/Consorcios/2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        FareSystemAPI fareSystemAPI = retrofit.create(FareSystemAPI.class);
        Call<SegmentList> segmentListCall = fareSystemAPI.getBloques(idNucleoDestino, idNucleoOrigen);
        segmentListCall.enqueue(new Callback<SegmentList>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(Call<SegmentList> call, Response<SegmentList> response) {
                if (!response.isSuccessful()) {
                    System.out.println("Code: " + response.code());
                    System.out.println(response.message());
                    return;
                }
                TextView textViewNoTrip = findViewById(R.id.text_view_no_trip);
                textViewNoTrip.setVisibility(View.INVISIBLE);
                SegmentList segmentList = response.body();
                listaSegments = new Segment[segmentList.getSegmentList().size()];
                //System.out.println(listaHorarios.length);
                String cadena = "";
                tableHeader = new String[segmentList.getSegmentList().size()];
                for (int i = 0; i < segmentList.getSegmentList().size(); i++) {
                    listaSegments[i] = segmentList.getSegmentList().get(i);
                    //System.out.println(segmentList.getSegmentList().get(i) + "\n");
                }
                //System.out.println(paradas.length);
                for (int i = 0; i < listaSegments.length; i++) {
                    System.out.println(listaSegments[i]);
                    tableHeader[i] = listaSegments[i].getNombre();
                }
                creaCabecera();
            }

            @Override
            public void onFailure(Call<SegmentList> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }
        });
    }
}
