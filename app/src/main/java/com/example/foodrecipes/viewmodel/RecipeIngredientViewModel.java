package com.example.foodrecipes.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.foodrecipes.models.Recipe;
import com.example.foodrecipes.repository.RecipeRepository;
import com.example.foodrecipes.utill.Resource;

public class RecipeIngredientViewModel extends AndroidViewModel {
    private RecipeRepository repository;


    public RecipeIngredientViewModel(@NonNull Application application) {
        super(application);
        repository = RecipeRepository.getInstance(application);
    }

    public LiveData<Resource<Recipe>> searchRecipe(String recipeId) {
        return repository.searchRecipe(recipeId);
    }
}
