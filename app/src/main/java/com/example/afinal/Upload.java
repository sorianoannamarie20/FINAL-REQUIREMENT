package com.example.afinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.afinal.Models.UploadRecipe;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Calendar;

public class Upload extends AppCompatActivity {
    ImageView imageView;
    EditText EName;
    EditText EIngredients;
    EditText EInstructions;

    Uri uri;
    String photoUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        imageView=findViewById(R.id.imageView);
        EName=findViewById(R.id.EtName);
        EIngredients=findViewById(R.id.EtIngredients);
        EInstructions=findViewById(R.id.EtInstructions);


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
        }
        else{

            Toast.makeText(this,"Permission Allowed",Toast.LENGTH_SHORT).show();
        }

    }



    public void choosePhoto(View view) {

        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK)
        {
            uri=data.getData();
            imageView.setImageURI(uri);
        }
        else{
            Toast.makeText(this,"You have not picked photo",Toast.LENGTH_SHORT).show();
        }
    }

    public void upload(View view) {
        uploadPhoto();
    }


    public void uploadPhoto()
    {
        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Uploading Recipe...");
        progressDialog.show();
        StorageReference storageReference= FirebaseStorage.getInstance().getReference()
                .child("UploadRecipe").child(uri.getLastPathSegment());

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task task=taskSnapshot.getStorage().getDownloadUrl();
                while (!task.isSuccessful());
                Uri uriPhoto= (Uri) task.getResult();
                photoUrl=uriPhoto.toString();
                uploadRecipe();
                progressDialog.dismiss();



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Upload.this,"Error"+e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });


    }


    public void uploadRecipe()
    {
        String name=EName.getText().toString().trim();
        String ingredients=EIngredients.getText().toString().trim();
        String instructions=EInstructions.getText().toString().trim();


        String timeStamp= DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        UploadRecipe recipe=new UploadRecipe(name,ingredients,instructions,photoUrl);
        FirebaseDatabase.getInstance().getReference("UploadRecipe").child(timeStamp)
                .setValue(recipe).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(Upload.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Upload.this, AddRecipe.class);
                startActivity(intent);
                finish();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Upload.this,"Error"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void back(View view) {
        Intent toAdd = new Intent (Upload.this, AddRecipe.class);
        startActivity (toAdd);
    }
}