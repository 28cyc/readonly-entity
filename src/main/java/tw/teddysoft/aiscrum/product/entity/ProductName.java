package tw.teddysoft.aiscrum.product.entity;

import tw.teddysoft.ezddd.entity.ValueObject;

import java.util.Objects;

public record ProductName(String name) implements ValueObject {

    public ProductName {
        Objects.requireNonNull(name, "ProductName cannot be null");
    }

    public static ProductName valueOf(String name) {
        return new ProductName(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
