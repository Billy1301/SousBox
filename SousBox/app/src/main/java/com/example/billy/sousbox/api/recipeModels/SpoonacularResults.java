package com.example.billy.sousbox.api.recipeModels;

/**
 * Created by Billy on 4/29/16.
 */
public class SpoonacularResults {
    private SpoonacularObjects[] results;


    public SpoonacularResults(SpoonacularObjects[] results) {
        this.results = results;
    }

    public SpoonacularObjects[] getResults() {
        return results;
    }

    public void setResults(SpoonacularObjects[] results) {
        this.results = results;
    }
}
