package com.taptrans.contactserver;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.taptrans.util.AppConstants;
import com.taptrans.util.ServerUtil;

public class RegisterUser {
	
	public String phoneNo;
	public String ipAddr;
	
	public RegisterUser(String phoneNumber, String ipAddress) {
		phoneNo = phoneNumber;
		ipAddr = ipAddress;
	}
	
	public void registerNow() {
		JSONObject obj = new JSONObject();
		try {
			obj.put(AppConstants.PHONE_NO, phoneNo);
			obj.put(AppConstants.IP_ADDR, ipAddr);
			ServerUtil.postRequest(AppConstants.SERVER_URL, obj);
		}catch(JSONException e) {
			Log.e("JSONException occurred", e.getStackTrace().toString());
		}
	}

}
