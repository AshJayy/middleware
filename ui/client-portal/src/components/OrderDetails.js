import React from 'react';
import StatusBadge from './StatusBadge';
import { MapPin, User, Clock, Package } from 'lucide-react';
import { orderEndpoints } from "../network/order";

const OrderDetails = ({ order, getStatusColor, getStatusIcon, formatStatus }) => {
  if (!order) return (
    <div className="order-details empty">
      <Package size={48} />
      <p>Select an order to view details</p>
    </div>
  );

  const handleTrack = async () => {
    await orderEndpoints.trackOrder(order.orderId);
  }
  return (
    <div className="order-details" key={order.orderId}>
      <h2>Order Details</h2>
      <div className="order-detail-row">
        <span className="order-detail-label">Order ID:</span> {order.orderId?.toString().slice(0, 8)}
      </div>
      <div className="order-detail-row">
        <span className="order-detail-label">Status:</span>
        <StatusBadge status={order.status} getStatusColor={getStatusColor} getStatusIcon={getStatusIcon} formatStatus={formatStatus} />
      </div>
      <div className="order-detail-row">
        <MapPin size={16} /> <span className="order-detail-label">Delivery Address:</span> {order.deliveryAddress}
      </div>
      <div className="order-detail-row">
        <MapPin size={16} /> <span className="order-detail-label">City:</span> {order.city}
      </div>
      <div className="order-detail-row">
        <User size={16} /> <span className="order-detail-label">Driver:</span> {order.driverName || 'Not assigned'}
      </div>
      <div className="order-detail-row">
        <Clock size={16} /> <span className="order-detail-label">ETA:</span> {order.estimatedDelivery}
      </div>
      {/*<div className="order-detail-row">*/}
      {/*  <span className="order-detail-label">Items:</span> {order.items.join(', ')}*/}
      {/*</div>*/}
      <div className="order-detail-row">
        <span className="order-detail-label">Created:</span> {order.createdAt}
      </div>
      <button className="create-order-btn" onClick={() => handleTrack(order.orderId)}>Track</button>
    </div>
  );
};

export default OrderDetails;

