package com.taptrans.util;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;
public class ServerUtil {

	public static HttpParams httpParams = null;
	public static HttpClient httpClient = null;

	public static void prepareHTTPParams() {
		httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams,
				AppConstants.TIMEOUT_MILLISEC);
		HttpConnectionParams.setSoTimeout(httpParams,
				AppConstants.TIMEOUT_MILLISEC);
		httpClient = new DefaultHttpClient(httpParams);
	}

	public static void postRequest(String serverUrl, JSONObject obj) {
		new PostHttp().execute(serverUrl, obj.toString());
	}

}
