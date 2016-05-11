package com.example.billy.sousbox.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.billy.sousbox.R;

/**
 * Created by Billy on 5/9/16.
 */
public class FirebaseRecipeVIewHolder extends RecyclerView.ViewHolder {

    public ImageView recipeImage;
    public TextView titleName;
    public TextView recipeID;


    public FirebaseRecipeVIewHolder(View itemView) {
        super(itemView);

        recipeImage = (ImageView) itemView.findViewById(R.id.saved_recipe_imageOne_id);
        titleName = (TextView) itemView.findViewById(R.id.saved_recipe_imageOne_title_id);
        recipeID = (TextView) itemView.findViewById(R.id.recipeID_ID);
    }


    public void setRecipeImage(ImageView recipeImage) {
        this.recipeImage = recipeImage;
    }

    public void setTitleName(TextView titleName) {
        this.titleName = titleName;
    }

    public void setRecipeID(TextView recipeID) {
        this.recipeID = recipeID;
    }
}
