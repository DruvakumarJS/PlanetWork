package com.netiapps.planetwork.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class UserLoginData {

    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "login_id")
    String loginID;

    @ColumnInfo(name = "user_id")
    String user_id;

    @ColumnInfo(name = "login_time")
    String login_time;

    @ColumnInfo(name = "logout_time")
    String logout_time;

    @ColumnInfo(name = "date")
    String date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLoginID() {
        return loginID;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setLoginID(String loginID) {
        this.loginID = loginID;
    }

    public String getLogin_time() {
        return login_time;
    }

    public void setLogin_time(String login_time) {
        this.login_time = login_time;
    }

    public String getLogout_time() {
        return logout_time;
    }

    public void setLogout_time(String logout_time) {
        this.logout_time = logout_time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
