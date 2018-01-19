package fyi.jackson.drew.roadquality.service;

import android.app.IntentService;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import fyi.jackson.drew.roadquality.data.AppDatabase;
import fyi.jackson.drew.roadquality.data.entities.Accelerometer;
import fyi.jackson.drew.roadquality.data.entities.Gps;
import fyi.jackson.drew.roadquality.data.entities.RoadPoint;
import fyi.jackson.drew.roadquality.data.entities.Trip;
import fyi.jackson.drew.roadquality.data.migrations.Migrations;
import fyi.jackson.drew.roadquality.utils.helpers;
import fyi.jackson.drew.roadquality.utils.maps;


public class DatabaseService extends IntentService {

    private static final String TAG = "DatabaseService";

    public DatabaseService() {
        super("DatabaseService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String process;
        try {
            process = intent.getStringExtra(ServiceConstants.SERVICE_PROCESS_TAG);
        } catch (NullPointerException e) {
            e.printStackTrace();
            process = ServiceConstants.PROCESS_NOT_DEFINED;
        }
        switch (process) {
            case ServiceConstants.PROCESS_NOT_DEFINED:
                Log.d(TAG, "onHandleIntent: Process not defined");
                break;
            case ServiceConstants.PROCESS_LONG_TERM_STORAGE:
                Log.d(TAG, "onHandleIntent: Process: Long Term Storage");
                longTermStorage(intent);
                break;
            case ServiceConstants.PROCESS_GET_ALL_TRIP_IDS:
                Log.d(TAG, "onHandleIntent: Process: Get all Trip Ids");
                getAllTripIds(intent);
                break;
            case ServiceConstants.PROCESS_GET_ALL_TRIPS:
                Log.d(TAG, "onHandleIntent: Process: Get all Trips");
                getAllTrips(intent);
                break;
            case ServiceConstants.PROCESS_GET_ALL_GPS_ROAD_POINTS:
                Log.d(TAG, "onHandleIntent: Process: Get all GPS Points");
                getAllGpsPoints(intent);
                break;
        }
    }

    private void longTermStorage(Intent intent) {
        AppDatabase db = helpers.getAppDatabase(this);

        List<Accelerometer> accelerometerList = db.accelerometerDao().getAll();
        List<Gps> gpsList = db.gpsDao().getAll();

        List<RoadPoint> roadPointList = maps.utils.interpolateAccelerometerAndGpsData(
                accelerometerList, gpsList);

        RoadPoint[] roadPointArray = new RoadPoint[roadPointList.size()];

        for (int i = 0; i < roadPointList.size(); i++) {
            roadPointArray[i] = roadPointList.get(i);
        }

        db.roadPointDao().insertAll(roadPointArray);
        int deletedAccelerometerEntries = db.accelerometerDao().deleteAll();
        int deletedGpsEntries = db.gpsDao().deleteAll();
        int allRoadPointEntries = db.roadPointDao().getAll().size();


        Intent broadcastIntent = new Intent(ServiceConstants.PROCESS_LONG_TERM_STORAGE);
        //intent.putExtra("RESULTS", roadPointList);
        broadcastIntent.putExtra(ServiceConstants.LONG_TERM_DATA_SERVICE_DELETED_ACCELEROMETER_ENTRIES_COUNT, deletedAccelerometerEntries);
        broadcastIntent.putExtra(ServiceConstants.LONG_TERM_DATA_SERVICE_DELETED_GPS_ENTRIES_COUNT, deletedGpsEntries);
        broadcastIntent.putExtra(ServiceConstants.LONG_TERM_DATA_SERVICE_ROAD_POINT_ENTRIES_COUNT, allRoadPointEntries);
        sendBroadcast(broadcastIntent);

        db.close();
    }

    private void getAllTripIds(Intent intent) {
        AppDatabase db = helpers.getAppDatabase(this);

        List<Long> tripIds = db.roadPointDao().getAllTripIds();

        int tripIdsCount = tripIds.size();

        for (long id : tripIds) {
            Log.d(TAG, "getAllTripIds: Trip ID = " + id);
        }

        Intent broadcastIntent = new Intent(ServiceConstants.PROCESS_GET_ALL_TRIP_IDS);
        broadcastIntent.putExtra(ServiceConstants.TRIP_IDS_COUNT, tripIdsCount);
        sendBroadcast(broadcastIntent);

        db.close();
    }

    private void getAllTrips(Intent intent) {
        AppDatabase db = helpers.getAppDatabase(this);

        List<Trip> trips = db.roadPointDao().getAllTrips();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (int i = 0; i < trips.size(); i++) {
                stringBuilder.append(trips.get(i).toJSONString());
                if (i < trips.size() - 1) stringBuilder.append(",");
        }
        stringBuilder.append("]");
        String tripsJsonExtra = stringBuilder.toString();

        Intent broadcastIntent = new Intent(ServiceConstants.PROCESS_GET_ALL_TRIPS);
        broadcastIntent.putExtra(ServiceConstants.TRIP_JSON_ARRAY_STRING, tripsJsonExtra);
        sendBroadcast(broadcastIntent);

        db.close();
    }

    private void getAllGpsPoints(Intent intent) {
        AppDatabase db = helpers.getAppDatabase(this);

        List<RoadPoint> gpsRoadPoints = db.roadPointDao().getAllGpsPoints();

        int gpsRoadPointCount = gpsRoadPoints.size();

        Intent broadcastIntent = new Intent(ServiceConstants.PROCESS_GET_ALL_GPS_ROAD_POINTS);
        broadcastIntent.putExtra(ServiceConstants.TRIP_IDS_COUNT, gpsRoadPointCount);
        sendBroadcast(broadcastIntent);

        db.close();
    }
}
