package fyi.jackson.drew.roadquality.service;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.List;

import fyi.jackson.drew.roadquality.data.AppDatabase;
import fyi.jackson.drew.roadquality.data.entities.Trip;
import fyi.jackson.drew.roadquality.utils.helpers;

public class TripListLoaderService extends AsyncTaskLoaderEx<List<Trip>>{

    public TripListLoaderService(Context context) {
        super(context);
    }

    @Nullable
    @Override
    public List<Trip> loadInBackground() {
        AppDatabase db = helpers.getAppDatabase(getContext());

        List<Trip> tripList = db.roadPointDao().getAllTrips();

        db.close();
        return tripList;
    }
}
