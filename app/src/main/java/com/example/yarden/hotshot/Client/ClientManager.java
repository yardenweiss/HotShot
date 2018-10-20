package com.example.yarden.hotshot.Client;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;


import com.example.yarden.hotshot.Utils.P2PWifi;
import com.example.yarden.hotshot.Utils.UpdateDataBase;
import com.example.yarden.hotshot.Utils.User;
import java.util.Calendar;
import java.util.List;

public class ClientManager implements IClientReciveEventListener {

    private P2PWifi p2pWifi;
    private  WifiP2pManager mManager;
    private WifiManager m_wifiManager;
    private WifiConfiguration m_wifiConf;
    private User providerUser;
    private Context context;
    private User clientUser;
    private String message;
    private  int netId;
    private static final int MINUTE_5 = 300;

    public ClientManager(P2PWifi _p2pWifi, WifiManager wifiManager, Context _context)
    {
        m_wifiManager = wifiManager;
        m_wifiConf = new WifiConfiguration();
        p2pWifi = _p2pWifi;
        context = _context;
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

    private void enableWifi() {
        if (!m_wifiManager.isWifiEnabled())
            m_wifiManager.setWifiEnabled(true);
    }


    public void run()
    {
        SettingProvider();
        checkIfHotspotAvailable();
        connectToWifi();
        UpdateDataBase updateDataBase = new UpdateDataBase(providerUser ,clientUser, m_wifiManager, context);
        updateDataBase.start();
    }

    public void disconnect(){
        m_wifiManager.removeNetwork(netId);
    }

    public void SettingProvider()
    {
        String[] parts = message.split("/");
        providerUser = new User();
        providerUser.setSsid(parts[0]);
        providerUser.setHotspotPassword(parts[1]);
        providerUser.setGetFirebaseUidProvider(parts[2]);
        clientUser = new User();
    }

    public boolean checkIfHotspotAvailable(){//TODO check hotspot availble

        boolean find =false;
        Calendar currentDate = Calendar.getInstance();
        Calendar timeout = Calendar.getInstance();
        timeout.add(Calendar.MINUTE, 1);

        while(timeout.compareTo(currentDate) >= 0) //אafter 2 min timeout
        {
            currentDate = Calendar.getInstance();
        }
        return true;

    }

    @Override
    public void handelMessage(String msg) {
        message = msg;
        p2pWifi.WriteConfirmation("MassegeRecived!!!");
        run();
    }

    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                List<ScanResult> mScanResults = m_wifiManager.getScanResults();

            }
        }
    };



}