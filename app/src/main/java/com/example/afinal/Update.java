package com.example.afinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.afinal.Models.UploadRecipe;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Update extends AppCompatActivity {
    ImageView imageView;
    EditText EName;
    EditText EIngredients;
    EditText EInstructions;
    String key, oldImageUrl;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    Uri uri;
    String photoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        getSupportActionBar().hide();
        imageView=findViewById(R.id.imageView);
        EName=findViewById(R.id.EtName);
        EIngredients=findViewById(R.id.EtIngredients);
        EInstructions=findViewById(R.id.EtInstructions);

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            Glide.with(Update.this)
                    .load(bundle.getString("oldImageUrl"))
                    .into(imageView);
            EName.setText(bundle.getString("nameKey"));
            EIngredients.setText(bundle.getString("ingredientsKey"));
            EInstructions.setText(bundle.getString("instructionKey"));
            key = bundle.getString("key");
            oldImageUrl = bundle.getString("oldImageUrl");
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("UploadRecipe").child(key);


    }

    public void selectImage(View view) {
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

    public void update(View view) {

        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Saving Update...");
        progressDialog.show();

        storageReference = FirebaseStorage.getInstance().getReference()
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
                Toast.makeText(Update.this,"Error"+e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });






    }

    public void uploadRecipe() {
        String dessertName = EName.getText().toString().trim();
        String dessertIngredients = EIngredients.getText().toString().trim();
        String dessertInstructions = EInstructions.getText().toString();


        UploadRecipe recipe = new UploadRecipe(dessertName, dessertIngredients, dessertInstructions, photoUrl);
        databaseReference.setValue(recipe).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(Update.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Update.this, AddRecipe.class);
                startActivity(intent);
                finish();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Update.this,"Error"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void back(View view) {
        Intent toDetail = new Intent (Update.this, AddRecipe.class);
        startActivity (toDetail);
    }
}