package com.example.maciek.beacony.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v7.app.NotificationCompat;

import com.example.maciek.beacony.MainActivity;
import com.example.maciek.beacony.R;
import com.example.maciek.beacony.dto.ContentDTO;

import java.util.ArrayList;

/**
 * Created by maciek on 2015-12-01.
 */
public class NotificationHelper {
    static public void createNotificationWithDeleteListener(Context ctx, String title, String text, ArrayList<ContentDTO> contents, ContentDTO lastContent) {
        Intent notifIntent = new Intent(ctx, MainActivity.class);
        Intent nextNotifIntent = new Intent(ctx, NotificationBroadcastReceiver.class);
        notifIntent.putExtra("contents", contents);
        notifIntent.putExtra("lastContent", lastContent);
        notifIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        nextNotifIntent.putExtra("contents", contents);
        nextNotifIntent.putExtra("lastContent", lastContent);
        nextNotifIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, notifIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(ctx, 0, nextNotifIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mNotifyBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(ctx)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ikonka)
                .setDeleteIntent(nextPendingIntent)
                .setContentIntent(pendingIntent);
        Settings settings = SettingsHelper.loadSettings(ctx);
        if(settings.isSounds()) {
            mNotifyBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        }
        if(settings.isVibrations()) {
            mNotifyBuilder.setVibrate(new long[]{0, 100, 200, 300});
        }

        NotificationManager notificationManager =
                (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = mNotifyBuilder.build();
        notificationManager.notify(1, notification);
    }
}
