package com.billy.sousbox.sousbox.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.billy.sousbox.sousbox.firebaseModels.Recipes;
import com.billy.billy.sousbox.R;
import com.billy.sousbox.sousbox.adapters.FirebaseRecipeVIewHolder;
import com.facebook.AccessToken;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseRecyclerAdapter;
import com.squareup.picasso.Picasso;

/**
 * Created by Billy on 5/2/16.
 */
public class SavedRecipeFragment extends Fragment {

    RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<Recipes, FirebaseRecipeVIewHolder> mAdapter;
    private Firebase firebaseChild;
    private Firebase firebaseRef;
    private ProgressBar progress;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.food_recipe_recycleview, container, false);
        setRetainInstance(true);
        Firebase.setAndroidContext(getContext());
        setViews(v);
        setFirebase();
        checkNetwork();
        fireBaseAdapter();
        firebaseRecycerItemClicker();
        return v;
    }

    /**
     * this is to connect to firebase and pull the saved recipe for user on multiply device
     */
    private void fireBaseAdapter(){
        mAdapter = new FirebaseRecyclerAdapter<Recipes, FirebaseRecipeVIewHolder>(Recipes.class,
                R.layout.recycleview_custom_layout, FirebaseRecipeVIewHolder.class, firebaseChild) {
            @Override
            public void populateViewHolder(FirebaseRecipeVIewHolder holder, final Recipes recipes, final int position) {
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
    }

    /**
     * check network and notify if not connected to any network
     */
    private void checkNetwork(){
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            Toast.makeText(getActivity(), getString(R.string.no_network), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Set the itemClicker
     *
     * bundle to pass the ID into the API ingredient called
     */
    private void firebaseRecycerItemClicker() {
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                Bundle recipeId = new Bundle();
                int recipeID = mAdapter.getItem(position).getId();
                String image = mAdapter.getItem(position).getImage();
                recipeId.putInt(FoodListsMainFragment.RECIPE_ID_KEY, recipeID);
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
                mAdapter.getRef(position).removeValue();

            }
        }));
    }

    /**
     * setting up ClickerListener for firebase
     */
    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private SavedRecipeFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final SavedRecipeFragment.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    private boolean isFacebookLoggedIn(){
        return AccessToken.getCurrentAccessToken() !=null;
    }

    private void setFirebase(){
        String facebookUserID = getAuthData();
        firebaseRef = new Firebase(SwipeItemFragment.Firebase_Link + facebookUserID);
        firebaseChild = firebaseRef.child("recipes");
    }

    public void setViews(android.view.View v){
        recyclerView = (RecyclerView)v.findViewById(R.id.recipeLists_recycleView_id);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progress = (ProgressBar) v.findViewById(R.id.main_progress_bar_id);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }

    private String getAuthData() {
        Firebase firebase = new Firebase(SwipeItemFragment.Firebase_Link);
        AuthData authData = firebase.getAuth();
        String uID = authData.getUid();
        return uID;
    }

}
