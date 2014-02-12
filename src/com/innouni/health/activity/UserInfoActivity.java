package com.innouni.health.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import net.tsz.afinal.FinalBitmap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseActivity;
import com.innouni.health.entity.UserInfo;
import com.innouni.health.net.ImageUploader;
import com.innouni.health.util.CalendaUtil;
import com.innouni.health.util.Util;

/**
 * 个人信息页面
 * 
 * @author HuGuojun
 * @date 2013-12-25 上午11:21:58
 * @modify
 * @version 1.0.0
 */
public class UserInfoActivity extends BaseActivity implements OnClickListener {

	private static int RESULT_LOAD_IMAGE = 1; // 从图库加载图片后的返回码
	private static int RESULT_TAKE_PHOTO = 2; // 调用相机拍照后的返回码

	private UserInfo user;
	private ImageView avatarView;
	private TextView nickView, emailView, phoneView, genderView;
	private TextView locationView, signView, birthView, heightView, weightView;
	private PopupWindow popupWindow;

	private FinalBitmap fb;
	private File imageFile; // 上传到服务器的图片文件

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);
		application = MainApplication.getApplication();
		application.setActivity(this);
		fb = FinalBitmap.create(this);
		user = application.getUserInfo();
		initBar();
		initView();
		registerEvent();
	}

	private void initBar() {
		titleLeftBtn = (TextView) findViewById(R.id.btn_title_left);
		titleContentView = (TextView) findViewById(R.id.txt_title_center);
		titleRightBtn = (TextView) findViewById(R.id.btn_title_right);
		titleLeftBtn.setOnClickListener(this);
		titleContentView.setText("个人信息");
		titleRightBtn.setVisibility(View.GONE);
	}

	private void initView() {
		avatarView = (ImageView) findViewById(R.id.image_user_avatar_info);
		nickView = (TextView) findViewById(R.id.txt_user_nick);
		emailView = (TextView) findViewById(R.id.txt_user_email);
		phoneView = (TextView) findViewById(R.id.txt_user_phone);
		genderView = (TextView) findViewById(R.id.txt_user_gender);
		locationView = (TextView) findViewById(R.id.txt_user_location);
		signView = (TextView) findViewById(R.id.txt_user_sign);
		birthView = (TextView) findViewById(R.id.txt_user_birthday);
		heightView = (TextView) findViewById(R.id.txt_user_height);
		weightView = (TextView) findViewById(R.id.txt_user_weight);

		if (user != null) {
			fb.display(avatarView, user.getAvatar());
//			nickView.setText(user.getName());
//			emailView.setText(user.getEmail());
//			phoneView.setText(user.getPhone());
//			genderView.setText(user.getGender() == 0 ? "男" : "女");
//			locationView.setText(user.getProvince() + user.getCity());
//			signView.setText(user.getSign());
//			birthView.setText(CalendaUtil.getFormatCnTime(user.getBirthday()));
//			heightView.setText(user.getHeight() + "cm");
//			weightView.setText(user.getWeight() + "kg");
		}
	}

	private void registerEvent() {
		findViewById(R.id.lay_user_avatar).setOnClickListener(this);
		findViewById(R.id.lay_user_nick).setOnClickListener(this);
		findViewById(R.id.lay_user_email).setOnClickListener(this);
		findViewById(R.id.lay_user_phone).setOnClickListener(this);
		findViewById(R.id.lay_user_pwd).setOnClickListener(this);
		findViewById(R.id.lay_user_gender).setOnClickListener(this);
		findViewById(R.id.lay_user_location).setOnClickListener(this);
		findViewById(R.id.lay_user_sign).setOnClickListener(this);
		findViewById(R.id.lay_user_birthday).setOnClickListener(this);
		findViewById(R.id.lay_user_height).setOnClickListener(this);
		findViewById(R.id.lay_user_weight).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.lay_user_avatar:
			showPopWindow();
			break;
		case R.id.lay_user_nick:
			intent = new Intent(UserInfoActivity.this, ModifyNickActivity.class);
			startActivity(intent);
			break;
		case R.id.lay_user_email:
			intent = new Intent(UserInfoActivity.this,
					ModifyEmailActivity.class);
			startActivity(intent);
			break;
		case R.id.lay_user_phone:
			intent = new Intent(UserInfoActivity.this,
					ModifyPhoneActivity.class);
			startActivity(intent);
			break;
		case R.id.lay_user_pwd:
			intent = new Intent(UserInfoActivity.this, ModifyPwdActivity.class);
			startActivity(intent);
			break;
		case R.id.lay_user_gender:
			intent = new Intent(UserInfoActivity.this,
					ModifyGenderActivity.class);
			startActivity(intent);
			break;
		case R.id.lay_user_location:
			intent = new Intent(UserInfoActivity.this,
					ModifyLocationActivity.class);
			startActivity(intent);
			break;
		case R.id.lay_user_sign:
			intent = new Intent(UserInfoActivity.this, ModifySignActivity.class);
			startActivity(intent);
			break;
		case R.id.lay_user_birthday:
			intent = new Intent(UserInfoActivity.this,
					ModifyBirthActivity.class);
			startActivity(intent);
			break;
		case R.id.lay_user_height:
			intent = new Intent(UserInfoActivity.this,
					ModifyHeightActivity.class);
			startActivity(intent);
			break;
		case R.id.lay_user_weight:
			intent = new Intent(UserInfoActivity.this,
					ModifyWeightActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_take_photo:
			popupWindow.dismiss();
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, RESULT_TAKE_PHOTO);
			break;
		case R.id.btn_choose_image:
			popupWindow.dismiss();
			intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, RESULT_LOAD_IMAGE);
			break;
		case R.id.btn_load_cancel:
			popupWindow.dismiss();
			break;
		}
	}

	private void showPopWindow() {
		LinearLayout layout = (LinearLayout) LayoutInflater.from(
				UserInfoActivity.this).inflate(R.layout.pop_take_photo, null);
		popupWindow = new PopupWindow(layout, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, false);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.showAtLocation(findViewById(R.id.lay_title_bar),
				Gravity.RIGHT | Gravity.BOTTOM, 0, 0);
		layout.findViewById(R.id.btn_take_photo).setOnClickListener(this);
		layout.findViewById(R.id.btn_choose_image).setOnClickListener(this);
		layout.findViewById(R.id.btn_load_cancel).setOnClickListener(this);

		WindowManager.LayoutParams attribute = getWindow().getAttributes();
		attribute.gravity = Gravity.TOP;
		attribute.alpha = 0.5f;
		getWindow().setAttributes(attribute);
		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				WindowManager.LayoutParams attribute = getWindow()
						.getAttributes();
				attribute.gravity = Gravity.TOP;
				attribute.alpha = 1.0f;
				getWindow().setAttributes(attribute);
			}
		});

	}

	@SuppressWarnings("static-access")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && data != null) {
			if (requestCode == RESULT_LOAD_IMAGE) {
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(data.getData(),
						filePathColumn, null, null, null);
				cursor.moveToFirst();
				String imagePath = cursor.getString(cursor
						.getColumnIndex(filePathColumn[0]));
				cursor.close();
				imageFile = new File(imagePath);
				fb.display(avatarView, imagePath);
			} else if (requestCode == RESULT_TAKE_PHOTO) {
				if (Util.IsCanUseSdCard()) {
					Bitmap bitmap = (Bitmap) data.getExtras().get("data");
					// 在SD卡根目录下创建文件夹health用于保存照片
					File file = new File(Environment
							.getExternalStorageDirectory().toString(), "health");
					if (!file.exists())
						file.mkdirs();
					String fileName = new DateFormat().format(
							"yyyyMMdd_hhmmss",
							Calendar.getInstance(Locale.CHINA))
							+ ".jpg";
					String imagePath = file.getAbsolutePath() + "/" + fileName;
					FileOutputStream fout = null;
					try {
						fout = new FileOutputStream(imagePath);
						bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
						bitmap.recycle();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} finally {
						try {
							fout.flush();
							fout.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					imageFile = new File(imagePath);
					fb.display(avatarView, imagePath);
				} else {
					showToast("SD卡不可用");
					return;
				}
			}
			UploadImageTask task = new UploadImageTask();
			task.execute();
		}
	}

	private class UploadImageTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... param) {
			if (imageFile != null) {
				try {
					String url = getResources().getString(R.string.app_url)
							+ "?c=app&a=upMemberLogo";
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("token", user.getToken());
					params.put("mId", user.getId());
					params.put("mOLogo", user.getAvatar());
					String json = ImageUploader.uploadFile(params, url,
							imageFile);
					System.out.println("上传头像返回 : " + json);
					return new JSONObject(json).optInt("status");
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (ProtocolException e) {
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Integer status) {
			if (status == null) {
				showToast(R.string.net_error);
			} else if (status == 0) {
				showToast(R.string.load_image_success);
			} else {
				showToast(R.string.load_image_fail);
			}
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (user != null) {
			nickView.setText(user.getName());
			emailView.setText(user.getEmail());
			phoneView.setText(user.getPhone());
			genderView.setText(user.getGender() == 0 ? "男" : "女");
			locationView.setText(user.getProvince() + user.getCity());
			signView.setText(user.getSign());
			birthView.setText(CalendaUtil.getFormatCnTime(user.getBirthday()));
			heightView.setText(user.getHeight() + "cm");
			weightView.setText(user.getWeight() + "kg");
		}
	}

}
