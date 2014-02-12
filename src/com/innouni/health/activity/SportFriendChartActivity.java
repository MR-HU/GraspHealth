package com.innouni.health.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseActivity;
import com.innouni.health.util.Util;

/**
 * 运动伙伴柱状图
 * 
 * @author HuGuojun
 * @date 2014-1-17 下午1:33:20
 * @modify
 * @version 1.0.0
 */
public class SportFriendChartActivity extends BaseActivity implements
		OnClickListener {

	private TextView caloryView;
	private TextView threeView, fourView, fiveView;
	private ImageView threeIamge, fourImage, fiveImage;
	private int chartLayHeight;
	private boolean isMeasured = false;
	private double three, four, five;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sport_friend_chart);
		application = MainApplication.getApplication();
		application.setActivity(this);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			three = bundle.getDouble("workCal", 0);
			four = bundle.getDouble("normalCal", 0);
		} else {
			three = 0;
			four = 0;
		}
		five = three + four;
		initView();
	}

	private void initView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleLeftBtn.setOnClickListener(this);
		titleContentView.setText("今日消耗");
		titleRightBtn.setVisibility(View.GONE);

		findViewById(R.id.lay_chart).setOnClickListener(this);
		caloryView = (TextView) findViewById(R.id.txt_calory);
		threeView = (TextView) findViewById(R.id.txt_nutition_num_three);
		fourView = (TextView) findViewById(R.id.txt_nutition_num_four);
		fiveView = (TextView) findViewById(R.id.txt_nutition_num_five);
		threeIamge = (ImageView) findViewById(R.id.image_nutition_three);
		fourImage = (ImageView) findViewById(R.id.image_nutition_four);
		fiveImage = (ImageView) findViewById(R.id.image_nutition_five);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (!isMeasured) {
			LinearLayout chartLayout = (LinearLayout) findViewById(R.id.lay_chart);
			chartLayHeight = chartLayout.getHeight();
			isMeasured = true;
			caloryView.setText((long)five + "");
			threeView.setText((long)three + "卡");
			fourView.setText((long)four + "卡");
			fiveView.setText((long)five + "卡");
			drawChart(three, four, five);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.lay_chart:
			Intent intent = new Intent(this, NutitionChartActivity.class);
			startActivity(intent);
		}
	}

	public void drawChart(double three, double four, double five) {
		int standard = (int) getMax(getMax(three, four), five) + 10;
		chartLayHeight = chartLayHeight - Util.dip2px(this, 60);
		int chartThree = (int) (three * chartLayHeight / standard);
		int chartFour = (int) (four * chartLayHeight / standard);
		int chartFive = (int) (five * chartLayHeight / standard);
		LinearLayout.LayoutParams params3 = new LayoutParams(
				LayoutParams.WRAP_CONTENT, chartThree);
		LinearLayout.LayoutParams params4 = new LayoutParams(
				LayoutParams.WRAP_CONTENT, chartFour);
		LinearLayout.LayoutParams params5 = new LayoutParams(
				LayoutParams.WRAP_CONTENT, chartFive);
		threeIamge.setLayoutParams(params3);
		fourImage.setLayoutParams(params4);
		fiveImage.setLayoutParams(params5);
	}

	private double getMax(double param1, double param2) {
		if (param1 > param2) {
			return param1;
		} else {
			return param2;
		}
	}

}
