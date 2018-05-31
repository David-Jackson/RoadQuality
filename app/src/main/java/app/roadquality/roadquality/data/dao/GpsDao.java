package app.roadquality.roadquality.data.dao;

//import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import app.roadquality.roadquality.data.entities.Gps;

@Dao
public interface GpsDao {
    @Query("SELECT * FROM gps ORDER BY timestamp ASC")
    List<Gps> getAll();

//    @Query("SELECT * FROM gps ORDER BY timestamp ASC")
//    LiveData<List<Gps>> getAllLiveData();

    @Insert
    long[] insertAll(Gps... gps);

    @Delete
    void delete(Gps gps);

    @Query("DELETE FROM gps")
    int deleteAll();
}
