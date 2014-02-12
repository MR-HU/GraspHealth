package com.innouni.health.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseActivity;
import com.innouni.health.entity.UserInfo;
import com.innouni.health.net.HttpPostRequest;
import com.innouni.health.util.Util;

/**
 * 我的推荐详情页面
 * 
 * @author HuGuojun
 * @date 2013-12-30 上午10:26:04
 * @modify
 * @version 1.0.0
 */
public class RecommendDetailActivity extends BaseActivity implements
		OnClickListener {

	private String recommendId;
	private WebView webView;
	private GetDetailTask task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recommend_detail);
		application = MainApplication.getApplication();
		application.setActivity(this);
		recommendId = getIntent().getStringExtra("recommendId");
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
		titleContentView.setText("");
		titleLeftBtn.setOnClickListener(this);
		titleRightBtn.setVisibility(View.GONE);

		webView = (WebView) findViewById(R.id.webview_recommend);
		WebSettings set = webView.getSettings();
		set.setBuiltInZoomControls(true);
		set.setJavaScriptEnabled(false);
	}

	private class GetDetailTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			UserInfo user = application.getUserInfo();
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("token", user.getToken()));
			pairs.add(new BasicNameValuePair("mId", user.getId()));
			pairs.add(new BasicNameValuePair("id", recommendId));
			return HttpPostRequest.getDataFromWebServer(
					RecommendDetailActivity.this, "getMyPushDetail", pairs);
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println("我的推荐详情: " + result);
			task = null;
			try {
				JSONObject object = new JSONObject(result);
				int status = object.optInt("status");
				if (status == 0) {
					String title = object.optString("title");
					titleContentView.setText(title);
					String content = object.optString("context");
					String url = object.optString("url");
					if (Util.isEmpty(url)) {
						webView.loadDataWithBaseURL("", content, "text/html",
								"UTF-8", null);
					} else {
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
