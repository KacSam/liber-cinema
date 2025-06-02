package com.example.liber_cinema.dtos;

import com.example.liber_cinema.models.UserList;
import com.example.liber_cinema.models.enums.UserListType;

import java.time.LocalDateTime;

public class ActivityDTO {
    private Long id;
    private String username;
    private Long movieId;
    private String movieTitle;
    private String moviePoster;
    private UserListType listType;
    private Integer userRating;
    private boolean isFavorite;
    private LocalDateTime timestamp;

    public ActivityDTO() {
    }

    public ActivityDTO(UserList userList) {
        this.id = userList.getId();
        this.username = userList.getUser().getUsername();
        this.movieId = userList.getMovie().getId();
        this.movieTitle = userList.getMovie().getTitle();
        // Puste pole na poster, które może być uzupełnione w przyszłości
        this.moviePoster = null;
        this.listType = userList.getUserListType();
        this.userRating = userList.getUserRating();
        this.isFavorite = userList.isFavorite();
        this.timestamp = userList.getCreatedAt();
    }

    // Gettery i settery

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMoviePoster() {
        return moviePoster;
    }

    public void setMoviePoster(String moviePoster) {
        this.moviePoster = moviePoster;
    }

    public UserListType getListType() {
        return listType;
    }

    public void setListType(UserListType listType) {
        this.listType = listType;
    }

    public Integer getUserRating() {
        return userRating;
    }

    public void setUserRating(Integer userRating) {
        this.userRating = userRating;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
