package com.example.afinal.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afinal.Models.Step;
import com.example.afinal.R;

import java.util.List;

public class InstructionStepAdapter extends RecyclerView.Adapter<InstructionStepViewHolder> {
    Context context;
    List<Step> list;

    public InstructionStepAdapter(Context context, List<Step> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public InstructionStepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InstructionStepViewHolder(LayoutInflater.from(context).inflate(R.layout.list_instructions_step, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull InstructionStepViewHolder holder, int position) {

        holder.stepNumber.setText(String.valueOf(list.get(position).number));
        holder.stepTitle.setText(list.get(position).step);

        holder.ingredients.setHasFixedSize(true);
        holder.ingredients.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        InstructionsIngredientsAdapter instructionsIngredientsAdapter = new InstructionsIngredientsAdapter(context, list.get(position).ingredients);
        holder.ingredients.setAdapter(instructionsIngredientsAdapter);

        holder.equipments.setHasFixedSize(true);
        holder.equipments.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        InstructionsEquipmentsAdapter instructionsEquipmentsAdapter = new InstructionsEquipmentsAdapter(context, list.get(position).equipment);
        holder.equipments.setAdapter(instructionsEquipmentsAdapter);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
class InstructionStepViewHolder extends RecyclerView.ViewHolder {
    TextView stepNumber, stepTitle;
    RecyclerView equipments, ingredients;
    public InstructionStepViewHolder(@NonNull View itemView) {
        super(itemView);

        stepNumber = itemView.findViewById(R.id.instruction_step_number);
        stepTitle = itemView.findViewById(R.id.instruction_step_title);
        equipments = itemView.findViewById(R.id.instructions_equipment);
        ingredients = itemView.findViewById(R.id.instructions_ingredients);
    }
}
