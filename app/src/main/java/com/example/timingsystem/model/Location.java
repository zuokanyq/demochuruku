package com.example.timingsystem.model;

/**
 * Created by zuokanyq on 2017-1-4.
 */

public class Location {
    int id;
    int batchid;
    String locationno;

    // constructors
    public Location() {
    }

    public Location(int batchid, String locationno) {
        this.batchid=batchid;
        this.locationno=locationno;
    }


    public Location(int id, int batchid, String locationno) {
        this.id=id;
        this.batchid=batchid;
        this.locationno=locationno;
    }

    //setters and getters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBatchid() {
        return batchid;
    }

    public void setBatchid(int batchid) {
        this.batchid = batchid;
    }

    public String getLocationno() {
        return locationno;
    }

    public void setLocationno(String locationno) {
        this.locationno = locationno;
    }
}
