package fyi.jackson.drew.roadquality.data;


import android.arch.persistence.room.Room;
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

    AppDatabase db;
    public long accelerationDbRowId = -1;
    public long gpsDbRowId = -1;

    public AsynchronousDatabase(Context context) {
        db = Room.databaseBuilder(context,
                AppDatabase.class, AppDatabase.DATABASE_NAME)
                .fallbackToDestructiveMigration().build();
    }

    public void addAccelerometerEntry(Vector3D a, Vector3D g, Vector3D j) {

        final Accelerometer accelerometer = new Accelerometer();
        accelerometer.setTimestamp(System.currentTimeMillis());
        accelerometer.setAx(a.x);
        accelerometer.setAy(a.y);
        accelerometer.setAz(a.z);
        accelerometer.setGx(g.x);
        accelerometer.setGy(g.y);
        accelerometer.setGz(g.z);
        accelerometer.setJx(j.x);
        accelerometer.setJy(j.y);
        accelerometer.setJz(j.z);

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

        final AsynchronousDatabase context = this;

        new Thread(new Runnable() {
            @Override
            public void run() {
                context.gpsDbRowId = db.gpsDao().insertAll(gps)[0];
            }
        }).start();
    }

    public void transferIntoTripDatabase(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Gps> gpsList = db.gpsDao().getAll();
                List<Accelerometer> accelerometerList = db.accelerometerDao().getAll();
                List<RoadPoint> roadPointList = maps.utils.interpolateAccelerometerAndGpsData(
                        accelerometerList, gpsList);

                RoadPoint[] roadPointArray = new RoadPoint[roadPointList.size()];

                for (int i = 0; i < roadPointList.size(); i++) {
                    roadPointArray[i] = roadPointList.get(i);
                }

                db.roadPointDao().insertAll(roadPointArray);
                int deletedAccelerometerEntries = db.accelerometerDao().deleteAll();
                int deletedGpsEntries = db.gpsDao().deleteAll();
                Toast.makeText(context, deletedAccelerometerEntries + " Accel Deleted", Toast.LENGTH_SHORT).show();
                Toast.makeText(context, deletedGpsEntries + " GPS Deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
