package com.billy.sousbox.sousbox.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.billy.sousbox.sousbox.api.RecipeAPI;
import com.billy.sousbox.sousbox.api.recipeModels.SpoonacularObjects;
import com.billy.sousbox.sousbox.api.recipeModels.SpoonacularResults;
import com.billy.billy.sousbox.R;
import com.billy.sousbox.sousbox.adapters.RecycleViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Billy on 5/4/16.
 */
public class FoodListsMainFragment extends Fragment implements RecycleViewAdapter.RecipeScrollListener {

    //region Private Variables
    private RecycleViewAdapter recycleAdapter;
    private RecyclerView recyclerView;
    private ArrayList<SpoonacularObjects> recipeLists;
    private String querySearch;
    private RecipeAPI searchAPI;
    private ProgressBar progress;
    private int offset = 0;
    //endregion Private Variables

    public final static String RECIPE_ID_KEY = "recipeID";
    public final static String IMAGE_KEY = "image";
    int position;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.food_recipe_recycleview, container, false);
        setRetainInstance(true);
        checkNetwork();
        setViews(v);
        retrofitRecipe();
        recycleAdapterItemClicker();
        return v;
    }

    private void setViews(View v){
        progress = (ProgressBar) v.findViewById(R.id.main_progress_bar_id);
        recyclerView = (RecyclerView) v.findViewById(R.id.recipeLists_recycleView_id);
        recipeLists = new ArrayList<>();
        querySearch = getSearchFilter();
        progress.setVisibility(View.VISIBLE);
    }

    /**
     * get filter preferences from fragment
     */
    private String getSearchFilter(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        return sharedPreferences.getString(PreferencesFragment.Shared_FILTER_KEY, "");
    }

    /**
     * check network and notify if not connected to any network
     */
    public void checkNetwork(){
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            Toast.makeText(getActivity(), getString(R.string.no_network), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Set the itemClicker for the recycleView
     *
     * bundle to pass the ID into the API ingredients called
     */
    private void recycleAdapterItemClicker() {
        recycleAdapter = new RecycleViewAdapter(recipeLists, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleAdapter.setOnItemClickListener(new RecycleViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                recipeLists.get(position);
                Bundle recipeId = new Bundle();
                int recipe = recipeLists.get(position).getId();
                String image = recipeLists.get(position).getImage();
                recipeId.putInt(RECIPE_ID_KEY, recipe);
                recipeId.putString(IMAGE_KEY, image);
                Fragment ingredients = new IngredientsFragment();
                ingredients.setArguments(recipeId);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container_id, ingredients);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    private void retrofitRecipe() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SwipeItemFragment.SPOON_API_LINK)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        searchAPI = retrofit.create(RecipeAPI.class);
        Call<SpoonacularResults> call = searchAPI.recipesAPIcall(offset, querySearch);
        call.enqueue(new Callback<SpoonacularResults>() {
            @Override
            public void onResponse(Call<SpoonacularResults> call, Response<SpoonacularResults> response) {
                SpoonacularResults spoonacularResults = response.body();
                if (spoonacularResults == null) {
                    return;
                }
                Collections.addAll(recipeLists, spoonacularResults.getResults());
                if (recyclerView != null) {
//                    long seed = System.nanoTime();
//                    Collections.shuffle(recipeLists, new Random(seed));
                    recyclerView.setAdapter(recycleAdapter);
                    recycleAdapter.notifyItemRangeInserted(position, spoonacularResults.getResults().length);
//                    recycleAdapter.notifyDataSetChanged();
                    progress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<SpoonacularResults> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    /**
     * to get more listing when almost to end
     * @param position
     */
    @Override
    public void loadNewRecipes(int position) {
        this.position = position;
        offset += 50;
        retrofitRecipe();
    }



}
