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
    private float totalUage =0;

    public UpdateDataBase(User _userProvider, User _userClient ,WifiManager _wifiManager) {
        wifiManager = _wifiManager;
        dataUsage = new DataUsage(wifiManager);
        dataUsage.StartCountDataUsage();
        uidClient = FirebaseAuthInstance.getUid();
        userProvider = _userProvider;
        userClient = _userClient;
        databaseRef = FirebaseAuthInstance.getDatabaseRef();
        updateDatabase();
    }

    @Override
    public void run() {
        dataUsage.StartCountDataUsage();
        float mb = 0;
        dataUsage.StartCountDataUsage();
        date = new Date();
        Format formatter = new SimpleDateFormat("yy-mm-dd hh:mm");
        String dateFormat = formatter.format(date);
        WifiInfo info = wifiManager.getConnectionInfo();
        String ssid = info.getSSID();

        while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED || wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            try {
                Thread.sleep(500);
                mb = dataUsage.GetStateOfUsage();
                totalUage += mb;
                databaseRef.child(userProvider.GetFirebaseUserUid()).child(dateFormat).child("shareWifi").setValue(mb);
                databaseRef.child(uidClient).child(dateFormat).child("getWifi").setValue(mb);
//update


            } catch (InterruptedException e) {

                e.printStackTrace();
            }
        }

    }

 private void updateDatabase(){
        try {
            databaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //update client data
                    Float mbGet = dataSnapshot.child(uidClient).child("TotalGetMB").getValue(Float.class);
                    if(mbGet!=null) {
                        mbGet -= totalUage;
                        databaseRef.child(uidClient).child("TotalGetMB").setValue(mbGet);
                    }
                    //update provider data
                    Float mbShare = dataSnapshot.child(userProvider.getFirebaseUidProvider()).child("TotalProviedMB").getValue(Float.class);
                    if(mbShare!=null) {
                        mbShare -= totalUage;
                        databaseRef.child(userProvider.getFirebaseUidProvider()).child("TotalProviedMB").setValue(mbShare);
                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }



}