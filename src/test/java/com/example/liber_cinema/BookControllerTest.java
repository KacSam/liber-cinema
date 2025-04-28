package com.example.liber_cinema;

import com.example.liber_cinema.controllers.BookController;
import com.example.liber_cinema.models.Book;
import com.example.liber_cinema.services.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnListOfBooks() throws Exception {
        Book book = new Book(1L, "Example Title", "Author", "Genre",
                LocalDate.now(), "Description", "5.0", null, null);

        Mockito.when(bookService.getAllBooks()).thenReturn(List.of(book));

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Example Title"));
    }

    @Test
    void shouldReturnBookById() throws Exception {
        Book book = new Book(1L, "Example Title", "Author", "Genre",
                LocalDate.now(), "Description", "5.0", null, null);

        Mockito.when(bookService.getBookById(1L)).thenReturn(Optional.of(book));

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Example Title"));
    }

    @Test
    void shouldReturn404WhenBookNotFound() throws Exception {
        Mockito.when(bookService.getBookById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/books/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn422WhenBookCannotBeProcessed() throws Exception {
        Book validBook = new Book(null, "Valid Book Title", "Author", "Genre",
                LocalDate.now(), "Description", "4.5", null, null);

        Mockito.when(bookService.addBook(any(Book.class))).thenReturn(null);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validBook)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void shouldReturn400BadRequestWhenInvalidBookData() throws Exception {
        Book invalidBook = new Book(null, "", "Author", "Genre",
                LocalDate.now(), "Description", "4.5", null, null);


        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidBook)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldAddNewBook() throws Exception {
        Book book = new Book(null, "New Book", "Author", "Genre",
                LocalDate.now(), "Description", "4.5", null, null);
        Book savedBook = new Book(1L, "New Book", "Author", "Genre",
                LocalDate.now(), "Description", "4.5", null, null);

        Mockito.when(bookService.addBook(any(Book.class))).thenReturn(savedBook);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New Book"));
    }

    @Test
    void shouldDeleteBook() throws Exception {
        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(bookService).deleteBook(eq(1L));
    }
}
