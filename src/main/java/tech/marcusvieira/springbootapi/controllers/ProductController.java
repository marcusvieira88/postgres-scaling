package tech.marcusvieira.springbootapi.controllers;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tech.marcusvieira.springbootapi.errorhandlers.exceptions.ProductNotFoundException;
import tech.marcusvieira.springbootapi.mappers.ProductMapper;
import tech.marcusvieira.springbootapi.models.ProductEntity;
import tech.marcusvieira.springbootapi.resources.ProductResource;
import tech.marcusvieira.springbootapi.services.ProductService;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @ApiOperation(value = "Post product")
    @ApiImplicitParam(name = "productResource", dataType = "ProductResource",
        value = "Product information")
    @PostMapping(path = "/products")
    public ProductEntity create(@RequestBody ProductResource productResource) {

        ProductEntity product = ProductMapper.INSTANCE.resourceToEntity(productResource);
        return productService.create(product);
    }

    @ApiOperation(value = "Put product")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", dataType = "long",
            value = "Product id"),
        @ApiImplicitParam(name = "productResource", dataType = "ProductResource",
            value = "Product information")
    })
    @PutMapping(path = "/products/{id}")
    public ProductEntity update(@NotNull @PathVariable Long id, @RequestBody ProductResource productResource) {

        final Optional<ProductEntity> product = productService.findById(id);
        if (product.isEmpty()) {
            throw new ProductNotFoundException("Product not found.");
        }

        ProductEntity productUpdate = ProductMapper.INSTANCE.resourceToEntity(productResource);
        productUpdate.setId(id);
        return  productService.update(productUpdate);
    }

    @ApiOperation(value = "Delete product")
    @ApiImplicitParam(name = "id", dataType = "long",
        value = "Product id")
    @DeleteMapping(path = "/products/{id}")
    public ProductEntity delete(@NotNull @PathVariable Long id) {

        final Optional<ProductEntity> product = productService.findById(id);
        if (product.isEmpty()) {
            throw new ProductNotFoundException("Product not found.");
        }

        productService.delete(id);

        return product.get();
    }

    @ApiOperation(value = "Get product")
    @ApiImplicitParam(name = "id", dataType = "long",
        value = "Product id")
    @GetMapping(path = "/products/{id}")
    public ProductEntity findById(@NotNull @PathVariable Long id) {

        final Optional<ProductEntity> product = productService.findById(id);
        if (product.isEmpty()) {
            throw new ProductNotFoundException("Product not found.");
        }
        return product.get();
    }

    @ApiOperation(value = "Get all products")
    @GetMapping(path = "/products")
    public List<ProductEntity> findAll() {

        final List<ProductEntity> products = productService.findAll();
        if (products == null || products.size() == 0) {
            throw new ProductNotFoundException("Products not found.");
        }

        return products;
    }
}
