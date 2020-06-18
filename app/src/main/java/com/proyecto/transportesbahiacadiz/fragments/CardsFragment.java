package com.proyecto.transportesbahiacadiz.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.proyecto.transportesbahiacadiz.activities.MenuActivity;
import com.proyecto.transportesbahiacadiz.dialogs.CardDialog;
import com.proyecto.transportesbahiacadiz.model.CardItem;
import com.proyecto.transportesbahiacadiz.adapters.CardsAdapter;
import com.proyecto.transportesbahiacadiz.R;
import com.proyecto.transportesbahiacadiz.activities.AddCardActivity;
import com.proyecto.transportesbahiacadiz.util.ConnectionClass;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import static com.proyecto.transportesbahiacadiz.activities.MainActivity.login;
import static com.proyecto.transportesbahiacadiz.activities.RegisterActivity.usuario;

public class CardsFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private CardsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private TextView textView;
    private ArrayList<CardItem> cardItemList = null;
    private int size;
    private String[] newDatos;
    private SwipeRefreshLayout swipeRefreshLayout;
    private double bs;
    private String horaSalida;
    private int destino;
    private String nombreMunicipio;
    private int numBilletes;
    private ConnectionClass connectionClass;
    private String saldoYDescuento;
    private String numtarjeta;
    private String estado;

    public CardsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cards, container, false);
        connectionClass = new ConnectionClass(getContext());
        if (getArguments() != null) {
            bs = getArguments().getDouble("pagar");
            horaSalida = getArguments().getString("salida");
            destino = getArguments().getInt("destino");
            numBilletes = getArguments().getInt("billetes");
            new getNombreMunicipioTask().execute();
            //System.out.println(bs);
        }
        swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        cardItemList = new ArrayList<CardItem>();
        new getTarjetasBusTask().execute();
        recyclerView = view.findViewById(R.id.recycler_view_cards);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new getTarjetasBusTask().execute();
                try {
                    recyclerView.setHasFixedSize(true);
                    layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);
                    Thread.sleep(1000);
                    swipeRefreshLayout.setRefreshing(false);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                viewHolder.getAdapterPosition();
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.deleteCard)
                        .setMessage(R.string.textDeleteCard)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new borrarTarjetaTask(viewHolder.getAdapterPosition()).execute();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //dialog.cancel();
                                adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                            }
                        })
                        .show();
            }
        }).attachToRecyclerView(recyclerView);

        return view;
    }

    private void buildRecycler() {
        adapter = new CardsAdapter(cardItemList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new CardsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                new getTarjetaTask(position).execute();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (login) {
            menu.clear();
            inflater.inflate(R.menu.add_menu, menu);
            super.onCreateOptionsMenu(menu, inflater);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_add_card:
                Intent intent = new Intent(getContext(), AddCardActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDialog() {
        CardDialog cardDialog = new CardDialog();
        cardDialog.setBs(bs);
        cardDialog.setMunicipio(nombreMunicipio);
        cardDialog.setHoraSalida(horaSalida);
        cardDialog.setNumBilletes(numBilletes);
        String[] split = saldoYDescuento.split("/");
        cardDialog.setSaldo(Double.parseDouble(split[0]));
        cardDialog.setDescuento(Double.parseDouble(split[1]));
        cardDialog.setNumtarjeta(numtarjeta);
        cardDialog.show(getFragmentManager(), "Card Dialog");
    }

    class getNombreMunicipioTask extends AsyncTask<Void, Void, Void> {
        Socket cliente;
        ObjectOutputStream outputStream;
        ObjectInputStream inputStream;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                cliente = new Socket(connectionClass.getConnection().get(0).getAddress(), connectionClass.getConnection().get(0).getPort());
                outputStream = new ObjectOutputStream(cliente.getOutputStream());
                inputStream = new ObjectInputStream(cliente.getInputStream());

                outputStream.writeUTF("nombre_municipio");
                outputStream.flush();
                outputStream.reset();

                outputStream.writeInt(destino);
                outputStream.flush();
                outputStream.reset();

                nombreMunicipio = inputStream.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class getTarjetasBusTask extends AsyncTask<Void, Void, Void> {
        Socket cliente;
        ObjectOutputStream outputStream;
        ObjectInputStream inputStream;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                cliente = new Socket(connectionClass.getConnection().get(0).getAddress(), connectionClass.getConnection().get(0).getPort());
                outputStream = new ObjectOutputStream(cliente.getOutputStream());
                inputStream = new ObjectInputStream(cliente.getInputStream());

                cardItemList.clear();
                outputStream.writeUTF("tarjetasb");
                outputStream.flush();
                outputStream.reset();

                outputStream.writeInt(usuario.getId());
                outputStream.flush();
                outputStream.reset();

                size = inputStream.readInt();
                for (int i = 0; i < size; i++) {
                    String datos;
                    datos = inputStream.readUTF();
                    newDatos = datos.split("/");
                    cardItemList.add(new CardItem(newDatos[0], newDatos[newDatos.length - 1]));
                }
            } catch (IOException e) {
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

        public borrarTarjetaTask(int position) {
            this.position = position;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                cliente = new Socket(connectionClass.getConnection().get(0).getAddress(), connectionClass.getConnection().get(0).getPort());
                outputStream = new ObjectOutputStream(cliente.getOutputStream());
                inputStream = new ObjectInputStream(cliente.getInputStream());

                outputStream.writeUTF("btarjetaBus");
                outputStream.flush();
                outputStream.reset();

                outputStream.writeInt(position);
                outputStream.flush();
                outputStream.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class getTarjetaTask extends AsyncTask<Void, Void, Void> {
        Socket cliente;
        ObjectOutputStream outputStream;
        ObjectInputStream inputStream;

        private int position;

        public getTarjetaTask(int position) {
            this.position = position;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                cliente = new Socket(connectionClass.getConnection().get(0).getAddress(), connectionClass.getConnection().get(0).getPort());
                outputStream = new ObjectOutputStream(cliente.getOutputStream());
                inputStream = new ObjectInputStream(cliente.getInputStream());

                outputStream.writeUTF("tarjeta");
                outputStream.flush();
                outputStream.reset();
                //textView = view.findViewById(R.id.text_view_number_card);
                outputStream.writeUTF(cardItemList.get(position).getTextNumber());
                outputStream.flush();

                numtarjeta = inputStream.readUTF();
                saldoYDescuento = inputStream.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            showDialog();
        }
    }
}
