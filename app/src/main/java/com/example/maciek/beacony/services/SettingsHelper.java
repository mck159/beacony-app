package com.example.maciek.beacony.services;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by maciek on 2015-12-01.
 */
public class SettingsHelper {
    public static Settings loadSettings(Context ctx) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("beaconsApp", 0);
        String serviceUrl = sharedPreferences.getString("serviceUrl", null);
        boolean sounds = sharedPreferences.getBoolean("sounds", false);
        boolean vibrations = sharedPreferences.getBoolean("vibrations", false);
        if(serviceUrl == null || serviceUrl.length() == 0) {
            serviceUrl = "http://127.0.0.1:3000";
            sharedPreferences.edit().putString("serviceUrl", serviceUrl).commit();
        }
        return new Settings(serviceUrl, vibrations, sounds);
    }
}
