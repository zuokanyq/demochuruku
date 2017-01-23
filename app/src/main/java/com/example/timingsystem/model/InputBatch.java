package com.example.timingsystem.model;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by zuokanyq on 2017-1-4.
 */

public class InputBatch extends Batch{

    Timestamp inputtime;

    // constructors
    public InputBatch() {
    }


    public Timestamp getInputtime() {
        return inputtime;
    }

    public void setInputtime(Timestamp inputtime) {
        this.inputtime = inputtime;
    }
}
