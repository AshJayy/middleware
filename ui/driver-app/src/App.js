import React, { useState } from 'react';
import './AppStyles.css';
import Header from './components/Header';
import NextDeliveryCard from './components/NextDeliveryCard';
import DeliveriesList from './components/DeliveriesList';
import CurrentLocationCard from './components/CurrentLocationCard';
import StatusUpdateModal from './components/StatusUpdateModal';

const DriverApp = () => {
  const [driver] = useState({ name: 'Kasun Perera', id: 'DRV-001', phone: '+94 77 123 4567' });
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

  // Get next delivery automatically
  const nextDelivery = deliveries.find(d => d.status === 'ASSIGNED');

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

  const handleStart = (orderId) => updateDeliveryStatus(orderId, 'IN_TRANSIT');
  const handleComplete = (orderId) => updateDeliveryStatus(orderId, 'COMPLETED');
  const handleFail = (orderId) => updateDeliveryStatus(orderId, 'FAILED');
  const handleRefresh = () => window.location.reload();
  const handleUpdateLocation = () => setCurrentLocation('Updated Location, Sri Lanka');

  return (
    <div className="container">
      <Header
        driver={driver}
        completedCount={completedCount}
        pendingCount={pendingCount}
        onEmergency={handleEmergency}
      />
      <div className="main">
        <NextDeliveryCard
          delivery={nextDelivery}
          onStart={handleStart}
          onComplete={handleComplete}
        />
        <DeliveriesList
          deliveries={deliveries}
          onStart={handleStart}
          onComplete={handleComplete}
          onRefresh={handleRefresh}
        />
        <CurrentLocationCard
          currentLocation={currentLocation}
          onUpdateLocation={handleUpdateLocation}
        />
      </div>
      <StatusUpdateModal
        delivery={selectedDelivery}
        onInTransit={() => selectedDelivery && updateDeliveryStatus(selectedDelivery.id, 'IN_TRANSIT')}
        onComplete={() => selectedDelivery && updateDeliveryStatus(selectedDelivery.id, 'COMPLETED')}
        onFail={() => selectedDelivery && updateDeliveryStatus(selectedDelivery.id, 'FAILED')}
        onCancel={() => setShowStatusUpdate(false)}
      />
    </div>
  );
};

export default DriverApp;

