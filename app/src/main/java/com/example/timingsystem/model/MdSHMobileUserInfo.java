package com.example.timingsystem.model;



import java.util.Hashtable;

import org.ksoap2.serialization.DataSoapObject;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

/**
 * Created by MELLY on 2017-1-6.
 */

public class MdSHMobileUserInfo extends DataSoapObject {
    private static final String DATA_NAMESPACE = "http://tempuri.org/";
    private static final String CLASS_NAME_LISTAESAMI = "MdSHMobileUserInfo";

    public String UserID = null;

    public String PassWord = null;

    public  String[] StockPosition =null ;

    // constructors

    public MdSHMobileUserInfo(){
        super(DATA_NAMESPACE, CLASS_NAME_LISTAESAMI);
    }

    public static void initMapping() {
        System.setProperty(CLASS_NAME_LISTAESAMI,
                "com.example.timingsystem.model.MdSHMobileUserInfo");
    }

    public String getUserID() {
        return this.UserID;
    }

    public void setUserID(String userID) {
        this.UserID = userID;
    }

    public String getPassWord() {
        return this.PassWord;
    }

    public void setPassWord(String passWord) {
        this.PassWord = passWord;
    }

    public String[] getStockPosition() {
        return StockPosition;
    }

    public void setStockPosition(String[] stockPosition) {
        StockPosition = stockPosition;
    }

    public String toString() {
        String tmp = "MdSHMobileUserInfo= { ";
        tmp += "UserID=" + UserID + ", ";
        tmp += "PassWord=" + PassWord + ", ";

        if (StockPosition != null) {
            tmp += ", StockPosition= [ ";
            for (int i = 0; i < StockPosition.length; i++) {
                tmp += StockPosition[i] + " ";
            }
            tmp += "]";
        }
        tmp += "}";
        return tmp;
    }
}
