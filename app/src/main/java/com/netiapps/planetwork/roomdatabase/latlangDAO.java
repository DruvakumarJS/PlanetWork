package com.netiapps.planetwork.roomdatabase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.netiapps.planetwork.database.DbGetTimeData;

import java.util.List;

@Dao
public interface latlangDAO {

    @Insert
    void insert(LatlangEntity userlatlang);

    @Query("SELECT * FROM LatlangEntity")
    List<DbGetTimeData> getAllData();

    @Query("DELETE FROM LatlangEntity")
    void delete();

}
