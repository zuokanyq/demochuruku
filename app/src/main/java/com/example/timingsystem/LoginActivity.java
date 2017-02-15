package com.example.timingsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.timingsystem.model.MdSHMobileUserInfo;
import com.example.timingsystem.services.CallWebService;

import org.ksoap2.serialization.SoapObject;
import java.util.HashMap;
import java.util.Map;


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
    private EditText urlEdit;

	private Button login;
    private ProgressDialog dialog = null;

    private CheckBox rememberPass;
    private String userID;
    private String passWord;
    private String serverUrl;
    private MyApplication app;

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
		urlEdit = (EditText) findViewById(R.id.url);
		rememberPass = (CheckBox) findViewById(R.id.remember_pass);
		login = (Button) findViewById(R.id.login);
		boolean isRemember = pref.getBoolean("remember_password", false);
        String url= pref.getString("url","192.168.168.196:7676");
        urlEdit.setText(url);
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
                serverUrl= urlEdit.getText().toString();
                editor = pref.edit();
                editor.putString("url", serverUrl);
                editor.commit();
                if (userID.length() != 0 && userID.toString() != "") {
                    AsyncCallWS task = new AsyncCallWS();
                    task.execute(serverUrl);
                } else {
                    accountEdit.setText("Please enter name");
                }

			}
		});
	}

    private class AsyncCallWS extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {
            //Invoke webservice
           // String url = "http://192.168.168.196:7676/SRC/business/mobilemanagement.asmx";
            String webMethName = "HasMobilePermission";
            Map<String, String> paramsmap=new HashMap<String, String>();
            paramsmap.put("LoginSend",generateLoginXml());
            SoapObject obj = CallWebService.invokeInputWS(params[0],webMethName,paramsmap);
            String result="";
            if("Success".equals(obj.getProperty("resMsg"))){
                result= parseSoapObjectToString(obj);

            }else if ("Error".equals((obj.getProperty("resMsg")))){
                result=LOGIN_ERROR;
                //Webservice 调用过程中出错
            }
            return result;
        }

        private String generateLoginXml() {

            String xml="<LoginSend><UserID>"+userID+"</UserID>" +
                    "<PassWord>"+passWord+"</PassWord></LoginSend>";
            return xml;
        }


        @Override
        protected void onPostExecute(String result) {

            //Make ProgressBar invisible

            dialog.dismiss();//关闭ProgressDialog

            if(result.equals(LOGIN_SUECCESS)) {

                String account = accountEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                editor = pref.edit();
                if (rememberPass.isChecked()) {
                    editor.putBoolean("remember_password", true);
                    editor.putString("account", account);
                    editor.putString("password", password);
                } else {
                    editor.remove("remember_password");
                    editor.remove("account");
                    editor.remove("password");
                }
                editor.commit();
                app = (MyApplication) getApplication();
                app.setUserID(account);
                app.setServerurl(serverUrl);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }else if (result.equals(LOGIN_ERROR)){

                    Toast.makeText(LoginActivity.this,
                            "网络故障",
                            Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(LoginActivity.this,
                        result,
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(LoginActivity.this, "登录提示", "正在登录，请稍等...", false);//创建ProgressDialog
        }

    }

    /**
     * 将ksoap2框架调用webservice传回的SoapObject转换成Batch对象
     * @param obj
     * @return
     */
    private String parseSoapObjectToString(SoapObject obj){
        String res="";
        SoapObject returnobj = (SoapObject)obj.getProperty("LoginReturn");
        res=returnobj.getProperty("Message").toString();
        return res;
    }

}
