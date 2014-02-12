package com.innouni.health.net;

import java.io.DataInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.innouni.health.activity.R;
import com.innouni.health.net.ExecutorsImageLoader.ImageCallback;
import com.innouni.health.util.MapUtil;

/**
 * 图片下载
 * 
 * @author HuGuojun
 * @date 2014-1-8 上午9:57:58
 * @modify
 * @version 1.0.0
 */
public class DownloadImage {

	/**
	 * 根据路径下载图片
	 * 
	 * @param imageUrl
	 * @return Bitmap
	 */
	public static Bitmap loadImage(String imageUrl) {
		byte[] buffer = null;
		Bitmap bit = null;
		try {
			URL url = new URL(imageUrl);
			HttpURLConnection connectoin = (HttpURLConnection) url
					.openConnection();
			DataInputStream dis = new DataInputStream(
					connectoin.getInputStream());
			if (connectoin.getContentLength() > 0) {
				buffer = new byte[connectoin.getContentLength()];
				dis.readFully(buffer);
			}
			dis.close();
			connectoin.disconnect();
			if (null != buffer && buffer.length > 0) {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.RGB_565;
				options.inPurgeable = true;
				options.inInputShareable = true;
				bit = BitmapFactory.decodeByteArray(buffer, 0, buffer.length,
						options);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bit;
	}

	/**
	 * 下载圆形头像
	 * 
	 * @description loadCircleImage
	 * @param context
	 * @param url
	 * @param imageView
	 */
	public static void loadCircleImage(Context context, String url,
			final ImageView imageView) {
		ExecutorsImageLoader exeLoader = ExecutorsImageLoader
				.getInstance(context);
		// 默认图片
		final Bitmap defaultBitmap = MapUtil.getCircleBitmap(BitmapFactory
				.decodeResource(context.getResources(), R.drawable.bg));
		imageView.setImageBitmap(defaultBitmap);
		Bitmap cacheImage = exeLoader.loadDrawable(url, new ImageCallback() {
			@Override
			public void imageLoaded(Bitmap imageDrawable) {
				if (imageDrawable != null) {
					imageView.setImageBitmap(MapUtil
							.getCircleBitmap(imageDrawable));
					defaultBitmap.recycle();
				} else {
					imageView.setImageBitmap(defaultBitmap);
				}
			}
		});
		if (cacheImage != null) {
			imageView.setImageBitmap(MapUtil.getCircleBitmap(cacheImage));
			defaultBitmap.recycle();
		} else {
			imageView.setImageBitmap(defaultBitmap);
		}
	}

	/**
	 * 下载普通矩形头像
	 * 
	 * @description loadCircleImage
	 * @param context
	 * @param url
	 * @param imageView
	 */
	public static void loadRectangeImage(Context context, String url,
			final ImageView imageView) {
		ExecutorsImageLoader exeLoader = ExecutorsImageLoader
				.getInstance(context);
		// 默认图片
		final Bitmap defaultBitmap = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.default_avatar);
		imageView.setImageBitmap(defaultBitmap);
		Bitmap cacheImage = exeLoader.loadDrawable(url, new ImageCallback() {
			@Override
			public void imageLoaded(Bitmap imageDrawable) {
				if (imageDrawable != null) {
					imageView.setImageBitmap(imageDrawable);
					defaultBitmap.recycle();
				} else {
					imageView.setImageBitmap(defaultBitmap);
				}
			}
		});
		if (cacheImage != null) {
			imageView.setImageBitmap(cacheImage);
			defaultBitmap.recycle();
		} else {
			imageView.setImageBitmap(defaultBitmap);
		}
	}
}
