package com.example.liber_cinema.dtos;

import com.example.liber_cinema.models.Movie;
import com.example.liber_cinema.models.UserList;
import com.example.liber_cinema.models.enums.UserListType;

/**
 * Data Transfer Object for UserList to avoid circular references during JSON serialization
 */
public class UserListDTO {
    private long id;
    private String username;
    private Movie movie;
    private UserListType userListType;

    public UserListDTO(UserList userList) {
        this.id = userList.getId();
        this.username = userList.getUser().getUsername();
        this.movie = userList.getMovie();
        this.userListType = userList.getUserListType();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public UserListType getUserListType() {
        return userListType;
    }

    public void setUserListType(UserListType userListType) {
        this.userListType = userListType;
    }
}
