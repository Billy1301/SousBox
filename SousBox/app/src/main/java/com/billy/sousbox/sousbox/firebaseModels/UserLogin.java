package com.billy.sousbox.sousbox.firebaseModels;

/**
 * Created by Billy on 5/10/16.
 */
public class UserLogin {

    private String facebook;
    private Recipes[] recipes;

    public UserLogin() {
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public Recipes[] getRecipes() {
        return recipes;
    }

    public void setRecipes(Recipes[] recipes) {
        this.recipes = recipes;
    }
}
