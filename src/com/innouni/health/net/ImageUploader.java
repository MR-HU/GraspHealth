package com.innouni.health.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.HttpStatus;

/**
 * 文件上传
 * 
 * @author HuGuojun
 * @date 2014-1-9 下午4:19:19
 * @modify
 * @version 1.0.0
 */
public class ImageUploader {

	private static final int DEFAULT_BUFF_SIZE = 1024 * 8;
	private static final int READ_TIMEOUT = 15 * 1000;
	private static final int CONNECTION_TIMEOUT = 15 * 1000;

	/**
	 * 上传文件
	 * 
	 * @description uploadFile
	 * @param map 参数
	 * @param serverUrl 地址
	 * @param file 文件
	 * @return
	 * @throws MalformedURLException
	 * @throws ProtocolException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @return String
	 */
	public static String uploadFile(HashMap<String, String> map, String serverUrl,
			File file) throws MalformedURLException, ProtocolException,
			FileNotFoundException, IOException {
		String boundary = "------------innouni"; // 分隔符
		String endLine = "\r\n"; // 换行符
		String underLine = "--";

		URL url = new URL(serverUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// 设置每次传输的流大小，可以有效防止手机因为内存不足崩溃
		// 此方法用于在预先不知道内容长度时启用没有进行内部缓冲的 HTTP 请求正文的流。
		conn.setChunkedStreamingMode(128 * 1024);
		// 允许输入输出流
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		// 超时时间设置
		conn.setReadTimeout(READ_TIMEOUT);
		conn.setConnectTimeout(CONNECTION_TIMEOUT);
		// 使用POST方法
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Connection", "Keep-alive");
		conn.setRequestProperty("Charset", "UTF-8");
		conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="
				+ boundary);

		DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
		// 发送参数
		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			String value = map.get(key);
			dos.writeBytes(underLine + boundary + endLine);
			dos.writeBytes("Content-Disposition: form-data; name=\"" + key
					+ "\"" + endLine);
			dos.writeBytes(endLine);
			dos.writeBytes(value);
			dos.writeBytes(endLine);
		}
		// 发送参数(key=filekey, value=uploadfile)
		dos.writeBytes(underLine + boundary + endLine);
		dos.writeBytes("Content-Disposition: form-data; name=\"filekey\""
				+ endLine);
		dos.writeBytes(endLine);
		dos.writeBytes("uploadfile");
		dos.writeBytes(endLine);
		// 发送文件
		dos.writeBytes(underLine + boundary + endLine);
		dos.writeBytes("Content-Disposition: form-data; name=\"uploadfile\"; filename=\""
				+ file.getName() + "\"" + endLine);
		dos.writeBytes(endLine);

		FileInputStream fis = new FileInputStream(file.getPath());
		byte[] buffer = new byte[DEFAULT_BUFF_SIZE];
		int count = 0;
		while ((count = fis.read(buffer)) != -1) {
			dos.write(buffer, 0, count);
		}
		fis.close();
		dos.writeBytes(endLine);
		// 最后多出"--"作为结束
		dos.writeBytes(underLine + boundary + underLine + endLine);
		dos.flush();

		// 从服务器读取返回数据
		if (conn.getResponseCode() == HttpStatus.SC_OK) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()), 8 * 1024);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			return sb.toString();
		}
		if (conn != null) {
			conn.disconnect();
		}
		return null;
	}

}
