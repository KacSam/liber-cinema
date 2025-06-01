package com.example.liber_cinema;

import com.example.liber_cinema.models.Book;
import com.example.liber_cinema.repositories.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Test
    public void testGetBooks() throws Exception {
        Book book1 = new Book(null, "Book 1", "Author 1", "Genre 1", LocalDate.of(2020, 1, 1), "Description 1", "8.5", null, null);
        Book book2 = new Book(null, "Book 2", "Author 2", "Genre 2", LocalDate.of(2021, 2, 2), "Description 2", "9.0", null, null);
        bookRepository.save(book1);
        bookRepository.save(book2);

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Book 1"))
                .andExpect(jsonPath("$[1].title").value("Book 2"));
    }

    @Test
    public void testGetBookById() throws Exception {
        Book book = new Book(null, "Book 1", "Author 1", "Genre 1", LocalDate.of(2020, 1, 1), "Description 1", "8.5", null, null);
        book = bookRepository.save(book);

        mockMvc.perform(get("/books/" + book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Book 1"))
                .andExpect(jsonPath("$.author").value("Author 1"));
    }

    @Test
    public void testAddNewBook() throws Exception {
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"New Book\", \"author\": \"New Author\", \"genre\": \"New Genre\", \"publicationDate\": \"2022-03-03\", \"description\": \"New Description\", \"externalRating\": \"7.5\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Book"))
                .andExpect(jsonPath("$.author").value("New Author"));

    }

    @Test
    public void testDeleteBook() throws Exception {
        Book book = new Book(null, "Book to Delete", "Author", "Genre", LocalDate.now(), "Description", "5.0", null, null);
        book = bookRepository.save(book);

        mockMvc.perform(delete("/books/" + book.getId()))
                .andExpect(status().isNoContent());
    }
}
