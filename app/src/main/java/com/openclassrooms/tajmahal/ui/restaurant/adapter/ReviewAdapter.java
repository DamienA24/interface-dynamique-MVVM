package com.openclassrooms.tajmahal.ui.restaurant.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.openclassrooms.tajmahal.R;
import com.openclassrooms.tajmahal.domain.model.Review;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying a list of reviews in a RecyclerView.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private final List<Review> reviewsList;
    private final Context context;

    /**
     * Constructs a new ReviewAdapter with the provided context.
     *
     * @param context The context in which the adapter is used.
     */
    public ReviewAdapter(Context context) {
        this.context = context;
        this.reviewsList = new ArrayList<>();
    }

    /**
     * Updates the list of reviews displayed by the adapter.
     *
     * @param newReviewList The new list of reviews to be displayed.
     *
     */
    public void updateReviews(List<Review> newReviewList) {
        this.reviewsList.clear();
        if (newReviewList != null) {
            this.reviewsList.addAll(newReviewList);
        }
        notifyDataSetChanged();
    }

    /**
     * Creates a new ViewHolder for the RecyclerView.
     *
     * @param parent The parent ViewGroup into which the new View will be added.
     * @param viewType The view type of the new View.
     * @return A new ReviewViewHolder.
     */
    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(itemView);
    }

    /**
     * Binds the data to the ViewHolder at the specified position.
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review currentReview = reviewsList.get(position);
        holder.bind(currentReview, context);
    }

    /**
     * 
     * @return The total number of items in the data set held by the adapter.
     */
    @Override
    public int getItemCount() {
        return reviewsList == null ? 0 : reviewsList.size();
    }

    /**
     * ViewHolder for displaying a single review item in the RecyclerView.
     */
    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        private final ImageView reviewerProfileImage;
        private final TextView reviewerName;
        private final RatingBar reviewerRatingBar;
        private final TextView reviewerComment;

        /**
         * Constructs a new ReviewViewHolder.
         *
         * @param itemView The View representing a single review item.
         */
        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            reviewerProfileImage = itemView.findViewById(R.id.reviewerProfileImage);
            reviewerName = itemView.findViewById(R.id.reviewerName);
            reviewerRatingBar = itemView.findViewById(R.id.reviewerRatingBar);
            reviewerComment = itemView.findViewById(R.id.reviewerComment);
        }

        /**
         * Binds the review data to the ViewHolder's views.
         *
         * @param review The review data to be displayed.
         * @param context The context in which the adapter is used.
         */
        public void bind(Review review, Context context) {
            reviewerName.setText(review.getUsername());
            reviewerRatingBar.setRating(review.getRate());
            reviewerComment.setText(review.getComment());


            String imageUrl = review.getPicture();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Log.d("ReviewAdapter", "Image Name: " + imageUrl);
                Glide.with(context)
                        .load(imageUrl)
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.default_avatar)
                                .error(R.drawable.default_avatar)
                        )
                        .into(reviewerProfileImage);
            } else {
                reviewerProfileImage.setImageResource(R.drawable.default_avatar);
            }
        }
    }
}