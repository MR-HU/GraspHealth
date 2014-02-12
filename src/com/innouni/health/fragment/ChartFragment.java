package com.innouni.health.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.innouni.health.activity.NutitionChartActivity;
import com.innouni.health.activity.R;
import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseFragment;
import com.innouni.health.entity.UserInfo;
import com.innouni.health.net.HttpPostRequest;
import com.innouni.health.util.CalendaUtil;

/**
 * 主页柱状图显示
 * 
 * @author HuGuojun
 * @date 2014-1-13 下午3:46:24
 * @modify
 * @version 1.0.0
 */
public class ChartFragment extends BaseFragment implements
		OnCheckedChangeListener, OnClickListener {

	private View view;
	private TextView eatView, sportView, leftView;
	private ImageView eatChart, sportChart;
	private TextView eatChartView, sportChartView;
	private RadioGroup group;
	private LinearLayout chartLayout;
	private GetCaloryTask dayTask;
	private GetCaloryWeekTask weekTask;
	private boolean isMeasured = false;
	private int chartLayHeight;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_chart, container, false);
		application = MainApplication.getApplication();
		initView();
		return view;
	}

	private void initView() {
		eatView = (TextView) view.findViewById(R.id.txt_eat);
		sportView = (TextView) view.findViewById(R.id.txt_sport);
		leftView = (TextView) view.findViewById(R.id.txt_left);
		eatChart = (ImageView) view.findViewById(R.id.food_chart);
		sportChart = (ImageView) view.findViewById(R.id.sport_chart);
		eatChartView = (TextView) view.findViewById(R.id.txt_food_eat);
		sportChartView = (TextView) view.findViewById(R.id.txt_sport_eat);
		group = (RadioGroup) view.findViewById(R.id.group_chart_type);
		group.setOnCheckedChangeListener(this);

		chartLayout = (LinearLayout) view.findViewById(R.id.lay_chart);
		chartLayout.setOnClickListener(this);
		ViewTreeObserver observer = chartLayout.getViewTreeObserver();
		observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				if (!isMeasured) {
					chartLayHeight = chartLayout.getMeasuredHeight();
					isMeasured = true;
				}
				return true;
			}
		});

		if (dayTask != null) {
			dayTask.cancel(true);
		}
		dayTask = new GetCaloryTask();
		dayTask.execute();
	}

	/**
	 * 获得本日卡路里
	 */
	private class GetCaloryTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			UserInfo user = application.getUserInfo();
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("token", user.getToken()));
			pairs.add(new BasicNameValuePair("mId", user.getId()));
			pairs.add(new BasicNameValuePair("date", CalendaUtil.getCurrentDate()));
			return HttpPostRequest.getDataFromWebServer(getActivity(),
					"getCalByDate", pairs);
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println("获取当日卡路里: " + result);
			try {
				JSONObject object = new JSONObject(result);
				int status = object.optInt("status");
				if (status == 0) {
					double food = Double.valueOf(object.opt("food").toString());
					double exercise = Double.valueOf(object.opt("exercise")
							.toString());
					double net = Double.valueOf(object.opt("net").toString());
					eatView.setText(food + "");
					sportView.setText(exercise + "");
					leftView.setText(net + "");
					eatChartView.setText("摄入" + food);
					sportChartView.setText("消耗" + exercise);
					drawChart(food, exercise);
				} else {
					showToast(R.string.net_error);
				}
			} catch (JSONException e) {
				showToast(R.string.net_error);
			}
		}
	}

	/**
	 * 获得本周卡路里
	 */
	private class GetCaloryWeekTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			UserInfo user = application.getUserInfo();
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("token", user.getToken()));
			pairs.add(new BasicNameValuePair("mId", user.getId()));
			return HttpPostRequest.getDataFromWebServer(getActivity(),
					"getCalWithWeek", pairs);
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println("获取本周卡路里: " + result);
			try {
				JSONObject object = new JSONObject(result);
				int status = object.optInt("status");
				if (status == 0) {
					double food = Double.valueOf(object.opt("food").toString());
					double exercise = Double.valueOf(object.opt("exercise")
							.toString());
					double net = Double.valueOf(object.opt("net").toString());
					eatView.setText(food + "");
					sportView.setText(exercise + "");
					leftView.setText(net + "");
					eatChartView.setText("摄入" + food);
					sportChartView.setText("消耗" + exercise);
					drawChart(food, exercise);
				} else {
					showToast(R.string.net_error);
				}
			} catch (JSONException e) {
				showToast(R.string.net_error);
			}
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.radio_one_day:
			if (dayTask != null) {
				dayTask.cancel(true);
			}
			dayTask = new GetCaloryTask();
			dayTask.execute();
			break;
		case R.id.radio_one_week:
			if (weekTask != null) {
				weekTask.cancel(true);
			}
			weekTask = new GetCaloryWeekTask();
			weekTask.execute();
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lay_chart:
			Intent intent = new Intent(getActivity(),
					NutitionChartActivity.class);
			startActivity(intent);
			break;
		}
	}

	private void drawChart(double caloryIn, double caloryOut) {
		int standard = ((int) getMax(caloryIn, caloryOut)) + 10;
		int caloryInHeight = (int) (caloryIn * chartLayHeight / standard);
		int caloryOutHeight = (int) (caloryOut * chartLayHeight / standard);
		LinearLayout.LayoutParams params1 = new LayoutParams(
				LayoutParams.WRAP_CONTENT, caloryInHeight);
		LinearLayout.LayoutParams params2 = new LayoutParams(
				LayoutParams.WRAP_CONTENT, caloryOutHeight);
		eatChart.setLayoutParams(params1);
		sportChart.setLayoutParams(params2);
	}

	private double getMax(double param1, double param2) {
		if (param1 > param2) {
			return param1;
		} else {
			return param2;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (dayTask != null) {
			dayTask.cancel(true);
		}
		if (weekTask != null) {
			weekTask.cancel(true);
		}
	}

}
