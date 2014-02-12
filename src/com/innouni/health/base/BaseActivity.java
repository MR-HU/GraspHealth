package com.innouni.health.base;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.innouni.health.app.MainApplication;

/**
 * Activity的基类
 * 
 * @author HuGuojun
 * @date 2013-12-20 下午2:03:43
 * @modify
 * @version 1.0.0
 */
public class BaseActivity extends Activity {

	protected TextView titleLeftBtn;
	protected TextView titleRightBtn;
	protected TextView titleContentView;
	protected ProgressBar titleRefreshBar;

	protected MainApplication application;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	protected void showToast(int resourceId) {
		Toast.makeText(this, resourceId, Toast.LENGTH_LONG).show();
	}

	protected void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}

	protected void showToastShort(int resourceId) {
		Toast.makeText(this, resourceId, Toast.LENGTH_SHORT).show();
	}

	protected void showToastShort(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}
