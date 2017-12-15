package fyi.jackson.drew.roadquality.service;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import fyi.jackson.drew.roadquality.ActivityMain;
import fyi.jackson.drew.roadquality.R;
import fyi.jackson.drew.roadquality.data.AsynchronousDatabase;
import fyi.jackson.drew.roadquality.sensors.AccelerometerSensor;
import fyi.jackson.drew.roadquality.sensors.LocationSensor;
import fyi.jackson.drew.roadquality.service.DatabaseService;
import fyi.jackson.drew.roadquality.utils.Vector3D;

public class ForegroundService extends Service {
    private static final String LOG_TAG = "ForegroundService";
    public static boolean IS_SERVICE_RUNNING = false;

    AccelerometerSensor accelerometerSensor;
    LocationSensor locationSensor;

    AsynchronousDatabase database;

    int gpsRecordCount = 0;
    int accelRecordCount = 0;
    NotificationManager notificationManager;
    Notification.Builder notificationBuilder;
    private static final int NOTIFICATION_ID = ForegroundConstants.NOTIFICATION_ID.FOREGROUND_SERVICE;

    @Override
    public void onCreate() {
        super.onCreate();

        setupNotification();

        database = new AsynchronousDatabase(this);

        accelerometerSensor = new AccelerometerSensor(this) {
            @Override
            public void onUpdate(Vector3D a, Vector3D g, Vector3D j) {
                if (accelerometerSensor.significantMotionDetected()) {
                    database.addAccelerometerEntry(a, g, j);
                    accelRecordCount++;
                    if (accelRecordCount % 100 == 0) {
                        updateNotification();
                    }
                }
            }
        };

        locationSensor = new LocationSensor(this) {
            @Override
            public void onUpdate(Location location) {
                database.addLocationEntry(location);
                gpsRecordCount++;
                updateNotification();
            }
        };


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(ForegroundConstants.ACTION.STARTFOREGROUND_ACTION)) {

            accelerometerSensor.start();
            locationSensor.start();

            Log.i(LOG_TAG, "Received Start Foreground Intent ");
            showNotification();
            Toast.makeText(this, "Service Started!", Toast.LENGTH_SHORT).show();

        } else if (intent.getAction().equals(
                ForegroundConstants.ACTION.STOPFOREGROUND_ACTION)) {


            Log.i(LOG_TAG, "Received Stop Foreground Intent");
            stopForeground(true);
            IS_SERVICE_RUNNING = false;

            Intent longTermStorageIntent = new Intent(this, DatabaseService.class);
            longTermStorageIntent.putExtra(ServiceConstants.SERVICE_PROCESS_TAG, ServiceConstants.PROCESS_LONG_TERM_STORAGE);
            Toast.makeText(this, "Started LongTermDataService", Toast.LENGTH_SHORT).show();
            startService(longTermStorageIntent);

            stopSelf();
        }
        return START_STICKY;
    }

    private void setupNotification() {
        Intent notificationIntent = new Intent(this, ActivityMain.class);
        notificationIntent.setAction(ForegroundConstants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent stopServiceIntent = new Intent(this, ForegroundService.class);
        stopServiceIntent.setAction(ForegroundConstants.ACTION.STOPFOREGROUND_ACTION);
        PendingIntent pendingStopServiceIntent = PendingIntent.getService(this,
                0, stopServiceIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder = new Notification.Builder(this)
                .setContentTitle("Road Quality")
                .setTicker("Road Quality")
                .setContentText("Recording...")
                .setSmallIcon(R.drawable.road_variant)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .addAction(R.drawable.ic_stop, "Stop Recording",
                        pendingStopServiceIntent);
    }

    private void showNotification() {
        Notification notification = notificationBuilder.build();
        startForeground(NOTIFICATION_ID, notification);
    }

    public void updateNotification() {
        notificationBuilder.setContentText("Recording... Accel: " + accelRecordCount + ", GPS: " + gpsRecordCount);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "In onDestroy");
        Toast.makeText(this, "Service Detroyed!", Toast.LENGTH_SHORT).show();

        accelerometerSensor.stop();
        locationSensor.stop();

        database.transferIntoTripDatabase(this);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case if services are bound (Bound Services).
        return null;
    }
}
