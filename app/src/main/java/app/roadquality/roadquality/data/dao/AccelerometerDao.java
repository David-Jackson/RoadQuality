package app.roadquality.roadquality.data.dao;

//import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import app.roadquality.roadquality.data.entities.Accelerometer;

@Dao
public interface AccelerometerDao {
    @Query("SELECT * FROM accelerometer ORDER BY ts ASC")
    List<Accelerometer> getAll();

//    @Query("SELECT * FROM accelerometer ORDER BY ts ASC")
//    LiveData<List<Accelerometer>> getAllLiveData();

    @Insert
    long[] insertAll(Accelerometer... accelerometers);

    @Delete
    void delete(Accelerometer accelerometer);

    @Query("DELETE FROM accelerometer")
    int deleteAll();
}
