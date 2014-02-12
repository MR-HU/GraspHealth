package com.innouni.health.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.innouni.health.activity.AddSportByCommomActivity;
import com.innouni.health.activity.R;
import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseFragment;
import com.innouni.health.entity.UserInfo;
import com.innouni.health.net.HttpPostRequest;
import com.innouni.health.util.Util;

/**
 * 运动伙伴中的常规
 * 
 * @author HuGuojun
 * @date 2014-1-17 下午2:52:15
 * @modify
 * @version 1.0.0
 */
public class CommomFragment extends BaseFragment implements OnClickListener,
		OnCheckedChangeListener {

	public static final int REQUEST_CODE_ON = 300;
	public static final int REQUEST_CODE_OFF = 301;
	public static final int RESULT_CODE = 302;

	private String slope, intercept;
	private String startWorkActiveId, startWorkActiveSubType, offWorkActiveId,
			offWorkActiveSubType;
	private String beginWorkTime, endWorkTime, startWorkActiveTime,
			offWorkActiveTime;
	private String workTime;
	private String startWorkCalorieOut, offWorkCalorieOut;

	private View view;
	private TimePickerDialog timePicker = null;
	private EditText toworkTime, offWorkTime, toWorkNeedTime, offWorkNeedTime;
	private TextView addToWork, addOffWork;
	private TextView toWorkContainer, offWorkContainer;
	private CheckBox box1, box2, box3, box4, box5, box6, box7;
	private GetCommomTask task;
	private SetWorkTask seTask;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_commom, null);
		application = MainApplication.getApplication();
		initView();
		if (task != null) {
			task.cancel(true);
		}
		task = new GetCommomTask();
		task.execute();
		return view;
	}

	private void initView() {
		toworkTime = (EditText) view.findViewById(R.id.edit_goto_time);
		offWorkTime = (EditText) view.findViewById(R.id.edit_off_time);
		toWorkNeedTime = (EditText) view.findViewById(R.id.edit_goto_time_need);
		offWorkNeedTime = (EditText) view.findViewById(R.id.edit_off_time_need);
		addToWork = (TextView) view.findViewById(R.id.txt_add_goto_work);
		addOffWork = (TextView) view.findViewById(R.id.txt_add_off_work);
		toWorkContainer = (TextView) view
				.findViewById(R.id.goto_work_container);
		offWorkContainer = (TextView) view
				.findViewById(R.id.goto_off_container);

		addToWork.setOnClickListener(this);
		addOffWork.setOnClickListener(this);
		toworkTime.setOnClickListener(this);
		offWorkTime.setOnClickListener(this);

		box1 = (CheckBox) view.findViewById(R.id.check1);
		box1.setOnCheckedChangeListener(this);
		box2 = (CheckBox) view.findViewById(R.id.check2);
		box2.setOnCheckedChangeListener(this);
		box3 = (CheckBox) view.findViewById(R.id.check3);
		box3.setOnCheckedChangeListener(this);
		box4 = (CheckBox) view.findViewById(R.id.check4);
		box4.setOnCheckedChangeListener(this);
		box5 = (CheckBox) view.findViewById(R.id.check5);
		box5.setOnCheckedChangeListener(this);
		box6 = (CheckBox) view.findViewById(R.id.check6);
		box6.setOnCheckedChangeListener(this);
		box7 = (CheckBox) view.findViewById(R.id.check7);
		box7.setOnCheckedChangeListener(this);

		view.findViewById(R.id.btn_submit).setOnClickListener(this);
	}

	private class GetCommomTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			UserInfo user = application.getUserInfo();
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("token", user.getToken()));
			pairs.add(new BasicNameValuePair("mId", user.getId()));
			return HttpPostRequest.getDataFromWebServer(getActivity(),
					"getWorkExercise", pairs);
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println("获取常规:" + result);
			task = null;
			try {
				JSONObject object = new JSONObject(result);
				int status = object.optInt("status");
				if (status == 0) {
					toworkTime.setText(object.optString("startWorkTime")
							.replace("null", ""));
					offWorkTime.setText(object.optString("offWorkTime")
							.replace("null", ""));
					toWorkNeedTime.setText(object.optString(
							"startWorkActiveTime").replace("null", ""));
					offWorkNeedTime.setText(object.optString(
							"offWorkActiveTime").replace("null", ""));
					String work = object.optString("WorkTime".replace("null",
							""));
					initBox(work);
					toWorkContainer.setText(object.optString(
							"startWorkActiveName").replace("null", "")
							+ object.optString("startWorkActiveSubTypeName")
									.replace("null", ""));
					offWorkContainer.setText(object.optString(
							"offWorkActiveName").replace("null", "")
							+ object.optString("offWorkActiveSubTypeName")
									.replace("null", ""));
					startWorkActiveId = object.optString("startWorkActiveId")
							.replace("null", "");
					startWorkActiveSubType = object.optString(
							"startWorkActiveSubType").replace("null", "");
					offWorkActiveId = object.optString("offWorkActiveId"
							.replace("null", ""));
					offWorkActiveSubType = object
							.optString("offWorkActiveSubType".replace("null",
									""));
				} else {
					showToast(R.string.net_error);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				showToast(R.string.net_error);
			}
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.txt_add_goto_work:
			intent = new Intent(getActivity(), AddSportByCommomActivity.class);
			startActivityForResult(intent, REQUEST_CODE_ON);
			break;
		case R.id.txt_add_off_work:
			intent = new Intent(getActivity(), AddSportByCommomActivity.class);
			startActivityForResult(intent, REQUEST_CODE_OFF);
			break;
		case R.id.edit_goto_time:
			showTimePicker(toworkTime);
			break;
		case R.id.edit_off_time:
			showTimePicker(offWorkTime);
			break;
		case R.id.btn_submit:
			submit();
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CODE) {
			slope = data.getStringExtra("activeASlope");
			intercept = data.getStringExtra("activeBInterept");
			if (requestCode == REQUEST_CODE_ON) {
				startWorkActiveId = data.getStringExtra("activeId");
				startWorkActiveSubType = data.getStringExtra("activeSubTypeId");
				toWorkContainer.setText(data.getStringExtra("activeName")
						+ data.getStringExtra("activeSubTypeName"));
			} else if (requestCode == REQUEST_CODE_OFF) {
				offWorkActiveId = data.getStringExtra("activeId");
				offWorkActiveSubType = data.getStringExtra("activeSubTypeId");
				offWorkContainer.setText(data.getStringExtra("activeName")
						+ data.getStringExtra("activeSubTypeName"));
			}
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

	}

	private void showTimePicker(final EditText textView) {
		TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				String hour = hourOfDay + "";
				String min = minute + "";
				if (hourOfDay < 10) {
					hour = "0" + hour;
				}
				if (minute < 10) {
					min = "0" + min;
				}
				textView.setText(hour + ":" + min);
				timePicker.dismiss();
			}
		};
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		timePicker = new TimePickerDialog(getActivity(), listener, hourOfDay,
				minute, true);
		timePicker.show();
	}

	public void initBox(String work) {
		if (work.contains("1")) {
			box1.setChecked(true);
		}
		if (work.contains("2")) {
			box2.setChecked(true);
		}
		if (work.contains("3")) {
			box3.setChecked(true);
		}
		if (work.contains("4")) {
			box4.setChecked(true);
		}
		if (work.contains("5")) {
			box5.setChecked(true);
		}
		if (work.contains("6")) {
			box6.setChecked(true);
		}
		if (work.contains("7")) {
			box7.setChecked(true);
		}
	}

	public void submit() {
		UserInfo user = application.getUserInfo();
		beginWorkTime = toworkTime.getText().toString();
		endWorkTime = offWorkTime.getText().toString();
		startWorkActiveTime = toWorkNeedTime.getText().toString().trim();
		if (Util.isEmpty(startWorkActiveTime)) {
			startWorkActiveTime = "0";
		}
		offWorkActiveTime = offWorkNeedTime.getText().toString().trim();
		if (Util.isEmpty(offWorkActiveTime)) {
			offWorkActiveTime = "0";
		}
		if (Util.isEmpty(slope)) {
			slope = "0";
		}
		if (Util.isEmpty(intercept)) {
			intercept = "0";
		}
		initWorktime();
		startWorkCalorieOut = (Integer.valueOf(startWorkActiveTime)
				* (Double.valueOf(slope) * user.getWeight() + Double
						.valueOf(intercept)) / 60)
				+ "";
		offWorkCalorieOut = (Integer.valueOf(offWorkActiveTime)
				* (Double.valueOf(slope) * user.getWeight() + Double
						.valueOf(intercept)) / 60)
				+ "";
		if (Util.isEmpty(beginWorkTime) || Util.isEmpty(endWorkTime)
				|| Util.isEmpty(startWorkActiveTime)
				|| Util.isEmpty(offWorkActiveTime) || Util.isEmpty(workTime)
				|| Util.isEmpty(startWorkCalorieOut)
				|| Util.isEmpty(offWorkCalorieOut)
				|| Util.isEmpty(startWorkActiveId)
				|| Util.isEmpty(offWorkActiveId)) {
			showToast(R.string.register_info_uncomplete);
		} else {
			if (seTask != null) {
				seTask.cancel(true);
			}
			seTask = new SetWorkTask();
			seTask.execute();
		}
	}

	private class SetWorkTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			UserInfo user = application.getUserInfo();
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("token", user.getToken()));
			pairs.add(new BasicNameValuePair("mId", user.getId()));
			pairs.add(new BasicNameValuePair("startWorkTime", beginWorkTime));
			pairs.add(new BasicNameValuePair("offWorkTime", endWorkTime));
			pairs.add(new BasicNameValuePair("startWorkActiveTime",
					startWorkActiveTime));
			pairs.add(new BasicNameValuePair("offWorkActiveTime",
					offWorkActiveTime));
			pairs.add(new BasicNameValuePair("WorkTime", workTime));
			pairs.add(new BasicNameValuePair("startWorkType", startWorkActiveId
					+ "_" + startWorkActiveSubType));
			pairs.add(new BasicNameValuePair("offWorkType", offWorkActiveId
					+ "_" + offWorkActiveSubType));
			pairs.add(new BasicNameValuePair("startWorkCalorieOut",
					startWorkCalorieOut));
			pairs.add(new BasicNameValuePair("offWorkCalorieOut",
					offWorkCalorieOut));
			System.out.println(beginWorkTime);
			System.out.println(endWorkTime);
			System.out.println(startWorkActiveTime);
			System.out.println(offWorkActiveTime);
			System.out.println(workTime);
			System.out
					.println(startWorkActiveId + "_" + startWorkActiveSubType);
			System.out.println(offWorkActiveId + "_" + offWorkActiveSubType);
			System.out.println(startWorkCalorieOut);
			System.out.println(offWorkCalorieOut);
			return HttpPostRequest.getDataFromWebServer(getActivity(),
					"setWorkExercise", pairs);
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println("修改常规信息返回: " + result);
			try {
				JSONObject object = new JSONObject(result);
				int status = object.optInt("status");
				if (status == 0) {
					showToast(R.string.modify_success);
				} else {
					showToast(R.string.modify_fail);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				showToast(R.string.net_error);
			}
		}
	}

	private void initWorktime() {
		workTime = "1 2 3 4 5 6 7";
		if (!box1.isChecked()) {
			workTime = workTime.replace("1", "");
		}
		if (!box2.isChecked()) {
			workTime = workTime.replace("2", "");
		}
		if (!box3.isChecked()) {
			workTime = workTime.replace("3", "");
		}
		if (!box4.isChecked()) {
			workTime = workTime.replace("4", "");
		}
		if (!box5.isChecked()) {
			workTime = workTime.replace("5", "");
		}
		if (!box6.isChecked()) {
			workTime = workTime.replace("6", "");
		}
		if (!box7.isChecked()) {
			workTime = workTime.replace("7", "");
		}
		System.out.println(workTime);
		workTime = workTime.trim().replace("  ", " ");
		while (workTime.contains("  ")) {
			workTime = workTime.replace("  ", " ");
		}
		workTime = workTime.replace(" ", ",");
	}

	@Override
	public void onPause() {
		super.onPause();
		if (task != null) {
			task.cancel(true);
		}
		if (seTask != null) {
			seTask.cancel(true);
		}
	}
}
