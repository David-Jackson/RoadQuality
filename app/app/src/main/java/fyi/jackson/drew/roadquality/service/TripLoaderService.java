package fyi.jackson.drew.roadquality.service;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.List;

import fyi.jackson.drew.roadquality.data.AppDatabase;
import fyi.jackson.drew.roadquality.data.entities.RoadPoint;
import fyi.jackson.drew.roadquality.utils.helpers;

public class TripLoaderService extends AsyncTaskLoaderEx<List<RoadPoint>> {

    long tripId;

    public TripLoaderService (Context context, long tripId) {
        super(context);
        this.tripId = tripId;
    }

    @Nullable
    @Override
    public List<RoadPoint> loadInBackground() {
        AppDatabase db = helpers.getAppDatabase(getContext());

        List<RoadPoint> tripRoadPoints = db.roadPointDao().getAllFromTrip(tripId);

        db.close();
        return tripRoadPoints;
    }

}
