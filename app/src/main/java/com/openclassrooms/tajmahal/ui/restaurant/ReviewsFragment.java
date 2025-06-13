package com.openclassrooms.tajmahal.ui.restaurant;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.bumptech.glide.Glide;
import com.openclassrooms.tajmahal.R;
import com.openclassrooms.tajmahal.databinding.FragmentDetailsBinding;
import com.openclassrooms.tajmahal.databinding.FragmentReviewsBinding;
import com.openclassrooms.tajmahal.domain.model.Review;
import com.openclassrooms.tajmahal.ui.restaurant.adapter.ReviewAdapter;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReviewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class ReviewsFragment extends Fragment {
    private FragmentReviewsBinding binding;
    private ReviewsViewModel reviewsViewModel;
    private ReviewAdapter reviewAdapter;
    private String currentAvatarUrl;
    public static ReviewsFragment newInstance() {
        return new ReviewsFragment();
    }

    /**
     *
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reviewsViewModel = new ViewModelProvider(this).get(ReviewsViewModel.class);    }

    /**
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentReviewsBinding.inflate(inflater, container, false); // Binds the layout using view binding.
        return binding.getRoot();
    }

    /**
     * This method is called immediately after `onCreateView()`.
     * Use this method to perform final initialization once the fragment views have been inflated.
     *
     * @param view The View returned by `onCreateView()`.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI();
        setupRecyclerView();
        observeViewModel();
        setupImgSrcAvatar();
        setupBackButton();
        setupAddReviewButton();
    }

    /**
     * Sets up the UI-specific properties, such as system UI flags and status bar color.
     */
    private void setupUI() {
        Window window = requireActivity().getWindow();
    }

    /**
     * Sets up the RecyclerView for displaying reviews.
     */
    private void setupRecyclerView() {
        reviewAdapter = new ReviewAdapter(requireContext());
        binding.recyclerViewReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewReviews.setAdapter(reviewAdapter);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
        );

        Drawable customDivider = ContextCompat.getDrawable(requireContext(), R.drawable.custom_black_divider);
        if (customDivider != null) {
            itemDecoration.setDrawable(customDivider);
        }

        binding.recyclerViewReviews.addItemDecoration(itemDecoration);
    }

    /**
     * Observes changes in the ViewModel's LiveData and updates the RecyclerView accordingly.
     */
    private void observeViewModel() {
        reviewsViewModel.getReviews().observe(getViewLifecycleOwner(), reviewList -> {
            if (reviewList != null) {
                reviewAdapter.updateReviews(reviewList);
            }
        });
    }

    /**
     * Sets up the back button to navigate back to the previous fragment.
     */
    private void setupBackButton() {
        binding.backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    /**
     * Sets up the "Add Review" button to add a new review.
     */
    private void setupAddReviewButton() {
        binding.buttonAddReview.setOnClickListener(v -> addReview());
    }
    /**
     * Sets up the avatar image source using Glide to load an image from a URL.
     */
    private void setupImgSrcAvatar() {
        currentAvatarUrl = "https://xsgames.co/randomusers/assets/avatars/female/73.jpg";

        // Assurez-vous que le contexte n'est pas null.
        // Dans onViewCreated et après, getContext() ou requireContext() devrait être sûr.
        if (getContext() == null) {
            return; // Ou gérer l'erreur autrement
        }

        Glide.with(this)
                .load(currentAvatarUrl)
                .placeholder(R.drawable.main_avatar)
                .error(R.drawable.main_avatar)
                .into(binding.profilePicture);
    }


    /**
     * Adds a new review to the list of reviews.
     * recover d
     */
    private void addReview() {
        String username = binding.textViewReviewUsername.getText().toString();
        int rate = Integer.parseInt(binding.ratingBarNewReview.getProgress() + "");
        String comment = binding.editTextReviewComment.getText().toString();

        if(comment.isEmpty() || rate == 0){
            return;
        }


        Review review = new Review(username, currentAvatarUrl, comment, rate);
        reviewsViewModel.addReview(review);
        cleanInputs();
    }

    /**
     * Cleans the input fields after adding a new review.
     */
    private void cleanInputs() {
        binding.textViewReviewUsername.setText("");
        binding.editTextReviewComment.setText("");
        binding.ratingBarNewReview.setProgress(0);
    }
}