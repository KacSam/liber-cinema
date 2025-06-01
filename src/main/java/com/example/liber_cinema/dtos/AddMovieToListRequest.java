package com.example.liber_cinema.dtos;

import com.example.liber_cinema.models.Movie;
import com.example.liber_cinema.models.enums.UserListType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddMovieToListRequest {
    private Movie movie;
    private UserListType listType;
}
