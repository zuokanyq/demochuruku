package com.example.timingsystem.model;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by zuokanyq on 2017-1-4.
 */

public class OutputBatch {
    int id;
    String batchno;
    Timestamp outputtime;
    int isfailed;
    String failedreason;
    List<Location> locationList;

    // constructors
    public OutputBatch() {
    }

    public OutputBatch(String batchno, Timestamp outputtime) {
        this.batchno = batchno;
        this.outputtime = outputtime;
    }
    public OutputBatch(int id, String batchno, Timestamp outputtime){
        this.id=id;
        this.batchno = batchno;
        this.outputtime = outputtime;
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

    public Timestamp getOutputtime() {
        return outputtime;
    }

    public void setOutputtime(Timestamp outputtime) {
        this.outputtime = outputtime;
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
}
