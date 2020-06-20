package com.proyecto.transportesbahiacadiz.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.proyecto.transportesbahiacadiz.R;
import com.proyecto.transportesbahiacadiz.activities.ChangeProfileActivity;
import com.proyecto.transportesbahiacadiz.activities.CreditCardActivity;
import com.proyecto.transportesbahiacadiz.activities.MenuActivity;
import com.proyecto.transportesbahiacadiz.model.Usuario;
import com.proyecto.transportesbahiacadiz.util.ConnectionClass;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static com.proyecto.transportesbahiacadiz.activities.MainActivity.getDatos;
import static com.proyecto.transportesbahiacadiz.activities.MainActivity.guardaEstado;
import static com.proyecto.transportesbahiacadiz.activities.MainActivity.login;
import static com.proyecto.transportesbahiacadiz.activities.RegisterActivity.usuario;

public class MeFragment extends Fragment {
    private TextView textViewUser;
    private TextView textViewBorn;
    private TextView textViewEmail;
    private TextView textViewPhone;
    private Button btnExit;
    private Button btnChangeProfile;
    private ImageView imageViewUser;
    private View view;
    private String nombre;
    private String contraseña;
    private String correo;
    private int tfno;
    private String fecha_nac;
    private ConnectionClass connectionClass;
    private serializable.Usuario user;


    @RequiresApi(api = Build.VERSION_CODES.P)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_me, container, false);

        connectionClass = new ConnectionClass(getContext());

        btnExit = view.findViewById(R.id.btn_exit);
        btnChangeProfile = view.findViewById(R.id.btn_change_profile);
        imageViewUser = view.findViewById(R.id.image_user);
        textViewUser = view.findViewById(R.id.text_view_user);
        textViewBorn = view.findViewById(R.id.text_view_born_date);
        textViewEmail = view.findViewById(R.id.text_view_email);
        textViewPhone = view.findViewById(R.id.text_view_phone);

        setUser();

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login = false;
                guardaEstado(getContext());
                Intent intent = new Intent(getContext(), MenuActivity.class);
                startActivity(intent);
            }
        });

        btnChangeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChangeProfileActivity.class);
                intent.putExtra("imagen", usuario.getImagen());
                intent.putExtra("id", usuario.getId());
                startActivity(intent);
            }
        });

        return view;
    }

    private void setUser() {
        new setUserTask().execute();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (login) {
            inflater.inflate(R.menu.add_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_add_card:
                Intent intent = new Intent(getContext(), CreditCardActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void cambiaImagen(Usuario usuario) {
        try {
            if (usuario.getImagen().toString().equals("null") || usuario.getImagen().toString().length() == 0 || usuario.getImagen() == null ||
                    usuario.getImagen().toString().equalsIgnoreCase("imagen")) {
                imageViewUser.setImageResource(R.drawable.ic_person_loggin);
            } else {
                byte[] blob = usuario.getImagen();
                ByteArrayInputStream bais = new ByteArrayInputStream(blob);
                Bitmap photo = BitmapFactory.decodeStream(bais);
                imageViewUser.setImageBitmap(photo);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    class setUserTask extends AsyncTask<Void, Void, Void> {
        Socket cliente;
        ObjectOutputStream outputStream;
        ObjectInputStream inputStream;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                cliente = new Socket(connectionClass.getConnection().get(0).getAddress(), connectionClass.getConnection().get(0).getPort());
                outputStream = new ObjectOutputStream(cliente.getOutputStream());
                inputStream = new ObjectInputStream(cliente.getInputStream());

                outputStream.writeUTF("cliente");
                outputStream.flush();
                outputStream.reset();

                outputStream.writeUTF(getDatos(getContext()));
                outputStream.flush();
                outputStream.reset();

                user = (serializable.Usuario) inputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            usuario = new Usuario();

            usuario.setId(user.getId());
            usuario.setContraseña(user.getContraseña());
            usuario.setNombre(user.getNombre());
            usuario.setCorreo(user.getCorreo());
            usuario.setFecha_nac(user.getFecha_nac().toString());
            usuario.setTfno(user.getTfno());
            usuario.setImagen(user.getImagen());

            nombre = usuario.getNombre();
            contraseña = usuario.getContraseña();
            correo = usuario.getCorreo();
            fecha_nac = usuario.getFecha_nac();
            tfno = usuario.getTfno();

            textViewUser.setText(nombre);
            String[] split_fecha = fecha_nac.split("-");
            textViewBorn.setText(split_fecha[2] + "-" + split_fecha[1] + "-" + split_fecha[0]);
            textViewEmail.setText(correo);
            textViewPhone.setText(tfno + "");

            cambiaImagen(usuario);
        }
    }
}
