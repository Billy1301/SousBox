package com.example.billy.sousbox;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.example.billy.sousbox.fragments.PreferencesFragment;
import com.example.billy.sousbox.fragments.FoodListsMainFragment;
import com.example.billy.sousbox.fragments.SavedRecipeFragment;
import com.example.billy.sousbox.fragments.SwipeItemFragment;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private FrameLayout fragContainer;
    private FoodListsMainFragment recipeListsFrag;
    private PreferencesFragment preferencesFragment;
    private SwipeItemFragment swipeItemActivityFrag;
    private CallbackManager callbackManager;
    private SavedRecipeFragment savedRecipeFrag;
    AHBottomNavigation bottomNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        initiViews();
        initiFragment();
        bottomNavi();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * start the fragment when app start
     */

    private void initiFragment(){
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);

        if(bottomNavigation.getCurrentItem() == 0){
        fragmentTransaction.replace(R.id.fragment_container_id, recipeListsFrag);
        } else if (bottomNavigation.getCurrentItem()==1){
            fragmentTransaction.replace(R.id.fragment_container_id, swipeItemActivityFrag);
        }else if (bottomNavigation.getCurrentItem()==2){
            fragmentTransaction.replace(R.id.fragment_container_id, preferencesFragment);
        }else if (bottomNavigation.getCurrentItem()==3){
            fragmentTransaction.replace(R.id.fragment_container_id, savedRecipeFrag);
        }

        fragmentTransaction.commit();

    }

    /**
     * setting up the views
     */
    private void initiViews(){
        callbackManager = CallbackManager.Factory.create();
        fragContainer = (FrameLayout)findViewById(R.id.fragment_container_id);
        recipeListsFrag = new FoodListsMainFragment();
        preferencesFragment = new PreferencesFragment();
        fragmentManager = getSupportFragmentManager();
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }

    /**
     * bottom Navi SDK  - each tab will change to the fragment of choose
     */
    private void bottomNavi(){
        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.tab_1, R.drawable.ic_menu_gallery, R.color.colorPrimary);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.tab_2, R.drawable.ic_random, R.color.colorPrimary);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.tab_3, R.drawable.ic_menu_manage, R.color.colorPrimaryDark);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(R.string.tab_4, R.drawable.ic_saved_icon, R.color.colorPrimaryDark);

        // Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);
        bottomNavigation.setBehaviorTranslationEnabled(false);
        // Force to tint the drawable (useful for font with icon for example)
        bottomNavigation.setForceTint(true);
        // Force the titles to be displayed (against Material Design guidelines!)
        bottomNavigation.setForceTitlesDisplay(true);
        // Use colored navigation with circle reveal effect
        bottomNavigation.setColored(true);
        // Set current item programmatically
        bottomNavigation.setCurrentItem(0);
        // Set listener
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, boolean wasSelected) {

                if(position ==0) {
                    toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    recipeListsFrag = new FoodListsMainFragment();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.fragment_container_id, recipeListsFrag);
                    fragmentTransaction.commit();
                }

                if(position ==1) {
                    toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    swipeItemActivityFrag = new SwipeItemFragment();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.fragment_container_id, swipeItemActivityFrag);
                    fragmentTransaction.commit();
                }

                if(position ==2) {
                    toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    preferencesFragment = new PreferencesFragment();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.fragment_container_id, preferencesFragment);
                    fragmentTransaction.commit();
                }

                if(position ==3) {
                    toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    if(isFacebookLoggedIn()) {
                        savedRecipeFrag = new SavedRecipeFragment();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.replace(R.id.fragment_container_id, savedRecipeFrag);
                        fragmentTransaction.commit();
                    } else {
                        Toast.makeText(MainActivity.this, "Please login to use this feature", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * to check if Facebook login or not
     * @return
     */
    private boolean isFacebookLoggedIn(){
        return AccessToken.getCurrentAccessToken() !=null;
    }
}
