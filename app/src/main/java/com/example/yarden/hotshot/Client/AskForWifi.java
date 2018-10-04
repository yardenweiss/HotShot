package com.example.yarden.hotshot.Client;


import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;


import com.example.yarden.hotshot.Activitys.MainActivity;
import com.example.yarden.hotshot.Utils.P2PWifi;

import java.lang.reflect.InvocationTargetException;

public class AskForWifi {

    private P2PWifi p2pWifi;
    private Context context;
    private MainActivity activity;
    private  WifiP2pManager mManager;
    private WifiManager m_wifiManager;
    private WifiConfiguration m_wifiConf;
    private  String m_ssid;
    private String m_key;

    public AskForWifi(P2PWifi _p2pWifi, WifiManager wifiManager)
    {

        m_wifiManager = wifiManager;
        m_wifiConf = new WifiConfiguration();
        p2pWifi = _p2pWifi;
    }


    private void setSSID(String ssid){
        m_ssid = ssid;
    }

    private void setKey(String key){
        m_key = key;
    }

    private void connectToWifi() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //  String ssid = "yarden"; // temp for debuging
        //  String key = "Aa123456"; // temp for debuging

        m_wifiManager.setWifiEnabled(true);

        m_wifiConf.SSID = "\"" + m_ssid + "\"";
        m_wifiConf.preSharedKey = "\""+ m_key +"\"";
        int netId = m_wifiManager.addNetwork(m_wifiConf);
        m_wifiManager.saveConfiguration();
        m_wifiManager.disconnect();
        m_wifiManager.enableNetwork(netId, true);
        m_wifiManager.reconnect();


    }

    private void enableWifi() {
        if (!m_wifiManager.isWifiEnabled())
            m_wifiManager.setWifiEnabled(true);
    }

    public void GetWifi() {

        try {
            enableWifi();
            p2pWifi.StartConnectionP2P();
            p2pWifi.WriteMessege("Hi");
            String msg = p2pWifi.GetAnswerMsg();
            String[] parts = msg.split("--");
            setSSID(parts[0]);
            setKey(parts[1]);
            wait(2000); // wait still provider hotspot is on
            connectToWifi();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}