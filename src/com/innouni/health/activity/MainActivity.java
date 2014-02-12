package com.innouni.health.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.innouni.health.adapter.AdvertisementAdapter;
import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseFragmentActivity;
import com.innouni.health.entity.Advertisement;
import com.innouni.health.entity.UserInfo;
import com.innouni.health.fragment.CaloryFragment;
import com.innouni.health.fragment.ChartFragment;
import com.innouni.health.net.HttpPostRequest;
import com.innouni.health.util.Util;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

/**
 * 主页
 * 
 * @author HuGuojun
 * @date 2014-1-22 下午3:44:59
 * @modify
 * @version 1.0.0
 */
public class MainActivity extends BaseFragmentActivity implements
		OnClickListener, OnPageChangeListener, OnTouchListener,
		OnCheckedChangeListener {

	private static final int LOOPTIME = 5 * 1000;

	private FrameLayout container;
	private RadioGroup groupShowType;

	private List<Advertisement> ads;
	private AdvertisementAdapter adapter;
	private PageIndicator indicator;
	private ViewPager viewPager;
	private TextView adTitleView;
	private float downX, upX, distanceX;
	private GetAdTask task;

	private Handler handler = new Handler();
	private boolean startLoop = false;
	private int currentIndex = 0;

	private PopupWindow popupWindow;
	private boolean isMeasured = false;
	private int offsetHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		application = MainApplication.getApplication();
		application.setActivity(this);
		application.setInActivity(true);
		initBar();
		initAd();
		initView();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (!isMeasured) {
			Rect frame = new Rect();
			getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
			int statusBarHeight = frame.top; // 获取状态栏高度
			RelativeLayout titleBar = (RelativeLayout) findViewById(R.id.lay_title_bar);
			int titleBarHeight = titleBar.getBottom(); // 获取titleBar高度
			offsetHeight = statusBarHeight + titleBarHeight;
			isMeasured = true;
		}
	}

	private void initBar() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleContentView.setText("掌握健康");
		titleLeftBtn.setOnClickListener(this);
		titleRightBtn.setOnClickListener(this);
	}

	private void initAd() {
		ads = new ArrayList<Advertisement>();
		adapter = new AdvertisementAdapter(this);
		adTitleView = (TextView) findViewById(R.id.txt_ad_title);
		viewPager = (ViewPager) findViewById(R.id.view_pager_main);
		viewPager.setAdapter(adapter);
		viewPager.setOnTouchListener(this);
		viewPager.setCurrentItem(0);
		indicator = (CirclePageIndicator) findViewById(R.id.page_indicator_main);
		indicator.setViewPager(viewPager);
		indicator.setOnPageChangeListener(this);
		((CirclePageIndicator) indicator).setSnap(true);
		if (task != null) {
			task.cancel(true);
		}
		task = new GetAdTask();
		task.execute();
	}

	private void initView() {
		container = (FrameLayout) findViewById(R.id.lay_fragment_container);
		if (container != null) {
			Fragment caloryFragment = new CaloryFragment();
			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			transaction.add(R.id.lay_fragment_container, caloryFragment);
			transaction.commit();
		}
		groupShowType = (RadioGroup) findViewById(R.id.group_energy_show_type);
		groupShowType.setOnCheckedChangeListener(this);
	}

	private Runnable looper = new Runnable() {
		@Override
		public void run() {
			if (ads != null && ads.size() > 0 && startLoop) {
				currentIndex++;
				if (currentIndex > (ads.size() - 1)) {
					currentIndex = 0;
				}
				viewPager.setCurrentItem(currentIndex);
				handler.postDelayed(looper, LOOPTIME);
			}
		}
	};

	private void showPopupWindow() {
		LinearLayout layout = (LinearLayout) LayoutInflater.from(
				MainActivity.this).inflate(R.layout.pop_main, null);
		popupWindow = new PopupWindow(layout, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, false);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.showAtLocation(findViewById(R.id.lay_title_bar),
				Gravity.RIGHT | Gravity.TOP, 0, offsetHeight);
		layout.findViewById(R.id.txt_main_user_center).setOnClickListener(this);
		layout.findViewById(R.id.txt_main_food_track).setOnClickListener(this);
		layout.findViewById(R.id.txt_main_sport_fri).setOnClickListener(this);
		layout.findViewById(R.id.txt_main_my_friend).setOnClickListener(this);
		layout.findViewById(R.id.txt_main_set).setOnClickListener(this);
		layout.findViewById(R.id.txt_main_health_center).setOnClickListener(
				this);

		WindowManager.LayoutParams attribute = getWindow().getAttributes();
		attribute.gravity = Gravity.TOP;
		attribute.alpha = 0.5f;
		getWindow().setAttributes(attribute);
		popupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				WindowManager.LayoutParams attribute = getWindow()
						.getAttributes();
				attribute.gravity = Gravity.TOP;
				attribute.alpha = 1.0f;
				getWindow().setAttributes(attribute);
			}
		});
	}

	private class GetAdTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			startLoop = false;
			if (ads != null && ads.size() > 0) {
				ads.clear();
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			UserInfo user = application.getUserInfo();
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("token", user.getToken()));
			pairs.add(new BasicNameValuePair("mId", user.getId()));
			String json = HttpPostRequest.getDataFromWebServer(
					MainActivity.this, "getPushFoods", pairs);
			System.out.println("首页广告图返回: " + json);
			try {
				JSONObject jsonObject = new JSONObject(json);
				int status = jsonObject.optInt("status");
				if (status == 0) {
					JSONArray array = jsonObject.optJSONArray("Foods");
					String screma = getResources().getString(R.string.app_url)
							+ "files/advmap/";
					for (int i = 0; i < array.length(); i++) {
						Advertisement advertisement = new Advertisement();
						JSONObject object = array.optJSONObject(i);
						advertisement.setId(object.optString("id"));
						advertisement.setTitle(object.optString("name"));
						advertisement.setImageUrl(screma
								+ object.optString("logo"));
						ads.add(advertisement);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			task = null;
			adapter.setData(ads);
			if (ads.size() > 0) {
				adTitleView.setText(ads.get(0).getTitle().toString());
			}
			if (!startLoop) {
				startLoop = true;
				handler.postDelayed(looper, LOOPTIME);
			}
		}

	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.btn_title_right:
			showPopupWindow();
			break;
		// 个人中心
		case R.id.txt_main_user_center:
			intent = new Intent(MainActivity.this, UserCenterActivity.class);
			startActivity(intent);
			popupWindow.dismiss();
			break;
		// 食物追踪
		case R.id.txt_main_food_track:
			intent = new Intent(MainActivity.this, FoodTrackActivity.class);
			startActivity(intent);
			popupWindow.dismiss();
			break;
		// 运动伙伴
		case R.id.txt_main_sport_fri:
			intent = new Intent(MainActivity.this, SportFriendActivity.class);
			startActivity(intent);
			popupWindow.dismiss();
			break;
		// 我的好友
		case R.id.txt_main_my_friend:
			break;
		// 健康中心
		case R.id.txt_main_health_center:
			intent = new Intent(MainActivity.this, HealthCenterActivity.class);
			startActivity(intent);
			popupWindow.dismiss();
			break;
		// 设置
		case R.id.txt_main_set:
			intent = new Intent(MainActivity.this, SettingActivity.class);
			startActivity(intent);
			popupWindow.dismiss();
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = event.getX();
			break;
		case MotionEvent.ACTION_UP:
			upX = event.getX();
			distanceX = Math.abs(upX - downX);
			// 当滑动的距离小于10时 执行跳转动作
			if (distanceX < 10) {
				Advertisement advertisement = ads.get(currentIndex);
				Intent intent = new Intent(MainActivity.this,
						DishDetailActivity.class);
				intent.putExtra("foodId", advertisement.getId().toString());
				startActivity(intent);
			}
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public void onPageScrollStateChanged(int position) {

	}

	@Override
	public void onPageScrolled(int position, float index, int id) {

	}

	@Override
	public void onPageSelected(int position) {
		currentIndex = position;
		if (ads != null && ads.size() > 0) {
			adTitleView.setText(ads.get(position).getTitle().toString());
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.radio_type_calory:
			if (container != null) {
				Fragment caloryFragment = new CaloryFragment();
				FragmentTransaction transaction = getSupportFragmentManager()
						.beginTransaction();
				transaction
						.replace(R.id.lay_fragment_container, caloryFragment);
				transaction.commit();
			}
			break;
		case R.id.radio_type_chart:
			if (container != null) {
				Fragment chartFragment = new ChartFragment();
				FragmentTransaction transaction = getSupportFragmentManager()
						.beginTransaction();
				transaction.replace(R.id.lay_fragment_container, chartFragment);
				transaction.commit();
			}
			break;
		}
	}

	private class GetUserInfoTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			UserInfo user = application.getUserInfo();
			String token = user.getToken();
			String id = user.getId();
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("token", token));
			pairs.add(new BasicNameValuePair("mId", id));
			String json = HttpPostRequest.getDataFromWebServer(
					MainActivity.this, "getMemberInfo", pairs);
			System.out.println("APP主页获取用户信息返回 " + json);
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

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (task != null) {
			task.cancel(true);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (application.getUserInfo() != null) {
			new GetUserInfoTask().execute();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (viewPager != null) {
			viewPager.removeAllViews();
			viewPager.destroyDrawingCache();
		}
		handler.removeCallbacks(looper);
	}
}
