package com.example.yarden.hotshot.Fragments;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yarden.hotshot.Client.DataSaveLocaly;
import com.example.yarden.hotshot.MainActivity;
import com.example.yarden.hotshot.R;
import com.example.yarden.hotshot.Utils.ConfigPaypal;
import com.example.yarden.hotshot.Utils.FirebaseAuthInstance;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;

import java.io.File;
import java.math.BigDecimal;


public class ProfileFragment extends Fragment {

    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton5;
    private  RadioButton radioButtonChose;
    private TextView textView_mb_share;
    private TextView textView_mb_get;
    private Button button_pay;
    private Button logout;
    private TextView textView_email;
    private Integer pay;
    private Activity activity ;
    private DatabaseReference myRef ;
    private String uid;
    private  Float mbGet = null;
    private Float mbProvied ;
    private DataSaveLocaly localyInfo;
    private int PAYPAL_REQUEST_CODE = 1;
    private  Integer USD_0 = 0;
    private  Integer USD_1 = 1;
    private  Integer USD_2 = 2;
    private  Integer USD_5 = 5;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile , container , false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activity = getActivity();
        Intent intent = new Intent(activity.getBaseContext() , PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION , config);
        activity.startService(intent);
        myRef = FirebaseAuthInstance.getDatabaseRef();
        textView_mb_get = (TextView)activity.findViewById(R.id.textView_wifi_get);
        textView_mb_share = (TextView)activity.findViewById(R.id.textView_wifi_proviedr);
        localyInfo = new DataSaveLocaly(activity.getBaseContext());
        databaseChange();
        initial();
    }

    @Override
    public void onDestroyView() {
        getActivity().stopService(new Intent(getActivity().getBaseContext() , PayPalService.class));
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PAYPAL_REQUEST_CODE)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                if(mbGet == null)
                   mbGet = pay.floatValue();
                else
                    mbGet += pay;
                myRef.child(uid).child("TotalGetMB").setValue(mbGet);
            }
            else
            {
                Toast.makeText(activity.getBaseContext() , "paypment failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initial()
    {

            pay = USD_0;
            textView_email = (TextView) activity.findViewById(R.id.textView_email);
            logout = (Button) activity.findViewById(R.id.button_sign_out2);
            radioButton1 = (RadioButton) activity.findViewById(R.id.radioButton_0_5);
            radioButton2 = (RadioButton) activity.findViewById(R.id.radioButton_2);
            radioButton5 = (RadioButton) activity.findViewById(R.id.radioButton_5);
            button_pay = (Button) activity.findViewById(R.id.button_pay);
            //   String email = FirebaseAuthInstance.getFirebaseUser().getEmail();
//             textView_email.setText(email);


            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FirebaseAuthInstance.getFirebaseAuth().signOut();
                }
            });

            radioButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pay = USD_1;
                    radioButtonChose = radioButton1;
                }
            });

            radioButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pay = USD_2;
                    radioButtonChose = radioButton2;
                }
            });

            radioButton5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pay = USD_5;
                    radioButtonChose = radioButton5;
                }
            });

            button_pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pay != USD_0) {
                        paypalPayment();
                        radioButtonChose.setChecked(false);

                    }
                }
            });

    }


    private static PayPalConfiguration config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(ConfigPaypal.PAYPAL_CLIENT_ID);

    private void paypalPayment(){
        PayPalPayment payment = new PayPalPayment(new BigDecimal(pay) , "USD" , "Get mb" , PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(activity.getBaseContext() , PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION , config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT , payment);
        startActivityForResult(intent , PAYPAL_REQUEST_CODE);

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void databaseChange(){

        if(isNetworkAvailable())
        {
            uid = FirebaseAuthInstance.getUid();
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //client
                     mbGet = dataSnapshot.child(uid).child("TotalGetMB").getValue(Float.class);
                     if(mbGet != null)
                     {
                         textView_mb_get.setText(mbGet.toString());
                         localyInfo.writeToFile(mbGet.toString());
                     }
                     else
                     {
                         textView_mb_get.setText("0mb");
                     }

                    //provider
                    mbProvied = dataSnapshot.child(uid).child("TotalProviedMB").getValue(Float.class);
                    if(mbProvied == null)
                        textView_mb_share.setText("0mb");
                    else
                         textView_mb_share.setText(mbProvied.toString());
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                }
            });
        }
        else
        {
            try {
                String mb = localyInfo.getReadData();
                textView_mb_get.setText(mb);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }







}
