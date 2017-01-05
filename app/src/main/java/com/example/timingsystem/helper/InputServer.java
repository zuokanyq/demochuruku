package com.example.timingsystem.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.timingsystem.model.InputBatch;
import com.example.timingsystem.model.InputLocation;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MELLY on 2017-1-5.
 */

public class InputServer {

    private DatabaseHelper dbHelper;

    public InputServer(Context context)
    {
        this.dbHelper = new DatabaseHelper(context);
    }

    private static final String LOG = "InputServer";

    /*
* Creating a input_batch
*/
    public long createInputBatch(InputBatch inputBatch) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long batch_id = 0;
        db.beginTransaction(); //开启事务
        try {
            ContentValues values = new ContentValues();
            values.put(dbHelper.KEY_BATCHNO, inputBatch.getBatchno());
            // insert row
            batch_id = db.insert(dbHelper.TABLE_INPUT_BATCH, null, values);
            List<InputLocation> locationList=inputBatch.getLocationList();
            if(locationList != null){

                for (InputLocation inputLocation : locationList) {
                    ContentValues locationvalues = new ContentValues();
                    locationvalues.put(dbHelper.KEY_BATCHID, batch_id);
                    locationvalues.put(dbHelper.KEY_LOCATIONNO,inputLocation.getLocationno());
                    db.insert(dbHelper.TABLE_INPUT_LOCATION,null,locationvalues);
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
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_INPUT_BATCH + " WHERE "
                + dbHelper.KEY_BATCHNO + " = " + batchno;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        InputBatch inputBatch = new InputBatch();
        inputBatch.setId(c.getInt(c.getColumnIndex(dbHelper.KEY_ID)));
        inputBatch.setBatchno(c.getString(c.getColumnIndex(dbHelper.KEY_BATCHNO)));
        String strinputtime=c.getString(c.getColumnIndex(dbHelper.KEY_INPUTTIME));

        inputBatch.setInputtime(Timestamp.valueOf(strinputtime));

        inputBatch.setLocationList(getLocationList(c.getInt(c.getColumnIndex(dbHelper.KEY_ID))));

        return inputBatch;
    }

    /*
    * get all input_batch
    */
    public List<InputBatch> getInputBatchList() {
        List<InputBatch> inputBatchList=new ArrayList<InputBatch>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_INPUT_BATCH;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {

                InputBatch inputBatch = new InputBatch();
                inputBatch.setId(c.getInt(c.getColumnIndex(dbHelper.KEY_ID)));
                inputBatch.setBatchno(c.getString(c.getColumnIndex(dbHelper.KEY_BATCHNO)));
                String strinputtime=c.getString(c.getColumnIndex(dbHelper.KEY_INPUTTIME));

                inputBatch.setInputtime(Timestamp.valueOf(strinputtime));

                inputBatch.setLocationList(getLocationList(c.getInt(c.getColumnIndex(dbHelper.KEY_ID))));

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
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<InputLocation> locationList = new ArrayList<InputLocation>();
        String queryLocation = "SELECT  * FROM " + dbHelper.TABLE_INPUT_LOCATION + " WHERE "
                + dbHelper.KEY_BATCHID + " = " + batchid;

        Log.e(LOG, queryLocation);
        Cursor c = db.rawQuery(queryLocation, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                InputLocation location = new InputLocation();
                location.setId(c.getInt(c.getColumnIndex(dbHelper.KEY_ID)));
                location.setBatchid(c.getInt(c.getColumnIndex(dbHelper.KEY_BATCHID)));
                location.setLocationno(c.getString(c.getColumnIndex(dbHelper.KEY_LOCATIONNO)));

                locationList.add(location);
            } while (c.moveToNext());
        }
        return locationList;
    }

    /*
 * 批量删除
 */
    public void deleteInputBatch(List<InputBatch> inputBatchList) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction(); //开启事务
        try {
            for (InputBatch inputBatch : inputBatchList){
                db.delete(dbHelper.TABLE_INPUT_LOCATION, dbHelper.KEY_BATCHID + " = ?",
                        new String[] { String.valueOf(inputBatch.getId()) });
                db.delete(dbHelper.TABLE_INPUT_BATCH, dbHelper.KEY_ID + " = ?",
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
