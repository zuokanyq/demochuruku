package com.example.timingsystem.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.timingsystem.model.InputBatch;
import com.example.timingsystem.model.Location;
import com.example.timingsystem.model.OutputBatch;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zuokanyq on 2017-1-5.
 */

public class DatabaseServer {

    private DatabaseHelper dbHelper;

    public DatabaseServer(Context context)
    {
        this.dbHelper = new DatabaseHelper(context);
    }

    private static final String LOG = "DatabaseServer";

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
            List<Location> locationList=inputBatch.getLocationList();
            if(locationList != null){

                for (Location location : locationList) {
                    ContentValues locationvalues = new ContentValues();
                    locationvalues.put(dbHelper.KEY_BATCHID, batch_id);
                    locationvalues.put(dbHelper.KEY_LOCATIONNO, location.getLocationno());
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

        inputBatch.setLocationList(getLocationList(c.getInt(c.getColumnIndex(dbHelper.KEY_ID)),dbHelper.TABLE_INPUT_LOCATION));

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

                inputBatch.setLocationList(getLocationList(c.getInt(c.getColumnIndex(dbHelper.KEY_ID)),dbHelper.TABLE_INPUT_LOCATION));

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
    public  List<Location> getLocationList(int batchid, String tablename){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Location> locationList = new ArrayList<Location>();
        String queryLocation = "SELECT  * FROM " + tablename + " WHERE "
                + dbHelper.KEY_BATCHID + " = " + batchid;

        Log.e(LOG, queryLocation);
        Cursor c = db.rawQuery(queryLocation, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Location location = new Location();
                location.setId(c.getInt(c.getColumnIndex(dbHelper.KEY_ID)));
                location.setBatchid(c.getInt(c.getColumnIndex(dbHelper.KEY_BATCHID)));
                location.setLocationno(c.getString(c.getColumnIndex(dbHelper.KEY_LOCATIONNO)));

                locationList.add(location);
            } while (c.moveToNext());
        }
        return locationList;
    }

    /*
 * 批量删除入库数据
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

    /*
* Creating a OUTput_batch
*/
    public long createOutputBatch(OutputBatch outputBatch) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long batch_id = 0;
        db.beginTransaction(); //开启事务
        try {
            ContentValues values = new ContentValues();
            values.put(dbHelper.KEY_BATCHNO, outputBatch.getBatchno());
            // insert row
            batch_id = db.insert(dbHelper.TABLE_OUTPUT_BATCH, null, values);
            List<Location> locationList=outputBatch.getLocationList();
            if(locationList != null){

                for (Location location : locationList) {
                    ContentValues locationvalues = new ContentValues();
                    locationvalues.put(dbHelper.KEY_BATCHID, batch_id);
                    locationvalues.put(dbHelper.KEY_LOCATIONNO, location.getLocationno());
                    db.insert(dbHelper.TABLE_OUTPUT_LOCATION,null,locationvalues);
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
   * get all OUTput_batch
   */
    public List<OutputBatch> getOutputBatchList() {
        List<OutputBatch> outputBatchList=new ArrayList<OutputBatch>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_OUTPUT_BATCH;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {

                OutputBatch outputBatch = new OutputBatch();
                outputBatch.setId(c.getInt(c.getColumnIndex(dbHelper.KEY_ID)));
                outputBatch.setBatchno(c.getString(c.getColumnIndex(dbHelper.KEY_BATCHNO)));
                String stroutputtime=c.getString(c.getColumnIndex(dbHelper.KEY_OUTPUTTIME));

                outputBatch.setOutputtime(Timestamp.valueOf(stroutputtime));

                outputBatch.setLocationList(getLocationList(c.getInt(c.getColumnIndex(dbHelper.KEY_ID)),dbHelper.TABLE_OUTPUT_LOCATION));

                outputBatchList.add(outputBatch);
            } while (c.moveToNext());
        }

        return outputBatchList;
    }

    /*
* 批量删除出库数据
*/
    public void deleteOutputBatch(List<OutputBatch> outputBatchList) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction(); //开启事务
        try {
            for (OutputBatch outputBatch : outputBatchList){
                db.delete(dbHelper.TABLE_OUTPUT_LOCATION, dbHelper.KEY_BATCHID + " = ?",
                        new String[] { String.valueOf(outputBatch.getId()) });
                db.delete(dbHelper.TABLE_OUTPUT_BATCH, dbHelper.KEY_ID + " = ?",
                        new String[] { String.valueOf(outputBatch.getId()) });
            }
            db.setTransactionSuccessful(); //事务执行成功
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }

}
