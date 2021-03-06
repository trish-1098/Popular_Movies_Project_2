package com.example.trishantsharma.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trishantsharma.popularmovies.adapters.MovieCastAdapter;
import com.example.trishantsharma.popularmovies.adapters.MovieProductionCompaniesAdapter;
import com.example.trishantsharma.popularmovies.adapters.ReviewAdapter;
import com.example.trishantsharma.popularmovies.adapters.TrailerAdapter;
import com.example.trishantsharma.popularmovies.database.MovieDatabase;
import com.example.trishantsharma.popularmovies.models.CastModel;
import com.example.trishantsharma.popularmovies.models.FavouriteMovieModel;
import com.example.trishantsharma.popularmovies.models.MovieDetailModel;
import com.example.trishantsharma.popularmovies.models.ReviewModel;
import com.example.trishantsharma.popularmovies.models.TrailerModel;
import com.example.trishantsharma.popularmovies.networkdata.NetworkAndDatabaseUtils;
import com.example.trishantsharma.popularmovies.utils.AppExecutors;
import com.example.trishantsharma.popularmovies.utils.ConstantUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity
        implements ReviewAdapter.OnReviewClickListener,
        TrailerAdapter.OnTrailerClickListener,
        LoaderManager.LoaderCallbacks<Void>{
    private static int idOfMovieSelected;
    private static final int LOADER_ID = 100;
    private Context context;
    private FavouriteMovieModel favouriteMovieModel;

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
    @BindView(R.id.detail_root_view)
    ScrollView movieDetailRootView;
    @BindView(R.id.trailer_recycler_view)
    RecyclerView movieTrailerRecyclerView;
    @BindView(R.id.review_recycler_view)
    RecyclerView movieReviewRecyclerView;
    @BindView(R.id.mark_as_fav_button)
    ImageButton markAsFavouriteButton;
    @BindView(R.id.trailer_label_tv)
    TextView trailerLabel;
    @BindView(R.id.review_label_tv)
    TextView reviewLabel;
    private static MovieDetailModel movieDetailModel;
    private List<TrailerModel> trailerModelList;
    private List<ReviewModel> reviewModelList;
    private List<CastModel> castModelList;
    private MovieCastAdapter movieCastAdapter;
    private MovieProductionCompaniesAdapter movieProductionCompaniesAdapter;
    private TrailerAdapter movieTrailerAdapter;
    private ReviewAdapter movieReviewAdapter;
    boolean checkIsFromMainOrOptions; //true for Fav //false for Main
    //TODO Fix star problem with the same intent solution
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
        context = MovieDetailActivity.this;
        if(getIntent().getIntExtra("MOVIE_ID",1) != 1) {
            idOfMovieSelected = getIntent().getIntExtra("MOVIE_ID",1);
            trailerModelList = new ArrayList<>();
            reviewModelList = new ArrayList<>();
            castModelList = new ArrayList<>();
            checkIsFromMainOrOptions = getIntent().getBooleanExtra(ConstantUtils.intentToDetailSourceCheckKey, false);
        }
        if(NetworkAndDatabaseUtils.isConnectionAvailable(this)) {
            noInternetImageView.setVisibility(View.GONE);
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);
            movieCastRecyclerView
                    .setLayoutManager(new LinearLayoutManager(this,
                            LinearLayoutManager.HORIZONTAL, false));
            movieTrailerRecyclerView
                    .setLayoutManager(new LinearLayoutManager(this,
                            LinearLayoutManager.HORIZONTAL,false));
            movieReviewRecyclerView
                    .setLayoutManager(new LinearLayoutManager(this,
                            LinearLayoutManager.HORIZONTAL,false));
            movieProductionCompanyRecyclerView
                    .setLayoutManager(new LinearLayoutManager(this,
                            LinearLayoutManager.HORIZONTAL, false));
            movieCastAdapter = new MovieCastAdapter(this);
            movieTrailerAdapter = new TrailerAdapter(this,this);
            movieReviewAdapter = new ReviewAdapter(this,this);
            movieProductionCompaniesAdapter = new MovieProductionCompaniesAdapter(this);
            movieCastRecyclerView.setAdapter(movieCastAdapter);
            movieTrailerRecyclerView.setAdapter(movieTrailerAdapter);
            movieReviewRecyclerView.setAdapter(movieReviewAdapter);
            movieProductionCompanyRecyclerView.setAdapter(movieProductionCompaniesAdapter);
        } else {
            new OfflineMovieLoad().execute();
            noInternetImageView.setVisibility(View.GONE);
        }
        markAsFavouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FavMovieAsyncTask().execute(context);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.homeAsUp:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Void> onCreateLoader(int id, @Nullable Bundle args) {
        return new MovieDetailLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Void> loader, Void data) {
        Object[] objects = NetworkAndDatabaseUtils.getAllMovieDetails();
        try {
            movieDetailModel = (MovieDetailModel) objects[0];
            trailerModelList = (List<TrailerModel>) objects[1];
            reviewModelList = (List<ReviewModel>) objects[2];
            castModelList = (List<CastModel>) objects[3];
        } catch(NullPointerException e) {
            Log.d("Exception in Details =>","<========= Something is null ===========>");
            e.printStackTrace();
        }
        setAllDetailsOfMovie();
        progressBarForMovieLoading.setVisibility(View.GONE);
//        Log.d("Inside onLoadFinished","<=========== Yes ============> ");
//        for (int i = 0; i < trailerModelList.size(); i++) {
//            Log.d("Trailer ==>","<====== " + trailerModelList.get(i).getName() + " ========>");
//        }
//        for (TrailerModel trailerModel: trailerModelList) {
//            Log.d("Trailer ==>","<====== " + trailerModel.getName() + " ========>");
//        }
//        for (ReviewModel reviewModel: reviewModelList) {
//            Log.d("Review ==>","<======= " + reviewModel.getAuthor() + "========>");
//        }
//        for(CastModel castModel: castModelList) {
//            Log.d("Cast ==>","<======= " + castModel.getName() + "=========>");
//        }
//        //setAllDetailsOfMovie();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Void> loader) {
    }

    @Override
    public void onReviewClick(int positionClicked) {
        if(reviewModelList != null) {
            Intent openReviewWebsite = new Intent(Intent.ACTION_VIEW,Uri.parse(reviewModelList.get(positionClicked).getUrl()));
            startActivity(openReviewWebsite);
        }
        //TODO onReviewClick
    }

    @Override
    public void onTrailerClick(int positionClicked) {
        Log.d("Trailer ==>","<====== Yes =========>");
        if(trailerModelList != null) {
            Intent openTrailerInYoutube =
                    new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" +
                            trailerModelList.get(positionClicked).getKey()));
            startActivity(openTrailerInYoutube);
        }
    }

    private static class MovieDetailLoader extends AsyncTaskLoader<Void>{
        public MovieDetailLoader(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Nullable
        @Override
        public Void loadInBackground() {
            NetworkAndDatabaseUtils.fetchMovieDetails(idOfMovieSelected);
            return null;
        }
    }
    private void setAllDetailsOfMovie() {
        if(NetworkAndDatabaseUtils.isConnectionAvailable(this)) {
            Picasso.get()
                    .load(NetworkAndDatabaseUtils
                            .buildPicassoBackdropLoadingUri(movieDetailModel.getBackdropPath()))
                    .into(movieCoverImageView);
            Picasso.get()
                    .load(NetworkAndDatabaseUtils.buildUriForPicassoImage(movieDetailModel.getPosterPath()))
                    .into(moviePosterImageView);

            movieTitleTextView.setText(movieDetailModel.getOriginalTitle());
            movieLanguageTextView.setText(movieDetailModel.getOriginalLanguage());
            //TODO(Date) Display Correct and User Friendly Date
            movieReleaseDateTextView.setText(movieDetailModel.getReleaseDate());
            movieRatingTextView.setText(Double.toString(movieDetailModel.getVoteAverage()));
            for (int i = 0; i < movieDetailModel.getGenres().size(); i++) {
                movieGenresTextView.append(movieDetailModel.getGenres().get(i).getName() + "\n");
            }
            if (movieDetailModel.getAdult()) {
                movieViewerRatingTextView.setText(getString(R.string.isAdultTrue));
            } else {
                movieViewerRatingTextView.setText(getString(R.string.isAdultFalse));
            }
            movieTaglineTextView.setText(movieDetailModel.getTagline());
            movieRuntimeTextView
                    .setText(movieDetailModel.getRuntime() + " " +
                            getString(R.string.runtime_unit));
            movieOverviewTextView.setText(movieDetailModel.getOverview());
            //Setting the link to the More Details TextView
            String linkText = "<a href='" + movieDetailModel.getHomepage() + "'>Click Here</a> if you want to know more !!";
            movieMoreDetailsLinkTextView.setText(Html.fromHtml(linkText));
            movieMoreDetailsLinkTextView.setMovementMethod(LinkMovementMethod.getInstance());
            if (castModelList != null) {
                movieCastAdapter.setDataToArrayList((ArrayList) castModelList);
            } else {
                castLabel.setVisibility(View.GONE);
            }
            if (trailerModelList != null) {
                movieTrailerAdapter.setTrailerArrayList((ArrayList) trailerModelList);
            } else {
                trailerLabel.setVisibility(View.GONE);
            }
            if (reviewModelList != null) {
                movieReviewAdapter.setReviewsArrayList((ArrayList) reviewModelList);
            } else {
                reviewLabel.setVisibility(View.GONE);
            }
            movieProductionCompaniesAdapter.setDataToArrayList((ArrayList) movieDetailModel.getProductionCompanies());
        } else {
            if(favouriteMovieModel != null) {
                movieTitleTextView.setText(favouriteMovieModel.getTitleOfMovie());
                movieLanguageTextView.setText(favouriteMovieModel.getLanguageOfMovie());
                movieReleaseDateTextView.setText(favouriteMovieModel.getReleaseDate());
                movieRatingTextView.setText(Double.toString(favouriteMovieModel.getAvgRating()));
                movieTaglineTextView.setText(favouriteMovieModel.getTagLineOfMovie());
                movieOverviewTextView.setText(favouriteMovieModel.getOverviewDescription());
                movieRuntimeTextView.setText(favouriteMovieModel.getRuntimeOfMovie());
                trailerLabel.setVisibility(View.GONE);
                movieTrailerRecyclerView.setVisibility(View.GONE);
                reviewLabel.setVisibility(View.GONE);
                movieReviewRecyclerView.setVisibility(View.GONE);
                productionCompaniesLabel.setVisibility(View.GONE);
                movieProductionCompanyRecyclerView.setVisibility(View.GONE);
                progressBarForMovieLoading.setVisibility(View.GONE);
                moviePosterImageView.setVisibility(View.INVISIBLE);
                movieCoverImageView.setVisibility(View.INVISIBLE);
                castLabel.setVisibility(View.GONE);
                viewerRatingLabel.setVisibility(View.GONE);
                genreIcon.setVisibility(View.INVISIBLE);
            }
        }
    }
    private class OfflineMovieLoad extends AsyncTask<Void,Void,FavouriteMovieModel> {
        @Override
        protected void onPostExecute(FavouriteMovieModel favouriteMovieModelReturned) {
            super.onPostExecute(favouriteMovieModel);
            favouriteMovieModel = favouriteMovieModelReturned;
            if(favouriteMovieModel != null) {
                setAllDetailsOfMovie();
            }
        }

        @Override
        protected FavouriteMovieModel doInBackground(Void... voids) {
            return MovieDatabase.getMovieDbInstance(context)
                    .getFavMovieDao()
                    .getSelectedFavMovie(idOfMovieSelected);
        }
    }
    private class FavMovieAsyncTask extends AsyncTask<Context,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Context... contexts) {
            return NetworkAndDatabaseUtils.addOrDeleteFavMovie(contexts[0],idOfMovieSelected,movieDetailModel);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean) {
                Toast.makeText(context,
                        context.getString(R.string.deleted_fav_movie), Toast.LENGTH_LONG)
                        .show();
                if(checkIsFromMainOrOptions) {
                    Intent goBackToMainActivity = new Intent(MovieDetailActivity.this, MainActivity.class);
                    goBackToMainActivity.putExtra("REMOVED", true);
                    startActivity(goBackToMainActivity);
                }
            } else {
                Toast.makeText(context,
                        context.getString(R.string.added_fav_movie), Toast.LENGTH_SHORT)
                        .show();
            }
            super.onPostExecute(aBoolean);
        }
    }
}