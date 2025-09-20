import React from 'react';
import StatusBadge from './StatusBadge';

const OrderList = ({ orders, onSelect, selectedOrder, getStatusColor, getStatusIcon, formatStatus }) => (
  <div className="order-list">
    <h2>Orders</h2>
    <ul>
      {orders.map(order => (
        <li
          key={order.orderId}
          className={selectedOrder && selectedOrder.orderId === order.orderId ? 'selected' : ''}
          onClick={() => onSelect(order)}
        >
          <div className="order-list-item">
            <span className="order-id">{order.orderId?.toString().slice(0, 8)}</span>
            <StatusBadge status={order.status} getStatusColor={getStatusColor} getStatusIcon={getStatusIcon} formatStatus={formatStatus} />
          </div>
          <div className="order-address">{order.deliveryAddress}</div>
        </li>
      ))}
    </ul>
  </div>
);

export default OrderList;

