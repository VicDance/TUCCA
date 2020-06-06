package com.proyecto.transportesbahiacadiz.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.proyecto.transportesbahiacadiz.FareSystemAPI;
import com.proyecto.transportesbahiacadiz.R;
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

import static android.view.Gravity.CENTER_VERTICAL;
import static com.proyecto.transportesbahiacadiz.activities.MainActivity.dataIn;
import static com.proyecto.transportesbahiacadiz.activities.MainActivity.dataOut;
import static com.proyecto.transportesbahiacadiz.activities.MainActivity.login;
import static com.proyecto.transportesbahiacadiz.activities.RegisterActivity.usuario;

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
    private String[] tableHeader;
    private List<Stop> stopList = new ArrayList<Stop>();
    private TableLayout tableLayout;
    private Stop[] paradas;
    private int length;
    private String nombreLinea = "";
    private int idLinea = 0;
    private String horaSalida;
    private String horaLlegada;

    private Button btnPay;
    private SwipeRefreshLayout swipeRefreshLayout;

    public StopsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stops);
        swipeRefreshLayout = findViewById(R.id.stops_refresh);
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
            //System.out.println(ciudadDestino);
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
                if(idLinea == 0){
                    Toast.makeText(StopsActivity.this, "Debe seleccionar una línea para pagar el viaje", Toast.LENGTH_SHORT).show();
                }else {
                    try {
                        dataOut.writeUTF("iviaje");
                        dataOut.flush();
                        dataOut.writeUTF(usuario.getId() + "/" + idLinea + "/" + ciudadDestino + "/" + bs + "/" + horaSalida + "/" + horaLlegada);
                        dataOut.flush();
                        String estado = dataIn.readUTF();
                        //System.out.println("Estado " + estado);
                        //Trip trip = new Trip(usuario.getId(), idLinea, ciudadOrigen, bs, timeSalida, timeLlegada, new Date());
                        startActivity(new Intent(StopsActivity.this, MenuActivity.class)
                                .putExtra("pagar", bs)
                                .putExtra("salida", horaSalida)
                                .putExtra("destino", ciudadDestino));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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
                    stopList.add(stop);
                }
                //System.out.println("Linea " + linea + " parada " + paradas[0]);
            }

            listarBloques(nucleoDestino, nucleoOrigen);
            listarHorarios(nucleoDestino, nucleoOrigen);
        } catch (IOException e) {
            e.printStackTrace();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    listarBloques(nucleoDestino, nucleoOrigen);
                    listarHorarios(nucleoDestino, nucleoOrigen);
                    Thread.sleep(1000);
                    swipeRefreshLayout.setRefreshing(false);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void creaCabecera() {
        TableRow cabecera = new TableRow(this);
        length = tableHeader.length;
        for (int i = 0; i < tableHeader.length; i++) {
            TableRow.LayoutParams lp = new TableRow.LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = 20;
            cabecera.setLayoutParams(lp);
            final TextView textView = new TextView(this);
            textView.setText("    " + tableHeader[i] + "    ");
            textView.setTextSize(20f);
            textView.setTextAppearance(R.style.Widget_MaterialComponents_TabLayout);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setBackgroundColor(Color.rgb(95, 173, 250));
            //for(int x = 0; x < tableHeader.length; x++){
                if(tableHeader[tableHeader.length -1].equalsIgnoreCase("observaciones")){
                    //for(int x = 1; x < tableHeader.length -1; x++){
                    if(i > 0 && i < tableHeader.length -2) {
                        final int finalI = i;
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //System.out.println("click");
                                try {
                                    dataOut.writeUTF("direccion_parada");
                                    dataOut.flush();
                                    dataOut.writeUTF(tableHeader[finalI].trim());
                                    dataOut.flush();
                                    System.out.println("parada " + tableHeader[finalI].trim());
                                    startActivity(new Intent(StopsActivity.this, MapStopsActivity.class));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
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
            TableRow.LayoutParams lp = new TableRow.LayoutParams(100, 100);
            lp.bottomMargin = 20;
            tableRow.setLayoutParams(lp);
            for (int x = 0; x < length; x++) {
                final TextView textView = new TextView(this);
                textView.setHeight(100);
                for(int z = 0; z < lineas.length; z++) {
                    if (listaHorarios[i].getNameLinea().equalsIgnoreCase(lineas[z])) {
                        System.out.println(lineas[z]);
                        if (x == 0) {
                            textView.setText(listaHorarios[i].getNameLinea());
                        } else if (/*x == tableHeader.length-1 && */tableHeader[tableHeader.length-1].equalsIgnoreCase("observaciones")) {
                            textView.setText(listaHorarios[i].getObservaciones());
                            if (x > 0 && x < (tableHeader.length - 2)) {
                                textView.setText(listaHorarios[i].getHoras().get(cont));
                                cont++;
                                if (cont == tableHeader.length - 3) {
                                    cont = 0;
                                }
                            }
                            if( x == tableHeader.length-2){
                                textView.setText(listaHorarios[i].getDias());
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
                        //textView.setTextAppearance(R.style.Widget_MaterialComponents_TabLayout);
                        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        textView.setGravity(CENTER_VERTICAL);
                        if(textView.getText().toString().trim().contains("M")) {
                            final int finalI = i;
                            textView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //System.out.println(textView.getText().toString());
                                    String linea = textView.getText().toString();
                                    textView.setBackgroundColor(Color.rgb(37, 121, 204));
                                    /*if (textView.getBackground() instanceof ColorDrawable) {
                                        ColorDrawable cd = (ColorDrawable) textView.getBackground();
                                        int colorCode = cd.getColor();
                                    }*/
                                    /*if(background == 0){
                                        textView.setBackgroundColor(Color.rgb(37, 121, 204));
                                        background++;
                                    }else{
                                        textView.setBackgroundColor(Color.WHITE);
                                    }*/
                                    try {
                                        dataOut.writeUTF("id_linea");
                                        dataOut.flush();
                                        dataOut.writeUTF(linea);
                                        dataOut.flush();
                                        idLinea = dataIn.readInt();
                                        horaSalida = listaHorarios[finalI].getHoras().get(0);
                                        if(horaSalida.contains("-")){
                                            for(int i = 0; i < listaHorarios[finalI].getHoras().size(); i++){
                                                horaSalida = listaHorarios[finalI].getHoras().get(i);
                                                if(horaSalida.contains("-")){
                                                    horaSalida = listaHorarios[finalI].getHoras().get(i+1);
                                                }else{
                                                    break;
                                                }
                                            }
                                        }
                                        horaLlegada = listaHorarios[finalI].getHoras().get(listaHorarios[finalI].getHoras().size()-1);
                                        if(horaLlegada.contains("-")) {
                                            for (int i = listaHorarios[finalI].getHoras().size() - 2; i >= 0; i--) {
                                                horaLlegada = listaHorarios[finalI].getHoras().get(i);
                                                if (horaLlegada.contains("-")) {
                                                    horaLlegada = listaHorarios[finalI].getHoras().get(i - 1);
                                                } else {
                                                    break;
                                                }
                                            }
                                        }
                                        /*horaLlegada = listaHorarios[finalI].getHoras().get(listaHorarios[finalI].getHoras().size()-1);
                                        if(horaLlegada.contains("-")){
                                            horaLlegada = listaHorarios[finalI].getHoras().get(listaHorarios[finalI].getHoras().size()-2);
                                        }*/
                                        /*DateFormat sdfLlegada = new SimpleDateFormat("hh:mm");
                                        DateFormat sdfSalida = new SimpleDateFormat("hh:mm");
                                        Date dateLlegada = sdfLlegada.parse(horaLlegada);
                                        Date dateSalida = sdfSalida.parse(horaSalida);
                                        timeLlegada = new Time(dateLlegada.getTime());
                                        timeSalida = new Time(dateSalida.getTime());
                                        System.out.println("Date " + new Date());*/
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
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
