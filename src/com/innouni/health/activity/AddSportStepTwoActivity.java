package com.innouni.health.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseActivity;
import com.innouni.health.entity.UserInfo;
import com.innouni.health.net.HttpPostRequest;
import com.innouni.health.util.CalendaUtil;
import com.innouni.health.util.Util;

/**
 * 添加运动第二步
 * 
 * @author HuGuojun
 * @date 2014-1-17 下午4:47:02
 * @modify
 * @version 1.0.0
 */
public class AddSportStepTwoActivity extends BaseActivity implements
		OnClickListener, TextWatcher {

	private ProgressDialog dialog;
	private String activeName;
	private String activiteId;
	private String activiteSubTypeId;
	private double slope, interept;
	private String minute, calory;
	private EditText minuteView;
	private TextView calView;
	private SubmitDataTask task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_sport_two);
		application = MainApplication.getApplication();
		application.setActivity(this);
		Bundle bundle = getIntent().getExtras();
		activeName = bundle.getString("activeName");
		activiteId = bundle.getString("activeId");
		activiteSubTypeId = bundle.getString("activeSubTypeId");
		slope = Double.valueOf(bundle.getString("activeASlope"));
		interept = Double.valueOf(bundle.getString("activeBInterept"));
		initView();
	}

	private void initView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleContentView.setText(activeName);
		titleLeftBtn.setOnClickListener(this);
		titleRightBtn.setOnClickListener(this);
		titleRightBtn.setBackgroundResource(R.drawable.titlebar_finish_selector);

		minuteView = (EditText) findViewById(R.id.edit_minute);
		minuteView.addTextChangedListener(this);
		calView = (TextView) findViewById(R.id.txt_calory);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.btn_title_right:
			minute = minuteView.getText().toString();
			calory = calView.getText().toString();
			if (Util.isEmpty(minute)) {
				showToast(R.string.register_info_uncomplete);
			} else {
				if (task != null) {
					task.cancel(true);
				}
				task = new SubmitDataTask();
				task.execute();
			}
			break;
		}
	}

	private class SubmitDataTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			showDialog();
		}

		@Override
		protected String doInBackground(Void... params) {
			UserInfo user = application.getUserInfo();
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("token", user.getToken()));
			pairs.add(new BasicNameValuePair("mId", user.getId()));
			pairs.add(new BasicNameValuePair("activeId", activiteId));
			pairs.add(new BasicNameValuePair("activeSubType", activiteSubTypeId));
			pairs.add(new BasicNameValuePair("activeTime", minute));
			pairs.add(new BasicNameValuePair("calorieOut", (1000 * Double
					.valueOf(calory)) + ""));
			pairs.add(new BasicNameValuePair("date", CalendaUtil
					.getCurrentDate()));
			return HttpPostRequest.getDataFromWebServer(
					AddSportStepTwoActivity.this, "addExerciseLog", pairs);
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println("添加运动返回: " + result);
			dialog.dismiss();
			task = null;
			try {
				int status = new JSONObject(result).optInt("status");
				if (status == 0) {
					showToast(R.string.sport_add_success);
					finish();
				} else {
					showToast(R.string.sport_add_fail);
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
		dialog.setMessage(getResources().getString(R.string.net_submiting));
		dialog.setIndeterminate(false);
		dialog.setCancelable(true);
		dialog.show();
	}

	@Override
	public void afterTextChanged(Editable s) {
		String time = s.toString();
		if (!Util.isEmpty(time)) {
			UserInfo user = application.getUserInfo();
			if (user != null) {
				// 需要获得用户的weight
				double weight = Double.valueOf(user.getWeight());
				double kcalhour = slope * weight + interept;
				calView.setText(subString((kcalhour * Double.valueOf(time) / 60)
						+ ""));
			}
		} else {
			calView.setText("");
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	/**
	 * 截取两位小数
	 */
	private String subString(String num) {
		if (!num.contains(".")) {
			return num;
		} else {
			DecimalFormat df = new DecimalFormat(".##");
			double d = Double.valueOf(num);
			return df.format(d);
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
