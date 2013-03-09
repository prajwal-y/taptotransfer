package com.taptrans.xmpp;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.taptrans.util.AppData;
import com.taptrans.util.ShowNotifications;

public class XMPPService extends Service {

	private static Connection m_XMPPConnection = null;
	private static String TAG = "XMPPService";

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		m_XMPPConnection = XMPPUtil.getConnection();
		new backgroundOperation().execute();
	}

	private class MyMessageListener implements MessageListener {
		@Override
		public void processMessage(final Chat chat, final Message message) {
			Log.i(TAG, "Xmpp message received: '" + message.getBody());
			ShowNotifications.notifyUser("Message received:",
					message.getBody(), new Intent());
		}
	}

	@Override
	public void onDestroy() {
		m_XMPPConnection.disconnect();
		ShowNotifications.notifyUser("XMPP",
				"Device logged out of the XMPP server", new Intent());
	}

	private class backgroundOperation extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				if (!m_XMPPConnection.isConnected())
					m_XMPPConnection.connect();
				SASLAuthentication.supportSASLMechanism("PLAIN", 0);
				m_XMPPConnection.login(AppData.username, AppData.IMEI);
			} catch (XMPPException ex) {
				Log.e(TAG, "XMPPException occurred: ", ex);
				ShowNotifications.notifyUser("XMPP",
						"Device could not connect to the XMPP server",
						new Intent());
				return null;
			}
			if (!m_XMPPConnection.isConnected()) {
				Log.e(TAG,
						"Could not connect to the XMPP server. Please check server logs");
				ShowNotifications.notifyUser("XMPP",
						"Device could not connect to the XMPP server",
						new Intent());
				return null;
			}

			Log.i(TAG, "User connected to the XMPP server");
			ShowNotifications.notifyUser("XMPP",
					"Device logged on to the XMPP server", new Intent());

			m_XMPPConnection.getChatManager().addChatListener(
					new ChatManagerListener() {
						@Override
						public void chatCreated(final Chat chat,
								final boolean createdLocally) {
							if (!createdLocally) {
								chat.addMessageListener(new MyMessageListener());
							}
						}
					});
			return null;
		}

	}
}
