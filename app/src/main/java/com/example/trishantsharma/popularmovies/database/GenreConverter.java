package com.example.trishantsharma.popularmovies.database;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class GenreConverter {
    @TypeConverter
    public static String encodeMovie(List<Integer> movieDataList) {

        Gson gson = new Gson();
        return gson.toJson(movieDataList);
    }

    @TypeConverter
    public static List<Integer> decodeMovie(String value) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Integer>>() {
        }.getType();
        return gson.fromJson(value, listType);
    }
}
