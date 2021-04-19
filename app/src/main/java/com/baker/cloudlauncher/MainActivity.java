package com.baker.cloudlauncher;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.sdsmdg.tastytoast.TastyToast;

public class MainActivity extends AppCompatActivity {

    public boolean kishiConnected = false;
    WirelessManager mWirelessManager = new WirelessManager();
    KishiManager km = new KishiManager();
    BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setBatIcon();
        }
    };
    MediaPlayer mediaPlayer;
    private TextView batteryText;
    private BroadcastReceiver minuteUpdateReceiver;
    private String displayBat;
    private boolean isOled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences pref = this.getPreferences(Context.MODE_PRIVATE);
        isOled = pref.getBoolean("oled", false);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        batteryText = findViewById(R.id.tv_bat);
        batteryText.setText(String.format("%s%%", getBattery()));
        registerBatteryReceiver();
        setIcons();

        BroadcastReceiver mUsbAttachReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                    setBatIcon();
                }
            }
        };

        BroadcastReceiver mUsbDetachReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                    setIcons();
                }
            }
        };

        IntentFilter filter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        registerReceiver(mUsbAttachReceiver, filter);
        filter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbDetachReceiver, filter);


    }

    public void startMinuteUpdater() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        minuteUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Update battery percentage every minute.
                displayBat = getBattery() + "%";
                batteryText.setText(displayBat);
                // Update battery icon every minute.
                setBatIcon();
            }
        };
        registerReceiver(minuteUpdateReceiver, intentFilter);
    }

    private void registerBatteryReceiver() {
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBatteryReceiver, batteryLevelFilter);
    }

    // Update the battery icon depending on the percentage.
    public void setBatIcon() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, ifilter);
        int percent = Integer.parseInt(getBattery());

        // How are we charging?
        int chargePlug = 0;
        if (batteryStatus != null) {
            chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        }
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        // Update Icon
        if (usbCharge || acCharge) {
            ImageView img = (ImageView) findViewById(R.id.iv_bat);
            img.setImageResource(R.drawable.ico_battery_charging);
        } else {
            if (percent < 10) {
                ImageView img = (ImageView) findViewById(R.id.iv_bat);
                img.setImageResource(R.drawable.ico_battery_empty);
            } else if (percent < 20 && percent > 10) {
                ImageView img = (ImageView) findViewById(R.id.iv_bat);
                img.setImageResource(R.drawable.ico_battery_low);
            } else if (percent > 20 && percent < 80) {
                ImageView img = (ImageView) findViewById(R.id.iv_bat);
                img.setImageResource(R.drawable.ico_battery_half);
            } else {
                ImageView img = (ImageView) findViewById(R.id.iv_bat);
                img.setImageResource(R.drawable.ico_battery_full);
            }
        }

        if (km.isKishiConnected(getApplicationContext())) {
            kishiConnected = true;
            ImageView img = (ImageView) findViewById(R.id.iv_razer);
            img.setImageResource(R.drawable.icon_razer);
        } else {
            kishiConnected = false;
            ImageView img = (ImageView) findViewById(R.id.iv_razer);
            img.setImageResource(R.drawable.icon_razer_unplugged);
        }

        // Change WiFi icon to show enabled or disabled state.
        if (mWirelessManager.checkWiFi(getApplicationContext())) {
            ImageView img = (ImageView) findViewById(R.id.iv_wifi);
            img.setImageResource(R.drawable.ico_wifi);
        } else {
            ImageView img = (ImageView) findViewById(R.id.iv_wifi);
            img.setImageResource(R.drawable.ico_wifi_disabled);
        }


        if (!isOled) {
            ImageView img = (ImageView) findViewById(R.id.iv_oled);
            img.setImageResource(R.drawable.ico_oled_off);
        } else {
            ImageView img = (ImageView) findViewById(R.id.iv_oled);
            img.setImageResource(R.drawable.ico_oled_on);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        startMinuteUpdater();
        setBatIcon();
    }

    @Override
    protected void onPause() {
        super.onPause();
        setBatIcon();
        unregisterReceiver(minuteUpdateReceiver);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            km.isKishiConnected(getApplicationContext());
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public void setIcons() {
        Log.d("DEBUG", "Loading icons");
        Drawable icoGamePass;
        try {
            icoGamePass = this.getPackageManager().getApplicationIcon("com.gamepass");
            if (icoGamePass != null) {
                ImageButton xcButton = findViewById(R.id.btn_xcloud);
                xcButton.setImageDrawable(icoGamePass);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("DEBUG", "GamePass app is not installed.");
        }


        Drawable icoStadia;
        try {
            icoStadia = this.getPackageManager().getApplicationIcon("com.google.stadia.android");
            if (icoStadia != null) {
                ImageButton sButton = findViewById(R.id.btn_stadia);
                sButton.setImageDrawable(icoStadia);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("DEBUG", "Stadia app is not installed.");
        }


        Drawable icoGFN;
        try {
            icoGFN = this.getPackageManager().getApplicationIcon("com.nvidia.geforcenow");
            if (icoGFN != null) {
                ImageButton gfnButton = findViewById(R.id.btn_gnow);
                gfnButton.setImageDrawable(icoGFN);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("DEBUG", "GeForce Now app is not installed.");
        }

    }

    public void launchXcloud(View view) {
        Log.d("DEBUG", "Launching X Cloud");
        launchApp("com.gamepass");
        playSoundFile(R.raw.button);
    }

    public void launchStadia(View view) {
        Log.d("DEBUG", "Launching Stadia");
        launchApp("com.google.stadia.android");
        playSoundFile(R.raw.button);
    }

    public void launchGnow(View view) {
        Log.d("DEBUG", "Launching GeForce Now");
        launchApp("com.nvidia.geforcenow");
        playSoundFile(R.raw.button);
    }

    public void launchChrome(View view) {
        Log.d("DEBUG", "Launching Chrome");
        launchApp("com.android.chrome");
        playSoundFile(R.raw.button);
    }

    public void launchYouTube(View view) {
        Log.d("DEBUG", "Launching Youtube");
        launchApp("com.google.android.youtube");
        playSoundFile(R.raw.button);
    }

    public void launchDiscord(View view) {
        Log.d("DEBUG", "Launching Discord Invite");
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://discord.gg/W7nchCbu")));
        playSoundFile(R.raw.button);
    }

    public void launchSettings(View view) {
        Log.d("DEBUG", "Launching settings");
        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
        playSoundFile(R.raw.button);
    }

    public void launchWiFiSettings(View view) {
        startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
        playSoundFile(R.raw.button);
    }

    public void launchBatSettings(View view) {
        startActivityForResult(new Intent(Intent.ACTION_POWER_USAGE_SUMMARY), 0);
        playSoundFile(R.raw.button);
    }

    public void launchKishi(View view) {
        if (kishiConnected) {
            Log.d("DEBUG", "Launching Razer Kishi");
            launchApp("com.razer.mobilegamepad.en");
            playSoundFile(R.raw.button);
        } else {
            Log.d("DEBUG", "Kishi is not connected.");
            //TODO add nice roast here.
        }
    }

    public void launchPlaystore(View view) {
        Log.d("DEBUG", "Launching Google Play Store");
        launchApp("com.android.vending");
        playSoundFile(R.raw.button);
    }

    public void toggleOled(View view) {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Log.d("DEBUG", "Toggling OLED support.");
        if (isOled) {
            playSoundFile(R.raw.button);
            TastyToast.makeText(getApplicationContext(), "OLED Mode disabled.", TastyToast.LENGTH_SHORT, TastyToast.DEFAULT);
            isOled = false;
            editor.putBoolean("oled", false);
            editor.apply();
            ImageView img = (ImageView) findViewById(R.id.iv_oled);
            img.setImageResource(R.drawable.ico_oled_off);
            ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.const_layout);
            constraintLayout.setBackgroundResource(R.drawable.bg_dark);

        } else {
            playSoundFile(R.raw.button);
            TastyToast.makeText(getApplicationContext(), "OLED Mode enabled.", TastyToast.LENGTH_SHORT, TastyToast.DEFAULT);
            isOled = true;
            editor.putBoolean("oled", true);
            editor.apply();
            ImageView img = (ImageView) findViewById(R.id.iv_oled);
            img.setImageResource(R.drawable.ico_oled_on);
            ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.const_layout);
            constraintLayout.setBackgroundResource(R.drawable.bg_black);
        }
    }

//    public void launchGameDraw(View view) {
//        Log.d("DEBUG", "Launching Google Play Games Library");
//        Intent intent = new Intent();
//        intent.setComponent(new ComponentName("com.google.android.play.games", "com.google.android.apps.play.games.features.gamefolder.GameFolderTrampolineActivity"));
//        if (getPackageManager().resolveActivity(intent, 0) != null) {
//            startActivity(intent);
//        } else {
//            Toast.makeText(this, "No app installed that can perform this action", Toast.LENGTH_SHORT).show();
//        }
//    }

    public String getBattery() {
        BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
        int percentage = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        return String.valueOf(percentage);
    }

    public void launchApp(String packageName) {
        try {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
            startActivity(launchIntent);
        } catch (Exception e) {
            Log.d("DEBUG", "Looks like you don't have " + packageName + " installed.");
        }
    }

    //play a soundfile
    public void playSoundFile(Integer fileName) {
        mediaPlayer = MediaPlayer.create(this, fileName);
        mediaPlayer.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD)
                == InputDevice.SOURCE_GAMEPAD) {
            if (keyCode == 82) {
                Toast.makeText(this, "Home Button Pressed", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}