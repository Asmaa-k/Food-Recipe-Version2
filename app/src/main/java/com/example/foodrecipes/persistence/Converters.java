package com.example.foodrecipes.persistence;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

//this class to convert the array list Ingridants to string, to be saved into SQlite
// SQlite don't support saving arrays and l
// lists
public class Converters {
    //Return Ingridants back to array list of string
    @TypeConverter
    public static String[] fromJsonToList(String value) {
        Type listType = new TypeToken<String[]>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    //To Convert List To Jason
    @TypeConverter
    public static String fromListToJason(String[] list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}
