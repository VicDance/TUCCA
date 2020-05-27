package com.proyecto.tucca.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.proyecto.tucca.R;

import java.io.IOException;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

import static com.proyecto.tucca.activities.MainActivity.dataIn;
import static com.proyecto.tucca.activities.MainActivity.dataOut;
import static com.proyecto.tucca.activities.RegisterActivity.usuario;

public class NewCreditCardActivity extends AppCompatActivity {
    public static final int SCAN_RESULT = 100;
    private TextView editTextTitular;
    private TextView editTextNumTarjeta;
    private TextView editTextCaducidad;
    private TextView editTextCCV;
    private Button buttonScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_credit_card);
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
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
                editTextNumTarjeta.setText(scanResult.getRedactedCardNumber());
                editTextCCV.setText(scanResult.cvv);
                editTextTitular.setText(scanResult.cardholderName);
                if(scanResult.isExpiryValid()){
                    String mes = String.valueOf(scanResult.expiryMonth);
                    String anio = String.valueOf(scanResult.expiryYear);
                    editTextCaducidad.setText(mes + "/" + anio);
                }
                //System.out.println(scanResult.getFormattedCardNumber());
                try {
                    dataOut.writeUTF("ntarjeta");
                    dataOut.flush();
                    dataOut.writeUTF(scanResult.getRedactedCardNumber() + "-" + usuario.getId() + "-" + editTextCaducidad.getText() + "-"
                    + editTextTitular.getText());
                    dataOut.flush();
                    String estado = dataIn.readUTF();
                    System.out.println(estado);
                    if(estado.equalsIgnoreCase("correcto")){
                        new AlertDialog.Builder(this)
                                .setTitle("Correcto")
                                .setMessage("Se ha ingresado una nueva tarjeta")
                                .show();
                    }else {
                        System.out.println("error");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
