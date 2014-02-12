package com.innouni.health.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.innouni.health.adapter.CityAdapter;
import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseActivity;
import com.innouni.health.entity.City;
import com.innouni.health.entity.Province;
import com.innouni.health.net.HttpPostRequest;
import com.innouni.health.util.CalendaUtil;
import com.innouni.health.util.Util;

/**
 * 注册第二步
 * 
 * @author HuGuojun
 * @date 2013-12-20 下午4:28:46
 * @modify
 * @version 1.0.0
 */
public class RegisterStepTwoActivity extends BaseActivity implements
		OnClickListener {

	private ProgressDialog dialog;
	private EditText nickText, phoneText, heightText, weightText;
	private Spinner provinceSpinner, citySpinner;
	private RadioGroup genderGroup;
	private Button nextButton, birthText;
	private String userId;
	private String userProvince, userCity;
	private String userName, mobileNo, userHeight, userWeight, birthday;
	private int sex = 1; // 默认性别为女
	private GetProvinceTask provinceTask;
	private GetCityTask cityTask;
	private CityAdapter provinceAdapter;
	private CityAdapter cityAdapter;
	private RegisterTask task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_step2);
		application = MainApplication.getApplication();
		application.setActivity(this);
		userId = getIntent().getStringExtra("userId");
		initView();
		if (provinceTask != null) {
			provinceTask.cancel(true);
		}
		provinceTask = new GetProvinceTask();
		provinceTask.execute();
	}

	private void initView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleContentView.setText("会员注册");
		titleLeftBtn.setOnClickListener(this);
		titleRightBtn.setVisibility(View.GONE);

		TextView naView = (TextView) findViewById(R.id.txt_step2);
		naView.setBackgroundResource(R.drawable.center_selected_nav);
		naView.setTextColor(Color.WHITE);

		nickText = (EditText) findViewById(R.id.edit_register_nick);
		phoneText = (EditText) findViewById(R.id.edit_register_phone);
		heightText = (EditText) findViewById(R.id.edit_register_height);
		weightText = (EditText) findViewById(R.id.edit_register_weight);
		birthText = (Button) findViewById(R.id.edit_register_born);
		genderGroup = (RadioGroup) findViewById(R.id.radio_member_gender);
		provinceSpinner = (Spinner) findViewById(R.id.spinner_province);
		citySpinner = (Spinner) findViewById(R.id.spinner_city);
		nextButton = (Button) findViewById(R.id.btn_next_step);
		registerEvent();
	}

	private void registerEvent() {
		birthText.setOnClickListener(this);
		nextButton.setOnClickListener(this);
		genderGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.radio_male) {
					sex = 0; // 性别为男
				} else if (checkedId == R.id.radio_female) {
					sex = 1; // 性别为女
				}
			}
		});
		provinceSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				Province province = (Province) parent
						.getItemAtPosition(position);
				userProvince = province.getId();
				if (!Util.isEmpty(userProvince)) {
					if (cityTask != null) {
						cityTask.cancel(true);
					}
					cityTask = new GetCityTask();
					cityTask.execute();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		citySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				City city = (City) parent.getItemAtPosition(position);
				userCity = city.getId();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.edit_register_born:
			// 弹出日期选择对话框
			popDatePickDialog();
			break;
		case R.id.btn_next_step:
			userName = nickText.getText().toString();
			mobileNo = phoneText.getText().toString();
			userHeight = heightText.getText().toString();
			userWeight = weightText.getText().toString();
			birthday = birthText.getText().toString();
			checkInput();
			break;
		}
	}

	private void checkInput() {
		if (Util.isEmpty(userName) || Util.isEmpty(mobileNo)
				|| Util.isEmpty(userProvince) || Util.isEmpty(userCity)
				|| Util.isEmpty(userHeight) || Util.isEmpty(userWeight)
				|| Util.isEmpty(String.valueOf(sex)) || Util.isEmpty(birthday)) {
			showToast(R.string.register_info_uncomplete);
			return;
		}
		if (userName.length() > 32) {
			showToast(R.string.register_name_too_long);
			return;
		}
		if (!Util.checkMobile(mobileNo)) {
			showToast(R.string.register_phone_error);
			return;
		}
		if (task != null) {
			task.cancel(true);
		}
		task = new RegisterTask();
		task.execute();
	}

	private void showDialog() {
		dialog = new ProgressDialog(RegisterStepTwoActivity.this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setTitle(getResources().getString(R.string.register_state));
		dialog.setMessage(getResources().getString(R.string.registering));
		dialog.setIndeterminate(false);
		dialog.setCancelable(true);
		dialog.show();
	}

	private void popDatePickDialog() {
		Calendar calendar = Calendar.getInstance();
		DatePickerDialog dialog = new DatePickerDialog(
				RegisterStepTwoActivity.this, new OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						monthOfYear = monthOfYear + 1;
						String currentDate = CalendaUtil.getStringDateFromYmd(
								year, monthOfYear, dayOfMonth);
						birthText.setText(currentDate.replace("-", ""));
					}
				}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH));
		dialog.show();
	}

	/**
	 * 获得省份列表
	 */
	private class GetProvinceTask extends AsyncTask<Void, Void, List<Object>> {

		@Override
		protected List<Object> doInBackground(Void... params) {
			String json = HttpPostRequest.getDataFromWebServer(
					RegisterStepTwoActivity.this, "getProvince", null);
			if (json == null || json.equals("net_err")) {
				return null;
			} else {
				try {
					List<Object> provinceList = new ArrayList<Object>();
					JSONArray array = new JSONObject(json)
							.optJSONArray("Province");
					for (int i = 0; i < array.length(); i++) {
						JSONObject object = array.getJSONObject(i);
						Province province = new Province();
						province.setId(object.optString("id"));
						province.setName(object.optString("name"));
						provinceList.add(province);
					}
					return provinceList;
				} catch (Exception e) {
					return null;
				}
			}
		}

		@Override
		protected void onPostExecute(List<Object> result) {
			provinceTask = null;
			if (result != null) {
				provinceAdapter = new CityAdapter(RegisterStepTwoActivity.this,
						result, CityAdapter.TYPE_PROVINCE);
				provinceSpinner.setAdapter(provinceAdapter);
			}
		}

	}

	/**
	 * 获得城市列表
	 */
	private class GetCityTask extends AsyncTask<Void, Void, List<Object>> {

		@Override
		protected List<Object> doInBackground(Void... params) {
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("provinceId", userProvince));
			String json = HttpPostRequest.getDataFromWebServer(
					RegisterStepTwoActivity.this, "getCity", pairs);
			if (json == null || json.equals("net_err")) {
				return null;
			} else {
				try {
					List<Object> cityList = new ArrayList<Object>();
					JSONArray array = new JSONObject(json).optJSONArray("City");
					for (int i = 0; i < array.length(); i++) {
						JSONObject object = array.getJSONObject(i);
						City city = new City();
						city.setId(object.optString("id"));
						city.setName(object.optString("name"));
						cityList.add(city);
					}
					return cityList;
				} catch (Exception e) {
					return null;
				}
			}
		}

		@Override
		protected void onPostExecute(List<Object> result) {
			cityTask = null;
			if (result != null) {
				cityAdapter = new CityAdapter(RegisterStepTwoActivity.this,
						result, CityAdapter.TYPE_CITY);
				citySpinner.setAdapter(cityAdapter);
			}
		}

	}

	private class RegisterTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected void onPreExecute() {
			showDialog();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("mId", userId));
			pairs.add(new BasicNameValuePair("userName", userName));
			pairs.add(new BasicNameValuePair("mobileNo", mobileNo));
			pairs.add(new BasicNameValuePair("userProvince", userProvince));
			pairs.add(new BasicNameValuePair("userCity", userCity));
			pairs.add(new BasicNameValuePair("userHeight", userHeight));
			pairs.add(new BasicNameValuePair("userWeight", userWeight));
			pairs.add(new BasicNameValuePair("sex", sex + ""));
			pairs.add(new BasicNameValuePair("birthday", birthday));
			String json = HttpPostRequest.getDataFromWebServer(
					RegisterStepTwoActivity.this, "regMemberTwo", pairs);
			System.out.println("注册第二步: " + json);
			if (json == null || json.equals("net_err")) {
				return null;
			} else {
				try {
					JSONObject object = new JSONObject(json);
					return object.optInt("status");
				} catch (Exception e) {
					return null;
				}
			}
		}

		@Override
		protected void onPostExecute(Integer status) {
			task = null;
			dialog.dismiss();
			if (status == null) {
				showToast(R.string.net_error);
				return;
			}
			if (status == 0) {
				Intent intent = new Intent(RegisterStepTwoActivity.this,
						RegisterStepThreeActivity.class);
				intent.putExtra("userId", userId);
				startActivity(intent);
			} else if (status == 1) {
				showToast(R.string.register_name_used);
			} else if (status == 2) {
				showToast(R.string.register_phone_used);
			}
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (task != null) {
			task.cancel(true);
		}
		if (provinceTask != null) {
			provinceTask.cancel(true);
		}
		if (cityTask != null) {
			cityTask.cancel(true);
		}
	}

}

// http://192.168.1.100/zwjk_app_server/?c=app&a=regMemberTwo&mId=20140107126934&userName=hgjjgh&mobileNo=15068821317&userProvince=387&userCity=388&userHeight=170&userWeight=60&sex=0&birthday=20130303
