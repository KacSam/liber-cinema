package com.example.liber_cinema.services;

import com.example.liber_cinema.models.Movie;
import com.example.liber_cinema.models.Rating;
import com.example.liber_cinema.models.User;
import com.example.liber_cinema.repositories.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;
    private final UserService userService;

    @Transactional
    public Rating rateMovie(Movie movie, int ratingValue) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("No authenticated user found");
        }

        Rating rating = new Rating();
        rating.setUser(currentUser);
        rating.setMovie(movie);
        rating.setRating(ratingValue);

        return ratingRepository.save(rating);
    }

    public Rating getUserRatingForMovie(Long movieId) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("No authenticated user found");
        }

        return ratingRepository.findByUserIdAndMovieId(currentUser.getId(), movieId)
                .orElse(null);
    }
}
