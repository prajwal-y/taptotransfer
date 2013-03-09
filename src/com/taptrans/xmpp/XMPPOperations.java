package com.taptrans.xmpp;

import java.util.HashMap;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Registration;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.taptrans.util.AppConstants;
import com.taptrans.util.ShowNotifications;

public class XMPPOperations {
	
	private static String TAG = "XMPPOperations";
	Registration regUser = null;
	AccountManager accountMgr = null;
	
	public void registerUserAccount(HashMap<String, String> attributes) {
		regUser = new Registration();
		regUser.setType(IQ.Type.SET);
		regUser.setAttributes(attributes);
		new RegisterUser().execute(attributes);
	}

	private class RegisterUser extends AsyncTask<HashMap<String, String>, Void, Void> {

		@Override
		protected Void doInBackground(HashMap<String, String>... params) {
			try {
				Connection conn = XMPPUtil.createConnection();
				conn.connect();
				accountMgr = new AccountManager(conn);
				HashMap<String, String> attributes = params[0];
				Log.i(TAG, "Creating a new xmpp account with username: " + attributes.get(AppConstants.USERNAME) + "and password: "+ attributes.get(AppConstants.PASSWORD));
				accountMgr.createAccount(attributes.get(AppConstants.USERNAME),
						attributes.get(AppConstants.PASSWORD), attributes);
				conn.sendPacket(regUser);
			} catch (XMPPException e) {
				Log.e(TAG, "XMPPException: ", e);
				ShowNotifications.notifyUser("Registration", "Account creation failed. Try Again.", new Intent());
			}
			Log.i(TAG, "Account Succesfully created!");
			ShowNotifications.notifyUser("Registration", "Account successfully created", new Intent());
			return null;
		}
	}
}
