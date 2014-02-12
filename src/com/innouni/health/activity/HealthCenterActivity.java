package com.innouni.health.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseActivity;

/**
 * 健康中心页面
 * 
 * @author HuGuojun
 * @date 2014-2-11 下午2:01:04
 * @modify
 * @version 1.0.0
 */
public class HealthCenterActivity extends BaseActivity implements
		OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_health_center);
		application = MainApplication.getApplication();
		application.setActivity(this);
		initView();
	}

	private void initView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleContentView.setText("健康中心");
		titleLeftBtn.setOnClickListener(this);
		titleRightBtn.setVisibility(View.GONE);

		findViewById(R.id.btn_health_news).setOnClickListener(this);
		findViewById(R.id.btn_environment).setOnClickListener(this);
		findViewById(R.id.btn_expert_list).setOnClickListener(this);
		findViewById(R.id.btn_opinion).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.btn_health_news:
			intent = new Intent(HealthCenterActivity.this,
					HealthNewsActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_environment:
			intent = new Intent(HealthCenterActivity.this,
					EnvironmentActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_expert_list:
			intent = new Intent(HealthCenterActivity.this,
					ExpertsActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_opinion:
			intent = new Intent(HealthCenterActivity.this,
					OpinionActivity.class);
			startActivity(intent);
			break;
		}
	}
}
