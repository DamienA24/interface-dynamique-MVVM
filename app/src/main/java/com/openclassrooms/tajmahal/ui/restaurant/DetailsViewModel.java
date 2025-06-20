package com.openclassrooms.tajmahal.ui.restaurant;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.openclassrooms.tajmahal.R;
import com.openclassrooms.tajmahal.data.repository.RestaurantRepository;
import com.openclassrooms.tajmahal.domain.model.Restaurant;
import com.openclassrooms.tajmahal.domain.model.Review;

import javax.inject.Inject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * MainViewModel is responsible for preparing and managing the data for the {@link DetailsFragment}.
 * It communicates with the {@link RestaurantRepository} to fetch restaurant details and provides
 * utility methods related to the restaurant UI.
 *
 * This ViewModel is integrated with Hilt for dependency injection.
 */
@HiltViewModel
public class DetailsViewModel extends ViewModel {

    private final RestaurantRepository restaurantRepository;
    /**
     * LiveData object containing the review statistics.
     * This LiveData object is used to observe changes in the review statistics and update the UI accordingly.
     */
    private final MediatorLiveData<ReviewStatsUIModel> reviewStatsLiveData = new MediatorLiveData<>();

    /**
     * Constructor that Hilt will use to create an instance of MainViewModel.
     *
     * @param restaurantRepository The repository which will provide restaurant data.
     */
    @Inject
    public DetailsViewModel(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;

        LiveData<List<Review>> reviewsSource = restaurantRepository.getReviews();
        reviewStatsLiveData.addSource(reviewsSource, reviews -> {
            if (reviews != null) {
                reviewStatsLiveData.setValue(calculateReviewStats(reviews));
            } else {
                reviewStatsLiveData.setValue(new ReviewStatsUIModel(0f, 0, new HashMap<>(), 0));
            }
        });
    }

    /**
     * Fetches the details of the Taj Mahal restaurant.
     *
     * @return LiveData object containing the details of the Taj Mahal restaurant.
     */
    public LiveData<Restaurant> getTajMahalRestaurant() {
        return restaurantRepository.getRestaurant();
    }

    /**
     * Retrieves the review statistics LiveData object.
     * @return LiveData object containing the review statistics.
     */
    public LiveData<ReviewStatsUIModel> getReviewStats() {
        return reviewStatsLiveData;
    }

    /**
     * Retrieves the current day of the week in French.
     *
     * @return A string representing the current day of the week in French.
     */
    public String getCurrentDay(Context context) {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String dayString;

        switch (dayOfWeek) {
            case Calendar.MONDAY:
                dayString = context.getString(R.string.monday);
                break;
            case Calendar.TUESDAY:
                dayString = context.getString(R.string.tuesday);
                break;
            case Calendar.WEDNESDAY:
                dayString = context.getString(R.string.wednesday);
                break;
            case Calendar.THURSDAY:
                dayString = context.getString(R.string.thursday);
                break;
            case Calendar.FRIDAY:
                dayString = context.getString(R.string.friday);
                break;
            case Calendar.SATURDAY:
                dayString = context.getString(R.string.saturday);
                break;
            case Calendar.SUNDAY:
                dayString = context.getString(R.string.sunday);
                break;
            default:
                dayString = "";
        }
        return dayString;
    }

    /**
     * Calculates the review statistics based on the provided list of reviews.
     * @param reviews The list of reviews.
     * @return The calculated review statistics.
     */
    private ReviewStatsUIModel calculateReviewStats(List<Review> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return new ReviewStatsUIModel(0f, 0, new HashMap<>(), 0);
        }

        float totalRatingSum = 0f;
        int totalReviewsCount = reviews.size();
        Map<Integer, Integer> ratingCounts = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            ratingCounts.put(i, 0);
        }

        for (Review review : reviews) {
            totalRatingSum += review.getRate();
            int roundedRate = Math.max(1, Math.min(5, Math.round(review.getRate())));
            ratingCounts.put(roundedRate, ratingCounts.getOrDefault(roundedRate, 0) + 1);
        }

        float averageRating = (totalReviewsCount > 0) ? totalRatingSum / totalReviewsCount : 0f;

        return new ReviewStatsUIModel(averageRating, totalReviewsCount, ratingCounts, totalReviewsCount);
    }


    /**
     * UI model for review statistics.
     * Contains information about the average rating, total number of reviews,
     * rating counts for each star, and the size of the review list.
     * This model is used to display review statistics in the UI.
     */
    public static class ReviewStatsUIModel {
        public final float averageRating;
        public final int totalReviews;
        public final Map<Integer, Integer> ratingCounts;
        public final int reviewListSize;


        /**
         * Constructor for ReviewStatsUIModel.
         * @param averageRating The average rating of the reviews.
         * @param totalReviews The total number of reviews.
         * @param ratingCounts A map containing the count of reviews for each star rating.
         * @param reviewListSize The size of the review list.
         */
        public ReviewStatsUIModel(float averageRating, int totalReviews, Map<Integer, Integer> ratingCounts, int reviewListSize) {
            this.averageRating = averageRating;
            this.totalReviews = totalReviews;
            this.ratingCounts = ratingCounts;
            this.reviewListSize = reviewListSize;
        }

        /**
         * Calculates the percentage of reviews for a given star rating.
         * @param star The star rating.
         * @return The percentage of reviews for the given star rating.
         */
        public int getPercentageForStar(int star) {
            if (totalReviews == 0 || !ratingCounts.containsKey(star) || ratingCounts.get(star) == null) {
                return 0;
            }
            Integer count = ratingCounts.get(star);
            if (count == null) return 0;
            return (int) ((count / (float) totalReviews) * 100);
        }
    }

}
