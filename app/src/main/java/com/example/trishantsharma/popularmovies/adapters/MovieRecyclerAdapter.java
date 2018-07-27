package com.example.trishantsharma.popularmovies.adapters;

import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.trishantsharma.popularmovies.R;
import com.example.trishantsharma.popularmovies.models.MovieModel;
import com.example.trishantsharma.popularmovies.networkdata.NetworkAndDatabaseUtils;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;

public class MovieRecyclerAdapter extends PagedListAdapter<MovieModel,MovieRecyclerAdapter.MovieRecyclerAdapterViewHolder> {

    private Context context;
    private MovieGridOnClickHandler movieGridOnClickHandler;
    public interface MovieGridOnClickHandler {
        void onClickMovie(int positionClicked);
    }
    private ArrayList<String[]> movieTitleAndPosterList = new ArrayList<>();

    public MovieRecyclerAdapter() {
        super(DIFF_CALLBACK);
    }
    private static final DiffUtil.ItemCallback<MovieModel> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<MovieModel>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull MovieModel oldUser, @NonNull MovieModel newUser) {
                    return oldUser.getId().equals(newUser.getId());
                }
                @Override
                public boolean areContentsTheSame(
                        @NonNull MovieModel oldUser, @NonNull MovieModel newUser) {
                    return oldUser.equals(newUser);
                }
            };
    public void setContextAndMovieGridListener(Context context,
                                               MovieGridOnClickHandler movieGridOnClickHandler) {
        this.context = context;
        this.movieGridOnClickHandler = movieGridOnClickHandler;
    }

    @Override
    public void submitList(PagedList<MovieModel> pagedList) {
        super.submitList(pagedList);
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
        MovieModel movieModel = getItem(position);
        //Add the first element of the array of the specified position of the ArrayList to the TxtVw
        if(movieModel != null){
            Picasso.get()
                    .load(NetworkAndDatabaseUtils
                            .buildUriForPicassoImage(movieModel.getPosterPath()))
                    .into(holder.movieImageView);
        } else {
            InputStream inputstream = context.getResources().openRawResource(R.raw.no_image_available);

            Bitmap bitmap = BitmapFactory.decodeStream(inputstream);

            Drawable drawable = new BitmapDrawable(context.getResources(),bitmap);
            holder.movieImageView.setImageDrawable(drawable);
        }
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
}
