package com.example.liber_cinema.controllers;


import com.example.liber_cinema.dtos.AddMovieToListRequest;
import com.example.liber_cinema.models.Movie;
import com.example.liber_cinema.models.UserList;
import com.example.liber_cinema.models.enums.UserListType;
import com.example.liber_cinema.services.MovieSearch;
import com.example.liber_cinema.services.MovieService;
import com.example.liber_cinema.services.UserListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final MovieSearch movieSearch;
    private final UserListService userListService;

    @GetMapping
    public ResponseEntity<List<Movie>> getMovies() {
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        return movieService.getMovieById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Movie> addNewMovie(@RequestBody Movie movie) {

        if (movie.getTitle() == null || movie.getTitle().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Movie savedMovie = movieService.addMovie(movie);
        if (savedMovie == null) {
            return ResponseEntity.unprocessableEntity().build();
        }
        return ResponseEntity.status(201).body(savedMovie);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Movie> searchMovieByTitle(@RequestParam String title) {
        Movie movie = movieSearch.searchMovie(title);
        if (movie == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(movie);
    }

    @GetMapping("/search/genre")
    public ResponseEntity<List<Movie>> searchMoviesByGenre(@RequestParam String genre) {
        List<Movie> movies = movieSearch.searchMoviesByGenre(genre);
        if (movies.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(movies);
    }    @PostMapping("/add-to-list")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserList> addMovieToUserList(
            @RequestBody AddMovieToListRequest request) {
        try {
            UserList userList = userListService.addMovieToUserList(request.getMovie(), request.getListType());
            return ResponseEntity.ok(userList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
