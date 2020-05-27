package com.proyecto.tucca.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.proyecto.tucca.R;
import com.proyecto.tucca.activities.CreditCardActivity;

import java.io.IOException;

import static android.content.Context.SENSOR_SERVICE;
import static com.proyecto.tucca.activities.MainActivity.dataIn;

public class CardDialog extends DialogFragment {
    private View view;
    //private Button btnPay;
    private Button btnreload;
    private TextView textView;
    private int saldo;
    private String numtarjeta;
    private String message;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_card, container, false);
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_card, null);
        textView = view.findViewById(R.id.text_view_balance);
        try {
            numtarjeta = dataIn.readUTF();
            saldo = dataIn.readInt();
            //System.out.println(saldo);
            textView.setText(saldo + "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //btnPay = view.findViewById(R.id.btn_pay);
        btnreload = view.findViewById(R.id.btn_reload);
        /*btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //System.out.println("holi");
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
        });*/

        btnreload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreditCardActivity.class);
                intent.putExtra("recarga", numtarjeta);
                startActivity(intent);
                dismiss();
            }
        });
        builder.setView(view);
        return builder.create();
    }

    public void setMessage(String message){
        this.message = message;
    }

    private void compruebaPosicion() {
        SensorManager sensorManager;
        Sensor rotationSensor;
        SensorEventListener sensorEventListener;

        sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorEventListener = new SensorEventListener() {
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
                /*if(orientations[1] > 45) {
                    getActivity().getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                } else if(orientations[1] < -45) {
                    getActivity().getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                } else if(Math.abs(orientations[1]) < 10) {
                    getActivity().getWindow().getDecorView().setBackgroundColor(Color.WHITE);
                }*/
                if (orientations[1] < -45) {
                    getActivity().getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(sensorEventListener, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
}
