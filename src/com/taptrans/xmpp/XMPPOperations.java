package com.taptrans.xmpp;

import java.util.HashMap;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Registration;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.taptrans.util.AppConstants;
import com.taptrans.util.AppData;
import com.taptrans.util.ShowNotifications;

public class XMPPOperations {

	private static String TAG = "XMPPOperations";
	Registration regUser = null;
	AccountManager accountMgr = null;
	
	@SuppressWarnings("unchecked")
	public void registerUserAccount(HashMap<String, String> attributes) {
		regUser = new Registration();
		regUser.setType(IQ.Type.SET);
		regUser.setAttributes(attributes);
		new RegisterUser().execute(attributes);
	}

	public void unregisterUser() {
		new UnRegisterUser().execute();
	}
	
	public void sendMessage() {
		new Message().execute();
	}
	private class RegisterUser extends
			AsyncTask<HashMap<String, String>, Void, Void> {
		//Background operation for creating device account.
		@Override
		protected Void doInBackground(HashMap<String, String>... params) {
			try {
				Connection conn = XMPPUtil.getConnection();
				if (!conn.isConnected())
					conn.connect();
				accountMgr = new AccountManager(conn);
				HashMap<String, String> attributes = params[0];
				Log.i(TAG,
						"Creating a new xmpp account with username: "
								+ attributes.get(AppConstants.USERNAME)
								+ "and password: "
								+ attributes.get(AppConstants.PASSWORD));
				accountMgr.createAccount(attributes.get(AppConstants.USERNAME),
						attributes.get(AppConstants.PASSWORD), attributes);
				conn.sendPacket(regUser);
			} catch (XMPPException e) {
				Log.e(TAG, "XMPPException: ", e);
				ShowNotifications.notifyUser("Registration",
						"Account creation failed. Try Again.", new Intent());
				return null;
			}
			Log.i(TAG, "Account Succesfully created!");
			ShowNotifications.notifyUser("Registration",
					"Account successfully created", new Intent());
			return null;
		}
	}

	public class UnRegisterUser extends AsyncTask<Void, Void, Void> {
		//Background operation for deleting device account.
		@Override
		protected Void doInBackground(Void... arg0) {
			Connection conn = XMPPUtil.getConnection();
			try {
				if(!conn.isConnected())
					conn.connect();
				if(!conn.isAuthenticated())
					conn.login(AppData.username, AppData.IMEI);
				conn.getAccountManager().deleteAccount();
			} catch (XMPPException e) {
				Log.e(TAG, "XMPPException: ", e);
				ShowNotifications.notifyUser("Unregistration",
						"Account deletion failed. Try Again.", new Intent());
				return null;
			}
			Log.i(TAG, "Account Succesfully deleted!");
			ShowNotifications.notifyUser("UnRegistration",
					"Account successfully deleted", new Intent());
			return null;
		}

	}
	
	public class Message extends AsyncTask<Void, Void, Void>{
		//Send message to device
		@Override
		protected Void doInBackground(Void... arg0) {
			Connection conn = XMPPUtil.getConnection();
			try {
				if(!conn.isConnected())
					conn.connect();
				if(!conn.isAuthenticated())
					conn.login(AppData.username, AppData.IMEI);
				ChatManager chatmanager = conn.getChatManager();
				Chat newChat = chatmanager.createChat("9008416496@107.20.254.21", new MessageListener() {
				    
					@Override
					public void processMessage(Chat arg0,
							org.jivesoftware.smack.packet.Message arg1) {
						Log.i(TAG, "Message received!");
						ShowNotifications.notifyUser("Message",
								"Message received:" + arg1, new Intent());
						
					}
				});

				try {
				    newChat.sendMessage("Howdy!");
				}
				catch (XMPPException e) {
					Log.e(TAG, "XMPPException: ", e);
					ShowNotifications.notifyUser("Message",
							"Message sending failed. Try Again.", new Intent());
				}
			} catch (XMPPException e) {
				Log.e(TAG, "XMPPException: ", e);
				ShowNotifications.notifyUser("Message",
						"Message sending failed. Try Again.", new Intent());
				return null;
			}
			Log.i(TAG, "Message Succesfully sent!");
			ShowNotifications.notifyUser("Message",
					"Message successfully sent", new Intent());
			return null;
		
	}
	}
	
	}

