package com.example.afinal.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.afinal.Interface.Callback;
import com.example.afinal.Models.UploadRecipe;
import com.example.afinal.R;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder>{
    Context context;
    ArrayList<UploadRecipe> arrayList;
    Callback callback;


    public DataAdapter(Context context,ArrayList<UploadRecipe> arrayList, Callback callback){
        this.context = context;
        this.arrayList = arrayList;
        this.callback= callback;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View v=layoutInflater.inflate(R.layout.item_layout,parent,false);
        return new DataViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        UploadRecipe model=arrayList.get(position);
        //go to google and search glide android and copy the dependencies implementation and paste in in gradle module
        Glide.with(context).load(model.getPhotoUrl()).into(holder.imgView);
        holder.TvName.setText(model.getName());




        holder.imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }




    public class DataViewHolder extends RecyclerView.ViewHolder
    {

        ImageView imgView;
        TextView TvName;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            imgView=itemView.findViewById(R.id.imgView);
            TvName=itemView.findViewById(R.id.TVName);



        }
    }

    public void searchITemName(ArrayList<UploadRecipe> recipeArrayList) {
        arrayList=recipeArrayList;
        notifyDataSetChanged();

    }
}
