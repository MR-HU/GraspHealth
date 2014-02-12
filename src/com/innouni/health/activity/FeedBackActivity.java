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

public class FeedBackActivity extends BaseActivity implements OnClickListener {

	private EditText suggestText;
	private String content;
	private SubmitTask task;
	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		application = MainApplication.getApplication();
		application.setActivity(this);
		initView();
	}

	private void initView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleContentView.setText("意见反馈");
		titleLeftBtn.setOnClickListener(this);
		titleRightBtn.setOnClickListener(this);
		titleRightBtn
				.setBackgroundResource(R.drawable.titlebar_finish_selector);

		suggestText = (EditText) findViewById(R.id.edit_suggestion);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.btn_title_right:
			content = suggestText.getText().toString();
			if (!Util.isEmpty(content)) {
				if (task != null) {
					task.cancel(true);
				}
				task = new SubmitTask();
				task.execute();
			}
			break;
		}
	}

	private class SubmitTask extends AsyncTask<Void, Void, String> {
		@Override
		protected void onPreExecute() {
			showDialog();
		}

		@Override
		protected String doInBackground(Void... params) {
			UserInfo user = application.getUserInfo();
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("mId", user.getId()));
			pairs.add(new BasicNameValuePair("token", user.getToken()));
			pairs.add(new BasicNameValuePair("content", content));
			return HttpPostRequest.getDataFromWebServer(FeedBackActivity.this,
					"sendFeedBack", pairs);
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println("意见反馈: " + result);
			task = null;
			dialog.dismiss();
			try {
				JSONObject object = new JSONObject(result);
				if (object.optInt("status") == 0) {
					showToast("信息提交成功");
					suggestText.setText(null);
				} else {
					showToast("信息提交失败");
				}
			} catch (JSONException e) {
				e.printStackTrace();
				showToast("信息提交失败");
			}
		}
	}

	private void showDialog() {
		dialog = new ProgressDialog(FeedBackActivity.this);
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
