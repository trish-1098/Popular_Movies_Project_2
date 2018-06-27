package com.example.trishantsharma.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.trishantsharma.popularmovies.utils.JSONUtils;
import com.example.trishantsharma.popularmovies.utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements MovieRecyclerAdapter.MovieGridOnClickHandler,
        LoaderManager.LoaderCallbacks<ArrayList<String[]>>{
    @BindView(R.id.movie_grid_recycler_view)
    RecyclerView movieGridRecyclerView;
    @BindView(R.id.progress_bar_for_movie_loading)
    ProgressBar loadingIndicator;
    @BindView(R.id.app_name_tv)
    TextView appNameTextView;
    private String sortByType;
    private final int LOADER_ID_FOR_MOVIE_LOADER = 1212;
    private static URL finalBuiltUrlForMultipleMovie;
    private MovieRecyclerAdapter movieRecyclerAdapter;
    @BindView(R.id.no_internet_image)
    ImageView noInternetImageView;
    @BindView(R.id.no_internet_tv)
    TextView noInternetTextView;
    @BindView(R.id.main_root_view)
    ConstraintLayout mainRootView;
    private ArrayList<String[]> moviePopularIdAndPosterFinalList;
    private String SORT_BY_TYPE_STATE_KEY = "sort_key";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        int numOfColumnsAccordingToOrientation =
                getResources().getInteger(R.integer.grid_column_count);
        if(savedInstanceState == null) {
            //By default the movies would be sorted as popular
            sortByType = getString(R.string.menuItemPopular);
        } else {
            sortByType = (String) savedInstanceState.getCharSequence(SORT_BY_TYPE_STATE_KEY);
        }
        //Log.d("Inside onCreate -->",savedInstanceState.getCharSequence(SORT_BY_TYPE_STATE_KEY).toString());
        ButterKnife.bind(this);
        moviePopularIdAndPosterFinalList = new ArrayList<>();
        if(NetworkUtils.isConnectionAvailable(this)) {
            noInternetTextView.setVisibility(View.INVISIBLE);
            noInternetImageView.setVisibility(View.INVISIBLE);
            //Initialising the loader
            getSupportLoaderManager()
                    .initLoader(LOADER_ID_FOR_MOVIE_LOADER, null, this);
            //Assigning the GridLayout Manager with a spanCount of 2 and vertical orientation
            GridLayoutManager movieGridManager =
                    new GridLayoutManager(this, numOfColumnsAccordingToOrientation,
                            GridLayoutManager.VERTICAL, false);
            movieRecyclerAdapter = new MovieRecyclerAdapter(this, this);
            movieGridRecyclerView.setLayoutManager(movieGridManager);
            movieGridRecyclerView.setAdapter(movieRecyclerAdapter);
        } else {
            displayNoInternetScreen();
        }
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            getSupportActionBar()
                    .setIcon(ContextCompat
                            .getDrawable(this, R.drawable.ic_filter_list_white_24dp));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putCharSequence(SORT_BY_TYPE_STATE_KEY,sortByType);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        sortByType = (String) savedInstanceState.getCharSequence(SORT_BY_TYPE_STATE_KEY);
    }

    private void displayNoInternetScreen() {
        //Code to show a screen in case the user is not connected to is no Internet
        movieGridRecyclerView.setVisibility(View.INVISIBLE);
        loadingIndicator.setVisibility(View.INVISIBLE);
        appNameTextView.setVisibility(View.INVISIBLE);
        noInternetTextView.setVisibility(View.VISIBLE);
        noInternetImageView.setVisibility(View.VISIBLE);
        loadingIndicator.getRootView()
                .setBackgroundColor(getResources().getColor(R.color.no_internet_background));
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(!NetworkUtils.isConnectionAvailable(this)) {
            displayNoInternetScreen();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_sort_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(NetworkUtils.isConnectionAvailable(this)) {
            switch (item.getItemId()) {
                case R.id.action_sort_by_popular:
                    if (!sortByType.equals(getString(R.string.menuItemPopular))) {
                        sortByType = getString(R.string.menuItemPopular);
                        getSupportLoaderManager()
                                .restartLoader(LOADER_ID_FOR_MOVIE_LOADER,null,this);
                        displayLoadingScreen();
                    }
                    return true;
                case R.id.action_sort_by_highest_rated:
                    if (!sortByType.equals(getString(R.string.menuItemHighestRated))) {
                        sortByType = getString(R.string.menuItemHighestRated);
                        getSupportLoaderManager()
                                .restartLoader(LOADER_ID_FOR_MOVIE_LOADER, null, this);
                        displayLoadingScreen();
                    }
                    return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    private void displayLoadingScreen() {
        movieGridRecyclerView.setVisibility(View.INVISIBLE);
        loadingIndicator.setVisibility(View.VISIBLE);
        appNameTextView.setVisibility(View.VISIBLE);
        mainRootView.setBackground(getResources().getDrawable(R.drawable.loading_image));
    }
    private void loadMovieDataUrl(String sortByType) {
        finalBuiltUrlForMultipleMovie = NetworkUtils.buildUrlWithSortOrderType(this,sortByType);
    }
    @Override
    public void onClickMovie(int positionClicked) {
        Intent intentToMovieDetail = new Intent(MainActivity.this,MovieDetailActivity.class);
        intentToMovieDetail.putExtra("MOVIE_ID",
                moviePopularIdAndPosterFinalList.get(positionClicked)[0]);
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
        if(movieTitleAndPosterPathList != null) {
            loadingIndicator.setVisibility(View.INVISIBLE);
            appNameTextView.setVisibility(View.INVISIBLE);
            moviePopularIdAndPosterFinalList.clear();
            moviePopularIdAndPosterFinalList.addAll(movieTitleAndPosterPathList);
            movieRecyclerAdapter.setMovieTitleAndPosterList(moviePopularIdAndPosterFinalList);
            movieRecyclerAdapter.notifyDataSetChanged();
            movieGridRecyclerView.refreshDrawableState();
            movieGridRecyclerView.setVisibility(View.VISIBLE);
            mainRootView.setBackground(getResources().getDrawable(R.drawable.movie_title_gradient));
        } else {
            displayNoInternetScreen();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<String[]>> loader) {
        moviePopularIdAndPosterFinalList.clear();
    }

    private static class MovieDataLoader extends AsyncTaskLoader<ArrayList<String[]>>{
        MovieDataLoader(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Nullable
        @Override
        public ArrayList<String[]> loadInBackground() {
            if(NetworkUtils.isConnectionAvailable(getContext())) {
                String jsonReceived =
                        NetworkUtils.getResponseFromHttpConnection(finalBuiltUrlForMultipleMovie);
                if(jsonReceived != null) {
                    return JSONUtils.parseMovieJSON(jsonReceived);
                }
                return null;
            }
            else
                return null;
        }
    }
}
//TODO(5) Set all the dimensions in dimens.xml file