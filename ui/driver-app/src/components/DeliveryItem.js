import React from 'react';
import { MapPin, Package } from 'lucide-react';
import '../AppStyles.css';

const DeliveryItem = ({ delivery, onStart, onComplete }) => {
  const getStatusColor = (status) => {
    switch (status) {
      case 'ASSIGNED': return { backgroundColor: '#dbeafe', color: '#1e40af', border: '1px solid #93c5fd' };
      case 'IN_TRANSIT': return { backgroundColor: '#e0e7ff', color: '#3730a3', border: '1px solid #a5b4fc' };
      case 'COMPLETED': return { backgroundColor: '#dcfce7', color: '#166534', border: '1px solid #86efac' };
      case 'FAILED': return { backgroundColor: '#fee2e2', color: '#991b1b', border: '1px solid #fca5a5' };
      default: return { backgroundColor: '#f3f4f6', color: '#374151', border: '1px solid #d1d5db' };
    }
  };
  const getPriorityColor = (priority) => {
    switch (priority) {
      case 'HIGH': return { backgroundColor: '#fee2e2', color: '#991b1b' };
      case 'MEDIUM': return { backgroundColor: '#fef3c7', color: '#92400e' };
      case 'LOW': return { backgroundColor: '#dcfce7', color: '#166534' };
      default: return { backgroundColor: '#f3f4f6', color: '#374151' };
    }
  };
  return (
    <div className="delivery-item">
      <div className="delivery-item-header">
        <div>
          <h3 className="delivery-id" style={{ margin: 0 }}>{delivery.id}</h3>
          <p className="customer-name" style={{ margin: 0 }}>{delivery.customerName}</p>
        </div>
        <div className="badge-container">
          <span className="status-badge" style={getStatusColor(delivery.status)}>
            {delivery.status.replace('_', ' ')}
          </span>
          <span className="status-badge" style={getPriorityColor(delivery.priority)}>
            {delivery.priority}
          </span>
        </div>
      </div>
      <div className="delivery-details2">
        <div className="detail-row">
          <MapPin size={16} color="#9ca3af" />
          <span>{delivery.deliveryAddress}</span>
        </div>
        <div className="detail-row">
          <Package size={16} color="#9ca3af" />
          <span>{delivery.items.join(', ')}</span>
        </div>
        {delivery.notes && (
          <div className="note">
            <p className="note-text" style={{ margin: 0 }}>Note: {delivery.notes}</p>
          </div>
        )}
      </div>
      {delivery.status === 'ASSIGNED' && (
        <div className="delivery-actions">
          <button onClick={() => onStart(delivery.id)} className="gray-button">
            Start
          </button>
          <button onClick={() => onComplete(delivery.id)} className="small-success-button">
            Complete
          </button>
        </div>
      )}
    </div>
  );
};

export default DeliveryItem;

