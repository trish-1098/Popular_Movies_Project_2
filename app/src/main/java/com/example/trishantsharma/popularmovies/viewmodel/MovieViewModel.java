package com.example.trishantsharma.popularmovies.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.trishantsharma.popularmovies.FavouriteMovieModel;
import com.example.trishantsharma.popularmovies.R;
import com.example.trishantsharma.popularmovies.database.MovieDatabase;
import com.example.trishantsharma.popularmovies.database.MovieModelBoundaryCallback;
import com.example.trishantsharma.popularmovies.models.MovieModel;
import com.example.trishantsharma.popularmovies.networkdata.NetworkAndDatabaseUtils;
import com.example.trishantsharma.popularmovies.utils.PrefUtils;

import java.util.List;


public class MovieViewModel extends AndroidViewModel{
    private LiveData<PagedList<MovieModel>> movieLiveData;
    private String sortOrderType;
    private PagedList.Config config;
    private MovieModelBoundaryCallback movieModelBoundaryCallback;
    private String dbSortType;
    public MovieViewModel(@NonNull final Application application) {
        super(application);
        config =
                new PagedList.Config.Builder()
                        .setPageSize(3)
                        .setEnablePlaceholders(false)
                        .build();
        sortOrderType = PrefUtils.getSortOrderType(application.getApplicationContext());
        movieModelBoundaryCallback =
                new MovieModelBoundaryCallback(application.getApplicationContext(),sortOrderType);
        if(getSortOrderType().equals(getApplication().getApplicationContext().getString(R.string.sortByPopular))) {
            dbSortType = "popularity";
        } else {
            dbSortType = "voteAverage";
        }
        movieLiveData =
                new LivePagedListBuilder<>(MovieDatabase
                        .getMovieDbInstance(application.getApplicationContext())
                        .getMovieDao().getAllMovies(), config)
                        .setBoundaryCallback(movieModelBoundaryCallback)
                        .build();
    }
    private String getSortOrderType() {
        return sortOrderType;
    }

    public LiveData<PagedList<MovieModel>> getMovieLiveData() {
        return movieLiveData;
    }
    public void setSortOrder(Context context) {
        NetworkAndDatabaseUtils.deleteAllPreviousMovies(context);
        sortOrderType =
                PreferenceManager.getDefaultSharedPreferences(context)
                        .getString(context.getString(R.string.pref_sort_type),
                                context.getString(R.string.sortByPopular));
        movieModelBoundaryCallback.changeSortType(sortOrderType);
        Log.d("Inside pref changed ==>","<=========== Yes ===========>");
    }
}