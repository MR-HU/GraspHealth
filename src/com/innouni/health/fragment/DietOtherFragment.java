package com.innouni.health.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.innouni.health.activity.R;
import com.innouni.health.adapter.DietAdapter;
import com.innouni.health.app.MainApplication;
import com.innouni.health.base.BaseFragment;
import com.innouni.health.entity.Diet;
import com.innouni.health.entity.UserInfo;
import com.innouni.health.net.HttpPostRequest;

/**
 * 饮食列表中的其他
 * 
 * @author HuGuojun
 * @date 2014-1-13 下午4:10:10
 * @modify
 * @version 1.0.0
 */
public class DietOtherFragment extends BaseFragment {

	private static final int DELETE_ITEM = 1;
	
	private ListView listView;
	private DietAdapter adapter;
	private DeleteTask deleteTask;
	private ProgressDialog dialog;
	private int index = -1; // 被删除的item的索引

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_diet, container, false);
		listView = (ListView) view.findViewById(R.id.listview_diet);
		adapter = new DietAdapter(getActivity());
		listView.setAdapter(adapter);
		listView.setOnCreateContextMenuListener(this);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Bundle bundle = getArguments();
		String json = bundle.getString("diet_info");
		System.out.println("饮食信息传到Fragment: " + json);
		try {
			List<Object> list = new ArrayList<Object>();
			JSONArray array = new JSONObject(json)
					.optJSONArray("other");
			if (array != null) {
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = array.optJSONObject(i);
					Diet diet = new Diet();
					diet.setId(object.opt("id"));
					diet.setFoodId(object.opt("foodId"));
					diet.setFoodName(object.opt("foodName"));
					diet.setFoodAmount(object.opt("foodAmount"));
					diet.setFoodUnitType(object.opt("foodUnitType"));
					diet.setCalorieIn(object.opt("calorieIn"));
					diet.setAddTime(object.opt("addTime"));
					list.add(diet);
				}
				adapter.setList(list, true);
				setListViewHeightBasedOnChildren(listView);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 固定Listview的高度,解决与ScrollView的冲突
	 * @description setListViewHeightBasedOnChildren
	 * @param listView 
	 */
	public void setListViewHeightBasedOnChildren(ListView listView) {  
	    ListAdapter listAdapter = listView.getAdapter();   
	    if (listAdapter == null) {  
	        return;  
	    }  

	    int totalHeight = 0;  
	    for (int i = 0; i < listAdapter.getCount(); i++) {  
	        View listItem = listAdapter.getView(i, null, listView);  
	        listItem.measure(0, 0);  
	        totalHeight += listItem.getMeasuredHeight();  
	    }  

	    ViewGroup.LayoutParams params = listView.getLayoutParams();  
	    params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));  
	    listView.setLayoutParams(params);  
	} 
	
	private void showDialog() {
		dialog = new ProgressDialog(getActivity());
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setMessage(getResources().getString(R.string.net_deleting));
		dialog.setIndeterminate(false);
		dialog.setCancelable(true);
		dialog.show();
	}
	
	private class DeleteTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			showDialog();
		}

		@Override
		protected String doInBackground(String... params) {
			application = MainApplication.getApplication();
			UserInfo user = application.getUserInfo();
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("token", user.getToken()));
			pairs.add(new BasicNameValuePair("mId", user.getId()));
			pairs.add(new BasicNameValuePair("id", params[0]));
			String json = HttpPostRequest.getDataFromWebServer(getActivity(),
					"delFoodLog", pairs);
			System.out.println("删除饮食列表item : " + json);
			return json;
		}

		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			deleteTask = null;
			try {
				int status = new JSONObject(result).optInt("status");
				if (status == 0) {
					showToast(R.string.net_delete_success);
					adapter.deleteItem(index);
					setListViewHeightBasedOnChildren(listView);
				} else {
					showToast(R.string.net_delete_fail);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				showToast(R.string.net_error);
			}
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.add(3, DELETE_ITEM, 0, "删除");
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getGroupId() == 3) {
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			index = (int) adapter.getItemId(info.position);
			Diet diet = (Diet) adapter.getItem(index);
			String dietId = diet.getId().toString();
			switch (item.getItemId()) {
			case DELETE_ITEM:
				if (deleteTask != null) {
					deleteTask.cancel(true);
				}
				deleteTask = new DeleteTask();
				deleteTask.execute(dietId);
				break;
			}
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (deleteTask != null) {
			deleteTask.cancel(true);
		}
	}
}
