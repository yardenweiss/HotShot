package com.example.yarden.hotshot.Fragments;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.yarden.hotshot.R;
import com.example.yarden.hotshot.Utils.Config;
import com.example.yarden.hotshot.Utils.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

@SuppressLint("ValidFragment")
public class ProfileFragment extends Fragment {

    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton5;
    private TextView textView_mb_share;
    private TextView textView_mb_get;
    private Button button_pay;

    private int pay;
    private int PAYPAL_REQUEST_CODE = 1;
    private  int USD_0 = 0;
    private  int USD_1 = 1;
    private  int USD_2 = 2;
    private  int USD_5 = 5;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile , container , false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initial();

    }

    private void initial() {

        pay = USD_0;
        TextView email = (TextView)getActivity().findViewById(R.id.textView_email);
        email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        Button logout = (Button)getActivity().findViewById(R.id.button_sign_out);
        radioButton1 = (RadioButton)getActivity().findViewById(R.id.radioButton_1);
        radioButton1 = (RadioButton)getActivity().findViewById(R.id.radioButton_2);
        radioButton1 = (RadioButton)getActivity().findViewById(R.id.radioButton_5);
        textView_mb_share = (TextView)getActivity().findViewById(R.id.textView_mb_share);
        textView_mb_get = (TextView)getActivity().findViewById(R.id.textView_mb_get);
        button_pay = (Button)getActivity().findViewById(R.id.button_pay);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();
            }
        });

        radioButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay=1;
            }
        });

        radioButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            pay=2;
            }
        });

        radioButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            pay =5;
            }
        });

        button_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pay != USD_0){
                        paypalPayment();
                }
            }
        });

    }

    private void paypalPayment(){

    }


}
