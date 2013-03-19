package com.taptrans.xmpp;

import java.io.File;
import java.util.HashMap;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

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
	
	@SuppressWarnings("unchecked")
	public void transferFile(HashMap<String, String> attributes) {
		new SendFile().execute(attributes);
	}

	private class RegisterUser extends
			AsyncTask<HashMap<String, String>, Void, Void> {
		// Background operation for creating device account.
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

	private class UnRegisterUser extends AsyncTask<Void, Void, Void> {
		// Background operation for deleting device account.
		@Override
		protected Void doInBackground(Void... arg0) {
			Connection conn = XMPPUtil.getConnection();
			try {
				if (!conn.isConnected())
					conn.connect();
				if (!conn.isAuthenticated())
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

	private class Message extends AsyncTask<Void, Void, Void> {
		// Send message to device
		@Override
		protected Void doInBackground(Void... arg0) {
			Connection conn = XMPPUtil.getConnection();
			try {
				if (!conn.isConnected())
					conn.connect();
				if (!conn.isAuthenticated())
					conn.login(AppData.username, AppData.IMEI);
				ChatManager chatManager = conn.getChatManager();
				Chat newChat = chatManager.createChat(
						"9481603262@ec2taptotransfer", new MessageListener() {
							@Override
							public void processMessage(Chat arg0,
									org.jivesoftware.smack.packet.Message arg1) {
								Log.i(TAG, "Message received!");
								ShowNotifications.notifyUser("Message",
										"Message received:" + arg1,
										new Intent());
							}
						});
				newChat.sendMessage("Howdy from Prajwal!");
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

	private class SendFile extends AsyncTask<HashMap<String, String>, Void, Void> {
		@Override
		protected Void doInBackground(HashMap<String, String>... params) {
			HashMap<String, String> attributes = params[0];
			Connection conn = XMPPUtil.getConnection();
			File file = new File(attributes.get(AppConstants.FILENAME));
			String receiver = attributes.get(AppConstants.RECEIPIENT);
			OutgoingFileTransfer transfer = null;
			Intent notifyIntent = new Intent();
			try {
				if (!conn.isConnected())
					conn.connect();
				if (!conn.isAuthenticated())
					conn.login(AppData.username, AppData.IMEI);
				FileTransferManager manager = new FileTransferManager(conn);
				transfer = manager
						.createOutgoingFileTransfer(receiver+"/Smack");
				transfer.sendFile(file, "test_file");
			} catch (XMPPException e) {
				Log.e(TAG, "XMPPException: ", e);
				ShowNotifications.notifyUser("ERROR",
						"XMPPException occurred: ", new Intent());
				ShowNotifications.notifyUser("XMPP File transfer",
						"File transfer status: FAILED", notifyIntent);
				return null;
			}
			while (!transfer.isDone()) {
				if (transfer
						.getStatus()
						.equals(org.jivesoftware.smackx.filetransfer.FileTransfer.Status.error)) {
					Log.e(TAG, "ERROR" + transfer.getError());
					ShowNotifications.notifyUser("XMPP File transfer",
							"File transfer status: FAILED", notifyIntent);
				} else if (transfer
						.getStatus()
						.equals(org.jivesoftware.smackx.filetransfer.FileTransfer.Status.cancelled)
						|| transfer
								.getStatus()
								.equals(org.jivesoftware.smackx.filetransfer.FileTransfer.Status.refused)) {
					Log.e(TAG, "Cancelled!!! " + transfer.getError());
					ShowNotifications.notifyUser("XMPP File transfer",
							"File transfer status: CANCELLED", notifyIntent);
				}
				try {
					Thread.sleep(1000L);
					Log.i(TAG, "Sending file");
					// ShowNotifications.notifyUser("XMPP File transfer",
					// "File transfer status: SENDING", notifyIntent);
				} catch (InterruptedException e) {
					Log.e(TAG, "InterruptedException occurred: ", e);
				}
			}
			if (transfer
					.getStatus()
					.equals(org.jivesoftware.smackx.filetransfer.FileTransfer.Status.refused)
					|| transfer
							.getStatus()
							.equals(org.jivesoftware.smackx.filetransfer.FileTransfer.Status.error)
					|| transfer
							.getStatus()
							.equals(org.jivesoftware.smackx.filetransfer.FileTransfer.Status.cancelled)) {
				Log.e(TAG, "Refused cancelled error " + transfer.getError());
				ShowNotifications.notifyUser("XMPP File transfer",
						"File transfer status: FAILED", notifyIntent);
			} else {
				Log.i(TAG, "Successfully transferred");
				ShowNotifications.notifyUser("XMPP File transfer",
						"File transfer status: SUCCESSFUL", notifyIntent);
			}
			return null;
		}
	}

}
