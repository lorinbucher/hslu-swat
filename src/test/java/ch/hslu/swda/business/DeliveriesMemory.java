package ch.hslu.swda.business;

import ch.hslu.swda.entities.Delivery;
import ch.hslu.swda.entities.DeliveryStatus;
import ch.hslu.swda.entities.WarehouseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory implementation of the deliveries used for testing.
 */
public final class DeliveriesMemory implements Deliveries {

    private final Map<Long, Delivery> deliveries = new HashMap<>();

    @Override
    public Delivery getById(final long branchId, final long orderNumber) {
        return branchId == 1 ? deliveries.get(orderNumber) : null;
    }

    @Override
    public List<Delivery> getAllByBranch(final long branchId, final DeliveryStatus status) {
        List<Delivery> result = new ArrayList<>(deliveries.values());
        if (status != null) {
            result = result.stream().filter(d -> d.status() == status).toList();
        }
        return branchId == 1 ? result : List.of();
    }

    @Override
    public List<WarehouseEntity<Delivery>> getAllByStatus(final DeliveryStatus status) {
        List<Delivery> result = new ArrayList<>(deliveries.values());
        if (status != null) {
            result = result.stream().filter(d -> d.status() == status).toList();
        }
        return result.stream().map(d -> new WarehouseEntity<>(1L, d)).toList();
    }

    @Override
    public Delivery create(final long branchId, final Delivery delivery) {
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
    public Delivery update(final long branchId, final long orderNumber, final Delivery delivery) {
        Delivery updated = null;
        if (branchId == 1 && deliveries.containsKey(orderNumber)) {
            updated = new Delivery(orderNumber, delivery.status(), delivery.articles());
            deliveries.put(orderNumber, updated);

        }
        return updated;
    }

    @Override
    public Delivery updateStatus(final long branchId, final long orderNumber, final DeliveryStatus status) {
        Delivery updated = null;
        if (branchId == 1) {
            Delivery exists = deliveries.get(orderNumber);
            if (exists != null) {
                Delivery delivery = new Delivery(orderNumber, status, exists.articles());
                updated = deliveries.put(orderNumber, delivery);
            }
        }
        return updated;
    }

    @Override
    public boolean delete(final long branchId, final long orderNumber) {
        if (branchId == 1) {
            deliveries.remove(orderNumber);
        }
        return branchId == 1;
    }
}
