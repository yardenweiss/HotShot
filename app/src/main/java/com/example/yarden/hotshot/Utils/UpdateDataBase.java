package com.example.yarden.hotshot.Utils;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

import static com.firebase.ui.auth.AuthUI.TAG;

public class UpdateDataBase extends Thread {
    private String uidClient;
    private User userProvider;
    private User userClient;
    private DataUsage dataUsage;
    private WifiManager wifiManager;
    private DatabaseReference databaseRef;
    private Date date;

    public UpdateDataBase(User _userProvider, User _userClient ,WifiManager _wifiManager) {
        wifiManager = _wifiManager;
        dataUsage = new DataUsage(wifiManager);
        dataUsage.StartCountDataUsage();
        uidClient = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userProvider = _userProvider;
        userClient = _userClient;
        databaseRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void run() {

        dataUsage.StartCountDataUsage();
        float mb = 0;
        dataUsage.StartCountDataUsage();
        date = new Date();
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateFormat = formatter.format(date);
        WifiInfo info = wifiManager.getConnectionInfo();
        String ssid = info.getSSID();

        while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED || wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            try {
                Thread.sleep(500);
                mb = dataUsage.GetStateOfUsage();
                databaseRef.child(userProvider.GetFirebaseUserUid()).child(dateFormat).child("shareWifi").setValue(mb);
                databaseRef.child(uidClient).child(dateFormat).child("getWifi").setValue(mb);
            } catch (InterruptedException e) {

                e.printStackTrace();
            }
        }

    }


}