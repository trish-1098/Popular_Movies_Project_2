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
import android.widget.TextView;

import com.example.trishantsharma.popularmovies.R;
import com.example.trishantsharma.popularmovies.models.CastModel;
import com.example.trishantsharma.popularmovies.networkdata.NetworkAndDatabaseUtils;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;

public class MovieCastAdapter extends RecyclerView.Adapter<MovieCastAdapter.MovieCastViewHolder>{

    private Context context;
    private ArrayList<CastModel> finalCastAndImageList;

    public MovieCastAdapter(Context context) {
        this.context = context;
        finalCastAndImageList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MovieCastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(context)
                .inflate(R.layout.cast_list_item_layout,parent,false);
        //view.setFocusable(true);
        return new MovieCastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieCastViewHolder holder, int position) {
        holder.castCharacterNameTextView.setText(finalCastAndImageList.get(position).getCharacter());
        holder.castRealNameTextView.setText(finalCastAndImageList.get(position).getName());
        InputStream inputstream = context.getResources().openRawResource(R.raw.no_image_available);

        Bitmap bitmap = BitmapFactory.decodeStream(inputstream);

        Drawable drawable = new BitmapDrawable(context.getResources(),bitmap);
        Picasso.get()
                .load(NetworkAndDatabaseUtils
                        .buildUriForPicassoImage(finalCastAndImageList
                                .get(position).getProfilePath()))
                .error(drawable)
                .into(holder.castImageView);
    }

    @Override
    public int getItemCount() {
        if(finalCastAndImageList == null) return 0;
        return finalCastAndImageList.size();
    }

    public class MovieCastViewHolder extends RecyclerView.ViewHolder {

        final ImageView castImageView;
        final TextView castCharacterNameTextView;
        final TextView castRealNameTextView;
        public MovieCastViewHolder(View itemView) {
            super(itemView);
            castImageView = itemView.findViewById(R.id.cast_img_iv);
            castCharacterNameTextView = itemView.findViewById(R.id.cast_character_name_tv);
            castRealNameTextView = itemView.findViewById(R.id.cast_real_name_tv);
        }
    }
    public void setDataToArrayList(ArrayList<CastModel> receivedCastAndImageArrayList) {
        this.finalCastAndImageList = receivedCastAndImageArrayList;
        notifyDataSetChanged();
    }
}
