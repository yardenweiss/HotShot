package com.example.yarden.hotshot.Activitys;

import android.arch.lifecycle.HolderFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.yarden.hotshot.Client.AskForWifi;
import com.example.yarden.hotshot.Utils.P2PWifi;
import com.example.yarden.hotshot.Provider.ShareWifi;
import com.example.yarden.hotshot.R;
import com.example.yarden.hotshot.SendReceive;
import com.example.yarden.hotshot.Utils.P2PWifi;
import com.example.yarden.hotshot.Utils.User;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private WifiManager wifiManager;
    private WifiP2pManager wifiP2pManager;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private SendReceive sendReceive;
    private P2PWifi p2PWifi;
    private AskForWifi getWifi;
    private ShareWifi shareWifi;
    private FirebaseUser currectUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


         drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        FirebaseAuth auth = FirebaseAuth.getInstance();
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
                Arrays.asList( new AuthUI.IdpConfig.EmailBuilder().build(),
                        new AuthUI.IdpConfig.GoogleBuilder().build()))
                .build() , 1);
        currectUser = auth.getCurrentUser();

        if(savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container ,
                    new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }





    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@Nullable MenuItem item) {
        // Handle navigation view item clicks here.
       switch (item.getItemId()){
           case R.id.nav_home:
               getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container ,
                       new HomeFragment()).commit();
               break;
           case R.id.nav_profile:
               getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container ,
                       new ProfileFragment()).commit();
               break;
           case R.id.nav_myactivity:
               getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container ,
                       new MyActivityFragment()).commit();
               break;
       }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
