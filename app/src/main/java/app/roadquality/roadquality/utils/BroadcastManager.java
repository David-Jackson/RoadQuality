package app.roadquality.roadquality.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import app.roadquality.roadquality.service.DatabaseService;
import app.roadquality.roadquality.service.ForegroundConstants;
import app.roadquality.roadquality.service.ServiceConstants;

// Manager to receive broadcasts from various services

public abstract class BroadcastManager {
    private final String TAG = "BroadcastManager";

    private BroadcastReceiver serviceStatusReceiver;
    private BroadcastReceiver longTermDataReceiver;
    private BroadcastReceiver tripUploadReceiver;
    private final Context context;

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

        tripUploadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(ServiceConstants.UPLOAD_TRIP_STATUS, -1);
                String refId = intent.getStringExtra(ServiceConstants.UPLOAD_TRIP_REFERNCE_ID);
                onTripUploadReceived(status, refId);
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

        IntentFilter tripUploadFilter = new IntentFilter(ServiceConstants.PROCESS_UPLOAD_TRIP);
        tripUploadFilter.addCategory(Intent.CATEGORY_DEFAULT);
        this.context.registerReceiver(tripUploadReceiver, tripUploadFilter);
    }

    public void unregister() {
        this.context.unregisterReceiver(serviceStatusReceiver);
        this.context.unregisterReceiver(longTermDataReceiver);
        this.context.unregisterReceiver(tripUploadReceiver);
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

    public abstract void onTripUploadReceived(int status, String referenceId);

    public void askToUploadTrip(long tripId) {
        Intent uploadIntent = new Intent(context, DatabaseService.class);
        uploadIntent.putExtra(ServiceConstants.SERVICE_PROCESS_TAG, ServiceConstants.PROCESS_UPLOAD_TRIP);
        uploadIntent.putExtra(ServiceConstants.TRIP_ID, tripId);
        context.startService(uploadIntent);
    }
}
