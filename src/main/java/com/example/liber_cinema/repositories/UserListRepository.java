package com.example.liber_cinema.repositories;

import com.example.liber_cinema.models.UserList;
import com.example.liber_cinema.models.enums.UserListType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserListRepository extends JpaRepository<UserList, Integer> {
    // Original query that might have issues
    List<UserList> findByUserIdAndMovie_IdIsNotNull(Long userId);
    
    // More explicit query using JPQL
    @Query("SELECT ul FROM UserList ul WHERE ul.user.id = :userId AND ul.movie IS NOT NULL")
    List<UserList> findUserMovieLists(@Param("userId") Long userId);
    
    List<UserList> findByUserIdAndUserListType(Long userId, UserListType listType);
    
    // Find specific entry by user, movie and list type
    Optional<UserList> findByUserIdAndMovieIdAndUserListType(Long userId, Long movieId, UserListType listType);
    
    // Find all entries for a user and movie combination
    List<UserList> findByUserIdAndMovieId(Long userId, Long movieId);    // Find latest activities from a list of users (for friends activity feed)
    List<UserList> findByUserIdInOrderByCreatedAtDesc(List<Long> userIds);
    
    // Find latest activities with limit
    List<UserList> findTop20ByUserIdInOrderByCreatedAtDesc(List<Long> userIds);
    
    // Find paginated activities
    @Query(value = "SELECT * FROM user_lists ul " +
           "WHERE ul.user_id IN :userIds " +
           "ORDER BY ul.created_at DESC " +
           "LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<UserList> findActivitiesPaginated(
            @Param("userIds") List<Long> userIds, 
            @Param("limit") int limit, 
            @Param("offset") int offset);
    
    // Remove duplicates
    @Modifying
    @Query(value = """
        DELETE FROM user_lists ul1 WHERE ul1.id NOT IN (
            SELECT MIN(ul2.id)
            FROM user_lists ul2
            GROUP BY ul2.user_id, ul2.movie_id, ul2.user_list_type
        )
    """, nativeQuery = true)
    void removeDuplicates();
}
