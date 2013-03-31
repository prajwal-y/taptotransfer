package com.taptrans.xmpp;



import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;

import com.taptrans.util.AppConstants;

public class XMPPUtil {

	//private static String TAG = "XMPPUtil";
	private static Connection conn = null;

	public static Connection getConnection() {
		Connection.DEBUG_ENABLED = true;
		if (conn == null) {
			ConnectionConfiguration config = new ConnectionConfiguration(
					AppConstants.XMPP_DOMAIN, AppConstants.XMPP_PORT);
			config.setCompressionEnabled(false);
			config.setSASLAuthenticationEnabled(true);
			conn = new XMPPConnection(config);
		}
		return conn;
	}

}
