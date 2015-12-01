package com.example.maciek.beacony.helpers;

/**
 * Created by maciek on 2015-12-01.
 */
public class Settings {
    String serviceUrl;
    boolean vibrations;
    boolean sounds;

    public Settings(String serviceUrl, boolean vibrations, boolean sounds) {
        this.serviceUrl = serviceUrl;
        this.vibrations = vibrations;
        this.sounds = sounds;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public boolean isVibrations() {
        return vibrations;
    }

    public void setVibrations(boolean vibrations) {
        this.vibrations = vibrations;
    }

    public boolean isSounds() {
        return sounds;
    }

    public void setSounds(boolean sounds) {
        this.sounds = sounds;
    }
}
