package com.innouni.health.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseFragmentActivity;
import com.innouni.health.entity.UserInfo;
import com.innouni.health.net.HttpPostRequest;

/**
 * 注册第三步
 * 
 * @author HuGuojun
 * @date 2013-12-20 下午4:29:04
 * @modify
 * @version 1.0.0
 */
public class RegisterStepThreeActivity extends BaseFragmentActivity implements
		OnClickListener {

	private ProgressDialog dialog;
	private Button nextButton;
	private HashMap<String, String> map;
	private String userId;
	private RegisterTask task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_step3);
		application = MainApplication.getApplication();
		application.setActivity(this);
		userId = getIntent().getStringExtra("userId");
		initView();
	}

	private void initView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleContentView.setText("会员注册");
		titleLeftBtn.setOnClickListener(this);
		titleRightBtn.setVisibility(View.GONE);

		TextView naView = (TextView) findViewById(R.id.txt_step3);
		naView.setBackgroundResource(R.drawable.center_selected_nav);
		naView.setTextColor(Color.WHITE);

		nextButton = (Button) findViewById(R.id.btn_next_step);
		nextButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.btn_next_step:
			handlerSubBtn();
			if (task != null) {
				task.cancel(true);
			}
			task = new RegisterTask();
			task.execute();
			break;
		}

	}

	private void handlerSubBtn() {
		map = new HashMap<String, String>();
		FragmentManager manager = getSupportFragmentManager();
		Fragment fragment = manager.findFragmentById(R.id.fragment_health_info);
		View view = fragment.getView();
		LinearLayout container = (LinearLayout) view
				.findViewById(R.id.lay_health_info_container);
		int size = container.getChildCount();
		for (int i = 0; i < size; i++) {
			LinearLayout linear = (LinearLayout) container.getChildAt(i);
			String desc = linear.getContentDescription().toString();
			String id = linear.getTag().toString();
			if (desc.equals(HealthInfoActivity.TYPE_RADIO)) {
				RadioGroup group = (RadioGroup) linear.getChildAt(1);
				RadioButton negativeBtn = (RadioButton) group.getChildAt(0);
				RadioButton positiveBtn = (RadioButton) group.getChildAt(1);
				String isChecked;
				if (negativeBtn.isChecked()) {
					isChecked = "no";
				} else if (positiveBtn.isChecked()) {
					isChecked = "yes";
				} else {
					isChecked = "";
				}
				map.put(id, isChecked);
			} else if (desc.equals(HealthInfoActivity.TYPE_EDIT)) {
				EditText editText = (EditText) linear.getChildAt(1);
				String content = editText.getText().toString();
				map.put(id, content);
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
			Iterator<String> it = map.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				String value = map.get(key);
				pairs.add(new BasicNameValuePair(key, value));
			}
			String json = HttpPostRequest.getDataFromWebServer(
					RegisterStepThreeActivity.this, "regMemberThree", pairs);
			System.out.println("注册第三步: " + json);
			try {
				JSONObject object = new JSONObject(json);
				int status = object.optInt("status");
				if (status == 0) {
					application = MainApplication.getApplication();
					UserInfo user = application.getUserInfo();
					user.setToken(object.optString("mToken"));
					user.setId(object.optString("mId"));
					user.setType(object.optString("mType"));
				}
				return status;
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(Integer status) {
			task = null;
			dialog.dismiss();
			if (status == null) {
				showToast(R.string.net_error);
			} else if (status == 0) {
				Intent intent = new Intent(RegisterStepThreeActivity.this,
						RegisterStepFourActivity.class);
				startActivity(intent);
			} else {
				showToast(R.string.register_error);
			}
		}

	}

	private void showDialog() {
		dialog = new ProgressDialog(RegisterStepThreeActivity.this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setTitle(getResources().getString(R.string.register_state));
		dialog.setMessage(getResources().getString(R.string.registering));
		dialog.setIndeterminate(false);
		dialog.setCancelable(true);
		dialog.show();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (task != null) {
			task.cancel(true);
		}
	}
}

// http://192.168.1.100/zwjk_app_server/?c=app&a=regMemberThree&mId=20140106561274&1111111111=45&0000000000=45&0000000001=34&0000000002=no&0000000003=no