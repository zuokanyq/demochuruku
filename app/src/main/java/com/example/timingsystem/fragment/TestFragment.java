package com.example.timingsystem.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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

	private MainActivity mContext;

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mContext = (MainActivity) getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_er_dsoft_set, container,
				false);
		ViewUtils.inject(this, v);
		return v;
	}

	@OnClick(R.id.btnGet)
	public void btnGet_onClick(View v) {
		// get param #

		InputIntentService.startActionGetData(mContext, null);
	}

	@OnClick(R.id.btnSet)
	public void btnSet_onClick(View v) {
		InputIntentService.startActionPushInput(mContext);
	}

	@Override
	public void myOnKeyDwon() {

	}
}
