package com.example.liber_cinema;

import com.example.liber_cinema.controllers.MovieController;
import com.example.liber_cinema.models.Movie;
import com.example.liber_cinema.services.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnListOfMovies() throws Exception {
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Example Movie");
        movie.setDirector("Director");
        movie.setGenre("Action");
        movie.setReleaseDate("2023-01-01");
        movie.setDuration("120 min");
        movie.setImdbRating(8.5);

        Mockito.when(movieService.getAllMovies()).thenReturn(List.of(movie));

        mockMvc.perform(get("/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Example Movie"));
    }

    @Test
    void shouldReturnMovieById() throws Exception {
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Example Movie");
        movie.setDirector("Director");
        movie.setGenre("Action");
        movie.setReleaseDate("2023-01-01");
        movie.setDuration("120 min");
        movie.setImdbRating(8.5);

        Mockito.when(movieService.getMovieById(1L)).thenReturn(Optional.of(movie));

        mockMvc.perform(get("/movies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Example Movie"));
    }

    @Test
    void shouldReturn404WhenMovieNotFound() throws Exception {
        Mockito.when(movieService.getMovieById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/movies/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400BadRequestWhenInvalidMovieData() throws Exception {
        Movie invalidMovie = new Movie();
        invalidMovie.setTitle(""); // pusty tytuł - niepoprawne
        invalidMovie.setDirector("Director");
        invalidMovie.setGenre("Action");
        invalidMovie.setReleaseDate("2023-01-01");
        invalidMovie.setDuration("120 min");
        invalidMovie.setImdbRating(8.5);

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMovie)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldAddNewMovie() throws Exception {
        Movie newMovie = new Movie();
        newMovie.setTitle("New Movie");
        newMovie.setDirector("Director");
        newMovie.setGenre("Action");
        newMovie.setReleaseDate("2023-01-01");
        newMovie.setDuration("120 min");
        newMovie.setImdbRating(8.5);

        Movie savedMovie = new Movie();
        savedMovie.setId(1L);
        savedMovie.setTitle("New Movie");
        savedMovie.setDirector("Director");
        savedMovie.setGenre("Action");
        savedMovie.setReleaseDate("2023-01-01");
        savedMovie.setDuration("120 min");
        savedMovie.setImdbRating(8.5);

        Mockito.when(movieService.addMovie(any(Movie.class))).thenReturn(savedMovie);

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMovie)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New Movie"));
    }

    @Test
    void shouldDeleteMovie() throws Exception {
        mockMvc.perform(delete("/movies/1"))
                .andExpect(status().isOk());

        Mockito.verify(movieService).deleteMovie(eq(1L));
    }
}