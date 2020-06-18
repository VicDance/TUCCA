package com.proyecto.transportesbahiacadiz.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.proyecto.transportesbahiacadiz.dialogs.ReloadDialog;
import com.proyecto.transportesbahiacadiz.model.CreditCard;
import com.proyecto.transportesbahiacadiz.R;
import com.proyecto.transportesbahiacadiz.adapters.CreditCardsAdapter;
import com.proyecto.transportesbahiacadiz.util.ConnectionClass;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import serializable.TarjetaCredito;

import static com.proyecto.transportesbahiacadiz.activities.RegisterActivity.usuario;

public class CreditCardActivity extends AppCompatActivity implements CreditCardsAdapter.OnItemClickListener, CreditCardsAdapter.OnLongItemCliclListener {
    private RecyclerView recyclerView;
    private CreditCardsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private TextView textView;
    private ArrayList<TarjetaCredito> creditCardItems = null;
    private Button btnNewCredit;
    private int size;
    private String newString;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ConnectionClass connectionClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card);
        connectionClass = new ConnectionClass(this);
        creditCardItems = new ArrayList<TarjetaCredito>();
        new getTarjetasCreditoTask().execute();
        creditCardItems.clear();
        recyclerView = findViewById(R.id.recycler_view_credit_cards);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
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
                new getTarjetasCreditoTask().execute();
                try {
                    creditCardItems.clear();
                    recyclerView = findViewById(R.id.recycler_view_credit_cards);
                    recyclerView.setHasFixedSize(true);
                    layoutManager = new LinearLayoutManager(CreditCardActivity.this);
                    recyclerView.setLayoutManager(layoutManager);
                    Thread.sleep(1000);
                    swipeRefreshLayout.setRefreshing(false);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void buildRecycler() {
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
        if (extras == null) {
            newString = null;
        } else {
            newString = extras.getString("recarga");
            showDialog();
        }
    }

    @Override
    public void onLongItemClick(int position) {
        showDeleteDialog(position);
    }

    private void showDeleteDialog(final int position) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.textDeleteCard)
                .setMessage(R.string.deleteCard)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new borrarTarjetaTask(position).execute();
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

    class getTarjetasCreditoTask extends AsyncTask<Void, Void, Void> {
        Socket cliente;
        ObjectOutputStream outputStream;
        ObjectInputStream inputStream;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                cliente = new Socket(connectionClass.getConnection().get(0).getAddress(), connectionClass.getConnection().get(0).getPort());
                outputStream = new ObjectOutputStream(cliente.getOutputStream());
                inputStream = new ObjectInputStream(cliente.getInputStream());

                creditCardItems.clear();
                outputStream.writeUTF("tarjetas");
                outputStream.flush();
                outputStream.reset();

                System.out.println(usuario.getId());
                outputStream.writeInt(usuario.getId());
                outputStream.flush();
                outputStream.reset();

                size = inputStream.readInt();
                for (int i = 0; i < size; i++) {
                    TarjetaCredito tarjetaCredito = (TarjetaCredito) inputStream.readObject();
                    creditCardItems.add(tarjetaCredito);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            buildRecycler();
        }
    }

    class borrarTarjetaTask extends AsyncTask<Void, Void, Void> {
        Socket cliente;
        ObjectOutputStream outputStream;
        ObjectInputStream inputStream;

        private int position;

        public borrarTarjetaTask(int position){
            this.position = position;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                cliente = new Socket(connectionClass.getConnection().get(0).getAddress(), connectionClass.getConnection().get(0).getPort());
                outputStream = new ObjectOutputStream(cliente.getOutputStream());
                inputStream = new ObjectInputStream(cliente.getInputStream());

                outputStream.writeUTF("btarjetaCredito");
                outputStream.flush();
                outputStream.reset();

                outputStream.writeInt(position);
                outputStream.flush();
                outputStream.reset();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            creditCardItems.remove(position);
            adapter.notifyItemRemoved(position);
        }
    }
}
