package fyi.jackson.drew.roadquality.data;


import android.content.Context;
import android.location.Location;
import android.widget.Toast;

import java.util.List;

import fyi.jackson.drew.roadquality.data.entities.Accelerometer;
import fyi.jackson.drew.roadquality.data.entities.Gps;
import fyi.jackson.drew.roadquality.data.entities.RoadPoint;
import fyi.jackson.drew.roadquality.utils.Vector3D;
import fyi.jackson.drew.roadquality.utils.maps;

public class AsynchronousDatabase {

    private final AppDatabase db;
    public long accelerationDbRowId = -1;
    public long gpsDbRowId = -1;

    public AsynchronousDatabase(Context context) {
        db = AppDatabase.getInstance(context);
    }

    public void addAccelerometerEntry(Vector3D a, Vector3D g) {

        final Accelerometer accelerometer = new Accelerometer();
        accelerometer.setTimestamp(System.currentTimeMillis());
        accelerometer.setAx(a.x);
        accelerometer.setAy(a.y);
        accelerometer.setAz(a.z);
        accelerometer.setGx(g.x);
        accelerometer.setGy(g.y);
        accelerometer.setGz(g.z);

        final AsynchronousDatabase context = this;

        new Thread(new Runnable() {
            @Override
            public void run() {
                context.accelerationDbRowId = db.accelerometerDao().insertAll(accelerometer)[0];
            }
        }).start();
    }

    public void addLocationEntry(Location location) {
        final Gps gps = new Gps();
        gps.setTimestamp(System.currentTimeMillis());
        gps.setProvider(location.getProvider());
        gps.setLatitude(location.getLatitude());
        gps.setLongitude(location.getLongitude());
        gps.setAccuracy(location.getAccuracy());
        gps.setAltitude(location.getAltitude());
        gps.setSpeed(location.getSpeed());

        final AsynchronousDatabase context = this;

        new Thread(new Runnable() {
            @Override
            public void run() {
                context.gpsDbRowId = db.gpsDao().insertAll(gps)[0];
            }
        }).start();
    }

}
