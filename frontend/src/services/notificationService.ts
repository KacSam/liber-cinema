import axios from 'axios';

const API_URL = 'http://localhost:8080/api/notifications';

// Get the auth token
const getAuthHeader = () => {
  const token = localStorage.getItem('token');
  return {
    headers: {
      Authorization: `Bearer ${token}`
    }
  };
};

export interface Notification {
  id: number;
  message: string;
  type: 'FRIEND_REQUEST' | 'FRIEND_ACCEPT' | 'COMMENT' | 'SYSTEM';
  relatedId: number;
  read: boolean;
  createdAt: string;
}

export const fetchNotifications = async (page = 0, size = 10) => {
  try {
    const response = await axios.get(`${API_URL}?page=${page}&size=${size}`, getAuthHeader());
    return response.data;
  } catch (error) {
    console.error('Error fetching notifications:', error);
    throw error;
  }
};

export const getUnreadCount = async () => {
  try {
    const response = await axios.get(`${API_URL}/unread-count`, getAuthHeader());
    return response.data.count;
  } catch (error) {
    console.error('Error fetching unread count:', error);
    return 0;
  }
};

export const markAsRead = async (id: number) => {
  try {
    await axios.post(`${API_URL}/${id}/mark-read`, {}, getAuthHeader());
  } catch (error) {
    console.error('Error marking notification as read:', error);
    throw error;
  }
};

export const markAllAsRead = async () => {
  try {
    await axios.post(`${API_URL}/mark-all-read`, {}, getAuthHeader());
  } catch (error) {
    console.error('Error marking all notifications as read:', error);
    throw error;
  }
};
