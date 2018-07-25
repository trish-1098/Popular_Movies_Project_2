package com.example.trishantsharma.popularmovies.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.trishantsharma.popularmovies.R;

public class PrefUtils {
    public static void incrementPageNumber(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        int pageNumber = sharedPreferences.getInt(context.getString(R.string.pref_page_number),1);
        pageNumber++;
        sharedPreferencesEditor.putInt(context.getString(R.string.pref_page_number),pageNumber);
        sharedPreferencesEditor.apply();
    }
    public static int getPageNumber(Context context) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getInt(context.getString(R.string.pref_page_number),1);
    }
    public static void resetPageNumber(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putInt(context.getString(R.string.pref_page_number),1).apply();
    }
    public static void changeSortOrderType(Context context,String sortOrderType) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();
        String currentSortType = sharedPreferences.getString(context.getString(R.string.pref_sort_type),
                context.getString(R.string.sortByPopular));
        if(!(sortOrderType
                .equals(currentSortType))) {
            currentSortType = sortOrderType;
            sharedPrefEditor.putString(context.getString(R.string.pref_sort_type),currentSortType);
            sharedPrefEditor.apply();
        }
    }
    public static String getSortOrderType(Context context) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.pref_sort_type),
                        context.getString(R.string.sortByPopular));
    }
}
