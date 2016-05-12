package com.example.billy.sousbox.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.billy.sousbox.R;
import com.example.billy.sousbox.adapters.CardAdapter;
import com.example.billy.sousbox.api.RecipeAPI;
import com.example.billy.sousbox.api.SpoonacularObjects;
import com.example.billy.sousbox.api.SpoonacularResults;
import com.facebook.AccessToken;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
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
 * this fragment is for the swipe left or right recipe
 */
public class SwipeItemFragment extends Fragment {

    private static final String TAG = "SwipeItemFragment: ";
    private ArrayList<SpoonacularObjects> recipeLists;
    private CardAdapter adapter;
    private RecipeAPI searchAPI;
    private String foodType;
    private int OFFSET = 0;
    private SwipeFlingAdapterView flingContainer;
    private Button dislikeButton;
    private Button likeButton;
    private Firebase firebaseRef;
    private Firebase recipeRef;
    private Firebase facebookUserRef;
    private ProgressBar swipeProgressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.swipe_recipe_fragment, container, false);
        ButterKnife.inject(getActivity());
        setRetainInstance(true);
        checkNetwork();
        setViews(v);
        swipeRecipePulling();
        initiButtons();
        setWhereToSave();
        setupFlingContainer();
        initiFlingListener();
        return v;
    }

    private void setViews(View v){
        recipeLists = new ArrayList<>();
        foodType = getSearchFilter();
        flingContainer = (SwipeFlingAdapterView) v.findViewById(R.id.swipe_frame);
        dislikeButton = (Button) v.findViewById(R.id.left);
        likeButton = (Button) v.findViewById(R.id.right);
        swipeProgressBar = (ProgressBar)v.findViewById(R.id.swipe_progress_bar_id);
        swipeProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * This setup how the swipe container works, left to remove, right to save it
     */
    private void setupFlingContainer(){
        adapter = new CardAdapter(getContext(), recipeLists);
        flingContainer.setAdapter(adapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {

            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //dislike swipe
                recipeLists.remove(0);
                adapter.notifyDataSetChanged();
            }

            /**
             * pushing like recipe to firebase to pull back from other device
             * @param dataObject
             */
            @Override
            public void onRightCardExit(Object dataObject) {

                if(isFacebookLoggedIn()){
                    recipeRef.push().setValue(recipeLists.get(0));
                } else{
                    Toast.makeText(getContext(),"Login to save", Toast.LENGTH_SHORT).show();
                }
                recipeLists.remove(0);
                adapter.notifyDataSetChanged();
            }

            /**
             * pull more from API call when card is about to be empty
             * @param itemsInAdapter
             */
            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {

                if(recipeLists.size() > 3){
                    swipeRecipePullingTwo();
                }
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });
    }

    /**
     * place to save the recipe
     */
    private void setWhereToSave(){
        if (isFacebookLoggedIn()){
            String facebookUserID = getAuthData();
            firebaseRef = new Firebase("https://sous-box.firebaseio.com/");
            facebookUserRef = firebaseRef.child(facebookUserID);
            recipeRef = facebookUserRef.child("recipes");
        }
        else {

        }

    }

    /**
     * check network and notify if not connected to any network
     */
    private void checkNetwork(){
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            Toast.makeText(getActivity(), "No network detected", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isFacebookLoggedIn(){
        return AccessToken.getCurrentAccessToken() !=null;
    }

    /**
     * to save the info and sent to ingredients page when clicked
     */
    private void initiFlingListener(){
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

    /**
     * this is for user who want to press button instead of swiping. it works the same as swiping
     */
    private void initiButtons(){
        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flingContainer.getTopCardListener().selectLeft();
            }
        });
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flingContainer.getTopCardListener().selectRight();
            }
        });

    }


    /**
     * calling api
     */
    private void swipeRecipePulling() {
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
                swipeProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<SpoonacularResults> call, Throwable t) {
                t.printStackTrace();

            }
        });
    }


    /**
     * calling api when list almost out to offset
     */
    private void swipeRecipePullingTwo() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        searchAPI = retrofit.create(RecipeAPI.class);

        Call<SpoonacularResults> call = searchAPI.swipeSearchMoreRecipes(foodType);
        call.enqueue(new Callback<SpoonacularResults>() {
            @Override
            public void onResponse(Call<SpoonacularResults> call, Response<SpoonacularResults> response) {
                SpoonacularResults spoonacularResults = response.body();

                if(spoonacularResults == null){
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


    private String getSearchFilter(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        return sharedPreferences.getString(PreferencesFragment.Shared_FILTER_KEY, "");
    }

    /**
     * get fb login
     * @return
     */
    private String getAuthData() {
        Firebase firebase = new Firebase("https://sous-box.firebaseio.com");
        AuthData authData = firebase.getAuth();
        String uID = authData.getUid();
        return uID;
    }
}
