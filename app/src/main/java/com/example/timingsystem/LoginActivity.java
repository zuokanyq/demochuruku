package com.example.timingsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.timingsystem.services.CallWebService;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 系统登录demo
 * @author zuokanyq
 *
 */

public class LoginActivity extends BaseActivity {
	
	private SharedPreferences pref;
	
	private SharedPreferences.Editor editor;

	private EditText accountEdit;

	private EditText passwordEdit;

	private Button login;
	
	private CheckBox rememberPass;
    private ProgressBar pg;
    private String resTxt;
    private String userID;
    private String passWord;

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
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
        //Display progress bar until web service invocation completes
        pg = (ProgressBar) findViewById(R.id.progressBar1);
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
                String url = "http://192.168.168.196:7676/SRC/business/moreuserlogin.asmx?op=HasMobilePermission";
                userID = accountEdit.getText().toString();
                passWord = passwordEdit.getText().toString();
                if (userID.length() != 0 && userID.toString() != "") {
                    //Create instance for AsyncCallWS
                    AsyncCallWS task = new AsyncCallWS();
                    //Call execute
                    task.execute();
                    //If text control is empty
                } else {
                    accountEdit.setText("Please enter name");
                }

               /* FormBody.Builder formBuilder = new FormBody.Builder()
                        .add("userID",account)
                       .add("passWord",password);
                RequestBody formBody = formBuilder.build();
                try {
                    doPostRequest(url,formBody);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
			}
		});
	}

    private class AsyncCallWS extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Invoke webservice
            String url = "http://192.168.168.196:7676/SRC/business/moreuserlogin.asmx";
            String webMethName = "HasMobilePermission";
            Map<String, String> paramsmap=new HashMap<String, String>();
            paramsmap.put("userID",userID);
            paramsmap.put("passWord",passWord);
            resTxt = CallWebService.invokeLoginWS(url,webMethName,paramsmap);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            //Make ProgressBar invisible
            pg.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPreExecute() {
            //Make ProgressBar invisible
            pg.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

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

    /**
     * 调用WebService
     *
     * @return WebService的返回值
     *
     *//*
    public String CallWebService(String webServiceUrl, String MethodName, Map<String, String> Params) {
        // 1、指定webservice的命名空间和调用的方法名

        SoapObject request = new SoapObject("http://tempuri.org/", MethodName);
        // 2、设置调用方法的参数值，如果没有参数，可以省略，
        if (Params != null) {
            Iterator iter = Params.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                request.addProperty((String) entry.getKey(),
                        (String) entry.getValue());
            }
        }
        // 3、生成调用Webservice方法的SOAP请求信息。该信息由SoapSerializationEnvelope对象描述
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER12);
        envelope.bodyOut = request;
        // c#写的应用程序必须加上这句
        envelope.dotNet = true;
        HttpTransportSE ht = new HttpTransportSE(webServiceUrl);
        // 使用call方法调用WebService方法
        try {
            ht.call(null, envelope);
        } catch (HttpResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        try {
            final SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
            if (result != null) {
                Log.d("----收到的回复----", result.toString());
                return result.toString();
            }

        } catch (SoapFault e) {
            Log.e("----发生错误---", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
*/

}
