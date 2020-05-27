package com.proyecto.tucca.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.proyecto.tucca.dialogs.CardDialog;
import com.proyecto.tucca.model.CardItem;
import com.proyecto.tucca.adapters.CardsAdapter;
import com.proyecto.tucca.R;
import com.proyecto.tucca.activities.AddCardActivity;

import java.io.IOException;
import java.util.ArrayList;

import static com.proyecto.tucca.activities.MainActivity.dataIn;
import static com.proyecto.tucca.activities.MainActivity.dataOut;
import static com.proyecto.tucca.activities.MainActivity.login;

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

    public CardsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cards, container, false);
        swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        cardItemList = new ArrayList<CardItem>();
        try {
            dataOut.writeUTF("tarjetasb");
            dataOut.flush();
            size = dataIn.readInt();
            for (int i = 0; i < size; i++) {
                String datos;
                datos = dataIn.readUTF();
                newDatos = datos.split("/");
                cardItemList.add(new CardItem(newDatos[0], newDatos[newDatos.length - 1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        recyclerView = view.findViewById(R.id.recycler_view_cards);
        if (login) {
            buildRecycler();
        } else {
            textView = view.findViewById(R.id.text_view_no_login);
            textView.setText("Debes estar conectado para guardar tarjetas");
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    dataOut.writeUTF("tarjetasb");
                    dataOut.flush();
                    size = dataIn.readInt();
                    cardItemList.clear();
                    for (int i = 0; i < size; i++) {
                        String datos;
                        datos = dataIn.readUTF();
                        newDatos = datos.split("/");
                        cardItemList.add(new CardItem(newDatos[0], newDatos[newDatos.length - 1]));
                    }
                    buildRecycler();
                    Thread.sleep(1000);
                    swipeRefreshLayout.setRefreshing(false);
                } catch (IOException | InterruptedException e) {
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
                                try {
                                    dataOut.writeUTF("btarjetaBus");
                                    dataOut.flush();
                                    dataOut.writeInt(viewHolder.getAdapterPosition());
                                    dataOut.flush();
                                    String estado = dataIn.readUTF();
                                    if(estado.equalsIgnoreCase("correcto")){
                                        new AlertDialog.Builder(getContext())
                                                .setTitle(R.string.correct)
                                                .setMessage("Borrado existoso")
                                                .show();
                                    }
                                    cardItemList.remove(viewHolder.getAdapterPosition());
                                    adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
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
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CardsAdapter(cardItemList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new CardsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //textView = view.findViewById(R.id.text_view_number_card);
                System.out.println(cardItemList.get(position).getTextNumber());
                try {
                    dataOut.writeUTF("tarjeta");
                    dataOut.flush();
                    //textView = view.findViewById(R.id.text_view_number_card);
                    dataOut.writeUTF(cardItemList.get(position).getTextNumber());
                    dataOut.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                showDialog();
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
                item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent = new Intent(getContext(), AddCardActivity.class);
                        startActivity(intent);
                        return true;
                    }
                });
                return false;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialog() {
        new CardDialog().show(getFragmentManager(), "Card Dialog");
    }
}
