package com.example.trishantsharma.popularmovies.networkdata;

import com.example.trishantsharma.popularmovies.containers.CastContainerModel;
import com.example.trishantsharma.popularmovies.containers.MovieContainerModel;
import com.example.trishantsharma.popularmovies.containers.ReviewContainer;
import com.example.trishantsharma.popularmovies.containers.TrailerContainer;
import com.example.trishantsharma.popularmovies.models.MovieDetailModel;
import com.example.trishantsharma.popularmovies.utils.ConstantUtils;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieAPI {
    @GET("{sortType}"+ ConstantUtils.apiKeyString + ConstantUtils.myApiKey)
    Call<MovieContainerModel> getMoviesBySortOrder(@Path("sortType") String sortType,
                                                   @Query("page") int pageNumber);
    @GET("{movieId}" + ConstantUtils.apiKeyString + ConstantUtils.myApiKey)
    Call<MovieDetailModel> getSelectedMovie(@Path("movieId") int movieId);
    @GET("{movieId}/casts" + ConstantUtils.apiKeyString + ConstantUtils.myApiKey)
    Call<CastContainerModel> getMovieCast(@Path("movieId") int movieId);
    @GET("{movieId}/videos" + ConstantUtils.apiKeyString + ConstantUtils.myApiKey)
    Call<TrailerContainer> getMovieTrailers(@Path("movieId") int movieId);
    @GET("{movieId}/reviews" + ConstantUtils.apiKeyString + ConstantUtils.myApiKey)
    Call<ReviewContainer> getMovieReviews(@Path("movieId") int movieId);
}
