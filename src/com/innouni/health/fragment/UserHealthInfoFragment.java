package com.innouni.health.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.innouni.health.activity.HealthInfoActivity;
import com.innouni.health.activity.R;
import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseFragment;
import com.innouni.health.entity.UserInfo;
import com.innouni.health.net.HttpPostRequest;

/**
 * 获取用户健康信息,动态布局<br>
 * 健康信息页面使用该Fragment
 * 
 * @author HuGuojun
 * @date 2014-1-8 下午3:49:18
 * @modify
 * @version 1.0.0
 */
public class UserHealthInfoFragment extends BaseFragment {

	public static final int NEGATIVE_ID = 0;
	public static final int POSITIVE_ID = 1;

	private View view;
	private LinearLayout container;
	private ProgressDialog dialog;
	private GetHealthInfoTask task;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup contain,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_health_info, contain, false);
		container = (LinearLayout) view
				.findViewById(R.id.lay_health_info_container);
		if (task != null) {
			task.cancel(true);
		}
		task = new GetHealthInfoTask();
		task.execute();
		return view;
	}

	private class GetHealthInfoTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			showDialog();
		}

		@Override
		protected String doInBackground(Void... params) {
			application = MainApplication.getApplication();
			UserInfo user = application.getUserInfo();
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("token", user.getToken()));
			pairs.add(new BasicNameValuePair("mId", user.getId()));
			return HttpPostRequest.getDataFromWebServer(getActivity(),
					"getMemberHealth", pairs);
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println("获取用户健康信息: " + result);
			task = null;
			dialog.dismiss();
			if (result == null || result.equals("net_err")) {
				showToast(R.string.net_error);
			} else {
				try {
					JSONArray array = new JSONObject(result)
							.optJSONArray("Indicator");
					for (int i = 0; i < array.length(); i++) {
						JSONObject object = array.optJSONObject(i);
						String unit = object.optString("unitType"); // 单位
						if (unit.equals("0")) { // 单选题
							LinearLayout linear = new LinearLayout(
									getActivity());
							linear.setBackgroundResource(R.drawable.list_item_normal);
							linear.setTag(object.optString("id"));
							linear.setContentDescription(HealthInfoActivity.TYPE_RADIO);
							LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
									LayoutParams.MATCH_PARENT,
									LayoutParams.WRAP_CONTENT);
							params.setMargins(0, 10, 0, 0);
							linear.setLayoutParams(params);
							linear.setGravity(Gravity.CENTER_VERTICAL);

							TextView textView = new TextView(getActivity());
							LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
									0, LayoutParams.MATCH_PARENT, 1);
							param.setMargins(0, 0, 6, 0);
							textView.setLayoutParams(param);
							textView.setGravity(Gravity.CENTER_VERTICAL);
							textView.setText(object.optString("name"));
							textView.setTextColor(Color.parseColor("#999999"));
							textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
							linear.addView(textView);

							RadioGroup group = new RadioGroup(getActivity());
							LinearLayout.LayoutParams parame = new LinearLayout.LayoutParams(
									0, LayoutParams.MATCH_PARENT, 2);
							group.setLayoutParams(parame);
							group.setOrientation(LinearLayout.HORIZONTAL);

							final RadioButton negativeButton = new RadioButton(
									getActivity());
							negativeButton.setId(NEGATIVE_ID);
							negativeButton.setText("否");
							negativeButton.setTextColor(Color
									.parseColor("#999999"));
							negativeButton.setTextSize(
									TypedValue.COMPLEX_UNIT_SP, 15);
							negativeButton
									.setButtonDrawable(R.drawable.radio_btn_selector);
							LinearLayout.LayoutParams paramete = new LinearLayout.LayoutParams(
									80, LayoutParams.MATCH_PARENT);
							negativeButton.setLayoutParams(paramete);

							final RadioButton positiveButton = new RadioButton(
									getActivity());
							positiveButton.setId(POSITIVE_ID);
							positiveButton.setText("是");
							positiveButton.setTextColor(Color
									.parseColor("#999999"));
							positiveButton.setTextSize(
									TypedValue.COMPLEX_UNIT_SP, 15);
							positiveButton
									.setButtonDrawable(R.drawable.radio_btn_selector);
							if (object.optString("value")
									.equalsIgnoreCase("no")) {
								negativeButton.setChecked(true);
							} else {
								positiveButton.setChecked(true);
							}
							group.addView(negativeButton);
							group.addView(positiveButton);
							group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

								@Override
								public void onCheckedChanged(RadioGroup group,
										int checkedId) {
									switch (checkedId) {
									case NEGATIVE_ID:
										negativeButton.setChecked(true);
										positiveButton.setChecked(false);
										break;
									case POSITIVE_ID:
										negativeButton.setChecked(false);
										positiveButton.setChecked(true);
										break;
									}
								}
							});
							linear.addView(group);
							container.addView(linear);
						} else {
							LinearLayout linear = new LinearLayout(
									getActivity());
							linear.setTag(object.optString("id"));
							linear.setContentDescription(HealthInfoActivity.TYPE_EDIT);
							linear.setBackgroundResource(R.drawable.list_item_normal);
							LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
									LayoutParams.MATCH_PARENT,
									LayoutParams.WRAP_CONTENT);
							params.setMargins(0, 10, 0, 0);
							linear.setLayoutParams(params);
							linear.setGravity(Gravity.CENTER);

							TextView textView = new TextView(getActivity());
							LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
									0, LayoutParams.MATCH_PARENT, 1);
							param.setMargins(0, 0, 6, 0);
							textView.setLayoutParams(param);
							textView.setGravity(Gravity.CENTER_VERTICAL);
							textView.setText(object.optString("name"));
							textView.setTextColor(Color.parseColor("#999999"));
							textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
							linear.addView(textView);

							EditText editText = new EditText(getActivity());
							LinearLayout.LayoutParams parame = new LinearLayout.LayoutParams(
									0, LayoutParams.MATCH_PARENT, 1);
							editText.setLayoutParams(parame);
							editText.setBackgroundColor(Color.TRANSPARENT);
							editText.setGravity(Gravity.CENTER_VERTICAL);
							editText.setTextColor(Color.BLACK);
							editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
							editText.setText(object.optString("value").replace("null", ""));
							linear.addView(editText);

							TextView unitView = new TextView(getActivity());
							LinearLayout.LayoutParams paramt = new LinearLayout.LayoutParams(
									0, LayoutParams.MATCH_PARENT, 1);
							paramt.setMargins(5, 0, 0, 0);
							unitView.setLayoutParams(paramt);
							unitView.setGravity(Gravity.CENTER);
							unitView.setText(unit);
							unitView.setTextColor(Color.parseColor("#999999"));
							unitView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
							linear.addView(unitView);
							container.addView(linear);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					showToast(R.string.net_error);
				}
			}
		}

	}

	private void showDialog() {
		dialog = new ProgressDialog(getActivity());
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setTitle(getResources().getString(R.string.net_data));
		dialog.setMessage(getResources().getString(R.string.net_loading));
		dialog.setIndeterminate(false);
		dialog.setCancelable(true);
		dialog.show();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (task != null) {
			task.cancel(true);
		}
	}
}
