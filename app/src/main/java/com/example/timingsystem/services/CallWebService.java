package com.example.timingsystem.services;
import com.example.timingsystem.model.MdSHMobileUserInfo;


import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by zuokanyq on 2017-1-5.
 */

public class CallWebService {
    //Namespace of the Webservice - It is http://tempuri.org for .NET webservice
    private static String NAMESPACE = "http://tempuri.org/";

    //SOAP Action URI again http://tempuri.org
    private static String SOAP_ACTION = "http://tempuri.org/";

    public static SoapObject invokeInputWS(String url, String webMethName, Map<String, String> Params) {
        SoapObject resobj = null;

        String webServiceUrl="http://"+url.trim()+"/SRC/business/mobilemanagement.asmx";
        // Create request
        SoapObject request = new SoapObject(NAMESPACE, webMethName);
        // 2、设置调用方法的参数值，如果没有参数，可以省略，
        if (Params != null) {
            Iterator iter = Params.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                PropertyInfo pi = new PropertyInfo();
                pi.setName((String) entry.getKey());
                pi.setValue((String) entry.getValue());
                pi.setType(PropertyInfo.STRING_CLASS);
                request.addProperty(pi);

            }
        }
        // Create envelope
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        //Set envelope as dotNet
        envelope.dotNet = true;

        // Set output SOAP object
        envelope.setOutputSoapObject(request);

        // Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(webServiceUrl);
     //   envelope.bodyOut = androidHttpTransport;

        try {
            // Invoke web service
      //      androidHttpTransport.debug = true;
            androidHttpTransport.call(SOAP_ACTION+webMethName, envelope);

//            String requestDump = androidHttpTransport.requestDump;
//            String responseDump = androidHttpTransport.responseDump;
            // Get the response
            resobj = (SoapObject) envelope.getResponse();
            resobj.addProperty("resMsg", "Success");

        } catch (Exception e) {
            //Print error
            e.printStackTrace();
            //Assign error message to resTxt
            resobj=new SoapObject("", "Error");
            resobj.addProperty("resMsg","Error");
        }
        //Return resTxt to calling object
        return resobj;
    }

}
