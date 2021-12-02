package org.joaquim.my_timecompanion;

public final class AppConstants {

  public static final byte COMMAND_BASE = 0x00;
  public static final byte COMMAND_TIME_UPDATE            = COMMAND_BASE + 0x01;
  public static final byte COMMAND_NOTIFICATION           = COMMAND_BASE + 0x02;
  public static final byte COMMAND_DELETE_NOTIFICATION    = COMMAND_BASE + 0x03;
  public static final byte COMMAND_SET_ALARMS             = COMMAND_BASE + 0x04;
  public static final byte COMMAND_SET_CALL               = COMMAND_BASE + 0x05;
  public static final byte COMMAND_SET_MUSIC              = COMMAND_BASE + 0x06;
  public static final byte COMMAND_SET_MUSIC_INFO         = COMMAND_BASE + 0x07;
  public static final byte COMMAND_ACTIVITY_STATUS        = COMMAND_BASE + 0x08;
  public static final byte COMMAND_FIND_DEVICE            = COMMAND_BASE + 0x09;
  public static final byte COMMAND_VIBRATION              = COMMAND_BASE + 0x0a;
  public static final byte COMMAND_WEATHER                = COMMAND_BASE + 0x0b;

  public static final byte COMMAND_PT_VERSION             = COMMAND_BASE + 0x01;
  public static final byte COMMAND_PT_BATTERY             = COMMAND_BASE + 0x02;


  // notification types and icons
  public static final byte NOTIFICATION_GENERIC       = (byte) 0x00;
  public static final byte NOTIFICATION_MISSED_CALL   = (byte) 0x01;
  public static final byte NOTIFICATION_SMS           = (byte) 0x02;
  public static final byte NOTIFICATION_SOCIAL        = (byte) 0x03;
  public static final byte NOTIFICATION_EMAIL         = (byte) 0x04;
  public static final byte NOTIFICATION_CALENDAR      = (byte) 0x05;
  public static final byte NOTIFICATION_WHATSAPP      = (byte) 0x06;
  public static final byte NOTIFICATION_MESSENGER     = (byte) 0x07;
  public static final byte NOTIFICATION_INSTAGRAM     = (byte) 0x08;
  public static final byte NOTIFICATION_TWITTER       = (byte) 0x09;
  public static final byte NOTIFICATION_SKYPE         = (byte) 0x0a;
}
