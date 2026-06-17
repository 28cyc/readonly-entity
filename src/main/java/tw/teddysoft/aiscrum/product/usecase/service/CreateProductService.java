package tw.teddysoft.aiscrum.product.usecase.service;

import tw.teddysoft.aiscrum.product.entity.Product;
import tw.teddysoft.aiscrum.product.entity.ProductId;
import tw.teddysoft.aiscrum.product.usecase.port.in.CreateProductUseCase;
import tw.teddysoft.ezddd.cqrs.usecase.CqrsOutput;
import tw.teddysoft.ezddd.usecase.port.out.repository.Repository;

public class CreateProductService implements CreateProductUseCase {

    private final Repository<Product, ProductId> productRepository;

    public CreateProductService(Repository<Product, ProductId> productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public CqrsOutput<?> execute(CreateProductInput input) {
        try {
            Product product = new Product(input.productId, input.name, input.userId);
            productRepository.save(product);
            return CqrsOutput.create()
                    .setId(product.getId().value())
                    .succeed();
        } catch (Exception e) {
            return CqrsOutput.create()
                    .setId(input.productId)
                    .setMessage(e.getMessage())
                    .fail();
        }
    }
}
