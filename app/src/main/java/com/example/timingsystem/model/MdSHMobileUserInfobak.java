package com.example.timingsystem.model;


import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by MELLY on 2017-1-6.
 */

public class MdSHMobileUserInfobak implements KvmSerializable {

    private String UserID = null;

    private String PassWord = null;

//    private  ArrayList<String> StockPosition =null ;

    // constructors

    public MdSHMobileUserInfobak(){
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

//    public List<String> getStockPosition() {
//        return this.StockPosition;
//    }
//
//    public void setStockPosition(ArrayList<String> stockPosition) {
//        this.StockPosition = stockPosition;
//    }

    @Override
    public Object getProperty(int index) {
        // TODO Auto-generated method stub
        switch(index)
        {
            case 0:
                return this.UserID;
            case 1:
                return this.PassWord;
            /*case 2:
                return this.StockPosition;*/
        }
        return null;
    }


    @Override
    public int getPropertyCount() {
        // TODO Auto-generated method stub
        return 2;
    }

    @Override
    public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
        // TODO Auto-generated method stub
        switch(index)
        {
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "UserID";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "PassWord";
                break;
            /*case 2:
                info.type = ArrayList.class;
                info.name = "StockPosition";
                break;*/
            default:break;
        }

    }

    @Override
    public void setProperty(int index, Object value) {
        // TODO Auto-generated method stub
        switch(index)
        {
            case 0:
                this.UserID = value.toString();
                break;
            case 1:
                this.PassWord = value.toString();
                break;
            /*case 2:
                this.StockPosition = (ArrayList<String>) value;
                break;*/
        }
    }

}
