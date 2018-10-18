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

import com.example.yarden.hotshot.Client.IClientReciveEventListener;
import com.example.yarden.hotshot.MainActivity;
import com.example.yarden.hotshot.Provider.IConnectionEstablishedInterface;
import com.example.yarden.hotshot.Provider.IPeersEventListener;
import com.example.yarden.hotshot.Provider.IServerReciveEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class P2PWifi implements Serializable {

    //Constants
    public static final int MESSAGE_READ = 1;
    public static final int CONFIRM_MESSAGE = 2;

    private WifiManager wifiManager;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    private String[] deviceNameArray;
    private WifiP2pDevice[] deviceArray;
    private ServerClass serverClass;
    private ClientClass clientClass;
    private SendReceive sendReceive;
    private Context context;
    private MainActivity activity;
    private String answerMsg = null;
    private WifiP2pConfig config;
    private ArrayAdapter<String> mPeersAdapter;

    // EventHandlers
    private ArrayList<IPeersEventListener> peersEventListeners;
    private ArrayList<IConnectionEstablishedInterface> connectionEstablishedEventListeners;
    private ArrayList<IClientReciveEventListener> clientReciveEventListeners;
    private ArrayList<IServerReciveEventListener> serverReciveEventListeners;
    // tmp param
    boolean mIsClient = true;
    boolean mIsConnectionEstablished = false;

    public P2PWifi(Context _context, MainActivity _activity, WifiP2pManager wifiP2pManager,
                   WifiP2pManager.Channel channel) {
        context = _context;
        activity = _activity;
        mManager = wifiP2pManager;
        config = new WifiP2pConfig();
        mChannel = channel;
        peersEventListeners = new ArrayList<>();
        connectionEstablishedEventListeners = new ArrayList<>();
        clientReciveEventListeners = new ArrayList<>();
        serverReciveEventListeners = new ArrayList<>();
        changeName();
    }

    public boolean IsConnectionEstablished() {
        return mIsConnectionEstablished;
    }

    public ArrayAdapter<String> getmPeersAdapter() {
        return mPeersAdapter;
    }

    public void StartDiscoveringP2P(boolean i_IsClient) {

        mIsClient = i_IsClient;
        try {
            mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(context, "Searching", Toast.LENGTH_SHORT).show();
                    Log.d("Discovery started", "success");
                }

                @Override
                public void onFailure(int i) {
                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                    Log.d("Discovery Failed", "fail");
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

        // else {return false;}
    }

    public void WriteMessege(String msg) {
        sendReceive.write(msg.getBytes());
    }

    public void WriteConfirmation(String msg){
        sendReceive.writeConfirmation(msg.getBytes());
    }

    public String GetAnswerMsg() {
        return answerMsg;
    }

    public void connectToDevice(int i) {
        final WifiP2pDevice device = deviceArray[i];
        WifiP2pConfig config = new WifiP2pConfig();
        if(!mIsClient) {
            config.groupOwnerIntent = 15;
        }
        config.deviceAddress = device.deviceAddress;
        try {
            mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(context, "Connected to " + device.deviceName, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int i) {
                    Toast.makeText(context, "Not Connected", Toast.LENGTH_SHORT).show();
                }
            });

            mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(context, "Connected to " + device.deviceName, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int i) {
                    Toast.makeText(context, "Not Connected", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void changeName(){
        try {
            Method m = mManager.getClass().getMethod(
                    "setDeviceName",
                    new Class[] { WifiP2pManager.Channel.class, String.class,
                            WifiP2pManager.ActionListener.class });

            m.invoke(mManager,mChannel , "HotShot", new WifiP2pManager.ActionListener() {
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

    // Handlers

    Handler sendPassHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            byte[] readBuff;
            switch (msg.what) {
                case MESSAGE_READ:
                    readBuff = (byte[]) msg.obj;
                    answerMsg = new String(readBuff, 0, msg.arg1);
                    // send the data to Client
                    notifyAllClientReceiveListeners();
                    break;
                case CONFIRM_MESSAGE:
                    readBuff = (byte[]) msg.obj;
                    answerMsg = new String(readBuff, 0, msg.arg1);
                    notifyAllServerReceiveListeners();
                    mIsConnectionEstablished = true;
                    //can close the wifi - sharewifi need to close the wifi and turn on the hot spot
            }
            return true;
        }
    });

    // Events

    private void notifyAllPeersListeners(){
        for(IPeersEventListener listener: peersEventListeners){
            listener.OnPeersAppearEvent(mPeersAdapter);
        }
    }

    public void setPeerEventListener(IPeersEventListener i_listener){
        peersEventListeners.add(i_listener);
    }

    private void notifyAllConnectionListeners(){
        for(IConnectionEstablishedInterface listener : connectionEstablishedEventListeners ){
            listener.SendInfo(sendReceive);
        }
    }

    public void setConnectionEstablishedEventListeners(IConnectionEstablishedInterface i_listener){
        connectionEstablishedEventListeners.add(i_listener);
    }

    private void notifyAllClientReceiveListeners(){
        for(IClientReciveEventListener listener : clientReciveEventListeners ){
            listener.handelMessage(answerMsg);
        }
    }

    private void notifyAllServerReceiveListeners(){
        for(IServerReciveEventListener listener : serverReciveEventListeners){
            listener.handelMessage(answerMsg);
        }
    }

    public void setClientReciveEventListeners(IClientReciveEventListener listener){
        clientReciveEventListeners.add(listener);
    }

    public void setServerReciveEventListeners(IServerReciveEventListener listener){
        serverReciveEventListeners.add(listener);
    }

    //Listeners

    public WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            if (!mIsClient) {
                if (!peerList.getDeviceList().equals(peers)) {
                    peers.clear();
                    peers.addAll(peerList.getDeviceList());

                    deviceNameArray = new String[peerList.getDeviceList().size()];
                    deviceArray = new WifiP2pDevice[peerList.getDeviceList().size()];
                    int index = 0;

                    for (WifiP2pDevice device : peerList.getDeviceList()) {
                        deviceNameArray[index] = device.deviceName;
                        deviceArray[index] = device;
                        index++;
                    }

                    mPeersAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, deviceNameArray);
                }

                // if we have data notify listeners.
                if (peers.size() == 0) {
                    Toast.makeText(context, "No Device Found", Toast.LENGTH_SHORT).show();

                } else {
                    notifyAllPeersListeners();
                }
            }
        }
    };

    public WifiP2pManager.ConnectionInfoListener connectionInfoListener =
            new WifiP2pManager.ConnectionInfoListener() {
                @Override
                public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                    final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;
                    if(wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner && serverClass==null) // Host
                    {
                        serverClass = new ServerClass();
                        serverClass.start();
                    } else if (wifiP2pInfo.groupFormed && clientClass==null) // Client
                    {
                        clientClass = new ClientClass(groupOwnerAddress);
                        clientClass.start();
                    }
                }
            };

    //inner Classes
    public class ServerClass extends Thread{
        Socket socket;
        ServerSocket serverSocket;
        private boolean isSendReciveNull = true;

        public ServerClass(){

        }

        @Override
        public void run() {
            try {
                serverSocket=new ServerSocket(8888);
                socket=serverSocket.accept();
                sendReceive = new SendReceive(socket);
                sendReceive.start();

                isSendReciveNull = false;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public boolean IsSendReciveNull(){
            return isSendReciveNull;
        }

        public SendReceive getSendReceive() {
            return sendReceive;
        }
    }

    public class ClientClass extends Thread{
        Socket socket;
        String hostAdd;
        private boolean isSendReciveNull = true;

        public  ClientClass(InetAddress hostAddress)
        {
            hostAdd=hostAddress.getHostAddress();
            socket=new Socket();
            //   sendReceive = i_sendReceive;
        }

        @Override
        public void run() {
            try {
                socket.connect(new InetSocketAddress(hostAdd,8888),500);
                sendReceive=new SendReceive(socket);
                isSendReciveNull = false;
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public boolean IsSendReciveNull(){
            return isSendReciveNull;
        }

        public SendReceive getSendReceive() {
            return sendReceive;
        }
    }

    public class SendReceive extends Thread{
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        private int messageType = P2PWifi.MESSAGE_READ;


        public SendReceive(Socket skt)
        {
            socket=skt;
            try {
                inputStream=socket.getInputStream();
                outputStream=socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            if (!mIsClient) {
                notifyAllConnectionListeners();
                messageType = P2PWifi.CONFIRM_MESSAGE;
            }

            while (socket != null) {
                try {
                    bytes = inputStream.read(buffer);
                    if (bytes > 0) {
                        sendPassHandler.obtainMessage(messageType, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }


        public void write(byte[] bytes)
        {
            try {
                messageType = P2PWifi.MESSAGE_READ;
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void writeConfirmation(byte[] bytes){
            try{
                messageType = P2PWifi.CONFIRM_MESSAGE;
                outputStream.write(bytes);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}