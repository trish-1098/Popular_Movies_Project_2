package com.example.trishantsharma.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.trishantsharma.popularmovies.models.FavouriteMovieModel;

import java.util.List;

@Dao
public interface FavMovieDao {
    @Query("SELECT * FROM fav_movies")
    LiveData<List<FavouriteMovieModel>> getAllFavMovies();
    @Query("SELECT * FROM fav_movies where movie_id = :movieId")
    FavouriteMovieModel getSelectedFavMovie(int movieId);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavMovie(FavouriteMovieModel favouriteMovieModel);
    @Query("DELETE FROM fav_movies where movie_id = :movieId ")
    void deleteParticularFavMovie(int movieId);
    @Query("SELECT * FROM fav_movies")
    List<FavouriteMovieModel> getListOfAllFavMovies();
}
