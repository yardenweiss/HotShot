package com.example.yarden.hotshot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.yarden.hotshot.Client.ClientManager;
import com.example.yarden.hotshot.Fragments.HomeFragment;
import com.example.yarden.hotshot.Fragments.MyActivityFragment;
import com.example.yarden.hotshot.Fragments.ProfileFragment;
import com.example.yarden.hotshot.Provider.ShareWifi;
import com.example.yarden.hotshot.Utils.P2PWifi;
import com.example.yarden.hotshot.Utils.User;
import com.example.yarden.hotshot.Utils.WifiClientBroadcastReceiver;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private Context mContext;

    // P2P variables
    private WifiP2pManager.Channel mChannel;
    private WifiManager mWifiManager;
    private WifiP2pManager mWifiP2pManager;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private P2PWifi p2PWifi;

    // Client/Server Variables
    private ClientManager getWifi;
    private ShareWifi shareWifi;
    private FirebaseUser currectUser;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = getApplicationContext();
        initialPTPWork();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        FirebaseAuth auth = FirebaseAuth.getInstance();
        //startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
        //       Arrays.asList( new AuthUI.IdpConfig.EmailBuilder().build(),
        //               new AuthUI.IdpConfig.GoogleBuilder().build()))
        //      .build() , 1);


        if(savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container ,
                    new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    public WifiManager getWifiManager() {
        return mWifiManager;
    }

    public P2PWifi getP2PWifi() {
        return p2PWifi;
    }

    private void initialPTPWork(){

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(getApplicationContext().WIFI_SERVICE);
        mWifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mWifiP2pManager.initialize(this,getMainLooper(),null);

        p2PWifi = new P2PWifi(mContext, this, mWifiP2pManager, mChannel);

        mIntentFilter=new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mReceiver = new WifiClientBroadcastReceiver(mWifiP2pManager, mChannel,this);
        registerReceiver(mReceiver, mIntentFilter);
        mWifiManager.setWifiEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mWifiP2pManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i) {

            }
        });
        mWifiManager.setWifiEnabled(false);
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
                       new ProfileFragment(user)).commit();
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