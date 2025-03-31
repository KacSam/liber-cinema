package com.example.liber_cinema.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "movies")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    private String description;
    private String genre;
    private String director;
    private String releaseDate;
    private String duration;
    private double imdbRating;

    @OneToMany(mappedBy = "movie")
    private Set<Rating> ratings;

    @OneToMany(mappedBy = "movie")
    private Set<UserList> userLists;

}
