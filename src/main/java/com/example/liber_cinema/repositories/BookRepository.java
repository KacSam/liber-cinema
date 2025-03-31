package com.example.liber_cinema.repositories;

import com.example.liber_cinema.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
