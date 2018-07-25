package com.example.trishantsharma.popularmovies.containers;

import com.example.trishantsharma.popularmovies.models.TrailerModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrailerContainer {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<TrailerModel> results = null;

    public TrailerContainer(Integer id, List<TrailerModel> results) {
        this.id = id;
        this.results = results;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<TrailerModel> getResults() {
        return results;
    }

    public void setResults(List<TrailerModel> results) {
        this.results = results;
    }

}
