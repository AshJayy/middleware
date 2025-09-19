import React, { useState } from 'react';

const initialState = (presaved) => ({
  deliveryAddress: '',
  city: '',
  postalCode: '',
  country: '',
  estimatedDelivery: '',
  ...presaved
});

const CreateOrderModal = ({ onClose, onCreate, presaved }) => {
  const [form, setForm] = useState(initialState(presaved));

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onCreate(form);
  };

  return (
    <div className="modal-overlay">
      <div className="modal">
        <h2 style={{marginTop:0,marginBottom:18,fontWeight:600,fontSize:22,color:'#1e293b'}}>Create New Order</h2>
        <form onSubmit={handleSubmit} className="create-order-form">
          <label>
            Delivery Address
            <input name="deliveryAddress" value={form.deliveryAddress} onChange={handleChange} required />
          </label>
          <label>
            City
            <input name="city" value={form.city} onChange={handleChange} required />
          </label>
          <label>
            Postal Code
            <input name="postalCode" value={form.postalCode} onChange={handleChange} required />
          </label>
          <label>
            Country
            <input name="country" value={form.country} onChange={handleChange} required />
          </label>
          <div className="modal-actions">
            <button type="button" onClick={onClose}>Cancel</button>
            <button type="submit">Create Order</button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CreateOrderModal;
