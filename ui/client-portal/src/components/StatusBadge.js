import React from 'react';

const StatusBadge = ({ status, getStatusColor, getStatusIcon, formatStatus }) => (
  <span style={{
    display: 'inline-flex',
    alignItems: 'center',
    gap: '4px',
    padding: '4px 8px',
    borderRadius: '12px',
    fontSize: '12px',
    fontWeight: 500,
    ...getStatusColor(status)
  }}>
    {getStatusIcon(status)} {formatStatus(status)}
  </span>
);

export default StatusBadge;

