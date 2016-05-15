package com.billy.sousbox.sousbox.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.billy.billy.sousbox.R;

/**
 * Created by Billy on 5/9/16.
 */
public class FirebaseRecipeVIewHolder extends RecyclerView.ViewHolder {

    public ImageView recipeImage;
    public TextView titleName;
    public TextView recipeID;
    private static OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public FirebaseRecipeVIewHolder(final View itemView) {
        super(itemView);

        recipeImage = (ImageView) itemView.findViewById(R.id.saved_recipe_imageOne_id);
        titleName = (TextView) itemView.findViewById(R.id.saved_recipe_imageOne_title_id);
        recipeID = (TextView) itemView.findViewById(R.id.recipeID_ID);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onItemClick(itemView, getLayoutPosition());
            }
        });
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
