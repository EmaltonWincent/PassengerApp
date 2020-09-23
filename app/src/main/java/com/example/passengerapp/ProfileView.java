package com.example.passengerapp;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileView extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    TextView textViewEmail;
    Button editProfile;
    TextView textViewName;
    TextView textViewBusNo;
    TextView textViewMobNo;
    ImageView imageView;
    DatabaseReference databaseReference;
    private ProgressBar spinner;
    private static final String TAG = "ProfileView";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        textViewEmail = (TextView) findViewById(R.id.textViewEmailProfileView);
        editProfile = (Button) findViewById(R.id.editProfile);
        textViewName = (TextView) findViewById(R.id.textProfileViewName);
        textViewBusNo = (TextView) findViewById(R.id.textViewBusNo);
        textViewMobNo = (TextView) findViewById(R.id.textViewPhoneNo);
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();

        textViewEmail.setText(email);

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("bookings");
        ref.child("1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("address").getValue().toString();
//                String busNo = dataSnapshot.child("busNo").getValue().toString();
//                String moNo = dataSnapshot.child("phoneNo").getValue().toString();
                textViewName.setText("Driver Name: " + name);
//                textViewBusNo.setText("Bus Number: " + busNo);
//                textViewMobNo.setText("Phone Number: " + moNo);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}











//        }
//        final FirebaseUser user = firebaseAuth.getCurrentUser();
//
//        databaseReference =  FirebaseDatabase.getInstance().getReference();
//        databaseReference = FirebaseDatabase.getInstance().getReference();
//        textViewEmail = (TextView)findViewById(R.id.textViewEmailProfileView);
//        editProfile = (Button)findViewById(R.id.editProfile);
//        textViewName = (TextView) findViewById(R.id.textProfileViewName);
//        textViewPhone = (TextView)findViewById(R.id.textProfileViewPhone);
//        textViewDOB = (TextView) findViewById(R.id.textProfileViewDOB);
//
//        textViewEmail.setText("Email id : "+user.getEmail());
//
//
//
//        editProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
//            }
//        });
//
//        ChildEventListener childEventListener = new ChildEventListener() {
//
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
////                UserInformation userInformation = dataSnapshot.getValue(UserInformation.class);
////                textViewName.setText("Name : "+userInformation.name);
////                textViewPhone.setText("Phone : "+userInformation.phone);
////                textViewDOB.setText("Birth Date : "+userInformation.DOB);
//
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                UserInformation userInformation = dataSnapshot.getValue(UserInformation.class);
//                textViewName.setText("Name : "+userInformation.name);
//                textViewPhone.setText("Phone : "+userInformation.phone);
//                textViewDOB.setText("Birth Date : "+userInformation.DOB);
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//
//        };
//        databaseReference.addChildEventListener(childEventListener);
//
//
//    }
