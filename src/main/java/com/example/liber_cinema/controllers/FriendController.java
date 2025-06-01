package com.example.liber_cinema.controllers;

import com.example.liber_cinema.dtos.ActivityDTO;
import com.example.liber_cinema.dtos.FriendDTO;
import com.example.liber_cinema.dtos.UserDTO;
import com.example.liber_cinema.models.Friend;
import com.example.liber_cinema.models.User;
import com.example.liber_cinema.models.UserList;
import com.example.liber_cinema.services.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FriendDTO>> getUserFriends() {
        List<FriendDTO> friends = friendService.getUserFriends().stream()
            .map(friendData -> {
                FriendDTO dto = new FriendDTO();
                dto.setId((Long) friendData.get("id"));
                
                // Tworzenie obiektu UserDTO dla przyjaciela
                UserDTO friendDto = new UserDTO();
                friendDto.setUsername((String) friendData.get("username"));
                friendDto.setEmail((String) friendData.get("email"));
                
                dto.setFriend(friendDto);
                dto.setCreatedAt((java.time.LocalDateTime) friendData.get("since"));
                
                return dto;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/pending")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FriendDTO>> getPendingFriendRequests() {
        List<FriendDTO> requests = friendService.getPendingFriendRequests().stream()
            .map(requestData -> {
                FriendDTO dto = new FriendDTO();
                dto.setId((Long) requestData.get("id"));
                
                // Tworzenie obiektu UserDTO dla użytkownika wysyłającego zaproszenie
                UserDTO userDto = new UserDTO();
                userDto.setUsername((String) requestData.get("username"));
                userDto.setEmail((String) requestData.get("email"));
                
                dto.setUser(userDto);
                dto.setCreatedAt((java.time.LocalDateTime) requestData.get("requestedAt"));
                
                return dto;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(requests);
    }    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addFriend(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        
        if (username == null || username.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Nazwa użytkownika jest wymagana"));
        }
        
        try {
            Friend friend = friendService.sendFriendRequest(username);
            
            // Tworzymy DTO z obiektu Friend
            FriendDTO friendDTO = new FriendDTO();
            friendDTO.setId(friend.getId());
            
            UserDTO userDTO = new UserDTO();
            userDTO.setId(friend.getUser().getId());
            userDTO.setUsername(friend.getUser().getUsername());
            userDTO.setEmail(friend.getUser().getEmail());
            
            UserDTO targetDTO = new UserDTO();
            targetDTO.setId(friend.getFriend().getId());
            targetDTO.setUsername(friend.getFriend().getUsername());
            targetDTO.setEmail(friend.getFriend().getEmail());
            
            friendDTO.setUser(userDTO);
            friendDTO.setFriend(targetDTO);
            friendDTO.setStatus(friend.getStatus());
            friendDTO.setCreatedAt(friend.getCreatedAt());
            
            return ResponseEntity.ok(Map.of(
                "message", "Zaproszenie do znajomych zostało wysłane",
                "friend", friendDTO
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }    @PostMapping("/add-by-id")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addFriendById(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        
        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "ID użytkownika jest wymagana"));
        }
        
        try {
            Friend friend = friendService.sendFriendRequestById(userId);
            
            // Tworzymy DTO z obiektu Friend
            FriendDTO friendDTO = new FriendDTO();
            friendDTO.setId(friend.getId());
            
            UserDTO userDTO = new UserDTO();
            userDTO.setId(friend.getUser().getId());
            userDTO.setUsername(friend.getUser().getUsername());
            userDTO.setEmail(friend.getUser().getEmail());
            
            UserDTO targetDTO = new UserDTO();
            targetDTO.setId(friend.getFriend().getId());
            targetDTO.setUsername(friend.getFriend().getUsername());
            targetDTO.setEmail(friend.getFriend().getEmail());
            
            friendDTO.setUser(userDTO);
            friendDTO.setFriend(targetDTO);
            friendDTO.setStatus(friend.getStatus());
            friendDTO.setCreatedAt(friend.getCreatedAt());
            
            return ResponseEntity.ok(Map.of(
                "message", "Zaproszenie do znajomych zostało wysłane",
                "friend", friendDTO
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }    @PostMapping("/{id}/accept")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable Long id) {
        try {
            Friend friend = friendService.acceptFriendRequest(id);
            
            // Tworzymy DTO z obiektu Friend
            FriendDTO friendDTO = new FriendDTO();
            friendDTO.setId(friend.getId());
            
            UserDTO userDTO = new UserDTO();
            userDTO.setId(friend.getUser().getId());
            userDTO.setUsername(friend.getUser().getUsername());
            userDTO.setEmail(friend.getUser().getEmail());
            
            UserDTO targetDTO = new UserDTO();
            targetDTO.setId(friend.getFriend().getId());
            targetDTO.setUsername(friend.getFriend().getUsername());
            targetDTO.setEmail(friend.getFriend().getEmail());
            
            friendDTO.setUser(userDTO);
            friendDTO.setFriend(targetDTO);
            friendDTO.setStatus(friend.getStatus());
            friendDTO.setCreatedAt(friend.getCreatedAt());
            
            return ResponseEntity.ok(Map.of(
                "message", "Zaproszenie do znajomych zostało zaakceptowane",
                "friend", friendDTO
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> rejectFriendRequest(@PathVariable Long id) {
        try {
            friendService.rejectFriendRequest(id);
            return ResponseEntity.ok(Map.of("message", "Zaproszenie do znajomych zostało odrzucone"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> removeFriend(@PathVariable Long id) {
        try {
            friendService.removeFriend(id);
            return ResponseEntity.ok(Map.of("message", "Znajomy został usunięty"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }    }    @GetMapping("/activity")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ActivityDTO>> getActivityFeed(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        
        List<ActivityDTO> activities = friendService.getFriendsActivity(page, size).stream()
            .map(activityData -> {
                ActivityDTO dto = new ActivityDTO();
                dto.setId((Long) activityData.get("id"));
                dto.setUsername((String) activityData.get("username"));
                dto.setMovieId((Long) activityData.get("movieId"));
                dto.setMovieTitle((String) activityData.get("movieTitle"));
                dto.setListType((com.example.liber_cinema.models.enums.UserListType) activityData.get("listType"));
                dto.setUserRating((Integer) activityData.get("userRating"));
                dto.setFavorite((boolean) activityData.get("isFavorite"));
                dto.setTimestamp((java.time.LocalDateTime) activityData.get("timestamp"));
                
                return dto;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(activities);
    }
    
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String query) {
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        List<UserDTO> users = friendService.searchUsers(query);
        return ResponseEntity.ok(users);
    }
}
