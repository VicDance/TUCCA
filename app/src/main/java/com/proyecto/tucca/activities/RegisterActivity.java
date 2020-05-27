package com.proyecto.tucca.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.proyecto.tucca.R;
import com.proyecto.tucca.fragments.DatePickerFragment;
import com.proyecto.tucca.model.Usuario;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.proyecto.tucca.activities.MainActivity.dataIn;
import static com.proyecto.tucca.activities.MainActivity.dataOut;

public class RegisterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private Button button;
    private EditText editTextUser;
    private EditText editTextPassword;
    private EditText editTextRepeat;
    private EditText editTextEmail;
    private EditText editTextPhone;
    private ImageButton datePicker;
    private TextView textView;
    private Long date;

    public static Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        datePicker = findViewById(R.id.button_date_picker);
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "Date Picker");
            }
        });
        button = findViewById(R.id.btn_register);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (compruebaCampos() && compruebaTfno(String.valueOf(editTextPhone.getText())) &&
                        compruebaEmail(String.valueOf(editTextEmail.getText()))) {
                    try {
                        dataOut.writeUTF("encriptar");
                        dataOut.flush();
                        dataOut.writeUTF("cliente" + "/" + editTextUser.getText() + "/" + editTextPassword.getText() + "/"
                        + editTextEmail.getText() + "/" + editTextPhone.getText() + "/" + date);
                        dataOut.flush();
                        String estado = dataIn.readUTF();
                        if(estado.equalsIgnoreCase("correcto")){
                            usuario = new Usuario(editTextUser.getText().toString(), editTextEmail.getText().toString(), Integer.parseInt(editTextPhone.getText().toString()), textView.getText().toString(), "");
                            System.out.println(usuario);
                            new AlertDialog.Builder(RegisterActivity.this)
                                    .setTitle(getString(R.string.correct))
                                    .setMessage("Se ha registrado satisfactoriamente")
                                    .show();
                        }else{
                            new AlertDialog.Builder(RegisterActivity.this)
                                    .setTitle(getString(R.string.incorrect))
                                    .setMessage("No se ha podido registrar")
                                    .show();
                        }
                        //finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    new AlertDialog.Builder(RegisterActivity.this)
                            .setTitle("Campos vacios")
                            .setMessage("Hay campos sin rellenar")
                            .show();
                }
            }
        });
    }

    public boolean compruebaCampos() {
        boolean llenos = false;
        editTextUser = findViewById(R.id.edit_text_register_user);
        editTextPassword = findViewById(R.id.edit_text_register_password);
        editTextRepeat = findViewById(R.id.edit_text_register_repeat_password);
        editTextEmail = findViewById(R.id.edit_text_register_email);
        editTextPhone = findViewById(R.id.edit_text_register_phone);
        if (editTextUser != null && editTextPassword != null && editTextRepeat != null
                && editTextEmail != null && editTextPhone != null && datePicker != null) {
            if(compruebaRepetirContraseña()){
                llenos = true;
            }
        }
        return llenos;
    }

    private boolean compruebaRepetirContraseña(){
        boolean correcta = false;
        String contraseña = String.valueOf(editTextPassword.getText());
        String contraseñaRepetida = String.valueOf(editTextRepeat.getText());

        if(contraseña.compareTo(contraseñaRepetida) == 0){
            correcta = true;
        }

        return correcta;
    }

    public boolean compruebaEmail(String email) {
        boolean valido = false;
        Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

        String emailAValidar = email;
        Matcher mather = pattern.matcher(emailAValidar);
        if (mather.find() == true) {
            System.out.println("El email ingresado es válido.");
            valido = true;
        } else {
            System.out.println("El email ingresado es inválido.");
        }

        return valido;
    }

    public boolean compruebaTfno(String tfno) {
        boolean valido = false;
        try {
            if (tfno.length() > 9) {
                return valido;
            } else {
                Integer.parseInt(tfno);
                valido = true;
            }
        } catch (Exception e) {
            valido = false;
        }
        return valido;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(/*DateFormat.DATE_FIELD*/).format(c.getTime());
        textView = findViewById(R.id.text_view_register_date);
        textView.setText(currentDateString);
        date = c.getTime().getTime();
    }
}
