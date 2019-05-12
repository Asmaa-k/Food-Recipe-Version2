package com.example.foodrecipes.utill;

public class Constants {

    public static final String BASE_URL ="https://www.food2fork.com";
    public static final String API_KEY ="bb3cc5cb70ed22dfc30c01e2b44b3bd6";
    public static final int CONNECTION_TIMEOUT =10;//10 SECOND
    public static final int READ_TIMEOUT =2;//2 SECOND
    public static final int WRITE_TIMEOUT =2;//2 SECOND

    public static final int RECIPE_REFRESH_TIME = 60 * 60 * 24 * 30;// 30 days

    public static final String[] DEFAULT_SEARCH_CATEGORIES =
            {"Barbeque", "Breakfast", "Chicken", "Beef", "Brunch", "Dinner", "Wine", "Italian"};

    public static final String[] DEFAULT_SEARCH_CATEGORY_IMAGES =
            {
                    "barbeque",
                    "breakfast",
                    "chicken",
                    "beef",
                    "brunch",
                    "dinner",
                    "wine",
                    "italian"
            };
}
