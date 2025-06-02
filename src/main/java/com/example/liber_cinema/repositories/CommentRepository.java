package com.example.liber_cinema.repositories;

import com.example.liber_cinema.models.Comment;
import com.example.liber_cinema.models.UserList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    List<Comment> findByActivityIdOrderByCreatedAtAsc(Long activityId);
    
    @Query("SELECT c FROM Comment c WHERE c.activity.id IN :activityIds ORDER BY c.createdAt ASC")
    List<Comment> findByActivityIdsOrderByCreatedAtAsc(@Param("activityIds") List<Long> activityIds);
    
    void deleteByActivityId(Long activityId);
}
