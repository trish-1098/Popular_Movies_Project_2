package com.example.trishantsharma.popularmovies;

import java.util.ArrayList;

public class Movie {
    private String pathTooPoster; //1
    private boolean isAdult; //1
    private String overviewDescription; //1
    private ArrayList<String> genreOfMovie; //1
    private String moreDetailsWebsite; //1
    private String titleOfMovie; //1
    private ArrayList<String[]> productionCompanies = new ArrayList<>(); //1
    private String releaseDate; //1
    private String runtimeOfMovie; //1
    private String tagLineOfMovie; //1
    private double avgRating; //1
    private String languageOfMovie;
    private String pathToBackDropImage;
    private ArrayList<String[]> castNameAndImage = new ArrayList<>(); //2

    public String getPathToBackDropImage() {
        return pathToBackDropImage;
    }

    public void setPathToBackDropImage(String pathToBackDropImage) {
        this.pathToBackDropImage = pathToBackDropImage;
    }

    public String getLanguageOfMovie() {
        return languageOfMovie;
    }

    public void setLanguageOfMovie(String languageOfMovie) {
        this.languageOfMovie = languageOfMovie;
    }

    public ArrayList<String> getGenreOfMovie() {
        return genreOfMovie;
    }

    public void setGenreOfMovie(ArrayList<String> genreOfMovie) {
        this.genreOfMovie = genreOfMovie;
    }
    public ArrayList<String[]> getCastNameAndImage() {
        return castNameAndImage;
    }

    public void setCastNameAndImage(ArrayList<String[]> castNameAndImage) {
        this.castNameAndImage = castNameAndImage;
    }

    public String getTagLineOfMovie() {
        return tagLineOfMovie;
    }

    public void setTagLineOfMovie(String tagLineOfMovie) {
        this.tagLineOfMovie = tagLineOfMovie;
    }

    public String getPathTooPoster() {
        return pathTooPoster;
    }

    public void setPathTooPoster(String pathTooPoster) {
        this.pathTooPoster = pathTooPoster;
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

    public String getMoreDetailsWebsite() {
        return moreDetailsWebsite;
    }

    public void setMoreDetailsWebsite(String moreDetailsWebsite) {
        this.moreDetailsWebsite = moreDetailsWebsite;
    }

    public String getTitleOfMovie() {
        return titleOfMovie;
    }

    public void setTitleOfMovie(String titleOfMovie) {
        this.titleOfMovie = titleOfMovie;
    }

    public ArrayList<String[]> getProductionCompanies() {
        return productionCompanies;
    }

    public void setProductionCompanies(ArrayList<String[]> productionCompanies) {
        this.productionCompanies.addAll(productionCompanies);
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
}
