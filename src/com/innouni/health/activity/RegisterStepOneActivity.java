package com.innouni.health.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseActivity;
import com.innouni.health.net.HttpPostRequest;
import com.innouni.health.util.Util;

/**
 * 注册第一步
 * 
 * @author HuGuojun
 * @date 2013-12-20 下午4:27:52
 * @modify
 * @version 1.0.0
 */
public class RegisterStepOneActivity extends BaseActivity implements
		OnClickListener {

	private ProgressDialog dialog;
	private RadioGroup radioGroup;
	private LinearLayout unitLayout;
	private EditText activationText, emailText, pwdText, surePwdText;
	private CheckBox protocalBox;
	private TextView protocalView;
	private Button nextBtn;
	// 记录是否单位会员
	private boolean isUnitMember = true;
	private String email, roleType, unitCode, password, surePwd;
	private String userId;
	private RegisterTask task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_step1);
		application = MainApplication.getApplication();
		application.setActivity(this);
		initView();
	}

	private void initView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleContentView.setText("会员注册");
		titleLeftBtn.setOnClickListener(this);
		titleRightBtn.setVisibility(View.GONE);

		TextView naView = (TextView) findViewById(R.id.txt_step1);
		naView.setBackgroundResource(R.drawable.left_corner_selected_nav);
		naView.setTextColor(Color.WHITE);

		activationText = (EditText) findViewById(R.id.edit_register_unit_activation);
		emailText = (EditText) findViewById(R.id.edit_register_email);
		pwdText = (EditText) findViewById(R.id.edit_register_pwd);
		surePwdText = (EditText) findViewById(R.id.edit_register_ensure_pwd);

		protocalBox = (CheckBox) findViewById(R.id.check_protocal);
		protocalView = (TextView) findViewById(R.id.txt_protocal);
		nextBtn = (Button) findViewById(R.id.btn_next_step);
		protocalBox.setOnClickListener(this);
		protocalView.setOnClickListener(this);
		nextBtn.setOnClickListener(this);

		unitLayout = (LinearLayout) findViewById(R.id.lay_unit_activation);
		radioGroup = (RadioGroup) findViewById(R.id.radio_member_type);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.radio_normal_member) {
					unitLayout.setVisibility(View.GONE);
					isUnitMember = false;
				} else if (checkedId == R.id.radio_unit_member) {
					unitLayout.setVisibility(View.VISIBLE);
					isUnitMember = true;
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.check_protocal:
			if (protocalBox.isChecked()) {
				nextBtn.setEnabled(true);
			} else {
				nextBtn.setEnabled(false);
			}
			break;
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.txt_protocal:
			Intent intent = new Intent(RegisterStepOneActivity.this,
					ProtocalActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_next_step:
			if (protocalBox.isChecked()) {
				roleType = isUnitMember ? String.valueOf(1) : String.valueOf(0);
				unitCode = isUnitMember ? activationText.getText().toString()
						: "";
				email = emailText.getText().toString();
				password = pwdText.getText().toString();
				surePwd = surePwdText.getText().toString();
				checkInput();
			} else {
				showToast(R.string.register_agreen_protocal_first);
			}
			break;
		}
	}

	private void checkInput() {
		if (isUnitMember
				&& (Util.isEmpty(unitCode) || Util.isEmpty(email)
						|| Util.isEmpty(password) || Util.isEmpty(surePwd))) {
			showToast(R.string.register_info_uncomplete);
			return;
		}
		if (!isUnitMember
				&& (Util.isEmpty(email) || Util.isEmpty(password) || Util
						.isEmpty(surePwd))) {
			showToast(R.string.register_info_uncomplete);
			return;
		}
		if (!Util.checkEmail(email)) {
			showToast(R.string.register_email_error);
			return;
		}
		if (password.length() < 6 || password.length() > 18) {
			showToast(R.string.register_pwd_error);
			return;
		}
		if (!surePwd.equals(password)) {
			showToast(R.string.register_pwd_unsame);
			return;
		}
		if (task != null) {
			task.cancel(true);
		}
		task = new RegisterTask();
		task.execute();
	}

	private class RegisterTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected void onPreExecute() {
			showDialog();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("email", email));
			pairs.add(new BasicNameValuePair("type", roleType));
			pairs.add(new BasicNameValuePair("unitCode", unitCode));
			pairs.add(new BasicNameValuePair("password", password));
			String json = HttpPostRequest.getDataFromWebServer(
					RegisterStepOneActivity.this, "regMemberOne", pairs);
			System.out.println("注册第一步: " + json);
			if (json == null || json.equals("net_err")) {
				return null;
			} else {
				try {
					JSONObject object = new JSONObject(json);
					userId = object.optString("mId");
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
				Intent intent = new Intent(RegisterStepOneActivity.this,
						RegisterStepTwoActivity.class);
				intent.putExtra("userId", userId);
				startActivity(intent);
			} else if (status == 1) {
				showToast(R.string.register_email_used);
			} else if (status == 2) {
				showToast(R.string.register_unicode_error);
			}
		}

	}

	private void showDialog() {
		dialog = new ProgressDialog(RegisterStepOneActivity.this);
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
