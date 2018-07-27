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
import com.example.trishantsharma.popularmovies.models.FavouriteMovieModel;
import com.example.trishantsharma.popularmovies.models.MovieModel;
import com.example.trishantsharma.popularmovies.networkdata.NetworkAndDatabaseUtils;
import com.example.trishantsharma.popularmovies.utils.ConstantUtils;
import com.example.trishantsharma.popularmovies.utils.PrefUtils;
import com.example.trishantsharma.popularmovies.viewmodel.MovieViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements MovieRecyclerAdapter.MovieGridOnClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener,
        FavouriteMovieAdapter.FavMovieClickListener {
    @BindView(R.id.movie_grid_recycler_view)
    RecyclerView movieGridRecyclerView;
    private String sortByType;
    private MovieRecyclerAdapter movieRecyclerAdapter;
    @BindView(R.id.no_internet_image)
    ImageView noInternetImageView;
    @BindView(R.id.no_internet_tv)
    TextView noInternetTextView;
    @BindView(R.id.main_root_view)
    ConstraintLayout mainRootView;
    @BindView(R.id.fav_movie_recycler_view)
    RecyclerView favMovieRecyclerView;
    private String SORT_BY_TYPE_STATE_KEY = "sort_key";
    private static LiveData<PagedList<MovieModel>> movieModelPagedList;
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
        //listOfFavMovies = new ArrayList<>();
        noInternetTextView.setVisibility(View.GONE);
        noInternetImageView.setVisibility(View.GONE);
        if(getIntent() != null && getIntent().hasExtra("REMOVED")) {
            if(getIntent().getBooleanExtra("REMOVED",false)) {
                setupFavMovieView();
            }
        }
        else {
            setupAllMoviesView();
        }
            favouriteMovieAdapter = new FavouriteMovieAdapter(this, this);
            favMovieRecyclerView
                    .setLayoutManager(new GridLayoutManager(this,
                            numOfColumnsAccordingToOrientation, GridLayoutManager.VERTICAL,
                            false));
            favMovieRecyclerView.setAdapter(favouriteMovieAdapter);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            getSupportActionBar()
                    .setIcon(ContextCompat
                            .getDrawable(this, R.drawable.ic_filter_list_white_24dp));
        }
    }
    private void setupAllMoviesView() {
        if (NetworkAndDatabaseUtils.isConnectionAvailable(this)) {
            NetworkAndDatabaseUtils.deleteAllPreviousMovies(this);
            //Assigning the GridLayout Manager with a spanCount of 2 and vertical orientation
            favMovieRecyclerView.setVisibility(View.GONE);
            GridLayoutManager movieGridManager =
                    new GridLayoutManager(this, numOfColumnsAccordingToOrientation,
                            GridLayoutManager.VERTICAL, false);
            movieRecyclerAdapter = new MovieRecyclerAdapter();
            movieRecyclerAdapter.setContextAndMovieGridListener(this, this);
            movieGridRecyclerView.setLayoutManager(movieGridManager);
            movieGridRecyclerView.setAdapter(movieRecyclerAdapter);
            movieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
            movieModelPagedList = movieViewModel.getMovieLiveData();
            movieModelPagedList.observe(this, new Observer<PagedList<MovieModel>>() {
                @Override
                public void onChanged(@Nullable PagedList<MovieModel> movieModels) {
                    movieRecyclerAdapter.submitList(movieModels);
                }
            });
            movieGridRecyclerView.setVisibility(View.VISIBLE);
        } else {
            //displayNoInternetScreen();
            setupFavMovieView();
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

//    private void displayNoInternetScreen() {
//        //Code to show a screen in case the user is not connected to is no Internet
//        movieGridRecyclerView.setVisibility(View.INVISIBLE);
//        noInternetTextView.setVisibility(View.VISIBLE);
//        noInternetImageView.setVisibility(View.VISIBLE);
//        loadingIndicator.getRootView()
//                .setBackgroundColor(getResources().getColor(R.color.no_internet_background));
//    }
    private void setupFavMovieView() {
        new FavMovieAsyncTask().execute();
        favMovieRecyclerView.setVisibility(View.VISIBLE);
        movieGridRecyclerView.setVisibility(View.GONE);
        //favMovieRecyclerView.setAdapter(favouriteMovieAdapter);
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
            favMovieRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        if(NetworkAndDatabaseUtils.isConnectionAvailable(this)) {
            String temp = PrefUtils.getSortOrderType(this);
            if (temp.equals(getString(R.string.sortByPopular))) {
                getSupportActionBar().setTitle(getString(R.string.menuItemPopular));
            } else if (temp.equals(getString(R.string.sortByHighestRated))) {
                getSupportActionBar().setTitle(getString(R.string.menuItemHighestRated));
            }
        }
        else {
            getSupportActionBar().setTitle(getString(R.string.fav_movies));
        }
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
                    if(movieViewModel == null) {
                        movieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
                        setupAllMoviesView();
                    }
                    if (!sortByType.equals(getString(R.string.menuItemPopular))) {
                        if(favouriteMovieAdapter != null) {
                            revokeFavMovieView();
                        }
                        sortByType = this.getString(R.string.sortByPopular);
                        PrefUtils.changeSortOrderType(this,sortByType);
                        setSpecificTitle(getString(R.string.menuItemPopular));
                   }
                    return true;
                case R.id.action_sort_by_highest_rated:
                    if(movieViewModel == null) {
                        movieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
                        setupAllMoviesView();
                    }
                    if (!sortByType.equals(getString(R.string.menuItemHighestRated))) {
                        if(favouriteMovieAdapter != null) {
                            revokeFavMovieView();
                        }
                        sortByType = this.getString(R.string.sortByHighestRated);
                        PrefUtils.changeSortOrderType(this,sortByType);
                        setSpecificTitle(getString(R.string.menuItemHighestRated));
                    }
                    return true;
                case R.id.action_show_favourites:
                    setupFavMovieView();
                    setSpecificTitle(getString(R.string.fav_movies));
            }
        }
        return super.onOptionsItemSelected(item);
    }
    private void setSpecificTitle(String specificTitle) {
        getSupportActionBar().setTitle(specificTitle);
    }
    @Override
    public void onClickMovie(int positionClicked) {
        Intent intentToMovieDetail = new Intent(MainActivity.this,MovieDetailActivity.class);
        if(movieModelPagedList != null) {
            intentToMovieDetail.putExtra(ConstantUtils.intentToDetailExtraKey,
                    movieModelPagedList.getValue().get(positionClicked).getId());
            intentToMovieDetail.putExtra(ConstantUtils.intentToDetailSourceCheckKey,false);
            startActivity(intentToMovieDetail);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.pref_sort_type))) {
            movieViewModel.setSortOrder(this);
            movieModelPagedList = movieViewModel.getMovieLiveData();
        }
    }

    @Override
    public void onFavClick(int positionClicked) {
        Intent openDetails = new Intent(this, MovieDetailActivity.class);
        openDetails.putExtra(ConstantUtils.intentToDetailExtraKey,
                listOfFavMovies.get(positionClicked).getMovieId());
        openDetails.putExtra(ConstantUtils.intentToDetailSourceCheckKey,true);
        startActivity(openDetails);
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
            if(listOfFavMovies.isEmpty()) {
                noInternetImageView.setVisibility(View.VISIBLE);
                noInternetTextView.setVisibility(View.VISIBLE);
            } else {
                favouriteMovieAdapter.setDataToFavList(listOfFavMovies);
            }
        }
    }
}
