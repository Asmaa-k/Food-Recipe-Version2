package com.example.foodrecipes;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.example.foodrecipes.adapters.OnRecipeListener;
import com.example.foodrecipes.adapters.RecipeAdapter;
import com.example.foodrecipes.models.Recipe;
import com.example.foodrecipes.utill.Resource;
import com.example.foodrecipes.utill.VerticalSpacingItemDecoration;
import com.example.foodrecipes.viewmodel.RecipeListViewModel;

import java.util.List;

import static com.example.foodrecipes.viewmodel.RecipeListViewModel.QUERY_EXHAUSTED;

public class RecipeListActivity extends BaseActivity {
    // private static final String TAG = "RecipeListActivity";

    RecipeListViewModel recipeListViewModel;
    RecipeAdapter adapter;
    RecyclerView recyclerView;
    SearchView searchView;
    private static final String TAG = "RecipeListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        recyclerView = findViewById(R.id.recipe_list);
        searchView = findViewById(R.id.search_view);
        recipeListViewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);

        initRecyclerView();
        initSearchView();
        subscribeObservers();
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
    }

    private void subscribeObservers() {
        recipeListViewModel.getRecipes().observe(this, new Observer<Resource<List<Recipe>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Recipe>> listResource) {
                if (listResource != null) {
                    Log.d(TAG, "onChanged: status: " + listResource.status);

                    if (listResource.data != null) {
                        switch (listResource.status) {
                            case LOADING: {
                                if (recipeListViewModel.getPageNumber() > 1) {
                                    adapter.displayLoading();
                                } else {
                                    adapter.displayOnlyLoading();
                                }
                                break;
                            }
                            case SUCCESS: {
                                Log.d(TAG, "onChanged: cache has been refreshed.");
                                Log.d(TAG, "onChanged: status: SUCCESS, #Recipes: " + listResource.data.size());
                                adapter.hideLoading();
                                adapter.setRecipes(listResource.data);
                                break;
                            }
                            case ERROR: {
                                Log.e(TAG, "onChanged: cannot refresh cache.");
                                Log.e(TAG, "onChanged: ERROR message: " + listResource.message);
                                Log.e(TAG, "onChanged: status: ERROR, #Recipes: " + listResource.data.size());
                                adapter.hideLoading();
                                adapter.setRecipes(listResource.data);
                                Toast.makeText(RecipeListActivity.this, listResource.message, Toast.LENGTH_SHORT).show();

                                if (listResource.message.equals(QUERY_EXHAUSTED)) {
                                    adapter.setQueryExhausted();
                                }
                                break;
                            }
                        }
                    }

                }
            }
        });

        recipeListViewModel.getViewstate().observe(this, new Observer<RecipeListViewModel.ViewState>() {
            @Override
            public void onChanged(@Nullable RecipeListViewModel.ViewState viewState) {
                if (viewState != null) {
                    switch (viewState) {
                        case RECIPES: {
                            // recipes will show automatically from other observer
                            break;
                        }
                        case CATEGORIES: {
                            displaySearchCategories();
                            break;
                        }
                    }
                }
            }
        });
    }

    private void searchRecipeApi(String query) {
        recipeListViewModel.searchRecipes(query, 1);
    }

    private RequestManager initGlide() {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.white_background);
        return Glide.with(this).setDefaultRequestOptions(options);
    }

    private void initRecyclerView() {
        ViewPreloadSizeProvider<String> viewPreload = new ViewPreloadSizeProvider<>();
        adapter = new RecipeAdapter(new OnRecipeListener() {
            @Override
            public void onRecipeClick(int position) {
                Intent intent = new Intent(RecipeListActivity.this, RecipeActivity.class);
                intent.putExtra("recipe", adapter.getSelectedRecipe(position));
                startActivity(intent);
            }
            @Override
            public void onCategoryClick(String category) {
                searchRecipeApi(category);
            }
        }, initGlide(), viewPreload);

        VerticalSpacingItemDecoration itemDecorator = new VerticalSpacingItemDecoration(35);
        recyclerView.addItemDecoration(itemDecorator);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewPreloader<String> preloader = new RecyclerViewPreloader<String>
                (Glide.with(this),
                        adapter,
                        viewPreload,
                        30); //caching 30 result
        recyclerView.addOnScrollListener(preloader);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)
                        && recipeListViewModel.getViewstate().getValue() == RecipeListViewModel.ViewState.RECIPES) {
                    // search the next page
                    recipeListViewModel.searchNextPage();
                }
            }
        });
    }

    private void initSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
               recyclerView.smoothScrollToPosition(0);
                searchRecipeApi(s);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }


    private void displaySearchCategories() {
        adapter.displaySearchCategories();
    }

    @Override
    public void onBackPressed() {
        if(recipeListViewModel.getViewstate().getValue() == RecipeListViewModel.ViewState.CATEGORIES){
            super.onBackPressed();
        }
        else {
            recipeListViewModel.setViewCategories();
            recipeListViewModel.cancelSearchRequest();
        }
    }


}






