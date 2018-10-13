package com.example.yarden.hotshot.Utils;



import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;

import static android.content.ContentValues.TAG;
import static android.net.wifi.WifiManager.WIFI_STATE_ENABLED;

public class DataUsage {
    private WifiManager wifiManager;
    private long startRecive;
    private long  startDown  ;
    private float totalDateUsageMb = 0;

    public DataUsage(WifiManager _wifiManager){wifiManager=_wifiManager;}

    public void StartCountDataUsage(  ) {
        startRecive = TrafficStats.getTotalRxBytes();
        startDown = TrafficStats.getTotalTxBytes();

    }

    public float GetTotalMb(){
        return totalDateUsageMb;
    }

    public float GetStateOfUsage(){
        long recive = TrafficStats.getTotalRxBytes() - startRecive;
        long dwon = TrafficStats.getTotalTxBytes() - startDown;
        float dataUsage = (float)(recive + dwon)/(1024*1024);
        Log.i(TAG,"delta  = "+(float)dataUsage/(1024*1024));
        totalDateUsageMb += dataUsage;
        return dataUsage;
    }
}
