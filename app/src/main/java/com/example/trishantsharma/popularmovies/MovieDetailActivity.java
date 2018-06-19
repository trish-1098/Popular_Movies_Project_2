package com.example.trishantsharma.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.trishantsharma.popularmovies.movie_details_classes.MovieCastAdapter;
import com.example.trishantsharma.popularmovies.movie_details_classes.MovieProductionCompaniesAdapter;
import com.example.trishantsharma.popularmovies.utils.JSONUtils;
import com.example.trishantsharma.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Movie>{
    private int idOfMovieSelected;
    private static final int LOADER_ID = 100;
    private static URL finalUrlForSelectedMovie;
    private static URL finalUrlForSelectedMovieCast;
    private static Movie finalMovieObjectReceived;

    @BindView(R.id.progress_for_selected_movie_loading)
    ProgressBar progressBarForMovieLoading;
    @BindView(R.id.movie_detail_title_tv)
    TextView movieTitleTextView;
    @BindView(R.id.language_detail_tv)
    TextView movieLanguageTextView;
    @BindView(R.id.release_date_tv)
    TextView movieReleaseDateTextView;
    @BindView(R.id.star_rating_tv)
    TextView movieRatingTextView;
    @BindView(R.id.genre_tv)
    TextView movieGenresTextView;
    @BindView(R.id.viewer_rating_tv)
    TextView movieViewerRatingTextView;
    @BindView(R.id.tagline_of_movie_tv)
    TextView movieTaglineTextView;
    @BindView(R.id.runtime_tv)
    TextView movieRuntimeTextView;
    @BindView(R.id.overview_tv)
    TextView movieOverviewTextView;
    @BindView(R.id.more_details_link_tv)
    TextView movieMoreDetailsLinkTextView;
    @BindView(R.id.movie_cover_iv)
    ImageView movieCoverImageView;
    @BindView(R.id.movie_poster_iv)
    ImageView moviePosterImageView;
    @BindView(R.id.cast_recycler_view)
    RecyclerView movieCastRecyclerView;
    @BindView(R.id.production_company_recycler_view)
    RecyclerView movieProductionCompanyRecyclerView;
    @BindView(R.id.no_internet_image_for_detail)
    ImageView noInternetImageView;
    @BindView(R.id.language_icon_iv)
    ImageView languageIcon;
    @BindView(R.id.star_rating_icon)
    ImageView ratingIcon;
    @BindView(R.id.genre_icon_iv)
    ImageView genreIcon;
    @BindView(R.id.release_date_icon)
    ImageView releaseDateIcon;
    @BindView(R.id.runtime_icon_iv)
    ImageView runtimeIcon;
    @BindView(R.id.overview_label_tv)
    TextView overviewLabel;
    @BindView(R.id.viewer_rating_label_tv)
    TextView viewerRatingLabel;
    @BindView(R.id.cast_label_tv)
    TextView castLabel;
    @BindView(R.id.production_companies_label_tv)
    TextView productionCompaniesLabel;

    private MovieCastAdapter movieCastAdapter;
    private MovieProductionCompaniesAdapter movieProductionCompaniesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
//        progressBarForMovieLoading = findViewById(R.id.progress_for_selected_movie_loading);
//        movieTitleTextView = findViewById(R.id.movie_detail_title_tv);
//        movieLanguageTextView = findViewById(R.id.language_detail_tv);
//        movieReleaseDateTextView = findViewById(R.id.release_date_tv);
//        movieRatingTextView = findViewById(R.id.star_rating_tv);
//        movieGenresTextView = findViewById(R.id.genre_tv);
//        movieViewerRatingTextView = findViewById(R.id.viewer_rating_tv);
//        movieTaglineTextView = findViewById(R.id.tagline_of_movie_tv);
//        movieRuntimeTextView = findViewById(R.id.runtime_tv);
//        movieOverviewTextView = findViewById(R.id.overview_tv);
//        movieMoreDetailsLinkTextView = findViewById(R.id.more_details_link_tv);
//        movieCoverImageView = findViewById(R.id.movie_cover_iv);
//        moviePosterImageView = findViewById(R.id.movie_poster_iv);
//        movieCastRecyclerView = findViewById(R.id.cast_recycler_view);
//        movieProductionCompanyRecyclerView = findViewById(R.id.production_company_recycler_view);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(!TextUtils.isEmpty(getIntent().getStringExtra("MOVIE_ID"))) {
            idOfMovieSelected = Integer.parseInt(getIntent().getStringExtra("MOVIE_ID"));
        }
        if(NetworkUtils.isConnectionAvailable(this)) {
            noInternetImageView.setVisibility(View.GONE);
            buildUrlForMovieDetailsAndCasts();
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);
            //Setting the Layout Manager to both RecyclerView
            movieCastRecyclerView
                    .setLayoutManager(new LinearLayoutManager(this,
                            LinearLayoutManager.HORIZONTAL, false));
            movieProductionCompanyRecyclerView
                    .setLayoutManager(new LinearLayoutManager(this,
                            LinearLayoutManager.HORIZONTAL, false));
            movieCastAdapter = new MovieCastAdapter(this);
            movieProductionCompaniesAdapter = new MovieProductionCompaniesAdapter(this);
            movieCastRecyclerView.setAdapter(movieCastAdapter);
            movieProductionCompanyRecyclerView.setAdapter(movieProductionCompaniesAdapter);
        } else {
            noInternetImageView.setVisibility(View.VISIBLE);
            progressBarForMovieLoading.setVisibility(View.GONE);
            movieTitleTextView.setVisibility(View.GONE);
            movieLanguageTextView.setVisibility(View.GONE);
            movieReleaseDateTextView.setVisibility(View.GONE);
            movieRatingTextView.setVisibility(View.GONE);
            movieGenresTextView.setVisibility(View.GONE);
            movieViewerRatingTextView.setVisibility(View.GONE);
            movieTaglineTextView.setVisibility(View.GONE);
            movieRuntimeTextView.setVisibility(View.GONE);
            movieOverviewTextView.setVisibility(View.GONE);
            movieMoreDetailsLinkTextView.setVisibility(View.GONE);
            movieOverviewTextView.setVisibility(View.GONE);
            movieCoverImageView.setVisibility(View.GONE);
            moviePosterImageView.setVisibility(View.GONE);
            movieCastRecyclerView.setVisibility(View.GONE);
            movieProductionCompanyRecyclerView.setVisibility(View.GONE);
            languageIcon.setVisibility(View.GONE);
            releaseDateIcon.setVisibility(View.GONE);
            runtimeIcon.setVisibility(View.GONE);
            genreIcon.setVisibility(View.GONE);
            ratingIcon.setVisibility(View.GONE);
            overviewLabel.setVisibility(View.GONE);
            viewerRatingLabel.setVisibility(View.GONE);
            castLabel.setVisibility(View.GONE);
            productionCompaniesLabel.setVisibility(View.GONE);
        }
    }
    private void buildUrlForMovieDetailsAndCasts(){
        finalUrlForSelectedMovie = NetworkUtils.buildUrlForAParticularMovie(idOfMovieSelected);
        finalUrlForSelectedMovieCast =
                NetworkUtils.buildUrlForCastOfParticularMovie(this,idOfMovieSelected);
    }
    @NonNull
    @Override
    public Loader<Movie> onCreateLoader(int id, @Nullable Bundle args) {
        return new MovieDetailLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Movie> loader, Movie movieObjectAfterParsing) {
        finalMovieObjectReceived = movieObjectAfterParsing;
        progressBarForMovieLoading.setVisibility(View.GONE);
        setAllDetailsOfMovie();
        movieCastAdapter.setDataToArrayList(movieObjectAfterParsing.getCastNameAndImage());
        //movieCastAdapter.notifyDataSetChanged();
        movieProductionCompaniesAdapter
                .setDataToArrayList(movieObjectAfterParsing.getProductionCompanies());
        //movieProductionCompaniesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Movie> loader) {
        finalMovieObjectReceived = null;
        onCreateLoader(LOADER_ID,null);
    }
    private static class MovieDetailLoader extends AsyncTaskLoader<Movie>{
        public MovieDetailLoader(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Nullable
        @Override
        public Movie loadInBackground() {
            String jsonResponseOfSelectedMovie =
                    NetworkUtils.getResponseFromHttpConnection(finalUrlForSelectedMovie);
            String jsonResponseOfCastOfSelectedMovie =
                    NetworkUtils.getResponseFromHttpConnection(finalUrlForSelectedMovieCast);
            return JSONUtils
                    .parseSelectedMovieAndCast(jsonResponseOfSelectedMovie,
                            jsonResponseOfCastOfSelectedMovie);
        }
    }
    private void setAllDetailsOfMovie() {
        Picasso.get()
                .load(buildPicassoBackdropLoadingUri(finalMovieObjectReceived.getPathToBackDropImage()))
                .into(movieCoverImageView);
        Picasso.get()
                .load(buildPicassoPosterLoadingUri(finalMovieObjectReceived.getPathTooPoster()))
                .into(moviePosterImageView);
        movieTitleTextView.setText(finalMovieObjectReceived.getTitleOfMovie());
        movieLanguageTextView.setText(finalMovieObjectReceived.getLanguageOfMovie());
        movieReleaseDateTextView.setText(finalMovieObjectReceived.getReleaseDate());
        movieRatingTextView.setText(Double.toString(finalMovieObjectReceived.getAvgRating()));
        for(int i = 0; i < finalMovieObjectReceived.getGenreOfMovie().size(); i++) {
            movieGenresTextView.append(finalMovieObjectReceived.getGenreOfMovie().get(i) + "\n");
        }
        if(finalMovieObjectReceived.isAdult()) {
            movieViewerRatingTextView.setText(getString(R.string.isAdultTrue));
        } else {
            movieViewerRatingTextView.setText(getString(R.string.isAdultFalse));
        }
        movieTaglineTextView.setText(finalMovieObjectReceived.getTagLineOfMovie());
        movieRuntimeTextView
                .setText(finalMovieObjectReceived.getRuntimeOfMovie() + " " +
                        getString(R.string.runtime_unit));
        movieOverviewTextView.setText(finalMovieObjectReceived.getOverviewDescription());
        //Setting the link to the More Details TextView
        String linkText = "<a href='" + finalMovieObjectReceived.getMoreDetailsWebsite() + "'>Click Here</a> if you want to know more !!";
        movieMoreDetailsLinkTextView.setText(Html.fromHtml(linkText));
        movieMoreDetailsLinkTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }
    private Uri buildPicassoPosterLoadingUri(String pathToImage) {
        Uri imageUri;
        final String baseUri = "http://image.tmdb.org/t/p/";
        final String imageSize = "w342";
        imageUri = Uri.parse(baseUri + imageSize + "/" + pathToImage);
        return imageUri;
    }
    private Uri buildPicassoBackdropLoadingUri(String pathToImage) {
        Uri imageUri;
        final String baseUri = "http://image.tmdb.org/t/p/";
        final String imageSize = "w500";
        imageUri = Uri.parse(baseUri + imageSize + "/" + pathToImage);
        return imageUri;
    }
}
//TODO(1) Handle lifecycle errors where the genres appear two times
//TODO(2)