package com.billy.sousbox.sousbox;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.billy.sousbox.sousbox.fragments.PreferencesFragment;
import com.billy.sousbox.sousbox.fragments.FoodListsMainFragment;
import com.billy.sousbox.sousbox.fragments.SavedRecipeFragment;
import com.billy.sousbox.sousbox.fragments.SearchFragment;
import com.billy.sousbox.sousbox.fragments.SwipeItemFragment;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


public class MainActivity extends AppCompatActivity {

    //region Private Variables
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
    private SearchFragment searchFragment;
    private SearchView searchView;
    private String searchQuery;
    //endregion Private Variables


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setViews();
        checkNetwork();
        bottomNavi();
        //prevent switch back to first frag when rotating screen
        if(savedInstanceState == null){
            setupFragmentOnFirstLoad();
        }
        handleIntent(getIntent());
    }


    /**
     * This is for the search option
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchQuery = intent.getStringExtra(SearchManager.QUERY);
            searchFragment.setSearchQuery(searchQuery);
            Toast.makeText(getApplicationContext(), "Searching for " + searchQuery, Toast.LENGTH_SHORT).show();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container_id, searchFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
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
    private void setViews(){
        callbackManager = CallbackManager.Factory.create();
        fragContainer = (FrameLayout)findViewById(R.id.fragment_container_id);
        fragmentManager = getSupportFragmentManager();
        recipeListsFrag = new FoodListsMainFragment();
        preferencesFragment = new PreferencesFragment();
        swipeItemActivityFrag = new SwipeItemFragment();
        savedRecipeFrag = new SavedRecipeFragment();
        searchFragment = new SearchFragment();
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
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);

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
