package com.example.yarden.hotshot.Fragments;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.yarden.hotshot.R;
import com.example.yarden.hotshot.Utils.MyAdapter;
import com.firebase.ui.auth.ui.email.RegisterEmailFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static com.firebase.ui.auth.AuthUI.TAG;

public class MyActivityFragment extends Fragment {
   private FirebaseDatabase database;
   private DatabaseReference refGet;
   private DatabaseReference refShared;
    private String firebaseUid;
    private HashMap<String, String> activitysShare = new HashMap<String ,String >();
    private HashMap<String, String> activitysGet =new HashMap<String ,String >();
    private boolean readFromFireBase = false;
    private  ListView listWifiShared;
    private ListView listWifiGet;
    private float totalMbShare = 0;
    private float totalMbGet = 0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dataBaseChange();
        return inflater.inflate(R.layout.fragment_my_activity , container , false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
         listWifiShared = (ListView)getActivity().findViewById(R.id.list_shared);
          listWifiGet = (ListView)getActivity().findViewById(R.id.list_found);
        FloatingActionButton buttonShared = (FloatingActionButton)getActivity().findViewById(R.id.floatingActionButton_shared);
        FloatingActionButton buttonGet = (FloatingActionButton)getActivity().findViewById(R.id.floatingActionButton_get);

        buttonShared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(readFromFireBase) {
                    MyAdapter adapter = new MyAdapter(activitysShare);
                    listWifiShared.setAdapter(adapter);
                }
            }
        });

        buttonGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(readFromFireBase) {
                    MyAdapter adapter = new MyAdapter(activitysGet);
                    listWifiGet.setAdapter(adapter);
                }
            }
        });
        }


    public void dataBaseChange() {
         database = FirebaseDatabase.getInstance();
         refGet = database.getReference();
         refShared = database.getReference();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseUid= firebaseUser.getUid();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot contactSnapshot = dataSnapshot.child(firebaseUid);
                Iterable<DataSnapshot> contactChildren = contactSnapshot.getChildren();
                for (DataSnapshot contact : contactChildren) {
                    Float mbGetFifi =contact.child("getWifi").getValue(Float.class);
                    String dateGetWifi = contact.getKey();
                    Float mbShareFifi =contact.child("shareWifi").getValue(Float.class);
                    String dateShareWifi = contact.getKey();

                    if(mbGetFifi!=null && dateGetWifi!=null) {
                        totalMbGet += mbGetFifi;
                        activitysGet.put(dateGetWifi, dateFormat(mbGetFifi));
                    }
                    if(mbShareFifi!=null && dateShareWifi!=null) {
                        totalMbShare += mbShareFifi;
                        activitysShare.put(dateShareWifi, dateFormat(mbShareFifi));
                    }

                }
                readFromFireBase = true;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        };

        refShared.addValueEventListener(postListener);
    }

    private String dateFormat( Float mb){
         Float f =Float.parseFloat(new DecimalFormat("##.####").format(mb));
         return "mb: " + f.toString();
    }

}

