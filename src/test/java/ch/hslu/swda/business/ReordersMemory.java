package ch.hslu.swda.business;

import ch.hslu.swda.entities.Reorder;
import ch.hslu.swda.entities.ReorderStatus;
import ch.hslu.swda.entities.WarehouseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * In-memory implementation of the reorders used for testing.
 */
public class ReordersMemory implements Reorders {

    private final Map<Long, Reorder> reorders = new HashMap<>();

    @Override
    public Reorder getById(long branchId, long orderNumber) {
        return branchId == 1 ? reorders.get(orderNumber) : null;
    }

    @Override
    public List<Reorder> getAllByBranch(long branchId, ReorderStatus status) {
        List<Reorder> result = new ArrayList<>(reorders.values());
        if (status != null) {
            result = result.stream().filter(r -> r.status() == status).toList();
        }
        return branchId == 1 ? result : List.of();
    }

    @Override
    public List<WarehouseEntity<Reorder>> getAllByStatus(ReorderStatus status) {
        List<Reorder> result = new ArrayList<>(reorders.values());
        if (status != null) {
            result = result.stream().filter(r -> r.status() == status).toList();
        }
        return result.stream().map(r -> new WarehouseEntity<>(1L, r)).toList();
    }

    @Override
    public Reorder create(long branchId, long articleId, int quantity) {
        Reorder created = null;
        if (branchId == 1) {
            long reorderId = reorders.size() + 1;
            while (reorders.containsKey(reorderId)) {
                reorderId++;
            }
            created = reorders.put(reorderId, new Reorder(reorderId, ReorderStatus.NEW, "", articleId, quantity));
        }
        return created;
    }

    @Override
    public Reorder updateStatus(long branchId, long reorderId, ReorderStatus status) {
        Reorder updated = null;
        if (branchId == 1) {
            Reorder exists = reorders.get(reorderId);
            if (exists != null) {
                Reorder reorder = new Reorder(reorderId, status, exists.date(), exists.articleId(), exists.quantity());
                updated = reorders.put(reorderId, reorder);
            }
        }
        return updated;
    }

    @Override
    public Reorder updateQuantity(long branchId, long reorderId, int quantity) {
        Reorder updated = null;
        if (branchId == 1) {
            Reorder exists = reorders.get(reorderId);
            if (exists != null) {
                Reorder reorder = new Reorder(reorderId, exists.status(), exists.date(), exists.articleId(), quantity);
                updated = reorders.put(reorderId, reorder);
            }
        }
        return updated;
    }

    @Override
    public boolean delete(long branchId, long reorderId) {
        if (branchId == 1) {
            reorders.remove(reorderId);
        }
        return branchId == 1;
    }

    @Override
    public int countReorderedArticles(long branchId, long articleId) {
        int count = 0;
        if (branchId == 1) {
            count = reorders.values().stream()
                    .filter(reorder -> reorder.articleId() == articleId)
                    .filter(reorder -> reorder.status() != ReorderStatus.COMPLETED)
                    .mapToInt(Reorder::quantity)
                    .sum();
        }
        return count;
    }
}
