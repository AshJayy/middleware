import React, { useEffect, useState } from 'react';

const initialState = () => ({
  deliveryAddress: '',
  city: '',
  postalCode: '',
  country: '',
  totalAmount: 10.0 // default before randomization
});

const CreateOrderModal = ({ onClose, onCreate }) => {
  const [form, setForm] = useState(initialState());

  useEffect(() => {
    // Set once on mount; ensure it's a number, not a string
    const rnd = Number((Math.random() * 95 + 5).toFixed(2));
    setForm(f => ({ ...f, totalAmount: rnd }));
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    await onCreate(form); // already has totalAmount
  };

  return (
    <div className="modal-overlay">
      <div className="modal">
        <h2 style={{ marginTop: 0, marginBottom: 18, fontWeight: 600, fontSize: 22, color: '#1e293b' }}>Create New
          Order</h2>
        <div className="create-order-form">
          <div>
            <span>Total Amount</span>
            <div style={{ fontWeight: 600, fontSize: 22, color: '#1e293b' }}>USD {form.totalAmount}</div>
          </div>
          <label>
            Delivery Address
            <input name="deliveryAddress" value={form.deliveryAddress} onChange={handleChange} required/>
          </label>
          <label>
            City
            <input name="city" value={form.city} onChange={handleChange} required/>
          </label>
          <label>
            Postal Code
            <input name="postalCode" value={form.postalCode} onChange={handleChange} required/>
          </label>
          <label>
            Country
            <input name="country" value={form.country} onChange={handleChange} required/>
          </label>
          <div className="modal-actions">
            <button type="button" onClick={onClose}>Cancel</button>
            <button type="button" onClick={handleSubmit}>Create Order</button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CreateOrderModal;