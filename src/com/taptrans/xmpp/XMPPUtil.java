package com.taptrans.xmpp;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;

import com.taptrans.util.AppConstants;

public class XMPPUtil {

	private static String TAG = "XMPPUtil";
	public static Connection createConnection() {
		ConnectionConfiguration config = new ConnectionConfiguration(AppConstants.XMPP_DOMAIN, AppConstants.XMPP_PORT);
		Connection conn = new XMPPConnection(config);
		return conn;
	}
}
