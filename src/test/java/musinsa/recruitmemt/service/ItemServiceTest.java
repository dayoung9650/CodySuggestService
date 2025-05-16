package musinsa.recruitmemt.service;

import musinsa.recruitmemt.dto.BrandTotalPriceDto;
import musinsa.recruitmemt.dto.CategoryMinPriceDto;
import musinsa.recruitmemt.dto.CategoryPriceRangeDto;
import musinsa.recruitmemt.model.Brand;
import musinsa.recruitmemt.model.Category;
import musinsa.recruitmemt.model.Item;
import musinsa.recruitmemt.repository.BrandRepository;
import musinsa.recruitmemt.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private ItemService itemService;

    private Brand brand1;
    private Brand brand2;
    private Category category1;
    private Category category2;
    private Item item1;
    private Item item2;
    private Item item3;

    @BeforeEach
    void setUp() {
        // 브랜드 설정
        brand1 = new Brand();
        brand1.setBrandId(1L);
        brand1.setBrandName("나이키");

        brand2 = new Brand();
        brand2.setBrandId(2L);
        brand2.setBrandName("아디다스");

        // 카테고리 설정
        category1 = new Category();
        category1.setCategoryId(1L);
        category1.setCategoryName("상의");

        category2 = new Category();
        category2.setCategoryId(2L);
        category2.setCategoryName("하의");

        // 상품 설정
        item1 = new Item();
        item1.setItemId(1L);
        item1.setName("나이키 티셔츠");
        item1.setPrice(50000);
        item1.setBrand(brand1);
        item1.setCategory(category1);

        item2 = new Item();
        item2.setItemId(2L);
        item2.setName("아디다스 티셔츠");
        item2.setPrice(45000);
        item2.setBrand(brand2);
        item2.setCategory(category1);

        item3 = new Item();
        item3.setItemId(3L);
        item3.setName("나이키 바지");
        item3.setPrice(60000);
        item3.setBrand(brand1);
        item3.setCategory(category2);
    }

    @Test
    @DisplayName("전체 상품 조회")
    void findAll() {
        // given
        given(itemRepository.findAll()).willReturn(Arrays.asList(item1, item2, item3));

        // when
        List<Item> items = itemService.findAll();

        // then
        assertThat(items).hasSize(3)
                .extracting("name")
                .containsExactly("나이키 티셔츠", "아디다스 티셔츠", "나이키 바지");
    }

    @Test
    @DisplayName("ID로 상품 조회")
    void findById() {
        // given
        given(itemRepository.findById(1L)).willReturn(Optional.of(item1));

        // when
        Item found = itemService.findById(1L);

        // then
        assertThat(found.getName()).isEqualTo("나이키 티셔츠");
        assertThat(found.getPrice()).isEqualTo(50000);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 상품 조회 시 예외 발생")
    void findByIdNotFound() {
        // given
        given(itemRepository.findById(99L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> itemService.findById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("상품을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("브랜드 ID로 상품 조회")
    void findByBrandId() {
        // given
        given(itemRepository.findByBrandBrandId(1L))
                .willReturn(Arrays.asList(item1, item3));

        // when
        List<Item> items = itemService.findByBrandId(1L);

        // then
        assertThat(items).hasSize(2)
                .extracting("name")
                .containsExactly("나이키 티셔츠", "나이키 바지");
    }

    @Test
    @DisplayName("카테고리 ID로 상품 조회")
    void findByCategoryId() {
        // given
        given(itemRepository.findByCategoryCategoryId(1L))
                .willReturn(Arrays.asList(item1, item2));

        // when
        List<Item> items = itemService.findByCategoryId(1L);

        // then
        assertThat(items).hasSize(2)
                .extracting("name")
                .containsExactly("나이키 티셔츠", "아디다스 티셔츠");
    }

    @Test
    @DisplayName("브랜드 ID와 카테고리 ID로 상품 조회")
    void findByBrandIdAndCategoryId() {
        // given
        given(itemRepository.findByBrandBrandIdAndCategoryCategoryId(1L, 1L))
                .willReturn(List.of(item1));

        // when
        List<Item> items = itemService.findByBrandIdAndCategoryId(1L, 1L);

        // then
        assertThat(items).hasSize(1)
                .extracting("name")
                .containsExactly("나이키 티셔츠");
    }

    @Test
    @DisplayName("상품 저장")
    void save() {
        // given
        Item newItem = new Item();
        newItem.setName("새 상품");
        newItem.setPrice(30000);

        given(brandRepository.findById(1L)).willReturn(Optional.of(brand1));
        given(itemRepository.save(any(Item.class))).willReturn(newItem);

        // when
        Item saved = itemService.save(newItem, 1L);

        // then
        assertThat(saved.getName()).isEqualTo("새 상품");
        assertThat(saved.getBrand()).isEqualTo(brand1);
        verify(itemRepository).save(newItem);
    }

    @Test
    @DisplayName("카테고리별 최저가격 조회")
    void findMinPricesByCategory() {
        // given
        given(itemRepository.findAll()).willReturn(Arrays.asList(item1, item2));

        // when
        List<CategoryMinPriceDto> result = itemService.findMinPricesByCategory();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategoryName()).isEqualTo("상의");
        assertThat(result.get(0).getBrandName()).isEqualTo("아디다스");
        assertThat(result.get(0).getPrice()).isEqualTo(45000);
    }

    @Test
    @DisplayName("브랜드별 총액 조회")
    void findTotalPricesByBrand() {
        // given
        given(itemRepository.findAll()).willReturn(Arrays.asList(item1, item2, item3));

        // when
        List<BrandTotalPriceDto> result = itemService.findTotalPricesByBrand();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("brandName", "totalPrice")
                .containsExactlyInAnyOrder(
                        tuple("나이키", 110000),
                        tuple("아디다스", 45000)
                );
    }

    @Test
    @DisplayName("카테고리별 가격 범위 조회")
    void findPriceRangeByCategory() {
        // given
        given(itemRepository.findByCategoryCategoryName("상의"))
                .willReturn(Arrays.asList(item1, item2));

        // when
        CategoryPriceRangeDto result = itemService.findPriceRangeByCategory("상의");

        // then
        assertThat(result.getCategoryName()).isEqualTo("상의");
        assertThat(result.getLowestPrice().getBrandName()).isEqualTo("아디다스");
        assertThat(result.getLowestPrice().getPrice()).isEqualTo(45000);
        assertThat(result.getHighestPrice().getBrandName()).isEqualTo("나이키");
        assertThat(result.getHighestPrice().getPrice()).isEqualTo(50000);
    }

    @Test
    @DisplayName("브랜드별 최신 상품 가격표 조회")
    void findLatestPricesByBrand() {
        // given
        LocalDateTime now = LocalDateTime.now();
        item1.setInsertTime(now.minusDays(1));  // 하루 전
        item2.setInsertTime(now.minusHours(1)); // 1시간 전
        item3.setInsertTime(now);               // 현재

        given(itemRepository.findAll()).willReturn(Arrays.asList(item1, item2, item3));

        // when
        List<BrandTotalPriceDto> result = itemService.findLatestPricesByBrand();

        // then
        assertThat(result).hasSize(2); // 나이키, 아디다스

        // 나이키의 경우
        BrandTotalPriceDto nikePrices = result.stream()
                .filter(dto -> dto.getBrandName().equals("나이키"))
                .findFirst()
                .orElseThrow();

        assertThat(nikePrices.getCategoryPrices())
                .containsEntry("상의", 50000)  // item1
                .containsEntry("하의", 60000); // item3 (최신)
        assertThat(nikePrices.getTotalPrice()).isEqualTo(110000);

        // 아디다스의 경우
        BrandTotalPriceDto adidasPrices = result.stream()
                .filter(dto -> dto.getBrandName().equals("아디다스"))
                .findFirst()
                .orElseThrow();

        assertThat(adidasPrices.getCategoryPrices())
                .containsEntry("상의", 45000);  // item2
        assertThat(adidasPrices.getTotalPrice()).isEqualTo(45000);
    }
} 