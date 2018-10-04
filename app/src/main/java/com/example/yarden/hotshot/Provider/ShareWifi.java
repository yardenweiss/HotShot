package com.example.yarden.hotshot.Provider;


import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;


import com.example.yarden.hotshot.Utils.P2PWifi;
import com.example.yarden.hotshot.Utils.User;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static android.app.PendingIntent.getActivity;

public class ShareWifi   {

    private P2PWifi p2pWifi;
    private  WifiP2pManager mManager;
    private WifiManager m_wifiManager;
    private WifiConfiguration m_wifiConf;
    private static Method getWifiApState;
    private User user;
    private int Time_Out=10;


    static {
        // lookup methods and fields not defined publicly in the SDK.
        Class<?> cls = WifiManager.class;
        for (Method method : cls.getDeclaredMethods()) {
            String methodName = method.getName();
            if (methodName.equals("getWifiApState")) {
                getWifiApState = method;
            }
        }
    }

    public ShareWifi(P2PWifi _p2pWifi, WifiManager wifiManager, User _user)
    {
        m_wifiConf = new WifiConfiguration();
        p2pWifi = _p2pWifi;
       // user = _user;
        m_wifiManager= wifiManager;
        user = _user;
    }

    public boolean GetHotspotStatus() {
          int AP_STATE_DISABLING = 10;
          int AP_STATE_DISABLED = 11;
          int AP_STATE_ENABLING = 12;
          int AP_STATE_ENABLED = 13;
          int AP_STATE_FAILED = 14;
        int actualState = 0;


        getWifiApState.setAccessible(true);
        try {
            actualState = (Integer) getWifiApState.invoke(m_wifiManager, (Object[]) null);
            String str = "" + actualState;//just for debuging
            Log.d("actualState", str); //just for debuging
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        if(actualState != AP_STATE_ENABLING &&  actualState != AP_STATE_ENABLED) // HotSpot Not available
            return false;
        else
            return true;

    }

    private void enableWifi(){
        if (!m_wifiManager.isWifiEnabled())
            m_wifiManager.setWifiEnabled(true);
    }

    public boolean ShareWifi() {
        int count = 0;
        boolean findClient = false;
        try {
            while (count == Time_Out) {
                wait(2000);
                if (p2pWifi.GetAnswerMsg() == "Hi") {
                    p2pWifi.WriteMessege("HT37--12345678");//need to send also firebaseuser id
                    findClient = true;
                    break;
                }
                //trun on HotSpot!
                count++;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            return findClient;
        }

    }

    public boolean CheckSetting()
    {
        if(user.GetHotsptPass() != null)
        {
            return true;
        }
        else return false;
    }
}
