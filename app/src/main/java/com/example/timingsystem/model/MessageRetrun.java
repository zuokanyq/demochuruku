package com.example.timingsystem.model;

import java.util.List;

/**
 * Created by zuokanyq on 2017-2-6.
 */

public class MessageRetrun {
    String message;
    List<String> successList;
    List<String> failedList;

    // constructors
    public MessageRetrun() {
    }


    //getters and setters

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getSuccessList() {
        return successList;
    }

    public void setSuccessList(List<String> successList) {
        this.successList = successList;
    }

    public List<String> getFailedList() {
        return failedList;
    }

    public void setFailedList(List<String> failedList) {
        this.failedList = failedList;
    }
}
