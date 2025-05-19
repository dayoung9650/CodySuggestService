package musinsa.recruitmemt.integration;

import musinsa.recruitmemt.dto.BrandTotalPriceDto;
import musinsa.recruitmemt.dto.CategoryMinPriceDto;
import musinsa.recruitmemt.dto.CategoryPriceRangeDto;
import musinsa.recruitmemt.model.Brand;
import musinsa.recruitmemt.model.Category;
import musinsa.recruitmemt.model.Item;
import musinsa.recruitmemt.repository.BrandRepository;
import musinsa.recruitmemt.repository.CategoryRepository;
import musinsa.recruitmemt.repository.ItemRepository;
import musinsa.recruitmemt.service.BrandService;
import musinsa.recruitmemt.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
@Transactional
class AdminAndStatsIntegrationTest {

    @Autowired
    private BrandService brandService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ItemRepository itemRepository;

    private Brand nike;
    private Brand adidas;
    private Category top;
    private Category bottom;
    private Category shoes;

    @BeforeEach
    void setUp() {
        brandRepository.deleteAll();
        categoryRepository.deleteAll();
        itemRepository.deleteAll();

        // 브랜드 생성
        nike = new Brand();
        nike.setBrandName("나이키");
        brandRepository.save(nike);

        adidas = new Brand();
        adidas.setBrandName("아디다스");
        brandRepository.save(adidas);

        // 카테고리 생성
        top = new Category();
        top.setCategoryName("상의");
        categoryRepository.save(top);

        bottom = new Category();
        bottom.setCategoryName("하의");
        categoryRepository.save(bottom);

        shoes = new Category();
        shoes.setCategoryName("신발");
        categoryRepository.save(shoes);

        LocalDateTime now = LocalDateTime.now();

        // 나이키 상품 생성
        createItem(nike, top, "나이키 티셔츠", 50000, now.minusDays(1));
        createItem(nike, top, "나이키 신상 티셔츠", 55000, now); // 최신 상품
        createItem(nike, bottom, "나이키 바지", 70000, now);
        createItem(nike, shoes, "나이키 운동화", 90000, now);

        // 아디다스 상품 생성
        createItem(adidas, top, "아디다스 티셔츠", 45000, now);
        createItem(adidas, bottom, "아디다스 바지", 65000, now);
        createItem(adidas, shoes, "아디다스 운동화", 85000, now);
    }

    private Item createItem(Brand brand, Category category, String name, int price, LocalDateTime updateTime) {
        Item item = new Item();
        item.setName(name);
        item.setPrice(price);
        item.setBrand(brand);
        item.setCategory(category);
        item.setUpdateTime(updateTime);
        return itemRepository.save(item);
    }

    @Test
    @DisplayName("브랜드 관리 - 추가, 변경, 삭제")
    void brandManagement() {
        // 브랜드 추가
        Brand puma = new Brand();
        puma.setBrandName("푸마");
        Brand savedPuma = brandService.save(puma);
        assertThat(savedPuma.getBrandName()).isEqualTo("푸마");

        // 브랜드에 상품 추가
        Item pumaShoes = createItem(savedPuma, shoes, "푸마 운동화", 80000, LocalDateTime.now());
        assertThat(itemService.findByBrandId(savedPuma.getBrandId()))
            .hasSize(1)
            .extracting("name", "price")
            .contains(tuple("푸마 운동화", 80000));

        // 브랜드 삭제 (연관 상품도 삭제되어야 함)
        brandService.delete(savedPuma.getBrandId());
        assertThat(itemRepository.findByBrandBrandId(savedPuma.getBrandId())).isEmpty();
    }

    @Test
    @DisplayName("상품 관리 - 추가, 변경, 삭제")
    void itemManagement() {
        // 상품 추가
        Item newItem = new Item();
        newItem.setName("새 운동화");
        newItem.setPrice(75000);
        newItem.setCategory(shoes);
        Item savedItem = itemService.save(newItem, nike.getBrandId());

        // 상품 조회
        Item foundItem = itemService.findById(savedItem.getItemId());
        assertThat(foundItem.getName()).isEqualTo("새 운동화");
        assertThat(foundItem.getPrice()).isEqualTo(75000);

        // 상품 수정
        foundItem.setPrice(80000);
        Item updatedItem = itemService.save(foundItem, nike.getBrandId());
        assertThat(updatedItem.getPrice()).isEqualTo(80000);

        // 상품 삭제
        itemService.delete(updatedItem.getItemId());
        List<Item> items = itemService.findByBrandId(nike.getBrandId());
        assertThat(items).extracting("name").doesNotContain("새 운동화");
    }

    @Test
    @DisplayName("브랜드/카테고리별 최신 상품 요약")
    void latestItemsSummary() {
        List<BrandTotalPriceDto> latestPrices = itemService.findLatestPricesByBrand();

        assertThat(latestPrices).hasSize(2);

        // 나이키의 최신 상품 가격 확인
        BrandTotalPriceDto nikePrices = latestPrices.stream()
            .filter(dto -> dto.getBrandName().equals("나이키"))
            .findFirst()
            .orElseThrow();

        Map<String, Integer> nikeCategoryPrices = nikePrices.getCategoryPrices();
        assertThat(nikeCategoryPrices)
            .containsEntry("상의", 55000)  // 최신 상품 가격
            .containsEntry("하의", 70000)
            .containsEntry("신발", 90000);
        assertThat(nikePrices.getTotalPrice()).isEqualTo(215000); // 55000 + 70000 + 90000

        // 아디다스의 최신 상품 가격 확인
        BrandTotalPriceDto adidasPrices = latestPrices.stream()
            .filter(dto -> dto.getBrandName().equals("아디다스"))
            .findFirst()
            .orElseThrow();

        Map<String, Integer> adidasCategoryPrices = adidasPrices.getCategoryPrices();
        assertThat(adidasCategoryPrices)
            .containsEntry("상의", 45000)
            .containsEntry("하의", 65000)
            .containsEntry("신발", 85000);
        assertThat(adidasPrices.getTotalPrice()).isEqualTo(195000); // 45000 + 65000 + 85000
    }

    @Test
    @DisplayName("카테고리별 최저가 브랜드와 가격")
    void categoryMinPrices() {
        List<CategoryMinPriceDto> minPrices = itemService.findMinPricesByCategory();

        assertThat(minPrices)
            .extracting("categoryName", "brandName", "price")
            .containsExactlyInAnyOrder(
                tuple("상의", "아디다스", 45000),
                tuple("하의", "아디다스", 65000),
                tuple("신발", "아디다스", 85000)
            );

        // 총액 계산 확인
        int totalMinPrice = minPrices.stream()
            .mapToInt(CategoryMinPriceDto::getPrice)
            .sum();
        assertThat(totalMinPrice).isEqualTo(195000); // 45000 + 65000 + 85000
    }

    @Test
    @DisplayName("브랜드별 최소 가격 계산")
    void brandTotalPrices() {
        BrandTotalPriceDto lowestBrand = itemService.findLowestTotalPriceBrand();

        assertThat(lowestBrand.getBrandName()).isEqualTo("아디다스");
        assertThat(lowestBrand.getTotalPrice()).isEqualTo(195000); // 45000 + 65000 + 85000
        assertThat(lowestBrand.getCategoryPrices())
            .containsEntry("상의", 45000)
            .containsEntry("하의", 65000)
            .containsEntry("신발", 85000);
    }

    @Test
    @DisplayName("카테고리별 최저/최고 가격 브랜드")
    void categoryPriceRange() {
        CategoryPriceRangeDto topRange = itemService.findPriceRangeByCategory("상의");

        assertThat(topRange.getCategoryName()).isEqualTo("상의");
        
        // 최저가 확인
        assertThat(topRange.getLowestPrice().getBrandName()).isEqualTo("아디다스");
        assertThat(topRange.getLowestPrice().getPrice()).isEqualTo(45000);
        
        // 최고가 확인 (나이키의 최신 상품 가격)
        assertThat(topRange.getHighestPrice().getBrandName()).isEqualTo("나이키");
        assertThat(topRange.getHighestPrice().getPrice()).isEqualTo(55000);
    }
} 