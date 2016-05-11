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
    @GET("search?limitLicense=false&number=100&offset=0&")
    Call<SpoonacularResults> searchRecipe(@Query("query")String q);

    @Headers("X-Mashape-Key: " + Keys.MASHAPLE)
    @GET("search?{cuisine=}&limitLicense=false&number=50&offset=0&&query=<required>")
    Call<SpoonacularResults> searchCuisineRecipe(@Query("cuisine=")String type);
//                                                @Query("query")String q);

    @Headers("X-Mashape-Key: " + Keys.MASHAPLE)
    @GET("search?limitLicense=false&number=50&{offset=}&")
    Call<SpoonacularResults> searchMoreRecipe(@Query("offset=")int limit,
                                                    @Query("query")String q);

    @Headers("X-Mashape-Key: " + Keys.MASHAPLE)
    @GET("{id}/information")
    Call<GetRecipeObjects> getRecipeIngredients(@Path("id")int id);


}
