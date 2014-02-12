package com.innouni.health.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
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
import com.innouni.health.entity.UserInfo;
import com.innouni.health.net.HttpPostRequest;
import com.innouni.health.util.Util;

/**
 * 单位会员激活码页面
 * 
 * @author HuGuojun
 * @date 2013-12-30 上午11:50:11
 * @modify
 * @version 1.0.0
 */
public class CodeActiveActivity extends BaseActivity implements OnClickListener {

	private ProgressDialog dialog;
	private EditText codeText;
	private String code;
	private ActiveTask task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_code_activite);
		application = MainApplication.getApplication();
		application.setActivity(this);
		initView();
	}

	private void initView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleLeftBtn.setOnClickListener(this);
		titleContentView.setText("单位会员激活码");
		titleRightBtn.setOnClickListener(this);
		titleRightBtn.setBackgroundResource(R.drawable.titlebar_finish_selector);

		codeText = (EditText) findViewById(R.id.edit_code_active);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.btn_title_right:
			code = codeText.getText().toString().trim();
			if (Util.isEmpty(code)) {
				showToast(R.string.register_info_uncomplete);
			} else {
				if (task != null) {
					task.cancel(true);
				}
				task = new ActiveTask();
				task.execute();
			}
			break;
		}
	}

	private class ActiveTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected void onPreExecute() {
			showDialog();
		}
		
		@Override
		protected Integer doInBackground(Void... params) {
			UserInfo user = application.getUserInfo();
 			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
 			pairs.add(new BasicNameValuePair("token", user.getToken()));
 			pairs.add(new BasicNameValuePair("mId", user.getId()));
 			pairs.add(new BasicNameValuePair("unitCode", code));
 			String json = HttpPostRequest.getDataFromWebServer(CodeActiveActivity.this, "activatingMember", pairs);
			try {
				int status = new JSONObject(json).optInt("status");
				return status;
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(Integer status) {
			dialog.dismiss();
			task = null;
			if (status == null) {
				showToast(R.string.net_error);
			} else if (status == 0) {
				showToast(R.string.activie_success);
				finish();
			} else {
				showToast(R.string.activie_fail);
			}
		}

	}
	
	private void showDialog() {
		dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setTitle(getResources().getString(R.string.net_submit));
		dialog.setMessage(getResources().getString(R.string.net_submiting));
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
