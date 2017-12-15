package fyi.jackson.drew.roadquality.data.dao;

//import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import fyi.jackson.drew.roadquality.data.entities.RoadPoint;

@Dao
public interface RoadPointDao {
    @Query("SELECT * FROM roadpoint ORDER BY timestamp ASC")
    List<RoadPoint> getAll();

//    @Query("SELECT * FROM roadpoint ORDER BY timestamp ASC")
//    LiveData<List<RoadPoint>> getAllLiveData();

    @Query("SELECT * FROM roadpoint WHERE trip_id = :tripId ORDER BY timestamp ASC")
    List<RoadPoint> getAllFromTrip(int tripId);

    @Query("SELECT * FROM roadpoint WHERE not interpolated ORDER BY timestamp ASC")
    List<RoadPoint> getAllGpsPoints();

    @Query("SELECT DISTINCT trip_id from roadpoint ORDER BY trip_id ASC")
    List<Long> getAllTripIds();

    @Insert
    void insertAll(RoadPoint... roadPoints);

    @Delete
    void delete(RoadPoint roadPoint);
}
