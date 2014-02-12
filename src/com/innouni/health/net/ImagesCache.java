package com.innouni.health.net;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import android.graphics.Bitmap;

/**
 * 图片缓存类
 * 
 * @author HuGuojun
 * @date 2014-1-2 下午5:15:42
 * @modify
 * @version 1.0.0
 */
public class ImagesCache extends HashMap<String, WeakReference<Bitmap>> {

	private static final long serialVersionUID = 1L;

	public boolean isCached(String cacheKey) {
		WeakReference<Bitmap> weakReference = get(cacheKey);
		return this.containsKey(cacheKey) && weakReference != null
				&& weakReference.get() != null;
	}

}