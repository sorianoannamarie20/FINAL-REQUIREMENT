package com.example.afinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.afinal.Models.ImageUrl;
import com.example.afinal.Models.ReadWriteUserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfile extends AppCompatActivity {
    private TextView updateFirstName, updateMiddleName, updateLastName, updateEmail;
    private String textFirstName, textMiddleName, textLastName, textEmail;
    private ImageView imgView, userProfile;
    private FirebaseAuth authProfile;
    private TextView choosePhoto;
    private ImageButton backBtn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        updateFirstName = findViewById (R.id.updateFname);
        updateMiddleName = findViewById (R.id.updateMname);
        updateLastName = findViewById (R.id.updateLname);
        updateEmail = findViewById (R.id.updateEmail);
        userProfile = findViewById (R.id.userProfile);
        progressBar = findViewById (R.id.progressBar);
        choosePhoto = findViewById (R.id.choosePhotoBtn);
        backBtn = findViewById (R.id.backBtn);




        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        choosePhoto.setOnClickListener ( v -> {
            Intent toChoosePhoto = new Intent (EditProfile.this, ChoosePhoto.class);

            /* toChoosePhoto.putExtra("image", ImageUrl.imgUrl);*/

            startActivity (toChoosePhoto);
        } );

        backBtn.setOnClickListener ( v -> {
            Intent toAccount = new Intent (EditProfile.this, Account.class);
            startActivity (toAccount);
        } );

        //show profile data
        showProfile (firebaseUser);

        //update profile
        TextView save = findViewById(R.id.saveBtn);
        save.setOnClickListener ( v -> updateProfile(firebaseUser) );
    }
    //updating...
    private void updateProfile(FirebaseUser firebaseUser) {

        if (TextUtils.isEmpty(textFirstName)) {
            Toast.makeText(EditProfile.this, "Please enter your first name", Toast.LENGTH_LONG).show();
            updateFirstName.setError("First Name is required");
            updateFirstName.requestFocus();
        } else if (TextUtils.isEmpty(textMiddleName)) {
            Toast.makeText(EditProfile.this, "Please enter your middle name", Toast.LENGTH_LONG).show();
            updateMiddleName.setError("Middle Name is required");
            updateMiddleName.requestFocus();
        } else if (TextUtils.isEmpty(textLastName)) {
            Toast.makeText(EditProfile.this, "Please enter your last name", Toast.LENGTH_LONG).show();
            updateLastName.setError("Last Name is required");
            updateLastName.requestFocus();
        } else {
            textFirstName = updateFirstName.getText().toString();
            textMiddleName = updateMiddleName.getText().toString();
            textLastName = updateLastName.getText().toString ();

            //enter data into database
            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textFirstName, textMiddleName, textLastName);

            //extract user reference from dbase for "Users"
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Users");

            String userID = firebaseUser.getUid ();

            referenceProfile.child (userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText (EditProfile.this, "Update Successful", Toast.LENGTH_LONG).show ();

                        Intent intent = new Intent(EditProfile.this, Account.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish(); // close activity
                    } else {
                        try {
                            throw  task.getException ();
                        } catch (Exception e){
                            Toast.makeText (EditProfile.this, e.getMessage (), Toast.LENGTH_LONG).show ();
                        }
                    }

                }
            } );
        }
    }

    //fetch data
    private void showProfile (FirebaseUser firebaseUser){
        String userIDofRegistered = firebaseUser.getUid ();

        progressBar.setVisibility (View.VISIBLE);

        //extracting...
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Users");

        referenceProfile.child (userIDofRegistered).addListenerForSingleValueEvent (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue (ReadWriteUserDetails.class);
                if (readUserDetails != null){
                    textFirstName = readUserDetails.fName;
                    textMiddleName = readUserDetails.mName;
                    textLastName = readUserDetails.lName;
                    textEmail = firebaseUser.getEmail();

                    updateFirstName.setText(textFirstName);
                    updateMiddleName.setText(textMiddleName);
                    updateLastName.setText(textLastName);
                    updateEmail.setText(textEmail);

                    //
                    Uri uri = firebaseUser.getPhotoUrl();
                    if (uri != null){
                        ImageUrl.imgUrl = uri.toString();
                        //  Glide.with(EditProfile.this).load(imgUrl).into(userProfile);
                        Glide.with(userProfile.getContext()).load(uri).placeholder(R.drawable.avatar).dontAnimate().into(userProfile);


                    }else {
                        Toast.makeText ( EditProfile.this, "Please Select Picture", Toast.LENGTH_SHORT ).show ();

                      /*  Glide.with(EditProfile.this).load(imgUrl).into(userProfile);
                        Glide.with(userProfile.getContext()).load(uri).placeholder(R.drawable.avatar).dontAnimate().into(userProfile);
*/

                    }

                } else {
                    Toast.makeText (EditProfile.this, "Something went wrong!", Toast.LENGTH_LONG).show ();
                }
                progressBar.setVisibility (View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText (EditProfile.this, "Something went wrong!", Toast.LENGTH_LONG).show ();

            }
        } );
    }
}