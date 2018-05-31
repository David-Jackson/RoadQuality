package app.roadquality.roadquality.data.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Upload {
    @PrimaryKey
    @ColumnInfo(name = "trip_id")
    private long tripId;

    @ColumnInfo(name = "reference_id")
    private String referenceId;

    public long getTripId() {
        return tripId;
    }

    public void setTripId(long tripId) {
        this.tripId = tripId;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }
}

