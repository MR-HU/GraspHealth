package com.innouni.health.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
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
 * 饮食添加第五步<br>
 * 提交数据
 * 
 * @author HuGuojun
 * @date 2014-1-14 下午7:30:24
 * @modify
 * @version 1.0.0
 */
public class AddDietStepFiveActivity extends BaseActivity implements
		OnClickListener {

	private String foodId, foodName, mealsType, calory;
	private String foodNum;
	private EditText numView;
	private ProgressDialog dialog;
	private SubmitDietTask task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_diet_five);
		application = MainApplication.getApplication();
		application.setActivity(this);
		application.setDietActivity(this);
		Bundle bundle = getIntent().getExtras();
		foodId = bundle.getString("foodId");
		foodName = bundle.getString("foodName");
		mealsType = bundle.getString("mealsType");
		calory = bundle.getString("calory");
		initView();
	}

	private void initView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleContentView.setText(foodName);
		titleLeftBtn.setOnClickListener(this);
		titleRightBtn.setVisibility(View.GONE);

		numView = (EditText) findViewById(R.id.edit_food_num);
		findViewById(R.id.btn_submit).setOnClickListener(this);
	}

	private class SubmitDietTask extends AsyncTask<Void, Void, String> {

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
			pairs.add(new BasicNameValuePair("date", CalendaUtil
					.getCurrentDate()));
			pairs.add(new BasicNameValuePair("foodId", foodId));
			pairs.add(new BasicNameValuePair("foodCount", foodNum));
			pairs.add(new BasicNameValuePair("foodCalorie", (Double
					.valueOf(calory) * Integer.valueOf(foodNum)) + ""));
			pairs.add(new BasicNameValuePair("mealsType", mealsType));
			return HttpPostRequest.getDataFromWebServer(
					AddDietStepFiveActivity.this, "addFoodLog", pairs);
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println("饮食添加返回: " + result);
			dialog.dismiss();
			task = null;
			try {
				if (new JSONObject(result).optInt("status") == 0) {
					application.isDietChanged = true;
					showToast(R.string.diet_add_success);
					List<Activity> dietActivities = application.getDietActivity();
					for (Activity activity : dietActivities) {
						if (activity != null) {
							activity.finish();
						}
					}
				} else {
					showToast(R.string.diet_add_fail);
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
		dialog.setCancelable(false);
		dialog.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.btn_submit:
			foodNum = numView.getText().toString().trim();
			if (Util.isEmpty(foodNum)) {
				showToast(R.string.register_info_uncomplete);
			} else {
				if (task != null) {
					task.cancel(true);
				}
				task = new SubmitDietTask();
				task.execute();
			}
			break;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (task != null) {
			task.cancel(true);
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
