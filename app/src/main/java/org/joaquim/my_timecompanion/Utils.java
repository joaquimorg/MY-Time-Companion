package org.joaquim.my_timecompanion;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class Utils {

  public static byte[] floatToByteArray ( final float i ) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      dos.writeFloat(i);
      dos.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return bos.toByteArray();
  }

  public static byte[] boolToByteArray ( final boolean i ) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      dos.writeBoolean(i);
      dos.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return bos.toByteArray();
  }

  public static byte[] intToByteArray(final int i) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      dos.writeInt(i);
      dos.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return bos.toByteArray();
  }

  public static byte[] byteToByteArray ( final byte i ) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      dos.writeByte(i);
      dos.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return bos.toByteArray();
  }

  public static String trim(String value, int numBytes, String charset) {
    do {
      byte[] valueInBytes = null;
      try {
        valueInBytes = value.getBytes(charset);
      } catch (UnsupportedEncodingException e) {
        throw new RuntimeException(e.getMessage(), e);
      }
      if (valueInBytes.length > numBytes) {
        value = value.substring(0, value.length() - 1);
      } else {
        return value;
      }
    } while (value.length() > 0);
    return "";

  }

  public static byte[] stringToByteArray ( String i, int maxLenght ) {

    int length = i.getBytes(StandardCharsets.UTF_8).length;

    //i = i.substring(0, length > maxLenght ? maxLenght : length);
    i = trim(i, length > maxLenght ? maxLenght : length, "UTF-8");
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      dos.writeByte(length + 1);
      dos.write(i.getBytes(StandardCharsets.UTF_8));
      dos.writeByte(0x00);
      dos.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return bos.toByteArray();
  }

  public static byte[] concatenateByteArrays(byte[] a, byte[] b) {
    byte[] result = new byte[a.length + b.length];
    System.arraycopy(a, 0, result, 0, a.length);
    System.arraycopy(b, 0, result, a.length, b.length);
    return result;
  }

  public static byte[] buildHeader(byte cmd, byte[] payload) {
    byte[] notification = new byte[4 + payload.length];
    notification[0] = 0x00;
    notification[1] = (byte) cmd;
    notification[2] = (byte) (payload.length & 0xff);
    notification[3] = (byte) (payload.length >> 8);

    System.arraycopy(payload, 0, notification, 4, payload.length);

    return notification;
  }

  private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
  public static String bytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = HEX_ARRAY[v >>> 4];
      hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars);
  }

}
