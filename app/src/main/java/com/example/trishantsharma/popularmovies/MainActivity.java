package com.example.trishantsharma.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity
        implements MovieRecyclerAdapter.MovieGridOnClickHandler,
        LoaderManager.LoaderCallbacks<ArrayList<String[]>>{

    private RecyclerView movieGridRecyclerView;
    private ProgressBar loadingIndicator;
    private TextView appNameTextView;
    private String sortByType;
    private final int LOADER_ID_FOR_MOVIE_LOADER = 1212;
    private static URL finalBuiltUrlForMultipleMovie;
    private MovieRecyclerAdapter movieRecyclerAdapter;
    private ImageView noInternetImageView;
    private TextView noInternetTextView;
    private ArrayList<String[]> moviePopularIdAndPosterFinalList;
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
        noInternetImageView = findViewById(R.id.no_internet_image);
        noInternetTextView = findViewById(R.id.no_internet_tv);
        moviePopularIdAndPosterFinalList = new ArrayList<>();
        if(NetworkUtils.isConnectionAvailable(this)) {
            noInternetTextView.setVisibility(View.INVISIBLE);
            noInternetImageView.setVisibility(View.INVISIBLE);
            //Initialising the loader
            getSupportLoaderManager()
                    .initLoader(LOADER_ID_FOR_MOVIE_LOADER, null, this);
            //Assigning the GridLayout Manager with a spanCount of 2 and vertical orientation
            GridLayoutManager movieGridManager =
                    new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
            movieRecyclerAdapter = new MovieRecyclerAdapter(this, this);
            movieGridRecyclerView.setLayoutManager(movieGridManager);
            movieGridRecyclerView.setAdapter(movieRecyclerAdapter);
        } else {
            displayNoInternetScreen();
        }
        //movieGridRecyclerView.setVisibility(View.INVISIBLE);
    }
    private void displayNoInternetScreen() {
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
                        getSupportLoaderManager().destroyLoader(LOADER_ID_FOR_MOVIE_LOADER);
                        getSupportLoaderManager()
                                .initLoader(LOADER_ID_FOR_MOVIE_LOADER, null, this);
                        displayLoadingScreen();
                    }
                    return true;
                case R.id.action_sort_by_highest_rated:
                    if (!sortByType.equals(getString(R.string.menuItemHighestRated))) {
                        sortByType = getString(R.string.menuItemHighestRated);
                        getSupportLoaderManager().destroyLoader(LOADER_ID_FOR_MOVIE_LOADER);
                        getSupportLoaderManager()
                                .initLoader(LOADER_ID_FOR_MOVIE_LOADER, null, this);
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
        Log.d("onCreateLoader-->","<--------Called------>");
        loadMovieDataUrl(sortByType);
        return new MovieDataLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<String[]>> loader,
                               ArrayList<String[]> movieTitleAndPosterPathList) {
        if(movieTitleAndPosterPathList != null) {
            loadingIndicator.setVisibility(View.INVISIBLE);
            appNameTextView.setVisibility(View.INVISIBLE);
            for (int i = 0; i < movieTitleAndPosterPathList.size(); i++) {
                Log.d("Data returned id->", movieTitleAndPosterPathList.get(i)[0]);
            }
//        int previousSize = 0;
//        if(!moviePopularIdAndPosterFinalList.isEmpty()) {
//            previousSize = moviePopularIdAndPosterFinalList.size();
//            moviePopularIdAndPosterFinalList.clear();
//            for(int i = previousSize; i < movieTitleAndPosterPathList.size(); i++) {
//                moviePopularIdAndPosterFinalList.add(movieTitleAndPosterPathList.get(i));
//            }
//        } else {
//            moviePopularIdAndPosterFinalList.addAll(movieTitleAndPosterPathList);
//        }
            moviePopularIdAndPosterFinalList.clear();
            moviePopularIdAndPosterFinalList.addAll(movieTitleAndPosterPathList);
            for (int i = 0; i < moviePopularIdAndPosterFinalList.size(); i++) {
                Log.d("Final list ---->", moviePopularIdAndPosterFinalList.get(i)[0]);
            }
            movieRecyclerAdapter.setMovieTitleAndPosterList(moviePopularIdAndPosterFinalList);
            movieRecyclerAdapter.notifyDataSetChanged();
            movieGridRecyclerView.refreshDrawableState();
            movieGridRecyclerView.setVisibility(View.VISIBLE);
            //movieRecyclerAdapter.notifyDataSetChanged();
            //getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        } else {
            displayNoInternetScreen();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<String[]>> loader) {
        //onCreateLoader(LOADER_ID_FOR_MOVIE_LOADER,null);
        moviePopularIdAndPosterFinalList.clear();
        Log.d("onLoaderReset -->","<------Called------->");
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
                return JSONUtils.parseMovieJSON(jsonReceived);
            }
            else
                return null;
        }

        /*@Override
        public void deliverResult(@Nullable ArrayList<String[]> data) {
            if(!receivedArrayList.isEmpty()) {
                receivedArrayList.clear();
                receivedArrayList.addAll(data);
                super.deliverResult(receivedArrayList);
                return;
            }
            super.deliverResult(data);
        }*/
    }
}
//TODO(1) Rectify the issue where tapping on the options menu item doesn't have any effect on the layout
//TODO(2) Implement the methods to manage the Activity Lifecycle
//TODO(1.1) Remove the temporary code of lines 159
//TODO(1.2) Try different methods again and again to solve the problem permanently
//TODO(1.3) Try the lifecycle methods and the loader methods
//TODO(1.4) Fix the error when going away from the activity and then returning the list gets duplicated
//TODO(3) After the problem is solved then clean up the Log statements from all classes
//TODO(4) Set all the strings in strings.xml file
//TODO(5) Set all the dimensions in dimens.xml file
//TODO(6) Check for the internet connection and show an appropriate image if it is not available
//TODO(6) Submit