package com.example.timingsystem.model;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by zuokanyq on 2017-1-4.
 */

public class Batch {
    int id;
    String batchno;
    int isfailed;
    String failedreason;
    String userID;
    List<Location> locationList;

    // constructors
    public Batch() {
    }



    //getters and setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBatchno() {
        return batchno;
    }

    public void setBatchno(String batchno) {
        this.batchno = batchno;
    }

    public int getIsfailed() {
        return isfailed;
    }

    public void setIsfailed(int isfailed) {
        this.isfailed = isfailed;
    }

    public String getFailedreason() {
        return failedreason;
    }

    public void setFailedreason(String failedreason) {
        this.failedreason = failedreason;
    }

    public List<Location> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<Location> locationList) {
        this.locationList = locationList;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
