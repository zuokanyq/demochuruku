package com.example.timingsystem.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timingsystem.Constants;
import com.example.timingsystem.MainActivity;
import com.example.timingsystem.R;
import com.example.timingsystem.model.Location;
import com.example.timingsystem.model.OutputBatch;
import com.example.timingsystem.services.InputIntentService;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zebra.adc.decoder.Barcode2DWithSoft;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class OutputFragment extends KeyDwonFragment {

    private Button btn_Start;
    private Button btn_BnScan;
    @ViewInject(R.id.btn_Clear)
    private Button btn_Clear;
    @ViewInject(R.id.btn_submit)
    private Button btn_Submit;
    @ViewInject(R.id.list_view)
    private ListView listView;
    @ViewInject(R.id.tv_batch_number)
    private TextView tv_batch_number;

    private Thread thread;
    @ViewInject(R.id.svResult)
    private ScrollView svResult;
    private String init_barcode;
/*    int sussCount = 0;
    int failCount = 0;
    int errorCount = 0;*/

    private boolean threadStop = true;

    private boolean isCurrFrag = false;
    private boolean isLocationNumber = false;

    private MainActivity mContext;

    private SubmitStateReceiver mSubmitStateReceiver;
    private MyAdapter adapter;
    private List<Location> list;

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
            if (isLocationNumber) {   //扫描库位号
                strData += barCode;
                for (Location loc : list) {
                    if (strData.equals(loc.getLocationno()))
                        loc.setIsscan(true);
                }
                adapter.notifyDataSetChanged();
            } else {    //扫描结果是批次号
                tv_batch_number.setText(barCode);
                list.clear();
                Location loc1 = new Location();
                loc1.setLocationno("SZ");
                loc1.setIsscan(false);
                list.add(loc1);
               /* for(int li=0;li<3;li++){
                    Location loc=new Location();
                    loc.setLocationno("locno"+String.valueOf(li));
                    loc.setIsscan(false);
                    list.add(loc);
                }*/
                adapter.notifyDataSetChanged();

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
        View v = inflater.inflate(R.layout.fragment_output,
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

        list = new ArrayList<Location>();
        adapter = new MyAdapter();
        listView.setAdapter(adapter);

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

    private void doDecode() {

        if (mContext.mReader != null) {
            mContext.mReader.setScanCallback(mScanCallback);
        }
        thread = new DecodeThread();
        thread.start();
    }

    /**
     * 提交，将批号、库位号提交到数据库
     */
    private void submit() {
        //输入校验
        String BatchNo = tv_batch_number.getText().toString();
        if (BatchNo.isEmpty()) {
            Toast.makeText(getActivity(),
                    R.string.msg_batchNo_empty,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        for (Location location : list) {
            if (!location.getIsscan()) {
                Toast.makeText(getActivity(),
                        R.string.msg_LocationNO_notscan,
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }
        //确认窗
        AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle("提示")
                .setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputIntentService.startActionSaveOutput(mContext, tv_batch_number.getText().toString());
                    }
                })
                .setMessage("确认提交吗？").create();
        dialog.show();


    }

    private void clear() {
        list.clear();
        adapter.notifyDataSetChanged();
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
        IntentFilter statusIntentFilter = new IntentFilter(Constants.ACTION_SAVEOUTPUT);
        mSubmitStateReceiver = new SubmitStateReceiver();
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
            }
        }
    }

    /**
     * 自定义适配器，常见将Cursor或者其他的数据存储在一个集合中，效率提高！
     */
    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            /*
             * convertView为一个item条目对象，每个item滑过之后，创建的item对象有被重新销毁，为此安卓提供一种机制，
             * 创建的条目不被销毁而是供后来展现的item重复使用，大大提高了效率
             */
            View view = null;
            if (convertView == null) {  //如果没有可供重复使用的item View对象
                view = getActivity().getLayoutInflater().inflate(R.layout.list_item, null);
                //如果view的布局很复杂，可以将内部的控件保存下来
                TextView textView = (TextView) view.findViewById(R.id.locationNO);
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
                ViewSet set = new ViewSet();
                set.textView = textView;
                set.checkBox = checkBox;
                view.setTag(set);
            } else {
                view = convertView; //如果已经加载将重复使用
            }
            //不用重复的查找控件
            ViewSet views = (ViewSet) view.getTag();
            views.textView.setText(list.get(position).getLocationno());
            views.checkBox.setChecked(list.get(position).getIsscan());
            return view;
        }


    }

    //保存item控件
    static class ViewSet {
        TextView textView;
        CheckBox checkBox;
    }


}
