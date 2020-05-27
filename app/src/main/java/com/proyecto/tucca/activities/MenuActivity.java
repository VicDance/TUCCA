package com.proyecto.tucca.activities;

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
import com.proyecto.tucca.R;
import com.proyecto.tucca.dialogs.ImageDialog;
import com.proyecto.tucca.fragments.CardsFragment;
import com.proyecto.tucca.fragments.GapAndFareFragment;
import com.proyecto.tucca.fragments.MainFragment;
import com.proyecto.tucca.fragments.MeFragment;
import com.proyecto.tucca.fragments.SalePointFragment;
import com.proyecto.tucca.fragments.TripFragment;
import com.proyecto.tucca.model.Usuario;

import static com.proyecto.tucca.activities.MainActivity.dataIn;
import static com.proyecto.tucca.activities.MainActivity.dataOut;
import static com.proyecto.tucca.activities.MainActivity.getDatos;
import static com.proyecto.tucca.activities.MainActivity.login;
import static com.proyecto.tucca.activities.RegisterActivity.usuario;

public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    public static final String STRING_PREFERENCES = "fragments";
    public static final String PREFERENCE_STATUS = "estado.button.sesion";
    private DrawerLayout drawerLayout;
    private ImageView userImage;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    public static final int PICK_IMAGE = 1;
    public static final int GALLERY = 200;

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
                System.out.println(newString);
                View headView = navigationView.getHeaderView(0);
                TextView textView = headView.findViewById(R.id.text_view_name);
                userImage = headView.findViewById(R.id.user_image);
                getUser();
                System.out.println(usuario);
                //System.out.println(user.getImagen().length());
                /*if(user.getImagen().length() != 0 || !user.getImagen().equals("")){
                    userImage.setImageBitmap(convertStringToBitmap(user.getImagen()));
                }*/
                /*userImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog();
                    }
                });*/
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
            newDatos = datos.split("/");
            usuario = new Usuario();
            if(datos.length() == 6) {
                usuario.setNombre(newDatos[0]);
                usuario.setCorreo(newDatos[2]);
                usuario.setFecha_nac(newDatos[3]);
                usuario.setTfno(Integer.parseInt(newDatos[4]));
                usuario.setImagen(newDatos[5]);
            }else{
                usuario.setNombre(newDatos[0]);
                usuario.setCorreo(newDatos[2]);
                usuario.setFecha_nac(newDatos[3]);
                usuario.setTfno(Integer.parseInt(newDatos[4]));
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

    private void showDialog(){
        ImageDialog imageDialog = new ImageDialog();
        imageDialog.show(getSupportFragmentManager(), "Image Dialog");
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
                /*String newString;
                Bundle extras = getIntent().getExtras();
                if(extras == null) {
                    newString= null;
                } else {
                    newString = extras.getString("nombre");
                    System.out.println(newString);
                }*/
                break;
            case R.id.nav_gap_fare:
                new TaskCambiarFragment().execute(new GapAndFareFragment());
                break;
            case R.id.nav_sign_up:
                startActivity(new Intent(MenuActivity.this, MainActivity.class));
                finish();
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
