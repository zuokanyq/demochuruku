package com.example.timingsystem.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Layout;
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
    private String init_barcode;
    private boolean threadStop = true;
    private boolean isCurrFrag = false;
    private boolean isLocationNumber = false;

    private MainActivity mContext;

    private SubmitStateReceiver mSubmitStateReceiver;

    /**
     * 扫码回调函数
     * 根据isLocationNumber的值判断扫描的是库位号还是批次号进行分别处理
     */
    public Barcode2DWithSoft.ScanCallback mScanCallback = new Barcode2DWithSoft.ScanCallback() {
        @Override
        public void onScanComplete(int i, int length, byte[] data) {

            Log.i("ErDSoftScanFragment", "onScanComplete() i=" + i);

            if (!isCurrFrag) {
                return;
            }

            String strData = "";

            mContext.mReader.stopScan();

            String barCode = null;
            try {
                barCode = new String(data, "GBK").trim();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (isLocationNumber) {

                strData += barCode + "\n";

                tv_Result.append(strData);

                mContext.scrollToBottom(svResult, tv_Result);
            } else {
             //   barCode="PP1930B8AE6217U24";
                tv_batch_number.setText(barCode);

            }
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

        btn_Start = (Button) v.findViewById(R.id.btn_Location_Scan);
        btn_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isLocationNumber = true;
                doDecode();
            }
        });

        btn_BnScan = (Button) v.findViewById(R.id.btn_BnScan);
        btn_BnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isLocationNumber = false;
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
        AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle("提示")
                .setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clear();
                    }
                })
                .setMessage("确认清空所有数据吗？").create();
        dialog.show();

    }

    @OnClick(R.id.btn_Location_Clear)
    public void btn_location_Clear_onClick(View v) {
        tv_Result.setText("");
    }

    @OnClick(R.id.btn_Location_Clearlast)
    public void btn_location_Clearlast_onClick(View v) {
        String locationNo = tv_Result.getText().toString();
        String resLoc = "";
        if (locationNo.indexOf("\n") > 0) {
            locationNo = locationNo.substring(0, locationNo.length() - 1);
            if (locationNo.indexOf("\n") > 0) {
                resLoc = locationNo.substring(0, locationNo.lastIndexOf("\n") + 1);
            }
        }

        tv_Result.setText(resLoc);
    }

    private void doDecode() {
//        isLocationNumber=true;

        if (mContext.mReader != null) {

            mContext.mReader.setScanCallback(mScanCallback);
        }
        Log.i("ErDSoftScanFragment", "doDecode() threadStop=" + threadStop);

        thread = new DecodeThread();
        thread.start();

    }


    private void submit() {

        //数据有效性验证，排除空值
        String BatchNo = tv_batch_number.getText().toString();
        if (BatchNo.isEmpty()) {
            Toast.makeText(getActivity(),
                    R.string.msg_batchNo_empty,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String locationNos = tv_Result.getText().toString();
        if (locationNos.isEmpty()) {
            Toast.makeText(getActivity(),
                    R.string.msg_LocationNO_empty,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //确认窗
        AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle("提示")
                .setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputIntentService.startActionSaveInput(mContext, tv_batch_number.getText().toString(), tv_Result.getText().toString());
                    }
                })
                .setMessage("确认提交吗？").create();
        dialog.show();
    }


    private void clear() {

        tv_Result.setText("");
        tv_batch_number.setText("");
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

        isCurrFrag = false;

        threadStop = true;
        btn_Start.setText(getString(R.string.input_btn_location_scan));

        btn_Clear.setEnabled(true);

        if (mContext.mReader != null) {

            mContext.mReader.stopScan();


        }
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mSubmitStateReceiver);

    }


    @Override
    public void onResume() {
        super.onResume();

        isCurrFrag = true;
        //动态注册广播接收器
        IntentFilter statusIntentFilter = new IntentFilter(Constants.ACTION_SAVEINPUT);
        // Instantiates a new DownloadStateReceiver
        mSubmitStateReceiver = new SubmitStateReceiver();
        // Registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(mContext).registerReceiver(
                mSubmitStateReceiver,
                statusIntentFilter);


    }


    @Override
    public void myOnKeyDwon(Boolean isScanKey) {
        isLocationNumber = isScanKey;
        doDecode();
    }

    // Broadcast receiver for receiving status updates from the IntentService
    private class SubmitStateReceiver extends BroadcastReceiver {
        // Prevents instantiation
        private SubmitStateReceiver() {
        }

        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(Context context, Intent intent) {
            String i = "";
            if (Constants.RES_SUCCEES.equals(intent.getStringExtra(Constants.EXTENDED_DATA_STATUS))) {

                Toast.makeText(getActivity(),
                        R.string.msg_submit_success,
                        Toast.LENGTH_SHORT).show();
                clear();
            }else if (Constants.RES_REPEAT.equals(intent.getStringExtra(Constants.EXTENDED_DATA_STATUS))){
                Toast.makeText(getActivity(),
                        R.string.msg_submit_repeat,
                        Toast.LENGTH_SHORT).show();
                clear();
            }

        /*
         * Handle Intents here.
         */

        }
    }


}
