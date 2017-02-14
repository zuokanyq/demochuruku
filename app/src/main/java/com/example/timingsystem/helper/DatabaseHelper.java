package com.example.timingsystem.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zuokanyq on 2017-1-4.
 */

public class DatabaseHelper  extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 5;

    // Database Name
    private static final String DATABASE_NAME = "TimingManager";

    // Table Names
    protected static final String TABLE_INPUT_BATCH = "input_batch";
    protected static final String TABLE_INPUT_LOCATION = "input_location";
    protected static final String TABLE_OUTPUT_BATCH = "output_batch";
    protected static final String TABLE_OUTPUT_LOCATION = "output_location";

    // Common column names
    protected static final String KEY_ID = "id";

    // BATCH Table - column nmaes
    protected static final String KEY_BATCHNO = "batchno";
    protected static final String KEY_INPUTTIME = "inputtime";
    protected static final String KEY_ISFAILED = "isfailed";
    protected static final String KEY_FAILEDREASON = "failedreason";
    protected static final String KEY_OUTPUTTIME = "outputtime";
    protected static final String KEY_USERID = "userid";

    // LOCATION Table - column names
    protected static final String KEY_BATCHID = "batchid";
    protected static final String KEY_LOCATIONNO = "locationno";

    // INPUTBATCH Table Create Statements
    private static final String CREATE_TABLE_INPUT_BATCH = "CREATE TABLE "
            + TABLE_INPUT_BATCH + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_BATCHNO
            + " TEXT," + KEY_INPUTTIME + " DATETIME DEFAULT  (datetime('now','localtime')), "
            + KEY_ISFAILED + " INTEGER DEFAULT 0 ," + KEY_FAILEDREASON + " TEXT,"
            + KEY_USERID + " TEXT"
            + " )";

    // INPUTLOCATION table create statement
    private static final String CREATE_TABLE_INPUT_LOCATION = "CREATE TABLE " + TABLE_INPUT_LOCATION
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_BATCHID + " INTEGER,"
            + KEY_LOCATIONNO + " TEXT" + ")";

    // OUTPUTBATCH table create statement
    private static final String CREATE_TABLE_OUTPUT_BATCH = "CREATE TABLE "
            + TABLE_OUTPUT_BATCH + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_BATCHNO
            + " TEXT," + KEY_OUTPUTTIME + " DATETIME DEFAULT  (datetime('now','localtime')), "
            + KEY_ISFAILED + " INTEGER DEFAULT 0 ," + KEY_FAILEDREASON + " TEXT,"
            + KEY_USERID + " TEXT"
            + " )";

    // OUTPUTLOCATION table create statement
    private static final String CREATE_TABLE_OUTPUT_LOCATION = "CREATE TABLE " + TABLE_OUTPUT_LOCATION
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_BATCHID + " INTEGER,"
            + KEY_LOCATIONNO + " TEXT" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_INPUT_BATCH );
        db.execSQL(CREATE_TABLE_INPUT_LOCATION );
        db.execSQL(CREATE_TABLE_OUTPUT_BATCH );
        db.execSQL(CREATE_TABLE_OUTPUT_LOCATION );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //系统上线后需要修改本方法，判断版本号，根据版本号更新表结构
        // on upgrade drop older tables

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INPUT_BATCH);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INPUT_LOCATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OUTPUT_BATCH);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OUTPUT_LOCATION);
        // create new tables
        onCreate(db);
        //
        //db.execSQL("ALTER TABLE person ADD COLUMN other STRING");
    }




}
