package com.example.maciek.beacony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.maciek.beacony.dto.ContentDTO;
import com.example.maciek.beacony.services.NotificationHelper;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by maciek on 2015-12-01.
 */
public class NotificationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent intent) {
        ArrayList<ContentDTO> contents = (ArrayList<ContentDTO>) intent.getSerializableExtra("contents");
        if(contents.size() == 0) {
            return;
        }
        ContentDTO lastContent = (ContentDTO) intent.getSerializableExtra("lastContent");
        if(lastContent != null) {
            contents.remove(lastContent);
        }
        NotificationHelper.createNotificationWithDeleteListener(ctx, "DUPA", "DUPA", contents, lastContent);
    }
}
