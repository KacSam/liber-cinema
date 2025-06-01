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
import java.util.AbstractMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserListService {

    private final UserListRepository userListRepository;
    private final UserService userService;
    private final MovieRepository movieRepository;
    private final MovieService movieService;    @Transactional
    public UserList addMovieToUserList(Movie movie, UserListType listType, boolean isFavorite, Integer userRating) {
        // Get the currently authenticated user
        User user = getCurrentUser();
        if (user == null) {
            throw new RuntimeException("No authenticated user found");
        }
        System.out.println("Adding movie to user list for user ID: " + user.getId());
        System.out.println("Movie details: " + movie.getTitle() + ", ID: " + movie.getId());
        System.out.println("List type: " + listType);
        
        // Check if movie already exists in database by title
        Movie existingMovie = findOrSaveMovie(movie);
        System.out.println("Movie found/saved with ID: " + existingMovie.getId());
        
        // Find any existing entries for this movie in any of user's lists
        List<UserList> existingEntries = userListRepository.findByUserIdAndMovieId(user.getId(), existingMovie.getId());
        
        // If movie exists in any list
        if (!existingEntries.isEmpty()) {
            // If movie is already in the requested list type, update it
            Optional<UserList> sameTypeEntry = existingEntries.stream()
                .filter(entry -> entry.getUserListType() == listType)
                .findFirst();
                
            if (sameTypeEntry.isPresent()) {
                UserList existingEntry = sameTypeEntry.get();
                existingEntry.setFavorite(isFavorite);
                existingEntry.setUserRating(userRating);
                return userListRepository.save(existingEntry);
            }
            
            // Remove movie from other lists if moving between WATCHLIST and WATCHED
            System.out.println("Removing movie from other lists before adding to new list");
            existingEntries.forEach(entry -> {
                System.out.println("Removing from list type: " + entry.getUserListType());
                userListRepository.delete(entry);
            });
        }
          // Create a new user list entry
        UserList userList = new UserList();
        userList.setUser(user);
        userList.setMovie(existingMovie);
        userList.setUserListType(listType);
        userList.setCreatedAt(LocalDateTime.now());
        userList.setFavorite(isFavorite);
        userList.setUserRating(userRating);
        
        UserList savedList = userListRepository.save(userList);
        System.out.println("New user list entry created with ID: " + savedList.getId());
        return savedList;
    }
      private Movie findOrSaveMovie(Movie movie) {
        // Check if this movie already exists in our database by title
        Movie existingMovie = movieRepository.findByTitleIgnoreCase(movie.getTitle());
        
        if (existingMovie != null) {
            System.out.println("Found existing movie in database with ID: " + existingMovie.getId());
            return existingMovie;
        }
        
        // If movie doesn't exist, save it
        Movie savedMovie = movieService.addMovie(movie);
        System.out.println("Saved new movie to database with ID: " + savedMovie.getId());
        return savedMovie;
    }
      private User getCurrentUser() {
        return userService.getCurrentUser();
    }    public List<UserList> getUserMovieLists() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("No authenticated user found");
        }
        System.out.println("Fetching user lists for user ID: " + currentUser.getId());
        
        // Get user lists and remove duplicates based on movie and list type
        List<UserList> lists = userListRepository.findUserMovieLists(currentUser.getId())
            .stream()
            .collect(Collectors.groupingBy(
                ul -> new AbstractMap.SimpleEntry<>(ul.getMovie().getId(), ul.getUserListType()),
                Collectors.reducing((first, second) -> first)
            ))
            .values()
            .stream()
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
            
        System.out.println("Found " + lists.size() + " unique user lists");
        lists.forEach(list -> System.out.println("List: " + list.getId() + ", Type: " + list.getUserListType() + 
                               ", Movie: " + (list.getMovie() != null ? list.getMovie().getTitle() : "null")));
        return lists;
    }      public List<UserList> getUserListByType(UserListType listType) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("No authenticated user found");
        }
        return userListRepository.findByUserIdAndUserListType(currentUser.getId(), listType);
    }
    
    @Transactional
    public void removeDuplicates() {
        System.out.println("Removing duplicate entries from user lists...");
        userListRepository.removeDuplicates();
        System.out.println("Duplicate entries removed successfully");
    }
    @Transactional
    public UserList updateMovieFavoriteStatus(Long movieId, boolean isFavorite) {
        User user = getCurrentUser();
        if (user == null) {
            throw new RuntimeException("No authenticated user found");
        }

        Optional<UserList> userList = userListRepository.findByUserIdAndMovieIdAndUserListType(
            user.getId(), movieId, UserListType.WATCHED);
        
        if (userList.isEmpty()) {
            throw new RuntimeException("Movie not found in user's watched list");
        }

        UserList entry = userList.get();
        entry.setFavorite(isFavorite);
        return userListRepository.save(entry);
    }

    @Transactional
    public UserList updateMovieRating(Long movieId, int rating) {
        if (rating < 1 || rating > 10) {
            throw new IllegalArgumentException("Rating must be between 1 and 10");
        }

        User user = getCurrentUser();
        if (user == null) {
            throw new RuntimeException("No authenticated user found");
        }

        Optional<UserList> userList = userListRepository.findByUserIdAndMovieIdAndUserListType(
            user.getId(), movieId, UserListType.WATCHED);
        
        if (userList.isEmpty()) {
            throw new RuntimeException("Movie not found in user's watched list");
        }

        UserList entry = userList.get();
        entry.setUserRating(rating);
        return userListRepository.save(entry);
    }
}
