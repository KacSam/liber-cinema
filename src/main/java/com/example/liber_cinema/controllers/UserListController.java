package com.example.liber_cinema.controllers;

import com.example.liber_cinema.models.UserList;
import com.example.liber_cinema.models.enums.UserListType;
import com.example.liber_cinema.services.UserListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/userlists")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class UserListController {

    private final UserListService userListService;

    @GetMapping("/my-lists")
    public ResponseEntity<List<UserList>> getUserMovieLists() {
        List<UserList> userLists = userListService.getUserMovieLists();
        return ResponseEntity.ok(userLists);
    }

    @GetMapping("/my-lists/type/{listType}")
    public ResponseEntity<List<UserList>> getUserMovieListsByType(
            @PathVariable UserListType listType) {
        List<UserList> userLists = userListService.getUserMovieListsByType(listType);
        return ResponseEntity.ok(userLists);
    }
}
