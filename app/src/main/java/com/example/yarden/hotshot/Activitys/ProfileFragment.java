package com.example.yarden.hotshot.Activitys;
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

import com.example.yarden.hotshot.Provider.ShareWifi;
import com.example.yarden.hotshot.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {
    private EditText editTextPassword;
    private Button buttonOk;
    private FirebaseDatabase database ;
    private DatabaseReference myRef;
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile , container , false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
          database = FirebaseDatabase.getInstance();
          myRef = database.getReference("message");
        firebaseAuth = FirebaseAuth.getInstance();

        editTextPassword = (EditText)getActivity().findViewById(R.id.editText_password);
        buttonOk =(Button)getActivity().findViewById(R.id.button_pass_ok) ;
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = editTextPassword.getText().toString();
                String id = firebaseAuth.getCurrentUser().getUid();
                    myRef.child(id).child("password").setValue(pass);



            }
        });
    }
}
