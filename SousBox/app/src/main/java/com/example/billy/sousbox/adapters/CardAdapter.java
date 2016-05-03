package com.example.billy.sousbox.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.billy.sousbox.R;
import com.example.billy.sousbox.api.SpoonacularObjects;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedList;
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

        String title = recipeData.get(position).getTitle();
        titleText.setText(title);

        String imageURI = recipeData.get(position).getImage();
        if (imageURI.isEmpty()) {
            imageURI = "R.drawable.ic_menu_gallery";
        }

        Picasso.with(getContext())
                .load("https://webknox.com/recipeImages/"+ imageURI)
                .placeholder(R.drawable.ic_menu_gallery)
                .resize(100, 100)
                .centerCrop()
                .into(image);


        return view;
    }

}
