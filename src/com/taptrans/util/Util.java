package com.taptrans.util;

import java.lang.reflect.Method;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import android.util.Log;

public class Util {

	private static String TAG = "Util";
	
	public static String getDeviceIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						Log.i(TAG, "IP Address is: "+inetAddress.getHostAddress().toString());
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("SocketException occurred", ex.getStackTrace().toString());
		}
		return "";
	}
	
	public static List<InetAddress> getAllNonLoopbackLocalIPAdresses(
	        boolean includeIPv6Addresses) throws UnknownHostException,
	        SocketException {

	        List<InetAddress> ips = new LinkedList<InetAddress>();

	        // Holds last ipv4 index in ips list (used to sort IPv4 before IPv6 IPs)
	        int ipv4Index = 0;

	        // Prepare method calls by reflection
	        Class<NetworkInterface> networkInterfaceClass = NetworkInterface.class;
	        Method mIsUp = null;
	        Method mIsLoopback = null;
	        try {
	            mIsUp = networkInterfaceClass.getMethod("isUp", (Class[]) null);
	        } catch (Exception e) {
	        	Log.e(TAG, "Exception occurred: ", e);
	        }
	        try {
	            mIsLoopback = networkInterfaceClass.getMethod("isLoopback",
	                (Class[]) null);
	        } catch (Exception e) {
	        	Log.e(TAG, "Exception occurred: ", e);
	        }

	        // Get all network interfaces
	        Enumeration<NetworkInterface> eInterfaces = NetworkInterface
	            .getNetworkInterfaces();

	        // Enumerate interfaces and enumerate all Internet addresses of each
	        if (eInterfaces != null) {
	            while (eInterfaces.hasMoreElements()) {
	                NetworkInterface ni = eInterfaces.nextElement();

	                // skip loopback devices and not running interfaces
	                try {
	                    if (mIsLoopback != null)
	                        if ((Boolean) mIsLoopback.invoke(ni, (Object[]) null))
	                            continue;
	                    if (mIsUp != null)
	                        if ((Boolean) mIsUp.invoke(ni, (Object[]) null) == false)
	                            continue;
	                } catch (Exception e) {
	                    Log.e(TAG, "Exception occurred: ", e);
	                }

	                Enumeration<InetAddress> iaddrs = ni.getInetAddresses();
	                while (iaddrs.hasMoreElements()) {
	                    InetAddress iaddr = iaddrs.nextElement();
	                    // in case ni.isLoopback failed to invoke
	                    if (iaddr.isLoopbackAddress())
	                        continue;

	                    if (iaddr instanceof Inet6Address) {
	                        if (includeIPv6Addresses)
	                            ips.add(iaddr);
	                    } else
	                        ips.add(ipv4Index++, iaddr);
	                }
	            }
	        }
	        return ips;
	    }
}
