package com.example.liber_cinema;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/books")
public class BookController {

    private static final Map<Integer, String> books = new HashMap<>();

    static {
        books.put(0, "Pan Tadeusz");
        books.put(1, "Harry Potter");
        books.put(2, "Atomic Habits");
        books.put(3, "Krzyżacy");
        books.put(4, "Costam");
    }

    @GetMapping
    public ResponseEntity<Object> getBooks(){
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<Object> getBookById(@PathVariable int bookId){
        String book = books.get(bookId);
        if(book == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(books.get(bookId));
    }

    @PostMapping
    public ResponseEntity<Object> addNewBook(@RequestBody String book){
        if(book == null || books.containsKey(book)) {
            return ResponseEntity.badRequest().body("Book title cannot be empty");
        }
        if (books.containsValue(book)) {
            return ResponseEntity.badRequest().body("Book already exists");
        }
        books.put(books.size(), book);
        return ResponseEntity.status(201).body("Book added successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteMovie(@PathVariable int id){
        if(books.get(id) == null){
            return ResponseEntity.notFound().build();
        }
        books.remove(id);
        return ResponseEntity.ok().build();
    }
}
