package com.example.liber_cinema.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String author;
    private String genre;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    private String description;

    @Column(name = "external_rating")    private String externalRating;

    @OneToMany(mappedBy = "book")
    @JsonIgnoreProperties("book")
    private Set<Rating> ratings;

    @OneToMany(mappedBy = "book")
    @JsonIgnoreProperties("book")
    private Set<UserList> userLists;




}
