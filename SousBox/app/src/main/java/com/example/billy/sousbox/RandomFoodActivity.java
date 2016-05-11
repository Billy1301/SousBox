package com.example.billy.sousbox;

import android.content.Intent;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.billy.sousbox.Keys.Keys;
import com.example.billy.sousbox.adapters.CardAdapter;
import com.example.billy.sousbox.api.RecipeAPI;
import com.example.billy.sousbox.api.SpoonacularObjects;
import com.example.billy.sousbox.api.SpoonacularResults;
import com.example.billy.sousbox.fragments.FoodListsMainFragment;
import com.example.billy.sousbox.fragments.IngredientsFragment;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.Collections;
import java.util.LinkedList;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class RandomFoodActivity extends AppCompatActivity {

    /**
     * don't need to do this anymore... keep for backup...
     */


    private LinkedList<SpoonacularObjects> recipeLists;
    private CardAdapter adapter;
    private int i;

    RecipeAPI searchAPI;
    public final static String MASHAPLE_HEADER = Keys.getMASHAPLE();
    private String CHINESE = "chinese";
    private String foodType = "beef, pork, chicken, seafood";
    private int OFFSET = 50;

    public final static String RandomFoodID_KEY = "RecipeId";
    public final static String RandomIMAGE_KEY = "Image";

    SwipeFlingAdapterView flingContainer;
    FragmentTransaction transaction;
    FragmentManager fragmentManager;
    IngredientsFragment ingredientsActivityfrag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_food);

        ButterKnife.inject(this);
        setViews();
        recipeLists = new LinkedList<>();
        retrofitRecipe();
        adapter = new CardAdapter(this, recipeLists);

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

                Toast.makeText(RandomFoodActivity.this, "clicked", Toast.LENGTH_SHORT);
                Timber.i("Saved");
                recipeLists.remove(0);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onRightCardExit(Object dataObject) {

                recipeLists.remove(0);
                adapter.notifyDataSetChanged();
                Timber.i("Not like");
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                //int moreRecipe = OFFSET *2;
                // moreRetrofitRecipePulling(moreRecipe);
//                if(itemsInAdapter == 29){
//                moreRetrofitRecipePulling();
//                }

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

        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Intent intent = new Intent(RandomFoodActivity.this, MainActivity.class);


                Bundle recipeId = new Bundle(); //will bundle
                int recipe = recipeLists.get(itemPosition).getId();
                String image = recipeLists.get(itemPosition).getImage();
                recipeId.putInt(FoodListsMainFragment.RECIPE_ID_KEY, recipe);
                recipeId.putString(FoodListsMainFragment.IMAGE_KEY, image);


                Fragment ingredients = new IngredientsFragment();
                ingredients.setArguments(recipeId);
                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container_id, ingredients);
                transaction.addToBackStack(null);
                transaction.commit();

                Toast.makeText(RandomFoodActivity.this, "clicked", Toast.LENGTH_SHORT).show();

                Timber.i("Clicked!");

                startActivity(intent);

            }
        });

    }

    @OnClick(R.id.right)
    public void right() {
        /**
         * Trigger the right event manually.
         */
        flingContainer.getTopCardListener().selectRight();
        Timber.i("Saved");
        recipeLists.remove(0);
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.left)
    public void left() {
        flingContainer.getTopCardListener().selectLeft();
        recipeLists.remove(0);
        adapter.notifyDataSetChanged();
        Timber.i("Not like");

    }


    private void setViews() {
        flingContainer = (SwipeFlingAdapterView)findViewById(R.id.frame);
        fragmentManager = getSupportFragmentManager();

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
                adapter.notifyDataSetChanged();
                //arrayAdapter.notifyDataSetChanged(spoonacularResults.getResults());
            }

            @Override
            public void onFailure(Call<SpoonacularResults> call, Throwable t) {
                t.printStackTrace();

            }
        });
    }


    /**
     * this is for pulling more data and offset it by 100 to not pull the same recipe.
     */
    private void moreRetrofitRecipePulling() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        searchAPI = retrofit.create(RecipeAPI.class);

        Call<SpoonacularResults> call = searchAPI.searchMoreRecipe(100, foodType);
        call.enqueue(new Callback<SpoonacularResults>() {
            @Override
            public void onResponse(Call<SpoonacularResults> call, Response<SpoonacularResults> response) {
                SpoonacularResults spoonacularResults = response.body();

                if (spoonacularResults == null) {
                    return;
                }

                Timber.i("pulling more listing");
                Collections.addAll(recipeLists, spoonacularResults.getResults());
                adapter.notifyDataSetChanged();
                //arrayAdapter.notifyDataSetChanged(spoonacularResults.getResults());
            }

            @Override
            public void onFailure(Call<SpoonacularResults> call, Throwable t) {
                t.printStackTrace();

            }
        });
    }
}