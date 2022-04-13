package com.androidapp.achillion_security_scanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

public class GpsLocationReceiver extends BroadcastReceiver {

    public static boolean gpsOn = true;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            boolean data = intent.getExtras().getBoolean("data");

            if(!data){
               gpsOn = false;
            }else{
                gpsOn = true;
            }

        }
    }



}
