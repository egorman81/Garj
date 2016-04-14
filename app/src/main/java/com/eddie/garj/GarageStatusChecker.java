package com.eddie.garj;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.support.v4.app.NotificationBuilderWithBuilderAccessor;
import android.support.v7.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Created by eddie on 20/03/2016.
 */
public class GarageStatusChecker extends IntentService {

    GarageStatus currentStatus;

    NotificationCompat.Builder mBuilder;
    public GarageStatusChecker() {
        super("GarageStatusChecker");
    }




    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();

        // Do work here, based on the contents of dataString

    }

    public void refreshGarageStatus(MainActivity activity) {
        GarageStatus garageStatus;
        try {
            HttpRequestTask httpRequestTask = new HttpRequestTask();
            httpRequestTask.setActivity(activity);
            httpRequestTask.execute();
        } catch (Exception e) {
            garageStatus = new GarageStatus();
            activity.updateUI(garageStatus);
        }
    }

    public void activateGarageDoor(MainActivity activity) {
        GarageStatus garageStatus;
        try {
            ActivateGarageTask activateGarageTask = new ActivateGarageTask();
            activateGarageTask.setActivity(activity);
            activateGarageTask.execute();
        } catch (Exception e) {
            garageStatus = new GarageStatus();
            activity.updateUI(garageStatus);
        }
    }

    public GarageStatus getGarageStatus() {
        GarageStatus garageStatus;
        try {
            HttpRequestTask httpRequestTask = new HttpRequestTask();
            garageStatus = httpRequestTask.execute().get();
        } catch (Exception e) {
            garageStatus = new GarageStatus();
        }
        return garageStatus;
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, GarageStatus> {
        MainActivity activity;
        @Override
        protected GarageStatus doInBackground(Void... params) {
            try {
                final String url = MainActivity.GARAGE_STATUS_URI;
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                GarageStatus garageStatus = restTemplate.getForObject(url, GarageStatus.class);
                return garageStatus;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return new GarageStatus();
        }

        public void setActivity(MainActivity activity) {
            this.activity = activity;
        }


        @Override
        protected void onPostExecute(GarageStatus garageStatus) {

            if(this.activity != null) {
                this.activity.updateUI(garageStatus);
            }
            currentStatus = garageStatus;
        }




    }

    private class ActivateGarageTask extends AsyncTask<Void, Void, GarageStatus> {
        MainActivity activity;
        @Override
        protected GarageStatus doInBackground(Void... params) {
            try {
                final String url = MainActivity.GARAGE_ACTIVATE_URI;
                RestTemplate restTemplate = new RestTemplate();

                HttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
                HttpMessageConverter stringHttpMessageConverternew = new StringHttpMessageConverter();

                restTemplate.getMessageConverters().add(formHttpMessageConverter);
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();


                //GarageStatus currentStatus = getGarageStatus();
                map.add("device", md5("password" + currentStatus.getLastChanged().getTime()));


                return restTemplate.postForObject(url ,map, GarageStatus.class);

            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return new GarageStatus();
        }

        public void setActivity(MainActivity activity) {
            this.activity = activity;
        }


        @Override
        protected void onPostExecute(GarageStatus garageStatus) {


            if(this.activity != null) {
                this.activity.updateUI(garageStatus);
            }
            this.activity.updateUI(garageStatus);
            currentStatus = garageStatus;

        }




    }

    public static String md5(String input) {

        String md5 = null;

        if(null == input) return null;

        try {

            //Create MessageDigest object for MD5
            MessageDigest digest = MessageDigest.getInstance("MD5");

            //Update input string in message digest
            digest.update(input.getBytes(), 0, input.length());

            //Converts message digest value in base 16 (hex)
            md5 = new BigInteger(1, digest.digest()).toString(16);

        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();
        }
        return md5;
    }

}
