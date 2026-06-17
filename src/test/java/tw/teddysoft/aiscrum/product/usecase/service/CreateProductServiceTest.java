package tw.teddysoft.aiscrum.product.usecase.service;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import tw.teddysoft.aiscrum.product.entity.ProductEvents;
import tw.teddysoft.aiscrum.product.usecase.port.in.CreateProductUseCase;
import tw.teddysoft.aiscrum.test.base.BaseUseCaseTest;
import tw.teddysoft.ezddd.cqrs.usecase.CqrsOutput;
import tw.teddysoft.ezddd.entity.InternalDomainEvent;
import tw.teddysoft.ezddd.usecase.port.in.interactor.ExitCode;
import tw.teddysoft.ezspec.extension.junit5.EzScenario;
import tw.teddysoft.ezspec.keyword.Feature;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class CreateProductServiceTest extends BaseUseCaseTest {

    @Autowired
    private CreateProductUseCase createProductUseCase;

    @BeforeEach
    public void setUp() {
        setUpEventCapture();
    }

    @AfterEach
    public void tearDown() {
        tearDownEventCapture();
    }

    @EzScenario
    public void create_a_product_with_required_fields() {
        Feature.New("Create Product")
                .newScenario("create a product with id and name")
                .Given("a user wants to create a product", env -> {
                    env.put("productId", UUID.randomUUID().toString());
                    env.put("name", "My Scrum Product");
                    env.put("userId", "user-001");
                })
                .When("the user submits the create product request", env -> {
                    CreateProductUseCase.CreateProductInput input = CreateProductUseCase.CreateProductInput.create();
                    input.productId = env.gets("productId");
                    input.name = env.gets("name");
                    input.userId = env.gets("userId");

                    CqrsOutput<?> output = createProductUseCase.execute(input);
                    env.put("output", output);
                })
                .Then("the product is created and ProductCreated event is emitted", env -> {
                    CqrsOutput<?> output = env.get("output", CqrsOutput.class);
                    assertEquals(ExitCode.SUCCESS, output.getExitCode());
                    assertEquals(env.gets("productId"), output.getId());

                    Awaitility.await().atMost(10, TimeUnit.SECONDS)
                            .until(() -> notifyFakeHandleAllEventsService.getHandledEventsSize() >= 1);

                    _productCreatedEventGenerated(env.gets("productId"), env.gets("name"));
                })
                .Execute();
    }

    private void _productCreatedEventGenerated(String expectedProductId, String expectedName) {
        InternalDomainEvent lastEvent = notifyFakeHandleAllEventsService.getLastHandledEvent();
        assertInstanceOf(ProductEvents.ProductCreated.class, lastEvent);

        ProductEvents.ProductCreated created = (ProductEvents.ProductCreated) lastEvent;
        assertEquals(expectedProductId, created.productId().value());
        assertEquals(expectedName, created.name().value());
        assertEquals("DRAFT", created.state());
    }
}
