package com.example.yarden.hotshot.Fragments;

import android.app.Fragment;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yarden.hotshot.MainActivity;
import com.example.yarden.hotshot.R;

public class HomeFragmentHome extends Fragment {

    MainActivity mActivity;

    ImageButton mSearchWifiButton;
    ImageButton mGetWifiButton;
    ListView mPeersListView;
    Button mConfigButton;
    Button mConnect;
    Button mStartServer;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home , container , false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (MainActivity)getActivity();
        initUI();
    }

    private void initUI(){
        mConfigButton = mActivity.findViewById(R.id.button_config);
        mConnect = mActivity.findViewById(R.id.button_connect2);
        mGetWifiButton = mActivity.findViewById(R.id.get_wifi_button);
        mPeersListView = mActivity.findViewById(R.id.hm_fr_peer_view);
        mSearchWifiButton= mActivity.findViewById(R.id.serch_wifi_button);
        mStartServer = mActivity.findViewById(R.id.button_start_server);
    }
}
