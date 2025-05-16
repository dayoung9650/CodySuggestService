package musinsa.recruitmemt.repository;

import musinsa.recruitmemt.model.Brand;
import musinsa.recruitmemt.model.Category;
import musinsa.recruitmemt.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Brand brand1;
    private Brand brand2;
    private Category category1;
    private Category category2;
    private Item item1;
    private Item item2;
    private Item item3;

    @BeforeEach
    void setUp() {
        // 브랜드 생성
        brand1 = new Brand();
        brand1.setBrandName("나이키");
        entityManager.persist(brand1);

        brand2 = new Brand();
        brand2.setBrandName("아디다스");
        entityManager.persist(brand2);

        // 카테고리 생성
        category1 = new Category();
        category1.setCategoryName("상의");
        entityManager.persist(category1);

        category2 = new Category();
        category2.setCategoryName("하의");
        entityManager.persist(category2);

        // 상품 생성
        item1 = new Item();
        item1.setName("나이키 티셔츠");
        item1.setPrice(50000);
        item1.setBrand(brand1);
        item1.setCategory(category1);
        entityManager.persist(item1);

        item2 = new Item();
        item2.setName("아디다스 티셔츠");
        item2.setPrice(45000);
        item2.setBrand(brand2);
        item2.setCategory(category1);
        entityManager.persist(item2);

        item3 = new Item();
        item3.setName("나이키 바지");
        item3.setPrice(60000);
        item3.setBrand(brand1);
        item3.setCategory(category2);
        entityManager.persist(item3);

        entityManager.flush();
    }

    @Test
    @DisplayName("상품 저장 테스트")
    void saveItem() {
        // given
        Item item = new Item();
        item.setName("반팔티");
        item.setPrice(10000);
        item.setBrand(brand1);
        item.setCategory(category1);

        // when
        Item savedItem = itemRepository.save(item);

        // then
        assertThat(savedItem.getItemId()).isNotNull();
        assertThat(savedItem.getName()).isEqualTo("반팔티");
        assertThat(savedItem.getPrice()).isEqualTo(10000);
        assertThat(savedItem.getBrand().getBrandName()).isEqualTo("나이키");
        assertThat(savedItem.getCategory().getCategoryName()).isEqualTo("상의");
    }

    @Test
    @DisplayName("브랜드 ID로 상품 조회")
    void findByBrandBrandId() {
        // when
        List<Item> items = itemRepository.findByBrandBrandId(brand1.getBrandId());

        // then
        assertThat(items).hasSize(2)
                .extracting("name")
                .containsExactlyInAnyOrder("나이키 티셔츠", "나이키 바지");
    }

    @Test
    @DisplayName("카테고리 이름으로 상품 조회")
    void findByCategoryCategoryName() {
        // when
        List<Item> items = itemRepository.findByCategoryCategoryName("상의");

        // then
        assertThat(items).hasSize(2)
                .extracting("name")
                .containsExactlyInAnyOrder("나이키 티셔츠", "아디다스 티셔츠");
    }

    @Test
    @DisplayName("카테고리 ID로 상품 조회")
    void findByCategoryCategoryId() {
        // when
        List<Item> items = itemRepository.findByCategoryCategoryId(category1.getCategoryId());

        // then
        assertThat(items).hasSize(2)
                .extracting("name")
                .containsExactlyInAnyOrder("나이키 티셔츠", "아디다스 티셔츠");
    }

    @Test
    @DisplayName("브랜드 ID와 카테고리 ID로 상품 조회")
    void findByBrandBrandIdAndCategoryCategoryId() {
        // when
        List<Item> items = itemRepository.findByBrandBrandIdAndCategoryCategoryId(
                brand1.getBrandId(), category1.getCategoryId());

        // then
        assertThat(items).hasSize(1)
                .extracting("name")
                .containsExactly("나이키 티셔츠");
    }

    @Test
    @DisplayName("가격 범위로 상품 조회 테스트")
    void findByPriceBetween() {
        // given
        Item item1 = new Item();
        item1.setName("반팔티");
        item1.setPrice(10000);
        item1.setBrand(brand1);
        item1.setCategory(category1);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("긴팔티");
        item2.setPrice(20000);
        item2.setBrand(brand1);
        item2.setCategory(category1);
        itemRepository.save(item2);

        // when
        List<Item> items = itemRepository.findByPriceBetween(5000, 15000);

        // then
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("반팔티");
        assertThat(items.get(0).getPrice()).isEqualTo(10000);
    }
} 