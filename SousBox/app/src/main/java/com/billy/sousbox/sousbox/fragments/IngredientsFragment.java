package com.billy.sousbox.sousbox.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.billy.sousbox.sousbox.api.recipeModels.SpoonGetRecipe;
import com.billy.sousbox.sousbox.firebaseModels.Recipes;
import com.billy.billy.sousbox.R;
import com.billy.sousbox.sousbox.api.recipeModels.GetRecipeObjects;
import com.billy.sousbox.sousbox.api.RecipeAPI;
import com.facebook.AccessToken;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Billy on 5/3/16.
 */
public class IngredientsFragment extends Fragment {

    //region Private Variables
    private ArrayList<String> ingredientLists;
    private ArrayAdapter adapter;
    private RecipeAPI searchAPI;
    private int id;
    private String image;
    private ListView ingredientsLV;
    private TextView title;
    private ImageView recipeImage;
    private Button instructionButton;
    private Button servingsButton;
    private GetRecipeObjects getRecipeObjects;
    private ProgressBar progress;
    private Bundle instructionBundle;
    private Firebase firebaseRef;
    private Firebase firebaseRecipe;
    //endregion Private Variables

    public final static String URL_KEY = "URL";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.ingredients_layout_fragment, container, false);
        setRetainInstance(true);
        setViews(v);
        if(isFacebookLoggedIn()){
          firebase();
        }
        setHasOptionsMenu(true);
        progress.setVisibility(View.VISIBLE);
        retrofitRecipeID();
        setInstructionAndServingButton();
        return v;
    }

    private void setViews(View v){
        recipeImage = (ImageView) v.findViewById(R.id.ingredients_imageView_id);
        title = (TextView) v.findViewById(R.id.ingredients_titleView_id);
        ingredientsLV = (ListView)v.findViewById(R.id.ingredients_listView_id);
        instructionButton = (Button) v.findViewById(R.id.instruction_button_id);
        progress = (ProgressBar) v.findViewById(R.id.ingredients_progress_bar_id);
        servingsButton = (Button)v.findViewById(R.id.ingredients_serving_button_id);

    }

    private void firebase(){
        String facebookUserID = getAuthData();
        firebaseRef = new Firebase(SwipeItemFragment.Firebase_Link + facebookUserID );
        firebaseRecipe = firebaseRef.child("recipes");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    /**
     * For share intent and saving recipe
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        String getURL = getRecipeObjects.getSpoonacularSourceUrl();

        if (id == R.id.share_settings) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, getURL);
            share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_msg));
            startActivity(Intent.createChooser(share, getString(R.string.sharing)));
            return true;
        }
            if (id == R.id.bookmark) {
                if(isFacebookLoggedIn()) {
                    //create a new object to match firebase data
                    Recipes recipes = new Recipes();
                    recipes.setId(getRecipeObjects.getId());
                    recipes.setImage(image);
                    recipes.setTitle(getRecipeObjects.getTitle());
                    firebaseRecipe.push().setValue(recipes);
                    item.setEnabled(false);
                    Toast.makeText(getContext(), R.string.recipe_saved, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), R.string.login_to_save, Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * To pull recipe by ID for ingredients
     */
    private void retrofitRecipeID() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        searchAPI = retrofit.create(RecipeAPI.class);
        Bundle bundle = getArguments();
        id = bundle.getInt(FoodListsMainFragment.RECIPE_ID_KEY);
        image = bundle.getString(FoodListsMainFragment.IMAGE_KEY);

        Call<GetRecipeObjects> call = searchAPI.getRecipeIngredients(id);
        call.enqueue(new Callback<GetRecipeObjects>() {
            @Override
            public void onResponse(Call<GetRecipeObjects> call, Response<GetRecipeObjects> response) {
                getRecipeObjects = response.body();
                if (getRecipeObjects == null) {
                    return;
                }
                title.setText(getRecipeObjects.getTitle());
                String imageURI = image;
                if (imageURI.isEmpty()) {
                    imageURI = "R.drawable.blank_white.png";
                }
                Picasso.with(getContext())
                        .load("https://webknox.com/recipeImages/" + imageURI)
                        .resize(400, 400)
                        .centerCrop()
                        .into(recipeImage);

                ingredientLists = new ArrayList<>();
                SpoonGetRecipe[] recipe = getRecipeObjects.getExtendedIngredients();
                for (int i = 0; i < recipe.length; i++) {
                    ingredientLists.add(recipe[i].getOriginalString());
                }
                adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, ingredientLists);
                ingredientsLV.setAdapter(adapter);
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<GetRecipeObjects> call, Throwable t) {
                t.printStackTrace();

            }
        });
    }

    /**
     * to use for checking Facebook login
     * @return
     */
    private boolean isFacebookLoggedIn(){
        return AccessToken.getCurrentAccessToken() !=null;
    }


    /**
     * Direct user to webview of instructions and how much ingredients to buy per servings
     */
    private void setInstructionAndServingButton(){
        instructionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = getRecipeObjects.getSourceUrl();
                instructionBundle = new Bundle();
                instructionBundle.putString(URL_KEY, url);
                setWebViewFragment();
            }
        });

        servingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String servingURL = getRecipeObjects.getSpoonacularSourceUrl();
                instructionBundle = new Bundle();
                instructionBundle.putString(URL_KEY, servingURL);
                setWebViewFragment();
            }
        });
    }

    /**
     * to send bundle information to fragment to display view
     */
    private void setWebViewFragment(){
        Fragment instructionsFrag = new InstructionsFragment();
        instructionsFrag.setArguments(instructionBundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.fragment_container_id, instructionsFrag);
        transaction.commit();

    }

    /**
     * to get Facebook ID
     * @return
     */
    private String getAuthData() {
        Firebase firebase = new Firebase("https://sous-box.firebaseio.com");
        AuthData authData = firebase.getAuth();
        String uID = authData.getUid();
        return uID;
    }
}
