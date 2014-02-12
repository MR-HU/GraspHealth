package com.innouni.health.activity;

import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseFragmentActivity;
import com.innouni.health.fragment.DishFragment;
import com.innouni.health.fragment.SportFragment;

/**
 * 我的收藏页面
 * 
 * @author HuGuojun
 * @date 2013-12-30 上午11:27:00
 * @modify
 * @version 1.0.0
 */
public class CollectionActivity extends BaseFragmentActivity implements
		OnClickListener {

	private FragmentTabHost tabHost;
	@SuppressWarnings("rawtypes")
	private final Class[] fragments = { DishFragment.class, SportFragment.class };
	private PopupWindow popupWindow;
	private int offsetHeight;
	private boolean isMeasured = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collection);
		application = MainApplication.getApplication();
		application.setActivity(this);
		initBar();
		initView();
	}

	private void initBar() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleContentView.setText("我的收藏");
		titleLeftBtn.setOnClickListener(this);
		titleRightBtn.setOnClickListener(this);
	}

	private void initView() {
		tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		tabHost.setup(this, getSupportFragmentManager(),
				R.id.lay_fragment_container);
		int count = fragments.length;
		for (int i = 0; i < count; i++) {
			TabSpec tabSpec = tabHost.newTabSpec(i + "").setIndicator(i + "");
			tabHost.addTab(tabSpec, fragments[i], null);
		}
		tabHost.setCurrentTab(0);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.btn_title_right:
			showPopupWindow();
			break;
		case R.id.txt_collection_dish:
			if (popupWindow != null && popupWindow.isShowing()) {
				popupWindow.dismiss();
				tabHost.setCurrentTab(0);
			}
			break;
		case R.id.txt_collection_spot:
			if (popupWindow != null && popupWindow.isShowing()) {
				popupWindow.dismiss();
				tabHost.setCurrentTab(1);
			}
			break;
		}
	}

	private void showPopupWindow() {
		LinearLayout layout = (LinearLayout) LayoutInflater.from(
				CollectionActivity.this).inflate(R.layout.pop_collection, null);
		popupWindow = new PopupWindow(layout, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, false);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.showAtLocation(findViewById(R.id.lay_title_bar),
				Gravity.RIGHT | Gravity.TOP, 0, offsetHeight);
		layout.findViewById(R.id.txt_collection_dish).setOnClickListener(this);
		layout.findViewById(R.id.txt_collection_spot).setOnClickListener(this);

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
}
