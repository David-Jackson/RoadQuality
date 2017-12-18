package fyi.jackson.drew.roadquality.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import fyi.jackson.drew.roadquality.service.ServiceConstants;

// Manager to receive broadcasts from various services

public class BroadcastManager {

    private BroadcastReceiver longTermDataReceiver;
    private Context context;

    public BroadcastManager (Context context) {
        this.context = context;
        this.setup();
    }

    public void setup() {
        longTermDataReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context receiveContext, Intent intent) {
                int totalRows = intent.getIntExtra(ServiceConstants.LONG_TERM_DATA_SERVICE_ROAD_POINT_ENTRIES_COUNT, -999);
                int deletedAccelRows = intent.getIntExtra(ServiceConstants.LONG_TERM_DATA_SERVICE_DELETED_ACCELEROMETER_ENTRIES_COUNT, -998);
                int deletedGpsRows = intent.getIntExtra(ServiceConstants.LONG_TERM_DATA_SERVICE_DELETED_GPS_ENTRIES_COUNT, -987);
                Toast.makeText(context, "Data Received: total = " + totalRows + " | delA = " + deletedAccelRows + " | delG =" + deletedGpsRows, Toast.LENGTH_LONG).show();
            }
        };

        IntentFilter longTermDataFilter = new IntentFilter(ServiceConstants.PROCESS_LONG_TERM_STORAGE);
        longTermDataFilter.addCategory(Intent.CATEGORY_DEFAULT);
        context.registerReceiver(longTermDataReceiver, longTermDataFilter);
    }

    public void onPause() {
        context.unregisterReceiver(longTermDataReceiver);
    }
}
