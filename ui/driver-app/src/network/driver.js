import apiClient from './network';

/**
 * Fetches the assigned route and delivery manifest for a specific driver.
 * Corresponds to the driver's need to view their assigned route for the day.
 *
 * @param {string} driverId - The ID of the logged-in driver.
 * @returns {Promise<object>} The driver's route and manifest.
 */
export const getDriverRoute = (driverId) => {
    if (!driverId) {
        return Promise.reject(new Error('Driver ID is required.'));
    }
    return apiClient.get(`/drivers/${driverId}/route`);
};

/**
 * Updates the status of a specific delivery.
 * This is crucial for the driver to mark packages as "delivered" or "failed".
 *
 * @param {object} updateData - The data for the update.
 * @param {string} updateData.orderId - The ID of the order being updated.
 * @param {string} updateData.status - The new status (e.g., 'DELIVERED', 'FAILED').
 * @param {string} [updateData.reason] - The reason for failure, if applicable.
 * @param {string} [updateData.proofOfDelivery] - A URL to the signature or photo.
 * @returns {Promise<object>} The server's confirmation response.
 */
export const updateDeliveryStatus = (updateData) => {
    const { orderId, status, reason, proofOfDelivery } = updateData;

    if (!orderId || !status) {
        return Promise.reject(new Error('Order ID and status are required.'));
    }

    const payload = {
        orderId,
        status,
        reason,
        proofOfDelivery,
    };

    return apiClient.post('/drivers/deliveries/update', payload);
};