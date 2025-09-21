import React from 'react';
import { MapPin } from 'lucide-react';
import '../AppStyles.css';

const CurrentLocationCard = ({ currentLocation, onUpdateLocation }) => (
  <div className="card">
    <div className="card-content">
      <h3 className="section-title">
        <MapPin size={20} color="#2563eb" />
        Current Location
      </h3>
      <p className="current-location-text">{currentLocation}</p>
      <button className="location-button" onClick={onUpdateLocation}>
        Update Location
      </button>
    </div>
  </div>
);

export default CurrentLocationCard;

