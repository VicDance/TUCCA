package com.proyecto.transportesbahiacadiz.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.proyecto.transportesbahiacadiz.BuildConfig;
import com.proyecto.transportesbahiacadiz.R;

public class SettingsFragment extends Fragment {
    private View view;
    private Button buttonMail;

    private static int MAIL = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        buttonMail = view.findViewById(R.id.button_contact_admin);
        buttonMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText subject = view.findViewById(R.id.edit_text_subject);
                EditText message = view.findViewById(R.id.edit_text_message);

                String[] TO = {"mvictoria.29397@gmail.com"};

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto"));
                emailIntent.setType("text/plain");

                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject.getText().toString());
                emailIntent.putExtra(Intent.EXTRA_TEXT, message.getText().toString());
                //if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(Intent.createChooser(emailIntent, getString(R.string.select_application)), MAIL);
                //}

            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == MAIL && resultCode == Activity.RESULT_OK) {
            Toast.makeText(getContext(), getString(R.string.email_success), Toast.LENGTH_LONG).show();
        }
    }
}
