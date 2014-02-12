package com.innouni.health.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseActivity;

/**
 * 登录账号选择页面
 * 
 * @author HuGuojun
 * @date 2013-12-24 下午4:05:36
 * @modify
 * @version 1.0.0
 */
public class LoadActivity extends BaseActivity implements OnClickListener {

	private Button healthBtn, qqBtn, sinaBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load);
		application = MainApplication.getApplication();
		application.setActivity(this);
		initView();
	}

	private void initView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleContentView.setText("会员登录");
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleLeftBtn.setOnClickListener(this);
		titleRightBtn.setVisibility(View.GONE);

		healthBtn = (Button) findViewById(R.id.btn_health_account_load);
		qqBtn = (Button) findViewById(R.id.btn_qq_account_load);
		sinaBtn = (Button) findViewById(R.id.btn_sina_account_load);
		healthBtn.setOnClickListener(this);
		qqBtn.setOnClickListener(this);
		sinaBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.btn_health_account_load:
			Intent intent = new Intent(LoadActivity.this, LoginActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_qq_account_load:
			break;
		case R.id.btn_sina_account_load:
			break;
		}
	}
}
