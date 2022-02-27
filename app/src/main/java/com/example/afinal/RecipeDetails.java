package com.example.afinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.afinal.Adapters.IngredientsAdapter;
import com.example.afinal.Adapters.InstructionsAdapter;
import com.example.afinal.Adapters.SimilarRecipeAdapter;
import com.example.afinal.Listeners.InstructionsListener;
import com.example.afinal.Listeners.RecipeClickListener;
import com.example.afinal.Listeners.RecipeDetailsListener;
import com.example.afinal.Listeners.SimilarRecipesListener;
import com.example.afinal.Models.InstructionsResponse;
import com.example.afinal.Models.RecipeDetailsResponse;
import com.example.afinal.Models.SimilarRecipeResponse;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecipeDetails extends AppCompatActivity {
    int id;
    TextView name, source, summary;
    ImageView meal_image;
    RecyclerView ingredients, similar, instructions;
    RequestManager manager;
    ProgressDialog dialog;
    IngredientsAdapter ingredientsAdapter;
    SimilarRecipeAdapter similarRecipeAdapter;
    InstructionsAdapter instructionsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        findViews();

        id = Integer.parseInt(getIntent().getStringExtra("id"));
        manager = new RequestManager(this);
        manager.getRecipeDetails(recipeDetailsListener, id);
        manager.getSimilarRecipes(similarRecipesListener, id);
        manager.getInstructions(instructionsListener, id);
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading Details...");
        dialog.show();




    }

    private void findViews() {
        name = findViewById(R.id.textView_name);
        source = findViewById(R.id.textView_source);
        summary = findViewById(R.id.textView_summary);
        meal_image = findViewById(R.id.imageView_image);
        ingredients = findViewById(R.id.recycler_ingredients);
        similar = findViewById(R.id.recycler_similar);
        instructions = findViewById(R.id.recycler_instructions);
    }
    private final RecipeDetailsListener recipeDetailsListener = new RecipeDetailsListener() {
        @Override
        public void didFetch(RecipeDetailsResponse response, String message) {
            dialog.dismiss();
            name.setText(response.title);
            source.setText(response.sourceName);
            summary.setText(response.summary);
            Picasso.get().load(response.image).into(meal_image);

            ingredients.setHasFixedSize(true);
            ingredients.setLayoutManager(new LinearLayoutManager(RecipeDetails.this, LinearLayoutManager.HORIZONTAL, false));
            ingredientsAdapter = new IngredientsAdapter(RecipeDetails.this, response.extendedIngredients);
            ingredients.setAdapter(ingredientsAdapter);





        }

        @Override
        public void didError(String message) {
            Toast.makeText(RecipeDetails.this, message, Toast.LENGTH_SHORT).show();

        }
    };
    private final SimilarRecipesListener similarRecipesListener = new SimilarRecipesListener() {
        @Override
        public void didFetch(List<SimilarRecipeResponse> response, String message) {
            similar.setHasFixedSize(true);
            similar.setLayoutManager(new LinearLayoutManager(RecipeDetails.this, LinearLayoutManager.HORIZONTAL, false));
            similarRecipeAdapter = new SimilarRecipeAdapter(RecipeDetails.this, response, recipeClickListener);
            similar.setAdapter(similarRecipeAdapter);


        }

        @Override
        public void didError(String message) {
            Toast.makeText(RecipeDetails.this, message, Toast.LENGTH_SHORT).show();

        }
    };
    private final RecipeClickListener recipeClickListener = new RecipeClickListener() {
        @Override
        public void onRecipeClicked(String id) {
            startActivity(new Intent(RecipeDetails.this, RecipeDetails.class)
            .putExtra("id", id));


        }
    };
    private final InstructionsListener instructionsListener = new InstructionsListener() {
        @Override
        public void didFetch(List<InstructionsResponse> response, String message) {
            instructions.setHasFixedSize(true);
            instructions.setLayoutManager(new LinearLayoutManager(RecipeDetails.this,LinearLayoutManager.VERTICAL, false));
            instructionsAdapter = new InstructionsAdapter(RecipeDetails.this, response);
            instructions.setAdapter(instructionsAdapter);
        }

        @Override
        public void didError(String message) {

        }
    };
}