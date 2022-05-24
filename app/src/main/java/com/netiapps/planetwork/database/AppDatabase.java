package com.netiapps.planetwork.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {DbModelSendingData.class, DbGetTimeData.class , UserLoginData.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract GetDataLatLng taskDao();
    public  abstract GetUserLogindata logintaskDao();

    private static AppDatabase INSTANCE;
    public static AppDatabase getDbInstance(Context context){
        if (INSTANCE==null){
            INSTANCE= Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,"PlanetWork")
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }
}

