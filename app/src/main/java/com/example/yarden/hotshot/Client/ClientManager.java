package com.example.yarden.hotshot.Client;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;

import com.example.yarden.hotshot.MainActivity;
import com.example.yarden.hotshot.Utils.UpdateDataBase;
import com.example.yarden.hotshot.Utils.User;
import java.util.Calendar;
import java.util.List;

public class ClientManager implements IClientReciveEventListener,IWifiFaoundEventListener {

    private WifiManager m_wifiManager;
    private WifiConfiguration m_wifiConf;
    private User providerUser;
    private User clientUser;
    private UpdateDataBase updateDataBase;
    private String message;
    private  int netId = 0;
    private MainActivity mActivity;

    public ClientManager(WifiManager wifiManager, MainActivity Activity)
    {
        mActivity = Activity;
        m_wifiManager = wifiManager;
        m_wifiConf = new WifiConfiguration();
        mActivity.SetWifiFoundEventListener(this);

    }

    public int getNetId() {
        return netId;
    }

    private void connectToWifi() {
        m_wifiManager.setWifiEnabled(true);
        m_wifiConf.SSID = "\"" + providerUser.getSsid() + "\"";
        m_wifiConf.preSharedKey = "\""+ providerUser.getHotspotPassword() +"\"";
         netId = m_wifiManager.addNetwork(m_wifiConf);
        m_wifiManager.saveConfiguration();
        m_wifiManager.disconnect();
        m_wifiManager.enableNetwork(netId, true);
        m_wifiManager.reconnect();
    }


    private void enableWifi(boolean state) {
            m_wifiManager.setWifiEnabled(state);
    }

// TODO resume dataBase
    public void run()
    {
        connectToWifi();
         updateDataBase = new UpdateDataBase(providerUser ,clientUser, m_wifiManager, mActivity.getApplicationContext());// TODO -- YARDEN
        updateDataBase.start();
    }

    public void disconnect( )
    {
        if(netId != 0)
             m_wifiManager.removeNetwork(netId);
        enableWifi(false);
    }

    public void SettingProvider()
    {
        String[] parts = message.split("/");
        providerUser = new User();
        providerUser.setSsid(parts[0]);
        providerUser.setHotspotPassword(parts[1]);
        providerUser.setGetFirebaseUidProvider(parts[2]);
        providerUser.setNetId(netId);
        clientUser = new User();
    }


    public String getSSID(){
        return providerUser.getSsid();
    }

    @Override
    public void handelMessage(String msg) {
        message = msg;
        SettingProvider();

    }

    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                List<ScanResult> mScanResults = m_wifiManager.getScanResults();

            }
        }
    };


    @Override
    public void StartConnection() {
        run();
    }
}