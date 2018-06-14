package com.example.trishantsharma.popularmovies.utils;

import android.content.Context;
import android.text.TextUtils;

import com.example.trishantsharma.popularmovies.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
    private final static String intitialURL = "http://api.themoviedb.org/3/movie/";
    private final static String myApiKey = "3b7a0a58fb202e9d0026658add86034a";
    private final static String apiKeyString = "?api_key=";
    public static URL buildUrlWithSortOrderType(Context context, String sortOrderType) {
        URL finalURL = null;
        try {
            if(sortOrderType.equals(context.getResources().getString(R.string.menuItemPopular))) {
                finalURL = new URL(intitialURL +
                        context.getResources().getString(R.string.sortByPopular) +
                        apiKeyString + myApiKey);
            } else if(sortOrderType.equals(context.getResources()
                    .getString(R.string.menuItemHighestRated))){
                finalURL = new URL(intitialURL +
                        context.getResources().getString(R.string.sortByHighestRated) +
                        apiKeyString + myApiKey);
            }
            else {
                throw new IllegalArgumentException("Wrong Sort order: " + sortOrderType);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return finalURL;
    }
    public static URL buildUrlForAParticularMovie(int movieId){
        URL finalURL = null;
        try{
            finalURL = new URL(intitialURL +
                    movieId +
                    apiKeyString + myApiKey);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return finalURL;
    }
    public static URL buildUrlForCastOfParticularMovie(Context context,int movieId) {
        URL finalURL = null;
        try {
            finalURL = new URL(intitialURL +
                    movieId +
                    "/" +
                    context.getResources().getString(R.string.castsOfParticularMovie) +
                    apiKeyString + myApiKey);
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
        return finalURL;
    }
    public static String getResponseFromHttpConnection(URL url) {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream in = httpURLConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String responseFromQuery = null;
            if (hasInput) {
                responseFromQuery = scanner.next();
            }
            scanner.close();
            return responseFromQuery;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
