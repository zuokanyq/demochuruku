package com.example.timingsystem.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timingsystem.MainActivity;
import com.example.timingsystem.R;
import com.example.timingsystem.helper.DatabaseServer;
import com.example.timingsystem.model.Batch;
import com.example.timingsystem.model.InputBatch;
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
                AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle("提示")
                        .setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                InputIntentService.startActionPushInputOne(mContext,inputbatch.getId());
                            }
                        })
                        .setMessage("您确定要上传该条数据吗？").create();
                dialog.show();
            }
        });


        return v;

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
        AsyncCallData task = new AsyncCallData();
        task.execute();
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
        protected Void doInBackground(String... params) {

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

}
