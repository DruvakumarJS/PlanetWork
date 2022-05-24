package com.netiapps.planetwork.model;

import java.io.Serializable;

public class Task implements Serializable {

    public String iiid;
    public String name;

    public String getIiid() {
        return iiid;
    }

    public void setIiid(String iiid) {
        this.iiid = iiid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
