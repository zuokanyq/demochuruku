package com.example.timingsystem.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timingsystem.Constants;
import com.example.timingsystem.MainActivity;
import com.example.timingsystem.R;
import com.example.timingsystem.services.InputIntentService;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zebra.adc.decoder.Barcode2DWithSoft;

import java.io.UnsupportedEncodingException;


public class InputFragment extends KeyDwonFragment {


//    @ViewInject(R.id.btn_Start)
    private Button btn_Start;
    private Button btn_BnScan;
    @ViewInject(R.id.btn_Clear)
    private Button btn_Clear;
    @ViewInject(R.id.btn_submit)
    private Button btn_Submit;
    @ViewInject(R.id.tv_result)
    private TextView tv_Result;
    @ViewInject(R.id.tv_batch_number)
    private TextView tv_batch_number;

    private Thread thread;
    @ViewInject(R.id.svResult)
    private ScrollView svResult;
  /*  @ViewInject(R.id.et_init_barcode)
    private EditText et_init_barcode;*/
    private String init_barcode;
    int sussCount = 0;
    int failCount = 0;
    int errorCount = 0;

    private boolean threadStop = true;

    private boolean isCurrFrag = false;
    private boolean isLocationNumber = false;

    private MainActivity mContext;

    private SubmitStateReceiver mSubmitStateReceiver;

    public Barcode2DWithSoft.ScanCallback mScanCallback = new Barcode2DWithSoft.ScanCallback() {
        @Override
        public void onScanComplete(int i, int length, byte[] data) {

            Log.i("ErDSoftScanFragment","onScanComplete() i="+i);

            if(!isCurrFrag)
            {
                return;
            }

            String strData = "";

           /* if ((sussCount + errorCount + failCount) % 1000 == 0) {
                tv_Result.setText("");
            }*/

            if (length < 1) {

                failCount += 1;
                Toast.makeText(getActivity(),
                        "",
                        Toast.LENGTH_SHORT).show();
//                strData = getString(R.string.yid_msg_scan_fail) + "\n";
//
//                tv_Result.append(strData);


                return;
            }

            mContext.mReader.stopScan();

            String barCode = null;
            try {
                barCode = new String(data,"GBK").trim();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if(isLocationNumber) {

                strData += barCode + "\n";

                tv_Result.append(strData);

                mContext.scrollToBottom(svResult, tv_Result);
            } else {

                tv_batch_number.setText(barCode);

            }


//            stat();
        }
    };


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = (MainActivity) getActivity();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_input,
                container, false);
        ViewUtils.inject(this, v);

        btn_Start= (Button) v.findViewById(R.id.btn_Location_Scan);
        btn_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isLocationNumber=true;
                doDecode();
            }
        });

        btn_BnScan=(Button)v.findViewById(R.id.btn_BnScan);
        btn_BnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isLocationNumber=false;
                doDecode();
            }
        });

        Log.i("ErDSoftScanFragment", "onCreateView() threadStop=" + threadStop);
        return v;

    }


    @OnClick(R.id.btn_submit)
    public void btn_Submit_onClick(View v) {
        submit();
    }

     @OnClick(R.id.btn_Clear)
    public void btn_Clear_onClick(View v) {
        clear();
    }

    private void doDecode() {
//        isLocationNumber=true;

        if (mContext.mReader != null) {

            mContext.mReader.setScanCallback(mScanCallback);
        }
        Log.i("ErDSoftScanFragment","doDecode() threadStop="+threadStop);

            thread = new DecodeThread();
            thread.start();

    }


    private void submit() {
        String BatchNo = tv_batch_number.getText().toString();
        if(BatchNo.isEmpty()){
            Toast.makeText(getActivity(),
                    R.string.msg_batchNo_empty,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String locationNos=tv_Result.getText().toString();
        if(locationNos.isEmpty()){
            Toast.makeText(getActivity(),
                    R.string.msg_LocationNO_empty,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        InputIntentService.startActionSaveInput(mContext,BatchNo,locationNos);

       /* Toast.makeText(getActivity(),
                R.string.msg_submit_success,
                Toast.LENGTH_SHORT).show();*/

    }



    private void clear() {
        tv_Result.setText("");
        tv_batch_number.setText("");
        int total = 0;
        sussCount = 0;
        failCount = 0;
        errorCount = 0;

        btn_Start.setText(getString(R.string.input_btn_start_scan));
        threadStop = true;

    }

    private class DecodeThread extends Thread {
        public DecodeThread() {
        }

        @Override
        public void run() {
            super.run();
                mContext.mReader.scan();
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        isCurrFrag=false;

        threadStop = true;
        btn_Start.setText(getString(R.string.input_btn_start_scan));

        btn_Clear.setEnabled(true);

        if (mContext.mReader != null) {

            mContext.mReader.stopScan();


        }
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mSubmitStateReceiver);

    }



    @Override
    public void onResume() {
        super.onResume();

        isCurrFrag=true;
        //动态注册广播接收器
        IntentFilter statusIntentFilter = new IntentFilter(Constants.ACTION_SAVEINPUT);
        // Adds a data filter for the HTTP scheme
     //   statusIntentFilter.addDataScheme("http");
        // Instantiates a new DownloadStateReceiver
        mSubmitStateReceiver = new SubmitStateReceiver();
        // Registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(mContext).registerReceiver(
                mSubmitStateReceiver,
                statusIntentFilter);


    }


    @Override
    public void myOnKeyDwon() {
        isLocationNumber=true;
        doDecode();
    }

    // Broadcast receiver for receiving status updates from the IntentService
    private class SubmitStateReceiver extends BroadcastReceiver
    {
        // Prevents instantiation
        private SubmitStateReceiver() {
        }
        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(Context context, Intent intent) {
            String i="";
           if(Constants.RES_SUCCEES.equals(intent.getStringExtra(Constants.EXTENDED_DATA_STATUS))){

               Toast.makeText(getActivity(),
                       R.string.msg_submit_success,
                       Toast.LENGTH_SHORT).show();
               clear();
           }
        /*
         * Handle Intents here.
         */

        }
    }


}
