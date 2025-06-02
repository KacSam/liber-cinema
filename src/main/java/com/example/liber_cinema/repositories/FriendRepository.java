package com.example.liber_cinema.repositories;

import com.example.liber_cinema.models.Friend;
import com.example.liber_cinema.models.User;
import com.example.liber_cinema.models.enums.FriendStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Integer> {
    
    // Znajdź wszystkie relacje przyjaźni użytkownika (zaakceptowane)
    List<Friend> findByUserAndStatusOrFriendAndStatus(User user, FriendStatus status1, User friend, FriendStatus status2);
      // Znajdź wszystkie oczekujące zaproszenia do znajomych dla użytkownika
    List<Friend> findByFriendAndStatus(User friend, FriendStatus status);
    
    // Znajdź wszystkie zaproszenia wysłane przez użytkownika
    List<Friend> findByUserAndStatus(User user, FriendStatus status);
    
    // Sprawdź, czy istnieje już relacja między dwoma użytkownikami
    boolean existsByUserAndFriendOrFriendAndUser(User user1, User friend1, User user2, User friend2);
}
