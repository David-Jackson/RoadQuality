package fyi.jackson.drew.roadquality.data.dao;

//import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import fyi.jackson.drew.roadquality.data.entities.RoadPoint;
import fyi.jackson.drew.roadquality.data.entities.Trip;

@Dao
public interface RoadPointDao {
    @Query("SELECT * FROM roadpoint ORDER BY timestamp ASC")
    List<RoadPoint> getAll();

//    @Query("SELECT * FROM roadpoint ORDER BY timestamp ASC")
//    LiveData<List<RoadPoint>> getAllLiveData();

    @Query("SELECT * FROM roadpoint WHERE trip_id = :tripId ORDER BY timestamp ASC")
    List<RoadPoint> getAllFromTrip(long tripId);

    @Query("SELECT * FROM roadpoint WHERE not interpolated ORDER BY timestamp ASC")
    List<RoadPoint> getAllGpsPoints();

    @Query("SELECT DISTINCT trip_id from roadpoint ORDER BY trip_id ASC")
    List<Long> getAllTripIds();

    @Query("SELECT rp.trip_id " +
            ", min(rp.timestamp) AS timestamp_start " +
            ", max(rp.timestamp) AS timestamp_end " +
            ", count(*) AS number_of_points " +
            ", u.reference_id " +
            "FROM roadpoint AS rp " +
            "LEFT OUTER JOIN upload AS u " +
            "ON rp.trip_id = u.trip_id " +
            "GROUP BY rp.trip_id " +
            "ORDER BY rp.trip_id DESC")
    List<Trip> getAllTrips();

    @Insert
    void insertAll(RoadPoint... roadPoints);

    @Delete
    void delete(RoadPoint roadPoint);
}
