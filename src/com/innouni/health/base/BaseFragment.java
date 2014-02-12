package com.innouni.health.base;

import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.innouni.health.app.MainApplication;

/**
 * Fragment的基类
 * 
 * @author HuGuojun
 * @date 2013-12-30 上午11:55:49
 * @modify
 * @version 1.0.0
 */
public class BaseFragment extends Fragment {
	
	protected MainApplication application;

	protected void showToast(int resourceId) {
		Toast.makeText(getActivity(), resourceId, Toast.LENGTH_LONG).show();
	}

	protected void showToast(String text) {
		Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
	}

	protected void showToastShort(int resourceId) {
		Toast.makeText(getActivity(), resourceId, Toast.LENGTH_SHORT).show();
	}

	protected void showToastShort(String text) {
		Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
	}
}
