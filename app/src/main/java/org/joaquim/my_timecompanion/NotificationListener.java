package org.joaquim.my_timecompanion;


import android.app.Notification;
import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class NotificationListener extends NotificationListenerService {

  public static final String BROADCAST_NOTIFICATION = "org.joaquim.my_timecompanion.BROADCAST_NOTIFICATION";

  public static String TAG = "NotificationListener";

  @Override
  public IBinder onBind(Intent intent) {
    return super.onBind(intent);
  }

  @Override
  public void onCreate() {
    super.onCreate();
  }

  @Override
  public void onNotificationPosted(StatusBarNotification sbn) {

    if (shouldIgnoreSource(sbn)) {
      return;
    }

    if (NotificationCompat.CATEGORY_CALL.equals(sbn.getNotification().category) && sbn.isOngoing()) {
      //handleCallNotification(sbn);
      //return;
    }

    String source = sbn.getPackageName();
    Notification notification = sbn.getNotification();

    NotificationType type = AppNotificationType.getInstance().get(source);
    if (type == null) {
      type = NotificationType.UNKNOWN;
    }

    if (notification.tickerText != null ) {
      Log.i(TAG, "ID:" + sbn.getId());
      Log.i(TAG, "Posted by:" + source);
      Log.i(TAG, "tickerText:" + notification.tickerText);

      Intent intent = new Intent(BROADCAST_NOTIFICATION);
      intent.putExtra("tickerText", sanitizeUnicode(notification.tickerText.toString()));
      intent.putExtra("type", type.icon);
      LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
  }

  @Override
  public void onNotificationRemoved(StatusBarNotification sbn) {

  }

  private String sanitizeUnicode(String orig) {
    return orig.replaceAll("\\p{C}", "");
  }

  private boolean shouldIgnoreSource(StatusBarNotification sbn) {
    String source = sbn.getPackageName();

    /* do not display messages from "android"
     * This includes keyboard selection message, usb connection messages, etc
     * Hope it does not filter out too much, we will see...
     */

    if (source.equals("android") ||
        source.equals("com.android.systemui") ||
        source.equals("com.android.dialer") ||
        source.equals("com.google.android.dialer") ||
        source.equals("com.cyanogenmod.eleven")) {
      return true;
    }

    if (source.equals("com.moez.QKSMS") ||
        source.equals("com.android.mms") ||
        source.equals("com.sonyericsson.conversations") ||
        source.equals("com.android.messaging") ||
        source.equals("org.smssecure.smssecure")) {
      return true;
    }

    return false;
  }
}
