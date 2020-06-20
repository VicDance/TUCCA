package com.proyecto.transportesbahiacadiz.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.proyecto.transportesbahiacadiz.R;
import com.proyecto.transportesbahiacadiz.activities.CreditCardActivity;
import com.proyecto.transportesbahiacadiz.model.CodigoQR;
import com.proyecto.transportesbahiacadiz.util.ConnectionClass;

import net.glxn.qrgen.android.QRCode;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static android.content.Context.SENSOR_SERVICE;
import static com.proyecto.transportesbahiacadiz.activities.AddCardActivity.codigoQR;

public class CardDialog extends DialogFragment {
    private View view;
    private Button btnPay;
    private Button btnreload;
    private Button btnQr;
    private ImageView imageViewQr;
    private TextView textView;
    private String numtarjeta;
    double saldo;
    double descuento;
    private double bs;
    private String municipio;
    private String hora_salida;
    private MediaPlayer mediaPlayer;
    int cont;
    static String contenidoQR;
    private String horaCodigo;
    private double costeBillete;
    private int numBilletes;
    private ConnectionClass connectionClass;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_card, container, false);
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        cont = 1;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_card, null);
        connectionClass = new ConnectionClass(getActivity());
        textView = view.findViewById(R.id.text_view_balance);
        btnQr = view.findViewById(R.id.btn_qr);
        imageViewQr = view.findViewById(R.id.image_view_qr);
        imageViewQr.setVisibility(View.GONE);

        textView.setText(saldo + " €");

        new getCodigoQRTask().execute();

        btnPay = view.findViewById(R.id.btn_pay);
        if (bs == 0) {
            btnPay.setVisibility(View.INVISIBLE);
        }
        btnreload = view.findViewById(R.id.btn_reload);
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saldo <= 0) {
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.attention)
                            .setMessage(R.string.no_money)
                            .show();
                } else {
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.attention)
                            .setMessage(R.string.payment_instruction)
                            .show();
                    compruebaPosicion();
                }
            }
        });

        btnreload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreditCardActivity.class);
                intent.putExtra("recarga", numtarjeta);
                startActivity(intent);
                dismiss();
            }
        });

        btnQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewQr.setVisibility(View.VISIBLE);
            }
        });
        builder.setView(view);
        return builder.create();
    }

    public void setNumtarjeta(String numtarjeta) {
        this.numtarjeta = numtarjeta;
    }

    public void setBs(double bs) {
        this.bs = bs;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public void setHoraSalida(String hora_salida) {
        this.hora_salida = hora_salida;
    }

    public void setNumBilletes(int numBilletes) {
        this.numBilletes = numBilletes;
    }

    public void setSaldo(double saldo){
        this.saldo = saldo;
    }

    public void setDescuento(double descuento){
        this.descuento = descuento;
    }

    private void compruebaPosicion() {
        SensorManager sensorManager;
        Sensor rotationSensor;
        SensorEventListener sensorEventListener;

        sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorEventListener = new SensorEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onSensorChanged(SensorEvent event) {
                float[] rotationMatrix = new float[16];
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
                float[] remappedRotationMatrix = new float[16];
                SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, remappedRotationMatrix);
                float[] orientations = new float[3];
                SensorManager.getOrientation(remappedRotationMatrix, orientations);
                for (int i = 0; i < 3; i++) {
                    orientations[i] = (float) (Math.toDegrees(orientations[i]));
                }
                if (orientations[1] <= -45) {
                    if (cont == 1) {
                        if (saldo < (bs - (bs * descuento)) * numBilletes) {
                            new AlertDialog.Builder(getContext())
                                    .setTitle(R.string.attention)
                                    .setMessage(R.string.no_money)
                                    .show();
                            mediaPlayer = MediaPlayer.create(getContext(), R.raw.error);
                            mediaPlayer.start();
                        } else {
                            String tipoTarjeta = "";
                            if (descuento == 0.1) {
                                costeBillete = (bs * 0.9) * numBilletes;
                                saldo = saldo - costeBillete;
                                tipoTarjeta = "estándar";
                            } else if (descuento == 0.5) {
                                costeBillete = (bs * 0.5) * numBilletes;
                                saldo = saldo - costeBillete;
                                tipoTarjeta = "estudiante";
                            } else if (descuento == 0.3) {
                                costeBillete = (bs * 0.7) * numBilletes;
                                saldo = saldo - costeBillete;
                                tipoTarjeta = "jubilado";
                            }

                            new actualizaSaldoTask().execute();
                            mediaPlayer = MediaPlayer.create(getContext(), R.raw.beep);
                            mediaPlayer.start();
                            cont++;

                            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                            java.util.Date horaActual = new java.util.Date();
                            horaCodigo = dateFormat.format(horaActual);
                            contenidoQR = "Hora última utilización " + horaCodigo + "\n"
                                    + "Hora de salida " + hora_salida + "\n"
                                    + "Municipio de destino " + municipio + "\n"
                                    + "Tarjeta tipo " + tipoTarjeta;
                            System.out.println("hora codigo " + horaCodigo);
                            setCodigo();
                            Bitmap bitmap = QRCode.from(contenidoQR).withSize(700, 700).withCharset("UTF-8").bitmap();
                            imageViewQr.setImageBitmap(bitmap);
                            btnQr.setVisibility(View.VISIBLE);

                            new actualizaCodigoTask().execute();

                        }
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(sensorEventListener, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void setCodigo() {
        codigoQR = new CodigoQR();
        codigoQR.setHora_utilizacion(horaCodigo);
        codigoQR.setHora_salida(hora_salida);
        codigoQR.setMensaje(contenidoQR);
    }

    class getCodigoQRTask extends AsyncTask<Void, Void, Void> {
        Socket cliente;
        ObjectOutputStream outputStream;
        ObjectInputStream inputStream;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                cliente = new Socket(connectionClass.getConnection().get(0).getAddress(), connectionClass.getConnection().get(0).getPort());
                outputStream = new ObjectOutputStream(cliente.getOutputStream());
                inputStream = new ObjectInputStream(cliente.getInputStream());

                outputStream.writeUTF("codigo");
                outputStream.flush();
                outputStream.reset();

                outputStream.writeUTF(numtarjeta);
                outputStream.flush();
                outputStream.reset();

                contenidoQR = inputStream.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Bitmap bitmap = QRCode.from(contenidoQR).withSize(700, 700).withCharset("UTF-8").bitmap();
            imageViewQr.setImageBitmap(bitmap);
        }
    }

    class actualizaSaldoTask extends AsyncTask<Void, Void, Void> {
        Socket cliente;
        ObjectOutputStream outputStream;
        ObjectInputStream inputStream;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                cliente = new Socket(connectionClass.getConnection().get(0).getAddress(), connectionClass.getConnection().get(0).getPort());
                outputStream = new ObjectOutputStream(cliente.getOutputStream());
                inputStream = new ObjectInputStream(cliente.getInputStream());

                outputStream.writeUTF("actualiza_saldo");
                outputStream.flush();
                outputStream.reset();

                outputStream.writeUTF(saldo + "/" + numtarjeta);
                outputStream.flush();
                outputStream.reset();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    class actualizaCodigoTask extends AsyncTask<Void, Void, Void> {
        Socket cliente;
        ObjectOutputStream outputStream;
        ObjectInputStream inputStream;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                cliente = new Socket(connectionClass.getConnection().get(0).getAddress(), connectionClass.getConnection().get(0).getPort());
                outputStream = new ObjectOutputStream(cliente.getOutputStream());
                inputStream = new ObjectInputStream(cliente.getInputStream());

                outputStream.writeUTF("actualiza_codigo");
                outputStream.flush();
                outputStream.reset();

                outputStream.writeUTF(horaCodigo + "/" + numtarjeta + "/" + contenidoQR);
                outputStream.flush();
                outputStream.reset();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
