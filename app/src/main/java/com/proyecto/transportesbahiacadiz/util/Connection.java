package com.proyecto.transportesbahiacadiz.util;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "connection")
public class Connection {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @NonNull
    private String address;
    @NonNull
    private int port;

    public Connection(@NonNull String address, int port) {
        this.address = address;
        this.port = port;
    }

    public Connection(){}

    @NonNull
    public long getId() {
        return id;
    }

    public void setId(@NonNull long id) {
        this.id = id;
    }

    @NonNull
    public String getAddress() {
        return address;
    }

    public void setAddress(@NonNull String address) {
        this.address = address;
    }

    @NonNull
    public int getPort() {
        return port;
    }

    public void setPort(@NonNull int port) {
        this.port = port;
    }
}
