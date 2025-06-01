package com.example.liber_cinema.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 25)
    private String username;

    @Column(unique = true, nullable = false, length = 50)
    private String email;    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties("user")
    private Set<Rating> ratings;

    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties("user")
    private Set<UserList> userLists;

//    @OneToMany(mappedBy = "user")
//    private Set<ActivityLog> activityLogs;
}
