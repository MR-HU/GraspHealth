package com.innouni.health.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

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

import com.innouni.health.adapter.ActiveAdapter;
import com.innouni.health.adapter.ActiveAdapter.OnOperateListener;
import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseActivity;
import com.innouni.health.entity.Activite;
import com.innouni.health.entity.UserInfo;
import com.innouni.health.net.HttpPostRequest;

/**
 * 添加运动第一步
 * 
 * @author HuGuojun
 * @date 2014-1-17 下午2:36:31
 * @modify
 * @version 1.0.0
 */
public class AddSportStepOneActivity extends BaseActivity implements
		OnClickListener, TextWatcher, OnItemClickListener {

	public static final int PUSH = 0;
	public static final int CUSROMER = 1;

	// private ProgressDialog dialog;
	private List<Object> data;
	private ActiveAdapter adapter;
	private ListView listView;
	private EditText keyView;
	private GetPushTask pushTask;
	private int type = PUSH;
	private String key = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_sport_one);
		application = MainApplication.getApplication();
		application.setActivity(this);
		initView();
		if (pushTask != null) {
			pushTask.cancel(true);
		}
		pushTask = new GetPushTask();
		pushTask.execute();
	}

	private void initView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleContentView.setText("运动伙伴");
		titleLeftBtn.setOnClickListener(this);
		titleRightBtn.setVisibility(View.GONE);

		keyView = (EditText) findViewById(R.id.edit_food_source_key);
		keyView.addTextChangedListener(this);
		listView = (ListView) findViewById(R.id.listview_food_source);
		adapter = new ActiveAdapter(this);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		adapter.setOnOperateListener(new OnOperateListener() {

			@Override
			public void onOperate(int position) {
				showToast("你好");
			}
		});
	}

	/**
	 * 推荐的运动信息读取
	 */
	private class GetPushTask extends AsyncTask<Void, Void, List<Object>> {

		@Override
		protected void onPreExecute() {
			if (data == null) {
				data = new ArrayList<Object>();
			} else {
				data.clear();
			}
			// showDialog();
		}

		@Override
		protected List<Object> doInBackground(Void... params) {
			UserInfo user = application.getUserInfo();
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			if (type == CUSROMER) {
				pairs.add(new BasicNameValuePair("name", key));
			}
			pairs.add(new BasicNameValuePair("token", user.getToken()));
			pairs.add(new BasicNameValuePair("mId", user.getId()));
			String method = type == PUSH ? "getPushExercise"
					: "searchExerciseInfo";
			String json = HttpPostRequest.getDataFromWebServer(
					AddSportStepOneActivity.this, method, pairs);
			System.out.println(user.getToken());
			System.out.println(user.getId());
			System.out.println("获取的会员运动信息: " + json);
			try {
				int status = new JSONObject(json).optInt("status");
				if (status == 0) {
					JSONArray array = new JSONObject(json)
							.optJSONArray("Actives");
					for (int i = 0; i < array.length(); i++) {
						Activite activite = new Activite();
						JSONObject object = array.optJSONObject(i);
						activite.setId(object.opt("id"));
						activite.setActiveId(object.opt("activeId"));
						activite.setActiveName(object.opt("activeName"));
						activite.setActiveASlope(object.opt("activeASlope"));
						activite.setActiveBInterept(object
								.opt("activeBInterept"));
						activite.setActiveSubType(object.opt("activeSubType"));
						activite.setActiveSubTypeName(object
								.opt("activeSubTypeName"));
						data.add(activite);
					}
				}
				return data;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<Object> result) {
			// dialog.dismiss();
			pushTask = null;
			if (result != null) {
				adapter.clear();
				adapter.setType(type);
				adapter.setList(result, true);
			} else {
				showToast(R.string.net_error);
			}
		}

	}

	// private void showDialog() {
	// dialog = new ProgressDialog(this);
	// dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	// dialog.setMessage(getResources().getString(R.string.load_loading_data));
	// dialog.setIndeterminate(false);
	// dialog.setCancelable(true);
	// dialog.show();
	// }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Activite activite = (Activite) adapter.getItem(position);
		Intent intent = new Intent(this, AddSportStepTwoActivity.class);
		intent.putExtra("activeName", activite.getActiveName().toString());
		intent.putExtra("activeId", activite.getActiveId().toString());
		intent.putExtra("activeSubTypeId", activite.getActiveSubType()
				.toString());
		intent.putExtra("activeASlope", activite.getActiveASlope().toString());
		intent.putExtra("activeBInterept", activite.getActiveBInterept()
				.toString());
		startActivity(intent);
		finish();
	}

	@Override
	public void afterTextChanged(Editable s) {
		key = s.toString();
		if (key.equals("")) {
			type = PUSH;
		} else {
			type = CUSROMER;
		}
		if (pushTask != null) {
			pushTask.cancel(true);
		}
		pushTask = new GetPushTask();
		pushTask.execute();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (pushTask != null) {
			pushTask.cancel(true);
		}
	}
}
