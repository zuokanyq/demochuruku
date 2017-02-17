package com.example.timingsystem;

/**
 * Created by zuokanyq on 2017-1-12.
 */

public final class Constants {
    public static final String ACTION_PUSHINPUT = "com.example.timingsystem.services.action.PUSHINPUT";
    public static final String ACTION_PUSHOUTPUT = "com.example.timingsystem.services.action.PUSHOUTPUT";
    public static final String ACTION_GETDATA = "com.example.timingsystem.services.action.GETDATA";
    public static final String ACTION_SAVEINPUT = "com.example.timingsystem.services.action.SAVEINPUT";
    public static final String ACTION_SAVEOUTPUT = "com.example.timingsystem.services.action.SAVEOUTPUT";
    public static final String ACTION_PUSHINPUTONE = "com.example.timingsystem.services.action.PUSHINPUTONE";
    public static final String ACTION_PUSHOUTPUTONE = "com.example.timingsystem.services.action.PUSHOUTPUTONE";

    public static final String EXTENDED_DATA_STATUS="com.example.timingsystem.services.extra.submitstatus";

    public static final String RES_SUCCEES ="Success";
    public static final String RES_REPEAT="Repeat";

    public static final int CYCLE_TIME=10*60*1000;  //定时推送周期设定 当前设定是10分钟。
}
