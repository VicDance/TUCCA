package com.proyecto.transportesbahiacadiz.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.proyecto.transportesbahiacadiz.dialogs.ReloadDialog;
import com.proyecto.transportesbahiacadiz.model.CreditCard;
import com.proyecto.transportesbahiacadiz.R;
import com.proyecto.transportesbahiacadiz.adapters.CreditCardsAdapter;

import java.io.IOException;
import java.util.ArrayList;

import static com.proyecto.transportesbahiacadiz.activities.MainActivity.dataIn;
import static com.proyecto.transportesbahiacadiz.activities.MainActivity.dataOut;

public class CreditCardActivity extends AppCompatActivity implements CreditCardsAdapter.OnItemClickListener, CreditCardsAdapter.OnLongItemCliclListener {
    private RecyclerView recyclerView;
    private CreditCardsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private TextView textView;
    private ArrayList<CreditCard> creditCardItems = null;
    private Button btnNewCredit;
    private int size;
    private String[] newDatos;
    private String newString;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card);
        creditCardItems = new ArrayList<CreditCard>();
        try {
            dataOut.writeUTF("tarjetas");
            dataOut.flush();
            size = dataIn.readInt();
            CreditCard creditCard = null;
            for(int i = 0; i < size; i++) {
                String datos;
                datos = dataIn.readUTF();
                newDatos = datos.split("-");
                creditCard = new CreditCard(newDatos[3], newDatos[0], newDatos[2]);
                creditCardItems.add(creditCard);
                //System.out.println(creditCard.getCardUser());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        buildRecycler();
        btnNewCredit = findViewById(R.id.btn_new_credit_card);
        btnNewCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreditCardActivity.this, NewCreditCardActivity.class);
                startActivity(intent);
            }
        });

        swipeRefreshLayout = findViewById(R.id.refresh_layout_credit_cards);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    dataOut.writeUTF("tarjetas");
                    dataOut.flush();
                    size = dataIn.readInt();
                    creditCardItems.clear();
                    CreditCard creditCard = null;
                    for(int i = 0; i < size; i++) {
                        String datos;
                        datos = dataIn.readUTF();
                        newDatos = datos.split("-");
                        creditCard = new CreditCard(newDatos[3], newDatos[0], newDatos[2]);
                        creditCardItems.add(creditCard);

                        Thread.sleep(1000);
                        swipeRefreshLayout.setRefreshing(false);

                        buildRecycler();
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void buildRecycler(){
        recyclerView = findViewById(R.id.recycler_view_credit_cards);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CreditCardsAdapter(creditCardItems, this, this);
        recyclerView.setAdapter(adapter);
    }

    private void showDialog() {
        ReloadDialog rd = new ReloadDialog();
        rd.setMessage(newString);
        rd.show(getSupportFragmentManager(), "Card Dialog");
    }

    @Override
    public void onItemClick(int position) {
        textView = findViewById(R.id.text_view_number_credit_card);
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            newString= null;
        } else {
            newString = extras.getString("recarga");
            showDialog();
        }
    }

    @Override
    public void onLongItemClick(int position) {
        showDeleteDialog(position);
    }

    private void showDeleteDialog(final int position){
        new AlertDialog.Builder(this)
                .setTitle(R.string.textDeleteCard)
                .setMessage(R.string.deleteCard)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            dataOut.writeUTF("btarjetaCredito");
                            dataOut.flush();
                            dataOut.writeInt(position);
                            dataOut.flush();
                            creditCardItems.remove(position);
                            adapter.notifyItemRemoved(position);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }
}
