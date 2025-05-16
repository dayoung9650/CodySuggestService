package musinsa.recruitmemt.repository;
import musinsa.recruitmemt.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByBrandBrandId(Long brandId);

    List<Item> findByCategoryCategoryName(String categoryName);


    List<Item> findByCategoryCategoryId(Long categoryId);

    List<Item> findByBrandBrandIdAndCategoryCategoryId(Long brandId, Long categoryId);

}