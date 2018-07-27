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
import com.example.trishantsharma.popularmovies.models.TrailerModel;
import com.example.trishantsharma.popularmovies.networkdata.NetworkAndDatabaseUtils;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;


public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {
    private Context context;
    private ArrayList<TrailerModel> trailerArrayList;
    public interface OnTrailerClickListener {
        void onTrailerClick(int positionClicked);
    }
    private OnTrailerClickListener onTrailerClickListener;

    public TrailerAdapter(Context context,OnTrailerClickListener onTrailerClickListener) {
        this.context = context;
        this.onTrailerClickListener = onTrailerClickListener;
        trailerArrayList = new ArrayList<>();
    }
    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.trailer_item_layout,parent,false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        InputStream inputstream = context.getResources().openRawResource(R.raw.no_image_available);

        Bitmap bitmap = BitmapFactory.decodeStream(inputstream);

        Drawable drawable = new BitmapDrawable(context.getResources(),bitmap);
        Picasso.get()
                .load(NetworkAndDatabaseUtils
                        .buildUriForTrailerPicassoImage(trailerArrayList.get(position).getKey()))
                .error(drawable)
                .into(holder.trailerImageView);
        holder.trailerNameTextView.setText(trailerArrayList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if(trailerArrayList == null)
            return 0;
        return trailerArrayList.size();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder{
        final ImageView trailerImageView;
        final TextView trailerNameTextView;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            trailerImageView = itemView.findViewById(R.id.trailer_image_iv);
            trailerNameTextView = itemView.findViewById(R.id.trailer_name_tv);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTrailerClickListener.onTrailerClick(getAdapterPosition());
                }
            });
        }
    }
    public void setTrailerArrayList(ArrayList<TrailerModel> trailerArrayList) {
        this.trailerArrayList = trailerArrayList;
        notifyDataSetChanged();
    }
}
