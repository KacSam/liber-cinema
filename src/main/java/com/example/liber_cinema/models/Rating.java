package com.example.liber_cinema.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ratings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"ratings", "userLists", "password"})
    private User user;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    @JsonIgnoreProperties({"ratings", "userLists"})
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "book_id")
    @JsonIgnoreProperties({"ratings", "userLists"})
    private Book book;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String review;
}
