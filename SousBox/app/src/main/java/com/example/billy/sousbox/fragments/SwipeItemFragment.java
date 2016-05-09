package com.example.billy.sousbox.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.billy.sousbox.Keys.Keys;
import com.example.billy.sousbox.R;
import com.example.billy.sousbox.adapters.CardAdapter;
import com.example.billy.sousbox.api.RecipeAPI;
import com.example.billy.sousbox.api.SpoonacularObjects;
import com.example.billy.sousbox.api.SpoonacularResults;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

/**
 * Created by Billy on 5/4/16.
 */
public class SwipeItemFragment extends Fragment {

    private static final String TAG = "SwipeItemFragment: ";
    private ArrayList<SpoonacularObjects> recipeLists;
    private CardAdapter adapter;
    private RecipeAPI searchAPI;
    public final static String MASHAPLE_HEADER = Keys.getMASHAPLE();
    private String foodType;
    private int OFFSET = 100;
    private SwipeFlingAdapterView flingContainer;
    private Button left;
    private Button right;
    SpoonacularObjects spoonRecipe;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.swipe_recipe_fragment, container, false);

        ButterKnife.inject(getActivity());
        recipeLists = new ArrayList<>();
        foodType = getSearchFilter();
        retrofitRecipe();
        flingContainer = (SwipeFlingAdapterView) v.findViewById(R.id.frame);
        left = (Button) v.findViewById(R.id.left);
        right = (Button) v.findViewById(R.id.right);
        initiButtons();

        adapter = new CardAdapter(getContext(), recipeLists);
        flingContainer.setAdapter(adapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)

                recipeLists.remove(0);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();

                Timber.i("Saved");
                recipeLists.remove(0);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Toast.makeText(getActivity(), "Next", Toast.LENGTH_SHORT).show();

                recipeLists.remove(0);
                adapter.notifyDataSetChanged();
                Timber.i("Not like");
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                //moreRetrofitRecipePulling();
                //Toast.makeText(getActivity(), "Getting more listings", Toast.LENGTH_SHORT).show();

                // Ask for more data here
//                al.add("XML ".concat(String.valueOf(i)));
//                arrayAdapter.notifyDataSetChanged();
//                Log.d("LIST", "notified");
//                i++;
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });

        initiFlingListener();

        return v;
    }


    private void initiFlingListener(){
        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {

                Bundle recipeId = new Bundle();

//                for (SpoonacularObjects objects : recipeLists){
//                    Log.d(TAG, "Object in list: "+ objects.getId() + " and "+ getCurrentID);
//                }

                int getCurrentID = recipeLists.get(0).getId();
                String image = recipeLists.get(0).getImage();
                recipeId.putInt(FoodListsMainFragment.RECIPE_ID_KEY, getCurrentID);
                recipeId.putString(FoodListsMainFragment.IMAGE_KEY, image);

                Fragment ingredients = new IngredientsFragment();
                ingredients.setArguments(recipeId);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.fragment_container_id, ingredients);
                transaction.commit();

            }
        });
    }


    private void initiButtons(){

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                flingContainer.getTopCardListener().selectLeft();
                recipeLists.remove(0);
                adapter.notifyDataSetChanged();
                Timber.i("Not like");
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                flingContainer.getTopCardListener().selectRight();
                Timber.i("Saved");
                recipeLists.remove(0);
                adapter.notifyDataSetChanged();
            }
        });

    }

    private void retrofitRecipe() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        searchAPI = retrofit.create(RecipeAPI.class);

        Call<SpoonacularResults> call = searchAPI.searchRecipe(foodType);
        call.enqueue(new Callback<SpoonacularResults>() {
            @Override
            public void onResponse(Call<SpoonacularResults> call, Response<SpoonacularResults> response) {
                SpoonacularResults spoonacularResults = response.body();
                if (spoonacularResults == null) {
                    return;
                }

                Collections.addAll(recipeLists, spoonacularResults.getResults());
                long seed = System.nanoTime();
                Collections.shuffle(recipeLists, new Random(seed));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<SpoonacularResults> call, Throwable t) {
                t.printStackTrace();

            }
        });
    }

    private void moreRetrofitRecipePulling() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        searchAPI = retrofit.create(RecipeAPI.class);

        Call<SpoonacularResults> call = searchAPI.searchMoreRecipe(foodType);
        call.enqueue(new Callback<SpoonacularResults>() {
            @Override
            public void onResponse(Call<SpoonacularResults> call, Response<SpoonacularResults> response) {
                SpoonacularResults spoonacularResults = response.body();


                if(spoonacularResults == null){
                    return;
                }

                Timber.i("pulling more listing");
                Collections.addAll(recipeLists, spoonacularResults.getResults());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<SpoonacularResults> call, Throwable t) {
                t.printStackTrace();

            }
        });
    }

    private String getSearchFilter(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        return sharedPreferences.getString(PreferencesFragment.Shared_FILTER_KEY, "");
    }


}
