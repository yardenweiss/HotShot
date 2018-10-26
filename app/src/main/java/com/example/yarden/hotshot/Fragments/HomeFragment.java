package com.example.yarden.hotshot.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yarden.hotshot.Client.ClientManager;
import com.example.yarden.hotshot.Client.DataSaveLocaly;
import com.example.yarden.hotshot.MainActivity;
import com.example.yarden.hotshot.Provider.ShareWifi;
import com.example.yarden.hotshot.R;
import com.example.yarden.hotshot.Utils.UpdateDataBase;
import com.example.yarden.hotshot.Utils.User;
import com.example.yarden.hotshot.Utils.WifiDirecct.ClientSocket;
import com.example.yarden.hotshot.Utils.WifiDirecct.MyPeerListener;
import com.example.yarden.hotshot.Utils.WifiDirecct.ServerSocketThread;
import com.example.yarden.hotshot.Utils.WifiDirecct.ServiceDiscovery;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class HomeFragment extends Fragment implements WifiP2pManager.ConnectionInfoListener,View.OnClickListener {
    private  WifiP2pManager mManager;
    private  WifiP2pManager.Channel mChannel;
    private  WifiP2pDevice device;
    private  MainActivity mainActivity;
    private  ClientManager getWifi;
    private  ShareWifi shareWifi;
    private  HomeFragment homeFragment;
    private  ServiceDiscovery serviceDisvcoery;
    private  ServerSocketThread serverSocketThread;
    private  ArrayAdapter mAdapter;
    private  WifiP2pDevice[] deviceListItems;
    private  static final int MY_PERMISSION_CODE = 100;
    private  boolean mIsClient = false;
    private  boolean mPermissionsGranted;
    private  static boolean  stateDiscovery = false;
    private  static boolean stateWifi = false;
    private  static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;

    public static boolean stateConnection = false;
    public static String IP = null;
    public static boolean IS_OWNER = false;

    ImageButton buttonGetWifi;
    ImageButton buttonShareWifi;
    Button buttonDiscoveryStop;
    Button buttonConnect;
    Button buttonServerStart;
    Button buttonClientStart;
    Button buttonClientStop;
    Button buttonServerStop;
    Button buttonConfigure;
    Button buttonConnectHotSpot;
    ListView listViewDevices;
    TextView textViewDiscoveryStatus;
    TextView textViewWifiP2PStatus;
    TextView textViewConnectionStatus;
    TextView textViewReceivedDataStatus;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home , container , false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = (MainActivity)getActivity();
        init();
    }

    private void init()
    {
        try {
            homeFragment = this;
            serviceDisvcoery = new ServiceDiscovery();
            mManager = (WifiP2pManager) mainActivity.getSystemService(Context.WIFI_P2P_SERVICE);
            mChannel = mainActivity.getmChannel();
            getWifi = new ClientManager(mainActivity.getmWifiManager(), mainActivity);
            shareWifi = new ShareWifi(mainActivity.getmWifiManager());
            setUpUI();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void setUpUI() {
        buttonGetWifi = mainActivity.findViewById(R.id.get_wifi);
        buttonShareWifi = mainActivity.findViewById(R.id.share_wifi);
        buttonConnect = mainActivity.findViewById(R.id.main_activity_button_connect);
        buttonServerStart = mainActivity.findViewById(R.id.main_activity_button_server_start);
        buttonServerStop = mainActivity.findViewById(R.id.main_activity_button_server_stop);
        buttonClientStart = mainActivity.findViewById(R.id.main_activity_button_client_start);
        buttonClientStop = mainActivity.findViewById(R.id.main_activity_button_client_stop);
        buttonConfigure = mainActivity.findViewById(R.id.main_activity_button_configure);
        listViewDevices = mainActivity.findViewById(R.id.main_activity_list_view_devices);
        textViewReceivedDataStatus = mainActivity.findViewById(R.id.main_acitivity_received_data);
        buttonConnectHotSpot = mainActivity.findViewById(R.id.main_activity_button_connect_hot);

        buttonConnectHotSpot.setOnClickListener(this);
        buttonServerStart.setOnClickListener(this);
        buttonServerStop.setOnClickListener(this);
        buttonClientStart.setOnClickListener(this);
        buttonClientStop.setOnClickListener(this);
        buttonConnect.setOnClickListener(this);
        buttonGetWifi.setOnClickListener(this);
        buttonShareWifi.setOnClickListener(this);
        buttonConfigure.setOnClickListener(this);

        buttonConnect.setVisibility(View.INVISIBLE);
        buttonConfigure.setVisibility(View.INVISIBLE);
        buttonConnectHotSpot.setVisibility(View.INVISIBLE);
        buttonClientStop.setVisibility(View.INVISIBLE);
        buttonClientStart.setVisibility(View.INVISIBLE);
        buttonServerStop.setVisibility(View.INVISIBLE);
        buttonServerStart.setVisibility(View.INVISIBLE);



        listViewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                device = deviceListItems[i];
                Toast.makeText(mainActivity.getApplicationContext(),"Selected device :"+ device.deviceName ,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean isClient() {
        return mIsClient;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.get_wifi:
               if(checkSettingForClient()) {
                   mIsClient = true;
                   if(!stateDiscovery) {
                  //  changeName("Client");
                    checkPermissionsAndAction();
                    buttonConnect.setVisibility(View.VISIBLE);
                }
                else
                    makeToast("U dont have any mb to use");
             }
              break;
            case R.id.share_wifi:
                if(!stateDiscovery) {
                    changeName("Provider");
                    checkPermissionsAndAction();
                    buttonConfigure.setVisibility(View.VISIBLE);
                    listViewDevices.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.main_activity_button_connect:

                if(device == null) {
                    makeToast("Please discover and select a device");
                    return;
                }
                connect(device);
                break;
            case R.id.main_activity_button_server_start:
                startServer();
                break;
            case R.id.main_activity_button_server_stop:
                if(serverSocketThread != null) {
                    serverSocketThread.setInterrupted(true);
                } else {
                    Log.d(MainActivity.TAG,"serverSocketThread is null");
                }
                //makeToast("Yet to do...");
                break;
            case R.id.main_activity_button_client_start:
                //serviceDisvcoery.startRegistrationAndDiscovery(mManager,mChannel);
                ClientStart();
                break;
            case R.id.main_activity_button_configure:
                mManager.requestConnectionInfo(mChannel,this);
                break;
            case R.id.main_activity_button_client_stop:
                makeToast("Yet to do");
                break;
            case R.id.main_activity_button_connect_hot:
                getWifi.run();
                break;
            default:
                break;
        }
    }

    private void ClientStart(){
        String dataToSend = shareWifi.getMessage();
        ClientSocket clientSocket = new ClientSocket(mainActivity.getApplicationContext(),this,dataToSend);
        clientSocket.execute();
        showAlart();
    }

    public void makeToast(String msg) {
        Toast.makeText(mainActivity.getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }

    private void discoverPeers()
    {
        Log.d(MainActivity.TAG,"discoverPeers()");
        setDeviceList(new ArrayList<WifiP2pDevice>());
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                stateDiscovery = true;
                Log.d(MainActivity.TAG,"peer discovery started");
                makeToast("peer discovery started");
                MyPeerListener myPeerListener = new MyPeerListener(homeFragment);
                mManager.requestPeers(mChannel,myPeerListener);

            }

            @Override
            public void onFailure(int i) {
                stateDiscovery = false;
                if (i == WifiP2pManager.P2P_UNSUPPORTED) {
                    Log.d(MainActivity.TAG," peer discovery failed :" + "P2P_UNSUPPORTED");
                    makeToast(" peer discovery failed :" + "P2P_UNSUPPORTED");

                } else if (i == WifiP2pManager.ERROR) {
                    Log.d(MainActivity.TAG," peer discovery failed :" + "ERROR");
                    makeToast(" peer discovery failed :" + "ERROR");

                } else if (i == WifiP2pManager.BUSY) {
                    Log.d(MainActivity.TAG," peer discovery failed :" + "BUSY");
                    makeToast(" peer discovery failed :" + "BUSY");
                }
            }
        });
    }

    public void setDeviceList(ArrayList<WifiP2pDevice> deviceDetails) {

        deviceListItems = new WifiP2pDevice[deviceDetails.size()];
        String[] deviceNames = new String[deviceDetails.size()];
        for(int i=0 ;i< deviceDetails.size(); i++){
            deviceNames[i] = deviceDetails.get(i).deviceName;
            deviceListItems[i] = deviceDetails.get(i);
        }
        mAdapter = new ArrayAdapter(mainActivity.getApplicationContext(),android.R.layout.simple_list_item_1,android.R.id.text1,deviceNames);
        listViewDevices.setAdapter(mAdapter);
    }

    private void stopPeerDiscover() {
        mManager.stopPeerDiscovery(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                stateDiscovery = false;
                Log.d(MainActivity.TAG,"Peer Discovery stopped");
                makeToast("Peer Discovery stopped" );
                //buttonDiscoveryStop.setEnabled(false);

            }

            @Override
            public void onFailure(int i) {
                Log.d(MainActivity.TAG,"Stopping Peer Discovery failed");
                makeToast("Stopping Peer Discovery failed" );
                //buttonDiscoveryStop.setEnabled(true);

            }
        });

    }

    public void connect (final WifiP2pDevice device) {
        // Picking the first device found on the network.

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        Log.d(MainActivity.TAG,"Trying to connect : " +device.deviceName);
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d(MainActivity.TAG, "Connected to :" + device.deviceName);
                makeToast("Connection successful with " + device.deviceName);
                //setDeviceList(new ArrayList<WifiP2pDevice>());
            }

            @Override
            public void onFailure(int reason) {
                if(reason == WifiP2pManager.P2P_UNSUPPORTED) {
                    Log.d(MainActivity.TAG, "P2P_UNSUPPORTED");
                    makeToast("Failed establishing connection: " + "P2P_UNSUPPORTED");
                }
                else if( reason == WifiP2pManager.ERROR) {
                    Log.d(MainActivity.TAG, "Conneciton falied : ERROR");
                    makeToast("Failed establishing connection: " + "ERROR");

                }
                else if( reason == WifiP2pManager.BUSY) {
                    Log.d(MainActivity.TAG, "Conneciton falied : BUSY");
                    makeToast("Failed establishing connection: " + "BUSY");

                }
            }
        });
    }


    private void startServer(){
        serverSocketThread = new ServerSocketThread();
        serverSocketThread. setUpdateListener(new ServerSocketThread.OnUpdateListener() {
            public void onUpdate(String obj) {
                getWifi.handelMessage(obj);

                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.AuthHotSpotArrived(getWifi.getSSID());
                    }
                });
            }
        });
        serverSocketThread.execute();
    }

    //Listeners

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        String hostAddress= wifiP2pInfo.groupOwnerAddress.getHostAddress();
        if (hostAddress == null) hostAddress= "host is null";

        //makeToast("Am I group owner : " + String.valueOf(wifiP2pInfo.isGroupOwner));
        //makeToast(hostAddress);
        Log.d(MainActivity.TAG,"wifiP2pInfo.groupOwnerAddress.getHostAddress() " + wifiP2pInfo.groupOwnerAddress.getHostAddress());
        IP = wifiP2pInfo.groupOwnerAddress.getHostAddress();
        IS_OWNER = wifiP2pInfo.isGroupOwner;

        if(IS_OWNER) {
             buttonClientStop.setVisibility(View.GONE);
            buttonClientStart.setVisibility(View.GONE);

            //Testing
            //buttonServerStop.setVisibility(View.VISIBLE);
            //buttonServerStart.setVisibility(View.VISIBLE);

            textViewReceivedDataStatus.setVisibility(View.VISIBLE);

            startServer();
        } else {
            //buttonClientStop.setVisibility(View.VISIBLE);
            //buttonClientStart.setVisibility(View.VISIBLE);
            buttonServerStop.setVisibility(View.GONE);
            buttonServerStart.setVisibility(View.GONE);
            textViewReceivedDataStatus.setVisibility(View.GONE);
            String tmp = shareWifi.getHotspotInfo();//TODO delete it after the checks
            ClientStart();
        }

        makeToast("Configuration Completed");
    }

    // test methods

   // public void setReceivedText(final String data) {
   //     mainActivity.runOnUiThread(new Runnable() {
   //         @Override
   //         public void run() {
   //             textViewReceivedData.setText(data);
   //         }
   //     });
   // }

/// permissions methods

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mPermissionsGranted = true;
                    discoverPeers();

                }else{
                    mPermissionsGranted = false;
                }
            }
        }
    }

    private boolean isPermissionsGranted(String[] permissions){

        boolean res = true;
        for(String permission : permissions){
            if(ContextCompat.checkSelfPermission(mainActivity.getApplicationContext(),
                    permission)!= PackageManager.PERMISSION_GRANTED){
                res =false;
                break;
            }
        }

        return res;
    }
// TODO check the override permission
    private void checkPermissionsAndAction(){
        if(!isPermissionsGranted(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.INTERNET,})){
            if(ActivityCompat.shouldShowRequestPermissionRationale(mainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION)){
                Toast.makeText(mainActivity.getApplicationContext(), "Need this permission to find nearby wifi", Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(mainActivity, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.INTERNET},MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        }else{
            discoverPeers();
        }
    }

    // utils

    private void changeName(String typeOfUser){
        try {
            Method m = mManager.getClass().getMethod(
                    "setDeviceName",
                    new Class[] { WifiP2pManager.Channel.class, String.class,
                            WifiP2pManager.ActionListener.class });

            m.invoke(mManager,mChannel , "HotShot" + typeOfUser, new WifiP2pManager.ActionListener() {
                public void onSuccess() {
                    Log.d("ChangeName", "success");
                }

                public void onFailure(int reason) {
                    Log.d("ChangeName", "failed");
                }
            });
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void showAlart(){
        AlertDialog.Builder hotspotAlert = new AlertDialog.Builder(getContext());
        hotspotAlert.setMessage("Please turn on the hotspot to enable to connect your internet")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings. ACTION_WIRELESS_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        hotspotAlert.show();
    }

    private boolean checkSettingForClient(){
        DataSaveLocaly dataSaveLocaly = new DataSaveLocaly(mainActivity.getApplicationContext());
        String mbGet =dataSaveLocaly.getReadData();
        if(mbGet == "0" )
            return false;
        else
            return true;
    }

}