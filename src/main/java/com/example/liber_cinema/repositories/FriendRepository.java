package com.example.liber_cinema.repositories;

import com.example.liber_cinema.models.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Integer> {
}
