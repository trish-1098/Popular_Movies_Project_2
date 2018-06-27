package com.example.trishantsharma.popularmovies.utils;

import com.example.trishantsharma.popularmovies.Movie;

import java.util.ArrayList;

import retrofit2.http.GET;

public interface MovieObjectFetcher {
    @GET("/popular"+ConstantUtils.apiKeyString+ConstantUtils.myApiKey)
    public ArrayList<Movie> getPopularMovies();

    @GET("/top_rated"+ConstantUtils.apiKeyString+ConstantUtils.myApiKey)
    public ArrayList<Movie> getTopRatedMovies();

    @GET("/{movieId}"+ConstantUtils.apiKeyString+ ConstantUtils.myApiKey)
    public Movie getParticularSelectedMovie();
}
