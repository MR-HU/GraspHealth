package com.innouni.health.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseActivity;
import com.innouni.health.net.HttpPostRequest;
import com.innouni.health.util.Util;

/**
 * 找回密码页面
 * 
 * @author HuGuojun
 * @date 2013-12-24 下午5:22:21
 * @modify
 * @version 1.0.0
 */
public class GetPwdBackActivity extends BaseActivity implements OnClickListener {

	private ProgressDialog dialog;
	private EditText emailText, nickText;
	private String email, username;
	private GetPwdBackTask task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_getpwdback);
		application = MainApplication.getApplication();
		application.setActivity(this);
		initView();
	}

	private void initView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleLeftBtn.setOnClickListener(this);
		titleRightBtn.setBackgroundResource(R.drawable.titlebar_finish_selector);
		titleContentView.setText("找回密码申请");
		titleRightBtn.setOnClickListener(this);

		emailText = (EditText) findViewById(R.id.edit_email_getback);
		nickText = (EditText) findViewById(R.id.edit_nick_getback);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.btn_title_right:
			email = emailText.getText().toString();
			username = nickText.getText().toString();
			if (Util.isEmpty(email) || Util.isEmpty(username)) {
				showToast(R.string.load_input_uncomplete);
			} else {
				if (task != null) {
					task.cancel(true);
				}
				task = new GetPwdBackTask();
				task.execute();
			}
			break;
		}
	}

	private class GetPwdBackTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected void onPreExecute() {
			showDialog();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("email", email));
			pairs.add(new BasicNameValuePair("userName", username));
			String json = HttpPostRequest.getDataFromWebServer(
					GetPwdBackActivity.this, "getPassword", pairs);
			if (json == null || json.equals("net_err")) {
				return null;
			} else {
				try {
					JSONObject object = new JSONObject(json);
					return object.optInt("status");
				} catch (Exception e) {
					return null;
				}
			}
		}

		@Override
		protected void onPostExecute(Integer status) {
			task = null;
			dialog.dismiss();
			if (status == null) {
				showToast(R.string.net_error);
			} else if (status == 0) {
				showToast("48小时内给予处理，注意查收邮件");
				finish();
			} else if (status == 1) {
				showToast("昵称不存在");
			} else if (status == 2) {
				showToast("邮箱不存在");
			} else if (status == 3) {
				showToast("邮箱和昵称不统一");
			} else if (status == 4) {
				showToast("今日已经提交过申请了");
			}
		}

	}

	private void showDialog() {
		dialog = new ProgressDialog(GetPwdBackActivity.this);
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
