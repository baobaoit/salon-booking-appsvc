package com.hesmantech.salonbooking.helper.order;

import com.hesmantech.salonbooking.domain.OrderEntity;

import java.util.Collection;
import java.util.UUID;

public interface OrderHelper {
    OrderEntity findCustomerAndCreateOrder(UUID customerId, String customerNotes, UUID technicianId, Collection<UUID> serviceIds);

    OrderEntity assignServices(OrderEntity order, Collection<UUID> services);

    OrderEntity assignTechnician(UUID id, UUID technicianId);

    OrderEntity assignTechnician(OrderEntity order, UUID technicianId);
}
