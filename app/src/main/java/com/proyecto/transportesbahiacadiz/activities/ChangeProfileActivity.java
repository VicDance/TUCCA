package com.proyecto.transportesbahiacadiz.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.proyecto.transportesbahiacadiz.R;
import com.proyecto.transportesbahiacadiz.model.Usuario;
import com.proyecto.transportesbahiacadiz.util.ConnectionClass;
import com.tapadoo.alerter.Alerter;
import com.tapadoo.alerter.OnHideAlertListener;
import com.tapadoo.alerter.OnShowAlertListener;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static com.proyecto.transportesbahiacadiz.activities.RegisterActivity.usuario;

public class ChangeProfileActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 300;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    public static final int GALLERY = 200;
    public static final int PICK_IMAGE = 10;

    private ImageView imageViewProfile;
    private ImageView imageViewGallery;
    private ImageView imageViewCamera;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextEmail;
    private Button buttonCancel;
    private Button buttonAccept;

    private ConnectionClass connectionClass;

    private serializable.Usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 1);
        byte[] imagen = intent.getByteArrayExtra("imagen");

        connectionClass = new ConnectionClass(this);

        imageViewProfile = findViewById(R.id.image_view_profile);
        imageViewGallery = findViewById(R.id.image_change_gallery);
        imageViewCamera = findViewById(R.id.image_change_camera);
        editTextUsername = findViewById(R.id.edit_text_change_username);
        editTextPassword = findViewById(R.id.edit_text_change_password);
        editTextEmail = findViewById(R.id.edit_text_change_email);
        buttonCancel = findViewById(R.id.button_cancel_change);
        buttonAccept = findViewById(R.id.button_accept_change);

        usuario = new Usuario();
        usuario.setImagen(imagen);
        usuario.setId(id);
        cambiarImagen(usuario);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeProfileActivity.this.finish();
            }
        });

        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextUsername.getText().toString().isEmpty() || editTextPassword.getText().toString().isEmpty() ||
                        editTextEmail.getText().toString().isEmpty()){
                    new AlertDialog.Builder(ChangeProfileActivity.this)
                            .setTitle(getString(R.string.empty))
                            .setMessage(getString(R.string.empty_message))
                            .show();
                }else if(editTextUsername.getText().toString().length() > 25){
                    new AlertDialog.Builder(ChangeProfileActivity.this)
                            .setTitle(getString(R.string.username_long))
                            .setMessage(getString(R.string.username_long_message))
                            .show();
                }else{
                    usuario.setNombre(editTextUsername.getText().toString());
                    usuario.setContraseña(editTextPassword.getText().toString());
                    usuario.setCorreo(editTextEmail.getText().toString());

                    new updateUserTask().execute();

                    Alerter.create(ChangeProfileActivity.this)
                            .setTitle("Aplicando cambios...")
                            .setText("Vuelve a iniciar sesion para aplicar los cambios")
                            .setIcon(R.drawable.alerter_ic_notifications)
                            .setBackgroundColorRes(R.color.alerter_login)
                            .setDuration(1550)
                            .enableSwipeToDismiss()
                            .enableProgress(true)
                            .setProgressColorRes(R.color.colorPrimary)
                            .setOnHideListener(new OnHideAlertListener() {
                                @Override
                                public void onHide() {
                                    ChangeProfileActivity.this.finish();
                                    Intent intent = new Intent(ChangeProfileActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .show();
                }
            }
        });

        imageViewCamera.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });

        imageViewGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });
    }

    private void cambiarImagen(Usuario usuario) {
        try {
            if (usuario.getImagen().equals("null") || usuario.getImagen().toString().length() == 0 || usuario.getImagen().equals("imagen")) {
                imageViewProfile.setImageResource(R.drawable.ic_person_loggin);
            } else {
                byte[] blob = usuario.getImagen();
                ByteArrayInputStream bais = new ByteArrayInputStream(blob);
                Bitmap photo = BitmapFactory.decodeStream(bais);
                imageViewProfile.setImageBitmap(photo);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }else if(requestCode == GALLERY) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            } else {
                Toast.makeText(this, "gallery permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Bitmap ScaledBmp = Bitmap.createScaledBitmap(photo, 400,450, true);
                imageViewProfile.setImageBitmap(ScaledBmp);

                ByteArrayOutputStream baos = new ByteArrayOutputStream(20480);
                ScaledBmp.compress(Bitmap.CompressFormat.PNG, 0 , baos);
                byte[] blob = baos.toByteArray();
                usuario.setImagen(blob);

            }else if(requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
                InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(data.getData());
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);

                Bitmap ScaledBmp = Bitmap.createScaledBitmap(bmp, 400,450, true);
                imageViewProfile.setImageBitmap(ScaledBmp);

                ByteArrayOutputStream baos = new ByteArrayOutputStream(20480);
                ScaledBmp.compress(Bitmap.CompressFormat.PNG, 0 , baos);
                byte[] blob = baos.toByteArray();
                usuario.setImagen(blob);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class updateUserTask extends AsyncTask<Void, Void, Void> {
        Socket cliente;
        ObjectOutputStream outputStream;
        ObjectInputStream inputStream;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                cliente = new Socket(connectionClass.getConnection().get(0).getAddress(), connectionClass.getConnection().get(0).getPort());
                outputStream = new ObjectOutputStream(cliente.getOutputStream());
                inputStream = new ObjectInputStream(cliente.getInputStream());

                outputStream.writeUTF("actualiza_usuario");
                outputStream.flush();
                outputStream.reset();

                user = new serializable.Usuario(usuario.getId(), usuario.getNombre(), usuario.getContraseña(), usuario.getCorreo(), usuario.getImagen());
                outputStream.writeObject(user);
                outputStream.flush();
                outputStream.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
