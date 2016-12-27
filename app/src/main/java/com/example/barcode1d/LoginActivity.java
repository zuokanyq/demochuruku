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

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends BaseActivity {
	
	private SharedPreferences pref;
	
	private SharedPreferences.Editor editor;

	private EditText accountEdit;

	private EditText passwordEdit;

	private Button login;
	
	private CheckBox rememberPass;

	public static final int LOGIN_SUECCESS = 1;
    public static final int LOGIN_FAULT = 0;
    public static final int LOGIN_ERROR = -1;

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
                    Intent intent = new Intent(LoginActivity.this, InputActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case LOGIN_ERROR:
                    Toast.makeText(LoginActivity.this,
                            "网络故障",
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(LoginActivity.this,
                            "用户名或密码不正确",
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
                String url = "http://192.168.168.115:8080/login.txt";
                String account = accountEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                FormBody.Builder formBuilder = new FormBody.Builder()
                        .add("account",account)
                        .add("password",password);
                RequestBody formBody = formBuilder.build();
                try {
                    doPostRequest(url,formBody);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                //     sendRequestWithHttpURLConnection();
			}
		});
	}

 //网络连接
    private void doPostRequest(String url,RequestBody formBody) throws IOException{
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(final Call call, IOException e) {
                        // Error
                        Message message = new Message();
                        message.what = LOGIN_ERROR;
                        handler.sendMessage(message);
                        /*runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // For the example, you can show an error dialog or a toast
                                // on the main UI thread
                            }
                        });*/
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        String res = response.body().string();
                        Message message = new Message();
                        if("success".equals(res)){
                            message.what = LOGIN_SUECCESS;
                        }else{
                            message.what = LOGIN_FAULT;
                        }
                        handler.sendMessage(message);
                    }
                });
    }


}
