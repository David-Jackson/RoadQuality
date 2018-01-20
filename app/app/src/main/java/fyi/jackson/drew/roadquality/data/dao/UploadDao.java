package fyi.jackson.drew.roadquality.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import fyi.jackson.drew.roadquality.data.entities.Upload;

@Dao
public interface UploadDao {

    @Query("SELECT * FROM upload WHERE reference_id NOT NULL")
    List<Upload> getAllUploads();

    @Insert
    void insertAll(Upload... uploads);

    @Delete
    void delete(Upload upload);
}
