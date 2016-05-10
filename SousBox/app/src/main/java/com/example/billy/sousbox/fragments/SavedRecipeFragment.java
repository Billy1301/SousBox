package com.example.billy.sousbox.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.billy.sousbox.R;
import com.example.billy.sousbox.adapters.FirebaseRecipeVIewHolder;
import com.example.billy.sousbox.firebaseModels.FirebaseObjects;
import com.example.billy.sousbox.firebaseModels.Recipes;
import com.facebook.AccessToken;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseRecyclerAdapter;
import com.squareup.picasso.Picasso;

/**
 * Created by Billy on 5/2/16.
 */
public class SavedRecipeFragment extends Fragment {

    RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<Recipes, FirebaseRecipeVIewHolder> mAdapter;
    Firebase mRef;
    Firebase ref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.food_recipe_recycleview, container, false);
        setRetainInstance(true);
        Firebase.setAndroidContext(getContext());

        if (isFacebookLoggedIn()){
            Log.d("Savedfragemnt", "Facebook Logged in");
            initFirebase();
        } else {
            ref = new Firebase("https://sous-box.firebaseio.com/users/");
            mRef = ref.child("recipes");
        }

        setViews(v);



        mAdapter = new FirebaseRecyclerAdapter<Recipes, FirebaseRecipeVIewHolder>(Recipes.class, R.layout.recycleview_custom_layout, FirebaseRecipeVIewHolder.class, mRef) {
            @Override
            public void populateViewHolder(FirebaseRecipeVIewHolder holder, Recipes recipes, final int position) {
                String titleNa = recipes.getTitle();


                holder.titleName.setText(recipes.getTitle());


                String imageURI = recipes.getImage();
                if (imageURI.isEmpty()) {
                    imageURI = "R.drawable.blank_white.png";
                }

                Picasso.with(getContext())
                        .load("https://webknox.com/recipeImages/"+ imageURI)
                        .resize(300, 300)
                        .centerCrop()
                        .into(holder.recipeImage);

            }
        };
        recyclerView.setAdapter(mAdapter);



        return v;
    }

    private boolean isFacebookLoggedIn(){
        return AccessToken.getCurrentAccessToken() !=null;
    }


    private void initFirebase(){
        String facebookUserID = getAuthData();
        ref = new Firebase("https://sous-box.firebaseio.com/users/" + facebookUserID);
        mRef = ref.child("recipes");
    }

    public void setViews(android.view.View v){
        recyclerView = (RecyclerView)v.findViewById(R.id.recipeLists_recycleView_id);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }

    private String getAuthData() {
        Firebase firebase = new Firebase("https://sous-box.firebaseio.com");
        AuthData authData = firebase.getAuth();
        String uID = authData.getUid();
        return uID;
    }



}
