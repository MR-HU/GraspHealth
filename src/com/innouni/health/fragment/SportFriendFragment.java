package com.innouni.health.fragment;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.innouni.health.activity.AddSportStepOneActivity;
import com.innouni.health.activity.R;
import com.innouni.health.activity.SportFriendChartActivity;
import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseFragment;
import com.innouni.health.entity.UserInfo;
import com.innouni.health.net.HttpPostRequest;
import com.innouni.health.util.CalendaUtil;

/**
 * 运动伙伴中的运动
 * 
 * @author HuGuojun
 * @date 2014-1-16 下午5:30:55
 * @modify
 * @version 1.0.0
 */
public class SportFriendFragment extends BaseFragment implements
		OnClickListener {

	private View view;
	private ProgressDialog dialog;
	private TextView timeView;
	private String currentTime, todayDate;
	private String time = "今日";
	private TextView schedumeView;
	private TextView totalView, targetView, eatView, sportView, leftView;
	private GetCaloryTask task;
	private GetSportInfo sportTask;
	private double workCal = 0, normalCal = 0;

	// private boolean isLoadedCalory = false;
	// private boolean isLoadedSport = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_sport_friend, container,
				false);
		application = MainApplication.getApplication();
		todayDate = CalendaUtil.getCurrentDate();
		currentTime = todayDate;
		initView();
		getData();
		return view;
	}

	private void initView() {
		timeView = (TextView) view.findViewById(R.id.txt_time_current);
		timeView.setText(time);
		view.findViewById(R.id.txt_time_preview).setOnClickListener(this);
		view.findViewById(R.id.txt_time_predict).setOnClickListener(this);
		view.findViewById(R.id.lay_calory_info).setOnClickListener(this);
		view.findViewById(R.id.txt_add_sport_list).setOnClickListener(this);

		totalView = (TextView) view.findViewById(R.id.txt_total);
		targetView = (TextView) view.findViewById(R.id.txt_target);
		eatView = (TextView) view.findViewById(R.id.txt_eat);
		sportView = (TextView) view.findViewById(R.id.txt_sport);
		leftView = (TextView) view.findViewById(R.id.txt_left);
		schedumeView = (TextView) view.findViewById(R.id.txt_sport_progress);
	}

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
			return HttpPostRequest.getDataFromWebServer(getActivity(),
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
	 * 获取运动列表
	 */
	private class GetSportInfo extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			// isLoadedSport = false;
			showDialog();
			workCal = 0;
			normalCal = 0;
		}

		@Override
		protected String doInBackground(Void... params) {
			UserInfo user = application.getUserInfo();
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("token", user.getToken()));
			pairs.add(new BasicNameValuePair("mId", user.getId()));
			pairs.add(new BasicNameValuePair("date", currentTime));
			return HttpPostRequest.getDataFromWebServer(getActivity(),
					"getExerciseLog", pairs);
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println("获取运动列表返回 : " + result);
			// isLoadedSport = true;
			dialog.dismiss();
			sportTask = null;
			try {
				JSONObject object = new JSONObject(result);
				int status = object.optInt("status");
				if (status == 0) {
					schedumeView.setText(object.opt("active").toString() + "%");
					initSportList(result);
					calculateCal(result);
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
		dialog = new ProgressDialog(getActivity());
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setTitle(getResources().getString(R.string.net_data));
		dialog.setMessage(getResources().getString(R.string.net_loading));
		dialog.setIndeterminate(false);
		dialog.setCancelable(true);
		dialog.show();
	}

	private void calculateCal (String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			if (jsonObject.optInt("status") == 0) {
				JSONArray array = jsonObject.optJSONArray("actives");
				if (array == null || array.length()==0) {
					normalCal = 0;
					workCal = 0;
				} else {
					for (int i = 0; i < array.length(); i++) {
						JSONObject object = array.optJSONObject(i);
						if (object.optString("activeType").equals("0")) {
							normalCal = normalCal + Double.valueOf(object.optString("calorieOut"));
						} else {
							workCal = workCal + Double.valueOf(object.optString("calorieOut"));
						}
					}
				}
			} else {
				normalCal = 0;
				workCal = 0;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			normalCal = 0;
			workCal = 0;
		}
	}
	
	public void initSportList(String json) throws JSONException {
		Bundle bundle = new Bundle();
		bundle.putString("sport_info", json);
		SportListFragment startFragment = new SportListFragment(
				SportListFragment.GOTO_WORK);
		SportListFragment endFragment = new SportListFragment(
				SportListFragment.GOOFF_WORK);
		SportListFragment normalFragment = new SportListFragment(
				SportListFragment.NORMAL_SPORT);
		startFragment.setArguments(bundle);
		endFragment.setArguments(bundle);
		normalFragment.setArguments(bundle);
		getActivity().getSupportFragmentManager().beginTransaction()
				.replace(R.id.lay_start_work_container, startFragment).commit();
		getActivity().getSupportFragmentManager().beginTransaction()
				.replace(R.id.lay_end_work_container, endFragment).commit();
		getActivity().getSupportFragmentManager().beginTransaction()
				.replace(R.id.lay_normal_sport_container, normalFragment)
				.commit();
	}

	public void getData() {
		getCalory();
		getSportList();
	}

	public void getSportList() {
		if (sportTask != null) {
			sportTask.cancel(true);
		}
		sportTask = new GetSportInfo();
		sportTask.execute();
	}

	public void getCalory() {
		if (task != null) {
			task.cancel(true);
		}
		task = new GetCaloryTask();
		task.execute();
	}

	@Override
	public void onClick(View v) {
		String date;
		switch (v.getId()) {
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
		case R.id.lay_calory_info:
			Intent intent = new Intent(getActivity(),
					SportFriendChartActivity.class);
			intent.putExtra("workCal", workCal);
			intent.putExtra("normalCal", normalCal);
			startActivity(intent);
			break;
		case R.id.txt_add_sport_list:
			Intent intent2 = new Intent(getActivity(),
					AddSportStepOneActivity.class);
			startActivity(intent2);
			break;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (task != null) {
			task.cancel(true);
		}
		if (sportTask != null) {
			sportTask.cancel(true);
		}
	}

//	@Override
//	public void onResume() {
//		super.onResume();
//		todayDate = CalendaUtil.getCurrentDate();
//		currentTime = todayDate;
//		time = "今日";
//		timeView.setText(time);
//		getData();
//		// if (!isLoadedCalory) {
//		// getCalory();
//		// }
//		// if (!isLoadedSport) {
//		// getSportList();
//		// }
//	}
}


