package com.innouni.health.app;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

import com.innouni.health.entity.UserInfo;
import com.innouni.health.net.ImagesCache;

/**
 * 管理应用程序
 * 
 * @author HuGuojun
 * @date 2013-12-20 下午1:56:56
 * @modify
 * @version 1.0.0
 */
public class MainApplication extends Application {

	//用于记录食物追踪中的饮食列表是否被修改(增加 删除)
	public boolean isDietChanged = false;
	
	private static MainApplication application;

	public static MainApplication getApplication() {
		return application;
	}

	private boolean inActivity = false;

	public boolean isInActivity() {
		return inActivity;
	}

	public void setInActivity(boolean inActivity) {
		this.inActivity = inActivity;
	}

	private ImagesCache imagesCache;

	public ImagesCache getImagesCache() {
		return imagesCache;
	}

	public void setImagesCache(ImagesCache imagesCache) {
		this.imagesCache = imagesCache;
	}

	private List<Activity> activitys = new ArrayList<Activity>();

	public void setActivity(Activity activity) {
		activitys.add(activity);
	}

	public List<Activity> getActivity() {
		return activitys;
	}

	private UserInfo userInfo;

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		application = this;
		imagesCache = new ImagesCache();
		initUserInfo();
	}

	public void exitApp() {
		for (Activity activity : activitys) {
			if (activity != null) {
				activity.finish();
			}
		}
	}

	private void initUserInfo() {
		UserInfo info = getUserInfo();
		if (info == null) {
			info = new UserInfo();
			setUserInfo(info);
		}
	}

	//添加饮食时记录添加步骤
	private List<Activity> dieActivities = new ArrayList<Activity>();

	public void setDietActivity(Activity activity) {
		dieActivities.add(activity);
	}

	public List<Activity> getDietActivity() {
		return dieActivities;
	}
	
}
