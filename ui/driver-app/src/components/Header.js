import React from 'react';
import { Truck, Settings, AlertTriangle } from 'lucide-react';
import '../AppStyles.css';

const Header = ({ driver, completedCount, pendingCount, onEmergency }) => (
  <header className="header">
    <div className="header-content">
      <div className="driver-info">
        <div className="truck-icon">
          <Truck size={24} />
        </div>
        <div>
          <h1 className="header-title">Driver Dashboard</h1>
          <p className="header-subtitle">{driver.name} • ID: {driver.id}</p>
        </div>
      </div>
      <div className="buttons">
        <button onClick={onEmergency} className="sos-button">
          <AlertTriangle size={16} />
          SOS
        </button>
        <button className="settings-button">
          <Settings size={20} />
        </button>
      </div>
    </div>
    <div className="stats">
      <div className="stat-card">
        <div className="stat-number">{completedCount}</div>
        <div className="stat-label">Completed</div>
      </div>
      <div className="stat-card">
        <div className="stat-number">{pendingCount}</div>
        <div className="stat-label">Pending</div>
      </div>
      <div className="stat-card">
        <div className="stat-number">5.2</div>
        <div className="stat-label">Rating</div>
      </div>
    </div>
  </header>
);

export default Header;

