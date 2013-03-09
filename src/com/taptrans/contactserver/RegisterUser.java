package com.taptrans.contactserver;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.taptrans.util.AppConstants;
import com.taptrans.util.ServerUtil;

public class RegisterUser {
	
	public String phoneNo;
	public String ipAddr;
	public String gcmRegId;
	
	public RegisterUser(String phoneNumber, String ipAddress, String gcmRegistrationId) {
		phoneNo = phoneNumber;
		ipAddr = ipAddress;
		gcmRegId = gcmRegistrationId;
	}
	
	public void registerNow() {
		JSONObject obj = new JSONObject();
		try {
			obj.put(AppConstants.PHONE_NO, phoneNo);
			obj.put(AppConstants.IP_ADDR, ipAddr);
			obj.put(AppConstants.GCM_REG_ID, gcmRegId);
			ServerUtil.postRequest(AppConstants.SERVER_URL, obj);
		}catch(JSONException e) {
			Log.e("ERROR", "JSONException occurred", e);
		}
	}

}
