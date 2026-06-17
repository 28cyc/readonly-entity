package tw.teddysoft.aiscrum.product.entity;

import tw.teddysoft.ezddd.entity.ValueObject;

import java.util.Objects;
import java.util.UUID;

public record ProductGoalId(String id) implements ValueObject {

    public ProductGoalId {
        Objects.requireNonNull(id, "ProductGoalId cannot be null");
    }

    public static ProductGoalId valueOf(String id) {
        return new ProductGoalId(id);
    }

    public static ProductGoalId valueOf(UUID id) {
        return new ProductGoalId(id.toString());
    }

    public static ProductGoalId create() {
        return new ProductGoalId(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return id;
    }
}
