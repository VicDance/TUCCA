package com.proyecto.tucca.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.proyecto.tucca.dialogs.LocationDialog;
import com.proyecto.tucca.R;
import com.proyecto.tucca.model.Zone;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.proyecto.tucca.activities.MainActivity.dataIn;
import static com.proyecto.tucca.activities.MainActivity.dataOut;

/*import static com.proyecto.tucca.activities.MainActivity.cliente;
import static com.proyecto.tucca.activities.MainActivity.dataIn;
import static com.proyecto.tucca.activities.MainActivity.dataOut;*/

public class MainFragment extends Fragment {
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    private TextView zone;
    private int size;
    private Zone[] zonas;
    private String[] nombreZonas;
    private String[] newDatos;
    //public static Socket cliente;
    //public static DataOutputStream dataOut;
    //public static DataInputStream dataIn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        zone = view.findViewById(R.id.text_view_zone);
        try {
            dataOut.writeUTF("zonas");
            dataOut.flush();
            //System.out.println(dataIn.readUTF());
            size = dataIn.readInt();
            //System.out.println(size);
            zonas = new Zone[size];
            for (int i = 0; i < size; i++) {
                String datos;
                try {
                    datos = dataIn.readUTF();
                    newDatos = datos.split("/");
                    Zone zona = new Zone(newDatos[0], newDatos[1]);
                    zonas[i] = zona;
                } catch (IOException ex) {
                    Logger.getLogger(TripFragment.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String content = "";
            for(int i = 0; i < zonas.length; i++){
                content += zonas[i].getIdZona() + ": " + zonas[i].getNombreZona() + "\n";
            }
            zone.append(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return view;
    }

    /*private void conectar(){
        final int PUERTO = 6000;
        final String HOST = "192.168.1.13";
        //"localhost";
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            try {
                cliente = new Socket(HOST, PUERTO);
                dataOut = new DataOutputStream(cliente.getOutputStream());
                dataIn = new DataInputStream(cliente.getInputStream());
            } catch (IOException ex) {
                Logger.getLogger(LoginFragment.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search_menu, menu);
        inflater.inflate(R.menu.location_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setQueryHint("Introduzca nombre o código de parada");
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.i("onQueryTextChange", newText);

                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.i("onQueryTextSubmit", query);

                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        searchView.setOnQueryTextListener(queryTextListener);
        switch (item.getItemId()) {
            case R.id.action_search:
                // Not implemented here
                return false;
            case R.id.action_location:
                item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        showDialog();
                        return true;
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        //searchView.setOnQueryTextListener(queryTextListener);
        //return super.onOptionsItemSelected(item);
    }

    private void showDialog() {
        new LocationDialog().show(getFragmentManager(), "Location Dialog");
    }

    /*class TaskConectar extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            System.out.println("Conectando...");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final int PUERTO = 6000;
            final String HOST = "192.168.1.13";
            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT > 8)
            {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
                try {
                    System.out.println(PUERTO + " " + HOST);
                    cliente = new Socket(HOST, PUERTO);
                    System.out.println(cliente);
                    dataOut = new DataOutputStream(cliente.getOutputStream());
                    dataIn = new DataInputStream(cliente.getInputStream());
                    //System.out.println(dataIn);
                } catch (IOException ex) {
                    Logger.getLogger(LoginFragment.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
            return null;
        }
    }*/
}
