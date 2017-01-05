package com.example.timingsystem.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.timingsystem.model.InputBatch;
import com.example.timingsystem.model.InputLocation;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zuokanyq on 2017-1-4.
 */

public class DatabaseHelper  extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "TimingManager";

    // Table Names
    protected static final String TABLE_INPUT_BATCH = "input_batch";
    protected static final String TABLE_INPUT_LOCATION = "input_location";

    // Common column names
    protected static final String KEY_ID = "id";

    // INPUT_BATCH Table - column nmaes
    protected static final String KEY_BATCHNO = "batchno";
    protected static final String KEY_INPUTTIME = "inputtime";

    // INPUT_LOCATION Table - column names
    protected static final String KEY_BATCHID = "batchid";
    protected static final String KEY_LOCATIONNO = "locationno";

    // Table Create Statements
    // Todo table create statement
    private static final String CREATE_TABLE_INPUT_BATCH = "CREATE TABLE "
            + TABLE_INPUT_BATCH + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_BATCHNO
            + " TEXT," + KEY_INPUTTIME + " DATETIME DEFAULT  (datetime('now','localtime')) " + ")";

    // Tag table create statement
    private static final String CREATE_TABLE_INPUT_LOCATION = "CREATE TABLE " + TABLE_INPUT_LOCATION
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INPUT_BATCH);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INPUT_LOCATION);

        // create new tables
        onCreate(db);
    }




}
