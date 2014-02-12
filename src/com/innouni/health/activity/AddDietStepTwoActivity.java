package com.innouni.health.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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
import android.widget.TextView;

import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseActivity;
import com.innouni.health.util.Util;
import com.zijunlin.Zxing.Demo.CaptureActivity;

/**
 * 饮食添加第二步<br>
 * 选择食物源类型(餐厅 食堂 包装)
 * 
 * @author HuGuojun
 * @date 2014-1-14 下午3:24:03
 * @modify
 * @version 1.0.0
 */
public class AddDietStepTwoActivity extends BaseActivity implements
		OnClickListener {

	private String type; // 企业类型(1 包装 2 餐厅 3 食堂 4 我的菜谱)
	private String mealsType; // 饮食类型(1 早餐 2 中餐 3 晚餐 4 其他)
	private TextView typeView;
	private PopupWindow popupWindow;
	private boolean isMeasured = false;
	private int offsetHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_diet_two);
		application = MainApplication.getApplication();
		application.setActivity(this);
		application.setDietActivity(this);
		mealsType = getIntent().getStringExtra("mealsType");
		initView();
	}

	private void initView() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleContentView.setText("食物源类型选择");
		titleLeftBtn.setOnClickListener(this);
		titleRightBtn.setOnClickListener(this);

		typeView = (TextView) findViewById(R.id.txt_enter_type);
		typeView.setOnClickListener(this);
		findViewById(R.id.btn_next).setOnClickListener(this);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (!isMeasured) {
			Rect frame = new Rect();
			getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
			int statusBarHeight = frame.top;
			RelativeLayout titleBar = (RelativeLayout) findViewById(R.id.lay_title_bar);
			int titleBarHeight = titleBar.getBottom();
			offsetHeight = statusBarHeight + titleBarHeight
					+ typeView.getBottom();
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
			Intent intent2 = new Intent(this, CaptureActivity.class);
			intent2.putExtra("mealsType", mealsType);
			startActivity(intent2);
			break;
		case R.id.txt_enter_type:
			showPopWindow();
			break;
		case R.id.txt_package:
			popupWindow.dismiss();
			typeView.setText("包装");
			type = "1";
			break;
		case R.id.txt_restaurant:
			popupWindow.dismiss();
			typeView.setText("餐厅");
			type = "2";
			break;
		case R.id.txt_canteen:
			popupWindow.dismiss();
			typeView.setText("食堂");
			type = "3";
			break;
		case R.id.txt_cookbook:
			popupWindow.dismiss();
			typeView.setText("我的菜谱");
			type = "4";
			break;
		case R.id.btn_next:
			if (!Util.isEmpty(type)) {
				if (!type.equals("4")) {
					Intent intent = new Intent(this, AddDietStepThreeActivity.class);
					intent.putExtra("mealsType", mealsType);
					intent.putExtra("entryType", type);
					startActivity(intent);
				} else {
					Intent intent = new Intent(this, AddDietStepFourActivity.class);
					intent.putExtra("mealsType", mealsType);
					intent.putExtra("entryType", type);
					startActivity(intent);
				}
			}
			break;
		}
	}

	private void showPopWindow() {
		LinearLayout layout = (LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.pop_entry_type, null);
		popupWindow = new PopupWindow(layout, getWindowManager()
				.getDefaultDisplay().getWidth() - Util.dip2px(this, 30),
				LayoutParams.WRAP_CONTENT, false);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
		popupWindow.showAtLocation(typeView, Gravity.CENTER_HORIZONTAL
				| Gravity.TOP, 0, offsetHeight);
		layout.findViewById(R.id.txt_package).setOnClickListener(this);
		layout.findViewById(R.id.txt_restaurant).setOnClickListener(this);
		layout.findViewById(R.id.txt_canteen).setOnClickListener(this);
		layout.findViewById(R.id.txt_cookbook).setOnClickListener(this);

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
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(application.getDietActivity().contains(this)){
			application.getDietActivity().remove(this);
		}
	}
}
