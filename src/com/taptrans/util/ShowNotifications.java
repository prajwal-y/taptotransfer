package com.taptrans.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.taptrans.taptotransfer.R;

public class ShowNotifications {
	static int mId;

	public static void notifyUser(String title, String text, Intent intent) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				AppData.activity).setSmallIcon(R.drawable.file_icon)
				.setContentTitle(title)
				.setContentText(text);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(AppData.activity);
		stackBuilder.addParentStack(AppData.activity.getClass());
		stackBuilder.addNextIntent(intent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) AppData.activity.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(mId, mBuilder.build());
	}

}
