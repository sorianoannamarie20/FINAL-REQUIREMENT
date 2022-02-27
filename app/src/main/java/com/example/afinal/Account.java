package com.example.afinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.afinal.Models.ImageUrl;
import com.example.afinal.Models.ReadWriteUserDetails;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Account extends AppCompatActivity {
    private TextView accFName, accMName, accLName, accEmail;
    private String firstName, middleName, lastName, email;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private ImageView imageView;
    private TextView logout;
    private ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.Account);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.Add:
                        startActivity(new Intent(getApplicationContext(), AddRecipe.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.Home:
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.Account:
                        return true;

                    case R.id.Video:
                        startActivity(new Intent(getApplicationContext(), VideoDashboard.class));
                        overridePendingTransition(0,0);
                        return true;


                }
                return false;
            }
        });

        TextView editProfileBtn = (TextView) findViewById(R.id.editProfileBtn);
        editProfileBtn.setOnClickListener( v -> {
            Intent intent = new Intent(Account.this, EditProfile.class);
            startActivity(intent);
        } );

        backBtn = findViewById (R.id.backBtn);
        backBtn.setOnClickListener ( v -> {
            Intent toHome = new Intent (Account.this, Home.class);
            startActivity (toHome);
        } );

        logout = (TextView) findViewById(R.id.btnLogout);
        logout.setOnClickListener( v -> {
            FirebaseAuth.getInstance().signOut();
            ImageUrl.imgUrl = "";
            ImageUrl.fImageUrl = "";
            Intent intent = new Intent (Account.this, Login.class);
            startActivity ( intent );
            finishAffinity ();
            /* startActivity(new Intent(Account.this, Login.class));*/
        } );

        progressBar = findViewById(R.id.progressBar);
        accFName = findViewById(R.id.fnameContainer);
        accMName = findViewById(R.id.mnameContainer);
        accLName = findViewById(R.id.lnameContainer);
        accEmail = findViewById(R.id.emailContainer);
        imageView = findViewById(R.id.userProfile);



        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(Account.this, "Something went wrong! User's details are not available at the moment", Toast.LENGTH_LONG).show();
        } else {
            showUserProfile(firebaseUser);
        }
    }


    private void showUserProfile(FirebaseUser firebaseUser){
        String userID = firebaseUser.getUid();

        //extracting user reference from database for "Users"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails= snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null) {
                    firstName = readUserDetails.fName;
                    middleName = readUserDetails.mName;
                    lastName = readUserDetails.lName;
                    email = firebaseUser.getEmail();

                    accFName.setText(firstName);
                    accMName.setText(middleName);
                    accLName.setText(lastName);
                    accEmail.setText(email);

                    //
                    Uri uri = firebaseUser.getPhotoUrl();
                    //Glide.with(Account.this).load(uri).into(imageView);
                    Glide.with(imageView.getContext()).load(uri).placeholder(R.drawable.avatar).dontAnimate().into(imageView);
                }else {
                    Toast.makeText(Account.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Account.this, "Something went wrong!", Toast.LENGTH_LONG).show();


            }
        });
    }
}