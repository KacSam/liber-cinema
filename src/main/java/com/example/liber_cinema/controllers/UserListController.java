package com.example.liber_cinema.controllers;

import com.example.liber_cinema.dtos.UserListDTO;
import com.example.liber_cinema.models.UserList;
import com.example.liber_cinema.models.enums.UserListType;
import com.example.liber_cinema.services.UserListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/userlists")
@RequiredArgsConstructor
public class UserListController {    private final UserListService userListService;

    @GetMapping("/my-lists")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserListDTO>> getUserMovieLists() {
        System.out.println("Received request for user movie lists");
        List<UserList> userLists = userListService.getUserMovieLists();
        System.out.println("Found " + userLists.size() + " user movie lists");
        
        List<UserListDTO> dtoList = userLists.stream()
            .map(userList -> {
                System.out.println("Converting UserList: id=" + userList.getId() + 
                               ", type=" + userList.getUserListType() + 
                               ", movie=" + (userList.getMovie() != null ? userList.getMovie().getTitle() : "null"));
                return new UserListDTO(userList);
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtoList);
    }    @GetMapping("/my-lists/type/{listType}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserListDTO>> getUserMovieListsByType(
            @PathVariable UserListType listType) {
        List<UserList> userLists = userListService.getUserListByType(listType);
        
        List<UserListDTO> dtoList = userLists.stream()
            .map(UserListDTO::new)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtoList);
    }
      @PostMapping("/remove-duplicates")
    public ResponseEntity<String> removeDuplicates() {
        userListService.removeDuplicates();
        return ResponseEntity.ok("Duplikaty zostały usunięte pomyślnie");
    }
    
    @PostMapping("/movies/{movieId}/favorite")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserListDTO> toggleFavorite(@PathVariable Long movieId, @RequestBody Map<String, Boolean> request) {
        if (!request.containsKey("isFavorite")) {
            return ResponseEntity.badRequest().build();
        }

        try {
            UserList updated = userListService.updateMovieFavoriteStatus(movieId, request.get("isFavorite"));
            return ResponseEntity.ok(new UserListDTO(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/movies/{movieId}/rating")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserListDTO> rateMovie(@PathVariable Long movieId, @RequestBody Map<String, Integer> request) {
        if (!request.containsKey("rating")) {
            return ResponseEntity.badRequest().build();
        }

        try {
            UserList updated = userListService.updateMovieRating(movieId, request.get("rating"));
            return ResponseEntity.ok(new UserListDTO(updated));        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
