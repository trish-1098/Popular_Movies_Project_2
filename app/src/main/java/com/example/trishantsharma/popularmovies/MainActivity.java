package com.example.trishantsharma.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.trishantsharma.popularmovies.utils.JSONUtils;
import com.example.trishantsharma.popularmovies.utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements MovieRecyclerAdapter.MovieGridOnClickHandler,LoaderManager.LoaderCallbacks<ArrayList<String[]>> {

    private RecyclerView movieGridRecyclerView;
    private ProgressBar loadingIndicator;
    private TextView appNameTextView;
    private String sortByType;
    private int LOADER_ID_FOR_MOVIE_LOADER = 1212;
    private static URL finalBuiltUrlForMultipleMovie;
    private MovieRecyclerAdapter movieRecyclerAdapter;
    private ArrayList<String[]> movieTitleAndPosterPathFinalList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        //By default the movies would be sorted as popular
        sortByType = getString(R.string.menuItemPopular);
        movieGridRecyclerView = findViewById(R.id.movie_grid_recycler_view);
        loadingIndicator = findViewById(R.id.progress_bar_for_movie_loading);
        appNameTextView = findViewById(R.id.app_name_tv);
        movieTitleAndPosterPathFinalList = new ArrayList<>();
        //Initialising the loader
        getSupportLoaderManager().initLoader(LOADER_ID_FOR_MOVIE_LOADER,null,this);
        //Assigning the GridLayout Manager with a spanCount of 2 and vertical orientation
        GridLayoutManager movieGridManager =
                new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        movieRecyclerAdapter = new MovieRecyclerAdapter(this,this);
        movieGridRecyclerView.setLayoutManager(movieGridManager);
        movieGridRecyclerView.setAdapter(movieRecyclerAdapter);
        //movieGridRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_sort_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_by_popular:
                if(!sortByType.equals(getString(R.string.menuItemPopular))) {
                    sortByType = getString(R.string.menuItemPopular);
                    getSupportLoaderManager()
                            .restartLoader(LOADER_ID_FOR_MOVIE_LOADER,null,this);
                }
                return true;
            case R.id.action_sort_by_highest_rated:
                if(!sortByType.equals(getString(R.string.menuItemHighestRated))) {
                    sortByType = getString(R.string.menuItemHighestRated);
                    getSupportLoaderManager()
                            .restartLoader(LOADER_ID_FOR_MOVIE_LOADER,null,this);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMovieDataUrl(String sortByType) {
        finalBuiltUrlForMultipleMovie = NetworkUtils.buildUrlWithSortOrderType(this,sortByType);
    }
    @Override
    public void onClickMovie(int positionClicked) {
        Intent intentToMovieDetail = new Intent(MainActivity.this,MovieDetailActivity.class);
        intentToMovieDetail.putExtra("MOVIE_ID",
                movieTitleAndPosterPathFinalList.get(positionClicked)[0]);
        startActivity(intentToMovieDetail);
    }

    @NonNull
    @Override
    public Loader<ArrayList<String[]>> onCreateLoader(int id, @Nullable Bundle args) {
        loadMovieDataUrl(sortByType);
        return new MovieDataLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<String[]>> loader,
                               ArrayList<String[]> movieTitleAndPosterPathList) {
        movieRecyclerAdapter.setMovieTitleAndPosterList(movieTitleAndPosterPathList);
        loadingIndicator.setVisibility(View.GONE);
        appNameTextView.setVisibility(View.GONE);
        movieTitleAndPosterPathFinalList.addAll(movieTitleAndPosterPathList);
        //getWindow().getDecorView().setBackgroundColor(Color.WHITE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<String[]>> loader) {
        onCreateLoader(LOADER_ID_FOR_MOVIE_LOADER,null);
    }
    private static class MovieDataLoader extends AsyncTaskLoader<ArrayList<String[]>>{
        public MovieDataLoader(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Nullable
        @Override
        public ArrayList<String[]> loadInBackground() {
            String jsonReceived =
                    NetworkUtils.getResponseFromHttpConnection(finalBuiltUrlForMultipleMovie);
            return JSONUtils.parseMovieJSON(jsonReceived);
        }
    }
}
//TODO(1) Rectify the issue where tapping on the options menu item doesn't have any effect on the layout
//TODO(2) Implement the methods to manage the Activity Lifecycle
//TODO(3) Send the correct intent to the DetailsActivity -- DONE !!!
//TODO(4) If possible, try some animations also