package com.taptrans.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.util.Log;

public class ServerUtil {

	private static HttpParams httpParams = null;
	private static HttpClient httpClient = null;
	private static HttpPost httpPost = null;
	private static HttpResponse httpResponse = null;

	private static void prepareHTTPParams() {
		httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams,
				AppConstants.TIMEOUT_MILLISEC);
		HttpConnectionParams.setSoTimeout(httpParams,
				AppConstants.TIMEOUT_MILLISEC);
		httpClient = new DefaultHttpClient(httpParams);
	}

	public static void postRequest(String serverUrl, JSONObject obj) {
		prepareHTTPParams();
		try {
			httpPost = new HttpPost(serverUrl);
			StringEntity se = new StringEntity(obj.toString());
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
					AppConstants.APP_JSON));
			httpPost.setEntity(se);
			httpResponse = httpClient.execute(httpPost);
			Log.i("Response during POST request", httpResponse.toString());
		} catch (Exception e) {
			Log.e("Exception occurred", e.getStackTrace().toString());
		}

	}
}
