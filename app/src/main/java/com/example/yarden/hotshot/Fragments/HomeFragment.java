package com.example.yarden.hotshot.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.yarden.hotshot.Client.ClientManager;
import com.example.yarden.hotshot.MainActivity;
import com.example.yarden.hotshot.Provider.IPeersEventListener;
import com.example.yarden.hotshot.Provider.IServerReciveEventListener;
import com.example.yarden.hotshot.Provider.ShareWifi;
import com.example.yarden.hotshot.R;
import com.example.yarden.hotshot.Utils.INotifyEndOfUsage;
import com.example.yarden.hotshot.Utils.P2PWifi;
import com.example.yarden.hotshot.Utils.UpdateDataBase;
import com.example.yarden.hotshot.Utils.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeFragment extends Fragment implements IPeersEventListener, IServerReciveEventListener {
    private WifiManager wifiManager;
    private WifiP2pManager wifiP2pManager;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private P2PWifi p2PWifi;
    private ClientManager getWifi;
    private ShareWifi shareWifi;
    private FirebaseUser firebaseUser;
    private MainActivity mainActivity;
    private FirebaseDatabase database ;
    private DatabaseReference myRef;
    private FragmentActivity myFRContext;
    private boolean mIsClient;
    private boolean mPermissionsGranted;
    private UpdateDataBase updateDataBase;
    private User clientUser;
    private User providerUser;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;


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
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("message");
        p2PWifi = mainActivity.getP2PWifi();
        wifiManager= (WifiManager) mainActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        getWifi = new ClientManager(p2PWifi , wifiManager , getContext());
        shareWifi = new ShareWifi(p2PWifi , wifiManager );
        shareWifi.setPeerEventListener(this);
        p2PWifi.setServerReciveEventListeners(this);
        p2PWifi.setClientReciveEventListeners(getWifi);

        //yarden - delete after logic complete
         providerUser = new User();
        providerUser.setGetFirebaseUidProvider("lvbDgu0zk4MGEktlCOvRdONeKUi2");
         clientUser = new User();
        updateDataBase = new UpdateDataBase(providerUser ,clientUser, wifiManager , getContext());


        final ListView list = (ListView)getActivity().findViewById(R.id.list_view_peers);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try{  p2PWifi.connectToDevice(i);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        FloatingActionButton fab_getWifi = (FloatingActionButton) mainActivity.findViewById(R.id.fab_getWifi);

        fab_getWifi.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
             /*   mIsClient = true;
                try{
                    // here request permission
                   checkPermissionsAndAction();
                }
                catch (Exception e){
                    e.printStackTrace();
                }*/
            }
        });


        FloatingActionButton fab_shareWifi = (FloatingActionButton) mainActivity.findViewById(R.id.fab_shareWifi);
        fab_shareWifi.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
              list.setVisibility(View.VISIBLE);
                mIsClient = false;
                try {
                    Toast.makeText(getActivity(), "Searching for available Wifi",
                            Toast.LENGTH_LONG).show();
                    checkPermissionsAndAction();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        FloatingActionButton fab_stop = (FloatingActionButton)mainActivity.findViewById(R.id.fab_stop_connection);
        fab_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//To check . and what with provider
                getWifi.disconnect();
            }
        });

    }


    @Override
    public void OnPeersAppearEvent(ArrayAdapter<String> adapter) {

        ListView listV = (ListView) getActivity().findViewById(R.id.list_view_peers);
        listV.setAdapter(p2PWifi.getmPeersAdapter());

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mPermissionsGranted = true;
                    p2PWifi.StartDiscoveringP2P(mIsClient);

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

    private void checkPermissionsAndAction(){
        if(!isPermissionsGranted(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.INTERNET})){
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
            p2PWifi.StartDiscoveringP2P(mIsClient);
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

    @Override
    public void handelMessage(String msg) {
        showAlart();
    }

}
