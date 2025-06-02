import React, { useState, useEffect } from 'react';
import './ActivityFeed.css';

interface CommentItem {
    id: number;
    activityId: number;
    user: {
        id: number;
        username: string;
    };
    content: string;
    createdAt: string;
}

interface ActivityItem {
    id: number;
    username: string;
    movieId: number;
    movieTitle: string;
    listType: 'WATCHLIST' | 'WATCHED';
    userRating: number | null;
    isFavorite: boolean;
    timestamp: string;
    comments?: CommentItem[];
    showComments?: boolean;
    newComment?: string;
}

const ActivityFeed: React.FC = () => {
    const [activities, setActivities] = useState<ActivityItem[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [page, setPage] = useState(0);
    const [hasMore, setHasMore] = useState(true);
    const [currentUser, setCurrentUser] = useState<{ id: number; username: string } | null>(null);

    useEffect(() => {
        // Get current user information
        const userStr = localStorage.getItem('user');
        if (userStr) {
            try {
                const userData = JSON.parse(userStr);
                setCurrentUser({
                    id: userData.id,
                    username: userData.username
                });
            } catch (e) {
                console.error('Error parsing user data:', e);
            }
        }
        
        fetchActivities();
    }, []);

    const fetchActivities = async (resetPage = true) => {
        if (resetPage) {
            setPage(0);
            setLoading(true);
        } else {
            setLoading(true);
        }
        setError('');
        
        try {
            const token = localStorage.getItem('token');
            
            if (!token) {
                setError('Musisz być zalogowany, aby zobaczyć aktywność znajomych');
                setLoading(false);
                return;
            }

            const response = await fetch(`http://localhost:8080/api/friends/activity?page=${resetPage ? 0 : page}`, {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                }
            });
            
            if (!response.ok) {
                if (response.status === 401) {
                    localStorage.removeItem('token');
                    localStorage.removeItem('user');
                    setError('Twoja sesja wygasła. Zaloguj się ponownie.');
                    window.dispatchEvent(new CustomEvent('showLoginModal'));
                } else {
                    const errorText = await response.text();
                    console.error('Treść błędu:', errorText);
                    throw new Error(`Błąd podczas pobierania aktywności: ${response.status}`);
                }
                return;
            }
              const data = await response.json();
            
            // Initialize activities with empty comments arrays and toggle state
            const activitiesWithComments = data.map((activity: ActivityItem) => ({
                ...activity,
                comments: [],
                showComments: false,
                newComment: ''
            }));
            
            if (data.length === 0) {
                setHasMore(false);
            } else {
                setHasMore(true);
                if (resetPage) {
                    setActivities(activitiesWithComments);
                } else {
                    setActivities(prev => [...prev, ...activitiesWithComments]);
                }
                setPage(prev => prev + 1);
            }
        } catch (err) {
            console.error('Błąd podczas pobierania aktywności znajomych:', err);
            setError(err instanceof Error ? err.message : 'Wystąpił błąd podczas pobierania danych');
        } finally {
            setLoading(false);
        }
    };

    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        return date.toLocaleString('pl-PL', { 
            day: '2-digit', 
            month: '2-digit', 
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    const getActivityText = (activity: ActivityItem) => {
        if (activity.listType === 'WATCHED') {
            if (activity.userRating) {
                return `obejrzał(a) film "${activity.movieTitle}" i ocenił(a) go na ${activity.userRating}/10`;
            } else {
                return `obejrzał(a) film "${activity.movieTitle}"`;
            }
        } else {
            return `dodał(a) film "${activity.movieTitle}" do listy do obejrzenia`;
        }
    };

    const toggleComments = async (activityId: number) => {
        setActivities(prevActivities => 
            prevActivities.map(activity => {
                if (activity.id === activityId) {
                    const newShowComments = !activity.showComments;
                    
                    // If showing comments and there are no comments loaded yet, fetch them
                    if (newShowComments && (!activity.comments || activity.comments.length === 0)) {
                        fetchCommentsForActivity(activityId);
                    }
                    
                    return {
                        ...activity,
                        showComments: newShowComments
                    };
                }
                return activity;
            })
        );
    };
    
    const fetchCommentsForActivity = async (activityId: number) => {
        try {
            const token = localStorage.getItem('token');
            
            if (!token) return;
            
            const response = await fetch(`http://localhost:8080/api/comments/activity/${activityId}`, {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                }
            });
            
            if (!response.ok) {
                console.error('Error fetching comments:', response.status);
                return;
            }
            
            const comments = await response.json();
            
            setActivities(prevActivities => 
                prevActivities.map(activity => {
                    if (activity.id === activityId) {
                        return {
                            ...activity,
                            comments: comments
                        };
                    }
                    return activity;
                })
            );
        } catch (err) {
            console.error('Error fetching comments:', err);
        }
    };
    
    const handleCommentChange = (activityId: number, value: string) => {
        setActivities(prevActivities => 
            prevActivities.map(activity => {
                if (activity.id === activityId) {
                    return {
                        ...activity,
                        newComment: value
                    };
                }
                return activity;
            })
        );
    };
    
    const submitComment = async (activityId: number) => {
        const activity = activities.find(a => a.id === activityId);
        if (!activity || !activity.newComment || activity.newComment.trim() === '') return;
        
        try {
            const token = localStorage.getItem('token');
            
            if (!token) {
                setError('Musisz być zalogowany, aby dodać komentarz');
                return;
            }
            
            const response = await fetch(`http://localhost:8080/api/comments/activity/${activityId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ content: activity.newComment })
            });
            
            if (!response.ok) {
                console.error('Error adding comment:', response.status);
                return;
            }
            
            const newComment = await response.json();
            
            // Update activity with the new comment
            setActivities(prevActivities => 
                prevActivities.map(act => {
                    if (act.id === activityId) {
                        return {
                            ...act,
                            comments: [...(act.comments || []), newComment],
                            newComment: ''
                        };
                    }
                    return act;
                })
            );
        } catch (err) {
            console.error('Error adding comment:', err);
        }
    };
    
    const deleteComment = async (commentId: number, activityId: number) => {
        try {
            const token = localStorage.getItem('token');
            
            if (!token) return;
            
            const response = await fetch(`http://localhost:8080/api/comments/${commentId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                }
            });
            
            if (!response.ok) {
                console.error('Error deleting comment:', response.status);
                return;
            }
            
            // Remove the comment from the UI
            setActivities(prevActivities => 
                prevActivities.map(activity => {
                    if (activity.id === activityId) {
                        return {
                            ...activity,
                            comments: activity.comments?.filter(c => c.id !== commentId) || []
                        };
                    }
                    return activity;
                })
            );
        } catch (err) {
            console.error('Error deleting comment:', err);
        }
    };    if (loading && activities.length === 0) {
        return <div className="activity-feed">Ładowanie aktywności znajomych...</div>;
    }

    if (error && activities.length === 0) {
        return <div className="activity-feed error">{error}</div>;
    }

    if (activities.length === 0) {
        return (
            <div className="activity-feed empty">
                <h2>Tablica aktywności</h2>
                <p>Brak aktywności do wyświetlenia. Dodaj znajomych, aby zobaczyć ich aktywność.</p>
            </div>
        );
    }    return (
        <div className="activity-feed">
            <h2>Tablica aktywności</h2>
            <div className="activities-list">
                {activities.map((activity) => (
                    <div key={activity.id} className="activity-item">
                        <div className="activity-header">
                            <span className="activity-username">{activity.username}</span>
                            <span className="activity-time">{formatDate(activity.timestamp)}</span>
                        </div>
                        <div className="activity-content">
                            {getActivityText(activity)}
                            {activity.isFavorite && <span className="favorite-badge">❤️ Ulubione</span>}
                        </div>
                        <div className="activity-comments">
                            <button onClick={() => toggleComments(activity.id)} className="toggle-comments-button">
                                {activity.showComments ? 'Ukryj komentarze' : 'Pokaż komentarze'}
                            </button>
                            {activity.showComments && activity.comments && activity.comments.length > 0 && (
                                <div className="comments-list">
                                    {activity.comments.map(comment => (
                                        <div key={comment.id} className="comment-item">
                                            <div className="comment-header">
                                                <span className="comment-username">{comment.user.username}</span>
                                                <span className="comment-time">{formatDate(comment.createdAt)}</span>
                                            </div>
                                            <div className="comment-content">
                                                {comment.content}
                                            </div>
                                            {comment.user.id === (currentUser?.id) && (
                                                <button 
                                                    className="delete-comment-button" 
                                                    onClick={() => deleteComment(comment.id, activity.id)}
                                                >
                                                    Usuń
                                                </button>
                                            )}
                                        </div>
                                    ))}
                                </div>
                            )}
                            {activity.showComments && (
                                <div className="add-comment">
                                    <textarea 
                                        value={activity.newComment || ''} 
                                        onChange={e => handleCommentChange(activity.id, e.target.value)} 
                                        placeholder="Napisz komentarz..."
                                        className="new-comment-input"
                                    />
                                    <button 
                                        onClick={() => submitComment(activity.id)} 
                                        className="submit-comment-button"
                                    >
                                        Dodaj komentarz
                                    </button>
                                </div>
                            )}
                        </div>
                    </div>
                ))}
            </div>
            <div className="activity-feed-actions">
                <button className="refresh-button" onClick={() => fetchActivities(true)}>Odśwież</button>
                {hasMore && activities.length > 0 && (
                    <button 
                        className="load-more-button" 
                        onClick={() => fetchActivities(false)}
                        disabled={loading}
                    >
                        {loading ? 'Ładowanie...' : 'Załaduj więcej'}
                    </button>
                )}
            </div>
        </div>
    );
};

export default ActivityFeed;
