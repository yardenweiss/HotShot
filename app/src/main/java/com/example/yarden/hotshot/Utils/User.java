package com.example.yarden.hotshot.Utils;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;

public class User implements Serializable{

    private String HotspotPass = null;
    private String ssid;
   private FirebaseUser firebaseUser;


    public User(FirebaseUser _firebaseUser ){
        firebaseUser = _firebaseUser;

    }

    public void SetPassword(String password)
    {
        HotspotPass = password;
    }

    public String GetHotsptPass() {
        return HotspotPass;
    }

    public String GetEmail(){
        return firebaseUser.getEmail();
    }

    public void UpdateEmail(String email){
        firebaseUser.updateEmail(email);
    }

    public void UpdatePassOfEmail(String pass){
        firebaseUser.updatePassword(pass);
    }
}
