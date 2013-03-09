package com.taptrans.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;

public class PostHttp extends AsyncTask<String, Void, Void> {

	@Override
	protected Void doInBackground(String... params) {
		if(ServerUtil.httpParams == null)
			ServerUtil.prepareHTTPParams();
		HttpPost httpPost = null;
		HttpResponse httpResponse = null;
		try {
			httpPost = new HttpPost(params[0]);
			StringEntity se = new StringEntity(params[1]);
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
					AppConstants.APP_JSON));
			httpPost.setEntity(se);
			httpResponse = ServerUtil.httpClient.execute(httpPost);
			Log.i("Response during POST request", EntityUtils.toString(httpResponse.getEntity()));
		} catch (Exception e) {
			Log.e("ERROR", "Exception occurred", e);
		}
		return null;
	}
}
