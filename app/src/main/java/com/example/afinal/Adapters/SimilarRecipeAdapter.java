package com.example.afinal.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afinal.Listeners.RecipeClickListener;
import com.example.afinal.Models.SimilarRecipeResponse;
import com.example.afinal.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SimilarRecipeAdapter extends RecyclerView.Adapter<SimilarRecipeViewHolder> {
    Context context;
    List<SimilarRecipeResponse> list;
    RecipeClickListener listener;

    public SimilarRecipeAdapter(Context context, List<SimilarRecipeResponse> list, RecipeClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SimilarRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SimilarRecipeViewHolder(LayoutInflater.from(context).inflate(R.layout.list_similar_recipe, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull SimilarRecipeViewHolder holder, int position) {
        holder.similar_title.setText(list.get(position).title);
        holder.similar_title.setSelected(true);
        holder.time.setText(list.get(position).readyInMinutes+"Minutes");
        holder.similar_servings.setText(list.get(position).servings+"Servings");
        Picasso.get().load("https://spoonacular.com/recipeImages/"+list.get(position).id+"-556x370."+list.get(position).imageType).into(holder.imageView_similar);

        holder.similar_recipe_holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRecipeClicked(String.valueOf(list.get(holder.getAdapterPosition()).id));
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class SimilarRecipeViewHolder extends RecyclerView.ViewHolder{
    CardView similar_recipe_holder;
    TextView similar_title, similar_servings, time;
    ImageView imageView_similar;

    public SimilarRecipeViewHolder(@NonNull View itemView) {
        super(itemView);

        similar_recipe_holder = itemView.findViewById(R.id.similar_recipe_holder);
        similar_title = itemView.findViewById(R.id.similar_title);
        similar_servings = itemView.findViewById(R.id.similar_servings);
        imageView_similar = itemView.findViewById(R.id.imageView_similar);
        time = itemView.findViewById(R.id.similar_time);
    }
}