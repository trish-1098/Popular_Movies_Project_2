package com.example.trishantsharma.popularmovies.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.trishantsharma.popularmovies.FavouriteMovieModel;
import com.example.trishantsharma.popularmovies.R;
import com.example.trishantsharma.popularmovies.networkdata.NetworkAndDatabaseUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FavouriteMovieAdapter extends RecyclerView.Adapter<FavouriteMovieAdapter.FavouriteMovieViewHolder> {
    private Context context;
    private List<FavouriteMovieModel> favouriteMovieModelList;
    public interface FavMovieClickListener {
        void onFavMovieClicked(int positionClicked);
    }
    private FavMovieClickListener favMovieClickListener;
    public FavouriteMovieAdapter(Context context,FavMovieClickListener favMovieClickListener) {
        this.context = context;
        favouriteMovieModelList = new ArrayList<>();
        this.favMovieClickListener = favMovieClickListener;
    }
    @NonNull
    @Override
    public FavouriteMovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_layout,parent,false);
        return new FavouriteMovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FavouriteMovieViewHolder holder, final int position) {
        Picasso.get()
                .load(NetworkAndDatabaseUtils
                        .buildUriForPicassoImage(favouriteMovieModelList
                                .get(position).getPosterPath())).networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.moviePosterView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                InputStream inputstream = context.getResources().openRawResource(R.raw.no_image_available);

                Bitmap bitmap = BitmapFactory.decodeStream(inputstream);

                Drawable drawable = new BitmapDrawable(context.getResources(),bitmap);
                Picasso.get()
                        .load(NetworkAndDatabaseUtils
                                .buildUriForPicassoImage(favouriteMovieModelList
                                        .get(holder.getAdapterPosition()).getPosterPath())).error(drawable);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(favouriteMovieModelList == null)
            return 0;
        return favouriteMovieModelList.size();
    }

    public class FavouriteMovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView moviePosterView;
        public FavouriteMovieViewHolder(View itemView) {
            super(itemView);
            moviePosterView = itemView.findViewById(R.id.movie_poster_iv);
        }

        @Override
        public void onClick(View v) {
            favMovieClickListener.onFavMovieClicked(getAdapterPosition());
        }
    }
    public void setDataToFavList(List<FavouriteMovieModel> favList) {
        this.favouriteMovieModelList = favList;
        notifyDataSetChanged();
    }
}
