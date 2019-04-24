package com.example.foodrecipes.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.foodrecipes.AppExecutor;
import com.example.foodrecipes.models.Recipe;
import com.example.foodrecipes.persistence.RecipeDao;
import com.example.foodrecipes.persistence.RecipeDatabase;
import com.example.foodrecipes.requests.ServiceGenerator;
import com.example.foodrecipes.requests.responses.ApiResponse;
import com.example.foodrecipes.requests.responses.RecipeResponse;
import com.example.foodrecipes.requests.responses.RecipeSearchResponse;
import com.example.foodrecipes.utill.Constants;
import com.example.foodrecipes.utill.NetworkBoundResource;
import com.example.foodrecipes.utill.Resource;

import java.util.List;

public class RecipeRepository {
    private static RecipeRepository instance;
    private RecipeDao recipeDao;
    private static final String TAG = "RecipeRepository";

    public static RecipeRepository getInstance(Context context) {
        if (instance == null) {
            instance = new RecipeRepository(context);
        }
        return instance;
    }

    private RecipeRepository(Context context) {
        recipeDao = RecipeDatabase.getInstance(context).getRecipeDao();
    }


    public LiveData<Resource<List<Recipe>>> searchRecipesApi(final String query, final int pageNumber) {
        return new NetworkBoundResource<List<Recipe>, RecipeSearchResponse>(AppExecutor.getInstant()) {

            @Override
            public void saveCallResult(@NonNull RecipeSearchResponse item) {
                //decide weather or not to save data into cache every time user make new request

                if (item.getRecipes() != null) { // recipe list will be null if api key is expired
                    Recipe[] recipes = new Recipe[item.getRecipes().size()];

                    int index = 0;
                    for (long rowId : recipeDao.insertRecipes((Recipe[]) (item.getRecipes().toArray(recipes)))) {
                        if (rowId == -1) { // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This recipe is already in cache.");
                            // if already exists, I don't want to set the ingredients or timestamp b/c they will be erased
                            recipeDao.updateRecipe(
                                    recipes[index].getRecipe_id(),
                                    recipes[index].getTitle(),
                                    recipes[index].getPublisher(),
                                    recipes[index].getImage_url(),
                                    recipes[index].getSocial_rank()
                            );
                        }
                        index++;
                    }
                }

            }

            @Override
            public boolean shouldFetch(@Nullable List<Recipe> data) {//Based on time stampe (when should i refresh the data)
                return true; // always query the network since the queries can be anything
            }

            @NonNull
            @Override
            public LiveData<List<Recipe>> loadFromDb() {
                return recipeDao.searchRecipes(query, pageNumber);
            }

            @NonNull
            @Override
            public LiveData<ApiResponse<RecipeSearchResponse>> createCall() {//i should convert call to live data

                return ServiceGenerator.getRecipeApi().searchRecipe(
                        Constants.API_KEY,
                        query,
                        String.valueOf(pageNumber)
                );
            }
        }.getAsLiveData();
    }
    /*
    advantages to convert from call object to live data:
    1.Dont need to runnables classes anymore (by live data it become syncronsley no need for executer)
    another word it just best to use live data
    live data is observable and good structuer
     */

    public LiveData<Resource<Recipe>> searchRecipe(final String recipeId) {
        return new NetworkBoundResource<Recipe, RecipeResponse>(AppExecutor.getInstant()) {
            @Override
            protected void saveCallResult(@NonNull RecipeResponse item) {
                //will be null if Api Key Expired
                if (item.getRecipe() != null) {
                    item.getRecipe().setTimestamp((int) (System.currentTimeMillis() / 1000));
                    recipeDao.insertRecipe(item.getRecipe());
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable Recipe data) {
                Log.d(TAG, "shouldFetch: recipe: " + data.toString());

                int currentTime = (int) (System.currentTimeMillis() / 1000);
                Log.d(TAG, "shouldFetch: current time: " + currentTime);

                int lastRefresh = data.getTimestamp();
                Log.d(TAG, "shouldFetch: last refresh: " + lastRefresh);

                Log.d(TAG, "shouldFetch: it's been " + ((currentTime - lastRefresh) / 60 * 60 / 24) +
                        "days since this recipe was refreshed.. 30 days must elapse before refreshing");

                if ((currentTime - data.getTimestamp()) >= Constants.RECIPE_REFRESH_TIME) {
                    Log.d(TAG, "shouldFetch: Should Refresh Recipe " + true);
                    return true;
                }
                Log.d(TAG, "shouldFetch: Should Refresh Recipe " + false);
                return false;
            }

            @NonNull
            @Override
            protected LiveData<Recipe> loadFromDb() {
                return recipeDao.getRecipe(recipeId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<RecipeResponse>> createCall() {
                return ServiceGenerator.getRecipeApi().getResponse(
                        Constants.API_KEY,
                        recipeId
                );
            }
        }.getAsLiveData();
    }
}