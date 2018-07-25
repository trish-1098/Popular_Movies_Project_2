package com.example.trishantsharma.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
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

import com.example.trishantsharma.popularmovies.adapters.FavouriteMovieAdapter;
import com.example.trishantsharma.popularmovies.adapters.MovieRecyclerAdapter;
import com.example.trishantsharma.popularmovies.database.MovieDatabase;
import com.example.trishantsharma.popularmovies.models.MovieModel;
import com.example.trishantsharma.popularmovies.networkdata.NetworkAndDatabaseUtils;
import com.example.trishantsharma.popularmovies.utils.PrefUtils;
import com.example.trishantsharma.popularmovies.viewmodel.MovieViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements MovieRecyclerAdapter.MovieGridOnClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener {
    @BindView(R.id.movie_grid_recycler_view)
    RecyclerView movieGridRecyclerView;
    @BindView(R.id.progress_bar_for_movie_loading)
    ProgressBar loadingIndicator;
    @BindView(R.id.app_name_tv)
    TextView appNameTextView;
    private String sortByType;
    private MovieRecyclerAdapter movieRecyclerAdapter;
    @BindView(R.id.no_internet_image)
    ImageView noInternetImageView;
    @BindView(R.id.no_internet_tv)
    TextView noInternetTextView;
    @BindView(R.id.main_root_view)
    ConstraintLayout mainRootView;
    private String SORT_BY_TYPE_STATE_KEY = "sort_key";
    private LiveData<PagedList<MovieModel>> movieModelPagedList;
    private MovieViewModel movieViewModel;
    private static SharedPreferences sharedPreferences;
    private int numOfColumnsAccordingToOrientation;
    private List<FavouriteMovieModel> listOfFavMovies;
    private FavouriteMovieAdapter favouriteMovieAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        numOfColumnsAccordingToOrientation =
                getResources().getInteger(R.integer.grid_column_count);
        if(savedInstanceState == null) {
            //By default the movies would be sorted as popular
            sortByType = PrefUtils.getSortOrderType(this);
        } else {
            sortByType = (String) savedInstanceState.getCharSequence(SORT_BY_TYPE_STATE_KEY);
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ButterKnife.bind(this);
        listOfFavMovies = new ArrayList<>();
        if(NetworkAndDatabaseUtils.isConnectionAvailable(this)) {
            displayLoadingScreen();
            noInternetTextView.setVisibility(View.INVISIBLE);
            noInternetImageView.setVisibility(View.INVISIBLE);
            NetworkAndDatabaseUtils.deleteAllPreviousMovies(this);
            //Assigning the GridLayout Manager with a spanCount of 2 and vertical orientation
            GridLayoutManager movieGridManager =
                    new GridLayoutManager(this, numOfColumnsAccordingToOrientation,
                            GridLayoutManager.VERTICAL, false);
            movieRecyclerAdapter = new MovieRecyclerAdapter();
            movieRecyclerAdapter.setContextAndMovieGridListener(this,this);
            movieGridRecyclerView.setLayoutManager(movieGridManager);
            movieGridRecyclerView.setAdapter(movieRecyclerAdapter);
            movieViewModel =  ViewModelProviders.of(this).get(MovieViewModel.class);
            movieModelPagedList = movieViewModel.getMovieLiveData();
            movieModelPagedList.observe(this, new Observer<PagedList<MovieModel>>() {
                @Override
                public void onChanged(@Nullable PagedList<MovieModel> movieModels) {
                    movieRecyclerAdapter.submitList(movieModels);
                }
            });
            movieGridRecyclerView.setVisibility(View.VISIBLE);
            loadingIndicator.setVisibility(View.INVISIBLE);
            appNameTextView.setVisibility(View.INVISIBLE);
        } else {
            displayNoInternetScreen();
            setupFavMovieView();
        }
        favouriteMovieAdapter = new FavouriteMovieAdapter(this);
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
    private void setupFavMovieView() {
        new FavMovieAsyncTask().execute();
        movieGridRecyclerView.setAdapter(favouriteMovieAdapter);
    }
    private void revokeFavMovieView() {
        if(NetworkAndDatabaseUtils.isConnectionAvailable(this)) {
            //displayLoadingScreen();
            noInternetTextView.setVisibility(View.INVISIBLE);
            noInternetImageView.setVisibility(View.INVISIBLE);
            NetworkAndDatabaseUtils.deleteAllPreviousMovies(this);
            movieGridRecyclerView.setAdapter(movieRecyclerAdapter);
            if(movieViewModel != null) {
                movieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
            }
            movieGridRecyclerView.setVisibility(View.VISIBLE);
            loadingIndicator.setVisibility(View.INVISIBLE);
            appNameTextView.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(!NetworkAndDatabaseUtils.isConnectionAvailable(this)) {
            displayNoInternetScreen();
        } else {
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_sort_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(NetworkAndDatabaseUtils.isConnectionAvailable(this)) {
            switch (item.getItemId()) {
                case R.id.action_sort_by_popular:
                    if (!sortByType.equals(getString(R.string.menuItemPopular))) {
                        if(favouriteMovieAdapter != null) {
                            revokeFavMovieView();
                        }
                        sortByType = this.getString(R.string.sortByPopular);
                        Log.d("sort type","<======= " + sortByType + " =====>");
                        PrefUtils.changeSortOrderType(this,sortByType);
                        Log.d("changed","<======= Yes =======>");
                   }
                    return true;
                case R.id.action_sort_by_highest_rated:
                    if (!sortByType.equals(getString(R.string.menuItemHighestRated))) {
                        if(favouriteMovieAdapter != null) {
                            revokeFavMovieView();
                        }
                        sortByType = this.getString(R.string.sortByHighestRated);
                        PrefUtils.changeSortOrderType(this,sortByType);
                    }
                    return true;
                case R.id.action_show_favourites:
                    setupFavMovieView();
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
    @Override
    public void onClickMovie(int positionClicked) {
        Intent intentToMovieDetail = new Intent(MainActivity.this,MovieDetailActivity.class);
        if(movieModelPagedList != null) {
            intentToMovieDetail.putExtra("MOVIE_ID",
                    movieModelPagedList.getValue().get(positionClicked).getId());
            startActivity(intentToMovieDetail);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d("Inside pref changed ==>","<===== Listener Called -- Yes ============>");
        if(key.equals(getString(R.string.pref_sort_type))) {
            movieViewModel.setSortOrder(this);
            movieModelPagedList = movieViewModel.getMovieLiveData();
            Log.d("New Data Changed", "<========== Changed and Received ===========>");
        }
    }
    private class FavMovieAsyncTask extends AsyncTask<Void,Void,List<FavouriteMovieModel>> {
        @Override
        protected List<FavouriteMovieModel> doInBackground(Void... voids) {
            return MovieDatabase
                    .getMovieDbInstance(getApplicationContext())
                    .getFavMovieDao()
                    .getListOfAllFavMovies();
        }

        @Override
        protected void onPostExecute(List<FavouriteMovieModel> favouriteMovieModels) {
            super.onPostExecute(favouriteMovieModels);
            listOfFavMovies = favouriteMovieModels;
            favouriteMovieAdapter.setDataToFavList(listOfFavMovies);
        }
    }
}
//TODO(1) Change the star to black when clicked on