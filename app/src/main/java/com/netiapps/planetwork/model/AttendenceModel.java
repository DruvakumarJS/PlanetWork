package com.netiapps.planetwork.model;

public class AttendenceModel {

    String date;
    String day;
    String login;
    String logout;
    String working_hours;
    String is_holiday;
    String holiday_title;
    String is_weekend;
    String is_leave;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogout() {
        return logout;
    }

    public void setLogout(String logout) {
        this.logout = logout;
    }

    public String getWorking_hours() {
        return working_hours;
    }

    public void setWorking_hours(String working_hours) {
        this.working_hours = working_hours;
    }

    public String getIs_holiday() {
        return is_holiday;
    }

    public void setIs_holiday(String is_holiday) {
        this.is_holiday = is_holiday;
    }

    public String getHoliday_title() {
        return holiday_title;
    }

    public void setHoliday_title(String holiday_title) {
        this.holiday_title = holiday_title;
    }

    public String getIs_weekend() {
        return is_weekend;
    }

    public void setIs_weekend(String is_weekend) {
        this.is_weekend = is_weekend;
    }

    public String getIs_leave() {
        return is_leave;
    }

    public void setIs_leave(String is_leave) {
        this.is_leave = is_leave;
    }
}
