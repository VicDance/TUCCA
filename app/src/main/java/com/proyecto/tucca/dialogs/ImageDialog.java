package com.proyecto.tucca.dialogs;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.proyecto.tucca.R;
import com.proyecto.tucca.model.Usuario;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.proyecto.tucca.activities.MainActivity.dataIn;
import static com.proyecto.tucca.activities.MainActivity.dataOut;
import static com.proyecto.tucca.activities.RegisterActivity.usuario;

public class ImageDialog extends DialogFragment {
    private View view;
    CircleImageView circleImageView;

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    public static final int PICK_IMAGE = 1;
    public static final int GALLERY = 200;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_image, container, false);
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_image, null);
        circleImageView = view.findViewById(R.id.user_image);
        TextView existing = view.findViewById(R.id.text_view_existing_photo);
        TextView camera = view.findViewById(R.id.text_view_camera);
        existing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                //ImageDialog.this.dismiss();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (getContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    //ImageDialog.this.dismiss();
                }
            }
        });

        cambiarImagen(usuario);

        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(getContext(), "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }else if(requestCode == GALLERY) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            } else {
                Toast.makeText(getContext(), "gallery permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Bitmap ScaledBmp = Bitmap.createScaledBitmap(photo, 400,450, true);
                circleImageView.setImageBitmap(ScaledBmp);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                String encoded_image = Base64.encodeToString(byteArray, Base64.NO_WRAP);

                usuario.setImagen(encoded_image);
            }else if(requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
                InputStream inputStream = getActivity().getApplicationContext().getContentResolver().openInputStream(data.getData());
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);

                Bitmap ScaledBmp = Bitmap.createScaledBitmap(bmp, 400,450, true);
                circleImageView.setImageBitmap(ScaledBmp);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                ScaledBmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                String encoded_image = Base64.encodeToString(byteArray, Base64.NO_WRAP);

                usuario.setImagen(encoded_image);
            }

            dataOut.writeUTF("i_imagen");
            dataOut.flush();
            dataOut.writeUTF(usuario.getNombre() + "/" + usuario.getImagen());
            String estado = dataIn.readUTF();
            if(estado.equalsIgnoreCase("correcto")){
                new AlertDialog.Builder(getContext())
                        .setTitle(getString(R.string.correct))
                        .setMessage("Se ha registrado satisfactoriamente")
                        .show();
            }else{
                new AlertDialog.Builder(getContext())
                        .setTitle(getString(R.string.incorrect))
                        .setMessage("Se ha registrado satisfactoriamente")
                        .show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cambiarImagen(Usuario usuario) {
        try {
            if (usuario.getImagen().equals("null") || usuario.getImagen().length() == 0) {
                circleImageView.setImageResource(R.mipmap.ic_launcher_round);
            }else {
                byte[] imageByte = Base64.decode(usuario.getImagen(), Base64.NO_WRAP);
                ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
                Bitmap photo = BitmapFactory.decodeStream(bis);
                circleImageView.setImageBitmap(photo);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
