package com.proyecto.transportesbahiacadiz.util;

import android.content.Context;

import androidx.annotation.NonNull;

import com.proyecto.transportesbahiacadiz.db.DataBaseRoom;

import java.util.List;

public class ConnectionClass {
    private static DataBaseRoom db;

    public ConnectionClass(@NonNull Context context) {
        db = DataBaseRoom.getINSTANCE(context);
    }

    public List<Connection> getConnection() {
        return db.connectionDAO().getAll();
    }
}
