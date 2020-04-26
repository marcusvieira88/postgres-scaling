package tech.marcusvieira.springbootapi.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import tech.marcusvieira.springbootapi.models.ProductEntity;
import tech.marcusvieira.springbootapi.resources.ProductResource;

@Mapper
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper( ProductMapper.class );

    ProductEntity resourceToEntity(ProductResource productResource);

    ProductResource entityToResource(ProductEntity productEntity);
}
