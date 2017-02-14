package com.example.timingsystem;

import android.app.Application;

/**
 * Created by MELLY on 2017-2-8.
 */

public class MyApplication extends Application
    {
        private static final String VALUE = "";

        private String userID;

        @Override
        public void onCreate()
        {
            super.onCreate();
            setUserID(VALUE); // 初始化全局变量
        }

        public String getUserID() {
            return userID;
        }

        public void setUserID(String userID) {
            this.userID = userID;
        }
    }

