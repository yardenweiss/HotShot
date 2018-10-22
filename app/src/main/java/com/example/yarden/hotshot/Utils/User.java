package com.example.yarden.hotshot.Utils;

import com.google.firebase.auth.FirebaseUser;

public class User {

    private String ssid;
    private String hotspotPassword;
    private String firebaseUid;
    private String firebaseUidProvider;
    private FirebaseUser firebaseUser ;
    private float totalUsageGet;
    private float totalUsageShare;

    public String GetFirebaseUserUid() {
        firebaseUid = FirebaseInstances.getUid();
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
