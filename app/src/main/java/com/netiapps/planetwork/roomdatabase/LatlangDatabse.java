package com.netiapps.planetwork.roomdatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.netiapps.planetwork.database.DbGetTimeData;

@Database(entities = {LatlangEntity.class, DbGetTimeData.class}, version = 1,exportSchema = false)
public abstract class LatlangDatabse extends RoomDatabase {

    public abstract latlangDAO getUserDao();

    private  static LatlangDatabse INSTANCE;

    public static LatlangDatabse getDbInstance(Context context){
        if (INSTANCE==null){
            INSTANCE= Room.databaseBuilder(context.getApplicationContext(),LatlangDatabse.class,"Druva_planetwork_latlang_data.db")
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }

}
