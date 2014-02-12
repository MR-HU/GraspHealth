package com.innouni.health.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.TextView;

import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseActivity;

/**
 * 注册协议页面
 * 
 * @author HuGuojun
 * @date 2013-12-20 下午4:29:28
 * @modify
 * @version 1.0.0
 */
public class ProtocalActivity extends BaseActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_protocal);
		application = MainApplication.getApplication();
		application.setActivity(this);
		initView();
	}

	private void initView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleContentView.setText("会员注册协议");
		titleLeftBtn.setOnClickListener(this);
		titleRightBtn.setVisibility(View.GONE);

		WebView webView = (WebView) findViewById(R.id.webview_protocal);
		webView.setBackgroundColor(getResources().getColor(R.color.bg_color));
		WebSettings settings = webView.getSettings();
		settings.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
		webView.loadDataWithBaseURL(
				"",
				"阿迪快减肥你的阿杜阿杜阿杜阿杜阿杜跟阿杜阿杜啊萨丹哈把三方矮人国巴萨丢风撒脚阿迪风景协议",
				"text/html", "UTF-8", "");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		}
	}
}
