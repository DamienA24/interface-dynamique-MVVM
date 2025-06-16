package com.openclassrooms.tajmahal.ui.restaurant;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.openclassrooms.tajmahal.data.repository.RestaurantRepository;
import com.openclassrooms.tajmahal.domain.model.Review;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * MainViewModel is responsible for preparing and managing the data for the {@link ReviewsFragment}.
 * It communicates with the {@link RestaurantRepository} to fetch restaurant details and provides
 */

@HiltViewModel
public class ReviewsViewModel extends ViewModel {
    private final RestaurantRepository restaurantRepository;

    @Inject
    public ReviewsViewModel(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    /**
     * Fetches the reviews of the Taj Mahal restaurant.
     *
     * @return LiveData object containing the list of reviews of the Taj Mahal restaurant.
     *
     */
    public LiveData<List<Review>> getReviews() {
        return restaurantRepository.getReviews();
    }

    /**
     * Adds a new review to the list of reviews.
     * @param username The username of the reviewer.
     * @param avatarUrl The URL of the reviewer's avatar.
     * @param rate The rating given by the reviewer.
     * @param comment The comment given by the reviewer.
     */
    public boolean addReview(String username, String avatarUrl, int rate, String comment) {
        if (comment == null || comment.isEmpty()) {
            Log.d("ReviewsViewModel", "Comment cannot be empty.");
            return false;
        }
        if (rate == 0) {
            Log.d("ReviewsViewModel", "Rating cannot be 0.");
            return false;
        }

        Review newReview = new Review(username, avatarUrl, comment, rate);
        try {
            restaurantRepository.addReview(newReview);
            Log.d("ReviewsViewModel", "Review added successfully for user: " + username);
            return true;
        } catch (Exception e) {
            Log.e("ReviewsViewModel", "Error adding review to repository for user: " + username, e);
            return false;
        }
    }
}
