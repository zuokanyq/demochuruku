package com.example.barcode1d;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends BaseActivity {
	
	private SharedPreferences pref;
	
	private SharedPreferences.Editor editor;

	private EditText accountEdit;

	private EditText passwordEdit;

	private Button login;
	
	private CheckBox rememberPass;

	public static final int LOGIN_SUECCESS = 1;
    public static final int LOGIN_FAULT = 0;

	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
				case LOGIN_SUECCESS:
					String response = (String) msg.obj;
                    String account = accountEdit.getText().toString();
                    String password = passwordEdit.getText().toString();
                    editor = pref.edit();
                    if (rememberPass.isChecked()) {
                        editor.putBoolean("remember_password", true);
                        editor.putString("account", account);
                        editor.putString("password", password);
                    } else {
                        editor.clear();
                    }
                    editor.commit();
                    Intent intent = new Intent(LoginActivity.this, FirstActivity.class);
                    startActivity(intent);
                    finish();
                    break;
				default:
                    Toast.makeText(LoginActivity.this,
                            "account or password is invalid",
                            Toast.LENGTH_SHORT).show();

			}
		}

	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		accountEdit = (EditText) findViewById(R.id.account);
		passwordEdit = (EditText) findViewById(R.id.password);
		rememberPass = (CheckBox) findViewById(R.id.remember_pass);
		login = (Button) findViewById(R.id.login);
		boolean isRemember = pref.getBoolean("remember_password", false);
		if (isRemember) {
			String account = pref.getString("account", "");
			String password = pref.getString("password", "");
			accountEdit.setText(account);
			passwordEdit.setText(password);
			rememberPass.setChecked(true);
		}
		login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                sendRequestWithHttpURLConnection();
			}
		});
	}

	private boolean login(){
		return true;
	}

	private void sendRequestWithHttpURLConnection() {
		// 开启线程来发起网络请求
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					URL url = new URL("http://192.168.168.115:8080/login.txt");
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					connection.setDoInput(true);
					connection.setDoOutput(true);
					InputStream in = connection.getInputStream();
					// 下面对获取到的输入流进行读取
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
					Message message = new Message();
                    if("success".equals(response.toString())){
                        message.what = LOGIN_SUECCESS;
					    // 将服务器返回的结果存放到Message中
                    }else{
                        message.what = LOGIN_FAULT;
                    }

					handler.sendMessage(message);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
		}).start();
	}

}
