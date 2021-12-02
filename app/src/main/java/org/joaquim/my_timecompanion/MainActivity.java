package org.joaquim.my_timecompanion;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;

import org.joaquim.my_timecompanion.profile.BleProfileService;
import org.joaquim.my_timecompanion.profile.BleProfileServiceReadyActivity;

import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.UUID;

import no.nordicsemi.android.log.Logger;

public class MainActivity extends BleProfileServiceReadyActivity<UARTService.UARTBinder> implements UARTInterface {

  public static final String CONNECTED_DEVICE_CHANNEL = "connected_device_channel";
  private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
  private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
  private AlertDialog enableNotificationListenerAlertDialog;

  SharedPreferences prefs;
  SharedPreferences.Editor editor;

  private SlidingPaneLayout slider;
  private UARTService.UARTBinder serviceBinder;

  private TextView battery;
  String BleDevice = "00:00:00:00:00:00";

  @Override
  protected void onCreateView(Bundle savedInstanceState) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      final NotificationChannel channel = new NotificationChannel(CONNECTED_DEVICE_CHANNEL, getString(R.string.channel_connected_devices_title), NotificationManager.IMPORTANCE_DEFAULT);
      channel.setDescription(getString(R.string.channel_connected_devices_description));
      channel.setShowBadge(false);
      channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

      NotificationManager notificationManager = getSystemService(NotificationManager.class);
      notificationManager.createNotificationChannel(channel);
    }

    setContentView(R.layout.activity_main);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    prefs = getSharedPreferences("Settings", MODE_PRIVATE);
    editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();

    BleDevice = prefs.getString("BleDevice", "00:00:00:00:00:00");

    // Setup the sliding pane if it exists
    final SlidingPaneLayout slidingPane = slider = findViewById(R.id.sliding_pane);

    if (slidingPane != null) {
      //slidingPane.setSliderFadeColor(Color.TRANSPARENT);
      slidingPane.setShadowResourceLeft(R.drawable.shadow_r);
      slidingPane.setPanelSlideListener(new SlidingPaneLayout.SimplePanelSlideListener() {
        @Override
        public void onPanelClosed(final View panel) {
          // Close the keyboard
          final UARTLogFragment logFragment = (UARTLogFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_log);
          logFragment.onFragmentHidden();
        }
      });
    }

    if (!isNotificationServiceEnabled()) {
      enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog();
      enableNotificationListenerAlertDialog.show();
    }

    /*if (!BleDevice.equals("00:00:00:00:00:00")) {
      // Start the service that will connect to selected device
      final Intent service = new Intent(this, getServiceClass());
      service.putExtra(BleProfileService.EXTRA_DEVICE_ADDRESS, BleDevice);
      startService(service);
    }*/

  }

  private boolean isNotificationServiceEnabled() {
    String pkgName = getPackageName();
    final String flat = Settings.Secure.getString(getContentResolver(), ENABLED_NOTIFICATION_LISTENERS);
    if (!TextUtils.isEmpty(flat)) {
      final String[] names = flat.split(":");
      for (String name : names) {
        final ComponentName cn = ComponentName.unflattenFromString(name);
        if (cn != null) {
          if (TextUtils.equals(pkgName, cn.getPackageName())) {
            return true;
          }
        }
      }
    }
    return false;
  }

  private AlertDialog buildNotificationServiceAlertDialog() {
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
    alertDialogBuilder.setTitle("Welcome to the MY-Time Companion");
    alertDialogBuilder.setMessage("Please allow Notification for This App in the Upcoming window and press the back button");
    alertDialogBuilder.setPositiveButton("Open settings",
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
          }
        });
    alertDialogBuilder.setNegativeButton("No Abort",
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            finish();
          }
        });
    return (alertDialogBuilder.create());
  }

  private boolean isDeviceServiceRunning() {
    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    assert manager != null;
    for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
      if (DeviceService.class.getName().equals(service.service.getClassName())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main_menu, menu);
    return true;
  }

  @Override
  protected void onStart() {
    super.onStart();
    final DeviceControlFragment deviceFragment = (DeviceControlFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_control);

    battery = deviceFragment.getActivity().findViewById(R.id.battery_value);

    final TextView deviceNameView = deviceFragment.getActivity().findViewById(R.id.device_name);
    deviceNameView.setText(BleDevice);

  }


  private ServiceConnection serviceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(final ComponentName name, final IBinder service) {
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
    }

  };

  private static IntentFilter makeIntentFilter() {
    final IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(DeviceService.SEND_STATUS);
    return intentFilter;
  }

  @Override
  protected void onInitialize(final Bundle savedInstanceState) {
    LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, makeIntentFilter());

  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
  }

  private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(final Context context, final Intent intent) {
      final String action = intent.getAction();
      if (DeviceService.SEND_STATUS.equals(action)) {
        final String code = intent.getStringExtra("Code");
        final String value = intent.getStringExtra("Value");
        if ( code == "BATT" ) {
          battery.setText(value);
        }

      }
    }
  };

  @Override
  protected void onServiceBound(final UARTService.UARTBinder binder) {
    serviceBinder = binder;

  }

  @Override
  protected void onServiceUnbound() {
    serviceBinder = null;
  }

  @Override
  protected Class<? extends BleProfileService> getServiceClass() {
    return UARTService.class;
  }

  @Override
  public void onDeviceSelected(@NonNull final BluetoothDevice device, final String name) {
    // The super method starts the service
    super.onDeviceSelected(device, name);
    // Notify the log fragment about it

    final UARTLogFragment logFragment = (UARTLogFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_log);
    logFragment.onServiceStarted();

    editor.putString("BleDevice", device.getAddress());
    editor.apply();
  }

  @Override
  public void onDeviceConnected(@NonNull final BluetoothDevice device) {
    super.onDeviceConnected(device);

    final Intent service = new Intent(this, DeviceService.class);

    service.putExtra(BleProfileService.EXTRA_LOG_URI, getLogSession().getSessionUri());
    startService(service);
    bindService(service, serviceConnection, 0);
  }

  @Override
  public void onDeviceDisconnected(@NonNull final BluetoothDevice device) {
    super.onDeviceDisconnected(device);

    stopService(new Intent( this, DeviceService.class ) );
    battery.setText("---%");

  }

  @Override
  protected void setDefaultUI() {
  }

  @Override
  protected int getDefaultDeviceName() {
    return R.string.uart_default_name;
  }

  @Override
  protected int getAboutTextId() {
    return 0;
  }

  @Override
  protected UUID getFilterUUID() {
    return UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
  }

  @Override
  protected int getLoggerProfileTitle() {
    return R.string.uart_feature_title;
  }


  @Override
  protected Uri getLocalAuthorityLogger() {
    return UARTLocalLogContentProvider.AUTHORITY_URI;
  }

  @Override
  public void send(byte[] bytes) {
    if (serviceBinder != null)
      serviceBinder.send(bytes);
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

  }

  public void onSetTimeClicked(final View view) {
    transmitTime();
  }

  public void onFindWatchClicked(final View view) {

    byte[] notification = Utils.buildHeader(AppConstants.COMMAND_FIND_DEVICE, Utils.byteToByteArray((byte)(0x01)));
    send(notification);
  }

  public void onDebugClicked(final View view) {

    byte[] notification = Utils.buildHeader(AppConstants.COMMAND_VIBRATION, Utils.byteToByteArray((byte)(0x01)));
    send(notification);
  }

}