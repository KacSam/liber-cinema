package com.example.liber_cinema.services;

import com.example.liber_cinema.dtos.CommentDTO;
import com.example.liber_cinema.models.Comment;
import com.example.liber_cinema.models.User;
import com.example.liber_cinema.models.UserList;
import com.example.liber_cinema.repositories.CommentRepository;
import com.example.liber_cinema.repositories.UserListRepository;
import com.example.liber_cinema.repositories.UserRepository;
import com.example.liber_cinema.security.services.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    
    private final CommentRepository commentRepository;
    private final UserListRepository userListRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    public List<CommentDTO> getCommentsForActivity(Long activityId) {
        List<Comment> comments = commentRepository.findByActivityIdOrderByCreatedAtAsc(activityId);
        return comments.stream()
                .map(CommentDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<CommentDTO> getCommentsForActivities(List<Long> activityIds) {
        if (activityIds.isEmpty()) {
            return List.of();
        }
        
        List<Comment> comments = commentRepository.findByActivityIdsOrderByCreatedAtAsc(activityIds);
        return comments.stream()
                .map(CommentDTO::fromEntity)
                .collect(Collectors.toList());
    }
      @Transactional
    public CommentDTO addComment(Long activityId, String content) {
        User currentUser = getCurrentUser();
        
        UserList activity = userListRepository.findById(activityId.intValue())
                .orElseThrow(() -> new RuntimeException("Activity not found"));
        
        Comment comment = new Comment();
        comment.setUser(currentUser);
        comment.setActivity(activity);
        comment.setContent(content);
        
        Comment savedComment = commentRepository.save(comment);
        
        // Create notification for the activity owner
        User activityOwner = activity.getUser();
        String movieTitle = activity.getMovie() != null ? activity.getMovie().getTitle() : "unknown";
        notificationService.createCommentNotification(activityOwner, currentUser, activityId, movieTitle);
        
        return CommentDTO.fromEntity(savedComment);
    }
    
    @Transactional
    public void deleteComment(Long commentId) {
        User currentUser = getCurrentUser();
        
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        // Check if the user is the comment owner
        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not authorized to delete this comment");
        }
        
        commentRepository.delete(comment);
    }
}
