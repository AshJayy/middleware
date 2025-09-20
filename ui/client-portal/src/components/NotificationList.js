import React from 'react';

const NotificationList = ({ notifications, onClose }) => (
  <div className="notification-dropdown">
    <div className="notification-header">
      <span className="notification-title">Notifications</span>
      <button className="notification-close" onClick={onClose}>×</button>
    </div>
    <ul className="notification-list">
      {notifications.length === 0 && <li className="notification-item empty">No notifications</li>}
      {notifications.map(n => (
        <li key={n.id} className={`notification-item${n.read ? ' read' : ''}`}>
          <span>{n.message}</span>
          <span className="notification-time">{n.time}</span>
        </li>
      ))}
    </ul>
  </div>
);

export default NotificationList;

