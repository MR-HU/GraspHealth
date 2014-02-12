package com.innouni.health.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseActivity;

/**
 * 合作商户账号登陆页面
 * 
 * @author HuGuojun
 * @date 2014-1-2 下午12:16:26
 * @modify
 * @version 1.0.0
 */
public class PartnerActivity extends BaseActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_partner);
		application = MainApplication.getApplication();
		application.setActivity(this);
		initView();
	}

	private void initView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleLeftBtn.setOnClickListener(this);
		titleRightBtn.setVisibility(View.GONE);
		findViewById(R.id.btn_jawbone).setOnClickListener(this);
		findViewById(R.id.btn_nike).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.btn_jawbone:
			//http://eric-blue.com/projects/up-api
			break;
		case R.id.btn_nike:
			break;
		}
	}

}
