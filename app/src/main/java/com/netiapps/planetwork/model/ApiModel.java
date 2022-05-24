package com.netiapps.planetwork.model;

import java.util.List;

public class ApiModel {
    public String status;
    public String file_upload;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFile_upload() {
        return file_upload;
    }

    public void setFile_upload(String file_upload) {
        this.file_upload = file_upload;
    }

    @Override
    public String toString() {
        return "ApiModel{" +
                "status='" + status + '\'' +
                ", file_upload='" + file_upload + '\'' +
                '}';
    }
}

