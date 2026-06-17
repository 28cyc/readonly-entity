package tw.teddysoft.aiscrum.product.entity;

import tw.teddysoft.aiscrum.common.entity.DateProvider;
import tw.teddysoft.ezddd.entity.EsAggregateRoot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static tw.teddysoft.ucontract.Contract.*;

public class Product extends EsAggregateRoot<ProductId, ProductEvents> {

    private ProductId id;
    private ProductName name;
    private ProductGoal goal;
    private String note;
    private String extension;
    private ProductLifecycleState state;

    public Product() {
    }

    public Product(String productId, String name, String userId) {
        requireNotNull("id", productId);
        requireNotNull("name", name);

        Map<String, String> metadata = new HashMap<>();
        if (userId != null) {
            metadata.put("userId", userId);
        }

        apply(new ProductEvents.ProductCreated(
                ProductId.valueOf(productId),
                ProductName.valueOf(name),
                null,
                null,
                null,
                ProductLifecycleState.DRAFT.name(),
                metadata,
                UUID.randomUUID(),
                DateProvider.now()
        ));

        ensure("id", () -> this.id != null);
        ensure("name", () -> this.name != null);
        ensure("state must be DRAFT", () -> this.state == ProductLifecycleState.DRAFT);
    }

    @Override
    public String getCategory() {
        return "Product";
    }

    @Override
    public ProductId getId() {
        return id;
    }

    @Override
    protected void when(ProductEvents event) {
        switch (event) {
            case ProductEvents.ProductCreated e -> {
                this.id = e.productId();
                this.name = e.name();
                this.goal = e.goal();
                this.note = e.note();
                this.extension = e.extension();
                this.state = ProductLifecycleState.valueOf(e.state());
            }
        }
    }

    public ProductName getName() { return name; }

    public ProductGoal getGoal() {
        return goal == null ? null : new ReadOnlyProductGoal(goal);
    }

    public String getNote() { return note; }

    public String getExtension() { return extension; }

    public ProductLifecycleState getState() { return state; }
}
