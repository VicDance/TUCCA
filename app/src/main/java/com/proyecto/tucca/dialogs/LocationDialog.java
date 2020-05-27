package com.proyecto.tucca.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.proyecto.tucca.R;

public class LocationDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.location_title).setItems(new String[]{"Utilizar posición actual", "Selecciona una dirección"},
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Toast toast = Toast.makeText(getContext(), "Pulsado item " + which, Toast.LENGTH_SHORT);
                                toast.show();
                                break;
                            case 1:
                                System.out.println("entra");
                                showDialog();
                        /*builder[0] = new AlertDialog.Builder(getContext());
                        LayoutInflater inflater = requireActivity().getLayoutInflater();
                        builder[0].setView(inflater.inflate(R.layout.dialog_direction, null))
                                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        // sign in the user ...
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        LocationDialog.this.getDialog().cancel();
                                    }
                                });*/
                                break;
                        }
                    }
                });
        return builder.create();
    }

    private void showDialog(){
        new AddressDialog().show(getFragmentManager(), "Address Dialog");
    }
}
