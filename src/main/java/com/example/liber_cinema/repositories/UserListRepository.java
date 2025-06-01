package com.example.liber_cinema.repositories;

import com.example.liber_cinema.models.UserList;
import com.example.liber_cinema.models.enums.UserListType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserListRepository extends JpaRepository<UserList, Integer> {
    List<UserList> findByUserIdAndMovie_IdIsNotNull(Long userId);
    List<UserList> findByUserIdAndUserListType(Long userId, UserListType listType);
}
