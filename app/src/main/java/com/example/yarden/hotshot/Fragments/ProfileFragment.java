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
import android.widget.TextView;

import com.example.yarden.hotshot.R;
import com.example.yarden.hotshot.Utils.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

@SuppressLint("ValidFragment")
public class ProfileFragment extends Fragment {
    private EditText editTextPassword;
    private Button buttonOk;
    private FirebaseDatabase database ;
    private DatabaseReference myRef;
    private DatabaseReference passRef;
    private FirebaseAuth firebaseAuth;
    private String TAG="FireBaseTag";
    private User user;

   public ProfileFragment(User _user){
        user= _user;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile , container , false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TextView email = (TextView)getActivity().findViewById(R.id.textView_email);
        email.setText(user.GetEmail());

        Button logout = (Button)getActivity().findViewById(R.id.button_sign_out);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.Logout();
            }
        });
    }



}
