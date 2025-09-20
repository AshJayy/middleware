import React from 'react';
import { MapPin } from 'lucide-react';
import '../AppStyles.css';

const CurrentLocationCard = ({ currentLocation, onUpdateLocation }) => (
  <div className="card">
    <div className="card-content">
      <h3 className="section-title" style={{ marginBottom: '12px' }}>
        <MapPin size={20} color="#2563eb" />
        Current Location
      </h3>
      <p style={{ color: '#6b7280', margin: 0 }}>{currentLocation}</p>
      <button className="location-button" onClick={onUpdateLocation}>
        Update Location
      </button>
    </div>
  </div>
);

export default CurrentLocationCard;

