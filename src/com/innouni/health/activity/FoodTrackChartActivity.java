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
 * 食物追踪柱状图
 * 
 * @author HuGuojun
 * @date 2014-1-14 上午9:21:36
 * @modify
 * @version 1.0.0
 */
public class FoodTrackChartActivity extends BaseActivity implements
		OnClickListener {

	private TextView caloryView;
	private TextView oneView, twoView, threeView, fourView, fiveView;
	private ImageView oneImage, twoImage, threeIamge, fourImage, fiveImage;
	private int chartLayHeight;
	private boolean isMeasured = false;
	private double one, two, three, four, five;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_food_chart);
		application = MainApplication.getApplication();
		application.setActivity(this);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			one = bundle.getDouble("break", 0);
			two = bundle.getDouble("lunch", 0);
			three = bundle.getDouble("dinner", 0);
			four = bundle.getDouble("other", 0);
		} else {
			one = 0;
			two = 0;
			three = 0;
			four = 0;
		}
		five = one + two + three + four;
		initView();
	}

	private void initView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleLeftBtn.setOnClickListener(this);
		titleContentView.setText("食物追踪-今日摄入");
		titleRightBtn.setVisibility(View.GONE);

		caloryView = (TextView) findViewById(R.id.txt_calory);
		oneView = (TextView) findViewById(R.id.txt_nutition_num_one);
		twoView = (TextView) findViewById(R.id.txt_nutition_num_two);
		threeView = (TextView) findViewById(R.id.txt_nutition_num_three);
		fourView = (TextView) findViewById(R.id.txt_nutition_num_four);
		fiveView = (TextView) findViewById(R.id.txt_nutition_num_five);

		oneImage = (ImageView) findViewById(R.id.image_nutition_one);
		twoImage = (ImageView) findViewById(R.id.image_nutition_two);
		threeIamge = (ImageView) findViewById(R.id.image_nutition_three);
		fourImage = (ImageView) findViewById(R.id.image_nutition_four);
		fiveImage = (ImageView) findViewById(R.id.image_nutition_five);

		findViewById(R.id.lay_chart).setOnClickListener(this);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (!isMeasured) {
			LinearLayout chartLayout = (LinearLayout) findViewById(R.id.lay_chart);
			chartLayHeight = chartLayout.getHeight();
			isMeasured = true;
			initChart();
		}
	}

	public void initChart() {
		caloryView.setText((long)five + "");
		oneView.setText(one + "卡");
		twoView.setText(two + "卡");
		threeView.setText(three + "卡");
		fourView.setText(four + "卡");
		fiveView.setText((long)five + "卡");
		drawChart(one / 1000, two / 1000, three / 1000, four / 1000,
				five / 1000);
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
			break;
		}
	}

	public void drawChart(double one, double two, double three, double four,
			double five) {
		int standard = (int) getMax(
				getMax(getMax(one, two), getMax(three, four)), five);
		int layHeight = chartLayHeight - Util.dip2px(this, 60);
		int chartOne = (int) (one * layHeight / standard);
		int chartTwo = (int) (two * layHeight / standard);
		int chartThree = (int) (three * layHeight / standard);
		int chartFour = (int) (four * layHeight / standard);
		int chartFive = (int) (five * layHeight / standard);
		LinearLayout.LayoutParams params1 = new LayoutParams(
				LayoutParams.WRAP_CONTENT, chartOne);
		LinearLayout.LayoutParams params2 = new LayoutParams(
				LayoutParams.WRAP_CONTENT, chartTwo);
		LinearLayout.LayoutParams params3 = new LayoutParams(
				LayoutParams.WRAP_CONTENT, chartThree);
		LinearLayout.LayoutParams params4 = new LayoutParams(
				LayoutParams.WRAP_CONTENT, chartFour);
		LinearLayout.LayoutParams params5 = new LayoutParams(
				LayoutParams.WRAP_CONTENT, chartFive);
		oneImage.setLayoutParams(params1);
		twoImage.setLayoutParams(params2);
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
