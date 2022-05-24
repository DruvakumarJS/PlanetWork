package com.netiapps.planetwork.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class DbModelSendingData {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "userempId")
    private String userempId;

    @ColumnInfo(name = "lat")
    private String lat;

    @ColumnInfo(name = "lng")
    private String lng;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "time")
    private String time;

    @ColumnInfo(name = "project_id")
    private String projectId;

    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo (name = "time_milli_sec")
    private String currentTimeMillis;

    @ColumnInfo (name = "speed")
    private String speed;

    @ColumnInfo (name = "idle")
    private String idle;

    @ColumnInfo (name = "isreached")
    private String isreached;




    public String getIdle() {
        return idle;
    }

    public void setIdle(String idle) {
        this.idle = idle;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    //    @ColumnInfo(name = "pause")
//    private int pause;
//
//    @ColumnInfo(name = "logout")
//    private boolean logout;


    public String getCurrentTimeMillis() {
        return currentTimeMillis;
    }

    public void setCurrentTimeMillis(String currentTimeMillis) {
        this.currentTimeMillis = currentTimeMillis;
    }

    public String getUserempId() {
        return userempId;
    }

    public void setUserempId(String userempId) {
        this.userempId = userempId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsreached() {
        return isreached;
    }

    public void setIsreached(String isreached) {
        this.isreached = isreached;
    }
}
