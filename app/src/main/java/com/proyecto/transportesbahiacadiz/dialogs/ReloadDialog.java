package com.proyecto.transportesbahiacadiz.dialogs;

import android.app.Dialog;
import android.os.AsyncTask;
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

import com.proyecto.transportesbahiacadiz.R;
import com.proyecto.transportesbahiacadiz.util.ConnectionClass;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ReloadDialog extends DialogFragment {
    private View view;
    private Button button;
    private EditText editText;
    private String message;
    private ConnectionClass connectionClass;
    private String estado;

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
        connectionClass = new ConnectionClass(getContext());
        button = view.findViewById(R.id.btn_credit_card_reload);
        editText = view.findViewById(R.id.edit_text_balance);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //System.out.println(message);
                new reloadTask().execute();
            }
        });
        builder.setView(view);
        return builder.create();
    }

    public void setMessage(String message){
        this.message = message;
    }

    class reloadTask extends AsyncTask<Void, Void, Void>{
        Socket cliente;
        ObjectOutputStream outputStream;
        ObjectInputStream inputStream;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                cliente = new Socket(connectionClass.getConnection().get(0).getAddress(), connectionClass.getConnection().get(0).getPort());
                outputStream = new ObjectOutputStream(cliente.getOutputStream());
                inputStream = new ObjectInputStream(cliente.getInputStream());

                outputStream.writeUTF("rtarjeta");
                outputStream.flush();
                outputStream.reset();

                outputStream.writeDouble(Double.parseDouble(editText.getText().toString()));
                outputStream.flush();
                outputStream.reset();

                System.out.println(message);

                outputStream.writeUTF(message);
                outputStream.flush();
                outputStream.reset();

                estado = inputStream.readUTF();
            }catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(estado.equalsIgnoreCase("correcto")) {
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.correct)
                        .show();
            }else{
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.incorrect)
                        .setMessage(R.string.incorrect_message)
                        .show();
            }
        }
    }
}
