package com.example.timingsystem.model;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by zuokanyq on 2017-1-4.
 */

public class OutputBatch extends Batch {

    Timestamp outputtime;


    // constructors
    public OutputBatch() {
    }


    //getters and setters


    public Timestamp getOutputtime() {
        return outputtime;
    }

    public void setOutputtime(Timestamp outputtime) {
        this.outputtime = outputtime;
    }

}
