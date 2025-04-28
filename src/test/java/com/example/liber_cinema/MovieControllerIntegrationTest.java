package com.example.liber_cinema;

import com.example.liber_cinema.models.Movie;
import com.example.liber_cinema.repositories.MovieRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieRepository movieRepository;

    @Test
    public void testGetMovies() throws Exception {
        Movie movie1 = new Movie(null, "Movie 1", "Description 1", "Genre 1", "Director 1", "2020-01-01", "120 min", 8.5, null, null);
        Movie movie2 = new Movie(null, "Movie 2", "Description 2", "Genre 2", "Director 2", "2021-02-02", "150 min", 9.0, null, null);
        movieRepository.save(movie1);
        movieRepository.save(movie2);

        mockMvc.perform(get("/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Movie 1"))
                .andExpect(jsonPath("$[1].title").value("Movie 2"));
    }

    @Test
    public void testGetMovieById() throws Exception {
        Movie movie = new Movie(null, "Movie 1", "Description 1", "Genre 1", "Director 1", "2020-01-01", "120 min", 8.5, null, null);
        movie = movieRepository.save(movie);

        mockMvc.perform(get("/movies/" + movie.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Movie 1"))
                .andExpect(jsonPath("$.director").value("Director 1"))
                .andExpect(jsonPath("$.releaseDate").value("2020-01-01"));
    }

    @Test
    public void testAddNewMovie() throws Exception {
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"New Movie\", \"description\": \"New Description\", \"genre\": \"New Genre\", \"director\": \"New Director\", \"releaseDate\": \"2022-03-03\", \"duration\": \"130 min\", \"imdbRating\": 7.5}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Movie"))
                .andExpect(jsonPath("$.director").value("New Director"))
                .andExpect(jsonPath("$.releaseDate").value("2022-03-03"))
                .andExpect(jsonPath("$.imdbRating").value(7.5));
    }

    @Test
    public void testDeleteMovie() throws Exception {
        Movie movie = new Movie(null, "Movie to Delete", "Description", "Genre", "Director", "2020-01-01", "120 min", 7.0, null, null);
        movie = movieRepository.save(movie);

        mockMvc.perform(delete("/movies/" + movie.getId()))
                .andExpect(status().isOk());
    }
}
