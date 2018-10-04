package com.example.yarden.hotshot.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.yarden.hotshot.Activitys.MainActivity;
import com.example.yarden.hotshot.Provider.ServerClass;
import com.example.yarden.hotshot.SendReceive;
import com.example.yarden.hotshot.Utils.WifiClientBroadcastReceiver;


import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import static android.os.Looper.getMainLooper;

public class P2PWifi implements Serializable {


    static final int MESSAGE_READ=1;
    private WifiManager wifiManager;
    private  WifiP2pManager mManager;
    private  WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private  IntentFilter mIntentFilter;
    private List<WifiP2pDevice> peers=new ArrayList<WifiP2pDevice>();
    private  String[] deviceNameArray;
    private   WifiP2pDevice[] deviceArray;
    private ServerClass serverClass;
    private ClientClass clientClass;
    private SendReceive sendReceive;
    private Context context;
    private MainActivity activity;
    private String answerMsg  = null;

    public P2PWifi(Context _context, MainActivity _activity , WifiP2pManager wifiP2pManager)
    {
        context= _context;
        activity = _activity;
        mManager = wifiP2pManager;
    }

    public void initialWork() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        wifiManager= (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //do when create the class and send in ctor
        // mManager= (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel=mManager.initialize(context,getMainLooper(),null);
        mReceiver=new WifiClientBroadcastReceiver(mManager, mChannel,activity);

        //try change name
        try {
            Method method = mManager.getClass().getMethod("setDeviceName",
                    WifiP2pManager.Channel.class, String.class, WifiP2pManager.ActionListener.class);

            method.invoke(mManager, mChannel, "HotShare", new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess(){
                    Log.d("setDeviceName succeeded", "true");
                }
                @Override
                public void onFailure(int reason) {
                    Log.d("setDeviceName failed", "true");
                }
            });
        } catch (Exception e){Log.d("setDeviceName exception", "true");}

        mIntentFilter=new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    public WifiP2pManager.PeerListListener peerListListener=new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            if(!peerList.getDeviceList().equals(peers))
            {
                peers.clear();

                deviceNameArray=new String[peerList.getDeviceList().size()];
                deviceArray=new WifiP2pDevice[peerList.getDeviceList().size()];
                int index=0;

                for(WifiP2pDevice device : peerList.getDeviceList())
                {
                    deviceNameArray[index]=device.deviceName;
                    deviceArray[index]=device;
                    index++;
                }

                ArrayAdapter<String> adapter=new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,deviceNameArray);
            }

            if(peers.size()==0)
            {
                Toast.makeText(context,"No Device Found",Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };

    public WifiP2pManager.ConnectionInfoListener connectionInfoListener=new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            final InetAddress groupOwnerAddress=wifiP2pInfo.groupOwnerAddress;

            if(wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) // Host
            {
                serverClass=new ServerClass(sendReceive);
                serverClass.start();
            }else if(wifiP2pInfo.groupFormed) // Client
            {
                clientClass=new ClientClass(groupOwnerAddress, sendReceive);
                clientClass.start();
            }
        }
    };

    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what)
            {
                case MESSAGE_READ:
                    byte[] readBuff= (byte[]) msg.obj;
                    answerMsg = new String(readBuff,0,msg.arg1);
                    break;
            }
            return true;
        }
    });



    public void StartConnectionP2P() throws InterruptedException {
        WifiP2pConfig config=new WifiP2pConfig();
        final boolean[] connectSuccess = {true};
        boolean getAnswer = false;

        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess()
            {
                Log.d("Connection started" , "success");
            }

            @Override
            public void onFailure(int i)
            {
                Log.d("Discovery Failed", "fail");
            }
        });

        while(connectSuccess[0] && deviceArray!=null )

            for(WifiP2pDevice device : deviceArray)
            {
                if(device.deviceName == "HotShsre")
                {
                    config.deviceAddress=device.deviceAddress;
                }
            }

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                connectSuccess[0] =true;
                Log.d("connect", "connect");
            }

            @Override
            public void onFailure(int i) {
                connectSuccess[0] =false;
                Log.d("Notconnect", "Notconnect");
            }
        });

    }

    public  void WriteMessege(String msg)
    {
        sendReceive.write(msg.getBytes());
    }

    public String GetAnswerMsg()
    {

        try {
            wait(7000);
            if( answerMsg  != null)
                return answerMsg;

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }




}
