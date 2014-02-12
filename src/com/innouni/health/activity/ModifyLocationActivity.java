package com.innouni.health.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.innouni.health.adapter.CityAdapter;
import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseActivity;
import com.innouni.health.entity.City;
import com.innouni.health.entity.Province;
import com.innouni.health.entity.UserInfo;
import com.innouni.health.net.HttpPostRequest;
import com.innouni.health.util.Util;

/**
 * 修改地区
 * 
 * @author HuGuojun
 * @date 2014-1-20 下午4:02:00
 * @modify
 * @version 1.0.0
 */
public class ModifyLocationActivity extends BaseActivity implements
		OnClickListener {

	private Spinner provinceSpinner, citySpinner;
	private ProgressDialog dialog;
	private ModifyTask task;
	private UserInfo user;
	private String userProvince, userCity;
	private String userProvinceName, userCityName;
	private GetProvinceTask provinceTask;
	private GetCityTask cityTask;
	private CityAdapter provinceAdapter;
	private CityAdapter cityAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_location);
		application = MainApplication.getApplication();
		application.setActivity(this);
		user = application.getUserInfo();
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
		titleLeftBtn.setOnClickListener(this);
		titleContentView.setText("修改地区");
		titleRightBtn.setOnClickListener(this);
		titleRightBtn.setBackgroundResource(R.drawable.titlebar_finish_selector);

		provinceSpinner = (Spinner) findViewById(R.id.spinner_province);
		citySpinner = (Spinner) findViewById(R.id.spinner_city);
		provinceSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				Province province = (Province) parent
						.getItemAtPosition(position);
				userProvince = province.getId();
				userProvinceName = province.getName();
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
				userCityName = city.getName();
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
		case R.id.btn_title_right:
			if (Util.isEmpty(userProvince) || Util.isEmpty(userCity)) {
				showToast(R.string.register_info_uncomplete);
			} else {
				if (task != null) {
					task.cancel(true);
				}
				task = new ModifyTask();
				task.execute();
			}
			break;
		}
	}

	private class ModifyTask extends AsyncTask<Void, Void, String> {
		@Override
		protected void onPreExecute() {
			showDialog();
		}

		@Override
		protected String doInBackground(Void... params) {
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("token", user.getToken()));
			pairs.add(new BasicNameValuePair("mId", user.getId()));
			pairs.add(new BasicNameValuePair("userProvince", userProvince));
			pairs.add(new BasicNameValuePair("userCity", userCity));
			return HttpPostRequest.getDataFromWebServer(
					ModifyLocationActivity.this, "editMemberCity", pairs);
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println("修改返回 : " + result);
			dialog.dismiss();
			task = null;
			try {
				JSONObject object = new JSONObject(result);
				int status = object.optInt("status");
				if (status == 0) {
					showToast(R.string.modify_success);
					user.setProvince(userProvinceName);
					user.setCity(userCityName);
					finish();
				} else {
					showToast(R.string.modify_fail);
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

	private class GetProvinceTask extends AsyncTask<Void, Void, List<Object>> {

		@Override
		protected List<Object> doInBackground(Void... params) {
			String json = HttpPostRequest.getDataFromWebServer(
					ModifyLocationActivity.this, "getProvince", null);
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
				provinceAdapter = new CityAdapter(ModifyLocationActivity.this,
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
					ModifyLocationActivity.this, "getCity", pairs);
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
				cityAdapter = new CityAdapter(ModifyLocationActivity.this,
						result, CityAdapter.TYPE_CITY);
				citySpinner.setAdapter(cityAdapter);
			}
		}

	}
}
