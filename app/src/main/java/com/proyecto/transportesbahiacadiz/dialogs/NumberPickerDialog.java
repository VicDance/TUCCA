package com.proyecto.transportesbahiacadiz.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.proyecto.transportesbahiacadiz.R;

import com.proyecto.transportesbahiacadiz.activities.MenuActivity;
import com.proyecto.transportesbahiacadiz.util.ConnectionClass;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static com.proyecto.transportesbahiacadiz.activities.RegisterActivity.usuario;

public class NumberPickerDialog extends DialogFragment {
    private View view;
    private Button accept;
    private Button cancel;
    private NumberPicker numberPicker;
    private int idCiudadDestino;
    private int idLinea;
    private double bs;
    private String horaSalida;
    private String horaLlegada;
    private ConnectionClass connectionClass;

    public NumberPickerDialog(double bs, String horaSalida, int idCiudadDestino, int idLinea, String horaLlegada) {
        this.idCiudadDestino = idCiudadDestino;
        this.bs = bs;
        this.horaSalida = horaSalida;
        this.idLinea = idLinea;
        this.horaLlegada = horaLlegada;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_number_picker, container, false);
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_number_picker, null);
        connectionClass = new ConnectionClass(getContext());
        numberPicker = view.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(10);
        accept = view.findViewById(R.id.button_number_accept);
        cancel = view.findViewById(R.id.button_number_cancel);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new insertarViajeTasK().execute();
                startActivity(new Intent(getContext(), MenuActivity.class)
                        .putExtra("pagar", bs)
                        .putExtra("salida", horaSalida)
                        .putExtra("destino", idCiudadDestino)
                        .putExtra("billetes", numberPicker.getValue()));
                //System.out.println("Billetes: " + numberPicker.getValue());
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        builder.setView(view);
        return builder.create();
    }

    class insertarViajeTasK extends AsyncTask<Void, Void, Void> {
        Socket cliente;
        ObjectOutputStream outputStream;
        ObjectInputStream inputStream;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                cliente = new Socket(connectionClass.getConnection().get(0).getAddress(), connectionClass.getConnection().get(0).getPort());
                outputStream = new ObjectOutputStream(cliente.getOutputStream());
                inputStream = new ObjectInputStream(cliente.getInputStream());

                outputStream.writeUTF("iviaje");
                outputStream.flush();
                outputStream.reset();

                outputStream.writeUTF(usuario.getId() + "/" + idLinea + "/" + idCiudadDestino + "/" + bs + "/" + horaSalida + "/" + horaLlegada);
                outputStream.flush();
                outputStream.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
