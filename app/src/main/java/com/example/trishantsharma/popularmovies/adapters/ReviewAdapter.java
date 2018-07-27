package com.example.trishantsharma.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.trishantsharma.popularmovies.R;
import com.example.trishantsharma.popularmovies.models.ReviewModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private Context context;
    private ArrayList<ReviewModel> reviewsArrayList;
    public interface OnReviewClickListener {
        void onReviewClick(int positionClicked);
    }
    private OnReviewClickListener onReviewClickListener;
    public ReviewAdapter(Context context,OnReviewClickListener onReviewClickListener) {
        this.context = context;
        this.onReviewClickListener = onReviewClickListener;
        reviewsArrayList = new ArrayList<>();
    }
    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.review_item_layout,parent,false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        holder.reviewerNameTextView.setText(reviewsArrayList.get(position).getAuthor());
        holder.reviewBodyTextView.setText(reviewsArrayList.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        if(reviewsArrayList == null)
            return 0;
        return reviewsArrayList.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder{
        final TextView reviewerNameTextView;
        final TextView reviewBodyTextView;
        public ReviewViewHolder(View itemView) {
            super(itemView);
            reviewerNameTextView = itemView.findViewById(R.id.reviewer_name_tv);
            reviewBodyTextView = itemView.findViewById(R.id.review_body_tv);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onReviewClickListener.onReviewClick(getAdapterPosition());
                }
            });
        }
    }
    public void setReviewsArrayList(ArrayList<ReviewModel> reviewsArrayList) {
        this.reviewsArrayList = reviewsArrayList;
        notifyDataSetChanged();
    }
}
//TODO Create the functionality to get the link from the description and then activate the more text link