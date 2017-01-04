package com.example.timingsystem.model;

import android.text.method.DateTimeKeyListener;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by zuokanyq on 2017-1-4.
 */

public class InputBatch {
    int id;
    String batchno;
    Timestamp inputtime;
    List<InputLocation> locationList;

    // constructors
    public InputBatch() {
    }

    public InputBatch(String batchno,Timestamp inputtime) {
        this.batchno = batchno;
        this.inputtime = inputtime;
    }
    public InputBatch(int id,String batchno,Timestamp inputtime){
        this.id=id;
        this.batchno = batchno;
        this.inputtime = inputtime;
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

    public Timestamp getInputtime() {
        return inputtime;
    }

    public void setInputtime(Timestamp inputtime) {
        this.inputtime = inputtime;
    }

    public List<InputLocation> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<InputLocation> locationList) {
        this.locationList = locationList;
    }
}
