package com.taptrans.xmpp;


import java.util.ArrayList;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.bytestreams.socks5.Socks5Proxy;

import com.taptrans.util.AppConstants;

public class XMPPUtil {

	//private static String TAG = "XMPPUtil";
	private static XMPPConnection conn = null;

	public static XMPPConnection getConnection() {
		XMPPConnection.DEBUG_ENABLED = true;
		if (conn == null) {
			ConnectionConfiguration config = new ConnectionConfiguration(
					AppConstants.XMPP_DOMAIN, AppConstants.XMPP_PORT);
			config.setCompressionEnabled(false);
			config.setSASLAuthenticationEnabled(true);
			conn = new XMPPConnection(config);
		}
		return conn;
	}
	
	public static Socks5Proxy getSocks5ProxySafe() {
        boolean isLocalS5Penabled = SmackConfiguration
            .isLocalSocks5ProxyEnabled();
        SmackConfiguration.setLocalSocks5ProxyEnabled(false);
        Socks5Proxy proxy = Socks5Proxy.getSocks5Proxy();
        SmackConfiguration.setLocalSocks5ProxyEnabled(isLocalS5Penabled);
        return proxy;
    }
	
	public static void addProxyAddress(String ip, boolean inFront) {
        Socks5Proxy proxy = getSocks5ProxySafe();
        if (!inFront) {
            proxy.addLocalAddress(ip);
            return;
        }
        ArrayList<String> list = new ArrayList<String>(
            proxy.getLocalAddresses());
        list.remove(ip);
        list.add(0, ip);
        proxy.replaceLocalAddresses(list);
    }

}
