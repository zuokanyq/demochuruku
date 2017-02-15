package com.example.timingsystem.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Xml;

import com.example.timingsystem.Constants;
import com.example.timingsystem.MainActivity;
import com.example.timingsystem.MyApplication;
import com.example.timingsystem.R;
import com.example.timingsystem.helper.DatabaseServer;
import com.example.timingsystem.model.Batch;
import com.example.timingsystem.model.InputBatch;
import com.example.timingsystem.model.Location;
import com.example.timingsystem.model.MessageRetrun;
import com.example.timingsystem.model.OutputBatch;

import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class InputIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS

    // TODO: Rename parameters
    private static final String EXTRA_ID = "com.example.timingsystem.services.extra.ID";
    private static final String EXTRA_BATCHNO = "com.example.timingsystem.services.extra.BATCHNO";
    private static final String EXTRA_LOCATIONNOS = "com.example.timingsystem.services.extra.LOCATIONNOS";

    private DatabaseServer databaseServer;
    private MyApplication app;


    public InputIntentService() {
        super("InputIntentService");
        databaseServer=new DatabaseServer(this);

    }

    /**
     * Starts this service to perform action PushInput with the given parameters. If
     * the service is already performing a task this action will be queued.
     */
    public static void startActionPushInput(Context context) {
        Intent intent = new Intent(context, InputIntentService.class);
        intent.setAction(Constants.ACTION_PUSHINPUT);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action PushOutput with the given parameters. If
     * the service is already performing a task this action will be queued.
     */
    public static void startActionPushOutput(Context context) {
        Intent intent = new Intent(context, InputIntentService.class);
        intent.setAction(Constants.ACTION_PUSHOUTPUT);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action GetData with the given parameters. If
     * the service is already performing a task this action will be queued.
     */
    public static void startActionGetData(Context context, String param1) {
        Intent intent = new Intent(context, InputIntentService.class);

        intent.setAction(Constants.ACTION_SAVEINPUT);
        intent.putExtra(EXTRA_BATCHNO, param1);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action SaveInput with the given parameters. If
     * the service is already performing a task this action will be queued.
     */
    public static void startActionSaveInput(Context context, String batchNo, String locationNos) {
        Intent intent = new Intent(context, InputIntentService.class);

        intent.setAction(Constants.ACTION_SAVEINPUT);
        intent.putExtra(EXTRA_BATCHNO, batchNo);
        intent.putExtra(EXTRA_LOCATIONNOS, locationNos);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action SaveOutput with the given parameters. If
     * the service is already performing a task this action will be queued.
     */
    public static void startActionSaveOutput(Context context, String batchNo) {
        Intent intent = new Intent(context, InputIntentService.class);

        intent.setAction(Constants.ACTION_SAVEOUTPUT);
        intent.putExtra(EXTRA_BATCHNO, batchNo);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action SaveOutput with the given parameters. If
     * the service is already performing a task this action will be queued.
     */
    public static void startActionPushInputOne(Context context, Integer id) {
        Intent intent = new Intent(context, InputIntentService.class);

        intent.setAction(Constants.ACTION_PUSHINPUTONE);
        intent.putExtra(EXTRA_ID, id);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action SaveOutput with the given parameters. If
     * the service is already performing a task this action will be queued.
     */
    public static void startActionPushOutputOne(Context context, Integer id) {
        Intent intent = new Intent(context, InputIntentService.class);

        intent.setAction(Constants.ACTION_PUSHOUTPUTONE);
        intent.putExtra(EXTRA_ID, id);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (Constants.ACTION_PUSHINPUT.equals(action)) {
                handleActionPushInput();
            } else if (Constants.ACTION_PUSHOUTPUT.equals(action)) {
                handleActionPushOutput();
            } else if (Constants.ACTION_GETDATA.equals(action)) {
                final String batchNO = intent.getStringExtra(EXTRA_BATCHNO);
                handleActionGetData(batchNO);
            } else if (Constants.ACTION_SAVEINPUT.equals(action)) {
                final String batchNO = intent.getStringExtra(EXTRA_BATCHNO);
                final String locationNos = intent.getStringExtra(EXTRA_LOCATIONNOS);
                handleActionSaveInput(batchNO,locationNos);
            } else if (Constants.ACTION_SAVEOUTPUT.equals(action)) {
                final String batchNO = intent.getStringExtra(EXTRA_BATCHNO);
                handleActionSaveOutput(batchNO);
            } else if (Constants.ACTION_PUSHINPUTONE.equals(action)) {
                final Integer id = intent.getIntExtra(EXTRA_ID,-1);
                handleActionPushInputOne(id);
            } else if (Constants.ACTION_PUSHOUTPUTONE.equals(action)) {
                final Integer id = intent.getIntExtra(EXTRA_ID,-1);
                handleActionPushOutputOne(id);
            }
        }
    }

    /**
     * Handle action PushInput in the provided background thread with the provided
     * parameters.
     * 推送数据库中的所有入库数据
     */
    private void handleActionPushInput() {
        // TODO: Handle action PushInput
        List<InputBatch> inputBatchList=databaseServer.getInputBatchList();
        if (inputBatchList.isEmpty()){
            return;
        }
       pushinputXml(inputBatchList);

    }
    /**
     * Handle action PushInput in the provided background thread with the provided
     * parameters.
     * 推送指定id的入库数据
     */
    private void handleActionPushInputOne(Integer id) {
        // TODO: Handle action PushInput
        List<InputBatch> inputBatchList=new ArrayList<InputBatch>();
        InputBatch inputBatch = databaseServer.getInputBatch(id);
        if (inputBatch!=null){
            inputBatchList.add(inputBatch);
            String message = pushinputXml(inputBatchList);
            Intent localIntent =
                    new Intent(Constants.ACTION_PUSHINPUT)
                            .putExtra(Constants.EXTENDED_DATA_STATUS, message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

        }

    }

    /**
     * 入库数据推送方法
     * @param inputBatchList
     */
    private String pushinputXml(List<InputBatch> inputBatchList) {
        String inputxml = generateXml(inputBatchList);
       // String url = "http://192.168.168.196:7676/SRC/business/mobilemanagement.asmx";
        String webMethName = "Input";
        String reStr="";
        Map<String, String> paramsmap=new HashMap<String, String>();
        paramsmap.put("InputSend",inputxml);
        app=(MyApplication)getApplication();
        SoapObject obj = CallWebService.invokeInputWS(app.getServerurl(),webMethName,paramsmap);

        if("Success".equals(obj.getProperty("resMsg"))){
            MessageRetrun message= parseSoapObjectToMessage(obj,true);
            databaseServer.deleteInputBatchByNo(message.getSuccessList());
            databaseServer.updateBatchByNo(message.getFailedList(),true);
            if("suceess".equals(message.getMessage())) {
                reStr = "推送成功";
            }else{
                reStr = message.getMessage();
            }
        }else if ("Error".equals((obj.getProperty("resMsg")))){
            reStr="推送失败,请检查网络状态。";
        }
        initNotify("入库数据推送通知",reStr,101);
        return reStr;
    }

    /**
     * Handle action PushInput in the provided background thread with the provided
     * parameters.
     * 推送指定id出库数据
     */
    private void handleActionPushOutputOne(Integer id) {
        // TODO: Handle action PushInput
        List<OutputBatch> outputBatchList=new ArrayList<OutputBatch>();
        OutputBatch outputBatch = databaseServer.getOutputBatch(id);
        if (outputBatch!=null){
            outputBatchList.add(outputBatch);
            String message = pushoutputXml(outputBatchList);
            Intent localIntent =
                    new Intent(Constants.ACTION_PUSHINPUT)
                            .putExtra(Constants.EXTENDED_DATA_STATUS, message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        }
    }

    /**
     * Handle action PushInput in the provided background thread with the provided
     * parameters.
     * 推送所有出库数据
     */
    private void handleActionPushOutput() {
        // TODO: Handle action PushOutput
        List<OutputBatch> outputBatchList=databaseServer.getOutputBatchList();
        if (outputBatchList.isEmpty()){
            return;
        }
        pushoutputXml(outputBatchList);

    }

    /**
     * 出库数据推送方法
     * @param outputBatchList
     */
    private String pushoutputXml(List<OutputBatch> outputBatchList){
        String outputxml = generateOutputXml(outputBatchList);
        //String url = "http://192.168.168.196:7676/SRC/business/mobilemanagement.asmx";
        String webMethName = "Output";
        String reStr="";
        Map<String, String> paramsmap=new HashMap<String, String>();
        paramsmap.put("OutputSend",outputxml);
        app=(MyApplication)getApplication();
            SoapObject obj = CallWebService.invokeInputWS(app.getServerurl(),webMethName,paramsmap);

        if("Success".equals(obj.getProperty("resMsg"))){
            MessageRetrun message= parseSoapObjectToMessage(obj,false);
            databaseServer.deleteOutputBatchByNo(message.getSuccessList());
            databaseServer.updateBatchByNo(message.getFailedList(),false);
            if(!"suceess".equals(message.getMessage())){
                reStr=message.getMessage();
                initNotify("出库数据推送通知",reStr,102);
            }
        }else if ("Error".equals((obj.getProperty("resMsg")))){
            reStr="推送失败,请检查网络状态是否正常";
            initNotify("出库数据推送通知",reStr,102);
        }
        return reStr;
     }


    /**
     * Handle action GetData in the provided background thread with the provided
     * parameters.
     */
    private void handleActionGetData(String param1) {
        // TODO: Handle action GetData
    }

    /**
     * Handle action SaveInput in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSaveInput(String batchno,String locationNo) {
        // TODO: Handle action SaveInput
        InputBatch queryBatch = databaseServer.getInputBatch(batchno);
        if (queryBatch!=null){
            Intent localIntent =
                    new Intent(Constants.ACTION_SAVEINPUT)
                            .putExtra(Constants.EXTENDED_DATA_STATUS, Constants.RES_REPEAT);
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            return;
        }

        batchno="PP305192AC6035A68";

        InputBatch inputBatch = new InputBatch();
        inputBatch.setBatchno(batchno);
        app=(MyApplication)getApplication();
        inputBatch.setUserID(app.getUserID());
        inputBatch.setLocationList(new ArrayList<Location>());
        String[] locationNos = locationNo.split("\n");
        for(String locNo:locationNos){
            if (!locNo.isEmpty()) {
                Location location = new Location();
                location.setLocationno(locNo);
                inputBatch.getLocationList().add(location);
            }
        }
        long id= databaseServer.createInputBatch(inputBatch);


        if (id>0){

         /*
     * Creates a new Intent containing a Uri object
     * BROADCAST_ACTION is a custom Intent action
     */
            Intent localIntent =
                    new Intent(Constants.ACTION_SAVEINPUT)
                            // Puts the status into the Intent
                            .putExtra(Constants.EXTENDED_DATA_STATUS, Constants.RES_SUCCEES);
            // Broadcasts the Intent to receivers in this app.
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        }

    }

    /**
     * Handle action SaveInput in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSaveOutput(String batchno) {
        // TODO: Handle action SaveOutnput
        OutputBatch queryBatch = databaseServer.getOutputBatch(batchno);
        if (queryBatch!=null){
            Intent localIntent =
                    new Intent(Constants.ACTION_SAVEOUTPUT)
                            .putExtra(Constants.EXTENDED_DATA_STATUS, Constants.RES_REPEAT);
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            return;
        }
        OutputBatch outputBatch = new OutputBatch();
        outputBatch.setBatchno(batchno);
        app=(MyApplication)getApplication();
        outputBatch.setUserID(app.getUserID());
        long id= databaseServer.createOutputBatch(outputBatch);
        if (id>0){
            Intent localIntent =
                    new Intent(Constants.ACTION_SAVEOUTPUT)
                            .putExtra(Constants.EXTENDED_DATA_STATUS, Constants.RES_SUCCEES);
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        }

    }


    /**
     * 将xml文件解析成InputBatch对象
     * @param xmlStr
     * @return
     */
    private InputBatch getInputBatch(String xmlStr){
        InputBatch inputbatch=null;
        List<Location> locationlist=null;
        XmlPullParser pullParser = Xml.newPullParser();
        StringReader reader = new StringReader(xmlStr);

        try {
            pullParser.setInput(reader);
            int event = pullParser.getEventType();//触发第一个事件
            while(event!=XmlPullParser.END_DOCUMENT){
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        inputbatch = new InputBatch();
                        break;
                    case XmlPullParser.START_TAG:
                        if("LotNO".equals(pullParser.getName())){
                            inputbatch.setBatchno(pullParser.nextText());
                        }
                        if("StockNO".equals(pullParser.getName())){
                            locationlist=new ArrayList<Location>();
                        }
                        if(locationlist!=null){
                            if("NO".equals(pullParser.getName())){
                                Location location = new Location();
                                location.setLocationno(pullParser.nextText());
                                locationlist.add(location);
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if("StockNO".equals(pullParser.getName())){
                            inputbatch.setLocationList(locationlist);
                        }
                        break;
                }
                event = pullParser.next();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return inputbatch;
    }

    /**
     * 将list<InputBatch>转换成接口约定的xml格式字符串
     * @param inputBatchList
     * @return
     */
    private String generateXml(List<InputBatch> inputBatchList) {
        String xml="";
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8",true);
            serializer.startTag(null,"InputSend");
            for(InputBatch inputbatch:inputBatchList){
                serializer.startTag(null, "StockInfo");

                serializer.startTag(null, "LotNO");
                serializer.text(inputbatch.getBatchno());
                serializer.endTag(null, "LotNO");

                serializer.startTag(null, "UserID");
                serializer.text(inputbatch.getUserID());
                serializer.endTag(null, "UserID");

                serializer.startTag(null, "InputDate");
                serializer.text(inputbatch.getInputtime().toString());
                serializer.endTag(null, "InputDate");

                serializer.startTag(null, "StockNO");  //库位信息
                if(!inputbatch.getLocationList().isEmpty()){
                    for(Location location: inputbatch.getLocationList()){
                        serializer.startTag(null, "NO");
                        serializer.text(location.getLocationno());
                        serializer.endTag(null, "NO");
                    }
                }
                serializer.endTag(null, "StockNO");
                serializer.endTag(null, "StockInfo");
            }
            serializer.endTag(null, "InputSend");
            serializer.endDocument();
            xml=writer.toString();

        } catch (IOException e) {
            e.printStackTrace();
            xml="error";
        }

        return xml;
    }

    /**
     * 将list<OutputBatch>转换成接口约定的xml格式字符串
     * @param outputBatchList
     * @return
     */
    private String generateOutputXml(List<OutputBatch> outputBatchList) {
        String xml="";
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8",true);
            serializer.startTag(null,"OutputSend");
            for(OutputBatch outputbatch:outputBatchList){
                serializer.startTag(null, "StockInfo");

                serializer.startTag(null, "LotNO");
                serializer.text(outputbatch.getBatchno());
                serializer.endTag(null, "LotNO");

                serializer.startTag(null, "UserID");
                serializer.text(outputbatch.getUserID());
                serializer.endTag(null, "UserID");

                serializer.startTag(null, "OutputDate");
                serializer.text(outputbatch.getOutputtime().toString());
                serializer.endTag(null, "OutputDate");

                serializer.endTag(null, "StockInfo");
            }
            serializer.endTag(null, "OutputSend");
            serializer.endDocument();
            xml=writer.toString();

        } catch (IOException e) {
            e.printStackTrace();
            xml="error";
        }

        return xml;
    }

    /**
     * 将ksoap2框架调用webservice传回的SoapObject转换成Batch对象
     * @param obj
     * @return
     */
    private Batch parseSoapObjectToBatch(SoapObject obj){
        Batch batch=new InputBatch();
        SoapObject returnobj = (SoapObject)obj.getProperty("OutputQueryReturn");
        batch.setBatchno(returnobj.getProperty("LotNO").toString());
        SoapObject locationsobj = (SoapObject)returnobj.getProperty("StockNO");
        List<Location> locationList = new ArrayList<Location>();
        for(int i=0; i<locationsobj.getPropertyCount();i++){
            Location location = new Location();
            location.setLocationno(locationsobj.getProperty(i).toString());
            locationList.add(location);
        }
        batch.setLocationList(locationList);
        return batch;
    }

    /**
     * 将ksoap2框架调用webservice传回的SoapObject转换成Batch对象
     * @param obj
     * @return
     */
    private MessageRetrun parseSoapObjectToMessage(SoapObject obj,Boolean isinput ){
        MessageRetrun message=new MessageRetrun();
        SoapObject returnobj=null;
        if (isinput){
            returnobj = (SoapObject)obj.getProperty("InputReturn");

        }else{
            returnobj = (SoapObject)obj.getProperty("OutputReturn");
        }
        message.setMessage(returnobj.getProperty("Message").toString());
        SoapObject sucessobj = (SoapObject)returnobj.getProperty("Success");
        List<String> sucessList = new ArrayList<String>();
        for(int i=0; i<sucessobj.getPropertyCount();i++){
            sucessList.add(sucessobj.getProperty(i).toString());
        }
        message.setSuccessList(sucessList);
        SoapObject failedobj = (SoapObject)returnobj.getProperty("Failed");
        List<String> failedList = new ArrayList<String>();
        for(int i=0; i<failedobj.getPropertyCount();i++){
            failedList.add(failedobj.getProperty(i).toString());
        }
        message.setFailedList(failedList);
        return message;
    }




    private void initNotify(String title,String Message,int NOTIFICATIONS_ID){
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, MainActivity.class), 0);

        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title)
                .setContentText(Message)
                .setContentIntent(contentIntent)
                .getNotification();
        mNotifyMgr.notify(NOTIFICATIONS_ID, notification);
    }



}
