import React, { useState, useEffect } from 'react';
import { Navigation, MapPin, Package, CheckCircle, Clock, Truck, User, Settings, RefreshCw, Phone, MessageSquare, AlertTriangle } from 'lucide-react';

const DriverApp = () => {
  const [driver] = useState({ name: 'Kasun Perera', id: 'DRV-001', phone: '+94 77 123 4567' });
  const [currentLocation] = useState('Colombo 03, Sri Lanka');
  const [deliveries, setDeliveries] = useState([
    {
      id: 'ORD-12345',
      customerId: 'CUST-001',
      customerName: 'John Doe',
      customerPhone: '+94 71 234 5678',
      pickupAddress: 'Warehouse A, Colombo 01',
      deliveryAddress: '123 Galle Road, Colombo 03',
      items: ['Electronics Package', 'Documents'],
      status: 'ASSIGNED',
      priority: 'HIGH',
      estimatedTime: '30 mins',
      distance: '5.2 km',
      notes: 'Handle with care - fragile items'
    },
    {
      id: 'ORD-12348',
      customerId: 'CUST-002',
      customerName: 'Priya Fernando',
      customerPhone: '+94 76 987 6543',
      pickupAddress: 'Warehouse A, Colombo 01',
      deliveryAddress: '456 Wellawatta Road, Colombo 06',
      items: ['Food Package'],
      status: 'ASSIGNED',
      priority: 'MEDIUM',
      estimatedTime: '45 mins',
      distance: '7.8 km',
      notes: 'Perishable goods - deliver ASAP'
    },
    {
      id: 'ORD-12349',
      customerId: 'CUST-003',
      customerName: 'Rajesh Kumar',
      customerPhone: '+94 70 555 7890',
      pickupAddress: 'Warehouse B, Dehiwala',
      deliveryAddress: '789 Galle Road, Mount Lavinia',
      items: ['Medical Supplies', 'Prescription'],
      status: 'COMPLETED',
      priority: 'HIGH',
      estimatedTime: 'Completed',
      distance: '3.1 km',
      notes: 'Delivered successfully at 14:30'
    }
  ]);

  const [selectedDelivery, setSelectedDelivery] = useState(null);
  const [showStatusUpdate, setShowStatusUpdate] = useState(false);

  // Get next delivery automatically
  const nextDelivery = deliveries.find(d => d.status === 'ASSIGNED');

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

  const updateDeliveryStatus = (orderId, newStatus) => {
    setDeliveries(prev => prev.map(delivery => 
      delivery.id === orderId 
        ? { ...delivery, status: newStatus }
        : delivery
    ));
    setShowStatusUpdate(false);
    setSelectedDelivery(null);
    
    // Simulate pushing update to backend/message broker
    console.log(`Status update sent: ${orderId} -> ${newStatus}`);
  };

  const handleEmergency = () => {
    alert('Emergency support contacted. Help is on the way!');
  };

  const completedCount = deliveries.filter(d => d.status === 'COMPLETED').length;
  const pendingCount = deliveries.filter(d => d.status === 'ASSIGNED').length;

  const styles = {
    container: {
      minHeight: '100vh',
      backgroundColor: '#f9fafb',
      fontFamily: 'Arial, sans-serif'
    },
    header: {
      backgroundColor: '#2563eb',
      color: 'white',
      boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.1)',
      padding: '16px'
    },
    headerContent: {
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      marginBottom: '16px'
    },
    driverInfo: {
      display: 'flex',
      alignItems: 'center',
      gap: '12px'
    },
    truckIcon: {
      backgroundColor: '#3b82f6',
      padding: '8px',
      borderRadius: '50%',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center'
    },
    buttons: {
      display: 'flex',
      gap: '8px'
    },
    sosButton: {
      backgroundColor: '#ef4444',
      color: 'white',
      border: 'none',
      padding: '8px 12px',
      borderRadius: '20px',
      cursor: 'pointer',
      fontSize: '14px',
      fontWeight: '500',
      display: 'flex',
      alignItems: 'center',
      gap: '4px'
    },
    settingsButton: {
      backgroundColor: '#3b82f6',
      color: 'white',
      border: 'none',
      padding: '8px',
      borderRadius: '50%',
      cursor: 'pointer'
    },
    stats: {
      display: 'grid',
      gridTemplateColumns: '1fr 1fr 1fr',
      gap: '16px',
      textAlign: 'center'
    },
    statCard: {
      backgroundColor: '#3b82f6',
      borderRadius: '8px',
      padding: '12px'
    },
    statNumber: {
      fontSize: '24px',
      fontWeight: 'bold'
    },
    statLabel: {
      color: '#bfdbfe',
      fontSize: '12px'
    },
    main: {
      padding: '24px 16px',
      display: 'flex',
      flexDirection: 'column',
      gap: '24px'
    },
    card: {
      backgroundColor: 'white',
      borderRadius: '12px',
      boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.1)',
      overflow: 'hidden'
    },
    nextDeliveryCard: {
      backgroundColor: 'white',
      borderRadius: '12px',
      boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.1)',
      borderLeft: '4px solid #2563eb',
      padding: '24px'
    },
    cardHeader: {
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      marginBottom: '16px'
    },
    sectionTitle: {
      fontSize: '18px',
      fontWeight: '600',
      color: '#111827',
      display: 'flex',
      alignItems: 'center',
      gap: '8px'
    },
    priorityBadge: {
      padding: '4px 12px',
      borderRadius: '20px',
      fontSize: '12px',
      fontWeight: '500'
    },
    deliveryDetails: {
      display: 'flex',
      flexDirection: 'column',
      gap: '12px'
    },
    addressRow: {
      display: 'flex',
      alignItems: 'flex-start',
      gap: '12px'
    },
    addressText: {
      fontSize: '14px',
      fontWeight: '500',
      color: '#111827'
    },
    addressSubText: {
      fontSize: '14px',
      color: '#6b7280'
    },
    infoRow: {
      display: 'flex',
      alignItems: 'center',
      gap: '16px',
      fontSize: '14px',
      color: '#6b7280'
    },
    infoItem: {
      display: 'flex',
      alignItems: 'center',
      gap: '4px'
    },
    customerRow: {
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between'
    },
    customerInfo: {
      fontSize: '14px',
      fontWeight: '500',
      color: '#111827'
    },
    customerPhone: {
      fontSize: '12px',
      color: '#6b7280'
    },
    contactButtons: {
      display: 'flex',
      gap: '8px'
    },
    contactButton: {
      border: 'none',
      padding: '8px',
      borderRadius: '50%',
      cursor: 'pointer',
      color: 'white'
    },
    phoneButton: {
      backgroundColor: '#10b981'
    },
    messageButton: {
      backgroundColor: '#3b82f6'
    },
    actionButtons: {
      marginTop: '16px',
      paddingTop: '16px',
      borderTop: '1px solid #e5e7eb',
      display: 'grid',
      gridTemplateColumns: '1fr 1fr',
      gap: '12px'
    },
    primaryButton: {
      backgroundColor: '#3b82f6',
      color: 'white',
      border: 'none',
      padding: '12px 16px',
      borderRadius: '8px',
      fontSize: '14px',
      fontWeight: '500',
      cursor: 'pointer',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      gap: '8px'
    },
    successButton: {
      backgroundColor: '#10b981',
      color: 'white',
      border: 'none',
      padding: '12px 16px',
      borderRadius: '8px',
      fontSize: '14px',
      fontWeight: '500',
      cursor: 'pointer',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      gap: '8px'
    },
    cardContent: {
      padding: '24px'
    },
    cardHeaderSection: {
      padding: '24px',
      borderBottom: '1px solid #e5e7eb'
    },
    deliveryList: {
      borderTop: 'none'
    },
    deliveryItem: {
      padding: '24px',
      borderBottom: '1px solid #e5e7eb'
    },
    deliveryItemHeader: {
      display: 'flex',
      alignItems: 'flex-start',
      justifyContent: 'space-between',
      marginBottom: '12px'
    },
    deliveryId: {
      fontWeight: '500',
      color: '#111827'
    },
    customerName: {
      fontSize: '14px',
      color: '#6b7280'
    },
    badgeContainer: {
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'flex-end',
      gap: '4px'
    },
    statusBadge: {
      padding: '4px 8px',
      borderRadius: '20px',
      fontSize: '12px',
      fontWeight: '500'
    },
    deliveryDetails2: {
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
    note: {
      backgroundColor: '#fefbf2',
      border: '1px solid #fcd34d',
      borderRadius: '4px',
      padding: '8px',
      marginTop: '8px'
    },
    noteText: {
      fontSize: '12px',
      color: '#92400e',
      fontWeight: '500'
    },
    deliveryActions: {
      marginTop: '16px',
      display: 'grid',
      gridTemplateColumns: '1fr 1fr',
      gap: '8px'
    },
    grayButton: {
      backgroundColor: '#f3f4f6',
      color: '#374151',
      border: 'none',
      padding: '8px 12px',
      borderRadius: '4px',
      fontSize: '14px',
      fontWeight: '500',
      cursor: 'pointer'
    },
    smallSuccessButton: {
      backgroundColor: '#10b981',
      color: 'white',
      border: 'none',
      padding: '8px 12px',
      borderRadius: '4px',
      fontSize: '14px',
      fontWeight: '500',
      cursor: 'pointer'
    },
    refreshButton: {
      color: '#3b82f6',
      border: 'none',
      background: 'none',
      padding: '4px',
      borderRadius: '50%',
      cursor: 'pointer'
    },
    locationButton: {
      backgroundColor: '#3b82f6',
      color: 'white',
      border: 'none',
      padding: '8px 16px',
      borderRadius: '8px',
      fontSize: '14px',
      fontWeight: '500',
      cursor: 'pointer',
      width: '100%',
      marginTop: '12px'
    },
    modal: {
      position: 'fixed',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      backgroundColor: 'rgba(0, 0, 0, 0.5)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '16px',
      zIndex: 50
    },
    modalContent: {
      backgroundColor: 'white',
      borderRadius: '12px',
      padding: '24px',
      width: '100%',
      maxWidth: '384px'
    },
    modalTitle: {
      fontSize: '18px',
      fontWeight: '600',
      marginBottom: '16px'
    },
    modalButtons: {
      display: 'flex',
      flexDirection: 'column',
      gap: '12px'
    },
    modalButton: {
      width: '100%',
      border: 'none',
      padding: '12px',
      borderRadius: '8px',
      fontWeight: '500',
      cursor: 'pointer'
    },
    modalPrimaryButton: {
      backgroundColor: '#3b82f6',
      color: 'white'
    },
    modalSuccessButton: {
      backgroundColor: '#10b981',
      color: 'white'
    },
    modalDangerButton: {
      backgroundColor: '#ef4444',
      color: 'white'
    },
    modalCancelButton: {
      backgroundColor: '#d1d5db',
      color: '#374151'
    }
  };

  return (
    <div style={styles.container}>
      {/* Mobile-optimized Header */}
      <header style={styles.header}>
        <div style={styles.headerContent}>
          <div style={styles.driverInfo}>
            <div style={styles.truckIcon}>
              <Truck size={24} />
            </div>
            <div>
              <h1 style={{ fontSize: '18px', fontWeight: '600', margin: 0 }}>Driver Dashboard</h1>
              <p style={{ color: '#bfdbfe', fontSize: '14px', margin: 0 }}>{driver.name} • ID: {driver.id}</p>
            </div>
          </div>
          <div style={styles.buttons}>
            <button 
              onClick={handleEmergency}
              style={styles.sosButton}
            >
              <AlertTriangle size={16} />
              SOS
            </button>
            <button style={styles.settingsButton}>
              <Settings size={20} />
            </button>
          </div>
        </div>
        
        {/* Stats */}
        <div style={styles.stats}>
          <div style={styles.statCard}>
            <div style={styles.statNumber}>{completedCount}</div>
            <div style={styles.statLabel}>Completed</div>
          </div>
          <div style={styles.statCard}>
            <div style={styles.statNumber}>{pendingCount}</div>
            <div style={styles.statLabel}>Pending</div>
          </div>
          <div style={styles.statCard}>
            <div style={styles.statNumber}>5.2</div>
            <div style={styles.statLabel}>Rating</div>
          </div>
        </div>
      </header>

      <div style={styles.main}>
        {/* Next Delivery Card */}
        {nextDelivery && (
          <div style={styles.nextDeliveryCard}>
            <div style={styles.cardHeader}>
              <h2 style={styles.sectionTitle}>
                <Navigation size={20} color="#2563eb" />
                Next Delivery
              </h2>
              <span style={{...styles.priorityBadge, ...getPriorityColor(nextDelivery.priority)}}>
                {nextDelivery.priority} PRIORITY
              </span>
            </div>
            
            <div style={styles.deliveryDetails}>
              <div style={styles.addressRow}>
                <MapPin size={20} color="#ef4444" style={{marginTop: '4px', flexShrink: 0}} />
                <div>
                  <p style={{...styles.addressText, margin: 0}}>Delivery Address</p>
                  <p style={{...styles.addressSubText, margin: 0}}>{nextDelivery.deliveryAddress}</p>
                </div>
              </div>
              
              <div style={styles.infoRow}>
                <span style={styles.infoItem}>
                  <Clock size={16} />
                  {nextDelivery.estimatedTime}
                </span>
                <span style={styles.infoItem}>
                  <Navigation size={16} />
                  {nextDelivery.distance}
                </span>
              </div>
              
              <div style={styles.customerRow}>
                <div>
                  <p style={{...styles.customerInfo, margin: 0}}>{nextDelivery.customerName}</p>
                  <p style={{...styles.customerPhone, margin: 0}}>{nextDelivery.customerPhone}</p>
                </div>
                <div style={styles.contactButtons}>
                  <button style={{...styles.contactButton, ...styles.phoneButton}}>
                    <Phone size={16} />
                  </button>
                  <button style={{...styles.contactButton, ...styles.messageButton}}>
                    <MessageSquare size={16} />
                  </button>
                </div>
              </div>
            </div>
            
            <div style={styles.actionButtons}>
              <button 
                onClick={() => updateDeliveryStatus(nextDelivery.id, 'IN_TRANSIT')}
                style={styles.primaryButton}
              >
                <Truck size={16} />
                Start Delivery
              </button>
              <button 
                onClick={() => updateDeliveryStatus(nextDelivery.id, 'COMPLETED')}
                style={styles.successButton}
              >
                <CheckCircle size={16} />
                Mark Complete
              </button>
            </div>
          </div>
        )}

        {/* All Deliveries */}
        <div style={styles.card}>
          <div style={styles.cardHeaderSection}>
            <div style={styles.cardHeader}>
              <h2 style={styles.sectionTitle}>Today's Deliveries</h2>
              <button style={styles.refreshButton}>
                <RefreshCw size={20} />
              </button>
            </div>
          </div>
          
          <div>
            {deliveries.map((delivery) => (
              <div key={delivery.id} style={styles.deliveryItem}>
                <div style={styles.deliveryItemHeader}>
                  <div>
                    <h3 style={{...styles.deliveryId, margin: 0}}>{delivery.id}</h3>
                    <p style={{...styles.customerName, margin: 0}}>{delivery.customerName}</p>
                  </div>
                  <div style={styles.badgeContainer}>
                    <span style={{...styles.statusBadge, ...getStatusColor(delivery.status)}}>
                      {delivery.status.replace('_', ' ')}
                    </span>
                    <span style={{...styles.statusBadge, ...getPriorityColor(delivery.priority)}}>
                      {delivery.priority}
                    </span>
                  </div>
                </div>
                
                <div style={styles.deliveryDetails2}>
                  <div style={styles.detailRow}>
                    <MapPin size={16} color="#9ca3af" />
                    <span>{delivery.deliveryAddress}</span>
                  </div>
                  
                  <div style={styles.detailRow}>
                    <Package size={16} color="#9ca3af" />
                    <span>{delivery.items.join(', ')}</span>
                  </div>
                  
                  {delivery.notes && (
                    <div style={styles.note}>
                      <p style={{...styles.noteText, margin: 0}}>Note: {delivery.notes}</p>
                    </div>
                  )}
                </div>
                
                {delivery.status === 'ASSIGNED' && (
                  <div style={styles.deliveryActions}>
                    <button 
                      onClick={() => updateDeliveryStatus(delivery.id, 'IN_TRANSIT')}
                      style={styles.grayButton}
                    >
                      Start
                    </button>
                    <button 
                      onClick={() => updateDeliveryStatus(delivery.id, 'COMPLETED')}
                      style={styles.smallSuccessButton}
                    >
                      Complete
                    </button>
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>

        {/* Current Location */}
        <div style={styles.card}>
          <div style={styles.cardContent}>
            <h3 style={{...styles.sectionTitle, marginBottom: '12px'}}>
              <MapPin size={20} color="#2563eb" />
              Current Location
            </h3>
            <p style={{color: '#6b7280', margin: 0}}>{currentLocation}</p>
            <button style={styles.locationButton}>
              Update Location
            </button>
          </div>
        </div>
      </div>

      {/* Status Update Modal */}
      {showStatusUpdate && selectedDelivery && (
        <div style={styles.modal}>
          <div style={styles.modalContent}>
            <h3 style={styles.modalTitle}>Update Status</h3>
            <div style={styles.modalButtons}>
              <button 
                onClick={() => updateDeliveryStatus(selectedDelivery.id, 'IN_TRANSIT')}
                style={{...styles.modalButton, ...styles.modalPrimaryButton}}
              >
                Mark as In Transit
              </button>
              <button 
                onClick={() => updateDeliveryStatus(selectedDelivery.id, 'COMPLETED')}
                style={{...styles.modalButton, ...styles.modalSuccessButton}}
              >
                Mark as Completed
              </button>
              <button 
                onClick={() => updateDeliveryStatus(selectedDelivery.id, 'FAILED')}
                style={{...styles.modalButton, ...styles.modalDangerButton}}
              >
                Mark as Failed
              </button>
              <button 
                onClick={() => setShowStatusUpdate(false)}
                style={{...styles.modalButton, ...styles.modalCancelButton}}
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default DriverApp;