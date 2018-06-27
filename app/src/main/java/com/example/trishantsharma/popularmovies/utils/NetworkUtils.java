package com.example.trishantsharma.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;

import com.example.trishantsharma.popularmovies.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


public class NetworkUtils {
    public static URL buildUrlWithSortOrderType(Context context, String sortOrderType) {
        URL finalURL = null;
        try {
            if(sortOrderType.equals(context.getResources().getString(R.string.menuItemPopular))) {
                finalURL = new URL(ConstantUtils.initialURL +
                        context.getResources().getString(R.string.sortByPopular) +
                        ConstantUtils.apiKeyString + ConstantUtils.myApiKey);
            } else if(sortOrderType.equals(context.getResources()
                    .getString(R.string.menuItemHighestRated))){
                finalURL = new URL(ConstantUtils.initialURL +
                        context.getResources().getString(R.string.sortByHighestRated) +
                        ConstantUtils.apiKeyString + ConstantUtils.myApiKey);
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
            finalURL = new URL(ConstantUtils.initialURL +
                    movieId +
                    ConstantUtils.apiKeyString + ConstantUtils.myApiKey);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return finalURL;
    }
    public static URL buildUrlForCastOfParticularMovie(Context context,int movieId) {
        URL finalURL = null;
        try {
            finalURL = new URL(ConstantUtils.initialURL +
                    movieId +
                    "/" +
                    context.getResources().getString(R.string.castsOfParticularMovie) +
                    ConstantUtils.apiKeyString + ConstantUtils.myApiKey);
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
    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo currentNetStatus = cm.getActiveNetworkInfo();
        return currentNetStatus != null && currentNetStatus.isConnectedOrConnecting();
    }
    public static Uri buildUriForPicassoImage(String pathToImage) {
        if(!TextUtils.isEmpty(pathToImage)) {
            return Uri.parse(ConstantUtils.baseUrlForPicassoImage +
                            ConstantUtils.defaultLoadedImageSize + "/" + pathToImage);
        }
        return null;
    }
}
