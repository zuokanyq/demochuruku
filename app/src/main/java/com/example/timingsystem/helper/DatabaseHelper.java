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
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "TimingManager";

    // Table Names
    private static final String TABLE_INPUT_BATCH = "input_batch";
    private static final String TABLE_INPUT_LOCATION = "input_location";

    // Common column names
    private static final String KEY_ID = "id";

    // INPUT_BATCH Table - column nmaes
    private static final String KEY_BATCHNO = "batchno";
    private static final String KEY_INPUTTIME = "inputtime";

    // INPUT_LOCATION Table - column names
    private static final String KEY_BATCHID = "batchid";
    private static final String KEY_LOCATIONNO = "locationno";

    // Table Create Statements
    // Todo table create statement
    private static final String CREATE_TABLE_INPUT_BATCH = "CREATE TABLE "
            + TABLE_INPUT_BATCH + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_BATCHNO
            + " TEXT," + KEY_INPUTTIME + "DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";

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

    /*
 * Creating a input_batch
 */
    public long createInputBatch(InputBatch inputBatch) {
        SQLiteDatabase db = this.getWritableDatabase();
        long batch_id = 0;
        db.beginTransaction(); //开启事务
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_BATCHNO, inputBatch.getBatchno());
            // insert row
            batch_id = db.insert(TABLE_INPUT_BATCH, null, values);
            List<InputLocation> locationList=inputBatch.getLocationList();
            if(locationList != null){

                for (InputLocation inputLocation : locationList) {
                    ContentValues locationvalues = new ContentValues();
                    locationvalues.put(KEY_BATCHID, batch_id);
                    locationvalues.put(KEY_LOCATIONNO,inputLocation.getLocationno());
                    db.insert(TABLE_INPUT_LOCATION,null,locationvalues);
                }
            }
            db.setTransactionSuccessful(); //事务执行成功
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        return batch_id;
    }

    /*
     * get single input_batch
     */
    public InputBatch getInputBatch(String batchno) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_INPUT_BATCH + " WHERE "
                + KEY_BATCHNO + " = " + batchno;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        InputBatch inputBatch = new InputBatch();
        inputBatch.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        inputBatch.setBatchno(c.getString(c.getColumnIndex(KEY_BATCHNO)));
        String strinputtime=c.getString(c.getColumnIndex(KEY_INPUTTIME));

        inputBatch.setInputtime(Timestamp.valueOf(strinputtime));

        inputBatch.setLocationList(getLocationList(c.getInt(c.getColumnIndex(KEY_ID))));

        return inputBatch;
    }

    /*
    * get all input_batch
    */
    public List<InputBatch> getInputBatchList() {
        List<InputBatch> inputBatchList=new ArrayList<InputBatch>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_INPUT_BATCH;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {

                InputBatch inputBatch = new InputBatch();
                inputBatch.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                inputBatch.setBatchno(c.getString(c.getColumnIndex(KEY_BATCHNO)));
                String strinputtime=c.getString(c.getColumnIndex(KEY_INPUTTIME));

                inputBatch.setInputtime(Timestamp.valueOf(strinputtime));

                inputBatch.setLocationList(getLocationList(c.getInt(c.getColumnIndex(KEY_ID))));

                inputBatchList.add(inputBatch);
            } while (c.moveToNext());
        }

        return inputBatchList;
    }


    /**
     * 根据批次号，获取库位号列表
     * @param batchid
     * @return
     */
    public  List<InputLocation> getLocationList(int batchid){
        SQLiteDatabase db = this.getReadableDatabase();
        List<InputLocation> locationList = new ArrayList<InputLocation>();
        String queryLocation = "SELECT  * FROM " + TABLE_INPUT_LOCATION + " WHERE "
                + KEY_BATCHID + " = " + batchid;

        Log.e(LOG, queryLocation);
        Cursor c = db.rawQuery(queryLocation, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                InputLocation location = new InputLocation();
                location.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                location.setBatchid(c.getInt(c.getColumnIndex(KEY_BATCHID)));
                location.setLocationno(c.getString(c.getColumnIndex(KEY_LOCATIONNO)));

                locationList.add(location);
            } while (c.moveToNext());
        }
        return locationList;
    }

    /*
 * 批量删除
 */
    public void deleteInputBatch(List<InputBatch> inputBatchList) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction(); //开启事务
        try {
            for (InputBatch inputBatch : inputBatchList){
                db.delete(TABLE_INPUT_LOCATION, KEY_BATCHID + " = ?",
                            new String[] { String.valueOf(inputBatch.getId()) });
                db.delete(TABLE_INPUT_BATCH, KEY_ID + " = ?",
                        new String[] { String.valueOf(inputBatch.getId()) });
            }
            db.setTransactionSuccessful(); //事务执行成功
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }



}
