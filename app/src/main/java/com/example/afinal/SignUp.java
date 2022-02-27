package com.example.afinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.afinal.Models.ReadWriteUserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {
    private EditText etFirstName, etMiddleName, etLastName, etEmailAddress, etPassword;
    private static final String TAG="Sign Up";
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().hide();



        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Creating your account....");
        progressDialog.setCanceledOnTouchOutside(false);


        Toast.makeText(SignUp.this, "You can sign up now", Toast.LENGTH_LONG).show();


        TextView login = findViewById(R.id.gotoLoginBtn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toLogin = new Intent(SignUp.this, Login.class);
                startActivity(toLogin);
                finish();
            }
        });

        etFirstName = findViewById(R.id.etReg_Fname);
        etMiddleName = findViewById(R.id.etReg_Mname);
        etLastName = findViewById(R.id.etReg_Lname);
        etEmailAddress = findViewById(R.id.etReg_emailAddress);
        etPassword = findViewById(R.id.etReg_password);




        TextView signUp = findViewById(R.id.btnSignup);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String textFirstName = etFirstName.getText().toString();
                String textMiddleName = etMiddleName.getText().toString();
                String textLastName = etLastName.getText().toString();
                String textEmail = etEmailAddress.getText().toString();
                String textPassword = etPassword.getText().toString();

                if (TextUtils.isEmpty(textFirstName)) {
                    Toast.makeText(SignUp.this, "Please enter your first name", Toast.LENGTH_LONG).show();
                    etFirstName.setError("First Name is required");
                    etFirstName.requestFocus();
                } else if (TextUtils.isEmpty(textMiddleName)) {
                    Toast.makeText(SignUp.this, "Please enter your middle name", Toast.LENGTH_LONG).show();
                    etMiddleName.setError("Middle Name is required");
                    etMiddleName.requestFocus();
                } else if (TextUtils.isEmpty(textLastName)) {
                    Toast.makeText(SignUp.this, "Please enter your last name", Toast.LENGTH_LONG).show();
                    etLastName.setError("Last Name is required");
                    etLastName.requestFocus();
                } else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(SignUp.this, "Please enter your email", Toast.LENGTH_LONG).show();
                    etEmailAddress.setError("Email is required");
                    etEmailAddress.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(SignUp.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
                    etEmailAddress.setError("Valid email is required");
                    etEmailAddress.requestFocus();
                } else if (TextUtils.isEmpty(textPassword)) {
                    Toast.makeText(SignUp.this, "Please enter your password", Toast.LENGTH_LONG).show();
                    etPassword.setError("Password is required");
                    etPassword.requestFocus();
                } else if (textPassword.length() < 6) {
                    Toast.makeText(SignUp.this, "Password should be at least 6 characters", Toast.LENGTH_LONG).show();
                    etPassword.setError("Password too weak");
                    etPassword.requestFocus();
                } else {
                    registerUser(textFirstName, textMiddleName, textLastName, textEmail, textPassword);
                }

            }

        });
    }


    private void registerUser(String textFirstName, String textMiddleName, String textLastName, String textEmail, String textPassword) {
        progressDialog.show();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();


        auth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseUser firebaseUser = auth.getCurrentUser();


                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails (textFirstName, textMiddleName, textLastName);


                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Users");


                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                progressDialog.dismiss();
                                firebaseUser.sendEmailVerification();
                                FirebaseUser firebaseUser = auth.getCurrentUser();
                                String email = firebaseUser.getEmail();
                                Toast.makeText(SignUp.this,"Account Successfully Created as\n"+email, Toast.LENGTH_SHORT).show();




                                Intent intent = new Intent(SignUp.this, Home.class);


                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {

                                Toast.makeText(SignUp.this, "Registration is failed. Please try again.", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getApplicationContext(), SignUp.class));
                                finish();
                            }

                        }
                    });

                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        etPassword.setError("Your password is too weak. Kindly use a combination of alphabets, numbers and special characters");
                        etPassword.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        etEmailAddress.setError("Your email is invalid or already used by another user.Use another email");
                        etEmailAddress.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e) {
                        etEmailAddress.setError("User is already registered with this email. Use another email.");
                        etEmailAddress.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }
            }
        });
    }
}