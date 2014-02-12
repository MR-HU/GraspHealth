package com.innouni.health.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
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
 * 健康信息页面
 * 
 * @author HuGuojun
 * @date 2013-12-25 下午4:59:40
 * @modify
 * @version 1.0.0
 */
public class HealthInfoActivity extends BaseFragmentActivity implements
		OnClickListener {

	public static final String TYPE_RADIO = "单选";
	public static final String TYPE_EDIT = "填空";

	private ProgressDialog dialog;
	private HashMap<String, String> map;
	private SubmitInfoTask task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_health_info);
		application = MainApplication.getApplication();
		application.setActivity(this);
		initBar();
	}

	private void initBar() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleLeftBtn.setOnClickListener(this);
		titleContentView.setText("健康信息");
		titleRightBtn.setOnClickListener(this);
		titleRightBtn.setBackgroundResource(R.drawable.titlebar_finish_selector);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.btn_title_right:
			handlerSubBtn();
			if (task != null) {
				task.cancel(true);
			}
			task = new SubmitInfoTask();
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

	private class SubmitInfoTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected void onPreExecute() {
			showDialog();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			UserInfo user = application.getUserInfo();
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("token", user.getToken()));
			pairs.add(new BasicNameValuePair("mId", user.getId()));
			Iterator<String> it = map.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				String value = map.get(key);
				pairs.add(new BasicNameValuePair(key, value));
			}
			String json = HttpPostRequest.getDataFromWebServer(
					HealthInfoActivity.this, "editMemberHealth", pairs);
			System.out.println("修改健康信息返回: " + json);
			try {
				return new JSONObject(json).optInt("status");
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(Integer status) {
			dialog.dismiss();
			task = null;
			if (status == null) {
				showToast(R.string.net_error);
			} else if (status == 0) {
				showToast(R.string.health_submit_success);
				finish();
			} else {
				showToast(R.string.health_submit_fail);
			}
		}

	}

	private void showDialog() {
		dialog = new ProgressDialog(HealthInfoActivity.this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setTitle(getResources().getString(R.string.net_submit));
		dialog.setMessage(getResources().getString(R.string.net_submiting));
		dialog.setIndeterminate(false);
		dialog.setCancelable(false);
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
