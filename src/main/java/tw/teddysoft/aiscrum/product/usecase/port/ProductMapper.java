package tw.teddysoft.aiscrum.product.usecase.port;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import tw.teddysoft.aiscrum.product.entity.GoalMetric;
import tw.teddysoft.aiscrum.product.entity.Product;
import tw.teddysoft.aiscrum.product.entity.ProductGoal;
import tw.teddysoft.aiscrum.product.entity.ProductGoalId;
import tw.teddysoft.aiscrum.product.entity.ProductGoalState;
import tw.teddysoft.aiscrum.product.entity.ProductId;
import tw.teddysoft.aiscrum.product.entity.ProductName;
import tw.teddysoft.aiscrum.product.usecase.port.out.ProductData;
import tw.teddysoft.ezddd.usecase.port.inout.domainevent.DomainEventMapper;
import tw.teddysoft.ezddd.usecase.port.out.repository.impl.outbox.OutboxMapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static tw.teddysoft.ucontract.Contract.*;

public class ProductMapper {

    public static Mapper newMapper() {
        return new Mapper();
    }

    public static class Mapper implements OutboxMapper<Product, ProductData> {

        private static final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

        @Override
        public Product toDomain(ProductData data) {
            requireNotNull("ProductData", data);

            ProductId id = ProductId.valueOf(data.getId());
            ProductName name = ProductName.valueOf(data.getProductName());

            Product product = new Product(id, name);
            product.setVersion(data.getVersion());
            product.clearDomainEvents();
            return product;
        }

        @Override
        public ProductData toData(Product aggregate) {
            ProductData data = new ProductData(aggregate.getVersion());
            data.setId(aggregate.getId().toString());
            data.setProductName(aggregate.getName().toString());
            data.setState(aggregate.getState().name());
            data.setNote(aggregate.getNote());
            data.setExtension(aggregate.getExtension());

            if (aggregate.getGoal() != null) {
                ProductGoal goal = aggregate.getGoal();
                data.setGoalId(goal.id().toString());
                data.setGoalTitle(goal.title());
                data.setGoalDescription(goal.description());
                data.setGoalState(goal.state().name());
                data.setGoalDefinedAt(goal.definedAt());
                data.setGoalRevisedAt(goal.revisedAt());

                if (goal.metrics() != null && !goal.metrics().isEmpty()) {
                    try {
                        data.setGoalMetricsJson(objectMapper.writeValueAsString(goal.metrics()));
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to serialize goalMetrics", e);
                    }
                }
            }

            data.setStreamName(aggregate.getCategory());
            data.setDomainEventDatas(
                    aggregate.getDomainEvents().stream()
                            .map(DomainEventMapper::toData)
                            .collect(Collectors.toList()));

            return data;
        }

        private ProductGoal reconstructGoal(ProductData data) {
            if (data.getGoalId() == null) {
                return null;
            }

            List<GoalMetric> metrics = Collections.emptyList();
            if (data.getGoalMetricsJson() != null && !data.getGoalMetricsJson().isBlank()) {
                try {
                    metrics = objectMapper.readValue(data.getGoalMetricsJson(),
                            new TypeReference<List<GoalMetric>>() {});
                } catch (Exception e) {
                    throw new RuntimeException("Failed to deserialize goalMetrics", e);
                }
            }

            return new ProductGoal(
                    ProductGoalId.valueOf(data.getGoalId()),
                    data.getGoalTitle(),
                    data.getGoalDescription(),
                    metrics,
                    data.getGoalDefinedAt(),
                    data.getGoalRevisedAt(),
                    ProductGoalState.valueOf(data.getGoalState())
            );
        }
    }
}
