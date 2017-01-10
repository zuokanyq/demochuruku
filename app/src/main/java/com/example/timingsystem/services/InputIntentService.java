package com.example.timingsystem.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Xml;

import com.example.timingsystem.MainActivity;
import com.example.timingsystem.helper.InputServer;
import com.example.timingsystem.model.InputBatch;
import com.example.timingsystem.model.InputLocation;
import com.example.timingsystem.model.MdSHMobileUserInfo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
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

    // TODO: Rename parameters
    private static final String EXTRA_GETPARAM = "com.example.timingsystem.services.extra.GETPARAM";

    private InputServer inputServer;
    private SharedPreferences pref;

    public InputIntentService() {
        super("InputIntentService");
        inputServer=new InputServer(this);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
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

        intent.setAction(ACTION_GETDATA);
        intent.putExtra(EXTRA_GETPARAM, param1);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PUSHINPUT.equals(action)) {
                handleActionPushInput();
            } else if (ACTION_GETDATA.equals(action)) {
                final String batchNO = intent.getStringExtra(EXTRA_GETPARAM);
                handleActionGetData(batchNO);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
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
        String webMethName = "HasMobilePermission";
        Map<String, String> paramsmap=new HashMap<String, String>();
        paramsmap.put("InputSend",inputxml);

        //debug
        String obj="success";
     //   String obj = CallWebService.invokeLoginWS(url,webMethName,paramsmap);

        if("success".equals(obj)){
            inputServer.deleteInputBatch(inputBatchList);
        }
        List<InputBatch> inputBatchListend=inputServer.getInputBatchList();
        //debug
        Integer size2=inputBatchListend.size();
        //size2=0
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
           //     serializer.text(pref.getString("account", ""));
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
            xml=serializer.toString();

        } catch (IOException e) {
            e.printStackTrace();
            xml="error";
        }

        return xml;
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionGetData(String param1) {
        // TODO: Handle action GetData
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

                        break;
                }
                event = pullParser.next();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return inputbatch;
    }


}
