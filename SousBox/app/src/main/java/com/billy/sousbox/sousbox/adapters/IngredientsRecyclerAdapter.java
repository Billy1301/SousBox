package com.billy.sousbox.sousbox.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.billy.billy.sousbox.R;
import com.billy.sousbox.sousbox.api.recipeModels.GetRecipeObjects;
import com.billy.sousbox.sousbox.api.recipeModels.SpoonGetRecipe;
import com.billy.sousbox.sousbox.api.recipeModels.SpoonacularObjects;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by Billy on 5/18/16.
 */
public class IngredientsRecyclerAdapter extends RecyclerView.Adapter<IngredientsRecyclerAdapter.RecyclerViewHolder> {

    private ArrayList<SpoonGetRecipe> data;
    private Context context;
    private ArrayList ingredientLists;
    private GetRecipeObjects getRecipeObjects;



    public IngredientsRecyclerAdapter(ArrayList<SpoonGetRecipe> data) {
        this.data = data;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView ingredientsName;

        public RecyclerViewHolder (final View itemView) {
            super(itemView);
            ingredientsName = (TextView) itemView.findViewById(R.id.ingredients_custom_name_id);

        }
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

        holder.ingredientsName.setText(data.get(position).getOriginalString());

    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.ingredients_recycle_custom_layout, parent, false);
        RecyclerViewHolder vh = new RecyclerViewHolder(view);
        return vh;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
