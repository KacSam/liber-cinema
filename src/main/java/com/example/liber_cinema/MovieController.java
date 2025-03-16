package com.example.liber_cinema;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private static final Map<Integer, String> movies = new HashMap<>();
    static {
        movies.put(1, "Interstellar");
        movies.put(2, "Harry Potter");
        movies.put(3, "Star Wars");
        movies.put(4, "The Godfather");
        movies.put(5, "John Wick");
    }
}
