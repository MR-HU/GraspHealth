package com.innouni.health.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseActivity;
import com.innouni.health.entity.UserInfo;
import com.innouni.health.net.DownloadImage;
import com.innouni.health.net.HttpPostRequest;
import com.innouni.health.util.Util;

/**
 * 个人中心页面
 * 
 * @author HuGuojun
 * @date 2013-12-25 上午10:42:02
 * @modify
 * @version 1.0.0
 */
public class UserCenterActivity extends BaseActivity implements OnClickListener {

	private ProgressDialog dialog;
	private ImageView avatarView, genderView;
	private TextView nickView, accountView, signView;
	private UserInfo user;
	private GetUserInfoTask task;
	private boolean isFirstLoad = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_center);
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
		titleContentView.setText("个人中心");
		titleRightBtn.setVisibility(View.GONE);

		avatarView = (ImageView) findViewById(R.id.image_user_avatar);
		genderView = (ImageView) findViewById(R.id.iamge_user_gender);
		accountView = (TextView) findViewById(R.id.txt_user_account_show);
		signView = (TextView) findViewById(R.id.txt_user_sign_show);
		nickView = (TextView) findViewById(R.id.txt_user_nick);

		findViewById(R.id.lay_user_info).setOnClickListener(this);
		findViewById(R.id.btn_health_info).setOnClickListener(this);
		findViewById(R.id.btn_user_recommend).setOnClickListener(this);
		findViewById(R.id.btn_user_favorite).setOnClickListener(this);
		findViewById(R.id.btn_user_menu).setOnClickListener(this);
		findViewById(R.id.btn_active_unit_member).setOnClickListener(this);
		findViewById(R.id.btn_exit_account).setOnClickListener(this);
	}

	private class GetUserInfoTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected void onPreExecute() {
			if (isFirstLoad) {
				showDialog();
			}
		}

		@Override
		protected Integer doInBackground(Void... params) {
			String token = user.getToken();
			String id = user.getId();
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("token", token));
			pairs.add(new BasicNameValuePair("mId", id));
			String json = HttpPostRequest.getDataFromWebServer(
					UserCenterActivity.this, "getMemberInfo", pairs);
			System.out.println("获取用户信息返回 " + json);
			try {
				JSONObject object = new JSONObject(json);
				int status = object.optInt("status");
				if (status == 0) {
					String avatar = object.optString("logo");
					if (Util.isEmpty(avatar) || avatar.equalsIgnoreCase("null")) {
						avatar = "";
					} else {
						avatar = getResources().getString(R.string.app_url)
								+ "files/m_logo/" + avatar;
					}
					user.setAvatar(avatar);
					user.setName(object.optString("userName"));
					user.setGender(object.optInt("sex"));
					String signature = object.optString("signature");
					if (Util.isEmpty(signature)
							|| signature.equalsIgnoreCase("null")) {
						signature = "暂无签名";
					}
					user.setSign(signature);
					user.setEmail(object.optString("email"));
					user.setPhone(object.optString("mobileNo"));
					user.setProvince(object.optString("province"));
					user.setCity(object.optString("city"));
					user.setBirthday(object.optString("birthday"));
					user.setHeight(object.optInt("height"));
					user.setWeight(object.optInt("weight"));
				}
				return status;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(Integer status) {
			if (isFirstLoad) {
				dialog.dismiss();
				isFirstLoad = false;
			}
			task = null;
			if (status == null) {
				showToast(R.string.net_error);
			} else if (status == 0) {
				nickView.setText(user.getName());
				accountView.setText(user.getEmail());
				signView.setText(user.getSign());
				// 设置性别
				int genderImage = user.getGender() == 0 ? R.drawable.icon_male
						: R.drawable.icon_female;
				genderView.setBackgroundResource(genderImage);
				// 加载头像
				DownloadImage.loadCircleImage(UserCenterActivity.this,
						user.getAvatar(), avatarView);
			} else {
				showToast(R.string.net_error);
			}
		}

	}

	private void showDialog() {
		dialog = new ProgressDialog(UserCenterActivity.this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setTitle(getResources().getString(R.string.net_data));
		dialog.setMessage(getResources().getString(R.string.net_loading));
		dialog.setIndeterminate(false);
		dialog.setCancelable(false);
		dialog.show();
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		// 个人信息
		case R.id.lay_user_info:
			intent = new Intent(UserCenterActivity.this, UserInfoActivity.class);
			startActivity(intent);
			break;
		// 健康信息
		case R.id.btn_health_info:
			intent = new Intent(UserCenterActivity.this,
					HealthInfoActivity.class);
			startActivity(intent);
			break;
		// 我的推荐
		case R.id.btn_user_recommend:
			intent = new Intent(UserCenterActivity.this,
					RecommendActivity.class);
			startActivity(intent);
			break;
		// 我的收藏
		case R.id.btn_user_favorite:
			intent = new Intent(UserCenterActivity.this,
					CollectionActivity.class);
			startActivity(intent);
			break;
		// 我的菜谱
		case R.id.btn_user_menu:
			intent = new Intent(UserCenterActivity.this, CookBookActivity.class);
			startActivity(intent);
			break;
		// 单位会员激活
		case R.id.btn_active_unit_member:
			intent = new Intent(UserCenterActivity.this,
					CodeActiveActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_exit_account:
			List<Activity> list = application.getActivity();
			for (Activity activity : list) {
				if (activity != null) {
					activity.finish();
				}
			}
			application.setUserInfo(null);
			intent = new Intent(UserCenterActivity.this, FirstLoadActivity.class);
			startActivity(intent);
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (task != null) {
			task.cancel(true);
		}
		task = new GetUserInfoTask();
		task.execute();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (task != null) {
			task.cancel(true);
		}
	}
}
