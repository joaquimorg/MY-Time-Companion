package org.joaquim.my_timecompanion;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.joaquim.my_timecompanion.profile.BleProfileService;
import org.joaquim.my_timecompanion.profile.BleProfileServiceReadyActivity;

import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

import no.nordicsemi.android.ble.BleManagerCallbacks;
import no.nordicsemi.android.log.ILogSession;
import no.nordicsemi.android.log.Logger;

public class DeviceService extends Service {

  private ILogSession logSession;

  public static final String SEND_STATUS = "org.joaquim.my_timecompanion.SEND_STATUS";

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, makeIntentFilter());
  }

  private static IntentFilter makeIntentFilter() {
    final IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(UARTService.BROADCAST_UART_TX);
    intentFilter.addAction(UARTService.BROADCAST_UART_RX);
    intentFilter.addAction(NotificationListener.BROADCAST_NOTIFICATION);
    return intentFilter;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    Logger.i(logSession, "Device Service destroyed");
  }

  @Override
  public int onStartCommand(final Intent intent, final int flags, final int startId) {
    final Uri logUri = intent.getParcelableExtra(BleProfileService.EXTRA_LOG_URI);
    logSession = Logger.openSession(getApplicationContext(), logUri);

    Logger.i(logSession, "Device Service started");

    return START_REDELIVER_INTENT;
  }

  private void send(byte[] bytes) {
    final Intent globalBroadcast = new Intent(UARTService.ACTION_SEND);
    globalBroadcast.putExtra(UARTService.EXTRA_DATA, bytes);
    sendBroadcast(globalBroadcast);
  }

  private void sendStatus(String code, String value) {
    final Intent globalBroadcast = new Intent(DeviceService.SEND_STATUS);
    globalBroadcast.putExtra("Code", code);
    globalBroadcast.putExtra("Value", value);
    LocalBroadcastManager.getInstance(this).sendBroadcast(globalBroadcast);
  }

  /* -------------------------------------------------------------------------------------------- */

  class NotificationData {
    public int id;
    public String subject;
    public String body;
    public int type;

    static final byte MAX_CHAR = 48;

    public byte[] serializeData() {
      byte[] data = {};

      byte type;

      switch (this.type) {
        case NotificationsID.GENERIC_SMS:
          type = AppConstants.NOTIFICATION_SMS;
          break;
        case NotificationsID.DURING_PHONE_CALL:
          type = AppConstants.NOTIFICATION_MISSED_CALL;
          break;
        case NotificationsID.NOTIFICATION_GMAIL:
        case NotificationsID.NOTIFICATION_GOOGLE_INBOX:
        case NotificationsID.NOTIFICATION_MAILBOX:
        case NotificationsID.NOTIFICATION_OUTLOOK:
        case NotificationsID.NOTIFICATION_YAHOO_MAIL:
        case NotificationsID.GENERIC_EMAIL:
          type = AppConstants.NOTIFICATION_EMAIL;
          break;
        case NotificationsID.NOTIFICATION_WHATSAPP:
          type = AppConstants.NOTIFICATION_WHATSAPP;
          break;
        case NotificationsID.NOTIFICATION_SKYPE:
          type = AppConstants.NOTIFICATION_SKYPE;
          break;
        case NotificationsID.NOTIFICATION_WECHAT:
        case NotificationsID.NOTIFICATION_GOOGLE_HANGOUTS:
        case NotificationsID.NOTIFICATION_HIPCHAT:
        case NotificationsID.NOTIFICATION_TELEGRAM:
        case NotificationsID.NOTIFICATION_GOOGLE_MESSENGER:
        case NotificationsID.NOTIFICATION_KIK:
        case NotificationsID.NOTIFICATION_SLACK:
        case NotificationsID.NOTIFICATION_SNAPCHAT:
        case NotificationsID.NOTIFICATION_LINKEDIN:
          type = AppConstants.NOTIFICATION_MESSENGER;
          break;
        case NotificationsID.NOTIFICATION_INSTAGRAM:
          type = AppConstants.NOTIFICATION_INSTAGRAM;
          break;
        case NotificationsID.NOTIFICATION_TWITTER:
          type = AppConstants.NOTIFICATION_TWITTER;
          break;
        case NotificationsID.TIMELINE_CALENDAR:
          type = AppConstants.NOTIFICATION_CALENDAR;
          break;
        default:
          type = AppConstants.NOTIFICATION_GENERIC;
          break;
      }

      if (this.subject != null) {
        int maxLength = (this.subject.length() < MAX_CHAR) ? this.subject.length() : MAX_CHAR;
        this.subject = this.subject.substring(0, maxLength);
      }

      Calendar time = GregorianCalendar.getInstance();

      data = Utils.concatenateByteArrays( data, Utils.intToByteArray(this.id) );
      data = Utils.concatenateByteArrays( data, Utils.byteToByteArray(type));
      data = Utils.concatenateByteArrays( data, Utils.byteToByteArray((byte)(time.get(Calendar.HOUR_OF_DAY))));
      data = Utils.concatenateByteArrays( data, Utils.byteToByteArray((byte)(time.get(Calendar.MINUTE))));
      data = Utils.concatenateByteArrays( data, Utils.stringToByteArray(this.subject == null ? "" : this.subject, 30) );
      data = Utils.concatenateByteArrays( data, Utils.stringToByteArray(this.body == null ? "" : this.body, 60) );

      return data;
    }
  }

  private void onNotification(String value, int type) {
    //COMMAND_NOTIFICATION
    String subject = " ";
    String body = "";
    if ( value.contains(":")) {
      String splitData[] = value.split("\\:", 2);
      if (splitData.length == 2) {
        subject = splitData[0];
        body = splitData[1];
      } else {
        body = value;
      }
    } else {
      body = value;
    }
    NotificationData notificationData = new NotificationData();
    notificationData.id = 1;
    notificationData.subject = subject;
    notificationData.body = body;
    notificationData.type = type;

    byte[] notification = Utils.buildHeader(AppConstants.COMMAND_NOTIFICATION, notificationData.serializeData());
    send(notification);

  }

  private void transmitTime() {

    //TimeZone tz = TimeZone.getTimeZone("Europe/Lisbon");
    //TimeZone.setDefault(tz);
    Calendar now = Calendar.getInstance();

    //(now.getTimeZone().getRawOffset() / 1000) +

    int datetime = (int)(now.getTimeInMillis() / 1000 ) - 946684800;

    byte[] tsByte = Utils.intToByteArray(datetime);
    byte[] notification = Utils.buildHeader(AppConstants.COMMAND_TIME_UPDATE, tsByte);

    send(notification);

    Logger.i(logSession, "[MY-Time] Send time.");
  }

  private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(final Context context, final Intent intent) {
      final String action = intent.getAction();
      final BluetoothDevice device = intent.getParcelableExtra(UARTService.EXTRA_DEVICE);

      if (UARTService.BROADCAST_UART_RX.equals(action)) {
        final byte[] value = intent.getByteArrayExtra(UARTService.EXTRA_DATA);
        final ByteBuffer dataBuf = ByteBuffer.wrap(value);

        if ( dataBuf.get() == 0x00 ) {
          byte code = dataBuf.get();
          switch (code) {
            case AppConstants.COMMAND_PT_VERSION:
              byte v1 = dataBuf.get();
              byte v2 = dataBuf.get();
              byte[] vData = {v1, v2};

              Logger.i(logSession, "[MY-Time] Version");
              transmitTime();
              break;
            case AppConstants.COMMAND_PT_BATTERY:
              int b = dataBuf.getInt();
              float volt = dataBuf.getFloat();
              byte status = dataBuf.get();
              if (b < 0) b = 0;
              if (b > 100) b = 100;

              Logger.i(logSession, "[MY-Time] Battery Info " + String.valueOf(b) + "%");

              sendStatus("BATT", String.valueOf(b) + "%");

              break;
            default:
              Logger.i(logSession, "[MY-Time] unknown code " + String.valueOf(code));
              break;
          }
        }

      } else if (NotificationListener.BROADCAST_NOTIFICATION.equals(action)) {
          Logger.d(logSession, "[MY-Time] Notification");
          final String value = intent.getStringExtra("tickerText");
          final int type = intent.getIntExtra("type", 0);
          onNotification(value, type);
      }
    }
  };

}