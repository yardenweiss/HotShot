package com.example.yarden.hotshot.Provider;


import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.widget.ArrayAdapter;


import com.example.yarden.hotshot.Utils.FirebaseAuthInstance;
import com.example.yarden.hotshot.Utils.P2PWifi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static android.app.PendingIntent.getActivity;

public class ShareWifi  implements IPeersEventListener, IConnectionEstablishedInterface {

    private P2PWifi p2pWifi;
    private WifiManager m_wifiManager;
    private WifiConfiguration m_wifiConf;
    private ArrayList<IPeersEventListener> mPeersEventListeners;
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
        for(IPeersEventListener listener: mPeersEventListeners){
            listener.OnPeersAppearEvent(mPeersStringAdapter);
        }
    }

    public void setPeerEventListener(IPeersEventListener i_listener){
        mPeersEventListeners.add(i_listener);
    }

    public String HotSpotConnectionInfo(){
        return message + FirebaseAuthInstance.getUid();

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