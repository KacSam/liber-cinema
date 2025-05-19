package com.example.liber_cinema.services;

import com.example.liber_cinema.models.Movie;
import com.example.liber_cinema.models.User;
import com.example.liber_cinema.models.UserList;
import com.example.liber_cinema.models.enums.UserListType;
import com.example.liber_cinema.repositories.MovieRepository;
import com.example.liber_cinema.repositories.UserListRepository;
import com.example.liber_cinema.repositories.UserRepository;
import com.example.liber_cinema.security.services.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserListService {

    private final UserListRepository userListRepository;
    private final UserService userService;
    private final MovieRepository movieRepository;
    private final MovieService movieService;@Transactional
    public UserList addMovieToUserList(Movie movie, UserListType listType) {
        // Get the currently authenticated user
        User user = getCurrentUser();
        if (user == null) {
            throw new RuntimeException("No authenticated user found");
        }
        
        // Check if movie already exists in database by title
        Movie existingMovie = findOrSaveMovie(movie);
          // Create a new user list entry
        UserList userList = new UserList();
        userList.setUser(user);
        userList.setMovie(existingMovie);
        userList.setUserListType(listType);
        userList.setCreatedAt(LocalDateTime.now());
        
        return userListRepository.save(userList);
    }
    
    private Movie findOrSaveMovie(Movie movie) {
        // Implementation will depend on whether you have a findByTitle method in MovieRepository
        // For now, let's assume we don't have one and will check manually
        
        // First check if this movie already exists in our database
        List<Movie> allMovies = movieService.getAllMovies();
        for (Movie existingMovie : allMovies) {
            if (existingMovie.getTitle().equalsIgnoreCase(movie.getTitle())) {
                return existingMovie;
            }
        }
        
        // If movie doesn't exist, save it
        return movieService.addMovie(movie);
    }
      private User getCurrentUser() {
        return userService.getCurrentUser();
    }
    
    public List<UserList> getUserMovieLists() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("No authenticated user found");
        }
        return userListRepository.findByUserIdAndMovie_IdIsNotNull(currentUser.getId());
    }
    
    public List<UserList> getUserMovieListsByType(UserListType listType) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("No authenticated user found");
        }
        return userListRepository.findByUserIdAndUserListType(currentUser.getId(), listType);
    }
}
