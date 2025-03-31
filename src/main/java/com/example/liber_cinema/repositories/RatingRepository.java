package com.example.liber_cinema.repositories;

import com.example.liber_cinema.models.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {
}
