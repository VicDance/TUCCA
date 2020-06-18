package com.proyecto.transportesbahiacadiz.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.proyecto.transportesbahiacadiz.R;
import com.proyecto.transportesbahiacadiz.fragments.CardsFragment;
import com.proyecto.transportesbahiacadiz.fragments.GapAndFareFragment;
import com.proyecto.transportesbahiacadiz.fragments.MainFragment;
import com.proyecto.transportesbahiacadiz.fragments.MeFragment;
import com.proyecto.transportesbahiacadiz.fragments.NewsFragment;
import com.proyecto.transportesbahiacadiz.fragments.PlacesFragment;
import com.proyecto.transportesbahiacadiz.fragments.SalePointFragment;
import com.proyecto.transportesbahiacadiz.fragments.SettingsFragment;
import com.proyecto.transportesbahiacadiz.fragments.TripFragment;
import com.proyecto.transportesbahiacadiz.model.Usuario;
import com.proyecto.transportesbahiacadiz.util.Connection;
import com.proyecto.transportesbahiacadiz.util.ConnectionClass;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import static com.proyecto.transportesbahiacadiz.activities.MainActivity.getDatos;
import static com.proyecto.transportesbahiacadiz.activities.MainActivity.login;
import static com.proyecto.transportesbahiacadiz.activities.RegisterActivity.usuario;

public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    public static final String STRING_PREFERENCES = "fragments";
    public static final String PREFERENCE_STATUS = "estado.button.sesion";
    private DrawerLayout drawerLayout;
    private ImageView userImage;
    private int id;
    private ConnectionClass connectionClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        connectionClass = new ConnectionClass(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        String newString;
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
        if(!login) {
            navigationView.getMenu().removeItem(R.id.nav_cards);
            navigationView.getMenu().removeItem(R.id.nav_log);
        }else{
            navigationView.getMenu().removeItem(R.id.nav_sign_up);
            Bundle extras = getIntent().getExtras();
            //String newString;
            if(extras == null) {
                newString= null;
            } else {
                newString= extras.getString("nombre");
                id = extras.getInt("id");
                if (newString == null || id == 0){
                    double bs = extras.getDouble("pagar");
                    String hora_salida = extras.getString("salida");
                    int destino = extras.getInt("destino");
                    int billetes = extras.getInt("billetes");
                    //newString= extras.getString("pagar");
                    Bundle bundle = new Bundle();
                    bundle.putDouble("pagar", bs);
                    bundle.putString("salida", hora_salida);
                    bundle.putInt("destino", destino);
                    bundle.putInt("billetes", billetes);
                    CardsFragment cardsFragment = new CardsFragment();
                    cardsFragment.setArguments(bundle);
                    new TaskCambiarFragment().execute(cardsFragment);
                }
                //System.out.println(newString);
                View headView = navigationView.getHeaderView(0);
                TextView textView = headView.findViewById(R.id.text_view_name);
                userImage = headView.findViewById(R.id.user_image);
                getUser();
                textView.setText(newString);
            }
        }
    }

    private void getUser(){
        getUserTask getUserTask = new getUserTask();
        getUserTask.execute();
    }

    public void cambiaImagen(Usuario usuario) {
        try {
            if (usuario.getImagen().toString().equals("null") || usuario.getImagen().toString().length() == 0 || usuario.getImagen() == null ||
                    usuario.getImagen().toString().equalsIgnoreCase("imagen")) {
                userImage.setImageResource(R.drawable.ic_person_loggin);
            } else {
                byte[] blob = usuario.getImagen();
                ByteArrayInputStream bais = new ByteArrayInputStream(blob);
                Bitmap photo = BitmapFactory.decodeStream(bais);
                userImage.setImageBitmap(photo);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_home:
                new TaskCambiarFragment().execute(new MainFragment());
                //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment()).commit();
                break;
            case R.id.nav_trip:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TripFragment()).commit();
                break;
            case R.id.nav_cards:
                new TaskCambiarFragment().execute(new CardsFragment());
                //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CardsFragment()).commit();
                break;
            case R.id.nav_sales:
                new TaskCambiarFragment().execute(new SalePointFragment());
                //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SalePointFragment()).commit();
                break;
            case R.id.nav_log:
                new TaskCambiarFragment().execute(new MeFragment());
                break;
            case R.id.nav_gap_fare:
                new TaskCambiarFragment().execute(new GapAndFareFragment());
                break;
            case R.id.nav_sign_up:
                startActivity(new Intent(MenuActivity.this, MainActivity.class));
                break;
            case R.id.nav_news:
                new TaskCambiarFragment().execute(new NewsFragment());
                break;
            case R.id.nav_places:
                new TaskCambiarFragment().execute(new PlacesFragment());
                break;
            case R.id.nav_settings:
                new TaskCambiarFragment().execute(new SettingsFragment());
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    class TaskCambiarFragment extends AsyncTask<Fragment, Void, String> {

        @Override
        protected String doInBackground(Fragment... fragments) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragments[0]).commit();
            return fragments[0].toString();
        }
    }

    class getUserTask extends AsyncTask<Void, Void, Void>{
        Socket cliente = null;
        ObjectOutputStream outputStream;
        ObjectInputStream inputStream;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                cliente = new Socket(connectionClass.getConnection().get(0).getAddress(), connectionClass.getConnection().get(0).getPort());
                outputStream = new ObjectOutputStream(cliente.getOutputStream());
                inputStream = new ObjectInputStream(cliente.getInputStream());

                outputStream.writeUTF("cliente");
                outputStream.flush();
                outputStream.reset();

                outputStream.writeUTF(getDatos(MenuActivity.this));
                outputStream.flush();
                outputStream.reset();
                serializable.Usuario us= (serializable.Usuario) inputStream.readObject();
                //System.out.println(us);
                usuario = new Usuario();
                usuario.setId(us.getId());
                usuario.setNombre(us.getNombre());
                usuario.setCorreo(us.getCorreo());
                usuario.setFecha_nac(us.getFecha_nac().toString());
                usuario.setTfno(us.getTfno());
                usuario.setImagen(us.getImagen());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            cambiaImagen(usuario);
        }
    }
}
