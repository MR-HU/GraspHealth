package com.innouni.health.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseActivity;

/**
 * 饮食添加第一步<br>
 * 选择类型(早餐 午餐 晚餐 其他)
 * 
 * @author HuGuojun
 * @date 2014-1-14 上午10:25:10
 * @modify
 * @version 1.0.0
 */
public class AddDietStepOneActivity extends BaseActivity implements
		OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_diet_one);
		application = MainApplication.getApplication();
		application.setActivity(this);
		application.setDietActivity(this);
		initView();
	}

	private void initView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleContentView.setText("饮食类型选择");
		titleLeftBtn.setOnClickListener(this);
		titleRightBtn.setVisibility(View.GONE);

		findViewById(R.id.txt_breakfaset).setOnClickListener(this);
		findViewById(R.id.txt_lunch).setOnClickListener(this);
		findViewById(R.id.txt_dinner).setOnClickListener(this);
		findViewById(R.id.txt_other).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.txt_breakfaset:
			intent = new Intent(this, AddDietStepTwoActivity.class);
			intent.putExtra("mealsType", "1");
			startActivity(intent);
			break;
		case R.id.txt_lunch:
			intent = new Intent(this, AddDietStepTwoActivity.class);
			intent.putExtra("mealsType", "2");
			startActivity(intent);
			break;
		case R.id.txt_dinner:
			intent = new Intent(this, AddDietStepTwoActivity.class);
			intent.putExtra("mealsType", "3");
			startActivity(intent);
			break;
		case R.id.txt_other:
			intent = new Intent(this, AddDietStepTwoActivity.class);
			intent.putExtra("mealsType", "4");
			startActivity(intent);
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(application.getDietActivity().contains(this)){
			application.getDietActivity().remove(this);
		}
	}
}
