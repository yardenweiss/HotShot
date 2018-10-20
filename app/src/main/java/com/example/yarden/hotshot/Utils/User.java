package com.example.yarden.hotshot.Utils;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class User {

    private String ssid;
    private String hotspotPassword;
    private String firebaseUid;
    private String firebaseUidProvider;
    private FirebaseUser firebaseUser ;

    public String GetFirebaseUserUid() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseUid= firebaseUser.getUid();
        return firebaseUid;
    }

    public void setGetFirebaseUidProvider(String uid)
    {
        firebaseUidProvider = uid;
    }

    public String getFirebaseUidProvider() {
        return firebaseUidProvider;
    }

    public void setSsid(String _ssid)
    {
        ssid = _ssid;
    }

    public String getSsid() {
        return ssid;
    }

    public void setHotspotPassword(String hotspotPassword) {
        this.hotspotPassword = hotspotPassword;
    }

    public String getHotspotPassword() {
        return hotspotPassword;
    }





}
