package com.example.liber_cinema.repositories;

import com.example.liber_cinema.models.UserList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserListRepository extends JpaRepository<UserList, Integer> {
}
