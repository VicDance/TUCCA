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
import android.media.MediaScannerConnection;
import android.net.Uri;
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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;

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
    private String tfno;
    private String fecha_nac;
    private String[] newDatos;
    private int id;
    String path;
    File imagen;

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    public static final int PICK_IMAGE = 1;
    public static final int GALLERY = 200;

    private final String CARPETA_RAIZ = "transportesCadizProfilePictures/";
    private final String RUTA_IMAGEN = CARPETA_RAIZ + "profile";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_me, container, false);
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
        //textViewName = view.findViewById(R.id.text_view_name);

        //try {
        if (login) {
            setUser();
        } else {
            textViewNoUser = view.findViewById(R.id.text_view_no_user);
            textViewNoUser.setText(R.string.no_user);
            disableUser();
        }

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login = false;
                Intent intent = new Intent(getContext(), MenuActivity.class);
                startActivity(intent);
                //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoginFragment()).commit();
            }
        });

        imageViewGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);*/
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        imageViewCamera.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (getContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        || (getContext().checkSelfPermission(WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA, WRITE_EXTERNAL_STORAGE}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    File file = new File(Environment.getExternalStorageDirectory(), RUTA_IMAGEN);
                    boolean creada = file.exists();
                    String nombre = "";
                    if (creada == false) {
                        creada = file.mkdirs();
                    } else if(creada){
                        //creada = file.mkdirs();
                        nombre = usuario.getNombre() + ".jpg";
                    }
                    path = Environment.getExternalStorageDirectory() + File.separator + RUTA_IMAGEN + File.separator + nombre;
                    imagen = new File(path);
                    //String path = Environment.getDataDirectory() + File.separator +  + File.separator
                    //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Uri photoURI = FileProvider.getUriForFile(getContext(),
                            BuildConfig.APPLICATION_ID + ".provider", imagen);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagen));
                    //ImageDialog.this.dismiss();
                }
                //File file = new File(Environment.getExternalStorageDirectory());
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
                /*if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N)
                {
                    String authorities=getContext().getApplicationContext().getPackageName()+".provider";
                    Uri imageUri= FileProvider.getUriForFile(getContext(),authorities,imagen);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    //cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }else
                {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagen));
                    //cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }*/
                //String authorities=getContext().getApplicationContext().getPackageName()+".provider";
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        BuildConfig.APPLICATION_ID + ".provider", imagen);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                //Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                /*cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);*/
            } else {
                Toast.makeText(getContext(), "camera permission denied", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == GALLERY) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                /*Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);*/
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
                                Log.i("Ruta de almacenamiento", "Path: "+ path);
                            }
                        });
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                Bitmap scaledBmp = Bitmap.createScaledBitmap(bitmap, 400, 450, true);
                imageViewUser.setImageBitmap(scaledBmp);
                usuario.setImagen(path);
                //System.out.println("Path " + path);
                /*Bitmap photo = (Bitmap) data.getExtras().get("data");
                scaledBmp = Bitmap.createScaledBitmap(photo, 400, 450, true);
                imageViewUser.setImageBitmap(scaledBmp);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byteArray = stream.toByteArray();

                encodedImage = Base64.encodeToString(byteArray, Base64.NO_WRAP);
                usuario.setImagen(encodedImage);*/
                /*System.out.println(resultCode);
                Uri path = data.getData();
                System.out.println(path);
                imageViewUser.setImageURI(path);*/
                /*Uri path = data.getData();
                imageViewUser.setImageURI(path);
                usuario.setImagen(path.getPath());*/
                //System.out.println(usuario.getImagen());
            } else if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
                /*Bitmap photo = (Bitmap) data.getExtras().get("data");
                System.out.println(photo);
                scaledBmp = Bitmap.createScaledBitmap(photo, 400, 450, true);
                imageViewUser.setImageBitmap(scaledBmp);*/

                /*ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byteArray = stream.toByteArray();

                encodedImage = Base64.encodeToString(byteArray, Base64.NO_WRAP);*/
                //usuario.setImagen(encodedImage);
                /*InputStream inputStream = getActivity().getApplicationContext().getContentResolver().openInputStream(data.getData());
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);

                scaledBmp = Bitmap.createScaledBitmap(bmp, 400, 450, true);
                imageViewUser.setImageBitmap(scaledBmp);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                scaledBmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byteArray = stream.toByteArray();

                encodedImage = Base64.encodeToString(byteArray, Base64.NO_WRAP);
                usuario.setImagen(encodedImage);*/
                Uri path = data.getData();
                imageViewUser.setImageURI(path);
                usuario.setImagen(path.getPath());
                //System.out.println("Path: " + path);
            }

            //System.out.println("sale2");
            dataOut.writeUTF("i_imagen");
            dataOut.flush();
            //System.out.println(usuario.getImagen());
            dataOut.writeUTF(usuario.getNombre() + "¬" + usuario.getImagen());
            dataOut.flush();
            String estado = dataIn.readUTF();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cambiarImagen(Usuario usuario) {
        try {
            if (usuario.getImagen() == null || usuario.getImagen().length() == 0) {
                imageViewUser.setImageResource(R.drawable.ic_person_loggin);
            } else {
                /*byte[] imageByte = Base64.decode(usuario.getImagen(), Base64.NO_WRAP);
                ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
                Bitmap photo = BitmapFactory.decodeStream(bis);
                imageViewUser.setImageBitmap(photo);*/
                System.out.println(usuario.getImagen());
                byte[] imageByte = Base64.decode(usuario.getImagen(), Base64.NO_WRAP);
                ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
                Bitmap photo = BitmapFactory.decodeStream(bis);
                imageViewUser.setImageBitmap(photo);
                /*Uri uri = Uri.parse(usuario.getImagen());
                imageViewUser.setImageURI(uri);*/
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void disableUser() {
        //try {
        btnExit.setEnabled(false);
        btnChangePassword.setVisibility(View.INVISIBLE);
        textViewUser = view.findViewById(R.id.text_view_user_user);
        textViewUser.setVisibility(View.INVISIBLE);
        textViewBorn = view.findViewById(R.id.text_view_date_date);
        textViewBorn.setVisibility(View.INVISIBLE);
        textViewEmail = view.findViewById(R.id.text_view_email_email);
        textViewEmail.setVisibility(View.INVISIBLE);
        textViewPhone = view.findViewById(R.id.text_view_phone_phone);
        textViewPhone.setVisibility(View.INVISIBLE);
        /*}catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    private void setUser() {
        String datos = null;
        try {
            dataOut.writeUTF("cliente");
            dataOut.flush();
            dataOut.writeUTF(getDatos(getContext()));
            dataOut.flush();
            datos = dataIn.readUTF();
            newDatos = datos.split("¬");
            usuario = new Usuario();
            if (newDatos.length == 6) {
                System.out.println("entra");
                usuario.setNombre(newDatos[0]);
                usuario.setCorreo(newDatos[2]);
                usuario.setFecha_nac(newDatos[3]);
                usuario.setTfno(Integer.parseInt(newDatos[4]));
                usuario.setImagen(newDatos[5]);
                //System.out.println(newDatos[5]);
                /*Uri uri = Uri.parse(usuario.getImagen());
                imageViewUser.setImageURI(uri);*/
                //ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(usuario.getFoto());
                //Bitmap bitmap = BitmapFactory.decodeFile(usuario.getImagen());
                //System.out.println(bitmap);
                /*Bitmap scaledBmp = Bitmap.createScaledBitmap(bitmap, 400, 450, true);
                imageViewUser.setImageBitmap(scaledBmp);*/
                /*usuario.setImagen(newDatos[5]);
                Uri uri = Uri.parse(usuario.getImagen());
                imageViewUser.setImageURI(uri);*/
            } else {
                usuario.setNombre(newDatos[0]);
                usuario.setCorreo(newDatos[2]);
                usuario.setFecha_nac(newDatos[3]);
                usuario.setTfno(Integer.parseInt(newDatos[4]));
            }
            nombre = newDatos[0];
            contraseña = newDatos[1];
            correo = newDatos[2];
            fecha_nac = newDatos[3];
            tfno = newDatos[4];

            textViewUser.setText(nombre);
            textViewBorn.setText(fecha_nac);
            textViewEmail.setText(correo);
            textViewPhone.setText(tfno);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
