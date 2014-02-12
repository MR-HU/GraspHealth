package com.innouni.health.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.innouni.health.activity.R;
import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseFragment;
import com.innouni.health.entity.UserInfo;
import com.innouni.health.net.HttpPostRequest;
import com.innouni.health.util.CalendaUtil;

/**
 * 主页卡路里显示
 * 
 * @author HuGuojun
 * @date 2014-1-13 下午3:46:10
 * @modify
 * @version 1.0.0
 */
public class CaloryFragment extends BaseFragment implements OnClickListener {

	private ProgressDialog dialog;
	private View view;
	private TextView timeView;
	private String currentTime, todayDate;
	private String time = "今日";
	private TextView totalView, targetView, eatView, sportView, leftView;
	private GetCaloryTask task;
	private boolean isLoaded = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_calory, container, false);
		todayDate = CalendaUtil.getCurrentDate(); // 获得今天日期
		currentTime = todayDate;
		initView();
		return view;
	}

	public void initView() {
		timeView = (TextView) view.findViewById(R.id.txt_time_current);
		timeView.setText(time);
		view.findViewById(R.id.txt_time_preview).setOnClickListener(this);
		view.findViewById(R.id.txt_time_predict).setOnClickListener(this);

		totalView = (TextView) view.findViewById(R.id.txt_calory_total);
		targetView = (TextView) view.findViewById(R.id.txt_target);
		eatView = (TextView) view.findViewById(R.id.txt_eat);
		sportView = (TextView) view.findViewById(R.id.txt_sport);
		leftView = (TextView) view.findViewById(R.id.txt_left);
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
			break;
		}
		if (task != null) {
			task.cancel(true);
		}
		task = new GetCaloryTask();
		task.execute();
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

	private class GetCaloryTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			isLoaded = false;
			showDialog();
		}

		@Override
		protected String doInBackground(Void... params) {
			application = MainApplication.getApplication();
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
			isLoaded = true;
			task = null;
			dialog.dismiss();
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

	@Override
	public void onResume() {
		super.onResume();
		if (!isLoaded) {
			if (task != null) {
				task.cancel(true);
			}
			task = new GetCaloryTask();
			task.execute();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (task != null) {
			task.cancel(true);
		}
	}
}
