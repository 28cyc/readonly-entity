package tw.teddysoft.aiscrum.product.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tw.teddysoft.ucontract.PreconditionViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProductContractTest {

    private ProductId validId;
    private ProductName validName;

    @BeforeEach
    void setUp() {
        validId = ProductId.valueOf("product-001");
        validName = ProductName.valueOf("My Product");
    }

    @Nested
    class Constructor {

        @Test
        void reject_null_id() {
            assertThatThrownBy(() -> new Product(null, validName))
                    .isInstanceOf(PreconditionViolationException.class)
                    .hasMessageContaining("id");
        }

        @Test
        void reject_null_name() {
            assertThatThrownBy(() -> new Product(validId, null))
                    .isInstanceOf(PreconditionViolationException.class)
                    .hasMessageContaining("name");
        }

        @Test
        void set_id_correctly() {
            Product product = new Product(validId, validName);
            assertThat(product.getId()).isEqualTo(validId);
        }

        @Test
        void set_name_correctly() {
            Product product = new Product(validId, validName);
            assertThat(product.getName()).isEqualTo(validName);
        }

        @Test
        void set_state_to_DRAFT_initially() {
            Product product = new Product(validId, validName);
            assertThat(product.getState()).isEqualTo(ProductLifecycleState.DRAFT);
        }

        @Test
        void generate_ProductCreated_event() {
            Product product = new Product(validId, validName);
            assertThat(product.getDomainEvents()).hasSize(1);
            assertThat(product.getDomainEvents().get(0))
                    .isInstanceOf(ProductEvents.ProductCreated.class);
        }

        @Test
        void ProductCreated_event_contains_correct_id() {
            Product product = new Product(validId, validName);
            ProductEvents.ProductCreated event =
                    (ProductEvents.ProductCreated) product.getDomainEvents().get(0);
            assertThat(event.productId()).isEqualTo(validId);
        }

        @Test
        void ProductCreated_event_contains_correct_name() {
            Product product = new Product(validId, validName);
            ProductEvents.ProductCreated event =
                    (ProductEvents.ProductCreated) product.getDomainEvents().get(0);
            assertThat(event.name()).isEqualTo(validName);
        }

        @Test
        void ProductCreated_event_contains_DRAFT_state() {
            Product product = new Product(validId, validName);
            ProductEvents.ProductCreated event =
                    (ProductEvents.ProductCreated) product.getDomainEvents().get(0);
            assertThat(event.state()).isEqualTo(ProductLifecycleState.DRAFT.name());
        }
    }
}
