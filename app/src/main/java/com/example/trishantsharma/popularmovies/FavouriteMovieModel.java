package com.example.trishantsharma.popularmovies;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;

@Entity(tableName = "fav_movies")
public class FavouriteMovieModel {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_as_inserted")
    private int idInDb;
    @ColumnInfo(name = "movie_id")
    private int movieId;
    @ColumnInfo(name = "viewer_rating")
    private boolean isAdult;
    @ColumnInfo(name = "movie_overview")
    private String overviewDescription;
    @ColumnInfo(name = "movie_title")
    private String titleOfMovie;
    @ColumnInfo(name = "release_date")
    private String releaseDate;
    @ColumnInfo(name = "runtime")
    private String runtimeOfMovie;
    @ColumnInfo(name = "tag_line")
    private String tagLineOfMovie;
    @ColumnInfo(name = "avg_rating")
    private double avgRating;
    @ColumnInfo(name = "language")
    private String languageOfMovie;
    @ColumnInfo(name = "poster_path")
    private String posterPath;
    @Ignore
    public FavouriteMovieModel() {
    }
    @Ignore
    public FavouriteMovieModel(String pathTooPoster, boolean isAdult, String overviewDescription,
                               ArrayList<String> genreOfMovie, String titleOfMovie, String releaseDate,
                               String runtimeOfMovie, String tagLineOfMovie, double avgRating,
                               String languageOfMovie) {
        this.isAdult = isAdult;
        this.overviewDescription = overviewDescription;
        this.titleOfMovie = titleOfMovie;
        this.releaseDate = releaseDate;
        this.runtimeOfMovie = runtimeOfMovie;
        this.tagLineOfMovie = tagLineOfMovie;
        this.avgRating = avgRating;
        this.languageOfMovie = languageOfMovie;
    }
    public FavouriteMovieModel(int movieId, boolean isAdult, String overviewDescription, String titleOfMovie,
                               String releaseDate, String runtimeOfMovie, String tagLineOfMovie,
                               double avgRating, String languageOfMovie,String posterPath) {
        this.movieId = movieId;
        this.isAdult = isAdult;
        this.overviewDescription = overviewDescription;
        this.titleOfMovie = titleOfMovie;
        this.releaseDate = releaseDate;
        this.runtimeOfMovie = runtimeOfMovie;
        this.tagLineOfMovie = tagLineOfMovie;
        this.avgRating = avgRating;
        this.languageOfMovie = languageOfMovie;
        this.posterPath = posterPath;
    }

    public int getIdInDb() {
        return idInDb;
    }

    public void setIdInDb(int idInDb) {
        this.idInDb = idInDb;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getLanguageOfMovie() {
        return languageOfMovie;
    }

    public void setLanguageOfMovie(String languageOfMovie) {
        this.languageOfMovie = languageOfMovie;
    }

    public String getTagLineOfMovie() {
        return tagLineOfMovie;
    }

    public void setTagLineOfMovie(String tagLineOfMovie) {
        this.tagLineOfMovie = tagLineOfMovie;
    }

    public boolean isAdult() {
        return isAdult;
    }

    public void setAdult(boolean adult) {
        isAdult = adult;
    }

    public String getOverviewDescription() {
        return overviewDescription;
    }

    public void setOverviewDescription(String overviewDescription) {
        this.overviewDescription = overviewDescription;
    }

    public String getTitleOfMovie() {
        return titleOfMovie;
    }

    public void setTitleOfMovie(String titleOfMovie) {
        this.titleOfMovie = titleOfMovie;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getRuntimeOfMovie() {
        return runtimeOfMovie;
    }

    public void setRuntimeOfMovie(String runtimeOfMovie) {
        this.runtimeOfMovie = runtimeOfMovie;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
}
