package ch.hslu.swda.business;

import ch.hslu.swda.entities.Delivery;
import ch.hslu.swda.entities.DeliveryStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory implementation of the deliveries used for testing.
 */
public class DeliveriesMemory implements Deliveries {

    private final Map<Long, Delivery> deliveries = new HashMap<>();

    @Override
    public Delivery getById(long branchId, long orderNumber) {
        return branchId == 1 ? deliveries.get(orderNumber) : null;
    }

    @Override
    public List<Delivery> getAll(long branchId, DeliveryStatus status) {
        List<Delivery> result = new ArrayList<>(deliveries.values());
        if (status != null) {
            result = result.stream().filter(d -> d.status() == status).toList();
        }
        return branchId == 1 ? result : List.of();
    }

    @Override
    public Delivery create(long branchId, Delivery delivery) {
        Delivery created = null;
        if (branchId == 1) {
            if (!deliveries.containsKey(delivery.orderNumber())) {
                deliveries.put(delivery.orderNumber(), delivery);
                created = delivery;
            } else {
                created = deliveries.get(delivery.orderNumber());
            }
        }
        return created;
    }

    @Override
    public Delivery update(long branchId, long orderNumber, Delivery delivery) {
        Delivery updated = null;
        if (branchId == 1) {
            if (deliveries.containsKey(orderNumber)) {
                updated = new Delivery(orderNumber, delivery.status(), delivery.articles());
                deliveries.put(orderNumber, updated);
            }
        }
        return updated;
    }

    @Override
    public Delivery updateStatus(long branchId, long orderNumber, DeliveryStatus status) {
        Delivery updated = null;
        if (branchId == 1) {
            updated = deliveries.get(orderNumber);
            if (updated != null) {
                Delivery delivery = new Delivery(orderNumber, status, updated.articles());
                updated = deliveries.put(orderNumber, delivery);
            }
        }
        return updated;
    }

    @Override
    public boolean delete(long branchId, long orderNumber) {
        if (branchId == 1) {
            deliveries.remove(orderNumber);
        }
        return branchId == 1;
    }
}
