package com.example.maciek.beacony;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.example.maciek.beacony.dto.ContentDTO;
import com.example.maciek.beacony.services.ReqService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Beacon beacon = null;
    BeaconManager beaconManager;
    TextView textViewMinor;
    TextView textViewMajor;
    TextView textViewTop;
    ReqService reqService;
    WebView webView;
    ProgressBar progressBar;
    String resourceUrl;
    private NotificationManager notificationManager;

    Map<Beacon, Date> beaconsCache;

    @Override
    protected void onResume() {
        super.onResume();
        if(resourceUrl != null) {
            webView.loadUrl(resourceUrl);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //////////////////
        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        reqService = new ReqService();
        textViewMajor = (TextView) findViewById(R.id.editTextMajor);
        textViewMinor = (TextView) findViewById(R.id.editTextMinor);
        textViewTop = (TextView) findViewById(R.id.textView2);
        webView = (WebView) findViewById(R.id.webView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                Toast.makeText(getApplicationContext(), "START MONITORING", Toast.LENGTH_SHORT).show();
                beaconManager.startMonitoring(new Region(
                        "monitored region",
//                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                        null,
                        null, null));
                beaconManager.startRanging(new Region(
                        "monitored region",
//                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                        null,
                        null, null));
            }
        });
        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                Toast.makeText(getApplicationContext(), "enter", Toast.LENGTH_SHORT).show();
                beacon = list.get(0);
                new RequestAsync().execute();

            }

            @Override
            public void onExitedRegion(Region region) {
                Toast.makeText(getApplicationContext(), "exit", Toast.LENGTH_SHORT).show();
            }
        });
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
//                Toast.makeText(getApplicationContext(), "beacons discovered " + list.size(), Toast.LENGTH_SHORT).show();

                for(Beacon beacon : list) {
//                    Toast.makeText(getApplicationContext(), beacon.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void submitButtonClick(View view) {
////        new RequestAsync().execute();
////        String major = textViewMajor.getText().toString();
////        String minor = textViewMinor.getText().toString();
//        webView.loadUrl(resourceUrl);
//        Toast.makeText(getApplicationContext(), "DUPA", Toast.LENGTH_SHORT).show();
//        reqService.requestForContent("A", major, minor);
        Toast.makeText(getApplicationContext(), "enter", Toast.LENGTH_SHORT).show();
        new RequestAsync().execute();
    }

    public class RequestAsync extends AsyncTask<Void, Void, Void>{
        protected Boolean hasTimedOut = false;
        private Boolean noContent = false;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Intent notifIntent = new Intent(getApplicationContext(), MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 , notifIntent, 0);
            NotificationCompat.Builder mNotifyBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle("New Message")
                    .setContentText("You've received new messages.")
                    .setSmallIcon(R.drawable.ikonka)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentIntent(pendingIntent);

            Notification notification = mNotifyBuilder.build();
            notification.defaults = Notification.DEFAULT_ALL;
            notificationManager.notify(1, notification);

            if(resourceUrl != null) {
                webView.loadUrl(resourceUrl);
            }
            progressBar.setVisibility(ProgressBar.INVISIBLE);

            if(hasTimedOut) {
                Toast.makeText(getApplicationContext(), "NO CONNECTION", Toast.LENGTH_SHORT).show();
            }
            if(noContent) {
                Toast.makeText(getApplicationContext(), "NO CONTENT", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                List<ContentDTO> contentDTOs;
                if (beacon != null) { //TODO to test, remove it
                    contentDTOs = reqService.requestForContent(beacon.getProximityUUID().toString(), new Integer(beacon.getMajor()).toString(), new Integer(beacon.getMinor()).toString());
                } else {
                    contentDTOs = reqService.requestForContent("d96e0731be8d0f59f3ad85bf58602e71", "1729", "13915");
                }
                if(contentDTOs == null) {
                    noContent = true;
                    return null;
                }
                resourceUrl = contentDTOs.get(0).getUrl();
            } catch(SocketTimeoutException e) {
                hasTimedOut = true;
            }
            return null;
        }
    }
}
