package com.innouni.health.net;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;

import com.innouni.health.activity.R;

/**
 * HTTP POST请求
 * 
 * @author HuGuojun
 * @date 2013-12-24 上午11:29:14
 * @modify
 * @version 1.0.0
 */
public class HttpPostRequest {

	public static HttpPost getHttpPost(String url) {
		HttpPost request = new HttpPost(url);
		return request;
	}

	public static HttpResponse getHttpResponse(HttpPost request)
			throws ClientProtocolException, IOException {
		HttpResponse response = new DefaultHttpClient().execute(request);
		return response;
	}

	public static String getDataFromWebServer(Context context, String action,
			List<NameValuePair> params) {
		String url = context.getString(R.string.app_url) + "?c=app&a=" + action;
		HttpPost request = HttpPostRequest.getHttpPost(url);
		String result = null;
		try {
			if (null != params) {
				request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			}
			HttpResponse response = HttpPostRequest.getHttpResponse(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity());
			}
		} catch (ClientProtocolException e) {
			result = "net_err";
		} catch (IOException e) {
			result = "net_err";
		} catch (Exception e) {
			result = "net_err";
		}
		return result;
	}
}
