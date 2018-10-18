package com.example.yarden.hotshot.Provider;

import com.example.yarden.hotshot.Utils.P2PWifi;

public interface IConnectionEstablishedInterface {

    public void SendInfo(P2PWifi.SendReceive i_sendReceive);
}