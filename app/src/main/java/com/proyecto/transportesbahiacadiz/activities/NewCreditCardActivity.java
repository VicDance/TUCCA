package com.proyecto.transportesbahiacadiz.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.proyecto.transportesbahiacadiz.R;
import com.proyecto.transportesbahiacadiz.util.ConnectionClass;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

import static com.proyecto.transportesbahiacadiz.activities.RegisterActivity.usuario;

public class NewCreditCardActivity extends AppCompatActivity {
    public static final int SCAN_RESULT = 100;
    private TextView editTextTitular;
    private TextView editTextNumTarjeta;
    private TextView editTextCaducidad;
    private TextView editTextCCV;
    private Button buttonScan;
    private ConnectionClass connectionClass;
    private CreditCard scanResult;
    private String estado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_credit_card);

        connectionClass = new ConnectionClass(this);

        editTextTitular = findViewById(R.id.edit_text_titular);
        editTextNumTarjeta = findViewById(R.id.edit_text_num_credit_card);
        editTextCaducidad = findViewById(R.id.edit_text_caducidad);
        editTextCCV = findViewById(R.id.edit_text_CCV);
        buttonScan = findViewById(R.id.button_scan_credit_card);
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewCreditCardActivity.this, CardIOActivity.class)
                        .putExtra(CardIOActivity.EXTRA_REQUIRE_CARDHOLDER_NAME, true)
                        .putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true)
                        .putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true)
                        .putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false);
                startActivityForResult(intent, SCAN_RESULT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SCAN_RESULT){
            if(data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)){
                scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
                editTextNumTarjeta.setText(scanResult.getRedactedCardNumber());
                editTextCCV.setText(scanResult.cvv);
                editTextTitular.setText(scanResult.cardholderName);
                if(scanResult.isExpiryValid()){
                    String mes = String.valueOf(scanResult.expiryMonth);
                    String anio = String.valueOf(scanResult.expiryYear);
                    editTextCaducidad.setText(mes + "/" + anio);
                    String caducidad = editTextCaducidad.getText().toString();
                    String titular = editTextTitular.getText().toString();
                    new nuevaTarjetaTask(caducidad, titular).execute();
                }
            }
        }
    }

    class nuevaTarjetaTask extends AsyncTask<Void, Void, Void>{
        Socket cliente;
        ObjectOutputStream outputStream;
        ObjectInputStream inputStream;
        private String caducidad, titular;

        public nuevaTarjetaTask(String caducidad, String titular){
            this.caducidad = caducidad;
            this.titular = titular;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                cliente = new Socket(connectionClass.getConnection().get(0).getAddress(), connectionClass.getConnection().get(0).getPort());
                outputStream = new ObjectOutputStream(cliente.getOutputStream());
                inputStream = new ObjectInputStream(cliente.getInputStream());

                outputStream.writeUTF("ntarjeta");
                outputStream.flush();
                outputStream.reset();

                System.out.println(usuario.getId());
                outputStream.writeUTF(scanResult.getRedactedCardNumber() + "-" + usuario.getId() + "-" + caducidad + "-"
                        + titular);
                outputStream.flush();
                outputStream.reset();
                estado = inputStream.readUTF();
                //System.out.println(estado);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(estado.equalsIgnoreCase("correcto")){
                new AlertDialog.Builder(NewCreditCardActivity.this)
                        .setTitle(R.string.correct)
                        .setMessage("Se ha ingresado una nueva tarjeta")
                        .show();
            }else {
                System.out.println("error");
            }
        }
    }
}
