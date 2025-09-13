import React, { useState, useEffect } from 'react';
import { Package, MapPin, Clock, Bell, User, LogOut, Truck, CheckCircle, AlertCircle, RefreshCw } from 'lucide-react';

const ClientPortal = () => {
  const [user, setUser] = useState({ name: 'John Doe', email: 'john@example.com' });
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
  const [notifications, setNotifications] = useState([
    { id: 1, message: 'Order ORD-12345 is now in transit', time: '10 minutes ago', read: false },
    { id: 2, message: 'Order ORD-12347 has been delivered', time: '1 day ago', read: true }
  ]);
  const [showNotifications, setShowNotifications] = useState(false);

  // Simulate real-time updates
  useEffect(() => {
    const interval = setInterval(() => {
      // Simulate receiving updates from message broker (RabbitMQ/Kafka)
      setOrders(prev => prev.map(order => {
        if (order.id === 'ORD-12345' && order.status === 'IN_TRANSIT') {
          // Simulate random status updates
          const random = Math.random();
          if (random > 0.98) {
            return { ...order, status: 'DELIVERED' };
          }
        }
        return order;
      }));
    }, 5000);

    return () => clearInterval(interval);
  }, []);

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

  const unreadCount = notifications.filter(n => !n.read).length;

  const styles = {
    container: {
      minHeight: '100vh',
      backgroundColor: '#f9fafb',
      fontFamily: 'Arial, sans-serif'
    },
    header: {
      backgroundColor: 'white',
      boxShadow: '0 1px 3px 0 rgba(0, 0, 0, 0.1)',
      borderBottom: '1px solid #e5e7eb'
    },
    headerContent: {
      maxWidth: '1280px',
      margin: '0 auto',
      padding: '0 16px',
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center',
      height: '64px'
    },
    logo: {
      display: 'flex',
      alignItems: 'center',
      gap: '16px'
    },
    logoText: {
      fontSize: '20px',
      fontWeight: '600',
      color: '#111827'
    },
    headerActions: {
      display: 'flex',
      alignItems: 'center',
      gap: '16px'
    },
    notificationButton: {
      position: 'relative',
      padding: '8px',
      color: '#6b7280',
      border: 'none',
      background: 'none',
      borderRadius: '50%',
      cursor: 'pointer'
    },
    notificationBadge: {
      position: 'absolute',
      top: '-4px',
      right: '-4px',
      height: '20px',
      width: '20px',
      backgroundColor: '#ef4444',
      color: 'white',
      fontSize: '12px',
      borderRadius: '50%',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center'
    },
    notificationDropdown: {
      position: 'absolute',
      right: 0,
      marginTop: '8px',
      width: '320px',
      backgroundColor: 'white',
      borderRadius: '6px',
      boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.1)',
      padding: '4px',
      zIndex: 50,
      border: '1px solid #e5e7eb'
    },
    notificationHeader: {
      padding: '16px',
      borderBottom: '1px solid #e5e7eb'
    },
    notificationTitle: {
      fontSize: '14px',
      fontWeight: '500',
      color: '#111827'
    },
    notificationItem: {
      padding: '12px 16px',
      cursor: 'pointer'
    },
    userInfo: {
      display: 'flex',
      alignItems: 'center',
      gap: '8px'
    },
    userText: {
      fontSize: '14px',
      color: '#374151'
    },
    logoutButton: {
      padding: '8px',
      color: '#6b7280',
      border: 'none',
      background: 'none',
      borderRadius: '50%',
      cursor: 'pointer'
    },
    main: {
      maxWidth: '1280px',
      margin: '0 auto',
      padding: '32px 16px'
    },
    grid: {
      display: 'grid',
      gridTemplateColumns: '2fr 1fr',
      gap: '32px'
    },
    ordersSection: {
      backgroundColor: 'white',
      borderRadius: '8px',
      boxShadow: '0 1px 3px 0 rgba(0, 0, 0, 0.1)'
    },
    sectionHeader: {
      padding: '24px',
      borderBottom: '1px solid #e5e7eb',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between'
    },
    sectionTitle: {
      fontSize: '18px',
      fontWeight: '500',
      color: '#111827'
    },
    refreshButton: {
      padding: '8px',
      color: '#6b7280',
      border: 'none',
      background: 'none',
      borderRadius: '50%',
      cursor: 'pointer'
    },
    ordersList: {
      borderTop: 'none'
    },
    orderItem: {
      padding: '24px',
      borderBottom: '1px solid #e5e7eb',
      cursor: 'pointer',
      transition: 'background-color 0.2s'
    },
    orderItemSelected: {
      backgroundColor: '#eff6ff',
      borderLeft: '4px solid #3b82f6'
    },
    orderHeader: {
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      marginBottom: '8px'
    },
    orderId: {
      fontSize: '14px',
      fontWeight: '500',
      color: '#111827'
    },
    statusBadge: {
      display: 'inline-flex',
      alignItems: 'center',
      gap: '4px',
      padding: '4px 10px',
      borderRadius: '20px',
      fontSize: '12px',
      fontWeight: '500'
    },
    orderDetails: {
      display: 'flex',
      flexDirection: 'column',
      gap: '8px',
      fontSize: '14px',
      color: '#6b7280'
    },
    detailRow: {
      display: 'flex',
      alignItems: 'center',
      gap: '8px'
    },
    detailsPanel: {
      backgroundColor: 'white',
      borderRadius: '8px',
      boxShadow: '0 1px 3px 0 rgba(0, 0, 0, 0.1)'
    },
    detailsPanelContent: {
      padding: '24px',
      display: 'flex',
      flexDirection: 'column',
      gap: '24px'
    },
    detailSection: {
      display: 'flex',
      flexDirection: 'column',
      gap: '8px'
    },
    detailLabel: {
      fontSize: '14px',
      fontWeight: '500',
      color: '#111827'
    },
    detailValue: {
      fontSize: '14px',
      color: '#6b7280'
    },
    itemsList: {
      fontSize: '14px',
      color: '#6b7280',
      display: 'flex',
      flexDirection: 'column',
      gap: '4px'
    },
    itemRow: {
      display: 'flex',
      alignItems: 'center',
      gap: '8px'
    },
    emptyState: {
      padding: '24px',
      textAlign: 'center',
      color: '#6b7280'
    },
    emptyIcon: {
      width: '48px',
      height: '48px',
      margin: '0 auto 16px',
      color: '#d1d5db'
    }
  };

  return (
    <div style={styles.container}>
      {/* Header */}
      <header style={styles.header}>
        <div style={styles.headerContent}>
          <div style={styles.logo}>
            <Package size={32} color="#2563eb" />
            <h1 style={styles.logoText}>DeliveryPro Portal</h1>
          </div>
          
          <div style={styles.headerActions}>
            <div style={{ position: 'relative' }}>
              <button 
                onClick={() => setShowNotifications(!showNotifications)}
                style={styles.notificationButton}
              >
                <Bell size={24} />
                {unreadCount > 0 && (
                  <span style={styles.notificationBadge}>
                    {unreadCount}
                  </span>
                )}
              </button>
              
              {showNotifications && (
                <div style={styles.notificationDropdown}>
                  <div style={styles.notificationHeader}>
                    <h3 style={styles.notificationTitle}>Notifications</h3>
                  </div>
                  {notifications.map(notif => (
                    <div key={notif.id} style={{
                      ...styles.notificationItem,
                      backgroundColor: !notif.read ? '#eff6ff' : 'transparent'
                    }}>
                      <p style={{ fontSize: '14px', color: '#111827', margin: 0 }}>{notif.message}</p>
                      <p style={{ fontSize: '12px', color: '#6b7280', margin: '4px 0 0 0' }}>{notif.time}</p>
                    </div>
                  ))}
                </div>
              )}
            </div>
            
            <div style={styles.userInfo}>
              <User size={24} color="#6b7280" />
              <span style={styles.userText}>{user.name}</span>
            </div>
            
            <button style={styles.logoutButton}>
              <LogOut size={20} />
            </button>
          </div>
        </div>
      </header>

      <div style={styles.main}>
        <div style={styles.grid}>
          {/* Orders List */}
          <div style={styles.ordersSection}>
            <div style={styles.sectionHeader}>
              <h2 style={styles.sectionTitle}>My Orders</h2>
              <button style={styles.refreshButton}>
                <RefreshCw size={16} />
              </button>
            </div>
            
            <div style={styles.ordersList}>
              {orders.map((order) => (
                <div 
                  key={order.id} 
                  style={{
                    ...styles.orderItem,
                    ...(selectedOrder?.id === order.id ? styles.orderItemSelected : {})
                  }}
                  onClick={() => setSelectedOrder(order)}
                  onMouseEnter={(e) => e.target.style.backgroundColor = '#f9fafb'}
                  onMouseLeave={(e) => e.target.style.backgroundColor = selectedOrder?.id === order.id ? '#eff6ff' : 'transparent'}
                >
                  <div style={styles.orderHeader}>
                    <h3 style={styles.orderId}>{order.id}</h3>
                    <span style={{...styles.statusBadge, ...getStatusColor(order.status)}}>
                      {getStatusIcon(order.status)}
                      <span>{formatStatus(order.status)}</span>
                    </span>
                  </div>
                  
                  <div style={styles.orderDetails}>
                    <div style={styles.detailRow}>
                      <MapPin size={16} color="#9ca3af" />
                      <span>{order.deliveryAddress}</span>
                    </div>
                    <div style={styles.detailRow}>
                      <Clock size={16} color="#9ca3af" />
                      <span>Est. Delivery: {new Date(order.estimatedDelivery).toLocaleString()}</span>
                    </div>
                    {order.driverName && (
                      <div style={styles.detailRow}>
                        <Truck size={16} color="#9ca3af" />
                        <span>Driver: {order.driverName}</span>
                      </div>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Order Details */}
          <div style={styles.detailsPanel}>
            <div style={styles.sectionHeader}>
              <h2 style={styles.sectionTitle}>
                {selectedOrder ? 'Order Details' : 'Select an Order'}
              </h2>
            </div>
            
            {selectedOrder ? (
              <div style={styles.detailsPanelContent}>
                <div style={styles.detailSection}>
                  <h3 style={styles.detailLabel}>Status</h3>
                  <span style={{...styles.statusBadge, ...getStatusColor(selectedOrder.status)}}>
                    {getStatusIcon(selectedOrder.status)}
                    <span style={{ marginLeft: '8px' }}>{formatStatus(selectedOrder.status)}</span>
                  </span>
                </div>
                
                <div style={styles.detailSection}>
                  <h3 style={styles.detailLabel}>Pickup Address</h3>
                  <p style={{...styles.detailValue, margin: 0}}>{selectedOrder.pickupAddress}</p>
                </div>
                
                <div style={styles.detailSection}>
                  <h3 style={styles.detailLabel}>Delivery Address</h3>
                  <p style={{...styles.detailValue, margin: 0}}>{selectedOrder.deliveryAddress}</p>
                </div>
                
                <div style={styles.detailSection}>
                  <h3 style={styles.detailLabel}>Items</h3>
                  <div style={styles.itemsList}>
                    {selectedOrder.items.map((item, idx) => (
                      <div key={idx} style={styles.itemRow}>
                        <Package size={12} color="#9ca3af" />
                        {item}
                      </div>
                    ))}
                  </div>
                </div>
                
                <div style={styles.detailSection}>
                  <h3 style={styles.detailLabel}>Estimated Delivery</h3>
                  <p style={{...styles.detailValue, margin: 0}}>{new Date(selectedOrder.estimatedDelivery).toLocaleString()}</p>
                </div>
                
                {selectedOrder.driverName && (
                  <div style={styles.detailSection}>
                    <h3 style={styles.detailLabel}>Assigned Driver</h3>
                    <p style={{...styles.detailValue, margin: 0}}>{selectedOrder.driverName}</p>
                  </div>
                )}
                
                <div style={styles.detailSection}>
                  <h3 style={styles.detailLabel}>Order Created</h3>
                  <p style={{...styles.detailValue, margin: 0}}>{new Date(selectedOrder.createdAt).toLocaleString()}</p>
                </div>
              </div>
            ) : (
              <div style={styles.emptyState}>
                <Package style={styles.emptyIcon} />
                <p>Click on an order to view details</p>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ClientPortal;