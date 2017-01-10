package com.example.timingsystem;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Xml;

import com.example.timingsystem.helper.InputServer;
import com.example.timingsystem.model.InputBatch;
import com.example.timingsystem.model.InputLocation;
import com.example.timingsystem.services.InputIntentService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;


/**
 * Created by zuokanyq on 2017-1-5.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class TestInputxml {
    private InputServer inputServer;

    @Before
    public void createInputServer() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        inputServer = new InputServer(appContext);
    }


    public void generateXmlTest() {
        insertInputData();

        List<InputBatch> inputBatchList=inputServer.getInputBatchList();

        assertThat(inputBatchList.size(), is(3));

        String inputxml = generateXml(inputBatchList);

        inputServer.deleteInputBatch(inputBatchList);

        List<InputBatch> inputBatchListno= inputServer.getInputBatchList();
        assertThat(inputBatchListno.size(), is(0));

    }


    @Test
    public void parserXmlTest() {
        String xmlstr="<OutputQueryReturn>" +
                "<LotNO>123</LotNO>" +
                "<StockNO>" +
                "<NO>1-1-1-1</NO>" +
                "<NO>2-2-2-2</NO>" +
                "</StockNO>" +
                "</OutputQueryReturn>";
        InputBatch inputBatch = getInputBatch(xmlstr);

        assertThat(inputBatch.getBatchno(), is("123"));

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
            xml=writer.toString();

        } catch (IOException e) {
            e.printStackTrace();
            xml="error";
        }

        return xml;
    }

}
