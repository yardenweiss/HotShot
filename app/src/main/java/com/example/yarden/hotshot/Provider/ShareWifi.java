package com.example.yarden.hotshot.Provider;


import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ArrayAdapter;


import com.example.yarden.hotshot.Utils.FirebaseInstances;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ShareWifi {

    private WifiManager m_wifiManager;
    private WifiConfiguration m_wifiConf;
    private ArrayAdapter<String> mPeersStringAdapter;
    private String message;
    private String uid;
    private boolean hotspotInfo = false;


    public ShareWifi(WifiManager wifiManager) {
        m_wifiConf = new WifiConfiguration();
        m_wifiManager = wifiManager;
        databaseUpdate();
    }


    public void enableWifi() {
        if (!m_wifiManager.isWifiEnabled())
            m_wifiManager.setWifiEnabled(true);
    }


    public String getMessage() {
       String tempMsg;
        if(!hotspotInfo)
         tempMsg =  getHotspotInfo() ;
        else
            tempMsg = message;
        return tempMsg + '/' + FirebaseInstances.getUid();

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

   public boolean getSettingStatus(){
        return hotspotInfo;
    }

    private void databaseUpdate() {
        DatabaseReference myRef = FirebaseInstances.getDatabaseRef();
         uid = FirebaseInstances.getUid();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(uid).child("Password").exists() && dataSnapshot.child(uid).child("SSID").exists()) {
                    String password = dataSnapshot.child(uid).child("Password").getValue(String.class);
                    String ssid = dataSnapshot.child(uid).child("SSID").getValue(String.class);
                    message = ssid + '/' + password + '/';
                    hotspotInfo = true;
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
}