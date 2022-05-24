package com.netiapps.planetwork.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GetUserLogindata {

    @Query("SELECT * FROM UserLoginData")
    List<UserLoginData> getlogindata();

    @Insert
    void insert(UserLoginData task);

    @Delete
    void delete(UserLoginData task);

    @Query("UPDATE UserLoginData SET logout_time=:logout_time WHERE login_id=:id")
    void update(String logout_time , String id );

    @Query("SELECT * FROM UserLoginData ORDER BY ID DESC LIMIT 1")
    DbModelSendingData getLastRecordIfAvailable();

    @Query("DELETE FROM UserLoginData WHERE logout_time!=0 ")
    void removeAllSavedLoginData();
    
}
