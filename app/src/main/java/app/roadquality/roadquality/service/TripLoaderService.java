package app.roadquality.roadquality.service;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.List;

import app.roadquality.roadquality.data.AppDatabase;
import app.roadquality.roadquality.data.entities.RoadPoint;

public class TripLoaderService extends AsyncTaskLoaderEx<List<RoadPoint>> {

    long tripId;

    public TripLoaderService (Context context, long tripId) {
        super(context);
        this.tripId = tripId;
    }

    @Nullable
    @Override
    public List<RoadPoint> loadInBackground() {
        AppDatabase db = AppDatabase.getInstance(getContext());

        List<RoadPoint> tripRoadPoints = db.roadPointDao().getAllFromTrip(tripId);

        return tripRoadPoints;
    }

}
