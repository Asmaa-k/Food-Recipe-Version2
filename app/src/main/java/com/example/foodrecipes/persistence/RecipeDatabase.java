package com.example.foodrecipes.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.example.foodrecipes.models.Recipe;

@Database(entities = {Recipe.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class RecipeDatabase extends RoomDatabase {

    private static final String db_name = "RecipeDB";
    private static RecipeDatabase instance;
    public abstract RecipeDao getRecipeDao();

    public static RecipeDatabase getInstance(Context context) {
        if (instance == null)
            instance = Room.databaseBuilder(context.getApplicationContext(), RecipeDatabase.class, db_name).build();
        return instance;
    }

}
