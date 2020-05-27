package com.proyecto.tucca.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.proyecto.tucca.R;

import java.io.IOException;

import static com.proyecto.tucca.activities.MainActivity.dataIn;
import static com.proyecto.tucca.activities.MainActivity.dataOut;

public class ReloadDialog extends DialogFragment {
    private View view;
    private Button button;
    private EditText editText;
    private String message;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_reload, container, false);
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_reload, null);
        button = view.findViewById(R.id.btn_credit_card_reload);
        editText = view.findViewById(R.id.edit_text_balance);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //System.out.println(message);
                try {
                    dataOut.writeUTF("rtarjeta");
                    dataOut.flush();
                    dataOut.writeInt(Integer.parseInt(editText.getText().toString()));
                    dataOut.flush();
                    dataOut.writeUTF(message);
                    dataOut.flush();
                    String estado = dataIn.readUTF();
                    if(estado.equalsIgnoreCase("correcto")) {
                        dismiss();
                        new AlertDialog.Builder(getContext())
                                .setTitle(R.string.correct)
                                .show();
                    }else{
                        new AlertDialog.Builder(getContext())
                                .setTitle(R.string.incorrect)
                                .setMessage(R.string.incorrect_message)
                                .show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setView(view);
        return builder.create();
    }

    public void setMessage(String message){
        this.message = message;
    }
}
