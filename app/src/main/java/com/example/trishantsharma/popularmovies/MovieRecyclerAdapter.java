package com.example.trishantsharma.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieRecyclerAdapter extends RecyclerView.Adapter<MovieRecyclerAdapter.MovieRecyclerAdapterViewHolder> {

    private Context context;
    private MovieGridOnClickHandler movieGridOnClickHandler;
    public interface MovieGridOnClickHandler {
        void onClickMovie(int positionClicked);
    }
    private ArrayList<String[]> movieTitleAndPosterList = new ArrayList<>();
    public MovieRecyclerAdapter(@NonNull Context context,
                                MovieGridOnClickHandler movieGridOnClickHandler) {
        this.context = context;
        this.movieGridOnClickHandler = movieGridOnClickHandler;
        //this.movieTitleAndPosterList.addAll(movieTitleAndPosterList);
    }
    private Uri buildPicassoImageLoadingUrl(String posterPath){
        Uri posterUri;
        final String baseURL = "http://image.tmdb.org/t/p/";
        final String posterSize = "w342";
        posterUri = Uri.parse(baseURL + posterSize + "/" + posterPath);
        return posterUri;
    }
    @NonNull
    @Override
    public MovieRecyclerAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(context)
                .inflate(R.layout.grid_item_layout,parent,false);
        view.setFocusable(true);
        return new MovieRecyclerAdapterViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MovieRecyclerAdapterViewHolder holder, int position) {
        //Add the first element of the array of the specified position of the ArrayList to the TxtVw
        if(movieTitleAndPosterList.get(position)[1]!=null){
            Picasso.get()
                    .load(buildPicassoImageLoadingUrl(movieTitleAndPosterList.get(position)[1]))
                    .into(holder.movieImageView);
        }
    }

    @Override
    public int getItemCount() {
        if(movieTitleAndPosterList == null) return 0;
        return movieTitleAndPosterList.size();
    }

    class MovieRecyclerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView movieImageView;
        MovieRecyclerAdapterViewHolder(View view){
            super(view);
            movieImageView = view.findViewById(R.id.movie_poster_iv);
            view.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            movieGridOnClickHandler.onClickMovie(getAdapterPosition());
        }
    }
    public void setMovieTitleAndPosterList(ArrayList<String[]> movieTitleAndPosterList){
        if(!this.movieTitleAndPosterList.isEmpty()) {
            this.movieTitleAndPosterList.clear();
        }
        this.movieTitleAndPosterList.addAll(movieTitleAndPosterList);
        notifyDataSetChanged();
    }
}
