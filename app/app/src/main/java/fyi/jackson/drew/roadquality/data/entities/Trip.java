package fyi.jackson.drew.roadquality.data.entities;

import android.arch.persistence.room.ColumnInfo;

public class Trip {
    @ColumnInfo(name = "trip_id")
    public long tripId;

    @ColumnInfo(name = "timestamp_start")
    public long startTime;

    @ColumnInfo(name = "timestamp_end")
    public long endTime;

    @ColumnInfo(name = "number_of_points")
    public int numberOfPoints;

    @ColumnInfo(name = "reference_id")
    public String referenceId;
}
