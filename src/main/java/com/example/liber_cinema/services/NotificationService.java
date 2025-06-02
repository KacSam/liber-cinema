package com.example.liber_cinema.services;

import com.example.liber_cinema.dtos.NotificationDTO;
import com.example.liber_cinema.models.Notification;
import com.example.liber_cinema.models.User;
import com.example.liber_cinema.models.enums.NotificationType;
import com.example.liber_cinema.repositories.NotificationRepository;
import com.example.liber_cinema.repositories.UserRepository;
import com.example.liber_cinema.security.services.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    public List<NotificationDTO> getUserNotifications(int page, int size) {
        User currentUser = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        
        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(currentUser, pageable);
        return notifications.stream()
                .map(NotificationDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public long getUnreadCount() {
        User currentUser = getCurrentUser();
        return notificationRepository.countByUserAndReadFalse(currentUser);
    }
    
    @Transactional
    public void markAllAsRead() {
        User currentUser = getCurrentUser();
        notificationRepository.markAllAsRead(currentUser);
    }
    
    @Transactional
    public void markAsRead(Long notificationId) {
        User currentUser = getCurrentUser();
        
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        if (!notification.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not authorized to access this notification");
        }
        
        notification.setRead(true);
        notificationRepository.save(notification);
    }
    
    @Transactional
    public void createFriendRequestNotification(User recipient, User sender, Long friendRequestId) {
        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setMessage(sender.getUsername() + " wysłał(a) Ci zaproszenie do znajomych");
        notification.setType(NotificationType.FRIEND_REQUEST);
        notification.setRelatedId(friendRequestId);
        
        notificationRepository.save(notification);
    }
    
    @Transactional
    public void createFriendAcceptNotification(User recipient, User acceptor) {
        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setMessage(acceptor.getUsername() + " zaakceptował(a) Twoje zaproszenie do znajomych");
        notification.setType(NotificationType.FRIEND_ACCEPT);
        notification.setRelatedId(acceptor.getId());
        
        notificationRepository.save(notification);
    }
    
    @Transactional
    public void createCommentNotification(User recipient, User commenter, Long activityId, String movieTitle) {
        // Don't notify if user comments on their own activity
        if (recipient.getId().equals(commenter.getId())) {
            return;
        }
        
        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setMessage(commenter.getUsername() + " skomentował(a) Twoją aktywność z filmem \"" + movieTitle + "\"");
        notification.setType(NotificationType.COMMENT);
        notification.setRelatedId(activityId);
        
        notificationRepository.save(notification);
    }
}
