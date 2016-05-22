package com.billy.sousbox.sousbox.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.billy.billy.sousbox.R;
import com.firebase.client.Firebase;

/**
 * Created by Billy on 5/22/16.
 */
public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progress;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.food_recipe_recycleview, container, false);
        setRetainInstance(true);
        Firebase.setAndroidContext(getContext());
        setHasOptionsMenu(true);
        setViews(v);



        return v;
    }



    private void setViews(View v){
        recyclerView = (RecyclerView)v.findViewById(R.id.recipeLists_recycleView_id);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progress = (ProgressBar) v.findViewById(R.id.main_progress_bar_id);
    }


}
