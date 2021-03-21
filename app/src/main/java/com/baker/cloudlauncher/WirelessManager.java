package com.baker.cloudlauncher;

import android.content.Context;
import android.net.wifi.WifiManager;

public class WirelessManager {

    public boolean checkWiFi(Context c) {
        WifiManager wifiManager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }


}
