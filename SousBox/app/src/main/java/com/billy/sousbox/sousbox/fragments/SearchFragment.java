package com.billy.sousbox.sousbox.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.billy.billy.sousbox.R;
import com.billy.sousbox.sousbox.adapters.RecycleViewAdapter;
import com.billy.sousbox.sousbox.adapters.RecyclerClicker.ClickListener;
import com.billy.sousbox.sousbox.adapters.RecyclerClicker.EndlessRecyclerOnScrollListener;
import com.billy.sousbox.sousbox.adapters.RecyclerClicker.RecyclerTouchListener;
import com.billy.sousbox.sousbox.adapters.SearchRecyclerAdapter;
import com.billy.sousbox.sousbox.api.RecipeAPI;
import com.billy.sousbox.sousbox.api.recipeModels.SpoonacularObjects;
import com.billy.sousbox.sousbox.api.recipeModels.SpoonacularResults;
import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Billy on 5/22/16.
 */
public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progress;
    private RecipeAPI searchAPI;
    private ArrayList<SpoonacularObjects> recipeLists;
    private String searchQuery;
    private SearchRecyclerAdapter recycleAdapter;
    private LinearLayoutManager linearLayoutManager;
    private int offset = 0;
    private int position;


    /**
     * this is to get filters clickbox
     * @param searchQuery
     */
    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.food_recipe_recycleview, container, false);
        setRetainInstance(true);
        Firebase.setAndroidContext(getContext());
        setHasOptionsMenu(true);
        setViews(v);
        retrofitRecipe();
        setRecyclerItemClicker();
        setEndlessScroll();
        return v;
    }

    /**
     * ItemClicker & bundle to pull ingredients page
     */
    private void setRecyclerItemClicker(){
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                recipeLists.get(position);
                Bundle recipeId = new Bundle();
                int recipe = recipeLists.get(position).getId();
                String image = recipeLists.get(position).getImage();
                recipeId.putInt(FoodListsMainFragment.RECIPE_ID_KEY, recipe);
                recipeId.putString(FoodListsMainFragment.IMAGE_KEY, image);
                Fragment ingredients = new IngredientsFragment();
                ingredients.setArguments(recipeId);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container_id, ingredients);
                transaction.addToBackStack(null);
                transaction.commit();
            }

            @Override
            public void onLongClick(View view, int position) {
                //nothing to do here. can't delete
            }
        }));
    }

    private void setViews(View v){
        recyclerView = (RecyclerView)v.findViewById(R.id.recipeLists_recycleView_id);
        recipeLists = new ArrayList<>();
        recycleAdapter = new SearchRecyclerAdapter(recipeLists);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recycleAdapter);
        progress = (ProgressBar) v.findViewById(R.id.main_progress_bar_id);
        progress.setVisibility(View.VISIBLE);
    }

    /**
     * pull more recipes when at end
     */
    private void setEndlessScroll(){
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                Toast.makeText(getContext(), "Loading more", Toast.LENGTH_SHORT).show();
                offset += 20;
                retrofitRecipe();
            }
        });
    }

    private void retrofitRecipe() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SwipeItemFragment.SPOON_API_LINK)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        searchAPI = retrofit.create(RecipeAPI.class);
        Call<SpoonacularResults> call = searchAPI.recipesAPIcall(offset, searchQuery);
        call.enqueue(new Callback<SpoonacularResults>() {
            @Override
            public void onResponse(Call<SpoonacularResults> call, Response<SpoonacularResults> response) {
                SpoonacularResults spoonacularResults = response.body();
                if (spoonacularResults == null) {
                    return;
                }
                Collections.addAll(recipeLists, spoonacularResults.getResults());

                if (recyclerView != null) {
                    position = recycleAdapter.getItemCount();
                    recycleAdapter.notifyItemRangeInserted(position, spoonacularResults.getResults().length);
                    progress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<SpoonacularResults> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


}
