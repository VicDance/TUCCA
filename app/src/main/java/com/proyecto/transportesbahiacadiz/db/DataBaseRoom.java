package com.proyecto.transportesbahiacadiz.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.proyecto.transportesbahiacadiz.idao.iConnectionDAO;
import com.proyecto.transportesbahiacadiz.util.Connection;

@Database(entities = {Connection.class}, version=1, exportSchema = false)
public abstract class DataBaseRoom extends RoomDatabase {
    public abstract iConnectionDAO connectionDAO();
    private static DataBaseRoom INSTANCE = null;

    public static DataBaseRoom getINSTANCE(final Context context) {
        if(INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), DataBaseRoom.class, "transportesbahiadecadiz.db").fallbackToDestructiveMigration().build();
        }
        return INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }
}
