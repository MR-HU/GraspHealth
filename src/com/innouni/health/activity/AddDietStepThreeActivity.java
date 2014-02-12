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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.innouni.health.adapter.EnterPriseAdapter;
import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseActivity;
import com.innouni.health.entity.EnterPrise;
import com.innouni.health.entity.UserInfo;
import com.innouni.health.net.HttpPostRequest;
import com.innouni.health.util.Util;
import com.zijunlin.Zxing.Demo.CaptureActivity;

/**
 * 饮食添加第三步<br>
 * 选择食物源(公司) 
 * 
 * @author HuGuojun
 * @date 2014-1-14 下午3:25:00
 * @modify
 * @version 1.0.0
 */
public class AddDietStepThreeActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener {

	private ProgressDialog dialog;
	private String entryType; // 企业类型
	private String mealsType; // 饮食类型
	private List<Object> data;
	private EnterPriseAdapter adapter;
	private ListView listView;
	private EditText keyView;
	private GetFoodSourecTask task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_diet_three);
		application = MainApplication.getApplication();
		application.setActivity(this);
		application.setDietActivity(this);
		Bundle bundle = getIntent().getExtras();
		entryType = bundle.getString("entryType");
		mealsType = bundle.getString("mealsType");
		initView();
		if (task != null) {
			task.cancel(true);
		}
		task = new GetFoodSourecTask();
		task.execute();
	}

	private void initView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleContentView.setText("食物源");
		titleLeftBtn.setOnClickListener(this);
		titleRightBtn.setOnClickListener(this);

		listView = (ListView) findViewById(R.id.listview_food_source);
		adapter = new EnterPriseAdapter(this);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);

		keyView = (EditText) findViewById(R.id.edit_food_source_key);
		keyView.addTextChangedListener(new KeyTextWatcher());
	}

	private class GetFoodSourecTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			showDialog();
			if (data == null) {
				data = new ArrayList<Object>();
			} else {
				data.clear();
			}
		}

		@Override
		protected String doInBackground(Void... params) {
			UserInfo user = application.getUserInfo();
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("token", user.getToken()));
			pairs.add(new BasicNameValuePair("mId", user.getId()));
			pairs.add(new BasicNameValuePair("type", entryType));
			return HttpPostRequest.getDataFromWebServer(
					AddDietStepThreeActivity.this, "getFoodFrom", pairs);
		}

		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			task = null;
			System.out.println("请求食物源返回: " + result);
			try {
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.optInt("status") == 0) {
					JSONArray array = jsonObject.optJSONArray("enters");
					for (int i = 0; i < array.length(); i++) {
						EnterPrise enterPrise = new EnterPrise();
						JSONObject object = array.optJSONObject(i);
						enterPrise.setId(object.optString("id"));
						enterPrise.setName(object.optString("name"));
						enterPrise.setAbbreName(object.optString("abbreName"));
						data.add(enterPrise);
					}
					adapter.setList(data, true);
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

	private class KeyTextWatcher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			String key = s.toString();
			adapter.clear();
			if (Util.isEmpty(key)) {
				adapter.setList(data, true);
			} else {
				List<Object> subData = new ArrayList<Object>();
				for (Object enterPrise : data) {
					if (((EnterPrise) enterPrise).getName().contains(key)) {
						subData.add(enterPrise);
					}
				}
				adapter.setList(subData, true);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.btn_title_right:
			Intent intent = new Intent(this, CaptureActivity.class);
			intent.putExtra("mealsType", mealsType);
			startActivity(intent);
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		EnterPrise enterPrise = (EnterPrise) adapter.getItem(position);
		Intent intent = new Intent(this, AddDietStepFourActivity.class);
		intent.putExtra("enterId", enterPrise.getId()); // 企业ID
		intent.putExtra("entryType", entryType);
		intent.putExtra("mealsType", mealsType);
		startActivity(intent);
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
