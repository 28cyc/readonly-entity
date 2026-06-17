package tw.teddysoft.aiscrum.product.usecase.service;

import tw.teddysoft.aiscrum.product.entity.Product;
import tw.teddysoft.aiscrum.product.entity.ProductId;
import tw.teddysoft.aiscrum.product.entity.ProductName;
import tw.teddysoft.aiscrum.product.usecase.port.in.CreateProductUseCase;
import tw.teddysoft.ezddd.cqrs.usecase.CqrsOutput;
import tw.teddysoft.ezddd.usecase.port.out.repository.Repository;

import static tw.teddysoft.ucontract.Contract.*;

public class CreateProductService implements CreateProductUseCase {

    private final Repository<Product, ProductId> productRepository;

    public CreateProductService(Repository<Product, ProductId> productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public CqrsOutput<?> execute(CreateProductInput input) {
        requireNotNull("input", input);
        requireNotNull("productId", input.productId);
        requireNotNull("name", input.name);

        try {
            ProductId productId = ProductId.valueOf(input.productId);

            if (productRepository.findById(productId).isPresent()) {
                return CqrsOutput.create()
                        .setId(input.productId)
                        .setMessage("Product with id " + input.productId + " already exists")
                        .fail();
            }

            ProductName name = ProductName.valueOf(input.name);
            Product product = new Product(productId, name);
            productRepository.save(product);

            return CqrsOutput.create()
                    .setId(productId.toString())
                    .succeed();

        } catch (Exception e) {
            return CqrsOutput.create()
                    .setId(input.productId)
                    .setMessage(e.getMessage())
                    .fail();
        }
    }
}
