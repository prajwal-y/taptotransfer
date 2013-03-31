package com.taptrans.xmpp;

import java.io.File;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.taptrans.util.AppData;
import com.taptrans.util.ShowNotifications;

public class XMPPService extends Service {

	private static Connection m_XMPPConnection = null;
	private static String TAG = "XMPPService";
	private static FileTransferManager manager = null;

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
		public void processMessage(Chat arg0,
				org.jivesoftware.smack.packet.Message message) {
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
		
		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(AppData.activity);
			dialog.setCancelable(true);
			dialog.setMessage("Logging on to the XMPP server. Hold on, you will be notified about the result!");
			dialog.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			dialog.cancel();
		}
		
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

			ServiceDiscoveryManager sdm = ServiceDiscoveryManager.getInstanceFor(m_XMPPConnection);
            if (sdm == null)
                sdm = new ServiceDiscoveryManager(m_XMPPConnection);
            sdm.addFeature("http://jabber.org/protocol/disco#info");
            sdm.addFeature("jabber:iq:privacy");
            manager = new FileTransferManager(m_XMPPConnection);
            FileTransferNegotiator.setServiceEnabled(m_XMPPConnection, true);
			manager.addFileTransferListener(new FileTransferListener() {
				public void fileTransferRequest(
						final FileTransferRequest request) {
					new Thread() {
						@Override
						public void run() {
							IncomingFileTransfer transfer = request.accept();
							Log.i(TAG, "FileName with full path: "+Environment.getExternalStorageDirectory()
									+ "/" + transfer.getFileName());
							File root = Environment.getExternalStorageDirectory();
							File file = new File(root, transfer.getFileName());
							Intent notifyIntent = new Intent();
							Log.i(TAG, file.getAbsolutePath());
							try {
								transfer.recieveFile(file);
								while (!transfer.isDone()) {
									try {
										Thread.sleep(1000L);
										Log.i(TAG, "Recieving file: " + (transfer.getProgress()*100) + "%");
										// ShowNotifications.notifyUser("XMPP File transfer",
										// "File transfer status: RECIEVING",
										// notifyIntent);
									} catch (Exception e) {
										Log.e(TAG, e.getMessage(), e);
										ShowNotifications.notifyUser(
												"XMPP File transfer",
												"File transfer status: FAILED",
												notifyIntent);
										return;
									}
									if (transfer
											.getStatus()
											.equals(org.jivesoftware.smackx.filetransfer.FileTransfer.Status.error)) {
										/*Log.e(TAG, transfer.getError()
												.getMessage());*/
										Log.e(TAG, "Transfer failed" + transfer.getError() + transfer.getStatus());
										ShowNotifications.notifyUser(
												"File transfer",
												"File transfer status: FAILED",
												notifyIntent);
										return;
									}
									if (transfer.getException() != null) {
										Log.e(TAG,
												"Exception during receiving file: ",
												transfer.getException());
										ShowNotifications.notifyUser(
												"File transfer",
												"File transfer status: FAILED",
												notifyIntent);
										return;
									}
								}
							} catch (Exception e) {
								Log.e(TAG, e.getMessage(), e);
								ShowNotifications.notifyUser("File transfer",
										"File transfer status: FAILED",
										notifyIntent);
								return;
							}
							ShowNotifications.notifyUser("File transfer",
									"File transfer status: SUCCESSFUL!",
									notifyIntent);
						};
					}.start();
				}
			});
			return null;
		}

	}
}
