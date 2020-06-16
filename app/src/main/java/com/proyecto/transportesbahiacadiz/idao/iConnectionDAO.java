package com.proyecto.transportesbahiacadiz.idao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.proyecto.transportesbahiacadiz.util.Connection;

import java.util.List;

@Dao
public interface iConnectionDAO {
    @Query("SELECT * FROM connection")
    List<Connection> getAll();
    @Insert
    long insertConnection(Connection connection);
    @Update
    int updateConnection(Connection connection);
}
