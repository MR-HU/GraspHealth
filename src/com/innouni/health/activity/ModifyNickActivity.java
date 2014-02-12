package com.innouni.health.activity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
import com.innouni.health.util.Util;

/**
 * 修改昵称
 * 
 * @author HuGuojun
 * @date 2014-1-20 下午3:02:16
 * @modify
 * @version 1.0.0
 */
public class ModifyNickActivity extends BaseActivity implements OnClickListener {

	private ProgressDialog dialog;
	private String nick;
	private EditText nickText;
	private ModifyTask task;
	private UserInfo user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_nick);
		application = MainApplication.getApplication();
		application.setActivity(this);
		user = application.getUserInfo();
		initView();
	}

	private void initView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleLeftBtn.setOnClickListener(this);
		titleContentView.setText("修改昵称");
		titleRightBtn.setOnClickListener(this);
		titleRightBtn.setBackgroundResource(R.drawable.titlebar_finish_selector);

		nickText = (EditText) findViewById(R.id.edit_modify);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.btn_title_right:
			nick = nickText.getText().toString();
			if (Util.isEmpty(nick)) {
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
			pairs.add(new BasicNameValuePair("userName", nick));
			return HttpPostRequest.getDataFromWebServer(
					ModifyNickActivity.this, "editMemberName", pairs);
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
					user.setName(nick);
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
}
