package com.example.liber_cinema.controllers;

import com.example.liber_cinema.models.Movie;
import com.example.liber_cinema.models.Rating;
import com.example.liber_cinema.services.MovieService;
import com.example.liber_cinema.services.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;
    private final MovieService movieService;

    @PostMapping("/movies/{movieId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Rating> rateMovie(@PathVariable Long movieId, @RequestBody Map<String, Integer> request) {
        if (!request.containsKey("rating")) {
            return ResponseEntity.badRequest().build();
        }

        int ratingValue = request.get("rating");
        if (ratingValue < 1 || ratingValue > 10) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Movie> movie = movieService.getMovieById(movieId);
        if (movie.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Rating rating = ratingService.rateMovie(movie.get(), ratingValue);
        return ResponseEntity.ok(rating);
    }

    @GetMapping("/movies/{movieId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Rating> getUserRatingForMovie(@PathVariable Long movieId) {
        Rating rating = ratingService.getUserRatingForMovie(movieId);
        if (rating == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rating);
    }
}
