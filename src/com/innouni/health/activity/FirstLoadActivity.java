package com.innouni.health.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseActivity;

/**
 * 注册登录选择页
 * 
 * @author HuGuojun
 * @date 2014-1-2 上午11:56:24
 * @modify
 * @version 1.0.0
 */
public class FirstLoadActivity extends BaseActivity implements OnClickListener {

	private Button cooperateBtn, registerBtn, loadBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_first_load);
		application = MainApplication.getApplication();
		application.setActivity(this);
		initView();
	}

	private void initView() {
		cooperateBtn = (Button) findViewById(R.id.btn_cooperate);
		registerBtn = (Button) findViewById(R.id.btn_register);
		loadBtn = (Button) findViewById(R.id.btn_load);
		cooperateBtn.setOnClickListener(this);
		registerBtn.setOnClickListener(this);
		loadBtn.setOnClickListener(this);
		findViewById(R.id.btn_load_partener1).setOnClickListener(this);
		findViewById(R.id.btn_load_partener2).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.btn_cooperate:
			intent.setClass(FirstLoadActivity.this, PartnerActivity.class);
			break;
		case R.id.btn_register:
			intent.setClass(FirstLoadActivity.this,
					RegisterStepOneActivity.class);
			break;
		case R.id.btn_load:
			intent.setClass(FirstLoadActivity.this, LoadActivity.class);
			break;
		case R.id.btn_load_partener1:
			break;
		case R.id.btn_load_partener2:
			break;
		}
		startActivity(intent);
	}
}
