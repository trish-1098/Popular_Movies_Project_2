package com.example.trishantsharma.popularmovies.database;

import android.arch.paging.PagedList;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.trishantsharma.popularmovies.models.MovieModel;
import com.example.trishantsharma.popularmovies.networkdata.NetworkAndDatabaseUtils;
import com.example.trishantsharma.popularmovies.utils.PrefUtils;

public class MovieModelBoundaryCallback extends PagedList.BoundaryCallback<MovieModel> {
    private Context context;
    private String sortOrderType;
    public MovieModelBoundaryCallback(Context context,String sortOrderType) {
        this.context = context;
        this.sortOrderType = sortOrderType;
    }
    public void changeSortType(String sortOrderType) {
        this.sortOrderType = sortOrderType;
    }
    @Override
    public void onZeroItemsLoaded() {
        PrefUtils.resetPageNumber(context);
        requestAndSaveData(context,sortOrderType);
    }

    @Override
    public void onItemAtEndLoaded(@NonNull MovieModel itemAtEnd) {
        PrefUtils.incrementPageNumber(context);
        requestAndSaveData(context,sortOrderType);
    }
    private static void requestAndSaveData(Context context,String sortOrderType) {
        if(NetworkAndDatabaseUtils.isConnectionAvailable(context)) {
            NetworkAndDatabaseUtils
                    .fetchNewMoviesAndSaveToDatabase(sortOrderType,
                            PrefUtils.getPageNumber(context),
                            context);
        }
    }
}
