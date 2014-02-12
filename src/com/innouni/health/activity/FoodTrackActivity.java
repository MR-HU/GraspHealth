package com.innouni.health.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseFragmentActivity;
import com.innouni.health.entity.UserInfo;
import com.innouni.health.fragment.DietBreakFastFragment;
import com.innouni.health.fragment.DietDinnerFragment;
import com.innouni.health.fragment.DietLunchFragment;
import com.innouni.health.fragment.DietOtherFragment;
import com.innouni.health.net.HttpPostRequest;
import com.innouni.health.util.CalendaUtil;

/**
 * 食物追踪页面
 * 
 * @author HuGuojun
 * @date 2014-1-13 下午2:23:12
 * @modify
 * @version 1.0.0
 */
public class FoodTrackActivity extends BaseFragmentActivity implements
		OnClickListener {

	private TextView totalView, targetView, eatView, sportView, leftView;
	private ProgressDialog dialog;
	private TextView scheduleView;
	private TextView timeView;

	private String currentTime, todayDate;
	private String time = "今日";
	private GetDietInfoTask task;
	private GetCaloryTask caloryTask;

	private double breakCal = 0, lunckCal = 0, dinnerCal = 0, otherCal = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_food_track);
		application = MainApplication.getApplication();
		application.setActivity(this);
		initView();
		getData();
	}

	public void getData() {
		if (task != null) {
			task.cancel(true);
		}
		task = new GetDietInfoTask();
		task.execute();

		if (caloryTask != null) {
			caloryTask.cancel(true);
		}
		caloryTask = new GetCaloryTask();
		caloryTask.execute();
	}

	private void initView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleContentView.setText("食物追踪");
		titleLeftBtn.setOnClickListener(this);
		titleRightBtn.setVisibility(View.GONE);

		totalView = (TextView) findViewById(R.id.txt_total);
		targetView = (TextView) findViewById(R.id.txt_target);
		eatView = (TextView) findViewById(R.id.txt_eat);
		sportView = (TextView) findViewById(R.id.txt_sport);
		leftView = (TextView) findViewById(R.id.txt_left);

		scheduleView = (TextView) findViewById(R.id.txt_food_progress);
		timeView = (TextView) findViewById(R.id.txt_time_current);
		todayDate = CalendaUtil.getCurrentDate();
		currentTime = todayDate;
		timeView.setText(time);

		findViewById(R.id.txt_time_preview).setOnClickListener(this);
		findViewById(R.id.txt_time_predict).setOnClickListener(this);
		findViewById(R.id.txt_add_food_list).setOnClickListener(this);
		findViewById(R.id.lay_food_pay).setOnClickListener(this);
	}

	/**
	 * 获取当前卡路里
	 */
	private class GetCaloryTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			// isLoadedCalory = false;
		}

		@Override
		protected String doInBackground(Void... params) {
			UserInfo user = application.getUserInfo();
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("token", user.getToken()));
			pairs.add(new BasicNameValuePair("mId", user.getId()));
			pairs.add(new BasicNameValuePair("date", currentTime));
			return HttpPostRequest.getDataFromWebServer(FoodTrackActivity.this,
					"getCalByDate", pairs);
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println("获取当前卡路里: " + result);
			// isLoadedCalory = true;
			task = null;
			try {
				JSONObject object = new JSONObject(result);
				int status = object.optInt("status");
				if (status == 0) {
					double goal = Double.valueOf(object.opt("goal").toString());
					double food = Double.valueOf(object.opt("food").toString());
					double exercise = Double.valueOf(object.opt("exercise")
							.toString());
					double net = Double.valueOf(object.opt("net").toString());
					double total = goal - net;
					targetView.setText(goal + "");
					eatView.setText(food + "");
					sportView.setText(exercise + "");
					leftView.setText(net + "");
					totalView.setText(total + "");
				} else {
					showToast(R.string.net_error);
				}
			} catch (JSONException e) {
				showToast(R.string.net_error);
			}
		}
	}

	/**
	 * 获取饮食信息
	 */
	private class GetDietInfoTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			showDialog();
			breakCal = 0;
			lunckCal = 0;
			dinnerCal = 0;
			otherCal = 0;
		}

		@Override
		protected String doInBackground(Void... params) {
			UserInfo user = application.getUserInfo();
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("token", user.getToken()));
			pairs.add(new BasicNameValuePair("mId", user.getId()));
			pairs.add(new BasicNameValuePair("date", currentTime));
			return HttpPostRequest.getDataFromWebServer(FoodTrackActivity.this,
					"getDietInfo", pairs);
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println("获取饮食信息返回: " + result);
			dialog.dismiss();
			task = null;
			try {
				JSONObject object = new JSONObject(result);
				if (object.optInt("status") == 0) {
					scheduleView.setText(object.optInt("meals") + "%");
					initDietList(result);
					calculateBreakCal(result);
					calculateLunchCal(result);
					calculateDinnerCal(result);
					calculateOtherCal(result);
				} else {
					showToast(R.string.net_error);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				showToast(R.string.net_error);
			}
		}

	}

	private void showDialog() {
		dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setTitle(getResources().getString(R.string.net_data));
		dialog.setMessage(getResources().getString(R.string.net_loading));
		dialog.setIndeterminate(false);
		dialog.setCancelable(true);
		dialog.show();
	}

	/**
	 * @throws JSONException
	 *             构建饮食列表
	 * @description initDietList
	 * @param result
	 *            void
	 * @exception
	 */
	private void calculateBreakCal(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray array = jsonObject.optJSONArray("breakfast");
			if (array == null || array.length() == 0) {
				breakCal = 0;
			} else {
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = array.optJSONObject(i);
					double count = Double.valueOf(object
							.optString("foodAmount"));
					double cal = Double.valueOf(object.optString("calorieIn"));
					breakCal = breakCal + (count * cal);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			breakCal = 0;
		}
	}

	private void calculateLunchCal(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray array = jsonObject.optJSONArray("lunch");
			if (array == null || array.length() == 0) {
				lunckCal = 0;
			} else {
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = array.optJSONObject(i);
					double count = Double.valueOf(object
							.optString("foodAmount"));
					double cal = Double.valueOf(object.optString("calorieIn"));
					lunckCal = lunckCal + (count * cal);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			lunckCal = 0;
		}
	}

	private void calculateDinnerCal(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray array = jsonObject.optJSONArray("dinner");
			if (array == null || array.length() == 0) {
				dinnerCal = 0;
			} else {
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = array.optJSONObject(i);
					double count = Double.valueOf(object
							.optString("foodAmount"));
					double cal = Double.valueOf(object.optString("calorieIn"));
					dinnerCal = dinnerCal + (count * cal);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			dinnerCal = 0;
		}
	}

	private void calculateOtherCal(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray array = jsonObject.optJSONArray("other");
			if (array == null || array.length() == 0) {
				otherCal = 0;
			} else {
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = array.optJSONObject(i);
					double count = Double.valueOf(object
							.optString("foodAmount"));
					double cal = Double.valueOf(object.optString("calorieIn"));
					otherCal = otherCal + (count * cal);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			otherCal = 0;
		}
	}

	public void initDietList(String json) throws JSONException {
		Bundle bundle = new Bundle();
		bundle.putString("diet_info", json);
		DietBreakFastFragment breakFragmentk = new DietBreakFastFragment();
		DietLunchFragment lunchFragment = new DietLunchFragment();
		DietDinnerFragment dinnerFragment = new DietDinnerFragment();
		DietOtherFragment othFragment = new DietOtherFragment();
		breakFragmentk.setArguments(bundle);
		lunchFragment.setArguments(bundle);
		dinnerFragment.setArguments(bundle);
		othFragment.setArguments(bundle);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.lay_breakfast_container, breakFragmentk).commit();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.lay_lunch_container, lunchFragment).commit();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.lay_dinner_container, dinnerFragment).commit();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.lay_other_container, othFragment).commit();
	}

	@Override
	public void onClick(View v) {
		String date;
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.lay_food_pay:
			Intent intent = new Intent(this, FoodTrackChartActivity.class);
			intent.putExtra("break", breakCal);
			intent.putExtra("lunch", lunckCal);
			intent.putExtra("dinner", dinnerCal);
			intent.putExtra("other", otherCal);
			startActivity(intent);
			break;
		case R.id.txt_time_preview:
			date = CalendaUtil.getSpecifiedDayBefore(currentTime);
			if (date.equals(todayDate)) {
				time = "今日";
			} else {
				String week = CalendaUtil.getWeek(date);
				time = week + "|" + date;
			}
			currentTime = date;
			timeView.setText(time);
			getData();
			break;
		case R.id.txt_time_predict:
			date = CalendaUtil.getSpecifiedDayAfter(currentTime);
			if (date.equals(todayDate)) {
				time = "今日";
			} else {
				String week = CalendaUtil.getWeek(date);
				time = week + "|" + date;
			}
			currentTime = date;
			timeView.setText(time);
			getData();
			break;
		case R.id.txt_add_food_list:
			Intent addIntent = new Intent(this, AddDietStepOneActivity.class);
			startActivity(addIntent);
			break;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (task != null) {
			task.cancel(true);
		}
		if (caloryTask != null) {
			caloryTask.cancel(true);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (application.isDietChanged) {
			if (task != null) {
				task.cancel(true);
			}
			task = new GetDietInfoTask();
			task.execute();
			application.isDietChanged = false;
		}
	}
}
