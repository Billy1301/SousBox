package com.billy.sousbox.sousbox;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.billy.billy.sousbox.R;
import com.billy.sousbox.sousbox.fragments.PreferencesFragment;
import com.billy.sousbox.sousbox.fragments.FoodListsMainFragment;
import com.billy.sousbox.sousbox.fragments.SavedRecipeFragment;
import com.billy.sousbox.sousbox.fragments.SwipeItemFragment;
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
    private AHBottomNavigation bottomNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        initiViews();
        checkNetwork();
        bottomNavi();

        //prevent switch back to first frag when rotating
        if(savedInstanceState == null){
            setupFragmentOnFirstLoad();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * start the fragment when app start
     */

    private void setupFragmentOnFirstLoad(){
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        if(isFacebookLoggedIn()){
            fragmentTransaction.replace(R.id.fragment_container_id, swipeItemActivityFrag);
        } else {
        fragmentTransaction.replace(R.id.fragment_container_id, preferencesFragment);
        }
        fragmentTransaction.commit();
    }

    /**
     * check network and notify if not connected to any network
     */
    public void checkNetwork(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            Toast.makeText(MainActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * setting up the views
     */
    private void initiViews(){
        callbackManager = CallbackManager.Factory.create();
        fragContainer = (FrameLayout)findViewById(R.id.fragment_container_id);
        fragmentManager = getSupportFragmentManager();
        recipeListsFrag = new FoodListsMainFragment();
        preferencesFragment = new PreferencesFragment();
        swipeItemActivityFrag = new SwipeItemFragment();
        savedRecipeFrag = new SavedRecipeFragment();
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
        setupBottomNavi();
        // Set listener
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, boolean wasSelected) {
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                if (position == 0) {
                    fragmentTransaction.replace(R.id.fragment_container_id, swipeItemActivityFrag);
                }
                if (position == 1) {

                    fragmentTransaction.replace(R.id.fragment_container_id, recipeListsFrag);
                }
                if (position == 2) {
                    fragmentTransaction.replace(R.id.fragment_container_id, preferencesFragment);
                }
                if (position == 3) {
                    if (isFacebookLoggedIn()) {
                        fragmentTransaction.replace(R.id.fragment_container_id, savedRecipeFrag);
                    } else {
                        Toast.makeText(MainActivity.this, R.string.login_to_use, Toast.LENGTH_SHORT).show();
                    }
                }
                fragmentTransaction.commit();
            }
        });
    }


    private void setupBottomNavi(){
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.tab_1, R.drawable.ic_random, R.color.colorPrimary);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.tab_2, R.drawable.ic_menu_gallery, R.color.colorPrimary);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.tab_3, R.drawable.ic_menu_manage, R.color.colorPrimaryDark);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(R.string.tab_4, R.drawable.ic_saved_icon, R.color.colorPrimaryDark);
        AHBottomNavigationItem item5 = new AHBottomNavigationItem(R.string.tab_5, R.drawable.ic_search_24dp, R.color.colorPrimaryDark);

        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);
        bottomNavigation.addItem(item5);


        bottomNavigation.setBehaviorTranslationEnabled(false);
        bottomNavigation.setForceTint(true);
        bottomNavigation.setForceTitlesDisplay(true);
        bottomNavigation.setColored(true);
        if(!isFacebookLoggedIn()) {
            bottomNavigation.setCurrentItem(2);
        } else {
            bottomNavigation.setCurrentItem(0);
        }
    }

    /**
     * to check if Facebook login or not
     * @return
     */
    private boolean isFacebookLoggedIn(){
        return AccessToken.getCurrentAccessToken() !=null;
    }

}
