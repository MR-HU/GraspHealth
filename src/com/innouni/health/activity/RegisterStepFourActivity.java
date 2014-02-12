package com.innouni.health.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseActivity;

/**
 * 注册第四步
 * 
 * @author HuGuojun
 * @date 2013-12-20 下午4:29:17
 * @modify
 * @version 1.0.0
 */
public class RegisterStepFourActivity extends BaseActivity implements
		OnClickListener {

	private Button nextButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_step4);
		application = MainApplication.getApplication();
		application.setActivity(this);
		initView();
	}

	private void initView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleContentView.setText("会员注册");
		titleLeftBtn.setOnClickListener(this);
		titleRightBtn.setVisibility(View.GONE);

		TextView naView = (TextView) findViewById(R.id.txt_step4);
		naView.setBackgroundResource(R.drawable.right_corner_selected_nav);
		naView.setTextColor(Color.WHITE);

		nextButton = (Button) findViewById(R.id.btn_next_step);
		nextButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.btn_next_step:
			List<Activity> list = application.getActivity();
			for (Activity activity : list) {
				activity.finish();
			}
			Intent intent = new Intent(RegisterStepFourActivity.this,
					MainActivity.class);
			startActivity(intent);
			break;
		}

	}
}
