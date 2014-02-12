package com.innouni.health.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.innouni.health.activity.R;
import com.innouni.health.app.MainApplication;
import com.innouni.health.entity.UserInfo;
import com.innouni.health.entity.VersionInfo;
import com.innouni.health.net.HttpPostRequest;
import com.innouni.health.widget.MsgBox;

/**
 * 版本更新功能工具类
 * 
 * @author HuGuojun
 * @date 2014-2-10 下午2:52:07
 * @modify
 * @version 1.0.0
 */
public class UpdateVersionUtil {

	private static Context context;
	private Resources resources;

	private ProgressDialog progress;
	private ProgressDialog progressBar;

	private boolean showLoading = false;

	static UpdateVersionUtil updateUtil = new UpdateVersionUtil();

	public static UpdateVersionUtil getInstance(Context mContext) {
		context = mContext;
		return updateUtil == null ? new UpdateVersionUtil() : updateUtil;
	}

	public void setShowLoading(boolean showLoading) {
		this.showLoading = showLoading;
	}

	public void startCheckVersion() {
		resources = context.getResources();
		progress = new ProgressDialog(context);
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progress.setMessage(resources.getString(R.string.ver_checking));

		progressBar = new ProgressDialog(context);
		progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressBar.setTitle(R.string.app_name);
		progressBar.setMessage(resources
				.getString(R.string.ver_apk_downloading));
		progressBar.setMax(100);

		new UpdateTask().execute();
	}

	private class UpdateTask extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (showLoading) {
				progress.show();
			}
		}

		@Override
		protected String doInBackground(String... params) {
			UserInfo user = MainApplication.getApplication().getUserInfo();
			if (user != null) {
				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("mId", user.getId()));
				pairs.add(new BasicNameValuePair("token", user.getToken()));
				pairs.add(new BasicNameValuePair("versionCode", getVersion()
						+ ""));
				return HttpPostRequest.getDataFromWebServer(context, "", pairs);
			} else {
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			System.out.println("获取更新返回: " + result);
			if (progress.isShowing()) {
				progress.hide();
			}
			if (result != null) {
				VersionInfo info = parseVersion(result);
				if (null == info || "".equals(String.valueOf(info.getVersionNum()))) {
					if (showLoading) {
						// 提示检查更新异常,请稍后重试
						dialogAlter(R.string.ver_update_exception);
					}
				} else {
					int updateVer = info.getVersionNum();
					if (updateVer <= getVersion()) {
						if (showLoading) {
							// 提示当前已经是最新版本，无需更新
							dialogAlter(R.string.no_new_version);
						}
					} else {
						// 有新版本,询问是否更新操作
						updateVer(info);
					}
				}
			} else {
				// 提示检查更新异常,请稍后重试
				if (showLoading) {
					// 提示检查更新异常,请稍后重试
					dialogAlter(R.string.ver_update_exception);
				}
			}
		}
	}

	/**
	 * 更新版本
	 * 
	 * @param info
	 */
	private void updateVer(final VersionInfo info) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.app_update_dialog_layout, null);
		TextView contentView = (TextView) view
				.findViewById(R.id.text_ver_content);
		contentView.setText(info.getContent());

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view).setTitle(R.string.ver_update);
		builder.setPositiveButton(R.string.ver_update_now,
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 开启下载APK任务
						new DownApkTask(info).execute();
						dialog.dismiss();
					}
				});
		builder.setNegativeButton(R.string.ver_update_next,
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.show();
	}

	private class DownApkTask extends AsyncTask<String, Integer, String> {

		private int allLength = 0;
		private File file = null;
		private VersionInfo verInfo;

		public DownApkTask(VersionInfo info) {
			this.verInfo = info;
			resources = context.getResources();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar.show();
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				String savePath = Environment.getExternalStorageDirectory()
						.getPath() + "/Android/data/";
				File tmpFile = new File(savePath);
				if (!tmpFile.exists()) {
					tmpFile.mkdir();
				}
				file = new File(tmpFile, resources.getString(R.string.app_name)
						+ verInfo.getVersionNum() + ".apk");
				if (file.exists()) {
					file.delete();
				}
				file.createNewFile();
				if (verInfo.getApkUrl().endsWith("apk")) {
					URL url = new URL(verInfo.getApkUrl());
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					allLength = conn.getContentLength();
					InputStream inputStream = conn.getInputStream();
					conn.connect();
					if (null != inputStream) {
						if (conn.getResponseCode() >= 400) {
							Toast.makeText(context, R.string.net_time_out,
									Toast.LENGTH_LONG).show();
						} else {
							int currentLen = 0;
							byte[] buf = new byte[inputStream.available()];
							FileOutputStream fos = new FileOutputStream(file);
							DataOutputStream dataOutputStream = new DataOutputStream(
									fos);
							int len = 0;
							while ((len = inputStream.read(buf)) != -1) {
								if (len <= 0) {
									break;
								} else {
									dataOutputStream.write(buf, 0, len);
									dataOutputStream.flush();
									currentLen += len;
									publishProgress((int) ((currentLen * 100) / ((float) allLength)));
								}
							}
							fos.close();
							inputStream.close();
						}
					}
					conn.disconnect();
				}
			} catch (Exception e) {
				if (file.exists()) {
					file.delete();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			progressBar.setProgress(0);
			progressBar.hide();
			if (null != file && file.exists()) {
				openAPKFile(file);
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			progressBar.setProgress(values[0]);
		}
	}

	/**
	 * 对话框提示
	 * 
	 * @param resId
	 */
	private void dialogAlter(int resId) {
		MsgBox.alert(resources.getString(resId), context);
	}

	/**
	 * 获取版本code
	 * 
	 * @return 当前应用的版本号
	 */
	private int getVersion() {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			return info.versionCode;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 解析新版本数据
	 * 
	 * @description parseVersion
	 * @param json
	 * @return VersionInfo
	 */
	private VersionInfo parseVersion(String json) {
		VersionInfo version = null;
		try {
			JSONObject object = new JSONObject(json);
			if (object.optInt("status") == 0) {
				version = new VersionInfo();
				version.setApkUrl(resources.getString(R.string.app_url)
						+ "files/apk/" + object.optString("apk_path"));
				version.setContent("");
				version.setName(object.optString("apk_versionName"));
				version.setVersionNum(Integer.valueOf(object.optString("apk_versionCode")));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return version;
	}

	/**
	 * 打开APK进行安装
	 * 
	 * @param file
	 * @param version
	 */
	private void openAPKFile(File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}
}
