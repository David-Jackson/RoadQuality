package fyi.jackson.drew.roadquality.service;

import android.app.IntentService;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fyi.jackson.drew.roadquality.data.AppDatabase;
import fyi.jackson.drew.roadquality.data.entities.Accelerometer;
import fyi.jackson.drew.roadquality.data.entities.Gps;
import fyi.jackson.drew.roadquality.data.entities.RoadPoint;
import fyi.jackson.drew.roadquality.data.entities.Trip;
import fyi.jackson.drew.roadquality.data.entities.Upload;
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
            case ServiceConstants.PROCESS_GET_ALL_GPS_ROAD_POINTS:
                Log.d(TAG, "onHandleIntent: Process: Get all GPS Points");
                getAllGpsPoints(intent);
                break;
            case ServiceConstants.PROCESS_UPLOAD_TRIP:
                Log.d(TAG, "onHandleIntent: Process: Uploading Trip");
                uploadTrip(intent);
                break;
            case ServiceConstants.PROCESS_SAVE_UPLOAD_TRIP:
                Log.d(TAG, "onHandleIntent: Process: Saving Uploaded Trip");
                saveUploadToDatabase(intent);
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

    private void getAllGpsPoints(Intent intent) {
        AppDatabase db = helpers.getAppDatabase(this);

        List<RoadPoint> gpsRoadPoints = db.roadPointDao().getAllGpsPoints();

        int gpsRoadPointCount = gpsRoadPoints.size();

        Intent broadcastIntent = new Intent(ServiceConstants.PROCESS_GET_ALL_GPS_ROAD_POINTS);
        broadcastIntent.putExtra(ServiceConstants.TRIP_IDS_COUNT, gpsRoadPointCount);
        sendBroadcast(broadcastIntent);

        db.close();
    }

    private void uploadTrip(Intent intent) {
        final long tripId = intent.getLongExtra(ServiceConstants.TRIP_ID, -1);
        if (tripId == -1) return;

        AppDatabase db = helpers.getAppDatabase(this);

        List<RoadPoint> roadPoints = db.roadPointDao().getAllFromTrip(tripId);

        List<Object> trimmedRoadPoints = new ArrayList<>();

        for (RoadPoint roadPoint : roadPoints) {
            trimmedRoadPoints.add(roadPoint.toHashMap());
        }

        HashMap<String, Object> uploadMap = new HashMap<>();
        uploadMap.put("points", trimmedRoadPoints);

        FirebaseApp.initializeApp(this);

        FirebaseFirestore fireDb = FirebaseFirestore.getInstance();

        Log.d(TAG, "uploadTrip: Trying to add to Firestore... tripId: " + tripId + ", " + trimmedRoadPoints.size() + " points");
        fireDb.collection("roadPoints")
                .add(uploadMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String docId = documentReference.getId();
                        Log.d(TAG, "DocumentSnapshot added with ID: " + docId);

                        Intent saveUploadIntent = new Intent(DatabaseService.this, DatabaseService.class);
                        saveUploadIntent.putExtra(ServiceConstants.SERVICE_PROCESS_TAG, ServiceConstants.PROCESS_SAVE_UPLOAD_TRIP);
                        saveUploadIntent.putExtra(ServiceConstants.TRIP_ID, tripId);
                        saveUploadIntent.putExtra(ServiceConstants.UPLOAD_TRIP_REFERNCE_ID, docId);
                        startService(saveUploadIntent);

                        Intent broadcastIntent = new Intent(ServiceConstants.PROCESS_UPLOAD_TRIP);
                        broadcastIntent.putExtra(ServiceConstants.UPLOAD_TRIP_STATUS,
                                ServiceConstants.UPLOAD_TRIP_SUCCESS);
                        broadcastIntent.putExtra(ServiceConstants.UPLOAD_TRIP_REFERNCE_ID, docId);
                        sendBroadcast(broadcastIntent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error adding document", e);
                        Intent broadcastIntent = new Intent(ServiceConstants.PROCESS_UPLOAD_TRIP);
                        broadcastIntent.putExtra(ServiceConstants.UPLOAD_TRIP_STATUS,
                                ServiceConstants.UPLOAD_TRIP_FAILURE);
                        sendBroadcast(broadcastIntent);
                    }
                });
    }

    void saveUploadToDatabase(Intent intent) {
        long tripId = intent.getLongExtra(ServiceConstants.TRIP_ID, -1);
        if (tripId == -1) return;
        String refId = intent.getStringExtra(ServiceConstants.UPLOAD_TRIP_REFERNCE_ID);

        AppDatabase db = helpers.getAppDatabase(this);

        Upload upload = new Upload();
        upload.setTripId(tripId);
        upload.setReferenceId(refId);
        db.uploadDao().insertAll(upload);
    }
}
