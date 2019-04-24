package com.example.foodrecipes.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.foodrecipes.models.Recipe;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;
@Dao
public interface RecipeDao {
    @Insert(onConflict = IGNORE)//if there's similer data just ignor
    long[] insertRecipes(Recipe... recipes);//returns list of long

    @Insert(onConflict = REPLACE)
    void insertRecipe(Recipe recipe);

    // Custom update statement so ingredients and timestamp don't get removed
    @Query("UPDATE recipes_table SET title = :title, publisher = :publisher, image_url = :image_url, " +
            "social_rank = :social_rank WHERE recipe_id = :recipe_id")
    void updateRecipe(String recipe_id, String title, String publisher, String image_url, float social_rank);

    // NOTE: The SQL query sometimes won't return EXACTLY what the api does since the API might use a different query
    // or even a different database. But they are very very close.
    @Query("SELECT * FROM recipes_table WHERE title LIKE '%' || :query || '%' OR " + " ingredients LIKE '%' || :query || '%'" +
            " ORDER BY social_rank DESC LIMIT (:pageNumber * 30)")
    LiveData<List<Recipe>> searchRecipes(String query, int pageNumber);

    @Query("SELECT * FROM recipes_table WHERE recipe_id = :recipeId")
    LiveData<Recipe> getRecipe(String recipeId);
}