package com.example.liber_cinema.controllers;

import com.example.liber_cinema.dtos.CommentDTO;
import com.example.liber_cinema.services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    
    private final CommentService commentService;
    
    @GetMapping("/activity/{activityId}")
    public ResponseEntity<List<CommentDTO>> getCommentsForActivity(@PathVariable Long activityId) {
        List<CommentDTO> comments = commentService.getCommentsForActivity(activityId);
        return ResponseEntity.ok(comments);
    }
    
    @PostMapping("/activity/{activityId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentDTO> addComment(
            @PathVariable Long activityId, 
            @RequestBody Map<String, String> request) {
        
        String content = request.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        CommentDTO comment = commentService.addComment(activityId, content);
        return ResponseEntity.ok(comment);
    }
    
    @DeleteMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        try {
            commentService.deleteComment(commentId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
