package com.taptrans.taptotransfer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.taptrans.util.AppConstants;
import com.taptrans.util.AppData;
import com.taptrans.util.ShowNotifications;

public class GCMIntentService extends GCMBaseIntentService {

	@Override
	protected void onError(Context context, String error) {
		Log.e("ERROR: ", "Error occurred: "+error);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i("INFO: ", "This is what I got: "+intent.getStringExtra("message"));
		ShowNotifications.notifyUser("GCM", intent.getStringExtra("message"), new Intent());
	}

	@Override
	protected void onRegistered(Context context, String regId) {
		AppData.gcmRegId = regId;
		SharedPreferences sharedPref = AppData.activity.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(AppConstants.GCM_REG_ID, AppData.gcmRegId);
		editor.commit();
		Log.i("INFO: ", "Device has been registered with GCM ID: "+AppData.gcmRegId);
	}

	@Override
	protected void onUnregistered(Context context, String regId) {
		AppData.gcmRegId = null;
		SharedPreferences sharedPref = AppData.activity.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(AppConstants.GCM_REG_ID, "");
		editor.commit();
		Log.i("INFO: ", "Device has been unregistered with GCM ID: "+AppData.gcmRegId);
	}


}
