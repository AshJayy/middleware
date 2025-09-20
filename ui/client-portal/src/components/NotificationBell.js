import React from 'react';
import { Bell } from 'lucide-react';

const NotificationBell = ({ unreadCount, onClick }) => (
  <button className="notification-bell" onClick={onClick} aria-label="Show notifications">
    <Bell size={24} />
    {unreadCount > 0 && <span className="notification-badge">{unreadCount}</span>}
  </button>
);

export default NotificationBell;

