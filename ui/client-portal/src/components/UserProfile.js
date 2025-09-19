import React from 'react';
import { User, LogOut } from 'lucide-react';

const UserProfile = ({ user }) => (
  <div className="user-info">
    <User size={20} />
    <span>{user.name}</span>
    <LogOut size={20} style={{ cursor: 'pointer' }} title="Logout" />
  </div>
);

export default UserProfile;

