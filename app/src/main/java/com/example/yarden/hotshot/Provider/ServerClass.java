package com.example.yarden.hotshot.Provider;



import com.example.yarden.hotshot.SendReceive;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerClass extends Thread{
    Socket socket;
    ServerSocket serverSocket;
    SendReceive sendReceive;

    public ServerClass(SendReceive _sendReceive){
        sendReceive=_sendReceive;
    }

    @Override
    public void run() {
        try {
            serverSocket=new ServerSocket(8888);
            socket=serverSocket.accept();
            sendReceive=new SendReceive(socket);
            sendReceive.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}