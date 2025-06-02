import React, { useState, useEffect, useRef } from 'react';
import { getUnreadCount, fetchNotifications, markAsRead, markAllAsRead, Notification } from '../services/notificationService';
import './NotificationIcon.css';

const NotificationIcon: React.FC = () => {
  const [unreadCount, setUnreadCount] = useState<number>(0);
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [showDropdown, setShowDropdown] = useState<boolean>(false);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const dropdownRef = useRef<HTMLDivElement>(null);
  // Fetch unread count on component mount
  useEffect(() => {
    const fetchCount = async () => {
      try {
        const count = await getUnreadCount();
        setUnreadCount(count);
      } catch (error) {
        console.error('Failed to fetch notification count', error);
      }
    };

    fetchCount();
    
    // Poll for new notifications every 30 seconds
    const interval = setInterval(fetchCount, 30000);
    
    return () => clearInterval(interval);
  }, []);

  // Handle clicks outside the dropdown to close it
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setShowDropdown(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  const handleIconClick = async () => {
    setShowDropdown(!showDropdown);
    
    if (!showDropdown && notifications.length === 0) {
      setIsLoading(true);
      try {
        const notifs = await fetchNotifications();
        setNotifications(notifs);
      } catch (error) {
        console.error('Failed to fetch notifications', error);
      } finally {
        setIsLoading(false);
      }
    }
  };

  const handleMarkAsRead = async (id: number) => {
    try {
      await markAsRead(id);
      setNotifications(notifications.map(notif => 
        notif.id === id ? { ...notif, read: true } : notif
      ));
      setUnreadCount(prev => Math.max(0, prev - 1));
    } catch (error) {
      console.error('Failed to mark notification as read', error);
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      await markAllAsRead();
      setNotifications(notifications.map(notif => ({ ...notif, read: true })));
      setUnreadCount(0);
    } catch (error) {
      console.error('Failed to mark all notifications as read', error);
    }
  };

  const getNotificationLink = (notification: Notification) => {
    switch(notification.type) {
      case 'FRIEND_REQUEST':
      case 'FRIEND_ACCEPT':
        return '/friends';
      case 'COMMENT':
        return `/activity?highlight=${notification.relatedId}`;
      default:
        return '#';
    }
  };

  const getTimeAgo = (dateString: string) => {
    const date = new Date(dateString);
    const now = new Date();
    const diffInSeconds = Math.floor((now.getTime() - date.getTime()) / 1000);
    
    if (diffInSeconds < 60) {
      return 'przed chwilą';
    } else if (diffInSeconds < 3600) {
      return `${Math.floor(diffInSeconds / 60)} min temu`;
    } else if (diffInSeconds < 86400) {
      return `${Math.floor(diffInSeconds / 3600)} godz temu`;
    } else {
      return `${Math.floor(diffInSeconds / 86400)} dni temu`;
    }
  };

  return (
    <div className="notification-container" ref={dropdownRef}>
      <div className="notification-icon" onClick={handleIconClick}>
        <i className="fa fa-bell">🔔</i>
        {unreadCount > 0 && (
          <span className="notification-badge">{unreadCount}</span>
        )}
      </div>
      
      {showDropdown && (
        <div className="notification-dropdown">
          <div className="notification-header">
            <h3>Powiadomienia</h3>
            {unreadCount > 0 && (
              <button className="mark-all-read" onClick={handleMarkAllAsRead}>
                Oznacz wszystkie jako przeczytane
              </button>
            )}
          </div>
          
          <div className="notification-list">
            {isLoading ? (
              <div className="notification-loading">Ładowanie...</div>
            ) : notifications.length === 0 ? (
              <div className="notification-empty">Brak powiadomień</div>
            ) : (
              notifications.map(notification => (
                <div 
                  key={notification.id} 
                  className={`notification-item ${!notification.read ? 'unread' : ''}`}
                  onClick={() => handleMarkAsRead(notification.id)}
                >
                  <a href={getNotificationLink(notification)}>
                    <div className="notification-content">
                      <p>{notification.message}</p>
                      <span className="notification-time">{getTimeAgo(notification.createdAt)}</span>
                    </div>
                  </a>
                </div>
              ))
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default NotificationIcon;
