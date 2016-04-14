package com.eddie.garj;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.mycompany.myfirstapp.MESSAGE";


    // Intent for checking if garage is closed
    private Intent garageIntent;

    private String currentStatus = "";
    private ProgressBar spinner;
    private Boolean shouldPoll;
    private int pollInterval = 10000;
    private GarageStatusChecker garageStatusChecker = new GarageStatusChecker();

    SharedPreferences sharedPref;
    String syncFreq;
    String syncHost;
    String syncPort;

    public static String GARAGE_STATUS_URI = "http://yourhostandport/GarjWS/garj/status";
    public static  String GARAGE_ACTIVATE_URI = "http://yourhostandport/GarjWS/garj/activate";



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
                Snackbar.make(view, "Refresh", Snackbar.LENGTH_LONG);
                refreshStatus(null);
            }
        });

        spinner = (ProgressBar) findViewById(R.id.gettingGarageStatusBar);
        spinner.setVisibility(View.GONE);




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshStatus(null);
        //new HttpRequestTask().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void activateDoor(View view) {
        spinner.setVisibility(View.VISIBLE);
        garageStatusChecker.activateGarageDoor(this);
    }

    public void refreshStatus(View view) {
        spinner.setVisibility(View.VISIBLE);
        garageStatusChecker.refreshGarageStatus(this);
    }


    public void updateUI(GarageStatus garageStatus) {
            updateStatusDisplay(garageStatus);
            updateButton(garageStatus);
            spinner.setVisibility(View.GONE);
    }

    private void updateButton(GarageStatus garageStatus) {
        Button button = (Button) findViewById(R.id.activate_garage);
        if(garageStatus.getStatus().equals(GarageStatus.GARAGE_CLOSED)) {
            button.setText(GarageStatus.OPEN_GARAGE);
        } else {
            button.setText(GarageStatus.CLOSE_GARAGE);
        }
    }

    private void updateStatusDisplay(GarageStatus garageStatus) {
        TextView garageStatusText = (TextView) findViewById(R.id.garage_status);
        garageStatusText.setText(garageStatus.getStatus());
    }


}
