package tw.teddysoft.aiscrum.product.usecase.port;

import tw.teddysoft.aiscrum.product.entity.Product;
import tw.teddysoft.aiscrum.product.entity.ProductId;
import tw.teddysoft.aiscrum.product.entity.ProductName;
import tw.teddysoft.aiscrum.product.usecase.port.out.ProductData;
import tw.teddysoft.ezddd.usecase.port.inout.domainevent.DomainEventMapper;
import tw.teddysoft.ezddd.usecase.port.out.repository.impl.outbox.OutboxMapper;

import java.util.stream.Collectors;

import static tw.teddysoft.ucontract.Contract.*;

public class ProductMapper implements OutboxMapper<Product, ProductData> {

    public static ProductMapper newMapper() {
        return new ProductMapper();
    }

    @Override
    public ProductData toData(Product product) {
        ProductData data = new ProductData();
        data.setId(product.getId().value());
        data.setProductName(product.getName().value());
        data.setState(product.getState().name());
        data.setNote(product.getNote());
        data.setExtension(product.getExtension());
        data.setGoalJson(null);
        data.setDomainEventDatas(
                product.getDomainEvents().stream()
                        .map(DomainEventMapper::toData)
                        .collect(Collectors.toList())
        );
        return data;
    }

    @Override
    public Product toDomain(ProductData data) {
        requireNotNull("data", data);
        Product product = new Product(data.getProductId(), data.getProductName(), null);
        product.setVersion(data.getVersion());
        product.clearDomainEvents();
        return product;
    }
}
