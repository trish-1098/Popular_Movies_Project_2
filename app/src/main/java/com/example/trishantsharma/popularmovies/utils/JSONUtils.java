package com.example.trishantsharma.popularmovies.utils;

import com.example.trishantsharma.popularmovies.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONUtils {
    private static ArrayList<String[]> movieTitleAndPosterList = new ArrayList<>();
    private static final String RESULT_JSON_ARRAY_STRING = "results";
    private static final String ID_JSON_OBJECT_STRING = "id";

    private static final String POSTER_JSON_OBJECT_STRING = "poster_path";

    private static final String ISADULT_JSON_OBJECT_STRING = "adult";
    private static final String OVERVIEW_JSON_OBJECT_STRING = "overview";
    private static final String GENRE_JSON_OBJECT_STRING = "genres";
    private static final String MORE_DETAILS_JSON_OBJECT_STRING = "homepage";
    private static final String MOVIE_TITLE_JSON_OBJECT_STRING = "original_title";
    private static final String PRODUCTION_JSON_OBJECT_STRING = "production_companies";
    private static final String RELEASE_DATE_JSON_OBJECT_STRING = "release_date";
    private static final String RUNTIME_JSON_OBJECT_STRING = "runtime";
    private static final String TAGLINE_JSON_OBJECT_STRING = "tagline";
    private static final String AVG_RATING_JSON_OBJECT_STRING = "vote_average";
    private static final String LANGUAGE_JSON_OBJECT_STRING = "original_language";
    private static final String LOGO_PROD_COMPANY_JSON_OBJECT_STRING = "logo_path";
    private static final String BACKDROP_PATH_JSON_OBJECT_STRING = "backdrop_path";

    private static final String CAST_JSON_OBJECT_STRING = "cast";
    private static final String CHARACTER_JSON_OBJECT_STRING = "character";
    private static final String REAL_NAME_JSON_OBJECT_STRING = "name";
    private static final String CHARACTER_POSTER_JSON_OBJECT_STRING = "profile_path";

    public static ArrayList<String[]> parseMovieJSON(String jsonReceived){
        movieTitleAndPosterList.clear();
        try {
            JSONObject movieObject = new JSONObject(jsonReceived);
            JSONArray allMoviesArray = movieObject.getJSONArray(RESULT_JSON_ARRAY_STRING);
            for (int i = 0; i < 19; i++) {
                JSONObject particularMovie = allMoviesArray.optJSONObject(i);
                String movieId = Integer.toString(particularMovie.optInt(ID_JSON_OBJECT_STRING));
                String moviePoster = particularMovie.optString(POSTER_JSON_OBJECT_STRING);
                String[] singleMovieTitleAndPosterArray = {movieId,moviePoster};
                movieTitleAndPosterList.add(singleMovieTitleAndPosterArray);
            }
            return movieTitleAndPosterList;
        } catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
    public static Movie parseSelectedMovieAndCast(String selectedMovieJSON,String selectedMovieCastJSON){
        movieTitleAndPosterList.clear();
        Movie selectedMovieDetailsObject = new Movie();
        try {
            JSONObject selectedMovieJSONObject = new JSONObject(selectedMovieJSON);
            //Setting adult or not
            selectedMovieDetailsObject
                    .setAdult(selectedMovieJSONObject.optBoolean(ISADULT_JSON_OBJECT_STRING));
            //Setting Overview
            selectedMovieDetailsObject
                    .setOverviewDescription(selectedMovieJSONObject
                            .optString(OVERVIEW_JSON_OBJECT_STRING));
            //Setting Genres
            JSONArray genreArray = selectedMovieJSONObject.optJSONArray(GENRE_JSON_OBJECT_STRING);
            ArrayList<String> genreList = new ArrayList<>();
            for (int i = 0; i < genreArray.length(); i++) {
                JSONObject particularGenre = genreArray.optJSONObject(i);
                genreList.add(particularGenre.optString(REAL_NAME_JSON_OBJECT_STRING));
            }
            selectedMovieDetailsObject.setGenreOfMovie(genreList);
            //Setting More Details
            selectedMovieDetailsObject
                    .setMoreDetailsWebsite(selectedMovieJSONObject
                            .optString(MORE_DETAILS_JSON_OBJECT_STRING));
            //Setting Movie Title
            selectedMovieDetailsObject
                    .setTitleOfMovie(selectedMovieJSONObject
                            .optString(MOVIE_TITLE_JSON_OBJECT_STRING));
            //Setting the Production Companies
            JSONArray productionCompaniesArray = 
                    selectedMovieJSONObject.optJSONArray(PRODUCTION_JSON_OBJECT_STRING);
            ArrayList<String[]> reusableStringArray_ArrayList = new ArrayList<>();
            for(int j = 0; j < productionCompaniesArray.length(); j++) {
                JSONObject particularProductionCompany = productionCompaniesArray.optJSONObject(j);
                String[] productionCompanyNameAndLogo =
                        {particularProductionCompany.optString(REAL_NAME_JSON_OBJECT_STRING),
                                particularProductionCompany
                                        .optString(LOGO_PROD_COMPANY_JSON_OBJECT_STRING)};
                reusableStringArray_ArrayList.add(j,productionCompanyNameAndLogo);
            }
            selectedMovieDetailsObject.setProductionCompanies(reusableStringArray_ArrayList);
            //Setting Release Date
            selectedMovieDetailsObject
                    .setReleaseDate(selectedMovieJSONObject
                            .optString(RELEASE_DATE_JSON_OBJECT_STRING));
            //Setting Runtime
            selectedMovieDetailsObject
                    .setRuntimeOfMovie(Integer
                            .toString(selectedMovieJSONObject.optInt(RUNTIME_JSON_OBJECT_STRING)));
            //Setting Tagline
            selectedMovieDetailsObject
                    .setTagLineOfMovie(selectedMovieJSONObject
                            .optString(TAGLINE_JSON_OBJECT_STRING));
            //Setting Avg Rating
            selectedMovieDetailsObject
                    .setAvgRating(selectedMovieJSONObject.optDouble(AVG_RATING_JSON_OBJECT_STRING));
            //Setting Language
            selectedMovieDetailsObject
                    .setLanguageOfMovie(selectedMovieJSONObject
                            .optString(LANGUAGE_JSON_OBJECT_STRING));
            //Setting the poster path
            selectedMovieDetailsObject
                    .setPathTooPoster(selectedMovieJSONObject.optString(POSTER_JSON_OBJECT_STRING));
            //Setting the backdrop image path
            selectedMovieDetailsObject
                    .setPathToBackDropImage(selectedMovieJSONObject
                            .optString(BACKDROP_PATH_JSON_OBJECT_STRING));
            //Parsing Casts JSON
            JSONObject selectedMovieCastJSONObject = new JSONObject(selectedMovieCastJSON);
            JSONArray castArray = selectedMovieCastJSONObject.optJSONArray(CAST_JSON_OBJECT_STRING);
            reusableStringArray_ArrayList.clear();
            for(int z = 0; z < castArray.length(); z++) {
                JSONObject eachCharacter = castArray.optJSONObject(z);
                String[] singleCastDetails = 
                        {eachCharacter.optString(CHARACTER_JSON_OBJECT_STRING),
                                eachCharacter.optString(REAL_NAME_JSON_OBJECT_STRING),
                                eachCharacter.optString(CHARACTER_POSTER_JSON_OBJECT_STRING)};
                reusableStringArray_ArrayList.add(z,singleCastDetails);
            }
            selectedMovieDetailsObject.setCastNameAndImage(reusableStringArray_ArrayList);
            return selectedMovieDetailsObject;
        } catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
}
