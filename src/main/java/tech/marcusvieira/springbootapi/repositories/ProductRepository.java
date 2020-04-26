package tech.marcusvieira.springbootapi.repositories;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tech.marcusvieira.springbootapi.models.ProductEntity;

@Repository
public interface ProductRepository extends CrudRepository<ProductEntity, Long> {

    public List<ProductEntity> findAll();
}
