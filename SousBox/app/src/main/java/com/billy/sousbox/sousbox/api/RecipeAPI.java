package com.billy.sousbox.sousbox.api;

import com.billy.sousbox.sousbox.api.recipeModels.SpoonacularResults;
import com.billy.sousbox.sousbox.Keys.Keys;
import com.billy.sousbox.sousbox.api.recipeModels.GetRecipeObjects;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Billy on 4/29/16.
 */
public interface RecipeAPI {

    //this one for search option
    @Headers("X-Mashape-Key: " + Keys.MASHAPLE)
    @GET("search?number=20&offset=0&")
    Call<SpoonacularResults> searchRecipe(@Query("query")String q);

    @Headers("X-Mashape-Key: " + Keys.MASHAPLE)
    @GET("search?limitLicense=false&number=50")
    Call<SpoonacularResults> recipesAPIcall(@Query("offset") int offset,
                                            @Query("query") String q);

    /**
     * this is to pull recipe ingredients
     * @param id
     * @return
     */
    @Headers("X-Mashape-Key: " + Keys.MASHAPLE)
    @GET("{id}/information")
    Call<GetRecipeObjects> getRecipeIngredients(@Path("id")int id);


}
