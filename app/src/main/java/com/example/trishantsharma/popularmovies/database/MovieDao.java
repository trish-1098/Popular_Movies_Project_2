package com.example.trishantsharma.popularmovies.database;

import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.trishantsharma.popularmovies.models.MovieModel;


@Dao
public interface MovieDao {
    @Query("DELETE FROM main_movie")
    void deleteAllMovies();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovieData(MovieModel movieModel);
    @Query("SELECT * from main_movie")
    DataSource.Factory<Integer,MovieModel> getAllMovies();
}
