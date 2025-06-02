import React, { useState } from 'react';
import './App.css';
import NavBar from './components/NavBar';
import UserLibrary from './components/UserLibrary';
import ActivityFeed from './components/ActivityFeed';
import FriendsPage from './components/FriendsPage';

function App() {
  const [activeView, setActiveView] = useState<'home' | 'library' | 'friends'>('home');

  const handleMoviesClick = () => {
    setActiveView('home');
  };

  const handleLibraryClick = () => {
    setActiveView('library');
  };

  const handleFriendsClick = () => {
    setActiveView('friends');
  };
  return (
    <div className="App">
      <NavBar 
        onMoviesClick={handleMoviesClick} 
        onLibraryClick={handleLibraryClick} 
        onFriendsClick={handleFriendsClick} 
      />
      
      <div className="content">
        {activeView === 'home' && <ActivityFeed />}
        {activeView === 'library' && <UserLibrary />}
        {activeView === 'friends' && <FriendsPage />}
      </div>
    </div>
  );
}

export default App;