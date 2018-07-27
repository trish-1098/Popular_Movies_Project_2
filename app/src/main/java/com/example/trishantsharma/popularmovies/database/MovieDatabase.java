package com.example.trishantsharma.popularmovies.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;

import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.util.Log;

import com.example.trishantsharma.popularmovies.models.FavouriteMovieModel;
import com.example.trishantsharma.popularmovies.models.MovieModel;

@Database(entities = {MovieModel.class,FavouriteMovieModel.class},version = 1,exportSchema = false)
@TypeConverters({GenreConverter.class})
public abstract class MovieDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "movie_db";
    private static final Object LOCK = new Object();
    private static MovieDatabase movieDbInstance;

    public static MovieDatabase getMovieDbInstance(Context context) {
        if(movieDbInstance == null) {
            synchronized (LOCK) {
                movieDbInstance = Room.databaseBuilder(context.getApplicationContext(),
                        MovieDatabase.class,DATABASE_NAME).build();
            }
        }
        return movieDbInstance;
    }
    public abstract FavMovieDao getFavMovieDao();
    public abstract MovieDao getMovieDao();
}
