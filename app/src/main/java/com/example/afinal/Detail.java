package com.example.afinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.afinal.Adapters.CommentAdapter;
import com.example.afinal.Models.Comment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Detail extends AppCompatActivity {
    ImageView imageView;
    TextView tvItemName;
    TextView tvItemIngredients;
    TextView tvItemInstructions;
    String photoUrl;
    String key;
    ImageView menu_show;
    MenuBuilder menuBuilder;

    EditText editTextComment;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    ImageView btnAddComment;

    FirebaseDatabase firebaseDatabase;
    RecyclerView RvComment;
    CommentAdapter commentAdapter;
    List<Comment> listComment;
    static  String COMMENT_KEY = "Comment";

    @SuppressLint("RestrictedApi")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        imageView = findViewById(R.id.image);
        tvItemName = findViewById(R.id.nameItem);
        tvItemIngredients = findViewById(R.id.ingredientsItem);
        tvItemInstructions = findViewById(R.id.instructionsItem);

        editTextComment = findViewById(R.id.post_detail_comment);
        btnAddComment = findViewById(R.id.post_detail_add_comment_btn);
        RvComment = findViewById(R.id.rv_comment);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();


        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAddComment.setVisibility(View.INVISIBLE);
                DatabaseReference commentReferences = firebaseDatabase.getReference(COMMENT_KEY).child(key).push();
                String comment_content = editTextComment.getText().toString();
                String uid = firebaseUser.getUid();
                String uname = firebaseUser.getEmail();
                String uimg = firebaseUser.getPhotoUrl().toString();
                Comment comment = new Comment(comment_content, uid, uimg, uname);

                commentReferences.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showMessage("comment added");
                        editTextComment.setText("");
                        btnAddComment.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("Failed to add : " +e.getMessage());
                    }
                });
            }
        });


        menu_show = (ImageView) findViewById(R.id.show_menu);
        menuBuilder = new MenuBuilder(this);
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.popmenu, menuBuilder);

        menu_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MenuPopupHelper optionMenu = new MenuPopupHelper(Detail.this, menuBuilder, view);
                optionMenu.setForceShowIcon(true);
                menuBuilder.setCallback(new MenuBuilder.Callback() {
                    @Override
                    public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                        switch (item.getItemId()) {

                            case R.id.update:
                                startActivity(new Intent(getApplicationContext(), Update.class)
                                        .putExtra("nameKey", tvItemName.getText().toString() )
                                        .putExtra("ingredientsKey", tvItemIngredients.getText().toString() )
                                        .putExtra("instructionKey", tvItemInstructions.getText().toString())
                                        .putExtra("oldImageUrl", photoUrl).putExtra("key", key)
                                );return true;

                            case R.id.delete:

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UploadRecipe");
                                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(Detail.this);
                                builder.setTitle("Delete Recipe");
                                builder.setMessage("Do you really want to delete?");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        reference.child(key).removeValue();
                                        Toast.makeText(Detail.this, "Recipe Deleted Successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Detail.this, AddRecipe.class));
                                        finish();
                                    }
                                });
                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {


                                    }
                                });
                                builder.show();
                                return true;


                            default:
                                return false;
                        }
                    }

                    @Override
                    public void onMenuModeChange(@NonNull MenuBuilder menu) {

                    }
                });
                optionMenu.show();
            }
        });

        Bundle mBundle = getIntent().getExtras();

        if (mBundle != null) {
            tvItemIngredients.setText(mBundle.getString("Ingredients"));
            key = mBundle.getString("keyValue");
            photoUrl = mBundle.getString("Image");
            tvItemName.setText(mBundle.getString("DessertName"));
            tvItemInstructions.setText(mBundle.getString("Instruction"));

            Glide.with(this)
                    .load(mBundle.getString("Image"))
                    .into(imageView);


        }


        Intent intent = getIntent();
        photoUrl = intent.getStringExtra("IMAGE");

        Glide.with(this).load(photoUrl).into(imageView);

        String itemName = intent.getStringExtra("NAME");
        tvItemName.setText(itemName);

        String itemIngredients = intent.getStringExtra("INGREDIENTS");
        tvItemIngredients.setText(itemIngredients);

        String itemInstructions = intent.getStringExtra("INSTRUCTIONS");
        tvItemInstructions.setText(itemInstructions);

        key = intent.getStringExtra("KEY");

        iniRvComment();
    }

    private void iniRvComment() {

        RvComment.setLayoutManager(new LinearLayoutManager(this));
        DatabaseReference commentRef = firebaseDatabase.getReference(COMMENT_KEY).child(key);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listComment = new ArrayList<>();
                for (DataSnapshot snap:dataSnapshot.getChildren()){
                    Comment comment = snap.getValue(Comment.class);
                    listComment.add(comment);

                }
                commentAdapter = new CommentAdapter(getApplicationContext(), listComment);
                RvComment.setAdapter(commentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void back(View view) {
        Intent toAdd = new Intent (Detail.this, AddRecipe.class);
        startActivity (toAdd);
    }
}