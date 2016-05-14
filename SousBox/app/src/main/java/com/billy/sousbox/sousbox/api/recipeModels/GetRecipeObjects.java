package com.billy.sousbox.sousbox.api.recipeModels;

/**
 * Created by Billy on 5/3/16.
 */
public class GetRecipeObjects {

    private String sourceUrl;
    private int id;
    public SpoonGetRecipe[] extendedIngredients;
    private int readyInMinutes;
    private String title;
    private String image;
    private String spoonacularSourceUrl;

    public String getSpoonacularSourceUrl() {
        return spoonacularSourceUrl;
    }

    public void setSpoonacularSourceUrl(String spoonacularSourceUrl) {
        this.spoonacularSourceUrl = spoonacularSourceUrl;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getReadyInMinutes() {
        return readyInMinutes;
    }

    public void setReadyInMinutes(int readyInMinutes) {
        this.readyInMinutes = readyInMinutes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SpoonGetRecipe[] getExtendedIngredients() {
        return extendedIngredients;
    }

    public void setExtendedIngredients(SpoonGetRecipe[] extendedIngredients) {
        this.extendedIngredients = extendedIngredients;
    }
}
