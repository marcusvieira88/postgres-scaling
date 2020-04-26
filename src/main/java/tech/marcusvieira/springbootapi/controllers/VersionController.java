package tech.marcusvieira.springbootapi.controllers;

import io.swagger.annotations.ApiOperation;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.marcusvieira.springbootapi.models.ProductEntity;

@RestController
public class VersionController {

    @ApiOperation(value = "Example of URL API versioning")
    @GetMapping(path = "/v1/products")
    public List<ProductEntity> urlVersion1BasedOnURL() {
        return Arrays.asList(new ProductEntity("ProductV1", 1000),
            new ProductEntity("ProductV1", 1000));
    }

    @ApiOperation(value = "Example of URL API versioning")
    @GetMapping(path = "/v2/products")
    public List<ProductEntity> urlVersion2BasedOnURL() {
        return Arrays.asList(new ProductEntity("ProductV2", 1000),
            new ProductEntity("ProductV2", 1000));
    }

    @ApiOperation(value = "Example of URL PARAM API versioning")
    @GetMapping(path = "/products", params = "version=1")
    public List<ProductEntity> paramVersion1BasedOnURL() {
        return Arrays.asList(new ProductEntity("ProductV1", 1000),
            new ProductEntity("ProductV1", 1000));
    }

    @ApiOperation(value = "Example of URL PARAM API versioning")
    @GetMapping(path = "/products", params = "version=2")
    public List<ProductEntity> paramVersion2BasedOnURL() {
        return Arrays.asList(new ProductEntity("ProductV2", 1000),
            new ProductEntity("ProductV2", 1000));
    }

    @ApiOperation(value = "Example of HEADER API versioning")
    @GetMapping(path = "/products", headers = "api-version=1")
    public List<ProductEntity> headerVersion1BasedOnURL() {
        return Arrays.asList(new ProductEntity("ProductV1", 1000),
            new ProductEntity("ProductV1", 1000));
    }

    @ApiOperation(value = "Example of HEADER API versioning")
    @GetMapping(path = "/products", headers = "api-version=2")
    public List<ProductEntity> headerVersion2BasedOnURL() {
        return Arrays.asList(new ProductEntity("ProductV2", 1000),
            new ProductEntity("ProductV2", 1000));
    }
}
