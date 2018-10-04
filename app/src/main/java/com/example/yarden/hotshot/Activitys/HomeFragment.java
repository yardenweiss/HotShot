package com.example.yarden.hotshot.Activitys;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.yarden.hotshot.Client.AskForWifi;
import com.example.yarden.hotshot.Provider.ShareWifi;
import com.example.yarden.hotshot.R;
import com.example.yarden.hotshot.SendReceive;
import com.example.yarden.hotshot.Utils.P2PWifi;
import com.example.yarden.hotshot.Utils.User;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.InvocationTargetException;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class HomeFragment extends Fragment {
    private WifiManager wifiManager;
    private WifiP2pManager wifiP2pManager;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private SendReceive sendReceive;
    private P2PWifi p2PWifi;
    private AskForWifi getWifi;
    private ShareWifi shareWifi;
    private FirebaseUser currectUser;
    private MainActivity mainActivity;

   @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       mainActivity = (MainActivity)getActivity();
        return inflater.inflate(R.layout.fragment_home , container , false);
    }

     private void init()
    {
        wifiManager= (WifiManager) mainActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiP2pManager = (WifiP2pManager) mainActivity.getSystemService(Context.WIFI_P2P_SERVICE);
        p2PWifi = new P2PWifi(mainActivity.getApplicationContext(),(MainActivity) mainActivity , wifiP2pManager);
        try {
            p2PWifi.initialWork();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        User user = new User(currectUser);
        getWifi = new AskForWifi(p2PWifi , wifiManager );
        shareWifi = new ShareWifi(p2PWifi , wifiManager , user);

        FloatingActionButton fab_getWifi = (FloatingActionButton) mainActivity.findViewById(R.id.fab_getWifi);
        fab_getWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Searching for available Wifi",
                        Toast.LENGTH_LONG).show();
                getWifi.GetWifi();
            }
        });

        FloatingActionButton fab_shareWifi = (FloatingActionButton) mainActivity.findViewById(R.id.fab_shareWifi);
        fab_shareWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(shareWifi.CheckSetting()) {
                    Snackbar.make(view, "Searching for users", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    shareWifi.ShareWifi();
                }
                else
                {
                    Snackbar.make(view, "Password not available", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    //Intent profileIntent = new Intent(MainActivity.this , ProfileFragment.class);
                    //profileIntent.putExtra("FillPassword", true);
               //     startActivity(profileIntent);
                }
            }
        });

    }


}
