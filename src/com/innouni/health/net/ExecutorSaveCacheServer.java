package com.innouni.health.net;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 文件保存
 * 
 * @author HuGuojun
 * @date 2014-1-2 下午4:56:45
 * @modify
 * @version 1.0.0
 */
public class ExecutorSaveCacheServer {

	private ExecutorService cacheService = Executors.newSingleThreadExecutor();

	private static ExecutorSaveCacheServer cacheServer = new ExecutorSaveCacheServer();

	public static ExecutorSaveCacheServer getInstance() {
		return cacheServer == null ? new ExecutorSaveCacheServer()
				: cacheServer;
	}

	public synchronized void savaCacheFile(final byte[] buffer,
			final File downFile) {
		cacheService.submit(new Runnable() {
			@Override
			public void run() {
				try {
					FileOutputStream fos = new FileOutputStream(downFile);
					fos.write(buffer);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
