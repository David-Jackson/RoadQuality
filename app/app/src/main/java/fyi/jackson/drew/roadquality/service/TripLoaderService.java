package fyi.jackson.drew.roadquality.service;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import fyi.jackson.drew.roadquality.data.AppDatabase;
import fyi.jackson.drew.roadquality.data.entities.RoadPoint;
import fyi.jackson.drew.roadquality.utils.helpers;
import fyi.jackson.drew.roadquality.utils.maps;

public class TripLoaderService extends AsyncTaskLoaderEx<JSONObject> {

    long tripId;

    public TripLoaderService (Context context, long tripId) {
        super(context);
        this.tripId = tripId;
    }

    @Nullable
    @Override
    public JSONObject loadInBackground() {
        AppDatabase db = helpers.getAppDatabase(getContext());

        List<RoadPoint> tripRoadPoints = db.roadPointDao().getAllFromTrip(tripId);

        JSONObject response = new JSONObject();

        try {
            JSONObject trip = new JSONObject();
            JSONArray coordinates = new JSONArray();
            for (RoadPoint roadPoint : tripRoadPoints) {
                maps.LatLng coord = new maps.LatLng(
                        roadPoint.getLatitude(), roadPoint.getLongitude());
                coordinates.put(coord.toJSON());
            }
            trip.put("id", tripId);
            trip.put("coordinates", coordinates);
            response.put("trip", trip);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        db.close();
        return response;
    }

}
