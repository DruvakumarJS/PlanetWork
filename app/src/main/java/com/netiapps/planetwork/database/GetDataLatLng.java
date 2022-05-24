package com.netiapps.planetwork.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GetDataLatLng {

    @Query("SELECT * FROM DbModelSendingData")
    List<DbModelSendingData> getAllData();

    @Insert
    void insert(DbModelSendingData task);

    @Delete
    void delete(DbModelSendingData task);

    @Update
    void update(DbModelSendingData task);

    @Query("SELECT * FROM DbModelSendingData ORDER BY ID DESC LIMIT 1")
    DbModelSendingData getLastRecordIfAvailable();

    @Query("DELETE FROM DbModelSendingData where id <= :id")
    void removeAllSavedLocationData(String id);

}
