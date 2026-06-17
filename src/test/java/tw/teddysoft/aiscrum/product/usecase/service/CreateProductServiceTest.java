package tw.teddysoft.aiscrum.product.usecase.service;

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

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class CreateProductServiceTest extends BaseUseCaseTest {

    @Autowired
    private CreateProductUseCase createProductUseCase;

    @BeforeEach
    void setUp() {
        setUpEventCapture();
    }

    @AfterEach
    void tearDown() {
        tearDownEventCapture();
    }

    @EzScenario
    public void create_a_product_successfully() {
        Feature.New("Create Product")
                .newScenario("Successfully create a product with valid inputs")
                .Given("A valid product id and name", env -> {
                    env.put("productId", "product-test-001");
                    env.put("name", "Test Product");
                    env.put("userId", "user-001");
                })
                .When("The user creates the product", env -> {
                    CreateProductUseCase.CreateProductInput input = CreateProductUseCase.CreateProductInput.create();
                    input.productId = env.gets("productId");
                    input.name = env.gets("name");
                    input.userId = env.gets("userId");

                    CqrsOutput<?> output = createProductUseCase.execute(input);
                    env.put("output", output);
                })
                .Then("The product is created successfully", env -> {
                    CqrsOutput<?> output = env.get("output", CqrsOutput.class);
                    assertThat(output.getExitCode()).isEqualTo(ExitCode.SUCCESS);
                    assertThat(output.getId()).isEqualTo("product-test-001");
                })
                .And("A ProductCreated event is published", env -> {
                    await().atMost(10, TimeUnit.SECONDS).until(() ->
                            notifyFakeHandleAllEventsService.getHandledEventsSize() >= 1);

                    InternalDomainEvent lastEvent = notifyFakeHandleAllEventsService.getLastHandledEvent();
                    assertThat(lastEvent).isInstanceOf(ProductEvents.ProductCreated.class);

                    ProductEvents.ProductCreated created = (ProductEvents.ProductCreated) lastEvent;
                    assertThat(created.productId().toString()).isEqualTo("product-test-001");
                    assertThat(created.name().toString()).isEqualTo("Test Product");
                    assertThat(created.state()).isEqualTo("DRAFT");
                })
                .Execute();
    }

    @EzScenario
    public void reject_duplicate_product_id() {
        Feature.New("Create Product")
                .newScenario("Reject creation when product id already exists")
                .Given("A product already exists with the same id", env -> {
                    CreateProductUseCase.CreateProductInput firstInput = CreateProductUseCase.CreateProductInput.create();
                    firstInput.productId = "product-dup-001";
                    firstInput.name = "Existing Product";
                    firstInput.userId = "user-001";
                    createProductUseCase.execute(firstInput);
                    env.put("productId", "product-dup-001");
                })
                .When("The user tries to create a product with the same id", env -> {
                    CreateProductUseCase.CreateProductInput input = CreateProductUseCase.CreateProductInput.create();
                    input.productId = env.gets("productId");
                    input.name = "Duplicate Product";
                    input.userId = "user-002";

                    CqrsOutput<?> output = createProductUseCase.execute(input);
                    env.put("output", output);
                })
                .Then("The creation fails", env -> {
                    CqrsOutput<?> output = env.get("output", CqrsOutput.class);
                    assertThat(output.getExitCode()).isEqualTo(ExitCode.FAILURE);
                })
                .Execute();
    }
}
