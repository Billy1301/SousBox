package com.example.billy.sousbox.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.billy.sousbox.R;
import com.example.billy.sousbox.api.QueryFilters;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import java.util.Map;
import timber.log.Timber;

/**
 * Created by Billy on 5/2/16.
 */
public class PreferencesFragment extends Fragment {

    private TextView info;
    private Button saveButton;
    /* The login button for Facebook */
    /* The callback manager for Facebook */
    /* Used to track user logging in/out off Facebook */
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private AccessTokenTracker mFacebookAccessTokenTracker;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private FrameLayout fragContainer;
    private FoodListsMainFragment recipeListsFrag;
    private ProgressDialog mAuthProgressDialog;
    /* A reference to the Firebase */
    private Firebase mFirebaseRef;
    /* Data from the authenticated user */
    private AuthData mAuthData;
    /* Listener for Firebase session changes */
    private Firebase.AuthStateListener mAuthStateListener;

    public static CheckBox beefCheckBox, porkCheckBox, chickenCheckBox, vegetarianCheckBox, seafoodCheckbox, allTypeCheckBox;
    SharedPreferences sharedPreferences;

    QueryFilters queryFilters;
    //Bundle filterBundle;

    //region Checked booleans
    private boolean beefCheck = false;
    private boolean porkCheck = false;
    private boolean chickenCheck = false;
    private boolean vegetarianCheck = false;
    private boolean seafoodCheck = false;
    private boolean allTypeCheck = false;
    //endregion Checked booleans

    // region Shared Preferences Booleans Codes
    private String BEEF_CODE = "beef";
    private String PORK_CODE = "pork";
    private String CHICKEN_CODE = "chicken";
    private String VEGETARIAN_CODE = "vegetarian";
    private String SEAFOOD_CODE = "seafood";
    private String ALL_TYPE_CODE = "allType";
    //endregion Shared Preferences Booleans Codes

    public final static String FILTER_KEY = "filter";
    public final static String Shared_FILTER_KEY = "shared filter";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.preferences_layout_fragment, container, false);
        setRetainInstance(true);
        FacebookSdk.sdkInitialize(getContext());
        callbackManager = CallbackManager.Factory.create();
        initiViews(v);
        loginButton.setFragment(this);
        facebookLogin();
        recipeListsFrag = new FoodListsMainFragment();
        queryFilters = new QueryFilters();
        initiCheckboxClicks();


        return v;
    }

    private void fireBase(){
        mAuthProgressDialog = new ProgressDialog(getContext());
        mAuthProgressDialog.setTitle("Loading");
        mAuthProgressDialog.setMessage("Authenticating with Firebase...");
        mAuthProgressDialog.setCancelable(false);
        mAuthProgressDialog.show();

        mAuthStateListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                mAuthProgressDialog.hide();
                setAuthenticatedUser(authData);
            }
        };
//         Check if the user is authenticated with Firebase already. If this is the case we can set the authenticated
//         * user and hide hide any login buttons
        mFirebaseRef.addAuthStateListener(mAuthStateListener);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // if user logged in with Facebook, stop tracking their token
        if (mFacebookAccessTokenTracker != null) {
            mFacebookAccessTokenTracker.stopTracking();
            logout();
        }
        // if changing configurations, stop tracking firebase session.
        mFirebaseRef.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void initiViews(View v){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        loginButton = (LoginButton)v.findViewById(R.id.login_button);
        loginButton.setFragment(this);
        info = (TextView)v.findViewById(R.id.info);
        mFirebaseRef = new Firebase("https://sous-box.firebaseio.com");

        fragContainer = (FrameLayout)v.findViewById(R.id.fragment_container_id);
        fragmentManager = getFragmentManager();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        beefCheckBox = (CheckBox)v.findViewById(R.id.beef_checkbox_id);
        porkCheckBox = (CheckBox)v.findViewById(R.id.pork_checkbox_id);
        chickenCheckBox = (CheckBox)v.findViewById(R.id.chicken_checkbox_id);
        seafoodCheckbox = (CheckBox)v.findViewById(R.id.seafood_checkout_id);
        vegetarianCheckBox = (CheckBox)v.findViewById(R.id.vege_checkout_id);
        allTypeCheckBox = (CheckBox)v.findViewById(R.id.allType_checkbox_id);

    }

    /**
     * listen to checkbox, saved it and set the filter
     * @param view
     */
    private void onCheckboxClicked(final View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean checked = ((CheckBox) view).isChecked();

                switch (view.getId()) {
                    case R.id.beef_checkbox_id:
                        if (checked) {
                            beefCheck = true;
                            String beefFilter = "beef";
                            setSearchFilter(beefFilter);
                            porkCheckBox.setEnabled(false);
                            chickenCheckBox.setEnabled(false);
                            seafoodCheckbox.setEnabled(false);
                            vegetarianCheckBox.setEnabled(false);
                            allTypeCheckBox.setEnabled(false);

                        } if (!checked) {
                            beefCheck = false;
                            setSearchFilter("");
                            porkCheckBox.setEnabled(true);
                            chickenCheckBox.setEnabled(true);
                            seafoodCheckbox.setEnabled(true);
                            vegetarianCheckBox.setEnabled(true);
                            allTypeCheckBox.setEnabled(true);
                        }
                        break;
                    case R.id.chicken_checkbox_id:
                        if (checked) {
                            chickenCheck = true;
                            String chickenFilter = "chicken";
                            setSearchFilter(chickenFilter);
                            porkCheckBox.setEnabled(false);
                            beefCheckBox.setEnabled(false);
                            seafoodCheckbox.setEnabled(false);
                            vegetarianCheckBox.setEnabled(false);
                            allTypeCheckBox.setEnabled(false);

                        } else {
                            chickenCheck = false;
                            setSearchFilter("");
                            porkCheckBox.setEnabled(true);
                            beefCheckBox.setEnabled(true);
                            seafoodCheckbox.setEnabled(true);
                            vegetarianCheckBox.setEnabled(true);
                            allTypeCheckBox.setEnabled(true);
                        }
                        break;
                    case R.id.pork_checkbox_id:
                        if (checked) {
                            porkCheck = true;
                            String porkFilter = "pork";
                            setSearchFilter(porkFilter);
                            chickenCheckBox.setEnabled(false);
                            beefCheckBox.setEnabled(false);
                            seafoodCheckbox.setEnabled(false);
                            vegetarianCheckBox.setEnabled(false);
                            allTypeCheckBox.setEnabled(false);

                        } else {
                            porkCheck = false;
                            setSearchFilter("");
                            chickenCheckBox.setEnabled(true);
                            beefCheckBox.setEnabled(true);
                            seafoodCheckbox.setEnabled(true);
                            vegetarianCheckBox.setEnabled(true);
                            allTypeCheckBox.setEnabled(true);
                        }
                        break;
                    case R.id.vege_checkout_id:
                        if (checked) {
                            vegetarianCheck = true;
                            String vegeFilter = "vegetarian";
                            setSearchFilter(vegeFilter);
                            porkCheckBox.setEnabled(false);
                            beefCheckBox.setEnabled(false);
                            seafoodCheckbox.setEnabled(false);
                            chickenCheckBox.setEnabled(false);
                            allTypeCheckBox.setEnabled(false);
                        } else {
                            vegetarianCheck = false;
                            setSearchFilter("");
                            porkCheckBox.setEnabled(true);
                            beefCheckBox.setEnabled(true);
                            seafoodCheckbox.setEnabled(true);
                            chickenCheckBox.setEnabled(true);
                            allTypeCheckBox.setEnabled(true);
                        }
                        break;
                    case R.id.seafood_checkout_id:
                        if (checked) {
                            seafoodCheck = true;
                            String seafoodFilter = "seafood";
                            setSearchFilter(seafoodFilter);
                            porkCheckBox.setEnabled(false);
                            beefCheckBox.setEnabled(false);
                            chickenCheckBox.setEnabled(false);
                            vegetarianCheckBox.setEnabled(false);
                            allTypeCheckBox.setEnabled(false);
                        } else {
                            seafoodCheck = false;
                            setSearchFilter("");
                            porkCheckBox.setEnabled(true);
                            beefCheckBox.setEnabled(true);
                            chickenCheckBox.setEnabled(true);
                            vegetarianCheckBox.setEnabled(true);
                            allTypeCheckBox.setEnabled(true);
                        }
                        break;

                    case R.id.allType_checkbox_id:
                        if (checked) {
                            allTypeCheck = true;
                            String allTypeFilter = "";
                            setSearchFilter(allTypeFilter);
                            porkCheckBox.setEnabled(false);
                            beefCheckBox.setEnabled(false);
                            seafoodCheckbox.setEnabled(false);
                            vegetarianCheckBox.setEnabled(false);
                            chickenCheckBox.setEnabled(false);


                        } else {
                            allTypeCheck = false;
                            setSearchFilter("");
                            porkCheckBox.setEnabled(true);
                            beefCheckBox.setEnabled(true);
                            seafoodCheckbox.setEnabled(true);
                            vegetarianCheckBox.setEnabled(true);
                            chickenCheckBox.setEnabled(true);
                        }
                        break;
                    default:
                        setSearchFilter("");
                }
            }
        });
    }

    /**
     * this is for saving the checkbox
     * @param filterName
     */
    private void setSearchFilter(String filterName){
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Shared_FILTER_KEY, filterName);
        editor.commit();
    }

    /**
     * Adds each checkbox to see if they have been checked
     */
    private void initiCheckboxClicks() {
        onCheckboxClicked(beefCheckBox);
        onCheckboxClicked(porkCheckBox);
        onCheckboxClicked(chickenCheckBox);
        onCheckboxClicked(vegetarianCheckBox);
        onCheckboxClicked(allTypeCheckBox);
        onCheckboxClicked(seafoodCheckbox);
    }

    /**
     * Saves the state of the checkboxes in the shared preferences
     */
    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(BEEF_CODE, beefCheck);
        editor.putBoolean(PORK_CODE, porkCheck);
        editor.putBoolean(CHICKEN_CODE, chickenCheck);
        editor.putBoolean(ALL_TYPE_CODE, allTypeCheck);
        editor.putBoolean(SEAFOOD_CODE, seafoodCheck);
        editor.putBoolean(VEGETARIAN_CODE, vegetarianCheck);
        editor.commit();
    }

    /**
     * Retrieves the state of the checkboxes from the shared preferences
     */
    @Override
    public void onResume() {
        super.onResume();
        porkCheck = sharedPreferences.getBoolean(PORK_CODE, porkCheck);
        porkCheckBox.setChecked(porkCheck);
        if (porkCheckBox.isChecked()) {
            seafoodCheckbox.setEnabled(false);
            beefCheckBox.setEnabled(false);
            chickenCheckBox.setEnabled(false);
            vegetarianCheckBox.setEnabled(false);
            allTypeCheckBox.setEnabled(false);
        }
        beefCheck = sharedPreferences.getBoolean(BEEF_CODE, beefCheck);
        beefCheckBox.setChecked(beefCheck);
        if (beefCheckBox.isChecked()) {
            seafoodCheckbox.setEnabled(false);
            porkCheckBox.setEnabled(false);
            chickenCheckBox.setEnabled(false);
            vegetarianCheckBox.setEnabled(false);
            allTypeCheckBox.setEnabled(false);
        }
        chickenCheck = sharedPreferences.getBoolean(CHICKEN_CODE, chickenCheck);
        chickenCheckBox.setChecked(chickenCheck);
        if (chickenCheckBox.isChecked()) {
            seafoodCheckbox.setEnabled(false);
            beefCheckBox.setEnabled(false);
            porkCheckBox.setEnabled(false);
            vegetarianCheckBox.setEnabled(false);
            allTypeCheckBox.setEnabled(false);
        }
        seafoodCheck = sharedPreferences.getBoolean(SEAFOOD_CODE, seafoodCheck);
        seafoodCheckbox.setChecked(seafoodCheck);
        if (seafoodCheckbox.isChecked()) {
            porkCheckBox.setEnabled(false);
            beefCheckBox.setEnabled(false);
            chickenCheckBox.setEnabled(false);
            vegetarianCheckBox.setEnabled(false);
            allTypeCheckBox.setEnabled(false);
        }
        vegetarianCheck = sharedPreferences.getBoolean(VEGETARIAN_CODE, vegetarianCheck);
        vegetarianCheckBox.setChecked(vegetarianCheck);
        if (vegetarianCheckBox.isChecked()) {
            seafoodCheckbox.setEnabled(false);
            beefCheckBox.setEnabled(false);
            chickenCheckBox.setEnabled(false);
            porkCheckBox.setEnabled(false);
            allTypeCheckBox.setEnabled(false);
        }
        allTypeCheck = sharedPreferences.getBoolean(ALL_TYPE_CODE, allTypeCheck);
        allTypeCheckBox.setChecked(allTypeCheck);
        if (allTypeCheckBox.isChecked()) {
            seafoodCheckbox.setEnabled(false);
            beefCheckBox.setEnabled(false);
            chickenCheckBox.setEnabled(false);
            vegetarianCheckBox.setEnabled(false);
            porkCheckBox.setEnabled(false);
        }
    }


    private boolean isFacebookLoggedIn(){
        return AccessToken.getCurrentAccessToken() !=null;
    }



    private void facebookLogin(){

        if(!isFacebookLoggedIn()) {
            mFacebookAccessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                   // Timber.i("Facebook.AccessTokenTracker.OnCurrentAccessTokenChanged");
                    PreferencesFragment.this.onFacebookAccessTokenChange(currentAccessToken);
                }
            };

            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    fireBase();

                    Toast.makeText(getActivity(), "You are logged in",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancel() {
//                    info.setText("Login attempt canceled.");
                }

                @Override
                public void onError(FacebookException e) {
                    //info.setText("Login attempt failed.");
                    e.printStackTrace();
                }
            });
        } else {
            Timber.d("logged in");
        }
    }

    private void onFacebookAccessTokenChange(AccessToken token) {
        if (token != null) {
            mAuthProgressDialog.show();
            mFirebaseRef.authWithOAuthToken("facebook", token.getToken(), new AuthResultHandler("facebook"));
        } else {
            // Logged out of Facebook and currently authenticated with Firebase using Facebook, so do a logout
            if (this.mAuthData != null && this.mAuthData.getProvider().equals("facebook")) {
                mFirebaseRef.unauth();
                setAuthenticatedUser(null);

            }
        }
    }

    /**
     * Unauthenticate from Firebase and from providers where necessary.
     */
    private void logout() {
        if (this.mAuthData != null) {
            /* logout of Firebase */
            mFirebaseRef.unauth();
            /* Logout of any of the Frameworks. This step is optional, but ensures the user is not logged into
             * Facebook/Google+ after logging out of Firebase. */
            if (this.mAuthData.getProvider().equals("facebook")) {
                /* Logout from Facebook */
                LoginManager.getInstance().logOut();
            }
            /* Update authenticated user and show login buttons */
            setAuthenticatedUser(null);
        }
    }


    /**
     * Once a user is logged in, take the mAuthData provided from Firebase and "use" it.
     */
    private void setAuthenticatedUser(AuthData authData) {
        if (authData != null) {
            /* Hide all the login buttons */
            //loginButton.setVisibility(View.GONE);

            /* show a provider specific status text */
            String name = null;
            if (authData.getProvider().equals("facebook")) {
                name = (String) authData.getProviderData().get("displayName");
            }
            else {
                Timber.i("Invalid provider: " + authData.getProvider());
            }
            if (name != null) {
//                mLoggedInStatusTextView.setText("Logged in as " + name + " (" + authData.getProvider() + ")");
            }
        } else {
            /* No authenticated user show all the login buttons */
            loginButton.setVisibility(View.VISIBLE);
        }
        this.mAuthData = authData;
        /* invalidate options menu to hide/show the logout button */

    }

    /**
     * Show errors to users
     */
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(getContext())
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Utility class for authentication results
     */
    private class AuthResultHandler implements Firebase.AuthResultHandler {

        private final String provider;

        public AuthResultHandler(String provider) {
            this.provider = provider;
        }

        @Override
        public void onAuthenticated(AuthData authData) {
            mAuthProgressDialog.hide();
            Timber.i(" " + provider + " auth successful");
            setAuthenticatedUser(authData);
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            mAuthProgressDialog.hide();
            showErrorDialog(firebaseError.toString());
        }
    }

}
