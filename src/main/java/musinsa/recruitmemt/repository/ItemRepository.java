package musinsa.recruitmemt.repository;
import musinsa.recruitmemt.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByBrandBrandId(Long brandId);

    List<Item> findByCategoryCategoryName(String categoryName);

    List<Item> findByCategoryCategoryId(Long categoryId);

    List<Item> findByBrandBrandIdAndCategoryCategoryId(Long brandId, Long categoryId);

    // 가격 범위로 상품 조회
    List<Item> findByPriceBetween(Integer minPrice, Integer maxPrice);

    /**
     * 각 브랜드별로 가장 최근에 등록된 상품을 조회합니다.
     * 브랜드와 카테고리 조합으로 그룹화하여 각각의 최신 상품을 반환합니다.
     */
    @Query("SELECT i FROM Item i " +
           "WHERE (i.brand.id, i.category.id, i.updateTime) IN " +
           "(SELECT i2.brand.id, i2.category.id, MAX(i2.updateTime) " +
           "FROM Item i2 GROUP BY i2.brand.id, i2.category.id)")
    List<Item> findLatestItemsByBrandAndCategory();

    /**
     * 특정 브랜드의 최신 상품들을 조회합니다.
     * 브랜드 내 각 카테고리별로 가장 최근에 등록된 상품을 반환합니다.
     */
    @Query("SELECT i FROM Item i " +
           "WHERE i.brand.id = :brandId " +
           "AND (i.category.id, i.updateTime) IN " +
           "(SELECT i2.category.id, MAX(i2.updateTime) " +
           "FROM Item i2 WHERE i2.brand.id = :brandId " +
           "GROUP BY i2.category.id)")
    List<Item> findLatestItemsByBrand(@Param("brandId") Long brandId);
}