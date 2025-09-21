import React from 'react';
import '../AppStyles.css';

const StatusUpdateModal = ({ delivery, onInTransit, onComplete, onFail, onCancel }) => {
  if (!delivery) return null;
  return (
    <div className="modal">
      <div className="modal-content">
        <h3 className="modal-title">Update Status</h3>
        <div className="modal-buttons">
          <button onClick={onInTransit} className="modal-button modal-primary-button">
            Mark as In Transit
          </button>
          <button onClick={onComplete} className="modal-button modal-success-button">
            Mark as Completed
          </button>
          <button onClick={onFail} className="modal-button modal-danger-button">
            Mark as Failed
          </button>
          <button onClick={onCancel} className="modal-button modal-cancel-button">
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
};

export default StatusUpdateModal;

