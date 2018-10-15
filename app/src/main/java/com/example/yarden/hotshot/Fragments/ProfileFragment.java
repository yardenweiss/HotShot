package com.example.yarden.hotshot.Fragments;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yarden.hotshot.R;
import com.example.yarden.hotshot.Utils.ConfigPaypal;
import com.google.firebase.auth.FirebaseAuth;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;

import java.math.BigDecimal;


public class ProfileFragment extends Fragment {

    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton5;
    private TextView textView_mb_share;
    private TextView textView_mb_get;
    private Button button_pay;
    private Button logout;
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



        Intent intent = new Intent(getActivity().getBaseContext() , PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION , config);
        getActivity().startService(intent);
        TextView email = (TextView)getActivity().findViewById(R.id.textView_email);
        email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        //logout = (Button)getActivity().findViewById(R.id.button_pay);
        radioButton1 = (RadioButton)getActivity().findViewById(R.id.radioButton_0_5);
        radioButton2 = (RadioButton)getActivity().findViewById(R.id.radioButton_2);
        radioButton5 = (RadioButton)getActivity().findViewById(R.id.radioButton_5);
        textView_mb_share = (TextView)getActivity().findViewById(R.id.textView_wifi_proviedr);
        textView_mb_get = (TextView)getActivity().findViewById(R.id.textView_wifi_get);
        button_pay = (Button)getActivity().findViewById(R.id.button_pay);
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

            }
            else
            {
                Toast.makeText(getActivity().getBaseContext() , "paypment failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initial() {

        pay = USD_0;


      /*  logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             //   FirebaseAuth.getInstance().signOut();
            }
        });*/
try{
    radioButton1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            pay = 1;
        }
    });
}catch (Exception e){
    e.printStackTrace();
}

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


    private static PayPalConfiguration config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(ConfigPaypal.PAYPAL_CLIENT_ID);

    private void paypalPayment(){
        PayPalPayment payment = new PayPalPayment(new BigDecimal(pay) , "USD" , "Get mb" , PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(getActivity().getBaseContext() , PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION , config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT , payment);
        startActivityForResult(intent , PAYPAL_REQUEST_CODE);

    }


}
