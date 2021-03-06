package com.billy.sousbox.sousbox.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.billy.billy.sousbox.R;
import com.billy.sousbox.sousbox.api.recipeModels.SpoonacularObjects;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by Billy on 5/22/16.
 */
public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.RecyclerViewHolder> {

    private ArrayList<SpoonacularObjects> data;
    private Context context;

    public SearchRecyclerAdapter(ArrayList<SpoonacularObjects> data) {
        this.data = data;
    }

    // this is where we setup TextView
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView recipeImageView;
        TextView recipeTitleText;

        public RecyclerViewHolder (final View itemView) {
            super(itemView);
            recipeImageView = (ImageView) itemView.findViewById(R.id.saved_recipe_imageOne_id);
            recipeTitleText = (TextView) itemView.findViewById(R.id.saved_recipe_imageOne_title_id);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.recipeTitleText.setText(data.get(position).getTitle());
        String imageURI = data.get(position).getImage();
        if (imageURI.isEmpty()) {
            imageURI = "R.drawable.blank_white.png";
        }

        Glide
                .with(context)
                .load("https://webknox.com/recipeImages/"+ imageURI)
                .centerCrop()
                .placeholder(R.drawable.blank_white)
                .crossFade()
                .override(150,150)
                .into(holder.recipeImageView);

    }



    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycleview_custom_layout, parent, false);
        RecyclerViewHolder vh = new RecyclerViewHolder(view);
        return vh;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


}
