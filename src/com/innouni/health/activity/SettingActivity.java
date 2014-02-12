package com.innouni.health.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseActivity;
import com.innouni.health.util.NetUtil;
import com.innouni.health.util.UpdateVersionUtil;

/**
 * 设置页面
 * 
 * @author HuGuojun
 * @date 2014-2-10 上午11:49:31
 * @modify
 * @version 1.0.0
 */
public class SettingActivity extends BaseActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		application = MainApplication.getApplication();
		application.setActivity(this);
		initView();
	}

	private void initView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleContentView.setText("设置");
		titleLeftBtn.setOnClickListener(this);
		titleRightBtn.setVisibility(View.GONE);

		findViewById(R.id.btn_about).setOnClickListener(this);
		findViewById(R.id.btn_update).setOnClickListener(this);
		findViewById(R.id.btn_feed_back).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.btn_about:
			Intent intent = new Intent(SettingActivity.this,
					AboutActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_update:
			if (NetUtil.isNetworkAvailable(this)) {
				UpdateVersionUtil versionUtil = UpdateVersionUtil
						.getInstance(SettingActivity.this);
				versionUtil.setShowLoading(true);
				versionUtil.startCheckVersion();
			}
			break;
		case R.id.btn_feed_back:
			Intent intent2 = new Intent(SettingActivity.this,
					FeedBackActivity.class);
			startActivity(intent2);
			break;
		}
	}
}
