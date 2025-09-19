import React, { useState, useEffect } from 'react';
import './AppStyles.css';
import OrderList from './components/OrderList';
import OrderDetails from './components/OrderDetails';
import NotificationBell from './components/NotificationBell';
import NotificationList from './components/NotificationList';
import UserProfile from './components/UserProfile';
import CreateOrderModal from './components/CreateOrderModal';
import SignInModal from './components/SignInModal';
import { useAuth } from './context/AuthContext';
import { Package, Clock, Truck, CheckCircle, AlertCircle } from 'lucide-react';

const getStatusColor = (status) => {
  switch (status) {
    case 'PENDING': return { backgroundColor: '#fef3c7', color: '#92400e', border: '1px solid #fcd34d' };
    case 'IN_WAREHOUSE': return { backgroundColor: '#dbeafe', color: '#1e40af', border: '1px solid #93c5fd' };
    case 'IN_TRANSIT': return { backgroundColor: '#e0e7ff', color: '#3730a3', border: '1px solid #a5b4fc' };
    case 'DELIVERED': return { backgroundColor: '#dcfce7', color: '#166534', border: '1px solid #86efac' };
    case 'FAILED': return { backgroundColor: '#fee2e2', color: '#991b1b', border: '1px solid #fca5a5' };
    default: return { backgroundColor: '#f3f4f6', color: '#374151', border: '1px solid #d1d5db' };
  }
};

const getStatusIcon = (status) => {
  switch (status) {
    case 'PENDING': return <Clock size={16} />;
    case 'IN_WAREHOUSE': return <Package size={16} />;
    case 'IN_TRANSIT': return <Truck size={16} />;
    case 'DELIVERED': return <CheckCircle size={16} />;
    case 'FAILED': return <AlertCircle size={16} />;
    default: return <Package size={16} />;
  }
};

const formatStatus = (status) => {
  return status.replace('_', ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase());
};



const ClientPortal = () => {
  const { customer, isAuthenticated, getCustomerId } = useAuth();
  const [orders, setOrders] = useState([
    {
      id: 'ORD-12345',
      pickupAddress: 'Warehouse A, Colombo 01',
      deliveryAddress: '123 Galle Road, Colombo 03',
      status: 'IN_TRANSIT',
      estimatedDelivery: '2024-11-15 14:30',
      driverName: 'Kasun Perera',
      items: ['Electronics Package', 'Documents'],
      createdAt: '2024-11-15 09:00'
    },
    {
      id: 'ORD-12346',
      pickupAddress: 'Warehouse B, Kandy',
      deliveryAddress: '456 Peradeniya Road, Kandy',
      status: 'PENDING',
      estimatedDelivery: '2024-11-16 10:00',
      driverName: null,
      items: ['Medical Supplies'],
      createdAt: '2024-11-15 10:15'
    },
    {
      id: 'ORD-12347',
      pickupAddress: 'Warehouse A, Colombo 01',
      deliveryAddress: '789 Negombo Road, Negombo',
      status: 'DELIVERED',
      estimatedDelivery: '2024-11-14 16:00',
      driverName: 'Saman Silva',
      items: ['Food Package', 'Beverages'],
      createdAt: '2024-11-14 08:30'
    }
  ]);
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [notifications] = useState([
    { id: 1, message: 'Order ORD-12345 is now in transit', time: '10 minutes ago', read: false },
    { id: 2, message: 'Order ORD-12347 has been delivered', time: '1 day ago', read: true }
  ]);
  const [showNotifications, setShowNotifications] = useState(false);
  const [showCreateOrder, setShowCreateOrder] = useState(false);
  const [showSignIn, setShowSignIn] = useState(false);

  useEffect(() => {

  }, []);

  const handleCreateOrder = (orderData) => {
    const newOrder = {
      id: `ORD-${Math.floor(Math.random() * 100000)}`,
      pickupAddress: 'Warehouse A, Colombo 01',
      driverName: null,
      items: ['General Package'],
      createdAt: new Date().toISOString().slice(0, 16).replace('T', ' '),
      status: 'PENDING',
      estimatedDelivery: orderData.estimatedDelivery || '',
      customerId: getCustomerId() || undefined,
      ...orderData
    };
    setOrders([newOrder, ...orders]);
    setShowCreateOrder(false);
  };

  const unreadCount = notifications.filter(n => !n.read).length;

  return (
    <div className="container">
      <header className="header">
        <div className="header-content">
          <div className="logo">
            <Package size={32} />
            <span className="logo-text">Client Portal</span>
          </div>
          <div className="header-actions">
            <NotificationBell unreadCount={unreadCount} onClick={() => setShowNotifications(!showNotifications)} />
            {showNotifications && (
              <NotificationList notifications={notifications} onClose={() => setShowNotifications(false)} />
            )}
            {isAuthenticated ? (
              <UserProfile user={{ name: customer?.name || customer?.fullName || customer?.email || 'Customer' }} />
            ) : (
              <button className="create-order-btn" onClick={() => setShowSignIn(true)}>Sign In</button>
            )}
            <button className="create-order-btn" onClick={() => setShowCreateOrder(true)}>
              + Create Order
            </button>
          </div>
        </div>
      </header>
      <main style={{ maxWidth: 1280, margin: '0 auto', padding: 16, display: 'flex', gap: 32 }}>
        <div style={{ flex: 1 }}>
          <OrderList
            orders={orders}
            onSelect={setSelectedOrder}
            selectedOrder={selectedOrder}
            getStatusColor={getStatusColor}
            getStatusIcon={getStatusIcon}
            formatStatus={formatStatus}
          />
        </div>
        <div style={{ flex: 2 }}>
          <OrderDetails
            order={selectedOrder}
            getStatusColor={getStatusColor}
            getStatusIcon={getStatusIcon}
            formatStatus={formatStatus}
          />
        </div>
      </main>
      {showCreateOrder && (
        <CreateOrderModal
          onClose={() => setShowCreateOrder(false)}
          onCreate={handleCreateOrder}
          presaved={{
            pickupAddress: 'Warehouse A, Colombo 01',
            driverName: null,
            items: ['General Package'],
            status: 'PENDING',
            estimatedDelivery: '',
            createdAt: new Date().toISOString().slice(0, 16).replace('T', ' ')
          }}
        />
      )}
      {showSignIn && (
        <SignInModal onClose={() => setShowSignIn(false)} />
      )}
    </div>
  );
};

export default ClientPortal;
