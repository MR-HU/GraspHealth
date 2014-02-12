package com.innouni.health.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseActivity;
import com.innouni.health.entity.UserInfo;
import com.innouni.health.net.HttpPostRequest;
import com.innouni.health.util.Util;

/**
 * 营养成分摄入情况柱状图
 * 
 * @author HuGuojun
 * @date 2014-1-16 下午1:32:19
 * @modify
 * @version 1.0.0
 */
public class NutitionChartActivity extends BaseActivity implements
		OnClickListener {

	private TextView oneView, twoView, threeView, fourView, fiveView;
	private ImageView oneImage, twoImage, threeImage, fourImage, fiveImage;
	private int chartLayHeight;
	private boolean isMeasured = false;
	private GetNutitionTask task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nutition_chart);
		application = MainApplication.getApplication();
		application.setActivity(this);
		initView();
		if (task != null) {
			task.cancel(true);
		}
		task = new GetNutitionTask();
		task.execute();
	}

	private void initView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleLeftBtn.setOnClickListener(this);
		titleContentView.setText("营养成分摄入情况");
		titleRightBtn.setVisibility(View.GONE);

		oneView = (TextView) findViewById(R.id.txt_nutition_num_one);
		twoView = (TextView) findViewById(R.id.txt_nutition_num_two);
		threeView = (TextView) findViewById(R.id.txt_nutition_num_three);
		fourView = (TextView) findViewById(R.id.txt_nutition_num_four);
		fiveView = (TextView) findViewById(R.id.txt_nutition_num_five);

		oneImage = (ImageView) findViewById(R.id.image_nutition_one);
		twoImage = (ImageView) findViewById(R.id.image_nutition_two);
		threeImage = (ImageView) findViewById(R.id.image_nutition_three);
		fourImage = (ImageView) findViewById(R.id.image_nutition_four);
		fiveImage = (ImageView) findViewById(R.id.image_nutition_five);

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (!isMeasured) {
			LinearLayout chartLayout = (LinearLayout) findViewById(R.id.lay_chart);
			chartLayHeight = chartLayout.getHeight();
			System.out.println(chartLayHeight);
			isMeasured = true;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		}
	}

	private class GetNutitionTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			UserInfo user = application.getUserInfo();
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("token", user.getToken()));
			pairs.add(new BasicNameValuePair("mId", user.getId()));
			pairs.add(new BasicNameValuePair("sdate", "2014-01-08"));
			pairs.add(new BasicNameValuePair("edate", "2014-01-15"));
			return HttpPostRequest.getDataFromWebServer(
					NutitionChartActivity.this, "getFiveNutition", pairs);
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println("获取营养成分返回: " + result);
			task = null;
			try {
				JSONObject object = new JSONObject(result);
				int status = object.optInt("status");
				if (status == 0) {
					double one = object.optDouble("nutritionOne");
					double two = object.optDouble("nutritionTwo");
					double three = object.optDouble("nutritionThree");
					double four = object.optDouble("nutritionForth");
					double five = object.optDouble("calorieIn");
					oneView.setText(one
							+ object.optString("nutritionOneUnitType"));
					twoView.setText(two
							+ object.optString("nutritionTwoUnitType"));
					threeView.setText(three
							+ object.optString("nutritionThreeUnitType"));
					fourView.setText(four
							+ object.optString("nutritionForthUnitType"));
					fiveView.setText(five + object.optString("calorieInUnity"));
					drawChart(one, two, three, four, five);
				} else {
					showToast(R.string.net_error);
				}
			} catch (JSONException e) {
				showToast(R.string.net_error);
			}
		}

	}

	private void drawChart(double one, double two, double three, double four,
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
		threeImage.setLayoutParams(params3);
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

	@Override
	protected void onPause() {
		super.onPause();
		if (task != null) {
			task.cancel(true);
		}
	}

}
