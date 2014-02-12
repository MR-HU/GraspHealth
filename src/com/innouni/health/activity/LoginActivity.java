package com.innouni.health.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseActivity;
import com.innouni.health.entity.UserInfo;
import com.innouni.health.net.HttpPostRequest;
import com.innouni.health.util.ShareUtil;
import com.innouni.health.util.Util;

/**
 * 登录页面
 * 
 * @author HuGuojun
 * @date 2013-12-24 下午5:21:08
 * @modify
 * @version 1.0.0
 */
public class LoginActivity extends BaseActivity implements OnClickListener {

	private ProgressDialog dialog;
	private EditText nameText, pwdText;
	private Button loginButton;
	private TextView getPwdBackView;
	private String userName, password;
	private LoginTask task;
	private ShareUtil shareUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		application = MainApplication.getApplication();
		application.setActivity(this);
		shareUtil = ShareUtil.getInstance(this);
		iniView();
	}

	private void iniView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleLeftBtn.setOnClickListener(this);
		titleRightBtn.setVisibility(View.GONE);

		nameText = (EditText) findViewById(R.id.edit_username_login);
		pwdText = (EditText) findViewById(R.id.edit_pwd_login);
		userName = shareUtil.getStringValues("username");
		password = shareUtil.getStringValues("password");
		nameText.setText(userName);
		pwdText.setText(password);
		loginButton = (Button) findViewById(R.id.btn_login);
		getPwdBackView = (TextView) findViewById(R.id.txt_get_pwd);
		loginButton.setOnClickListener(this);
		getPwdBackView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.btn_login:
			userName = nameText.getText().toString();
			password = pwdText.getText().toString();
			if (Util.isEmpty(userName) || Util.isEmpty(password)) {
				showToast(R.string.load_input_uncomplete);
			} else {
				if (task != null) {
					task.cancel(true);
				}
				task = new LoginTask();
				task.execute();
			}
			break;
		case R.id.txt_get_pwd:
			Intent intent = new Intent(LoginActivity.this,
					GetPwdBackActivity.class);
			startActivity(intent);
			break;
		}
	}

	private class LoginTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			showDialog();
		}

		@Override
		protected String doInBackground(Void... params) {
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("userName", userName));
			pairs.add(new BasicNameValuePair("password", password));
			return HttpPostRequest.getDataFromWebServer(LoginActivity.this,
					"login", pairs);
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println("普通登录返回: " + result);
			task = null;
			dialog.dismiss();
			if (result == null || result.equals("net_err")) {
				showToast(R.string.net_error);
			} else {
				try {
					JSONObject object = new JSONObject(result);
					int status = object.optInt("status");
					if (status == 1) {
						showToast(R.string.load_fail);
					} else if (status == 0) {
						shareUtil.setStringValues("username", userName);
						shareUtil.setStringValues("password", password);
						showToast(R.string.load_success);
						UserInfo user = application.getUserInfo();
						user.setToken(object.optString("mToken")); // 用户令牌
						user.setId(object.optString("mId")); // 用户ID
						user.setType(object.optString("mType")); // 会员类型,普通会员(0)或单位会员(1)
						List<Activity> list = application.getActivity();
						for (Activity activity : list) {
							activity.finish();
						}
						Intent intent = new Intent(LoginActivity.this,
								MainActivity.class);
						startActivity(intent);
					}
				} catch (Exception e) {
					showToast(R.string.load_fail);
				}
			}
		}

	}

	private void showDialog() {
		dialog = new ProgressDialog(LoginActivity.this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setTitle(getResources().getString(R.string.load));
		dialog.setMessage(getResources().getString(R.string.loading));
		dialog.setIndeterminate(false);
		dialog.setCancelable(true);
		dialog.show();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (task != null) {
			task.cancel(true);
		}
	}
}
