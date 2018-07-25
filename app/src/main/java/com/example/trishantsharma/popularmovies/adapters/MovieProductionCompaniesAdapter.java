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
import com.example.trishantsharma.popularmovies.models.ProductionCompanyModel;
import com.example.trishantsharma.popularmovies.networkdata.NetworkAndDatabaseUtils;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;

public class MovieProductionCompaniesAdapter extends
        RecyclerView.Adapter<MovieProductionCompaniesAdapter.MovieProductionCompaniesViewHolder> {

    private Context context;
    private ArrayList<ProductionCompanyModel> finalProductionCompaniesList;
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
        holder.productionCompanyNameTextView.setText(finalProductionCompaniesList.get(position).getName());
        InputStream inputstream = context.getResources().openRawResource(R.raw.no_image_available);

        Bitmap bitmap = BitmapFactory.decodeStream(inputstream);

        Drawable drawable = new BitmapDrawable(context.getResources(),bitmap);
        Picasso.get()
                .load(NetworkAndDatabaseUtils
                        .buildUriForPicassoImage(finalProductionCompaniesList.get(position).getLogoPath()))
                .error(drawable)
                .into(holder.productionCompanyImageView);
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
    public void setDataToArrayList(ArrayList<ProductionCompanyModel> receivedProductionCompaniesList) {
        finalProductionCompaniesList.addAll(receivedProductionCompaniesList);
        notifyDataSetChanged();
    }
}
