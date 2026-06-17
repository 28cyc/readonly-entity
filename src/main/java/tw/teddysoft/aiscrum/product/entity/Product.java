package tw.teddysoft.aiscrum.product.entity;

import tw.teddysoft.aiscrum.common.entity.DateProvider;
import tw.teddysoft.ezddd.entity.EsAggregateRoot;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static tw.teddysoft.ucontract.Contract.*;

public class Product extends EsAggregateRoot<ProductId, ProductEvents> {

    public static final String CATEGORY = "Product";

    private ProductId id;
    private ProductName name;
    private ProductGoal goal;
    private String note;
    private String extension;
    private ProductLifecycleState state;

    public Product(List<ProductEvents> domainEvents) {
        super(domainEvents);
    }

    public Product(ProductId productId, ProductName productName) {
        super();

        requireNotNull("Product id", productId);
        requireNotNull("Product name", productName);

        apply(new ProductEvents.ProductCreated(
                productId,
                productName,
                null,
                null,
                null,
                ProductLifecycleState.DRAFT.name(),
                new HashMap<>(),
                UUID.randomUUID(),
                DateProvider.now()
        ));

        ensure("Product id is set correctly", () -> _idMatches(productId));
        ensure("Product name is set correctly", () -> _nameMatches(productName));
        ensure("Product state is DRAFT initially", this::_stateIsDraft);
        ensure("A ProductCreated event is generated correctly",
                () -> _productCreatedEventGenerated(productId, productName));
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

    @Override
    public String getCategory() {
        return CATEGORY;
    }

    @Override
    public ProductId getId() {
        return id;
    }

    public ProductName getName() { return name; }
    public ProductGoal getGoal() { return goal; }
    public String getNote() { return note; }
    public String getExtension() { return extension; }
    public ProductLifecycleState getState() { return state; }

    private boolean _idMatches(ProductId productId) {
        return productId.equals(this.id);
    }

    private boolean _nameMatches(ProductName productName) {
        return productName.equals(this.name);
    }

    private boolean _stateIsDraft() {
        return ProductLifecycleState.DRAFT == this.state;
    }

    private boolean _productCreatedEventGenerated(ProductId productId, ProductName productName) {
        if (getDomainEvents().isEmpty()) return false;
        var last = getDomainEvents().get(getDomainEvents().size() - 1);
        if (!(last instanceof ProductEvents.ProductCreated created)) return false;
        return created.productId().equals(productId)
                && created.name().equals(productName)
                && ProductLifecycleState.DRAFT.name().equals(created.state());
    }
}
