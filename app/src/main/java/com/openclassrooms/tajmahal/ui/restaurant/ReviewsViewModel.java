package com.openclassrooms.tajmahal.ui.restaurant;

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
}
