package com.example.yarden.hotshot.Utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.example.yarden.hotshot.Client.DataSaveLocaly;
import com.example.yarden.hotshot.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.firebase.ui.auth.AuthUI.TAG;

public class UpdateDataBase extends Thread {
    private String uidClient;
    private User userProvider;
    private User userClient;
    private DataUsage dataUsage;
    private WifiManager wifiManager;
    private DatabaseReference databaseRef;
    private Date date;
    private float totalUsageGet =0;
    private float totalUsageShare =0;
    private boolean StartState = true;
    private boolean NotAllowToGetWifi = false;
    private ArrayList<INotifyEndOfUsage> notifyEndOfUsagesListeners = new ArrayList<INotifyEndOfUsage>();
    private DataSaveLocaly dataSaveLocaly;
    private Context context;

    public UpdateDataBase(User _userProvider, User _userClient ,WifiManager _wifiManager, Context _context) {
        wifiManager = _wifiManager;
        dataUsage = new DataUsage(wifiManager);
        context = _context;
        dataUsage.StartCountDataUsage();
        userProvider = _userProvider;
        userClient = _userClient;
        dataSaveLocaly = new DataSaveLocaly(context);
        databaseRef = FirebaseInstances.getDatabaseRef();
        GetUpdateFromDatabase();
    }

    @Override
    public void run() {
        uidClient = FirebaseInstances.getUid();
        //move to ctor
        dataUsage.StartCountDataUsage();
        float mb = 0;
        dataUsage.StartCountDataUsage();
        date = new Date();
        Format formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        String dateFormat = formatter.format(date);
        WifiInfo info = wifiManager.getConnectionInfo();
        String ssid = info.getSSID();

        while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED || wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            try {
                Thread.sleep(500);
                mb = dataUsage.GetStateOfUsage();
                databaseRef.child(userProvider.getFirebaseUidProvider()).child(dateFormat).child("shareWifi").setValue(mb);
                databaseRef.child(uidClient).child(dateFormat).child("getWifi").setValue(mb);
                if(!StartState){
                    databaseRef.child(uidClient).child("TotalGetGB").setValue(totalUsageGet - (mb/1024));
                    databaseRef.child(userProvider.getFirebaseUidProvider()).child("TotalProviedGB").setValue(totalUsageShare + (mb/1024));
                    if(totalUsageGet - (mb/1024) < 0.2)
                        MainActivity.ShowNotification(context);
                }
            } catch (InterruptedException e) {

                e.printStackTrace();
            }
        }
        Float mbString = mb;
        dataSaveLocaly.writeToFile(mbString.toString());
        wifiManager.removeNetwork(userProvider.getNetId());
    }

     private void GetUpdateFromDatabase(){
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (StartState) {
                    if(dataSnapshot.child(uidClient).child("TotalGetGB").exists()) {
                        totalUsageGet = dataSnapshot.child(uidClient).child("TotalGetGB").getValue(Float.class);
                        StartState = false;
                    }
                    if(dataSnapshot.child(userProvider.GetFirebaseUserUid()).child("TotalProviedGB").exists()) {
                        totalUsageShare = dataSnapshot.child(userProvider.getFirebaseUidProvider()).child("TotalProviedGB").getValue(Float.class);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void notifyAllListeners(){
        for(INotifyEndOfUsage listener: notifyEndOfUsagesListeners){
            listener.ShowNotification();
        }
    }

    public void setEventListener(INotifyEndOfUsage i_listener){
        notifyEndOfUsagesListeners.add(i_listener);
    }
}