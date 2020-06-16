package com.proyecto.transportesbahiacadiz.dialogs;

import android.app.Dialog;
import android.content.Intent;
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

public class NumberPickerDialog extends DialogFragment {
    private View view;
    private Button accept;
    private Button cancel;
    private NumberPicker numberPicker;
    private int idCiudadDestino;
    private double bs;
    private String horaSalida;

    public NumberPickerDialog(double bs, String horaSalida, int idCiudadDestino) {
        this.idCiudadDestino = idCiudadDestino;
        this.bs = bs;
        this.horaSalida = horaSalida;
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
        numberPicker = view.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(10);
        accept = view.findViewById(R.id.button_number_accept);
        cancel = view.findViewById(R.id.button_number_cancel);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
}
