package com.example.liber_cinema.dtos;

import com.example.liber_cinema.models.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    private Long activityId;
    private UserDTO user;
    private String content;
    private LocalDateTime createdAt;
    
    public static CommentDTO fromEntity(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setActivityId(comment.getActivity().getId());
        
        UserDTO userDto = new UserDTO();
        userDto.setId(comment.getUser().getId());
        userDto.setUsername(comment.getUser().getUsername());
        
        dto.setUser(userDto);
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        
        return dto;
    }
}
