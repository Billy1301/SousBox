package com.example.billy.sousbox.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.billy.sousbox.R;
import com.example.billy.sousbox.api.recipeModels.SpoonacularObjects;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Billy on 5/2/16.
 */
public class CardAdapter extends ArrayAdapter<SpoonacularObjects> {

    List<SpoonacularObjects> recipeData;


    public CardAdapter(Context context, List<SpoonacularObjects> recipeData) {
        super(context, -1, recipeData);
        this.recipeData = recipeData;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        TextView titleText = (TextView) view.findViewById(R.id.swipeCard_titleView);
        ImageView image = (ImageView) view.findViewById(R.id.swipeCardImage);

        String title = recipeData.get(0).getTitle();
        titleText.setText(title);

        String imageURI = recipeData.get(0).getImage();
        if (imageURI.isEmpty()) {
            imageURI = "R.drawable.blank_white.png";
        }

        Picasso.with(getContext())
                .load("https://webknox.com/recipeImages/"+ imageURI)
                .placeholder(R.drawable.blank_white)
                .resize(400, 400)
                .centerCrop()
                .into(image);

        return view;
    }


}
