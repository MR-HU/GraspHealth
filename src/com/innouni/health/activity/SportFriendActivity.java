package com.innouni.health.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseFragmentActivity;
import com.innouni.health.fragment.CommomFragment;
import com.innouni.health.fragment.SportFriendFragment;
import com.innouni.health.fragment.SuggestFragment;

/**
 * 运动伙伴页面
 * 
 * @author HuGuojun
 * @date 2014-1-16 下午4:40:45
 * @modify
 * @version 1.0.0
 */
public class SportFriendActivity extends BaseFragmentActivity implements
		OnClickListener, OnCheckedChangeListener {

	private FragmentTabHost tabHost;
	@SuppressWarnings("rawtypes")
	private final Class[] fragments = { SportFriendFragment.class,
			SuggestFragment.class, CommomFragment.class };
	private RadioGroup group;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sport_friend);
		application = MainApplication.getApplication();
		application.setActivity(this);
		initBar();
		initView();
	}

	private void initBar() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleContentView.setText("运动伙伴");
		titleLeftBtn.setOnClickListener(this);
		titleRightBtn.setVisibility(View.GONE);
	}

	private void initView() {
		group = (RadioGroup) findViewById(R.id.group_sport);
		group.setOnCheckedChangeListener(this);
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.btn_title_right:
			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.radio_sport:
			tabHost.setCurrentTab(0);
			titleRightBtn.setVisibility(View.GONE);
			break;
		case R.id.radio_suggest:
			tabHost.setCurrentTab(1);
			titleRightBtn.setVisibility(View.GONE);
			break;
		case R.id.radio_set:
			tabHost.setCurrentTab(2);
			titleRightBtn.setVisibility(View.GONE);
			break;
		}
	}
}
