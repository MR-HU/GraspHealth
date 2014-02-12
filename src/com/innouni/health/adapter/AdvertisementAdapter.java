package com.innouni.health.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.innouni.health.activity.R;
import com.innouni.health.entity.Advertisement;
import com.innouni.health.net.ExecutorsImageLoader;
import com.innouni.health.net.ExecutorsImageLoader.ImageCallback;

/**
 * 主页广告图适配器
 * 
 * @author HuGuojun
 * @date 2014-1-14 下午4:09:44
 * @modify
 * @version 1.0.0
 */
public class AdvertisementAdapter extends PagerAdapter {

	private LayoutInflater inflater;
	private ExecutorsImageLoader exeLoader;
	private List<Advertisement> ads;

	public AdvertisementAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		exeLoader = ExecutorsImageLoader.getInstance(context);
		ads = new ArrayList<Advertisement>();
	}

	public void setData(List<Advertisement> data) {
		if (ads.size() > 0) {
			ads.clear();
		}
		ads.addAll(data);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return ads.size();
	}

	@Override
	public Object instantiateItem(View collection, int position) {
		ImageView view = (ImageView) (inflater.inflate(
				R.layout.item_viewpage_main, null));
		Advertisement ad = ads.get(position);
		String url = ad.getImageUrl().toString();
		loadRemoteImage(url, view);
		((ViewPager) collection).addView(view);
		return view;
	}

	private void loadRemoteImage(String url, final ImageView imageView) {
		imageView.setImageResource(R.drawable.ic_launcher);
		Bitmap cacheImage = exeLoader.loadDrawable(url, new ImageCallback() {
			@Override
			public void imageLoaded(Bitmap imageDrawable) {
				if (null != imageDrawable) {
					imageView.setImageBitmap(imageDrawable);
				} else {
					imageView.setImageResource(R.drawable.ic_launcher);
				}
			}
		});
		if (cacheImage != null) {
			imageView.setImageBitmap(cacheImage);
		} else {
			imageView.setImageResource(R.drawable.ic_launcher);
		}
	}

	@Override
	public void destroyItem(View collection, int position, Object view) {
		((ViewPager) collection).removeView((View) view);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public void startUpdate(View arg0) {
	}

	@Override
	public void finishUpdate(View arg0) {
	}
}
