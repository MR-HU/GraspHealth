package com.innouni.health.util;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

/**
 * {@code FileUtil}文件操作工具类<br>
 * <li></li>Purpose：规范文件操作，方便操作，该工具类可自由拓展其内部方法<br>
 * <li></li>Rules：使用该类之前必须获取该类实例 {@link #getInstance}<br>
 * <li></li>对外方法：
 * {@link #fileProber}
 * {@link #getExternalCacheDir}
 * {@link #deleteFileByName}
 * {@link #createNewFile}
 * {@link #isExistsFile}
 * {@link #findFileByName}
 * 
 * @author HuGuojun
 * @data 2013-09-05
 */
public class FileUtil {
	
	private static final long MB = 1024  * 1024;
	
	/**
	 *  缓存空间大小(默认30M)
	 */
	private static final long SDCARD_CACHE_SIZE = 30; 
	
	private final String  TAG = "file";
	
	static FileUtil fielUtil = new FileUtil();
	
	private static Context context;
	
	/**
	 * 实例化{@link FileUtil} 工具对象
	 * @return FileUtils
	 */
	public static FileUtil getInstance(Context mContext){
		context = mContext;
		return fielUtil == null ? new FileUtil() : fielUtil;
	}
	
	/**
	 * 文件探针 当父目录不存在时，创建目录！
	 * 
	 * @param dirFile
	 */
	public void fileProber(File dirFile) {
		File parentFile = dirFile.getParentFile();
		if (!parentFile.exists()) {
			// 递归寻找上级目录
			fileProber(parentFile);
			Log.v(TAG, "fileProber "+parentFile.getAbsolutePath());
			parentFile.mkdirs();
		}
	}
	
	/**
	 * 获得外部存储目录
	 * @param 
	 * @return File
	 */
	public File getExternalCacheDir() {
		File file = null;
		String cacheDir = "";
        if (Util.IsCanUseSdCard()) {
        	//使用SDCard
        	cacheDir = Environment.getExternalStorageDirectory().getPath()
        			+ "/Android/data/" + context.getPackageName() + "/cache/";
        }else{
        	//使用手机自带的存储
        	cacheDir =context.getCacheDir().getAbsolutePath();
        }
        file = new File(cacheDir);
        if(!file.exists()){
        	 fileProber(file);
        }
        Log.v(TAG, file.getAbsolutePath());
        return file;
	}
	
	
	/**
	 * 删除指定名称的文件
	 * @param fileName
	 * @return {@code true} 删除成功, {@code false} 失败.
	 */
	public boolean deleteFileByName(String fileName){
		File file = new File(getExternalCacheDir(), fileName);
		return file.delete();
	}
	
	/**
	 * 创建文件
     * 
	 * @param fileName
	 * @return {@code file} 成功返回新建的file, {@code null} 失败.
	 */
	public File createNewFile(String fileName){
		File file = null;
		try {
			file = new File(getExternalCacheDir(), fileName);
			if(file.exists()){
				deleteFileByName(fileName);
			}
			fileProber(file);
			file.createNewFile();
		} catch (Exception e) {
			Log.v(TAG, " Create File fileName fail "+e.toString());
		}
		return file;
	}
	
	/**
	 * 判断文件是否存在
     * 
	 * @param fileName
	 * @return {@code true} 存在, {@code false} 不存在.
	 */
	public boolean isExistsFile(String fileName){
		return new File(getExternalCacheDir().getAbsoluteFile()+"/"+fileName).exists();
	}
	
	/**
	 * 通过名字查找文件
     * 
	 * @param fileName
	 * @return {@code file} 文件存在, {@code null} 文件不存在.
	 */
	public File findFileByName(String fileName){
		return new File(getExternalCacheDir(), fileName);
	}
	
	/**
	 * 修改文件时间
	 * @param file
	 */
	public void updateFileTime(File file){     
		long newModifiedTime = System.currentTimeMillis();     
		file.setLastModified(newModifiedTime); 
	}
	
	/**
	 * 根据文件的最后修改时间进行排序  
	 **/ 
	private class FileLastModifSort implements Comparator<File>{     
		public int compare(File arg0, File arg1) {   
			if (arg0.lastModified() >arg1.lastModified()) {           
				return 1;        
			} 
			else if (arg0.lastModified() ==arg1.lastModified()) {
				return 0;   
			} 
			else {
				return -1;     
			}
		}
	}
	
	/**
	 * 计算存储目录下的文件大小，当文件总大小大于规定的SDCARD_CACHE_SIZE 
	 * 或者
	 * sdcard剩余空间小于SDCARD_CACHE_SIZE  那么删除40%最近没有被使用的文件  * 
	 */
	public void removeCache() {
		File fileDir = getExternalCacheDir(); 
		File[] files = fileDir.listFiles(); 
		if (files == null) {
			return;  
		} 
		long dirSize = 0;
		int len = files.length;
		for (int i = 0; i < len; i++){
			dirSize += files[i].length();
		}
		dirSize = dirSize/MB;
		//当内存卡空间不足或者缓存大小大于定义的大小时，删除数据
		if (dirSize >= SDCARD_CACHE_SIZE || SDCARD_CACHE_SIZE >= freeSpaceOnSd()) 
		{
			int removeFactor = (int) ((0.4 *files.length) + 1);         
			Arrays.sort(files, new FileLastModifSort());
			//排序后将是重小到达的排序
			for (int i = 0; i <removeFactor; i++){
				files[i].delete();
			}
		}
	}
	
	/**
	 * 计算sdcard上的剩余空间 
	 * return 返回 字节数
	**/ 
	private int freeSpaceOnSd() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		double sdFreeMB = ((double)stat.getAvailableBlocks() * (double) stat.getBlockSize()) / MB;
		return (int) sdFreeMB; 
	} 
	
}
