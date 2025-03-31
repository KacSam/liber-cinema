package com.example.liber_cinema.repositories;

import com.example.liber_cinema.models.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Integer> {
}
