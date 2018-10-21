package com.example.yarden.hotshot.Provider;


import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.widget.ArrayAdapter;


import com.example.yarden.hotshot.Utils.FirebaseInstances;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ShareWifi {

    private WifiManager m_wifiManager;
    private WifiConfiguration m_wifiConf;
    private ArrayAdapter<String> mPeersStringAdapter;
    private String message;


    public ShareWifi(WifiManager wifiManager) {
        m_wifiConf = new WifiConfiguration();
        m_wifiManager = wifiManager;
    }


    private void enableWifi() {
        if (!m_wifiManager.isWifiEnabled())
            m_wifiManager.setWifiEnabled(true);
    }


    public String getMessage() {
        return  getHotspotInfo() + '/' + FirebaseInstances.getUid();

    }


    public String getHotspotInfo() {

        Method[] methods = m_wifiManager.getClass().getDeclaredMethods();
        for (Method m : methods) {
            if (m.getName().equals("getWifiApConfiguration")) {
                try {
                    m_wifiConf = (WifiConfiguration) m.invoke(m_wifiManager);
                    message = m_wifiConf.SSID + '/' + m_wifiConf.preSharedKey + '/';
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

            }
        }
        return message;
    }

}