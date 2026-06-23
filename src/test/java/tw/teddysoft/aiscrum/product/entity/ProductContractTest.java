package tw.teddysoft.aiscrum.product.entity;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tw.teddysoft.aiscrum.common.entity.DateProvider;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

public class ProductContractTest {

    private static final String PRODUCT_ID = "product-123";
    private static final String PRODUCT_NAME = "My Scrum Product";
    private static final String USER_ID = "user-001";

    @Nested
    class WhenCreatingProduct {

        @Test
        void create_product_with_valid_inputs() {
            Product product = new Product(PRODUCT_ID, PRODUCT_NAME, USER_ID);

            assertEquals(PRODUCT_ID, product.getId().value());
            assertEquals(PRODUCT_NAME, product.getName().value());
            assertEquals(ProductLifecycleState.DRAFT, product.getState());
            assertNull(product.getGoal());
        }

        @Test
        void reject_null_id() {
            assertThatThrownBy(() -> new Product(null, PRODUCT_NAME, USER_ID))
                    .hasMessageContaining("id");
        }

        @Test
        void reject_null_name() {
            assertThatThrownBy(() -> new Product(PRODUCT_ID, null, USER_ID))
                    .hasMessageContaining("name");
        }

        @Test
        void product_created_event_is_generated() {
            Product product = new Product(PRODUCT_ID, PRODUCT_NAME, USER_ID);
            _productCreatedEventGenerated(product);
        }

        @Test
        void product_goal_should_be_read_only() {
            ProductGoal goal = new ProductGoal(
                    ProductGoalId.valueOf("goal-001"),
                    "original title",
                    "description",
                    List.of(),
                    DateProvider.now(),
                    null,
                    ProductGoalState.PLANNED
            );

            Product product = new Product();

            product.when(new ProductEvents.ProductCreated(
                    ProductId.valueOf(PRODUCT_ID),
                    ProductName.valueOf(PRODUCT_NAME),
                    goal,
                    null,
                    null,
                    ProductLifecycleState.DRAFT.name(),
                    Map.of(),
                    UUID.randomUUID(),
                    DateProvider.now()
            ));

            ProductGoal productGoal = product.getGoal();

            assertThat(productGoal).isInstanceOf(ReadOnlyProductGoal.class);
            assertThat(productGoal.getTitle()).isEqualTo("original title");

            assertThatThrownBy(() -> productGoal.updateTitle("new title"))
                    .isInstanceOf(UnsupportedOperationException.class)
                    .hasMessage("ProductGoal is read-only");
        }
    }

    private void _productCreatedEventGenerated(Product product) {
        assertFalse(product.getDomainEvents().isEmpty(),
                "Expected ProductCreated event to be generated");

        Object lastEvent = product.getDomainEvents().getLast();
        assertInstanceOf(ProductEvents.ProductCreated.class, lastEvent);

        ProductEvents.ProductCreated event = (ProductEvents.ProductCreated) lastEvent;
        assertEquals(PRODUCT_ID, event.productId().value());
        assertEquals(PRODUCT_NAME, event.name().value());
        assertEquals(ProductLifecycleState.DRAFT.name(), event.state());
        assertNull(event.goal());
    }
}
