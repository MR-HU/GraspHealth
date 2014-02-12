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
import android.webkit.WebView;
import android.widget.TextView;

import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseActivity;
import com.innouni.health.entity.UserInfo;
import com.innouni.health.net.HttpPostRequest;
import com.innouni.health.util.Util;

/**
 * 运动建议详情页面
 * 
 * @author HuGuojun
 * @date 2014-1-18 下午3:42:59
 * @modify
 * @version 1.0.0
 */
public class SuggestDetailActivity extends BaseActivity implements
		OnClickListener {

	private ProgressDialog dialog;
	private TextView contentView;
	private WebView webView;
	private String id;
	private GetDetailTask task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_suggest_detail);
		application = MainApplication.getApplication();
		application.setActivity(this);
		id = getIntent().getStringExtra("id");
		initView();
		if (task != null) {
			task.cancel(true);
		}
		task = new GetDetailTask();
		task.execute();
	}

	private void initView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleLeftBtn.setOnClickListener(this);
		titleContentView.setText("运动建议");
		titleRightBtn.setVisibility(View.GONE);

		contentView = (TextView) findViewById(R.id.txt_content);
		webView = (WebView) findViewById(R.id.webview_content);
	}

	private class GetDetailTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			showDialog();
		}

		@Override
		protected String doInBackground(Void... params) {
			UserInfo user = application.getUserInfo();
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("token", user.getToken()));
			pairs.add(new BasicNameValuePair("mId", user.getId()));
			pairs.add(new BasicNameValuePair("id", id));
			return HttpPostRequest.getDataFromWebServer(
					SuggestDetailActivity.this, "getAdviseDetail", pairs);
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println("建议详情: " + result);
			dialog.dismiss();
			task = null;
			try {
				JSONObject object = new JSONObject(result);
				int status = object.optInt("status");
				if (status == 0) {
					String title = object.optString("title");
					String content = object.optString("context");
					String url = object.optString("url");
					titleContentView.setText(title);
					if (Util.isEmpty(url)) {
						contentView.setVisibility(View.VISIBLE);
						contentView.setText(content);
					} else {
						webView.setVisibility(View.VISIBLE);
						webView.loadUrl(url);
					}
				} else {
					showToast(R.string.net_error);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				showToast(R.string.net_error);
			}
		}
	}

	private void showDialog() {
		dialog = new ProgressDialog(this);
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
