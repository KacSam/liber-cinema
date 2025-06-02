package com.example.liber_cinema.services;

import com.example.liber_cinema.dtos.UserDTO;
import com.example.liber_cinema.models.Friend;
import com.example.liber_cinema.models.User;
import com.example.liber_cinema.models.UserList;
import com.example.liber_cinema.models.enums.FriendStatus;
import com.example.liber_cinema.repositories.FriendRepository;
import com.example.liber_cinema.repositories.UserListRepository;
import com.example.liber_cinema.repositories.UserRepository;
import com.example.liber_cinema.security.services.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final UserListRepository userListRepository;
    private final NotificationService notificationService;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<Map<String, Object>> getUserFriends() {
        User currentUser = getCurrentUser();
        
        // Find accepted friend relationships where the current user is either the user or the friend
        List<Friend> friendships = friendRepository.findByUserAndStatusOrFriendAndStatus(
                currentUser, FriendStatus.ACCEPTED, currentUser, FriendStatus.ACCEPTED);
        
        // Transform the data to a format suitable for the frontend
        return friendships.stream().map(friendship -> {
            Map<String, Object> friendData = new HashMap<>();
            
            // Determine which user is the friend (not the current user)
            User friend = friendship.getUser().getId().equals(currentUser.getId()) 
                ? friendship.getFriend() 
                : friendship.getUser();
            
            friendData.put("id", friendship.getId());
            friendData.put("username", friend.getUsername());
            friendData.put("email", friend.getEmail());
            friendData.put("since", friendship.getCreatedAt());
            
            return friendData;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getPendingFriendRequests() {
        User currentUser = getCurrentUser();
        
        // Find pending friend requests where the current user is the recipient
        List<Friend> pendingRequests = friendRepository.findByFriendAndStatus(currentUser, FriendStatus.PENDING);
        
        // Transform the data
        return pendingRequests.stream().map(request -> {
            Map<String, Object> requestData = new HashMap<>();
            
            requestData.put("id", request.getId());
            requestData.put("username", request.getUser().getUsername());
            requestData.put("email", request.getUser().getEmail());
            requestData.put("requestedAt", request.getCreatedAt());
            
            return requestData;
        }).collect(Collectors.toList());
    }    @Transactional
    public Friend sendFriendRequest(String username) {
        User currentUser = getCurrentUser();
        
        // Find the user by username
        User targetUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Użytkownik o nazwie " + username + " nie istnieje"));
        
        // Check if this is not the current user
        if (currentUser.getId().equals(targetUser.getId())) {
            throw new RuntimeException("Nie możesz dodać siebie do znajomych");
        }
        
        // Check if there's already a friendship or request between these users
        boolean existingRelationship = friendRepository.existsByUserAndFriendOrFriendAndUser(
                currentUser, targetUser, currentUser, targetUser);
        
        if (existingRelationship) {
            throw new RuntimeException("Relacja znajomości z tym użytkownikiem już istnieje");
        }
        
        // Create a new friend request
        Friend friendRequest = new Friend();
        friendRequest.setUser(currentUser);
        friendRequest.setFriend(targetUser);
        friendRequest.setStatus(FriendStatus.PENDING);
        friendRequest.setCreatedAt(LocalDateTime.now());
        
        Friend savedRequest = friendRepository.save(friendRequest);
        
        // Create notification for the target user
        notificationService.createFriendRequestNotification(targetUser, currentUser, savedRequest.getId());
        
        return savedRequest;
    }    @Transactional
    public Friend acceptFriendRequest(Long requestId) {
        User currentUser = getCurrentUser();
        
        Friend friendRequest = friendRepository.findById(requestId.intValue())
                .orElseThrow(() -> new RuntimeException("Zaproszenie do znajomych nie istnieje"));
        
        // Check if the current user is the recipient of this request
        if (!friendRequest.getFriend().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Nie masz uprawnień do akceptacji tego zaproszenia");
        }
        
        // Check if the request is pending
        if (friendRequest.getStatus() != FriendStatus.PENDING) {
            throw new RuntimeException("To zaproszenie do znajomych nie oczekuje na akceptację");
        }
        
        // Accept the request
        friendRequest.setStatus(FriendStatus.ACCEPTED);
        Friend savedRequest = friendRepository.save(friendRequest);
        
        // Create notification for the sender
        notificationService.createFriendAcceptNotification(friendRequest.getUser(), currentUser);
        
        return savedRequest;
    }

    @Transactional
    public void rejectFriendRequest(Long requestId) {
        User currentUser = getCurrentUser();
        
        Friend friendRequest = friendRepository.findById(requestId.intValue())
                .orElseThrow(() -> new RuntimeException("Zaproszenie do znajomych nie istnieje"));
        
        // Check if the current user is the recipient of this request
        if (!friendRequest.getFriend().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Nie masz uprawnień do odrzucenia tego zaproszenia");
        }
        
        // Delete the request
        friendRepository.delete(friendRequest);
    }

    @Transactional
    public void removeFriend(Long friendshipId) {
        User currentUser = getCurrentUser();
        
        Friend friendship = friendRepository.findById(friendshipId.intValue())
                .orElseThrow(() -> new RuntimeException("Relacja znajomości nie istnieje"));
        
        // Check if the current user is part of this friendship
        if (!friendship.getUser().getId().equals(currentUser.getId()) && 
            !friendship.getFriend().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Nie masz uprawnień do usunięcia tej relacji znajomości");
        }
        
        // Delete the friendship
        friendRepository.delete(friendship);
    }    public List<Map<String, Object>> getFriendsActivity(int page, int size) {
        User currentUser = getCurrentUser();
        
        // Get all accepted friendships
        List<Friend> friendships = friendRepository.findByUserAndStatusOrFriendAndStatus(
                currentUser, FriendStatus.ACCEPTED, currentUser, FriendStatus.ACCEPTED);
        
        // Get friend IDs
        Set<Long> friendIds = friendships.stream()
                .map(friendship -> {
                    return friendship.getUser().getId().equals(currentUser.getId()) 
                        ? friendship.getFriend().getId() 
                        : friendship.getUser().getId();
                })
                .collect(Collectors.toSet());
        
        // Add current user ID to see their own activity as well
        friendIds.add(currentUser.getId());
          
        if (friendIds.isEmpty()) {
            // This shouldn't happen now as we've added current user, but keeping as a safety check
            return new ArrayList<>();
        }
          
        // Get paginated activities
        int offset = page * size;
        List<UserList> friendsActivity = userListRepository.findActivitiesPaginated(
                new ArrayList<>(friendIds), size, offset);
        
        // Transform to a format suitable for the frontend
        return friendsActivity.stream()
                .map(activity -> {
                    Map<String, Object> activityData = new HashMap<>();
                    
                    activityData.put("id", activity.getId());
                    activityData.put("username", activity.getUser().getUsername());
                    activityData.put("movieId", activity.getMovie().getId());
                    activityData.put("movieTitle", activity.getMovie().getTitle());
                    activityData.put("listType", activity.getUserListType());
                    activityData.put("userRating", activity.getUserRating());
                    activityData.put("isFavorite", activity.isFavorite());
                    activityData.put("timestamp", activity.getCreatedAt());
                    
                    return activityData;
                })
                .collect(Collectors.toList());
    }

    public List<UserDTO> searchUsers(String query) {
        User currentUser = getCurrentUser();
        
        // Find users matching the search query
        List<User> matchingUsers = userRepository.findByUsernameOrEmailContaining(query);
        
        // Get IDs of existing friends or friend requests
        List<Friend> friendships = friendRepository.findByUserAndStatusOrFriendAndStatus(
                currentUser, FriendStatus.ACCEPTED, currentUser, FriendStatus.ACCEPTED);
        
        List<Friend> pendingRequests = friendRepository.findByUserAndStatus(currentUser, FriendStatus.PENDING);
        
        // Create a set of IDs to exclude (current user, friends, and users with pending requests)
        Set<Long> excludeIds = new HashSet<>();
        excludeIds.add(currentUser.getId());
        
        for (Friend friendship : friendships) {
            if (friendship.getUser().getId().equals(currentUser.getId())) {
                excludeIds.add(friendship.getFriend().getId());
            } else {
                excludeIds.add(friendship.getUser().getId());
            }
        }
        
        for (Friend pending : pendingRequests) {
            excludeIds.add(pending.getFriend().getId());
        }
        
        // Filter out users that are already friends or have pending requests
        return matchingUsers.stream()
                .filter(user -> !excludeIds.contains(user.getId()))
                .map(user -> {
                    UserDTO dto = new UserDTO();
                    dto.setId(user.getId());
                    dto.setUsername(user.getUsername());
                    dto.setEmail(user.getEmail());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public Friend sendFriendRequestById(Long userId) {
        User currentUser = getCurrentUser();
        
        // Find the user by ID
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik o ID " + userId + " nie istnieje"));
        
        // Check if this is not the current user
        if (currentUser.getId().equals(targetUser.getId())) {
            throw new RuntimeException("Nie możesz dodać siebie do znajomych");
        }
        
        // Check if there's already a friendship or request between these users
        boolean existingRelationship = friendRepository.existsByUserAndFriendOrFriendAndUser(
                currentUser, targetUser, currentUser, targetUser);
        
        if (existingRelationship) {
            throw new RuntimeException("Relacja znajomości z tym użytkownikiem już istnieje");
        }
        
        // Create a new friend request
        Friend friendRequest = new Friend();
        friendRequest.setUser(currentUser);
        friendRequest.setFriend(targetUser);
        friendRequest.setStatus(FriendStatus.PENDING);
        friendRequest.setCreatedAt(LocalDateTime.now());
        
        Friend savedRequest = friendRepository.save(friendRequest);
        
        // Create notification for the target user
        notificationService.createFriendRequestNotification(targetUser, currentUser, savedRequest.getId());
        
        return savedRequest;
    }
}
