package com.openclassrooms.tajmahal;

import android.util.Log;

import com.openclassrooms.tajmahal.data.repository.RestaurantRepository;
import com.openclassrooms.tajmahal.domain.model.Review;
import com.openclassrooms.tajmahal.ui.restaurant.ReviewsViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link ReviewsViewModel} class, specifically focusing on the addReview method.
 * These tests verify the behavior of review validation, interaction with the repository,
 * and logging for different input scenarios.
 */
// @RunWith(MockitoJUnitRunner.class) // Alternative way to initialize mocks
public class ReviewsViewModelTest {

    @Mock
    private RestaurantRepository mockRestaurantRepository;

    @Captor
    private ArgumentCaptor<Review> reviewArgumentCaptor;

    private ReviewsViewModel reviewsViewModel;

    // To mock static calls to android.util.Log
    private MockedStatic<Log> mockedLog;

    /**
     * Sets up the test environment before each test.
     * Initializes mocks, creates the ViewModel instance, and sets up static mocking for Log.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Initializes fields annotated with @Mock

        // Initialize static mock for Log. This is necessary to verify Log calls.
        mockedLog = Mockito.mockStatic(Log.class);

        // Create an instance of the ViewModel with the mocked repository
        reviewsViewModel = new ReviewsViewModel(mockRestaurantRepository);
    }

    /**
     * Cleans up the test environment after each test.
     * Closes the static Log mock to prevent interference between tests.
     */
    @After
    public void tearDown() {
        // It's crucial to close the static mock to avoid test pollution
        if (mockedLog != null) {
            mockedLog.close();
        }
    }

    /**
     * Tests the {@link ReviewsViewModel#addReview(String, String, int, String)} method
     * with valid input.
     * Verifies that the repository's addReview method is called, the correct Review object is passed,
     * a success log is made, and the method returns true.
     */
    @Test
    public void addReview_withValidInput_shouldCallRepositoryAndReturnTrue() {
        // Arrange
        String username = "TestUser";
        String avatarUrl = "test_avatar.jpg";
        String comment = "This is a great comment!";
        int rate = 5;

        // Act
        boolean result = reviewsViewModel.addReview(username, avatarUrl, rate, comment);

        // Assert
        assertTrue("addReview should return true for valid input", result);

        // Verify that restaurantRepository.addReview was called exactly once and capture the argument
        verify(mockRestaurantRepository, times(1)).addReview(reviewArgumentCaptor.capture());

        // Check the content of the captured Review object
        Review capturedReview = reviewArgumentCaptor.getValue();
        assertEquals("Username should match", username, capturedReview.getUsername());
        assertEquals("Avatar URL should match", avatarUrl, capturedReview.getPicture());
        assertEquals("Comment should match", comment, capturedReview.getComment());
        assertEquals("Rate should match", rate, capturedReview.getRate());

        // Verify that the success log was made
        mockedLog.verify(() -> Log.d("ReviewsViewModel", "Review added successfully for user: " + username));

        // Ensure no validation error logs were made
        mockedLog.verify(() -> Log.d("ReviewsViewModel", "Comment cannot be empty."), never());
        mockedLog.verify(() -> Log.d("ReviewsViewModel", "Rating cannot be 0."), never());
        // Ensure no repository error log was made
        mockedLog.verify(() -> Log.e(eq("ReviewsViewModel"), anyString(), any(Exception.class)), never());
    }

    /**
     * Tests the {@link ReviewsViewModel#addReview(String, String, int, String)} method
     * with an empty comment.
     * Verifies that the repository's addReview method is never called, an appropriate log is made,
     * and the method returns false.
     */
    @Test
    public void addReview_withEmptyComment_shouldLogAndReturnFalse() {
        // Arrange
        String username = "TestUser";
        String avatarUrl = "test_avatar.jpg";
        String comment = ""; // Empty comment
        int rate = 4;

        // Act
        boolean result = reviewsViewModel.addReview(username, avatarUrl, rate, comment);

        // Assert
        assertFalse("addReview should return false for empty comment", result);

        // Verify that restaurantRepository.addReview was never called
        verify(mockRestaurantRepository, never()).addReview(any(Review.class));

        // Verify that the "empty comment" log was made
        mockedLog.verify(() -> Log.d("ReviewsViewModel", "Comment cannot be empty."));

        // Ensure other irrelevant logs were not made
        mockedLog.verify(() -> Log.d("ReviewsViewModel", "Rating cannot be 0."), never());
        mockedLog.verify(() -> Log.d(eq("ReviewsViewModel"), startsWith("Review added successfully")), never());
    }

    /**
     * Tests the {@link ReviewsViewModel#addReview(String, String, int, String)} method
     * with a null comment.
     * Verifies that the repository's addReview method is never called, an appropriate log is made
     * (same as empty comment in the current implementation), and the method returns false.
     */
    @Test
    public void addReview_withNullComment_shouldLogAndReturnFalse() {
        // Arrange
        String username = "TestUser";
        String avatarUrl = "test_avatar.jpg";
        String comment = null; // Null comment
        int rate = 4;

        // Act
        boolean result = reviewsViewModel.addReview(username, avatarUrl, rate, comment);

        // Assert
        assertFalse("addReview should return false for null comment", result);

        // Verify that restaurantRepository.addReview was never called
        verify(mockRestaurantRepository, never()).addReview(any(Review.class));

        // Verify that the "empty comment" log was made (current SUT logic handles null and empty the same way for logging)
        mockedLog.verify(() -> Log.d("ReviewsViewModel", "Comment cannot be empty."));
    }

    /**
     * Tests the {@link ReviewsViewModel#addReview(String, String, int, String)} method
     * with a zero rating.
     * Verifies that the repository's addReview method is never called, an appropriate log is made,
     * and the method returns false.
     */
    @Test
    public void addReview_withZeroRate_shouldLogAndReturnFalse() {
        // Arrange
        String username = "TestUser";
        String avatarUrl = "test_avatar.jpg";
        String comment = "Valid comment";
        int rate = 0; // Zero rating

        // Act
        boolean result = reviewsViewModel.addReview(username, avatarUrl, rate, comment);

        // Assert
        assertFalse("addReview should return false for zero rate", result);

        // Verify that restaurantRepository.addReview was never called
        verify(mockRestaurantRepository, never()).addReview(any(Review.class));

        // Verify that the "zero rating" log was made
        mockedLog.verify(() -> Log.d("ReviewsViewModel", "Rating cannot be 0."));

        // Ensure other irrelevant logs were not made
        mockedLog.verify(() -> Log.d("ReviewsViewModel", "Comment cannot be empty."), never());
        mockedLog.verify(() -> Log.d(eq("ReviewsViewModel"), startsWith("Review added successfully")), never());
    }

    /**
     * Tests the {@link ReviewsViewModel#addReview(String, String, int, String)} method
     * for the scenario where the repository throws an exception during review addition.
     * Verifies that the repository's addReview method is called, an error log is made
     * with the exception, and the method returns false.
     */
    @Test
    public void addReview_whenRepositoryThrowsException_shouldLogExceptionAndReturnFalse() {
        // Arrange
        String username = "TestUser";
        String avatarUrl = "test_avatar.jpg";
        String comment = "Valid comment";
        int rate = 5;
        RuntimeException repositoryException = new RuntimeException("Database connection failed");

        // Configure the mock repository to throw an exception when addReview is called
        doThrow(repositoryException).when(mockRestaurantRepository).addReview(any(Review.class));

        // Act
        boolean result = reviewsViewModel.addReview(username, avatarUrl, rate, comment);

        // Assert
        assertFalse("addReview should return false when repository throws an exception", result);

        // Verify that restaurantRepository.addReview was called (even though it threw an exception)
        verify(mockRestaurantRepository, times(1)).addReview(any(Review.class));

        // Verify that the error log was made with the correct message and exception
        mockedLog.verify(() -> Log.e("ReviewsViewModel", "Error adding review to repository for user: " + username, repositoryException));

        // Ensure the success log was not made
        mockedLog.verify(() -> Log.d(eq("ReviewsViewModel"), startsWith("Review added successfully")), never());
    }
}