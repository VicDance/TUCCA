package com.proyecto.tucca.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.proyecto.tucca.R;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity /*implements NavigationView.OnNavigationItemSelectedListener*/ {
    public static Socket cliente;
    public static DataOutputStream dataOut;
    public static DataInputStream dataIn;
    private Button buttonRegister;
    private Button buttonLogin;
    private EditText editTextUser;
    private EditText editTextPassword;
    public static final String STRING_PREFERENCES = "fragments";
    public static final String STRING_NAME_PREFERENCES = "user";
    public static final String PREFERENCE_STATUS = "estado.button.sesion";
    private RadioButton radioButton;
    public static boolean login;
    public static boolean invitado;
    //public static boolean guardado;
    private TextView textViewInvitado;
    public static String nombreCliente;
    /*public static String correoCliente;
    public static String tfnoCliente;
    public static String nacimientoCliente;*/
    public static int idCliente;
    private boolean activado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        conectar();
        if (getEstado()) {
            login = true;
            //guardado = true;
            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
            intent.putExtra("nombre", nombreCliente);
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
                if (editTextUser.getText().length() != 0 && editTextPassword.getText().length() != 0) {
                    try {
                        dataOut.writeUTF("inicio");
                        dataOut.flush();
                        dataOut.writeUTF(editTextUser.getText().toString().trim());
                        dataOut.flush();
                        dataOut.writeUTF(editTextPassword.getText().toString());
                        dataOut.flush();
                        String respuesta = dataIn.readUTF();
                        if (respuesta.equalsIgnoreCase("correcto")) {
                            idCliente = dataIn.readInt();
                            //nombreCliente = editTextUser.getText().toString().trim();
                            login = true;
                            guardaEstado();
                            guardarDatos();
                            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                            intent.putExtra("nombre", editTextUser.getText().toString().trim());
                            startActivity(intent);
                            finish();
                        } else {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("No se pudo conectar")
                                    .setMessage("Usuario o contraseña incorrectos")
                                    .show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                } else {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("No se pudo conectar")
                            .setMessage("Alguno de los campos está vacío")
                            .show();
                }
            }
        });

        textViewInvitado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //invitado = true;
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void guardaEstado() {
        SharedPreferences preferences = getSharedPreferences(STRING_PREFERENCES, MODE_PRIVATE);
        preferences.edit().putBoolean(PREFERENCE_STATUS, radioButton.isChecked()).apply();
    }

    public boolean getEstado() {
        SharedPreferences preferences = getSharedPreferences(STRING_PREFERENCES, MODE_PRIVATE);
        return preferences.getBoolean(PREFERENCE_STATUS, false);
    }

    public void guardarDatos(){
        SharedPreferences preferences = getSharedPreferences(STRING_PREFERENCES, MODE_PRIVATE);
        preferences.edit().putString(STRING_NAME_PREFERENCES, editTextUser.getText().toString().trim()).apply();
    }

    public static String getDatos(Context context){
        SharedPreferences preferences = context.getSharedPreferences(STRING_PREFERENCES, MODE_PRIVATE);
        return preferences.getString(STRING_NAME_PREFERENCES, "");
    }

    private void conectar() {
        final int PUERTO = 6000;
        //mi casa
        final String HOST = "192.168.1.13";
        //casa angel
        //final String HOST = "192.168.137.1";
        //"localhost";
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            try {
                cliente = new Socket(HOST, PUERTO);
                dataOut = new DataOutputStream(cliente.getOutputStream());
                dataIn = new DataInputStream(cliente.getInputStream());
            } catch (IOException ex) {
                Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
}
