package com.example.liber_cinema;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private static final Map<Integer, String> movies = new HashMap<>();
    static {
        movies.put(0, "Interstellar");
        movies.put(1, "Harry Potter");
        movies.put(2, "Star Wars");
        movies.put(3, "The Godfather");
        movies.put(4, "John Wick");
    }

    @GetMapping
    public ResponseEntity<Object> getMovies(){
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<Object> getMovieById(@PathVariable int movieId){
        String movie = movies.get(movieId);
        if(movie == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(movies.get(movieId));
    }
    @PostMapping
    public ResponseEntity<Object> addNewMovie(@RequestBody String movie){
        if(movie == null || movies.containsKey(movie)) {
            return ResponseEntity.badRequest().body("Movie title cannot be empty");
        }
        if (movies.containsValue(movie)) {
            return ResponseEntity.badRequest().body("Movie already exists");
        }
        movies.put(movies.size(), movie);
        return ResponseEntity.status(201).body("Movie added successfully");
    }
}
