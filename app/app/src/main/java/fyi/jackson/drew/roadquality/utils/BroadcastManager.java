package fyi.jackson.drew.roadquality.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fyi.jackson.drew.roadquality.service.DatabaseService;
import fyi.jackson.drew.roadquality.service.ForegroundConstants;
import fyi.jackson.drew.roadquality.service.ServiceConstants;

// Manager to receive broadcasts from various services

public abstract class BroadcastManager {
    private String TAG = "BroadcastManager";

    private BroadcastReceiver serviceStatusReceiver;
    private BroadcastReceiver longTermDataReceiver;
    private BroadcastReceiver tripListReceiver;
    private Context context;

    public BroadcastManager (Context context) {
        this.context = context;
        this.setup();
    }

    public void setup() {
        serviceStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(ForegroundConstants.STATUS_NAME,
                        ForegroundConstants.STATUS_INACTIVE); // default status inactive
                onServiceStatusChanged(status);
            }
        };

        longTermDataReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context receiveContext, Intent intent) {
                int totalRows = intent.getIntExtra(
                        ServiceConstants.LONG_TERM_DATA_SERVICE_ROAD_POINT_ENTRIES_COUNT,
                        -999);
                int deletedAccelRows = intent.getIntExtra(
                        ServiceConstants.LONG_TERM_DATA_SERVICE_DELETED_ACCELEROMETER_ENTRIES_COUNT,
                        -998);
                int deletedGpsRows = intent.getIntExtra(
                        ServiceConstants.LONG_TERM_DATA_SERVICE_DELETED_GPS_ENTRIES_COUNT,
                        -987);
                onDataTransferredToLongTerm(totalRows, deletedAccelRows, deletedGpsRows);
            }
        };

        tripListReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String tripsJsonString = intent.getStringExtra(
                        ServiceConstants.TRIP_JSON_ARRAY_STRING);
                try {
                    JSONArray tripsJson = new JSONArray(tripsJsonString);
                    onTripListReceived(tripsJson);
                } catch (JSONException e) {
                    Log.d(TAG, "onReceive: JSON Array Error parsing string.");
                    e.printStackTrace();
                }
            }
        };

        register();
    }

    public void register() {
        IntentFilter serviceStatusFilter = new IntentFilter(ServiceConstants.PROCESS_SERVICE_STATUS);
        serviceStatusFilter.addCategory(Intent.CATEGORY_DEFAULT);
        this.context.registerReceiver(serviceStatusReceiver, serviceStatusFilter);

        IntentFilter longTermDataFilter = new IntentFilter(ServiceConstants.PROCESS_LONG_TERM_STORAGE);
        longTermDataFilter.addCategory(Intent.CATEGORY_DEFAULT);
        this.context.registerReceiver(longTermDataReceiver, longTermDataFilter);

        IntentFilter tripListDataFilter = new IntentFilter(ServiceConstants.PROCESS_GET_ALL_TRIPS);
        tripListDataFilter.addCategory(Intent.CATEGORY_DEFAULT);
        this.context.registerReceiver(tripListReceiver, tripListDataFilter);
    }

    public void unregister() {
        this.context.unregisterReceiver(serviceStatusReceiver);
        this.context.unregisterReceiver(longTermDataReceiver);
        this.context.unregisterReceiver(tripListReceiver);
    }

    public void onResume() {
        register();
    }

    public void onPause() {
        unregister();
    }

    public abstract void onServiceStatusChanged(int serviceStatus);

    public abstract void onDataTransferredToLongTerm(int totalRows,
                                                     int deletedAccelRows,
                                                     int deletedGpsRows);

    public abstract void onTripListReceived(JSONArray tripList);

    public abstract void onTripDataReceived(JSONObject tripData);

    public void askToUpdateTripList() {
        Intent getTripsListIntent = new Intent(context, DatabaseService.class);
        getTripsListIntent.putExtra(ServiceConstants.SERVICE_PROCESS_TAG, ServiceConstants.PROCESS_GET_ALL_TRIPS);
        Log.d(TAG, "askToUpdateTripList: Getting all trips");
        context.startService(getTripsListIntent);
    }
}
