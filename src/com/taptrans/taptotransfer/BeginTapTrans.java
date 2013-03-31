package com.taptrans.taptotransfer;

import java.util.HashMap;

import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.taptrans.filebrowser.FileBrowser;
import com.taptrans.util.AppConstants;
import com.taptrans.util.AppData;
import com.taptrans.xmpp.XMPPOperations;
import com.taptrans.xmpp.XMPPService;

public class BeginTapTrans extends Activity {

	private static final int REQUEST_PATH = 1;
	private static String TAG = "BeginTapTrans";
	String curFileName;
	String path;
	EditText edittext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_begin_tap_trans);
		edittext = (EditText) findViewById(R.id.editText);
		TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		AppData.IMEI = tm.getDeviceId();
		AppData.username = "9008416496";
		//AppData.username = "123456789";
		AppData.activity = this;
		try{
		ProviderManager pm = ProviderManager.getInstance();
		pm.addIQProvider("si", "http://jabber.org/protocol/si", new StreamInitiationProvider());
		pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams", new BytestreamsProvider());
		pm.addIQProvider("query","http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());
		pm.addIQProvider("query","http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());
		}catch(Exception e){
			Log.e(TAG, "Exception occurred: ", e);
		}
		/*pm.addExtensionProvider("data", "http://jabber.org/protocol/ibb", new DataPacketProvider());
	    pm.addIQProvider("open", "http://jabber.org/protocol/ibb", new OpenIQProvider());
	    pm.addIQProvider("data", "http://jabber.org/protocol/ibb", new DataPacketProvider());
	    pm.addIQProvider("close", "http://jabber.org/protocol/ibb", new CloseIQProvider());*/

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_begin_tap_trans, menu);
		return true;
	}

	/*public void checkGCMRegistration() {
		//Firstly, checking whether the registration ID is stored
		SharedPreferences sharedPref = BeginTapTrans.this.getPreferences(Context.MODE_PRIVATE);
		AppData.gcmRegId = sharedPref.getString(AppConstants.GCM_REG_ID, "");
		Log.i("INFO: ", "GCM Registration ID received from stored preferences: "+AppData.gcmRegId);
		if (AppData.gcmRegId.equals("")) {
			GCMRegistrar.checkDevice(this);
			GCMRegistrar.checkManifest(this);
			String regId = GCMRegistrar.getRegistrationId(this);
			if (regId.equals("")) {
				GCMRegistrar.register(this, AppConstants.SENDER_ID);
			} else {
				Log.v("INFO:", "Already registered");
			}
		}
	}*/

	public void getFile(View view) {
		String extState = Environment.getExternalStorageState();
		if (!extState.equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(
					this,
					"External storage is not mounted. Please mount and try again.",
					Toast.LENGTH_LONG).show();
		} else {
			Intent intent1 = new Intent(this, FileBrowser.class);
			startActivityForResult(intent1, REQUEST_PATH);
		}
	}

	public void registerAccount(View view) {
		Toast.makeText(this, "Registering account..", Toast.LENGTH_SHORT).show();
		stopService(new Intent(this, XMPPService.class));
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(AppConstants.USERNAME, AppData.username);
		map.put(AppConstants.PASSWORD, AppData.IMEI);
		new XMPPOperations().registerUserAccount(map);
	}

	public void deleteAccount(View view) {
		new XMPPOperations().unregisterUser();
	}
	
	public void logInAccount(View view) {
		startService(new Intent(this, XMPPService.class));
	}
	
	public void logOutAccount(View view) {
		stopService(new Intent(this, XMPPService.class));
	}
	
	public void transferData(View view) {
		if(path == null || curFileName == null)
			Toast.makeText(this, "Select a file to transfer", Toast.LENGTH_LONG).show();
		else {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(AppConstants.FILENAME, path+"/"+curFileName);
		map.put(AppConstants.RECEIPIENT, "9481603262@ec2taptotransfer");
		//map.put(AppConstants.RECEIPIENT, "123456789@ec2taptotransfer");
		//map.put(AppConstants.RECEIPIENT, "9008416496@ec2taptotransfer");
		new XMPPOperations().transferFile(map);
		}
		//new XMPPOperations().sendMessage();
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_PATH) {
			if (resultCode == RESULT_OK) {
				path = data.getStringExtra("GetPath");
				curFileName = data.getStringExtra("GetFileName");
				Toast.makeText(this, "Selected file: "+path+"/"+curFileName, Toast.LENGTH_SHORT).show();
				edittext.setText(curFileName);
			}
		}
	}

}
