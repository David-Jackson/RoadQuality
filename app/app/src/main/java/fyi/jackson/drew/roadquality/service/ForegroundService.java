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

import fyi.jackson.drew.roadquality.ActivityMain;
import fyi.jackson.drew.roadquality.R;
import fyi.jackson.drew.roadquality.data.AsynchronousDatabase;
import fyi.jackson.drew.roadquality.sensors.AccelerometerSensor;
import fyi.jackson.drew.roadquality.sensors.LocationSensor;
import fyi.jackson.drew.roadquality.utils.Vector3D;

public class ForegroundService extends Service {
    private static final String LOG_TAG = "ForegroundService";
    public static boolean IS_SERVICE_RUNNING = false;

    private AccelerometerSensor accelerometerSensor;
    private LocationSensor locationSensor;

    private AsynchronousDatabase database;

    private int gpsRecordCount = 0;
    private int accelRecordCount = 0;
    private NotificationManager notificationManager;
    private Notification.Builder notificationBuilder;
    private static final int NOTIFICATION_ID = ForegroundConstants.NOTIFICATION_ID.FOREGROUND_SERVICE;

    @Override
    public void onCreate() {
        super.onCreate();

        setupNotification();

        database = new AsynchronousDatabase(this);

        accelerometerSensor = new AccelerometerSensor(this) {
            @Override
            public void onUpdate(Vector3D a, Vector3D g) {
                if (accelerometerSensor.significantMotionDetected()) {
                    database.addAccelerometerEntry(a, g);
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

            broadcastStatus(ForegroundConstants.STATUS_ACTIVE);

            accelerometerSensor.start();
            locationSensor.start();

            Log.i(LOG_TAG, "Received Start Foreground Intent ");
            showNotification();
            IS_SERVICE_RUNNING = true;

        } else if (intent.getAction().equals(
                ForegroundConstants.ACTION.STOPFOREGROUND_ACTION)) {

            broadcastStatus(ForegroundConstants.STATUS_INACTIVE);

            Log.i(LOG_TAG, "Received Stop Foreground Intent");
            stopForeground(true);
            IS_SERVICE_RUNNING = false;

            Intent longTermStorageIntent = new Intent(this, DatabaseService.class);
            longTermStorageIntent.putExtra(ServiceConstants.SERVICE_PROCESS_TAG, ServiceConstants.PROCESS_LONG_TERM_STORAGE);
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

    private void broadcastStatus(int status) {
        Intent serviceStatusIntent = new Intent(ServiceConstants.PROCESS_SERVICE_STATUS);
        serviceStatusIntent.putExtra(ForegroundConstants.STATUS_NAME, status);
        sendBroadcast(serviceStatusIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "In onDestroy");

        accelerometerSensor.stop();
        locationSensor.stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case if services are bound (Bound Services).
        return null;
    }
}
