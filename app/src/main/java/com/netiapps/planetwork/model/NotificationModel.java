package com.netiapps.planetwork.model;

public class NotificationModel {
    private String tittle;
    private  String description;
    private  String id;
    private  String date;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "NotificationModel{" +
                "tittle='" + tittle + '\'' +
                ", description='" + description + '\'' +
                ", id='" + id + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
