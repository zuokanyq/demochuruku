package com.example.timingsystem.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.timingsystem.R;
import com.example.timingsystem.services.InputIntentService;
import com.example.timingsystem.services.LongRunningService;

public class ConnectionChangeReceiver extends BroadcastReceiver {

    private static final String TAG = ConnectionChangeReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {


        boolean success = false;

        //获得网络连接服务
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager != null){
            NetworkInfo.State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            // 判断是否正在使用WIFI网络
            if (NetworkInfo.State.CONNECTED == state) {
                success = true;
              //  InputIntentService.startActionPushInput(context);
               // InputIntentService.startActionPushOutput(context);
            }

        }

    }
}
