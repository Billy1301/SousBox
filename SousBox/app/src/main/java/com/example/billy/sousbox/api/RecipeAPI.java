package com.example.billy.sousbox.api;

import com.example.billy.sousbox.Keys.Keys;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Billy on 4/29/16.
 */
public interface RecipeAPI {


    @Headers("X-Mashape-Key: " + Keys.MASHAPLE)
    @GET("search?limitLicense=false&number=75&offset=0&")
    Call<SpoonacularResults> searchRecipe(@Query("query")String q);



    @Headers("X-Mashape-Key: " + Keys.MASHAPLE)
    @GET("search?limitLicense=false&number=50&offset=0&")
    Call<SpoonacularResults> swipeSearchRecipes(@Query("query") String q);


    /**
     * this is to offset pulling by 75
     * @param q
     * @return
     */
    @Headers("X-Mashape-Key: " + Keys.MASHAPLE)
    @GET("search?limitLicense=false&number=75&offset=75&")
    Call<SpoonacularResults> swipeSearchMoreRecipes(@Query("query") String q);


    /**
     * this is to pull recipe ingredients
     * @param id
     * @return
     */

    @Headers("X-Mashape-Key: " + Keys.MASHAPLE)
    @GET("{id}/information")
    Call<GetRecipeObjects> getRecipeIngredients(@Path("id")int id);


}
