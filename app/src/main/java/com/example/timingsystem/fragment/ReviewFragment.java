package com.example.timingsystem.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timingsystem.Constants;
import com.example.timingsystem.R;

import com.example.timingsystem.MainActivity;

import com.example.timingsystem.helper.DatabaseServer;
import com.example.timingsystem.model.Batch;
import com.example.timingsystem.model.InputBatch;
import com.example.timingsystem.model.Location;
import com.example.timingsystem.model.OutputBatch;
import com.example.timingsystem.services.InputIntentService;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;


public class ReviewFragment extends KeyDwonFragment {

    @ViewInject(R.id.list_input)
    private ListView listviewinput;
    @ViewInject(R.id.list_output)
    private ListView listviewoutput;

    private MainActivity mContext;

    private BatchAdapter inputadapter;
    private BatchAdapter outputadapter;
    private List<Batch> inputlist;
    private List<Batch> outputlist;

    private ProgressDialog dialog = null;
    private DatabaseServer dbserver;
    private View reviewView;
    private View popview;
    private PopupWindow popupWindow;
    private Button btn_delete;
    private Button btn_push;
    private Button btn_cancel;

    private PushOneStateReceiver mPushOneStateReceiver;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = (MainActivity) getActivity();
        dbserver = new DatabaseServer(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_review,
                container, false);
        ViewUtils.inject(this, v);
        reviewView=v;
        inputlist = new ArrayList<Batch>();
        outputlist = new ArrayList<Batch>();
        inputadapter = new BatchAdapter(inputlist);
        outputadapter = new BatchAdapter(outputlist);
        listviewinput.setAdapter(inputadapter);
        listviewoutput.setAdapter(outputadapter);

        listviewinput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Object o = listviewinput.getItemAtPosition(position);
                final InputBatch inputbatch=(InputBatch)o;
                InputBatch detailInputBatch = dbserver.getInputBatch(inputbatch.getId());
                showWindow(view,true,detailInputBatch,null);
                /*AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle("提示")
                        .setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                InputIntentService.startActionPushInputOne(mContext,inputbatch.getId());
                            }
                        })
                        .setMessage("您确定要上传该条数据吗？").create();
                dialog.show();*/
            }
        });


        return v;

    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mPushOneStateReceiver);

    }

    @Override
    public void onResume() {
        super.onResume();
        AsyncCallData task = new AsyncCallData();
        task.execute();
        IntentFilter statusIntentFilter = new IntentFilter(Constants.ACTION_PUSHINPUT);
        mPushOneStateReceiver = new PushOneStateReceiver();
        LocalBroadcastManager.getInstance(mContext).registerReceiver(
                mPushOneStateReceiver,
                statusIntentFilter);

    }

    @Override
    public void myOnKeyDwon(Boolean isScanKey) {

    }

    /**
     * 自定义适配器，常见将Cursor或者其他的数据存储在一个集合中，效率提高！
     */
    private class BatchAdapter extends BaseAdapter {

        private List<Batch> list ;

        BatchAdapter(List<Batch> listbatch){
            this.list=listbatch;
        }

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
            View view = null;
            if (convertView == null) {  //如果没有可供重复使用的item View对象
                view = getActivity().getLayoutInflater().inflate(R.layout.list_review_item, null);
                //如果view的布局很复杂，可以将内部的控件保存下来
                TextView batchNoView = (TextView) view.findViewById(R.id.batchNO);
                TextView reasonView = (TextView) view.findViewById(R.id.failedtreason);
                ViewSet set = new ViewSet();
                set.batchNoView = batchNoView;
                set.reasonView = reasonView;
                view.setTag(set);
            } else {
                view = convertView; //如果已经加载将重复使用
            }
            //不用重复的查找控件
            ViewSet views = (ViewSet) view.getTag();
            views.batchNoView.setText(list.get(position).getBatchno());
            views.reasonView.setText(list.get(position).getFailedreason());
            return view;
        }

        public void changlist(List<Batch> listbatch){
            this.list=listbatch;
            super.notifyDataSetChanged();
        }

    }
   //保存item控件
    static class ViewSet {

       TextView batchNoView;
       TextView reasonView;
   }


    private boolean getNetworkState(){
        boolean success = false;
        //获得网络连接服务
        ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager != null){
            NetworkInfo.State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            // 判断是否正在使用WIFI网络
            if (NetworkInfo.State.CONNECTED == state) {
                success = true;
            }
        }
        return success;
    }


    private class AsyncCallData extends AsyncTask<String, Void, Void> {


        @Override
        protected Void  doInBackground(String... params) {

            inputlist=new ArrayList<Batch>(dbserver.getInputBatchList());
            outputlist=new ArrayList<Batch>(dbserver.getOutputBatchList());

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {

            //Make ProgressBar invisible
            dialog.dismiss();//关闭ProgressDialog
            inputadapter.changlist(inputlist);
            outputadapter.changlist(outputlist);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(mContext, "加载", "正在加载数据，请稍等...", false);//创建ProgressDialog
        }

    }

    private void showWindow(View parent, final Boolean isinput, final InputBatch detailInputBatch, final OutputBatch detailOutputBatch) {

            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if(isinput){
                popview = layoutInflater.inflate(R.layout.popwin_input, null);
                inputTextViewSetValue(popview,detailInputBatch);
            }else{
                popview = layoutInflater.inflate(R.layout.popwin_output, null);
                outputTextViewSetValue(popview,detailOutputBatch);
            }
        btn_delete = (Button)popview.findViewById(R.id.btn_delete);
        btn_push = (Button)popview.findViewById(R.id.btn_push);;
        btn_cancel = (Button)popview.findViewById(R.id.btn_cancel);

        //为各文本框赋值

            // 创建一个PopuWidow对象
            popupWindow = new PopupWindow(popview, WindowManager.LayoutParams.MATCH_PARENT,610);

        // 使其聚集
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
   //     popupWindow.setClippingEnabled(false);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        ColorDrawable dw255 = new ColorDrawable(Color.WHITE);
        dw255.setAlpha(255);

        popupWindow.setBackgroundDrawable(dw255);

        popupWindow.showAtLocation(reviewView, Gravity.BOTTOM | Gravity.LEFT,0,0);

        ColorDrawable dw120 = new ColorDrawable(Color.WHITE);
        dw120.setAlpha(120);
        mContext.getWindow().setBackgroundDrawable(dw120);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener(){
            @Override
            public void onDismiss() {
                // TODO Auto-generated method stub
                ColorDrawable dw255 = new ColorDrawable(Color.WHITE);
                dw255.setAlpha(255);
                mContext.getWindow().setBackgroundDrawable(dw255);
            }
        });


        btn_delete.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle("提示")
                        .setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int resint=0;
                                if(isinput){
                                    resint= dbserver.deleteInputBatch(detailInputBatch);
                                }else{
                                    resint= dbserver.deleteOutputBatch(detailOutputBatch);
                                }
                                String resStr="删除失败";
                               if(resint>0){resStr="删除成功";}
                                Toast.makeText(mContext,resStr, Toast.LENGTH_SHORT).show();
                                if (popupWindow != null) {
                                    popupWindow.dismiss();
                                }
                                AsyncCallData task = new AsyncCallData();
                                task.execute();
                            }
                        })
                        .setMessage("您确定要删除该条数据吗？").create();
                dialog.show();

            }
        });

        btn_push.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle("提示")
                        .setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(isinput){
                                    InputIntentService.startActionPushInputOne(mContext,detailInputBatch.getId());
                                }else {
                                    InputIntentService.startActionPushOutputOne(mContext, detailOutputBatch.getId());
                                }
                                if (popupWindow != null) {
                                    popupWindow.dismiss();
                                }
                            }
                        })
                        .setMessage("您确定要推送该条数据吗？").create();
                dialog.show();
            }
        });

        btn_cancel.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (popupWindow != null) {
                    popupWindow.dismiss();
                }
            }
        });
    }

    private void outputTextViewSetValue(View popview, OutputBatch detailOutputBatch) {
        TextView tv_batchNo = (TextView) popview.findViewById(R.id.tv_batch_number);
        TextView tv_outputtime = (TextView) popview.findViewById(R.id.tv_outputtime);
        TextView tv_userid = (TextView) popview.findViewById(R.id.tv_userid);
        TextView tv_isfailed = (TextView) popview.findViewById(R.id.tv_isfailed);
        TextView tv_reasonView = (TextView) popview.findViewById(R.id.tv_failedreason);

        tv_batchNo.setText(detailOutputBatch.getBatchno());
        tv_userid.setText(detailOutputBatch.getUserID());
        tv_outputtime.setText(detailOutputBatch.getOutputtime().toString());
        String isfailed = "";
        if (detailOutputBatch.getIsfailed()==1){ isfailed="上传失败"; }
        tv_isfailed.setText(isfailed);
        tv_reasonView.setText(detailOutputBatch.getFailedreason());
    }

    private void inputTextViewSetValue(View popview, InputBatch detailInputBatch) {
        TextView tv_batchNo = (TextView) popview.findViewById(R.id.tv_batch_number);
        TextView tv_locationNo = (TextView) popview.findViewById(R.id.tv_locationNo);
        TextView tv_inputtime = (TextView) popview.findViewById(R.id.tv_inputtime);
        TextView tv_userid = (TextView) popview.findViewById(R.id.tv_userid);
        TextView tv_isfailed = (TextView) popview.findViewById(R.id.tv_isfailed);
        TextView tv_reasonView = (TextView) popview.findViewById(R.id.tv_failedreason);

        tv_batchNo.setText(detailInputBatch.getBatchno());
        String locationNOs="";
        if (detailInputBatch.getLocationList()!=null){
            for (Location location: detailInputBatch.getLocationList()){
                locationNOs+= location.getLocationno()+"\n";
            }
        }
        tv_locationNo.setText(locationNOs);
        tv_userid.setText(detailInputBatch.getUserID());
        tv_inputtime.setText(detailInputBatch.getInputtime().toString());
        String isfailed = "";
        if (detailInputBatch.getIsfailed()==1){ isfailed="上传失败"; }
        tv_isfailed.setText(isfailed);
        tv_reasonView.setText(detailInputBatch.getFailedreason());
    }


    // Broadcast receiver for receiving status updates from the IntentService
    private class PushOneStateReceiver extends BroadcastReceiver {
        // Prevents instantiation
        private PushOneStateReceiver() {
        }

        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getActivity(),
                    intent.getStringExtra(Constants.EXTENDED_DATA_STATUS),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
