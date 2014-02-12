package com.innouni.health.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseActivity;
import com.innouni.health.entity.UserInfo;
import com.innouni.health.net.HttpPostRequest;
import com.innouni.health.util.CalendaUtil;
import com.innouni.health.util.Util;

/**
 * 修改出生日期
 * 
 * @author HuGuojun
 * @date 2014-1-20 下午4:25:25
 * @modify
 * @version 1.0.0
 */
public class ModifyBirthActivity extends BaseActivity implements OnClickListener {

	private ProgressDialog dialog;
	private String nick;
	private Button birthText;
	private ModifyTask task;
	private UserInfo user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_bitrh);
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
		titleContentView.setText("修改出生日期");
		titleRightBtn.setOnClickListener(this);
		titleRightBtn.setBackgroundResource(R.drawable.titlebar_finish_selector);

		birthText = (Button) findViewById(R.id.edit_register_born);
		birthText.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.btn_title_right:
			nick = birthText.getText().toString();
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
		case R.id.edit_register_born:
			// 弹出日期选择对话框
			popDatePickDialog();
			break;
		}
	}

	private void popDatePickDialog() {
		Calendar calendar = Calendar.getInstance();
		DatePickerDialog dialog = new DatePickerDialog(
				ModifyBirthActivity.this, new OnDateSetListener() {

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
			pairs.add(new BasicNameValuePair("birthday", nick));
			return HttpPostRequest.getDataFromWebServer(
					ModifyBirthActivity.this, "editMemberBirthday", pairs);
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
					user.setBirthday(nick);
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