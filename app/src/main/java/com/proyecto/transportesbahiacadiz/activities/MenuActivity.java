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
import com.proyecto.transportesbahiacadiz.fragments.TripFragment;
import com.proyecto.transportesbahiacadiz.model.Usuario;

import java.io.IOException;

import static com.proyecto.transportesbahiacadiz.activities.MainActivity.dataIn;
import static com.proyecto.transportesbahiacadiz.activities.MainActivity.dataOut;
import static com.proyecto.transportesbahiacadiz.activities.MainActivity.getDatos;
import static com.proyecto.transportesbahiacadiz.activities.MainActivity.login;
import static com.proyecto.transportesbahiacadiz.activities.RegisterActivity.usuario;

public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    public static final String STRING_PREFERENCES = "fragments";
    public static final String PREFERENCE_STATUS = "estado.button.sesion";
    private DrawerLayout drawerLayout;
    private ImageView userImage;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

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
                    //newString= extras.getString("pagar");
                    Bundle bundle = new Bundle();
                    bundle.putDouble("pagar", bs);
                    bundle.putString("salida", hora_salida);
                    bundle.putInt("destino", destino);
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
        String datos = null;
        String newDatos[];
        try {
            dataOut.writeUTF("cliente");
            dataOut.flush();
            dataOut.writeUTF(getDatos(this));
            dataOut.flush();
            datos = dataIn.readUTF();
            newDatos = datos.split("¬");
            usuario = new Usuario();
            //System.out.println(datos);
            if(newDatos.length == 7) {
                usuario.setId(Integer.parseInt(newDatos[0]));
                usuario.setNombre(newDatos[1]);
                usuario.setCorreo(newDatos[3]);
                usuario.setFecha_nac(newDatos[4]);
                usuario.setTfno(Integer.parseInt(newDatos[5]));
                //usuario.setImagen(newDatos[6]);
                //usuario.setImagen(newDatos[5]);
            }else{
                usuario.setId(Integer.parseInt(newDatos[0]));
                usuario.setNombre(newDatos[1]);
                usuario.setCorreo(newDatos[3]);
                usuario.setFecha_nac(newDatos[4]);
                usuario.setTfno(Integer.parseInt(newDatos[5]));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Bitmap convertStringToBitmap(String url){
        byte[] encodeByte = Base64.decode(usuario.getImagen(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        return bitmap;
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
                try {
                    dataOut.writeUTF("lugares_interes");
                    dataOut.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                new TaskCambiarFragment().execute(new PlacesFragment());
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
}
