package com.example.yarden.hotshot.Utils.WifiDirecct;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import com.example.yarden.hotshot.MainActivity;

import java.util.List;

public class WifiBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = "WifiBReceiver";

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainActivity mActivity;

    private boolean IsSSIDPassed = false;
    private String SSID;

    public WifiBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       MainActivity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
    }


    public void setSSID(String SSID) {
        IsSSIDPassed = true;
        this.SSID = SSID;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Log.d(WifiBroadcastReceiver.TAG,"WIFI P2P ENABLED");
            } else {
                Log.d(WifiBroadcastReceiver.TAG,"WIFI P2P NOT ENABLED");
            }

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            Log.d(WifiBroadcastReceiver.TAG,"WIFI_P2P_PEERS_CHANGED_ACTION");
            if (mManager != null) {
                MyPeerListener myPeerListener = new MyPeerListener(mActivity.getHomeFragment());
                mManager.requestPeers(mChannel, myPeerListener);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            if (mManager == null) {
                return;
            }
            NetworkInfo networkInfo = intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            //WifiP2pInfo p2pInfo = intent
            //        .getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);

            //if (p2pInfo != null && p2pInfo.groupOwnerAddress != null) {
            //    String goAddress = Utils.getDottedDecimalIP(p2pInfo.groupOwnerAddress
            //            .getAddress());
            //    boolean isGroupOwner = p2pInfo.isGroupOwner;
           //     Log.d(WifiBroadcastReceiver.TAG,"I am a group owner");
           // }
            if (networkInfo.isConnected()) {

                // we are connected with the other device, request connection
                // info to find group owner IP
                //mManager.requestConnectionInfo(mChannel, mActivity);
            } else {
                // It's a disconnect
                Log.d(WifiBroadcastReceiver.TAG,"Its a disconnect");


                //activity.resetData();
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            Log.d(WifiBroadcastReceiver.TAG,"WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
            // Respond to this device's wifi state changing


        } else if(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION.equals(action)) {

            int state = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, 10000);
            if( state == WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED ) {
                Toast.makeText(mActivity.getApplicationContext(), "Discovery Started",Toast.LENGTH_SHORT);
            } else if(state == WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED) {
                Toast.makeText(mActivity.getApplicationContext(), "Discovery Stopped",Toast.LENGTH_SHORT);
            }
        }
        else if(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
            if (IsSSIDPassed) {
                List<ScanResult> results = mActivity.getmWifiManager().getScanResults();
                for(ScanResult current : results){
                    if(current.SSID == SSID){
                        mActivity.WifiFound();
                    }
                }
            }
        }
    }
}
