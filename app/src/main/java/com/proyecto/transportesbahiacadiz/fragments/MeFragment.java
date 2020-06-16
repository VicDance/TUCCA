package com.proyecto.transportesbahiacadiz.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.proyecto.transportesbahiacadiz.BuildConfig;
import com.proyecto.transportesbahiacadiz.R;
import com.proyecto.transportesbahiacadiz.activities.CreditCardActivity;
import com.proyecto.transportesbahiacadiz.activities.MenuActivity;
import com.proyecto.transportesbahiacadiz.model.Usuario;
import com.proyecto.transportesbahiacadiz.util.ConnectionClass;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.proyecto.transportesbahiacadiz.activities.MainActivity.cliente;
import static com.proyecto.transportesbahiacadiz.activities.MainActivity.dataIn;
import static com.proyecto.transportesbahiacadiz.activities.MainActivity.dataOut;
//import static com.proyecto.tucca.activities.MainActivity.guardado;
import static com.proyecto.transportesbahiacadiz.activities.MainActivity.getDatos;
import static com.proyecto.transportesbahiacadiz.activities.MainActivity.login;
import static com.proyecto.transportesbahiacadiz.activities.RegisterActivity.usuario;

public class MeFragment extends Fragment {
    private TextView textViewUser;
    private TextView textViewBorn;
    private TextView textViewEmail;
    private TextView textViewPhone;
    private Button btnExit;
    private Button btnChangePassword;
    //private Button btnChangeImage;
    private TextView textViewName;
    private TextView textViewNoUser;
    private ImageView imageViewUser;
    private ImageView imageViewGallery;
    private ImageView imageViewCamera;
    //public static User user;
    private View view;
    private String nombre;
    private String contraseña;
    private String correo;
    private int tfno;
    private String fecha_nac;
    private String[] newDatos;
    private int id;
    private String path;
    private File imagen;
    private ConnectionClass connectionClass;
    private String estado;

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    public static final int PICK_IMAGE = 1;
    public static final int GALLERY = 200;

    private final String CARPETA_RAIZ = "transportesCadizProfilePictures/";
    private final String RUTA_IMAGEN = CARPETA_RAIZ + "profile";

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_me, container, false);

        connectionClass = new ConnectionClass(getContext());

        btnExit = view.findViewById(R.id.btn_exit);
        btnChangePassword = view.findViewById(R.id.btn_change_password);
        imageViewUser = view.findViewById(R.id.image_user);
        imageViewCamera = view.findViewById(R.id.change_image_camera);
        imageViewGallery = view.findViewById(R.id.change_image_gallery);
        //btnChangeImage = view.findViewById(R.id.btn_change_image);
        textViewUser = view.findViewById(R.id.text_view_user);
        textViewBorn = view.findViewById(R.id.text_view_born_date);
        textViewEmail = view.findViewById(R.id.text_view_email);
        textViewPhone = view.findViewById(R.id.text_view_phone);

        setUser();

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login = false;
                Intent intent = new Intent(getContext(), MenuActivity.class);
                startActivity(intent);
            }
        });

        imageViewGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        imageViewCamera.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (getContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        || (getContext().checkSelfPermission(WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA, WRITE_EXTERNAL_STORAGE}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    File file = new File(Environment.getExternalStorageDirectory(), RUTA_IMAGEN);
                    boolean creada = file.exists();
                    String nombre = "";
                    if (creada == false) {
                        creada = file.mkdirs();
                    } else if (creada) {
                        //creada = file.mkdirs();
                        nombre = usuario.getNombre() + ".jpg";
                    }
                    path = Environment.getExternalStorageDirectory() + File.separator + RUTA_IMAGEN + File.separator + nombre;
                    imagen = new File(path);
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Uri photoURI = FileProvider.getUriForFile(getContext(),
                            BuildConfig.APPLICATION_ID + ".provider", imagen);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });

        cambiarImagen(usuario);
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        BuildConfig.APPLICATION_ID + ".provider", imagen);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(getContext(), "camera permission denied", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == GALLERY) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE);
            } else {
                Toast.makeText(getContext(), "gallery permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        //Bitmap scaledBmp = null;
        String encodedImage = null;
        byte[] byteArray = null;
        try {
            if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
                //System.out.println("entra camara");
                MediaScannerConnection.scanFile(getContext(), new String[]{path}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {
                                Log.i("Ruta de almacenamiento", "Path: " + path);
                            }
                        });
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                Bitmap scaledBmp = Bitmap.createScaledBitmap(bitmap, 400, 450, true);
                imageViewUser.setImageBitmap(scaledBmp);
                usuario.setImagen(path);
            } else if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                path = imageUri.getPath();
                if (path != null) {
                    InputStream imageStream = null;
                    try {
                        imageStream = getActivity().getContentResolver().openInputStream(
                                imageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    // Transformamos la URI de la imagen a inputStream y este a un Bitmap
                    Bitmap bmp = BitmapFactory.decodeStream(imageStream);
                    imageViewUser.setImageBitmap(bmp);
                    System.out.println(path);
                    usuario.setImagen(path);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void cambiarImagen(Usuario usuario) {
        try {
            if (usuario.getImagen() == null || usuario.getImagen().length() == 0) {
                imageViewUser.setImageResource(R.drawable.ic_person_loggin);
            } else {
                System.out.println("file:" + usuario.getImagen());
                Picasso.with(getContext()).load("file:" + usuario.getImagen()).config(Bitmap.Config.RGB_565).fit().centerCrop().into(imageViewUser);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setUser() {
        new setUserTask().execute();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //menu.clear();
        //super.onCreateOptionsMenu(menu, inflater);
        if (login) {
            inflater.inflate(R.menu.add_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_add_card:
                item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent = new Intent(getContext(), CreditCardActivity.class);
                        startActivity(intent);
                        return true;
                    }
                });
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class setUserTask extends AsyncTask<Void, Void, Void>{
        Socket cliente;
        DataInputStream dataIn;
        DataOutputStream dataOut;

        @Override
        protected Void doInBackground(Void... voids) {
            String datos = null;
            try {
                cliente = new Socket(connectionClass.getConnection().get(0).getAddress(), connectionClass.getConnection().get(0).getPort());
                dataIn = new DataInputStream(cliente.getInputStream());
                dataOut = new DataOutputStream(cliente.getOutputStream());

                dataOut.writeUTF("cliente");
                dataOut.flush();
                dataOut.writeUTF(getDatos(getContext()));
                dataOut.flush();
                datos = dataIn.readUTF();
                newDatos = datos.split("¬");
                usuario = new Usuario();
                if (newDatos.length == 7) {
                    //System.out.println("entra");
                    usuario.setId(Integer.parseInt(newDatos[0]));
                    usuario.setNombre(newDatos[1]);
                    usuario.setCorreo(newDatos[3]);
                    usuario.setFecha_nac(newDatos[4]);
                    usuario.setTfno(Integer.parseInt(newDatos[5]));
                    usuario.setImagen(newDatos[6]);
                } else {
                    usuario.setId(Integer.parseInt(newDatos[0]));
                    usuario.setNombre(newDatos[1]);
                    usuario.setCorreo(newDatos[3]);
                    usuario.setFecha_nac(newDatos[4]);
                    usuario.setTfno(Integer.parseInt(newDatos[5]));
                }
                nombre = usuario.getNombre();
                contraseña = newDatos[1];
                correo = usuario.getCorreo();
                fecha_nac = usuario.getFecha_nac();
                tfno = usuario.getTfno();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            textViewUser.setText(nombre);
            String[] split_fecha = fecha_nac.split("-");
            textViewBorn.setText(split_fecha[2] + "-" + split_fecha[1] + "-" + split_fecha[0]);
            textViewEmail.setText(correo);
            textViewPhone.setText(tfno + "");
        }
    }

    class insertarImagenTask extends AsyncTask<Void, Void, Void>{
        Socket cliente;
        DataInputStream dataIn;
        DataOutputStream dataOut;

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                cliente = new Socket(connectionClass.getConnection().get(0).getAddress(), connectionClass.getConnection().get(0).getPort());
                dataIn = new DataInputStream(cliente.getInputStream());
                dataOut = new DataOutputStream(cliente.getOutputStream());

                dataOut.writeUTF("i_imagen");
                dataOut.flush();
                //System.out.println(usuario.getImagen());
                dataOut.writeUTF(usuario.getNombre() + "¬" + usuario.getImagen());
                dataOut.flush();
                estado = dataIn.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (estado.equalsIgnoreCase("correcto")) {
                new AlertDialog.Builder(getContext())
                        .setTitle(getString(R.string.correct))
                        .setMessage("Se ha registrado satisfactoriamente")
                        .show();
            } else {
                new AlertDialog.Builder(getContext())
                        .setTitle(getString(R.string.incorrect))
                        .setMessage("No se ha registrar")
                        .show();
            }
        }
    }
}
