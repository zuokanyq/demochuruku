package com.example.timingsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.example.timingsystem.fragment.InputFragment;
import com.example.timingsystem.fragment.OutputFragment;
import com.example.timingsystem.fragment.ReviewFragment;
import com.example.timingsystem.services.LongRunningService;
import com.rscja.utility.StringUtility;
import com.zebra.adc.decoder.Barcode2DWithSoft;

/**
 * 基于wushengjun的二维软解码使用demo修改而成
 * 
 * @author zuokanyq
 *
 */
public class MainActivity extends BaseTabFragmentActivity {

	private final static String TAG = "MainActivity";

	public Barcode2DWithSoft mReader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initViewPageData();
		initViewPager();
		initTabs();

//		启动定时服务
		Intent intent = new Intent(this, LongRunningService.class);
		startService(intent);


		try {
			mReader = Barcode2DWithSoft.getInstance();
		} catch (Exception ex) {

			Toast.makeText(MainActivity.this, ex.getMessage(),
					Toast.LENGTH_SHORT).show();

			return;
		}

		
	}

	@Override
	protected void initViewPageData() {
		lstFrg.add(new InputFragment());
		lstFrg.add(new OutputFragment());
		lstFrg.add(new ReviewFragment());

		lstTitles.add(getString(R.string.er_dsoft_tab_input));
		lstTitles.add(getString(R.string.er_dsoft_tab_output));
		lstTitles.add(getString(R.string.er_dsoft_tab_set));
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		if (mReader != null) {
			mReader.close();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		
		if (mReader != null) {
			new InitTask().execute();
		}
	}

	@Override
	protected void onDestroy() {

		
		super.onDestroy();
	}

	/**
	 * 设备上电异步类
	 * 
	 * @author liuruifeng
	 */
	public class InitTask extends AsyncTask<String, Integer, Boolean> {
		ProgressDialog mypDialog;

		@Override
		protected Boolean doInBackground(String... params) {
			
			
			boolean result = false;

            if (mReader != null) {
                result = mReader.open(MainActivity.this);

                if (result) {
                    mReader.setParameter(324, 1);
                    mReader.setParameter(300, 0); // Snapshot Aiming
                    mReader.setParameter(361, 0); // Image Capture Illumination
                }
            }
            return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			mypDialog.cancel();

			if (!result) {
				
				
				
				Toast.makeText(MainActivity.this, "init fail",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			mypDialog = new ProgressDialog(MainActivity.this);
			mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mypDialog.setMessage("init...");
			mypDialog.setCanceledOnTouchOutside(false);
			mypDialog.show();
		}

	}

	/**
	 * 验证十六进制输入是否正确
	 * 
	 * @param str
	 * @return
	 */
	public boolean vailHexInput(String str) {

		if (str == null || str.length() == 0) {
			return false;
		}

		// 长度必须是偶数
		if (str.length() % 2 == 0) {
			return StringUtility.isHexNumberRex(str);
		}

		return false;
	}
	/**
	 * 将ScrollView根据内部控件高度定位到底端
	 * 
	 * @param scroll
	 * @param inner
	 */
	public void scrollToBottom(final View scroll, final View inner) {

		Handler mHandler = new Handler();

		mHandler.post(new Runnable() {
			public void run() {
				if (scroll == null || inner == null) {
					return;
				}
				int offset = inner.getMeasuredHeight() - scroll.getHeight();
				if (offset < 0) {
					offset = 0;
				}

				scroll.scrollTo(0, offset);
			}
		});
	}
}
