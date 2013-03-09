package com.taptrans.xmpp;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPException;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.taptrans.util.ShowNotifications;


public class XMPPService extends Service {
	
	private Connection m_XMPPConnection = null;
	private static String TAG = "XMPPService";
	private String username = null;
	private String password = null;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		m_XMPPConnection = XMPPUtil.createConnection();
		try {
			m_XMPPConnection.connect();
			m_XMPPConnection.login(username, password);
		} catch (XMPPException ex) {
			Log.e(TAG, "XMPPException occurred: ", ex);
		}
		
		if(!m_XMPPConnection.isConnected()) {
			Log.e(TAG, "Could not connect to the XMPP server. Please check server logs");
			ShowNotifications.notifyUser("XMPP", "Device could not connect to the XMPP server", new Intent());
			return;
		}
		
		Log.i(TAG, "User connected to the XMPP server");
		ShowNotifications.notifyUser("XMPP", "Device connected to the XMPP server", new Intent());
	}

}
