package com.android.wikipedia.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.android.wikipedia.Database.*;
@Database(entities = { Entities.class },version = 1)
public abstract class DatabaseManager extends RoomDatabase {
    public static final String TABLE="ASHI";
    public abstract DAO dao();
    private static DatabaseManager INSTANCE;
    public synchronized static DatabaseManager getINSTANCE(Context context){
        if (INSTANCE == null){
            INSTANCE= Room.databaseBuilder(context.getApplicationContext(),DatabaseManager.class,TABLE)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
