package com.example.yarden.hotshot.Utils.WifiDirecct;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import com.example.yarden.hotshot.Fragments.HomeFragment;
import com.example.yarden.hotshot.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ash on 14/2/18.
 */

public class MyPeerListener implements WifiP2pManager.PeerListListener {
    public static final String TAG = "===MyPeerListener";
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    private HomeFragment homeFragment;

    public MyPeerListener(HomeFragment ihomeFragment) {
        homeFragment = ihomeFragment;
        Log.d(MyPeerListener.TAG,"MyPeerListener object created");

    }


    @Override
    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {

        ArrayList<WifiP2pDevice> deviceDetails = new ArrayList<>();

        Log.d(MyPeerListener.TAG, "OnPeerAvailable()");
        if(wifiP2pDeviceList != null ) {

            if(wifiP2pDeviceList.getDeviceList().size() == 0) {
                Log.d(MyPeerListener.TAG, "wifiP2pDeviceList size is zero");
                return;
            }
            Log.d(MainActivity.TAG,"");
            for (WifiP2pDevice device : wifiP2pDeviceList.getDeviceList()) {
                deviceDetails.add(device);
                Log.d(MyPeerListener.TAG, "Found device :" + device.deviceName + " " + device.deviceAddress);
            }
            if(homeFragment != null) {
                homeFragment.setDeviceList(deviceDetails);
            }

        }
        else {
            Log.d(MyPeerListener.TAG, "wifiP2pDeviceList is null");

        }
    }
}
