package com.baker.cloudlauncher;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView batteryText;
    private BroadcastReceiver minuteUpdateReceiver;
    private String displayBat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        batteryText = findViewById(R.id.tv_bat);
        batteryText.setText(String.format("%s%%", getBattery()));
        setBatIcon();
        setIcons();
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

    // Update the battery icon depending on the percentage.
    public void setBatIcon() {
        int percent = Integer.parseInt(getBattery());
        if (percent < 20) {
            ImageView img = (ImageView) findViewById(R.id.iv_bat);
            img.setImageResource(R.drawable.ico_battery_empty);
        } else if (percent > 20 && percent < 80) {
            ImageView img = (ImageView) findViewById(R.id.iv_bat);
            img.setImageResource(R.drawable.ico_battery_half);
        } else {
            ImageView img = (ImageView) findViewById(R.id.iv_bat);
            img.setImageResource(R.drawable.ico_battery_full);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startMinuteUpdater();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(minuteUpdateReceiver);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {

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
    }

    public void launchStadia(View view) {
        Log.d("DEBUG", "Launching Stadia");
        launchApp("com.google.stadia.android");
    }

    public void launchGnow(View view) {
        Log.d("DEBUG", "Launching GeForce Now");
        launchApp("com.nvidia.geforcenow");
    }

    public void launchDiscord(View view) {
        Log.d("DEBUG", "Launching Discord Invite");
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://discord.gg/W7nchCbu")));
    }

    public void launchSettings(View view) {
        Log.d("DEBUG", "Launching settings");
        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
    }

    public void launchCortex(View view) {
        Log.d("DEBUG", "Launching Razer Cortex");
        launchApp("com.razer.cortex");
    }

    public void launchPlaystore(View view) {
        Log.d("DEBUG", "Launching Google Play Store");
        launchApp("com.android.vending");
    }

    public void launchGameDraw(View view) {
        Log.d("DEBUG", "Launching Google Play Games Library");
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.google.android.play.games", "com.google.android.apps.play.games.features.gamefolder.GameFolderTrampolineActivity"));
        if (getPackageManager().resolveActivity(intent, 0) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "No app installed that can perform this action", Toast.LENGTH_SHORT).show();
        }
    }

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

}