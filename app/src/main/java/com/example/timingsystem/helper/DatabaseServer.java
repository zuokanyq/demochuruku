package com.example.timingsystem.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.timingsystem.model.Batch;
import com.example.timingsystem.model.InputBatch;
import com.example.timingsystem.model.Location;
import com.example.timingsystem.model.OutputBatch;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.id;

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
            values.put(dbHelper.KEY_USERID, inputBatch.getUserID());
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

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_INPUT_BATCH + " WHERE "
                + dbHelper.KEY_BATCHNO + " = '" + batchno +"'";

        return queryInputBatch(selectQuery);
    }

    /*
  * get single input_batch
  */
    public InputBatch getInputBatch(Integer id) {

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_INPUT_BATCH + " WHERE "
                + dbHelper.KEY_ID + " = " + id.toString();

        return queryInputBatch(selectQuery);
    }

    private InputBatch queryInputBatch(String selectQuery){
        InputBatch inputBatch=null;
        Log.e(LOG, selectQuery);

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null && c.getCount()!=0) {
            c.moveToFirst();
            inputBatch =  setValuetoBatch(c);
            String strinputtime = c.getString(c.getColumnIndex(dbHelper.KEY_INPUTTIME));
            inputBatch.setInputtime(Timestamp.valueOf(strinputtime));
        }
        return inputBatch;
    }

    /*
* get single output_batch
*/
    public OutputBatch getOutputBatch(String batchNO) {

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_OUTPUT_BATCH + " WHERE "
                + dbHelper.KEY_BATCHNO + " = '" + batchNO + "'";
        return queryOutputBatch(selectQuery);
    }

    /*
  * get single output_batch
  */
    public OutputBatch getOutputBatch(Integer id) {

        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_OUTPUT_BATCH + " WHERE "
                + dbHelper.KEY_ID + " = " + id.toString();

        return queryOutputBatch(selectQuery);
    }

    private OutputBatch queryOutputBatch(String selectQuery) {
        OutputBatch outputBatch=null;
        Log.e(LOG, selectQuery);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null && c.getCount()!=0) {
            c.moveToFirst();
            outputBatch =  setValuetoOutputBatch(c);
            String strOutputtime = c.getString(c.getColumnIndex(dbHelper.KEY_OUTPUTTIME));
            outputBatch.setOutputtime(Timestamp.valueOf(strOutputtime));
        }
        return outputBatch;
    }




    /*
    * get all input_batch
    */
    public List<InputBatch> getInputBatchList() {
        List<InputBatch> inputBatchList=new ArrayList<InputBatch>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_INPUT_BATCH;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                InputBatch inputBatch = setValuetoBatch(c);
                String strinputtime=c.getString(c.getColumnIndex(dbHelper.KEY_INPUTTIME));
                inputBatch.setInputtime(Timestamp.valueOf(strinputtime));

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

    }   /*
 * 删除单条入库数据
 */
    public int deleteInputBatch(InputBatch inputBatch) {
        int resint=0;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction(); //开启事务
        try {
                db.delete(dbHelper.TABLE_INPUT_LOCATION, dbHelper.KEY_BATCHID + " = ?",
                        new String[] { String.valueOf(inputBatch.getId()) });
                db.delete(dbHelper.TABLE_INPUT_BATCH, dbHelper.KEY_ID + " = ?",
                        new String[] { String.valueOf(inputBatch.getId()) });
            db.setTransactionSuccessful(); //事务执行成功
            resint=1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return resint;
    }

    /*
* 批量删除入库数据
*/
    public void deleteInputBatchByNo(List<String> batchNoList) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String SqlDeleteLoc = "DELETE FROM "+dbHelper.TABLE_INPUT_LOCATION
                +" WHERE "+dbHelper.KEY_BATCHID+" IN "
                    +"(SELECT "+dbHelper.KEY_ID+" FROM "+dbHelper.TABLE_INPUT_BATCH+" WHERE "+dbHelper.KEY_BATCHNO+" = ?);";
        db.beginTransaction(); //开启事务
        try {
            for (String batchNo : batchNoList){
                db.execSQL(SqlDeleteLoc, new String[] { batchNo.trim() });
                db.delete(dbHelper.TABLE_INPUT_BATCH, dbHelper.KEY_BATCHNO + " = ?",
                        new String[] { batchNo.trim() });
            }
            db.setTransactionSuccessful(); //事务执行成功
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }

    /*
* 批量更新入库数据
*/
    public void updateBatchByNo(List<String> batchNoList,boolean isinput) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String SqlUPDATE = "UPDATE ";
        if(isinput) {
            SqlUPDATE+= dbHelper.TABLE_INPUT_BATCH;
        }else{
            SqlUPDATE+= dbHelper.TABLE_OUTPUT_BATCH;
        }
        SqlUPDATE+=" SET "+ dbHelper.KEY_ISFAILED +" = 1, "+dbHelper.KEY_FAILEDREASON+" = ?"
                    +" WHERE "+dbHelper.KEY_BATCHNO+" = ? ;";
        db.beginTransaction(); //开启事务
        try {
            for (String batchNo : batchNoList){
                String[] batchNoandmessage = batchNo.split("\\^\\^\\^");
                db.execSQL(SqlUPDATE, new String[] { batchNoandmessage[1].trim(), batchNoandmessage[0].trim()});
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
            values.put(dbHelper.KEY_USERID, outputBatch.getUserID());
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
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                OutputBatch outputBatch = setValuetoOutputBatch(c);
                String stroutputtime=c.getString(c.getColumnIndex(dbHelper.KEY_OUTPUTTIME));
                outputBatch.setOutputtime(Timestamp.valueOf(stroutputtime));
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
    /*
* 删除单条出库数据
*/
    public int deleteOutputBatch(OutputBatch outputBatch) {
        int resint=0;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction(); //开启事务
        try {
                db.delete(dbHelper.TABLE_OUTPUT_LOCATION, dbHelper.KEY_BATCHID + " = ?",
                        new String[] { String.valueOf(outputBatch.getId()) });
                db.delete(dbHelper.TABLE_OUTPUT_BATCH, dbHelper.KEY_ID + " = ?",
                        new String[] { String.valueOf(outputBatch.getId()) });
            db.setTransactionSuccessful(); //事务执行成功
            resint=1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return resint;
    }

    /*
  * 批量删除出库数据
  */
    public void deleteOutputBatchByNo(List<String> batchNoList) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String SqlDeleteLoc = "DELETE FROM "+dbHelper.TABLE_OUTPUT_LOCATION
                +" WHERE "+dbHelper.KEY_BATCHID+" IN "
                +"(SELECT "+dbHelper.KEY_ID+" FROM "+dbHelper.TABLE_OUTPUT_BATCH+" WHERE "+dbHelper.KEY_BATCHNO+" = ?);";
        db.beginTransaction(); //开启事务
        try {
            for (String batchNo : batchNoList){
                db.execSQL(SqlDeleteLoc, new String[] { batchNo.trim() });
                db.delete(dbHelper.TABLE_OUTPUT_BATCH, dbHelper.KEY_BATCHNO + " = ?",
                        new String[] { batchNo.trim() });
            }
            db.setTransactionSuccessful(); //事务执行成功
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }


    private InputBatch setValuetoBatch(Cursor c){
        InputBatch batch=new InputBatch();
        batch.setId(c.getInt(c.getColumnIndex(dbHelper.KEY_ID)));
        batch.setBatchno(c.getString(c.getColumnIndex(dbHelper.KEY_BATCHNO)));
        batch.setUserID(c.getString(c.getColumnIndex(dbHelper.KEY_USERID)));
        batch.setIsfailed(c.getInt(c.getColumnIndex(dbHelper.KEY_ISFAILED)));
        batch.setFailedreason(c.getString(c.getColumnIndex(dbHelper.KEY_FAILEDREASON)));
        batch.setLocationList(getLocationList(c.getInt(c.getColumnIndex(dbHelper.KEY_ID)),dbHelper.TABLE_INPUT_LOCATION));
        return batch;
    }

    private OutputBatch setValuetoOutputBatch(Cursor c){
        OutputBatch batch=new OutputBatch();
        batch.setId(c.getInt(c.getColumnIndex(dbHelper.KEY_ID)));
        batch.setBatchno(c.getString(c.getColumnIndex(dbHelper.KEY_BATCHNO)));
        batch.setUserID(c.getString(c.getColumnIndex(dbHelper.KEY_USERID)));
        batch.setIsfailed(c.getInt(c.getColumnIndex(dbHelper.KEY_ISFAILED)));
        batch.setFailedreason(c.getString(c.getColumnIndex(dbHelper.KEY_FAILEDREASON)));
        batch.setLocationList(getLocationList(c.getInt(c.getColumnIndex(dbHelper.KEY_ID)),dbHelper.TABLE_INPUT_LOCATION));
        return batch;
    }

}
