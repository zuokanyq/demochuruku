package com.example.barcode1d;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.rscja.deviceapi.Barcode1D;
import com.rscja.deviceapi.exception.ConfigurationException;
import com.rscja.utility.StringUtility;

import android.support.v4.app.Fragment;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

/**
 * 一维条码使用demo
 * 
 * 1、使用前请确认您的机器已安装此模块。 
 * 2、要正常使用模块需要在\libs\armeabi\目录放置libDeviceAPI.so文件，同时在\libs\目录下放置DeviceAPIver20160627.jar文件。
 * 3、在操作设备前需要调用 open() 打开设备，使用完后调用 close() 关闭设备
 * 4、调用scan()进行条码扫描
 * 
 * 更多函数的使用方法请查看API说明文档
 * 
 * @author wushengjun
 * 更新于2016月8月8日
 */
public class MainActivity extends Activity {

	private final static String TAG = "MainActivity";

	private Button btn_Start;
	private Button btn_Clear;
	private TextView tv_Result;
	private TextView tv_scan_count;
	private TextView tv_succ_count;
	private TextView tv_fail_count;
	private TextView tv_error_count;
	private TextView tv_succ_rate;
	private TextView tv_fail_rate;
	private TextView tv_error_rate;
	private CheckBox cbContinuous;
	private CheckBox cbCompare;
	private EditText et_between;

	private Handler handler;
	private Thread thread;
	private ScrollView svResult;
	private EditText et_init_barcode;
	private String init_barcode;
	int sussCount = 0;
	int failCount = 0;
	int errorCount = 0;

	int readerStatus = 0;

	private boolean threadStop = true;

	private boolean isBarcodeOpened = false;

	private Barcode1D mInstance;
	
	private ExecutorService executor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
	}

	private void init() {
		btn_Start = (Button) findViewById(R.id.btn_Start);
		btn_Clear = (Button) findViewById(R.id.btn_Clear);
		tv_Result = (TextView) findViewById(R.id.tv_result);
		cbContinuous = (CheckBox) findViewById(R.id.cbContinuous);
		cbCompare = (CheckBox) findViewById(R.id.cbCompare);
		et_between = (EditText) findViewById(R.id.et_between);
		svResult = (ScrollView) findViewById(R.id.svResult);
		et_init_barcode = (EditText) findViewById(R.id.et_init_barcode);

		tv_scan_count = (TextView) findViewById(R.id.tv_scan_count);
		tv_succ_count = (TextView) findViewById(R.id.tv_succ_count);
		tv_fail_count = (TextView) findViewById(R.id.tv_fail_count);
		tv_error_count = (TextView) findViewById(R.id.tv_error_count);
		tv_succ_rate = (TextView) findViewById(R.id.tv_succ_rate);
		tv_fail_rate = (TextView) findViewById(R.id.tv_fail_rate);
		tv_error_rate = (TextView) findViewById(R.id.tv_error_rate);

	
		btn_Clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				clear();

			}
		});

		try {
			mInstance = Barcode1D.getInstance();

		} catch (ConfigurationException e) {

			Toast.makeText(MainActivity.this, R.string.rfid_mgs_error_config,
					Toast.LENGTH_SHORT).show();
			
			return;
		}

		btn_Start.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				scan();

			}
		});

		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				if (msg != null) {

					String strData = "";

					if ((sussCount + errorCount + failCount) % 1000 == 0) {
						tv_Result.setText("");
					}

					switch (msg.arg1) {
					case 0:
						failCount += 1;
						strData = getString(R.string.yid_msg_scan_fail) + "\n";
						

						break;
					case 1:

						if (cbCompare.isChecked()) {

							init_barcode = et_init_barcode.getText().toString();

							if (StringUtility.isEmpty(init_barcode))// 设置初始条码
							{
								et_init_barcode.setText(msg.obj.toString());

								init_barcode = et_init_barcode.getText()
										.toString();
							}

							if (init_barcode.equals(msg.obj)) {
								sussCount += 1;
							} else {
								errorCount += 1;

								strData = getString(R.string.yid_msg_scan_error)
										+ "：";
							}
						} else {
							sussCount += 1;
							init_barcode = "";
							et_init_barcode.setText("");
						}

						strData += msg.obj.toString() + "\n";

						break;

					default:
						failCount += 1;
						
						break;
					}

					tv_Result.append(strData);
					
					scrollToBottom(svResult, tv_Result); //滑动到底部
					stat();
				}
			}

		};

	}

	private void scan() {
		if (threadStop) {

			Log.i("MY", "readerStatus " + readerStatus);

			boolean bContinuous = false;

			int iBetween = 0;

			bContinuous = cbContinuous.isChecked();
			if (bContinuous) {
				btn_Start.setText(getString(R.string.title_stop));
				threadStop = false;

				String strBetween = et_between.getText().toString();
				if (StringUtility.isEmpty(strBetween)) {

				} else {
					iBetween = StringUtility.string2Int(strBetween,0);// 毫秒
				}

				btn_Clear.setEnabled(false);
				cbContinuous.setEnabled(false);
			}
			init_barcode = et_init_barcode.getText().toString();

//			thread = new Thread(new GetBarcode(bContinuous, iBetween));
//			thread.start();
			
			executor.execute(new GetBarcode(bContinuous, iBetween));

		} else {
			btn_Start.setText(getString(R.string.title_scan));
			threadStop = true;
			cbContinuous.setEnabled(true);
			btn_Clear.setEnabled(true);
		}
	}

	private void stat() {
		int total = sussCount + failCount + errorCount;

		if (total > 0) {
			tv_scan_count.setText(String.valueOf(total));
			tv_succ_count.setText(String.valueOf(sussCount));
			tv_fail_count.setText(String.valueOf(failCount));
			tv_error_count.setText(String.valueOf(errorCount));

			tv_error_rate.setText(String.valueOf(errorCount * 1000 / total)
					+ "‰");
			tv_succ_rate
					.setText(String.valueOf(sussCount * 1000 / total) + "‰");
			tv_fail_rate
					.setText(String.valueOf(failCount * 1000 / total) + "‰");
		}
	}

	private void clear() {
		tv_Result.setText("");

		int total = 0;
		sussCount = 0;
		failCount = 0;
		errorCount = 0;

		et_init_barcode.setText("");
		tv_scan_count.setText(String.valueOf(total));
		tv_succ_count.setText(String.valueOf(sussCount));
		tv_fail_count.setText(String.valueOf(failCount));
		tv_error_count.setText(String.valueOf(errorCount));

		tv_error_rate.setText(String.valueOf(0));
		tv_succ_rate.setText(String.valueOf(0));
		tv_fail_rate.setText(String.valueOf(0));

		btn_Start.setText(getString(R.string.title_scan));
		threadStop = true;

	}

	class GetBarcode implements Runnable {

		private boolean isContinuous = false;
		String barCode = "";
		private long sleepTime = 1000;
		Message msg = null;

		public GetBarcode(boolean isContinuous) {
			this.isContinuous = isContinuous;
		}

		public GetBarcode(boolean isContinuous, int sleep) {
			this.isContinuous = isContinuous;
			this.sleepTime = sleep;
		}

		@Override
		public void run() {

			do {
				barCode = mInstance.scan();

				Log.i("MY", "barCode " + barCode.trim());

				msg = new Message();

				if (StringUtility.isEmpty(barCode)) {
					msg.arg1 = 0;
					msg.obj = "";

				} else {

					msg.arg1 = 1;

					msg.obj = barCode;
				}

				handler.sendMessage(msg);

				if (isContinuous) {
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			} while (isContinuous && !threadStop);

		}

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
			// TODO Auto-generated method stub
			return mInstance.open();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			isBarcodeOpened = result;
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
	@Override
	protected void onResume() {
		super.onResume();

		executor = Executors.newFixedThreadPool(6);
		
		new InitTask().execute();

	}

	@Override
	protected void onPause() {
		super.onPause();
		
		threadStop = true;
		executor.shutdownNow();
		
		if (isBarcodeOpened) {
			mInstance.close();
		}
		btn_Start.setText(getString(R.string.title_scan));
		
		cbContinuous.setEnabled(true);
		btn_Clear.setEnabled(true);
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == 139) {
			if (event.getRepeatCount() == 0) {

				scan();
				Log.i("MY", "keyCode " + keyCode);
				return true;
			}
		}

		return super.onKeyDown(keyCode, event);

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
