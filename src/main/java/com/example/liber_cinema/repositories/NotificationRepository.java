package com.example.liber_cinema.repositories;

import com.example.liber_cinema.models.Notification;
import com.example.liber_cinema.models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    
    List<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    long countByUserAndReadFalse(User user);
    
    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.user = :user AND n.read = false")
    void markAllAsRead(@Param("user") User user);
}
