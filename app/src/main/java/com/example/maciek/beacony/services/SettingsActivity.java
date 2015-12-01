package com.example.maciek.beacony.services;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.maciek.beacony.R;

public class SettingsActivity extends AppCompatActivity {
    EditText editTextUrl;
    CheckBox checkBoxVibrations;
    CheckBox checkBoxSounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        editTextUrl = (EditText) findViewById(R.id.editTextUrl);
        checkBoxVibrations = (CheckBox) findViewById(R.id.checkBoxVibrations);
        checkBoxSounds = (CheckBox) findViewById(R.id.checkBoxSounds);

        loadPreferences();
    }

    public void savePreferencesButtonClick(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("beaconsApp", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String serviceUrl = editTextUrl.getText().toString();
        if(Patterns.WEB_URL.matcher(serviceUrl).matches()) {
            editor.putString("serviceUrl", serviceUrl);
        } else {
            Toast.makeText(getApplicationContext(), "INVALID URL!", Toast.LENGTH_SHORT).show();
        }
        editor.putBoolean("sounds", checkBoxSounds.isChecked());
        editor.putBoolean("vibrations", checkBoxVibrations.isChecked());
        editor.commit();

        Toast.makeText(getApplicationContext(), "Preferences saved", Toast.LENGTH_SHORT).show();
        loadPreferences();
    }

    public void loadPreferences() {
        Settings settings = SettingsHelper.loadSettings(getApplicationContext());

        checkBoxVibrations.setChecked(settings.isVibrations());
        checkBoxSounds.setChecked(settings.isSounds());
        editTextUrl.setText(settings.getServiceUrl());
    }

}
