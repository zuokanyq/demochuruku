package com.example.timingsystem.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Xml;

import com.example.timingsystem.Constants;
import com.example.timingsystem.helper.InputServer;
import com.example.timingsystem.model.InputBatch;
import com.example.timingsystem.model.InputLocation;

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
    private static final String ACTION_PUSHINPUT = "com.example.timingsystem.services.action.PUSHINPUT";
    private static final String ACTION_GETDATA = "com.example.timingsystem.services.action.GETDATA";
    private static final String ACTION_SAVEINPUT = "com.example.timingsystem.services.action.SAVEINPUT";

    // TODO: Rename parameters
    private static final String EXTRA_BATCHNO = "com.example.timingsystem.services.extra.BATCHNO";
    private static final String EXTRA_LOCATIONNOS = "com.example.timingsystem.services.extra.LOCATIONNOS";

    private InputServer inputServer;
    private SharedPreferences pref;

    public InputIntentService() {
        super("InputIntentService");
        inputServer=new InputServer(this);
  //      pref = PreferenceManager.getDefaultSharedPreferences(this);
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionPushInput(Context context) {
        Intent intent = new Intent(context, InputIntentService.class);
        intent.setAction(ACTION_PUSHINPUT);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionGetData(Context context, String param1) {
        Intent intent = new Intent(context, InputIntentService.class);

        intent.setAction(ACTION_SAVEINPUT);
        intent.putExtra(EXTRA_BATCHNO, param1);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionSaveInput(Context context, String batchNo, String locationNos) {
        Intent intent = new Intent(context, InputIntentService.class);

        intent.setAction(ACTION_SAVEINPUT);
        intent.putExtra(EXTRA_BATCHNO, batchNo);
        intent.putExtra(EXTRA_LOCATIONNOS, locationNos);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PUSHINPUT.equals(action)) {
                handleActionPushInput();
            } else if (ACTION_GETDATA.equals(action)) {
                final String batchNO = intent.getStringExtra(EXTRA_BATCHNO);
                handleActionGetData(batchNO);
            } else if (ACTION_SAVEINPUT.equals(action)) {
                final String batchNO = intent.getStringExtra(EXTRA_BATCHNO);
                final String locationNos = intent.getStringExtra(EXTRA_LOCATIONNOS);

                handleActionSaveInput(batchNO,locationNos);
            }
        }
    }

    /**
     * Handle action PushInput in the provided background thread with the provided
     * parameters.
     */
    private void handleActionPushInput() {
        // TODO: Handle action PushInput
        //debug
        insertInputData();
        //

        List<InputBatch> inputBatchList=inputServer.getInputBatchList();

        //debug
        Integer size1=inputBatchList.size();
        //size1=3

        String inputxml = generateXml(inputBatchList);

        String url = "http://192.168.168.196:7676/SRC/business/mobilemanagement.asmx";
        String webMethName = "Input";
        Map<String, String> paramsmap=new HashMap<String, String>();
        paramsmap.put("InputSend",inputxml);

        //debug
     //   String obj="success";
        SoapObject obj = CallWebService.invokeInputWS(url,webMethName,paramsmap);

        if("Success".equals(obj.getProperty("resMsg"))){
            InputBatch inputBatch= parseSoapObject(obj);
            inputServer.deleteInputBatch(inputBatchList);
        }else if ("Error".equals((obj.getProperty("resMsg")))){
            //Webservice 调用过程中出错
        }
        else{

        }
        List<InputBatch> inputBatchListend=inputServer.getInputBatchList();
        //debug
        Integer size2=inputBatchListend.size();
        //size2=0
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
        InputBatch inputBatch = new InputBatch();
        inputBatch.setBatchno(batchno);
        inputBatch.setLocationList(new ArrayList<InputLocation>());
        String[] locationNos = locationNo.split("\n");
        for(String locNo:locationNos){
            if (!locNo.isEmpty()) {
                InputLocation location = new InputLocation();
                location.setLocationno(locNo);
                inputBatch.getLocationList().add(location);
            }
        }

        long id= inputServer.createInputBatch(inputBatch);


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


    private void insertInputData(){
        for (int j=0;j<3;j++){

            InputBatch inputBatch = new InputBatch();
            inputBatch.setBatchno("RE4332R09_"+String.valueOf(j));
            inputBatch.setLocationList(new ArrayList<InputLocation>());
            for (int i=0;i<3;i++){
                InputLocation inputLocation=new InputLocation();
                inputLocation.setLocationno("LocatNo_"+String.valueOf(j)+"_"+String.valueOf(i));
                inputBatch.getLocationList().add(inputLocation);
            }
            long id= inputServer.createInputBatch(inputBatch);
        }
    }

    private InputBatch getInputBatch(String xmlStr){
        InputBatch inputbatch=null;
        List<InputLocation> locationlist=null;
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
                            locationlist=new ArrayList<InputLocation>();
                        }
                        if(locationlist!=null){
                            if("NO".equals(pullParser.getName())){
                                InputLocation location = new InputLocation();
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
                serializer.text("aaa");
                serializer.endTag(null, "UserID");

                serializer.startTag(null, "InputDate");
                serializer.text(inputbatch.getInputtime().toString());
                serializer.endTag(null, "InputDate");

                serializer.startTag(null, "StockNO");  //库位信息
                if(!inputbatch.getLocationList().isEmpty()){
                    for(InputLocation location: inputbatch.getLocationList()){
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

    private InputBatch parseSoapObject(SoapObject obj){
        InputBatch inputBatch=new InputBatch();
        SoapObject returnobj = (SoapObject)obj.getProperty("OutputQueryReturn");
        inputBatch.setBatchno(returnobj.getProperty("LotNO").toString());
        SoapObject locationsobj = (SoapObject)returnobj.getProperty("StockNO");
        List<InputLocation> locationList = new ArrayList<InputLocation>();
        for(int i=0; i<locationsobj.getPropertyCount();i++){
            InputLocation location = new InputLocation();
            location.setLocationno(locationsobj.getProperty(i).toString());
            locationList.add(location);
        }
        inputBatch.setLocationList(locationList);
        return inputBatch;
    }

}
