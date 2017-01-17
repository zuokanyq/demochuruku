package com.example.timingsystem;

import android.app.ProgressDialog;
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

import com.example.timingsystem.model.MdSHMobileUserInfo;
import com.example.timingsystem.services.CallWebService;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;*/

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
    private ProgressDialog dialog = null;

    private CheckBox rememberPass;
    private String userID;
    private String passWord;

	public static final String LOGIN_SUECCESS = "success";
    public static final String LOGIN_ERROR = "error";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

        MdSHMobileUserInfo.initMapping();

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
                userID = accountEdit.getText().toString();
                passWord = passwordEdit.getText().toString();
                if (userID.length() != 0 && userID.toString() != "") {
                    AsyncCallWS task = new AsyncCallWS();
                    task.execute();
                } else {
                    accountEdit.setText("Please enter name");
                }

			}
		});
	}

    private class AsyncCallWS extends AsyncTask<String, Void, Void> {


        @Override
        protected Void doInBackground(String... params) {
            //Invoke webservice
            String url = "http://192.168.168.196:7676/SRC/business/mobilemanagement.asmx";
            String webMethName = "HasMobilePermission";
            Map<String, String> paramsmap=new HashMap<String, String>();
            paramsmap.put("LoginSend",generateLoginXml());
            CallWebService.invokeLoginWS(url,webMethName,paramsmap);

            return null;
        }

        private String generateLoginXml() {
            String xml="<UserID>"+userID+"</UserID>" +
                    "<PassWord>"+passWord+"</PassWord>";
            return xml;
        }


        @Override
        protected void onPostExecute(Void result) {

            //Make ProgressBar invisible
            String res=result.toString();
            dialog.dismiss();//关闭ProgressDialog
            String resultstr=result.toString();
            if(resultstr.equals(LOGIN_SUECCESS)) {

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
            }else if (resultstr.equals(LOGIN_ERROR)){

                    Toast.makeText(LoginActivity.this,
                            "网络故障",
                            Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(LoginActivity.this,
                        resultstr,
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(LoginActivity.this, "登录提示", "正在登录，请稍等...", false);//创建ProgressDialog
        }

    }

}
