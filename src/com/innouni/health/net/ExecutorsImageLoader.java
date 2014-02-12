package com.innouni.health.net;

import java.io.DataInputStream;
import java.io.File;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import com.innouni.health.app.MainApplication;
import com.innouni.health.util.FileUtil;
import com.innouni.health.util.Util;

/**
 * 图片下载 缓存 本地存储
 * 
 * @author HuGuojun
 * @date 2014-1-2 下午5:05:31
 * @modify
 * @version 1.0.0
 */
public class ExecutorsImageLoader {

	private ExecutorService executorService = Executors.newFixedThreadPool(5);

	private static MainApplication application;

	private static Context context;

	private static ImagesCache imageCache;

	private FileUtil fileUtil;

	private static ExecutorsImageLoader executorsLoader = new ExecutorsImageLoader();

	private Handler handler = new Handler();

	/** 保存已经访问过的图片路径 */
	public ArrayList<String> cacheUrl = new ArrayList<String>();

	public static ExecutorsImageLoader getInstance(Context mContext) {
		context = mContext;
		application = MainApplication.getApplication();
		imageCache = application.getImagesCache();
		return executorsLoader == null ? new ExecutorsImageLoader()
				: executorsLoader;
	}

	/**
	 * 根据网络 Url 加载图片数据
	 * 
	 * @param imageUrl
	 *            图片的网络地址
	 * @param callback
	 * @param downData
	 * @return
	 */
	public Bitmap loadDrawable(final String imageUrl,
			final ImageCallback callback) {
		final String cacheName = Util.convertStr(imageUrl);
		fileUtil = FileUtil.getInstance(context);
		if (loadCache(cacheName) == null) {
			executorService.submit(new Runnable() {
				public void run() {
					final Bitmap map = downImageFromUrl(imageUrl);
					handler.post(new Runnable() {
						@Override
						public void run() {
							callback.imageLoaded(map);
						}
					});
				}
			});
		}
		return imageCache.isCached(cacheName) ? imageCache.get(cacheName).get()
				: null;
	}

	/**
	 * 下载网络图片
	 * 
	 * @param imageUrl
	 *            网络图片图片地址
	 * @param isCacheFile
	 *            是否设为本地缓存文件
	 * @return 成功返回网络图片, 无则返回null
	 */
	private Bitmap downImageFromUrl(String imageUrl) {
		if (cacheUrl.contains(imageUrl)) {
			return loadCache(Util.convertStr(imageUrl));
		} else {
			Log.e("cache", "imageUrl= " + imageUrl);
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
					options.inPreferredConfig = Bitmap.Config.RGB_565; // 设置内存中的编码方式
					// inPurgeable设为true表示使用BitmapFactory创建的Bitmap用于存储Pixel的内存空间在系统内存不足时可以被回收
					options.inPurgeable = true;
					// 与inPurgeable 一起使用
					options.inInputShareable = true;
					try {
						BitmapFactory.Options.class.getField("inNativeAlloc")
								.setBoolean(options, true);
					} catch (Exception e) {
					}
					bit = BitmapFactory.decodeByteArray(buffer, 0,
							buffer.length, options);
					imageCache.put(Util.convertStr(imageUrl),
							new WeakReference<Bitmap>(bit));
					// 生成名称为XXX.CACHEIMG的文件用于保存下载的图片
					File downFile = fileUtil.createNewFile(Util
							.convertStr(imageUrl));
					ExecutorSaveCacheServer.getInstance().savaCacheFile(buffer,
							downFile);
				}
				cacheUrl.add(imageUrl);
			} catch (Exception e) {
				Log.i("cache", "downImageFromUrl  error" + e.toString());
			}
			return bit;
		}
	}

	/**
	 * 加载本地缓存数据
	 * 
	 * @param cacheName
	 *            图片的缓存名称
	 * @return 有本地缓存文件则返回缓存图片, 无则返回null
	 */
	private Bitmap loadCache(String cacheName) {
		// 如果缓存过就从缓存中取出数据
		if (imageCache.isCached(cacheName)) {
			WeakReference<Bitmap> softReference = imageCache.get(cacheName);
			return softReference.get();
		} else {
			boolean isExistsCache = fileUtil.isExistsFile(cacheName);
			Log.e("cache", ">>>>>>>>>>>>>>>>>>>>" + isExistsCache);
			if (isExistsCache) {
				String filePath = fileUtil.findFileByName(cacheName)
						.getAbsolutePath();
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(filePath, options);
				int scale = Math.min((int) (options.outHeight / (float) 80),
						(int) (options.outWidth / (float) 60));
				Log.v("file", "org---" + scale);
				if (scale > 2) {
					scale = 3;
				}
				Log.v("file", "inSampleSize---" + scale);
				options.inSampleSize = scale;
				options.inJustDecodeBounds = false;
				imageCache.put(cacheName, new WeakReference<Bitmap>(
						BitmapFactory.decodeFile(filePath, options)));
				return BitmapFactory.decodeFile(filePath, options);
			} else {
				return null;
			}
		}
	}

	// 对外界开放的回调接口
	public interface ImageCallback {
		// 此方法是用来设置目标对象的图像资源
		public void imageLoaded(Bitmap imageDrawable);
	}

}