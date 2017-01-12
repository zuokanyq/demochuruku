package com.example.timingsystem.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.timingsystem.Constants;
import com.example.timingsystem.MainActivity;
import com.example.timingsystem.R;
import com.example.timingsystem.UIHelper;
import com.example.timingsystem.services.InputIntentService;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class TestFragment extends KeyDwonFragment {


	@ViewInject(R.id.btnGet)
	private Button btnGet;
	@ViewInject(R.id.btnSet)
	private Button btnSet;

	private SubmitStateReceiver mSubmitStateReceiver;

	private MainActivity mContext;

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mContext = (MainActivity) getActivity();
		//动态注册广播接收器
		IntentFilter statusIntentFilter = new IntentFilter(Constants.ACTION_SAVEINPUT);
		// Adds a data filter for the HTTP scheme
	//	statusIntentFilter.addDataScheme("http");
		// Instantiates a new DownloadStateReceiver
		mSubmitStateReceiver = new SubmitStateReceiver();
		// Registers the DownloadStateReceiver and its intent filters
		LocalBroadcastManager.getInstance(mContext).registerReceiver(
				mSubmitStateReceiver,
				statusIntentFilter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_test, container,
				false);
		ViewUtils.inject(this, v);
		return v;
	}

	@OnClick(R.id.btnGet)
	public void btnGet_onClick(View v) {
		// get param #

		Intent localIntent =
				new Intent(Constants.ACTION_SAVEINPUT)
						// Puts the status into the Intent
						.putExtra(Constants.EXTENDED_DATA_STATUS, Constants.RES_SUCCEES);
		// Broadcasts the Intent to receivers in this app.
		LocalBroadcastManager.getInstance(mContext).sendBroadcast(localIntent);


	}

	@OnClick(R.id.btnSet)
	public void btnSet_onClick(View v) {
		InputIntentService.startActionPushInput(mContext);
	}

	@Override
	public void myOnKeyDwon() {

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

			}
        /*
         * Handle Intents here.
         */

		}
	}
}
