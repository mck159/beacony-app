package com.example.maciek.beacony;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import com.example.maciek.beacony.dto.ContentDTO;
import com.example.maciek.beacony.helpers.NotificationHelper;
import com.example.maciek.beacony.services.ReqService;
import com.example.maciek.beacony.helpers.Settings;
import com.example.maciek.beacony.helpers.SettingsHelper;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Beacon beacon = null;
    BeaconManager beaconManager;
    ReqService reqService;
    WebView webView;
    ProgressBar progressBar;
    private NotificationManager notificationManager;
    ArrayList<ContentDTO> contentDTOs;
    ContentDTO currentContentDTO = null;
    Settings settings = null;

    Map<Beacon, Date> beaconsCache;
    Bitmap currentContentDTOBITMAP = null;

    @Override
    protected void onResume() {
        super.onResume();
        if(currentContentDTO != null) {
            webView.setWebViewClient(new WebViewClient());
            webView.loadUrl(currentContentDTO.getUrl());
        }
        settings = SettingsHelper.loadSettings(getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<ContentDTO> notifContentDTOs = (List<ContentDTO>) getIntent().getSerializableExtra("contents");
        if(currentContentDTO == null && contentDTOs == null && notifContentDTOs == null) {
            // TODO uncomments
//            showSettings();
        }
        if(notifContentDTOs != null) {
            currentContentDTO = notifContentDTOs.get(0);
            notifContentDTOs.remove(0);
        }


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
        webView = (WebView) findViewById(R.id.webView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
//                Toast.makeText(getApplicationContext(), "START MONITORING", Toast.LENGTH_SHORT).show();
                beaconManager.startMonitoring(new Region(
                        "monitored region",
                        null,
                        null, null));
                beaconManager.startRanging(new Region(
                        "monitored region",
                        null,
                        null, null));
            }
        });
        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
//                Toast.makeText(getApplicationContext(), "enter", Toast.LENGTH_SHORT).show();
                if(list == null || list.size() == 0) {
                    return;
                }
                int max_power_beacon_id = 0;
                for(int i = 0 ; i < list.size() ; i++) {
                    if(list.get(i).getMeasuredPower() > list.get(i).getMeasuredPower()) {
                        max_power_beacon_id = i;
                    }
                }
                beacon = list.get(max_power_beacon_id);
                new RequestAsync().execute();

            }

            @Override
            public void onExitedRegion(Region region) {
//                Toast.makeText(getApplicationContext(), "exit", Toast.LENGTH_SHORT).show();
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
        if(item.getItemId() == R.id.action_settings) {
            showSettings();
        }

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
//        Toast.makeText(getApplicationContext(), "enter", Toast.LENGTH_SHORT).show();
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
            // TODO
            if(currentContentDTO != null) {
                NotificationHelper.createNotificationWithDeleteListener(getApplicationContext(), currentContentDTOBITMAP, currentContentDTO.getName(), currentContentDTO.getDescription(), contentDTOs, currentContentDTO);
                webView.setWebViewClient(new WebViewClient());
                webView.loadUrl(currentContentDTO.getUrl());
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
                if (beacon != null) { //TODO to test, remove it
                    contentDTOs = reqService.requestForContent(getApplicationContext(), Utils.computeAccuracy(beacon), beacon.getProximityUUID().toString(), new Integer(beacon.getMajor()).toString(), new Integer(beacon.getMinor()).toString());
                } else {
                    contentDTOs = reqService.requestForContent(getApplicationContext(), 1.0, "test1", "test2", "test3");
                }
                if(contentDTOs == null) {
                    noContent = true;
                    return null;
                }
                if(contentDTOs.size() != 0) {
                    currentContentDTO = contentDTOs.get(0);
                    currentContentDTOBITMAP = BitmapFactory.decodeStream((InputStream) new URL(currentContentDTO.getImgUrl()).getContent());
                }
            } catch(SocketTimeoutException e) {
                hasTimedOut = true;
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "WRONG IMAGE", Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }

    public void showSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
