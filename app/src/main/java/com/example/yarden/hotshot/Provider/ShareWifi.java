package com.example.yarden.hotshot.Provider;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;


import com.example.yarden.hotshot.Utils.P2PWifi;
import com.example.yarden.hotshot.Utils.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static android.app.PendingIntent.getActivity;

public class ShareWifi  implements PeersEventListener, ConnectionEstablishedInterface   {

    private P2PWifi p2pWifi;
    private WifiManager m_wifiManager;
    private WifiConfiguration m_wifiConf;
    private ArrayList<PeersEventListener> mPeersEventListeners;
    private ArrayAdapter<String> mPeersStringAdapter;
    private String message;


    public ShareWifi(P2PWifi _p2pWifi, WifiManager wifiManager )
    {
        m_wifiConf = new WifiConfiguration();
        p2pWifi = _p2pWifi;
        m_wifiManager= wifiManager;
        mPeersEventListeners = new ArrayList<>();
        p2pWifi.setPeerEventListener(this);
        p2pWifi.setConnectionEstablishedEventListeners(this);
        getHotspotInfo();
    }



    private void enableWifi(){
        if (!m_wifiManager.isWifiEnabled())
            m_wifiManager.setWifiEnabled(true);
    }



    @Override
    public void OnPeersAppearEvent(ArrayAdapter<String> adapter) {
        mPeersStringAdapter = adapter;
        notifyAllListeners();
    }

    private void notifyAllListeners(){
        for(PeersEventListener listener: mPeersEventListeners){
            listener.OnPeersAppearEvent(mPeersStringAdapter);
        }
    }

    public void setPeerEventListener(PeersEventListener i_listener){
        mPeersEventListeners.add(i_listener);
    }

    public String HotSpotConnectionInfo(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        return message +  firebaseUser.getUid();

    }


    @Override
    public void SendInfo(P2PWifi.SendReceive i_sendReceive) {
        p2pWifi.WriteMessege(HotSpotConnectionInfo());
    }


public void getHotspotInfo(){

    Method[] methods = m_wifiManager.getClass().getDeclaredMethods();
    for (Method m: methods) {
        if (m.getName().equals("getWifiApConfiguration")) {
            try {
                m_wifiConf = (WifiConfiguration)m.invoke(m_wifiManager);
                message = m_wifiConf.SSID + '/' + m_wifiConf.preSharedKey +'/';
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }
    }
}

}