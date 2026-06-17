package tw.teddysoft.aiscrum.product.entity;

import tw.teddysoft.ezddd.entity.ValueObject;

import java.util.Objects;
import java.util.UUID;

public record ProductId(String id) implements ValueObject {

    public ProductId {
        Objects.requireNonNull(id, "ProductId cannot be null");
    }

    public static ProductId valueOf(String id) {
        return new ProductId(id);
    }

    public static ProductId valueOf(UUID id) {
        return new ProductId(id.toString());
    }

    public static ProductId create() {
        return new ProductId(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return id;
    }
}
