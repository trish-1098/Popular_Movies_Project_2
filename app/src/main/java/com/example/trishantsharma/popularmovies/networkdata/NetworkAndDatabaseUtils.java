package com.example.trishantsharma.popularmovies.networkdata;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.trishantsharma.popularmovies.FavouriteMovieModel;
import com.example.trishantsharma.popularmovies.MainActivity;
import com.example.trishantsharma.popularmovies.R;
import com.example.trishantsharma.popularmovies.containers.CastContainerModel;
import com.example.trishantsharma.popularmovies.containers.MovieContainerModel;
import com.example.trishantsharma.popularmovies.containers.ReviewContainer;
import com.example.trishantsharma.popularmovies.containers.TrailerContainer;
import com.example.trishantsharma.popularmovies.database.MovieDatabase;
import com.example.trishantsharma.popularmovies.models.CastModel;
import com.example.trishantsharma.popularmovies.models.MovieDetailModel;
import com.example.trishantsharma.popularmovies.models.MovieModel;
import com.example.trishantsharma.popularmovies.models.ReviewModel;
import com.example.trishantsharma.popularmovies.models.TrailerModel;
import com.example.trishantsharma.popularmovies.utils.AppExecutors;
import com.example.trishantsharma.popularmovies.utils.ConstantUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NetworkAndDatabaseUtils {
    private static MovieDetailModel movieDetailModelFinal = new MovieDetailModel();
    private static List<TrailerModel> trailerModelFinalList = new ArrayList<>();
    private static List<ReviewModel> reviewModelFinalList = new ArrayList<>();
    private static List<CastModel> castModelFinalList = new ArrayList<>();
    private static List<FavouriteMovieModel> favouriteMovieModelList = new ArrayList<>();
    private static boolean isInserted = false;
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
    public static Uri buildUriForTrailerPicassoImage(String keyOfImage) {
        if(!TextUtils.isEmpty(keyOfImage)) {
            return Uri.parse(ConstantUtils.baseUrlForTrailerPicassoImage + keyOfImage + "/hqdefault.jpg");
        }
        return null;
    }
    public static Uri buildPicassoBackdropLoadingUri(String pathToImage) {
        Uri imageUri;
        final String baseUri = "http://image.tmdb.org/t/p/";
        final String imageSize = "w500";
        imageUri = Uri.parse(baseUri + imageSize + "/" + pathToImage);
        return imageUri;
    }
    public static void fetchNewMoviesAndSaveToDatabase(String sortTypeRequired, int pageNumber, final Context context) {
        MovieAPI movieAPI = MovieDataClient.getAPI();
        Call<MovieContainerModel> movieContainerModelCall = movieAPI.getMoviesBySortOrder(sortTypeRequired,pageNumber);
        movieContainerModelCall.enqueue(new Callback<MovieContainerModel>() {
            @Override
            public void onResponse(Call<MovieContainerModel> call, Response<MovieContainerModel> response) {
                MovieContainerModel movieContainerModel = response.body();
                List<MovieModel> moviesList = movieContainerModel.getResults();
                for (MovieModel movieModel:moviesList) {
                    Log.d("The movie received","<======At position " + movieModel.getOriginalTitle() + "======>");
                }
                addMovieDataInDatabase(moviesList,context);
            }

            @Override
            public void onFailure(Call<MovieContainerModel> call, Throwable t) {

            }
        });
    }
    private static void addMovieDataInDatabase(final List<MovieModel> moviesList,final Context context) {
        final MovieDatabase movieDatabase = MovieDatabase.getMovieDbInstance(context);
        final MovieModel[] movieModelArray = new MovieModel[moviesList.size()];
        for (int i = 0; i < movieModelArray.length; i++) {
            movieModelArray[i] = moviesList.get(i);
        }
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                for (MovieModel movieModel : moviesList) {
                    movieDatabase.getMovieDao().insertMovieData(movieModel);
                    Log.d("Data inserted","<==== " + movieModel.getOriginalTitle() + " =====>");
                }
            }
        });
    }
    public static void deleteAllPreviousMovies(Context context) {
        //In the repository
        final MovieDatabase movieDatabase = MovieDatabase.getMovieDbInstance(context);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                movieDatabase.getMovieDao().deleteAllMovies();
            }
        });
    }
    private static void clearAllLists() {
        //movieDetailModelFinal = null;
        trailerModelFinalList.clear();
        reviewModelFinalList.clear();
        castModelFinalList.clear();
    }
    public static void fetchMovieDetails(int movieId) {
        if(!trailerModelFinalList.isEmpty()
                && !(reviewModelFinalList.isEmpty())
                && (!castModelFinalList.isEmpty())) {
            clearAllLists();
        }
        MovieAPI movieAPI = MovieDataClient.getAPI();
        Call<MovieDetailModel> movieDetailModelCall = movieAPI.getSelectedMovie(movieId);
        final Call<TrailerContainer> trailerContainerCall = movieAPI.getMovieTrailers(movieId);
        final Call<ReviewContainer> reviewContainerCall = movieAPI.getMovieReviews(movieId);
        final Call<CastContainerModel> castContainerModelCall = movieAPI.getMovieCast(movieId);
        try {
            Response<MovieDetailModel> movieModelResponse= movieDetailModelCall.execute();
            Response<TrailerContainer> trailerContainerResponse = trailerContainerCall.execute();
            Response<ReviewContainer> reviewContainerResponse = reviewContainerCall.execute();
            Response<CastContainerModel> castContainerModelResponse = castContainerModelCall.execute();
            movieDetailModelFinal = movieModelResponse.body();
            Log.d("Details ====>","<====== Back path : =========== " + movieDetailModelFinal.getBackdropPath() + "==========>");
            trailerModelFinalList = trailerContainerResponse.body().getResults();
            reviewModelFinalList = reviewContainerResponse.body().getResults();
            castModelFinalList = castContainerModelResponse.body().getCast();
        } catch (IOException e) {
            Log.d("Exception in Details =>","" + e);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Object[] getAllMovieDetails() {
//        if(!trailerModelFinalList.isEmpty()
//                && !(reviewModelFinalList.isEmpty())
//                && (!castModelFinalList.isEmpty())) {
        Object[] objects = new Object[4];
        objects[0] = movieDetailModelFinal;
        objects[1] = trailerModelFinalList;
        objects[2] = reviewModelFinalList;
        objects[3] = castModelFinalList;
        return objects;
    }
    public static boolean addOrDeleteFavMovie(final Context context,final int movieId,
                                           final MovieDetailModel movieDetailModel) {
        final MovieDatabase movieDatabase = MovieDatabase.getMovieDbInstance(context);
        favouriteMovieModelList = movieDatabase.getFavMovieDao().getListOfAllFavMovies();
        boolean isAFavMovie = false;
        for (int i = 0; i < favouriteMovieModelList.size(); i++) {
            if(movieId == favouriteMovieModelList.get(i).getMovieId()) {
                isAFavMovie = true;
                break;
            }
        }
        if(isAFavMovie) {
            movieDatabase.getFavMovieDao().deleteParticularFavMovie(movieId);
            Log.d("Delete Fav Movie","<======== Yes: Deleted ============>");
        } else {
            FavouriteMovieModel favouriteMovieModel =
                    new FavouriteMovieModel(movieDetailModel.getId(),
                            movieDetailModel.getAdult(),
                            movieDetailModel.getOverview(),
                            movieDetailModel.getOriginalTitle(),
                            movieDetailModel.getReleaseDate(),
                            Integer.toString(movieDetailModel.getRuntime()),
                            movieDetailModel.getTagline(),
                            movieDetailModel.getVoteAverage(),
                            movieDetailModel.getOriginalLanguage(),
                            movieDetailModel.getPosterPath());
            movieDatabase.getFavMovieDao().insertFavMovie(favouriteMovieModel);
            Log.d("Added to Fav ==>","<=========== Yes ==============>");
        }
        return isAFavMovie;
    }
    public static LiveData<List<FavouriteMovieModel>> getAllFavMovies(final Context context) {
        return MovieDatabase.getMovieDbInstance(context).getFavMovieDao().getAllFavMovies();
    }
}
//TODO(1) Check the nullability and place appropriate views if it is null
//TODO(2) Add the Tap to add to favourites functionality and ALSO to delete that movie if the the button is again clicked
//TODO(3) Return only the list with all movies in the above function
//TODO(4) Create the intent on clicking on the video
//TODO(4.1) Create the intent to go to the review site
//TODO(5) Test it for most of the movies
//TODO(6) COntinue with the TODOs of the MainActivity