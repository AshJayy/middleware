import React, { useState } from 'react';
import { AuthProvider, useAuth } from './context/AuthContext';
import AuthPage from './components/AuthPage';
import NextDeliveryCard from './components/NextDeliveryCard';
import DeliveriesList from './components/DeliveriesList';
import CurrentLocationCard from './components/CurrentLocationCard';
import StatusUpdateModal from './components/StatusUpdateModal';
import { Truck, AlertTriangle } from 'lucide-react';
import './AppStyles.css';

const MainDriverApp = () => {
  const { driver, logout } = useAuth();
  const [currentLocation, setCurrentLocation] = useState('Colombo 03, Sri Lanka');
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

  const nextDelivery = deliveries.find(d => d.status === 'ASSIGNED');
  const completedCount = deliveries.filter(d => d.status === 'COMPLETED').length;
  const pendingCount = deliveries.filter(d => d.status === 'ASSIGNED').length;

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

  const handleUpdateLocation = () => {
    // Simulate location update
    setCurrentLocation('Updated Location, Sri Lanka');
    alert('Location updated!');
  };

  const handleRefresh = () => {
    // Simulate refresh
    alert('Deliveries refreshed!');
  };

  // Use driver details from auth context if available
  const driverDetails = driver || { name: 'Kasun Perera', id: 'DRV-001', phone: '+94 77 123 4567' };

  return (
    <div className="container">
      <header className="header">
        <div className="header-content">
          <div className="driver-info">
            <div className="truck-icon">
              <Truck size={24} />
            </div>
            <div>
              <h1 style={{ fontSize: '18px', fontWeight: '600', margin: 0 }}>Driver Dashboard</h1>
              <p style={{ color: '#bfdbfe', fontSize: '14px', margin: 0 }}>{driverDetails.driverName}</p>
            </div>
          </div>
          <div className="buttons">
            <button className="settings-button" onClick={logout} title="Sign Out">
              Sign Out
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
      <div className="main">
        <NextDeliveryCard
          delivery={nextDelivery}
          onStart={() => updateDeliveryStatus(nextDelivery?.id, 'IN_TRANSIT')}
          onComplete={() => updateDeliveryStatus(nextDelivery?.id, 'COMPLETED')}
        />
        <DeliveriesList
          deliveries={deliveries}
          onStart={id => updateDeliveryStatus(id, 'IN_TRANSIT')}
          onComplete={id => updateDeliveryStatus(id, 'COMPLETED')}
          onRefresh={handleRefresh}
        />
        <CurrentLocationCard
          currentLocation={currentLocation}
          onUpdateLocation={handleUpdateLocation}
        />
      </div>
      <StatusUpdateModal
        delivery={selectedDelivery}
        onInTransit={() => updateDeliveryStatus(selectedDelivery?.id, 'IN_TRANSIT')}
        onComplete={() => updateDeliveryStatus(selectedDelivery?.id, 'COMPLETED')}
        onFail={() => updateDeliveryStatus(selectedDelivery?.id, 'FAILED')}
        onCancel={() => setShowStatusUpdate(false)}
      />
    </div>
  );
};

const App = () => {
  const { isAuthenticated } = useAuth();
  return isAuthenticated ? <MainDriverApp /> : <AuthPage />;
};

const Root = () => (
  <AuthProvider>
    <App />
  </AuthProvider>
);

export default Root;
