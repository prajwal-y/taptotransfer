package com.taptrans.xmpp;

import java.io.File;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;

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
		manager = new FileTransferManager(m_XMPPConnection);
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

			manager.addFileTransferListener(new FileTransferListener() {
				public void fileTransferRequest(
						final FileTransferRequest request) {
					new Thread() {
						@Override
						public void run() {
							IncomingFileTransfer transfer = request.accept();
							File mf = Environment.getExternalStorageDirectory();
							File file = new File(mf.getAbsoluteFile()
									+ transfer.getFileName());
							Intent notifyIntent = new Intent();
							try {
								transfer.recieveFile(file);
								while (!transfer.isDone()) {
									try {
										Thread.sleep(1000L);
										Log.i(TAG, "Recieving file");
										// ShowNotifications.notifyUser("XMPP File transfer",
										// "File transfer status: RECIEVING",
										// notifyIntent);
									} catch (Exception e) {
										Log.e(TAG, e.getMessage(), e);
										ShowNotifications.notifyUser(
												"XMPP File transfer",
												"File transfer status: FAILED",
												notifyIntent);
									}
									if (transfer
											.getStatus()
											.equals(org.jivesoftware.smackx.filetransfer.FileTransfer.Status.error)) {
										Log.e(TAG, transfer.getError()
												.getMessage());
										ShowNotifications.notifyUser(
												"File transfer",
												"File transfer status: FAILED",
												notifyIntent);
									}
									if (transfer.getException() != null) {
										Log.e(TAG,
												"Exception during receiving file: ",
												transfer.getException());
										ShowNotifications.notifyUser(
												"File transfer",
												"File transfer status: FAILED",
												notifyIntent);
									}
								}
							} catch (Exception e) {
								Log.e(TAG, e.getMessage(), e);
								ShowNotifications.notifyUser("File transfer",
										"File transfer status: FAILED",
										notifyIntent);
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
