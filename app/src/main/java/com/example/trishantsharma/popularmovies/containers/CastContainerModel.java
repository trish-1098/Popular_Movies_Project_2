package com.example.trishantsharma.popularmovies.containers;

import com.example.trishantsharma.popularmovies.models.CastModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CastContainerModel {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("cast")
    @Expose
    private List<CastModel> cast = null;

    public CastContainerModel(Integer id, List<CastModel> cast) {
        this.id = id;
        this.cast = cast;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<CastModel> getCast() {
        return cast;
    }

    public void setCast(List<CastModel> cast) {
        this.cast = cast;
    }
}
