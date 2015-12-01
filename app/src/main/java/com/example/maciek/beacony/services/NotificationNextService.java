package com.example.maciek.beacony.services;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by maciek on 2015-12-01.
 */
public class NotificationNextService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public NotificationNextService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Toast.makeText(getApplicationContext(), "NO CONNECTION", Toast.LENGTH_SHORT).show();
    }
}
