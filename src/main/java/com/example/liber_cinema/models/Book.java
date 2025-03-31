package com.example.liber_cinema.models;



import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String title;

    private String author;
    private String genre;
    private String publicationDate;
    private String description;
    private String externalRating;

    @OneToMany
    private Set<Rating> ratings;

    @OneToMany
    private Set<UserList> userLists;
}
