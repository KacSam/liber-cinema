package com.example.liber_cinema.models;


import com.example.liber_cinema.models.enums.UserListType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.type.ListType;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(
    name = "user_lists",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_user_movie_list_type",
        columnNames = {"user_id", "movie_id", "user_list_type"}
    )
)
@AllArgsConstructor
@NoArgsConstructor
public class UserList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"userLists", "ratings", "password"})
    private User user;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    @JsonIgnoreProperties({"userLists"})
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "book_id")
    @JsonIgnoreProperties({"userLists"})
    private Book book;@Enumerated(EnumType.STRING)
    private UserListType userListType;    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();    @Column(name = "is_favorite")
    private boolean isFavorite = false;

    private Integer userRating;

}
