package com.example.liber_cinema.controllers;

import com.example.liber_cinema.dtos.AddMovieToListRequest;
import com.example.liber_cinema.models.Movie;
import com.example.liber_cinema.models.UserList;
import com.example.liber_cinema.models.enums.UserListType;
import com.example.liber_cinema.services.MovieSearch;
import com.example.liber_cinema.services.MovieService;
import com.example.liber_cinema.services.UserListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final MovieSearch movieSearch;
    private final UserListService userListService;
      // Removed toggleFavorite endpoint as the functionality is now handled through addMovieToUserList

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
        return ResponseEntity.status(201).body(savedMovie);    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Movie> searchMovieByTitle(@RequestParam String title) {
        System.out.println("Searching for movie with title: " + title);
        Movie movie = movieSearch.searchMovie(title);
        if (movie == null) {
            System.out.println("No movie found with title: " + title);
            return ResponseEntity.notFound().build();
        }
        System.out.println("Found movie: " + movie.getTitle() + ", ID: " + movie.getId());
        return ResponseEntity.ok(movie);
    }

    @GetMapping("/search/genre")
    public ResponseEntity<List<Movie>> searchMoviesByGenre(@RequestParam String genre) {
        System.out.println("Searching for movies with genre: " + genre);
        List<Movie> movies = movieSearch.searchMoviesByGenre(genre);
        if (movies.isEmpty()) {
            System.out.println("No movies found for genre: " + genre);
            return ResponseEntity.notFound().build();
        }
        System.out.println("Found " + movies.size() + " movies for genre: " + genre);
        return ResponseEntity.ok(movies);    }

    @PostMapping("/add-to-list")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> addMovieToUserList(@RequestBody AddMovieToListRequest request) {
        try {
            System.out.println("Received request to add movie to list: " + 
                (request != null && request.getMovie() != null ? request.getMovie().getTitle() : "null") + 
                ", list type: " + (request != null ? request.getListType() : "null"));
            
            // Walidacja danych wejściowych
            if (request == null || request.getMovie() == null || request.getListType() == null) {
                System.out.println("Invalid request data: " + 
                    (request == null ? "request is null" : 
                     (request.getMovie() == null ? "movie is null" : "listType is null")));
                return ResponseEntity.badRequest().body(Map.of("message", "Nieprawidłowe dane filmu lub typ listy"));
            }

            // Set default values for isFavorite and userRating if not provided
            boolean isFavorite = request.getIsFavorite() != null ? request.getIsFavorite() : false;
            Integer userRating = request.getUserRating();

            // If it's the WATCHED list, rating is required
            if (request.getListType() == UserListType.WATCHED && userRating == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Ocena jest wymagana dla listy obejrzanych"));
            }

            // Validate rating range if provided
            if (userRating != null && (userRating < 1 || userRating > 10)) {
                return ResponseEntity.badRequest().body(Map.of("message", "Ocena musi być w zakresie od 1 do 10"));
            }

            // Debugowanie pełnego obiektu żądania
            System.out.println("Full request data: " + request);
            System.out.println("Movie title: " + request.getMovie().getTitle());
            System.out.println("List type: " + request.getListType());
            System.out.println("Is favorite: " + isFavorite);
            System.out.println("User rating: " + userRating);
            
            UserList userList = userListService.addMovieToUserList(request.getMovie(), request.getListType(), isFavorite, userRating);            System.out.println("Movie added to list successfully: " + userList.getId() + 
                               ", Movie: " + userList.getMovie().getTitle() + 
                               ", Type: " + userList.getUserListType() +
                               ", Favorite: " + userList.isFavorite() +
                               ", Rating: " + userList.getUserRating());
            return ResponseEntity.ok(userList);
        } catch (Exception e) {
            System.out.println("Error adding movie to list: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Błąd podczas dodawania filmu do listy: " + e.getMessage()));
        }
    }    @GetMapping("/my-lists")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<UserListType, List<UserList>>> getUserLists() {
        try {
            Map<UserListType, List<UserList>> lists = new HashMap<>();
            lists.put(UserListType.WATCHLIST, userListService.getUserListByType(UserListType.WATCHLIST));
            lists.put(UserListType.WATCHED, userListService.getUserListByType(UserListType.WATCHED));
            return ResponseEntity.ok(lists);
        } catch (Exception e) {
            System.out.println("Error getting user lists: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
