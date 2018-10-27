package com.example.yarden.hotshot.Utils;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.yarden.hotshot.Fragments.HomeFragment;
import com.example.yarden.hotshot.R;
import com.google.firebase.database.DatabaseReference;

public class HotspotDetailsActivity extends AppCompatActivity {

    private EditText editText_Password;
    private EditText editText_SSID;
    private Button buttonHotspot;
    private Button button_Submit;
    private String ssid;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotspot_details);
        init();
    }

    private void init() {
    try {
        editText_Password = findViewById(R.id.editText_Password);
        editText_SSID = findViewById(R.id.editText_ssid);
        buttonHotspot = findViewById(R.id.button_hotspot);
        button_Submit = findViewById(R.id.button_submit);

        button_Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = editText_Password.getText().toString();
                ssid = editText_SSID.getText().toString();
                if (password == null || ssid == null) {
                    Toast.makeText(getApplicationContext(), "Pleace enter ssid and password", Toast.LENGTH_LONG).show();
                    editText_Password.clearComposingText();
                    editText_SSID.clearComposingText();
                } else {
                    String uid = FirebaseInstances.getUid();
                    DatabaseReference myRef = FirebaseInstances.getDatabaseRef();

                    myRef.child(uid).child("Password").setValue(password);
                    myRef.child(uid).child("SSID").setValue(ssid);

                }
            }
        });

        buttonHotspot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent);
            }
        });
    }catch (Exception e){
        e.printStackTrace();
    }
    }

}
