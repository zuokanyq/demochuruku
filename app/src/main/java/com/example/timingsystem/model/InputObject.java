package com.example.timingsystem.model;

import org.ksoap2.serialization.DataSoapObject;

/**
 * Created by MELLY on 2017-1-6.
 */

public class InputObject extends DataSoapObject {
    public String batchno = null;
    public String inputtime = null;
    public String[] locations = null;

    private static final String DATA_NAMESPACE = "http://test.axis2.com/xsd";
    private static final String CLASS_NAME_ESAME = "Esame";

    public InputObject() {
        super(DATA_NAMESPACE, CLASS_NAME_ESAME);
    }

    public InputObject(String batchno, String inputtime, String[] locations) {
        super(DATA_NAMESPACE, CLASS_NAME_ESAME);
        this.batchno = batchno;
        this.inputtime = inputtime;
        this.locations = locations;
    }

    public static void initMapping() {
        System.setProperty(CLASS_NAME_ESAME,
                "com.android.activitytest.data.Esame");
    }

    public String getBatchno() {
        return batchno;
    }

    public void setBatchno(String batchno) {
        this.batchno = batchno;
    }

    public String getInputtime() {
        return inputtime;
    }

    public void setInputtime(String inputtime) {
        this.inputtime = inputtime;
    }

    public String[] getLocations() {
        return locations;
    }

    public void setLocations(String[] locations) {
        this.locations = locations;
    }

    public String toString() {
        String tmp = "InputObject: {";
        tmp += "batchno=" + batchno + ", ";
        tmp += "inputtime=" + inputtime + ", ";
        if (locations != null) {
            tmp += ", locations= [ ";
            for (int i = 0; i < locations.length; i++) {
                tmp += locations[i] + " ";
            }
            tmp += "]";
        }
        tmp += "} ";
        return tmp;
    }
}
