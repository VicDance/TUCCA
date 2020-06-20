package com.proyecto.transportesbahiacadiz.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.proyecto.transportesbahiacadiz.R;
import com.proyecto.transportesbahiacadiz.db.DataBaseRoom;
import com.proyecto.transportesbahiacadiz.util.Connection;
import com.proyecto.transportesbahiacadiz.util.ConnectionClass;
import com.tapadoo.alerter.Alerter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import static android.os.Build.VERSION.SDK_INT;

public class MainActivity extends AppCompatActivity /*implements NavigationView.OnNavigationItemSelectedListener*/ {
    private Button buttonRegister;
    private Button buttonLogin;
    private EditText editTextUser;
    private EditText editTextPassword;

    public static final String STRING_PREFERENCES = "fragments";
    public static final String STRING_NAME_PREFERENCES = "user";
    public static final String PREFERENCE_STATUS = "estado.button.sesion";

    private RadioButton radioButton;
    public static boolean login;
    private TextView textViewInvitado;
    public static String nombreCliente;
    int idCliente;
    static boolean activado;
    private ImageButton settingsButton;
    private String respuesta;

    String direccionIP = "";
    int puerto = 0;

    ConnectionClass connectionClass = null;
    DataBaseRoom dataBaseRoom = null;
    Connection connection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectionClass = new ConnectionClass(this);
        dataBaseRoom = DataBaseRoom.getINSTANCE(this);
        settingsButton = findViewById(R.id.button_select_ip);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view_popup = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_settings, null);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.DialogSettings));
                alertDialog.setView(view_popup);
                final Dialog dialog = alertDialog.create();
                dialog.show();

                getConnection();

                final EditText direccionIP_editText = view_popup.findViewById(R.id.direccionIP);
                final EditText puerto_editText = view_popup.findViewById(R.id.puerto);

                ImageView confirmar = view_popup.findViewById(R.id.aceptar_settings);
                confirmar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (connection == null) {
                            direccionIP = direccionIP_editText.getText().toString();
                            puerto = Integer.valueOf(puerto_editText.getText().toString());

                            connection = new Connection();
                            connection.setAddress(direccionIP);
                            connection.setPort(puerto);
                            addConnection();
                            dialog.dismiss();
                        } else {
                            direccionIP = direccionIP_editText.getText().toString();
                            puerto = Integer.valueOf(puerto_editText.getText().toString());
                            connection.setAddress(direccionIP);
                            connection.setPort(puerto);
                            editConnection();
                            dialog.dismiss();
                        }
                        Alerter.create(MainActivity.this)
                                .setTitle(getString(R.string.connection_established))
                                .setText("Se ha establecido la conexión con " + direccionIP + " a traves del puerto " + puerto)
                                .setIcon(R.drawable.alerter_ic_notifications)
                                .setBackgroundColorRes(R.color.alerter_login)
                                .setDuration(2100)
                                .enableSwipeToDismiss()
                                .enableProgress(true)
                                .setProgressColorRes(R.color.black)
                                .show();
                    }
                });
            }
        });
        if (getEstado()) {
            login = true;
            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
            intent.putExtra("nombre", nombreCliente);
            intent.putExtra("id", idCliente);
            startActivity(intent);
        }
        textViewInvitado = findViewById(R.id.text_view_invitado);
        radioButton = findViewById(R.id.radio_no_close);
        activado = radioButton.isChecked();
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activado) {
                    radioButton.setChecked(false);
                }
                activado = radioButton.isChecked();
            }
        });
        buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        editTextUser = findViewById(R.id.edit_text_user);
        editTextPassword = findViewById(R.id.edit_text_password);
        buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = editTextUser.getText().toString();
                String contraseña = editTextPassword.getText().toString();
                login(nombre, contraseña);
            }
        });

        textViewInvitado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public static void guardaEstado(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(STRING_PREFERENCES, MODE_PRIVATE);
        preferences.edit().putBoolean(PREFERENCE_STATUS, activado).apply();
    }

    public boolean getEstado() {
        SharedPreferences preferences = getSharedPreferences(STRING_PREFERENCES, MODE_PRIVATE);
        return preferences.getBoolean(PREFERENCE_STATUS, false);
    }

    public void guardarDatos() {
        SharedPreferences preferences = getSharedPreferences(STRING_PREFERENCES, MODE_PRIVATE);
        preferences.edit().putString(STRING_NAME_PREFERENCES, editTextUser.getText().toString().trim()).apply();
    }

    public static String getDatos(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(STRING_PREFERENCES, MODE_PRIVATE);
        return preferences.getString(STRING_NAME_PREFERENCES, "");
    }

    private void login(String nombre, String contraseña) {
        loginTask loginTask = new loginTask(nombre, contraseña);
        loginTask.execute();
    }

    private void addConnection() {
        insertConnectionTask insertConnectionTask = new insertConnectionTask();
        insertConnectionTask.execute();
    }

    private void getConnection() {
        getConnectionTask getConnectionTask = new getConnectionTask();
        getConnectionTask.execute();
    }

    private void editConnection() {
        updateConnectionTask updateConnectionTask = new updateConnectionTask();
        updateConnectionTask.execute();
    }

    public class loginTask extends AsyncTask<String, Void, Void> {
        Socket cliente;
        ObjectOutputStream outputStream;
        ObjectInputStream inputStream;
        private String nombre, contraseña;

        public loginTask(String nombre, String contraseña){
            this.nombre = nombre;
            this.contraseña = contraseña;
        }

        @Override
        protected Void doInBackground(String... strings) {
            int SDK_INT = Build.VERSION.SDK_INT;
            if (SDK_INT > 8) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
                try {
                    cliente = new Socket(connectionClass.getConnection().get(0).getAddress(), connectionClass.getConnection().get(0).getPort());
                    System.out.println("direccion: " + connectionClass.getConnection().get(0).getAddress());
                    System.out.println("puerto: " + connectionClass.getConnection().get(0).getPort());
                    outputStream = new ObjectOutputStream(cliente.getOutputStream());
                    inputStream = new ObjectInputStream(cliente.getInputStream());
                    if (nombre.length() != 0 && contraseña.length() != 0) {
                        outputStream.writeUTF("inicio");
                        outputStream.flush();
                        outputStream.reset();

                        outputStream.writeUTF(nombre.trim());
                        outputStream.flush();
                        outputStream.reset();

                        outputStream.writeUTF(contraseña);
                        outputStream.flush();
                        outputStream.reset();

                        respuesta = inputStream.readUTF();
                        System.out.println(respuesta);
                    } else {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(getString(R.string.impossible_to_connect))
                                .setMessage(getString(R.string.empty_fields))
                                .show();
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (SDK_INT > 8) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);

                if (respuesta.trim().contains("cliente")) {
                    try {
                        idCliente = inputStream.readInt();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    login = true;
                    guardaEstado(MainActivity.this);
                    guardarDatos();
                    Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                    intent.putExtra("nombre", editTextUser.getText().toString().trim());
                    intent.putExtra("id", idCliente);
                    startActivity(intent);
                    finish();
                } else if (respuesta.trim().contains("revisor")) {
                    startActivity(new Intent(MainActivity.this, ScanActivity.class));
                    finish();
                } else {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(getString(R.string.impossible_to_connect))
                            .setMessage(getString(R.string.user_incorrect))
                            .show();
                }
            }
        }
    }

    public class insertConnectionTask extends AsyncTask<String, Void, Long> {
        @Override
        protected Long doInBackground(String... strings) {
            long id = 0;

            id = dataBaseRoom.connectionDAO().insertConnection(connection);
            return id;
        }
    }

    public class getConnectionTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            try {
                connection = dataBaseRoom.connectionDAO().getAll().get(0);
            } catch (Exception e) {

            }
            return null;
        }
    }

    public class updateConnectionTask extends AsyncTask<String, Void, Long> {
        @Override
        protected Long doInBackground(String... strings) {
            long id = 0;

            id = dataBaseRoom.connectionDAO().updateConnection(connection);
            return id;
        }
    }
}
