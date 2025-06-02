package com.example.liber_cinema.dtos;

import com.example.liber_cinema.models.Friend;
import com.example.liber_cinema.models.User;
import com.example.liber_cinema.models.enums.FriendStatus;

import java.time.LocalDateTime;

public class FriendDTO {
    private Long id;
    private UserDTO user;
    private UserDTO friend;
    private FriendStatus status;
    private LocalDateTime createdAt;

    public FriendDTO() {
    }

    public FriendDTO(Friend friend, boolean isCurrentUserInitiator) {
        this.id = friend.getId();
        
        if (isCurrentUserInitiator) {
            this.user = new UserDTO(friend.getUser());
            this.friend = new UserDTO(friend.getFriend());
        } else {
            this.user = new UserDTO(friend.getFriend());
            this.friend = new UserDTO(friend.getUser());
        }
        
        this.status = friend.getStatus();
        this.createdAt = friend.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public UserDTO getFriend() {
        return friend;
    }

    public void setFriend(UserDTO friend) {
        this.friend = friend;
    }

    public FriendStatus getStatus() {
        return status;
    }

    public void setStatus(FriendStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
