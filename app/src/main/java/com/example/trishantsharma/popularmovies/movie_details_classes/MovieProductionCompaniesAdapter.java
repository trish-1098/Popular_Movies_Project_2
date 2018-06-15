package com.example.trishantsharma.popularmovies.movie_details_classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.trishantsharma.popularmovies.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class MovieProductionCompaniesAdapter extends
        RecyclerView.Adapter<MovieProductionCompaniesAdapter.MovieProductionCompaniesViewHolder> {

    private Context context;
    private ArrayList<String[]> finalProductionCompaniesList;
    public MovieProductionCompaniesAdapter(Context context) {
        this.context = context;
        finalProductionCompaniesList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MovieProductionCompaniesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(context)
                .inflate(R.layout.prod_company_item_layout,parent,false);
        //view.setFocusable(true);
        return new MovieProductionCompaniesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieProductionCompaniesViewHolder holder, int position) {
        holder.productionCompanyNameTextView.setText(finalProductionCompaniesList.get(position)[0]);
        final AtomicBoolean imageLoaded = new AtomicBoolean();
        Picasso.get()
                .load(buildUriForPicassoCastImage(finalProductionCompaniesList.get(position)[1]))
                .into(holder.productionCompanyImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        imageLoaded.set(true);
                    }

                    @Override
                    public void onError(Exception e) {
                        imageLoaded.set(false);
                    }
                });
        if(!imageLoaded.get()) {
            InputStream inputstream = context.getResources().openRawResource(R.raw.no_image_available);

            Bitmap bitmap = BitmapFactory.decodeStream(inputstream);

            Drawable drawable = new BitmapDrawable(context.getResources(),bitmap);
            holder.productionCompanyImageView.setImageDrawable(drawable);
        }
    }

    @Override
    public int getItemCount() {
        if(finalProductionCompaniesList == null) return 0;
        return finalProductionCompaniesList.size();
    }

    public class MovieProductionCompaniesViewHolder extends RecyclerView.ViewHolder {

        final ImageView productionCompanyImageView;
        final TextView productionCompanyNameTextView;
        public MovieProductionCompaniesViewHolder(View itemView) {
            super(itemView);
            productionCompanyImageView =itemView.findViewById(R.id.prod_comp_iv);
            productionCompanyNameTextView = itemView.findViewById(R.id.company_title_tv);
        }
    }
    public void setDataToArrayList(ArrayList<String[]> receivedProductionCompaniesList) {
        finalProductionCompaniesList.addAll(receivedProductionCompaniesList);
        notifyDataSetChanged();
    }
    private Uri buildUriForPicassoCastImage(String pathToImage) {
        Uri finalUri;
        final String baseUri = "http://image.tmdb.org/t/p/";
        final String posterSize = "w342";
        finalUri = Uri.parse(baseUri + posterSize + "/" + pathToImage);
        return finalUri;
    }
}
