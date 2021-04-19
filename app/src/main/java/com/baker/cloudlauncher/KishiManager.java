package com.baker.cloudlauncher;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

import java.util.Collection;
import java.util.HashMap;

import static android.content.Context.USB_SERVICE;

public class KishiManager {

    public boolean isKishiConnected(Context c) {
        UsbManager m = (UsbManager) c.getSystemService(USB_SERVICE);
        HashMap<String, UsbDevice> usbDevices = m.getDeviceList();
        Collection<UsbDevice> ite = usbDevices.values();
        UsbDevice[] usbs = ite.toArray(new UsbDevice[]{});
        for (UsbDevice usb : usbs) {
            //TODO Do something when Kishi is connected.
            return true;
        }
        return false;
    }

}
