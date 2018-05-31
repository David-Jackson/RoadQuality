package app.roadquality.roadquality.service;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.List;

import app.roadquality.roadquality.data.AppDatabase;
import app.roadquality.roadquality.data.entities.Trip;

public class TripListLoaderService extends AsyncTaskLoaderEx<List<Trip>>{

    public TripListLoaderService(Context context) {
        super(context);
    }

    @Nullable
    @Override
    public List<Trip> loadInBackground() {
        AppDatabase db = AppDatabase.getInstance(getContext());

        List<Trip> tripList = db.roadPointDao().getAllTrips();

        return tripList;
    }
}
