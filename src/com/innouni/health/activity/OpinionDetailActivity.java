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
import android.widget.TextView;

import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseActivity;
import com.innouni.health.entity.UserInfo;
import com.innouni.health.net.HttpPostRequest;

/**
 * 指导信息详情页
 * 
 * @author HuGuojun
 * @date 2014-2-12 上午10:11:58
 * @modify
 * @version 1.0.0
 */
public class OpinionDetailActivity extends BaseActivity implements
		OnClickListener {

	private TextView contentView;
	private String title = "";
	private String id = "";
	private String content = "";
	private ProgressDialog dialog;
	private GetContentTask task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_health_news_detail);
		application = MainApplication.getApplication();
		application.setActivity(this);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			title = bundle.getString("title");
			id = bundle.getString("id");
		}
		initView();
	}

	private void initView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleContentView.setText(title);
		titleLeftBtn.setOnClickListener(this);
		titleRightBtn.setVisibility(View.GONE);
		contentView = (TextView) findViewById(R.id.txt_content);

		if (task != null) {
			task.cancel(true);
		}
		task = new GetContentTask();
		task.execute();
	}

	private class GetContentTask extends AsyncTask<Void, Void, String> {

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
			pairs.add(new BasicNameValuePair("guidanceId", id));
			return HttpPostRequest.getDataFromWebServer(
					OpinionDetailActivity.this, "getGuidanceDes", pairs);
		}

		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			task = null;
			System.out.println("指导信息详情返回: " + result);
			try {
				JSONObject object = new JSONObject(result);
				if (object.optInt("status") == 0) {
					content = object.optString("guidanceContent");
					contentView.setText(content);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void showDialog() {
		dialog = new ProgressDialog(OpinionDetailActivity.this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setMessage(getResources().getString(R.string.load_loading_data));
		dialog.setIndeterminate(false);
		dialog.setCancelable(true);
		dialog.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (task != null) {
			task.cancel(true);
		}
	}

}
