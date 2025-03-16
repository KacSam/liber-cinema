package com.example.liber_cinema;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/books")
public class BookController {

    private static final Map<Integer, String> books = new HashMap<>();

    static {
        books.put(1, "Pan Tadeusz");
        books.put(2, "Harry Potter");
        books.put(3, "Atomic Habits");
        books.put(4, "Krzyżacy");
        books.put(5, "Costam");
    }
}
