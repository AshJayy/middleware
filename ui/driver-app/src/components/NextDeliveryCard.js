import React from 'react';
import { Navigation, MapPin, Clock, Phone, MessageSquare, Truck, CheckCircle } from 'lucide-react';
import '../AppStyles.css';

const NextDeliveryCard = ({ delivery, onStart, onComplete }) => {
  if (!delivery) return null;
  const getPriorityColor = (priority) => {
    switch (priority) {
      case 'HIGH': return { backgroundColor: '#fee2e2', color: '#991b1b' };
      case 'MEDIUM': return { backgroundColor: '#fef3c7', color: '#92400e' };
      case 'LOW': return { backgroundColor: '#dcfce7', color: '#166534' };
      default: return { backgroundColor: '#f3f4f6', color: '#374151' };
    }
  };
  return (
    <div className="next-delivery-card">
      <div className="card-header">
        <h2 className="section-title">
          <Navigation size={20} color="#2563eb" />
          Next Delivery
        </h2>
        <span className="priority-badge" style={getPriorityColor(delivery.priority)}>
          {delivery.priority} PRIORITY
        </span>
      </div>
      <div className="delivery-details">
        <div className="address-row">
          <MapPin size={20} color="#ef4444" style={{ marginTop: '4px', flexShrink: 0 }} />
          <div>
            <p className="address-text" style={{ margin: 0 }}>Delivery Address</p>
            <p className="address-subtext" style={{ margin: 0 }}>{delivery.deliveryAddress}</p>
          </div>
        </div>
        <div className="info-row">
          <span className="info-item">
            <Clock size={16} />
            {delivery.estimatedTime}
          </span>
          <span className="info-item">
            <Navigation size={16} />
            {delivery.distance}
          </span>
        </div>
        <div className="customer-row">
          <div>
            <p className="customer-info" style={{ margin: 0 }}>{delivery.customerName}</p>
            <p className="customer-phone" style={{ margin: 0 }}>{delivery.customerPhone}</p>
          </div>
          <div className="contact-buttons">
            <button className="contact-button phone-button">
              <Phone size={16} />
            </button>
            <button className="contact-button message-button">
              <MessageSquare size={16} />
            </button>
          </div>
        </div>
      </div>
      <div className="action-buttons">
        <button onClick={() => onStart(delivery.id)} className="primary-button">
          <Truck size={16} />
          Start Delivery
        </button>
        <button onClick={() => onComplete(delivery.id)} className="success-button">
          <CheckCircle size={16} />
          Mark Complete
        </button>
      </div>
    </div>
  );
};

export default NextDeliveryCard;

