package musinsa.recruitmemt.controller;

import musinsa.recruitmemt.dto.BrandTotalPriceDto;
import musinsa.recruitmemt.dto.CategoryMinPriceDto;
import musinsa.recruitmemt.dto.CategoryPriceRangeDto;
import musinsa.recruitmemt.model.Brand;
import musinsa.recruitmemt.model.Category;
import musinsa.recruitmemt.model.Item;
import musinsa.recruitmemt.service.BrandService;
import musinsa.recruitmemt.service.CategoryService;
import musinsa.recruitmemt.service.ItemService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @MockBean
    private BrandService brandService;

    @MockBean
    private CategoryService categoryService;

    @Test
    @DisplayName("상품 목록 페이지 테스트")
    void listItems() throws Exception {
        // given
        Brand brand = new Brand();
        brand.setBrandId(1L);
        brand.setBrandName("나이키");

        Category category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("상의");

        given(brandService.findAll()).willReturn(Collections.singletonList(brand));
        given(categoryService.findAll()).willReturn(Collections.singletonList(category));

        // when & then
        mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("item/list"))
                .andExpect(model().attributeExists("priceTable"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    @DisplayName("카테고리별 최저가격 페이지 테스트")
    void getLowestPrices() throws Exception {
        // given
        CategoryMinPriceDto dto = new CategoryMinPriceDto("상의", "나이키", 10000);

        given(itemService.findMinPricesByCategory())
                .willReturn(Collections.singletonList(dto));

        // when & then
        mockMvc.perform(get("/items/lowest-prices"))
                .andExpect(status().isOk())
                .andExpect(view().name("item/summary/lowest-prices"))
                .andExpect(model().attributeExists("lowestPrices"))
                .andExpect(model().attributeExists("totalPrice"));
    }

    @Test
    @DisplayName("브랜드별 총액 페이지 테스트")
    void getBrandTotals() throws Exception {
        // given
        BrandTotalPriceDto dto = new BrandTotalPriceDto("나이키", 100000);

        given(itemService.findTotalPricesByBrand())
                .willReturn(Collections.singletonList(dto));

        // when & then
        mockMvc.perform(get("/items/brand-totals"))
                .andExpect(status().isOk())
                .andExpect(view().name("item/summary/brand-totals"))
                .andExpect(model().attributeExists("brandTotals"));
    }

    @Test
    @DisplayName("카테고리 가격 범위 페이지 테스트")
    void getCategoryRange() throws Exception {
        // given
        CategoryPriceRangeDto dto = new CategoryPriceRangeDto();
        dto.setCategoryName("상의");
        CategoryPriceRangeDto.BrandPrice lowestPrice = new CategoryPriceRangeDto.BrandPrice("나이키", 10000);
        CategoryPriceRangeDto.BrandPrice highestPrice = new CategoryPriceRangeDto.BrandPrice("아디다스", 50000);
        dto.setLowestPrice(lowestPrice);
        dto.setHighestPrice(highestPrice);

        given(itemService.findPriceRangeByCategory("상의"))
                .willReturn(dto);

        // when & then
        mockMvc.perform(get("/items/category-range")
                        .param("categoryName", "상의"))
                .andExpect(status().isOk())
                .andExpect(view().name("item/summary/category-range"))
                .andExpect(model().attributeExists("priceRange"));
    }

    @Test
    @DisplayName("새 상품 등록 폼 페이지 테스트")
    void createForm() throws Exception {
        // given
        Brand brand = new Brand();
        brand.setBrandId(1L);
        brand.setBrandName("나이키");

        given(brandService.findAll()).willReturn(Collections.singletonList(brand));

        // when & then
        mockMvc.perform(get("/items/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("item/form"))
                .andExpect(model().attributeExists("item"))
                .andExpect(model().attributeExists("brands"));
    }

    @Test
    @DisplayName("상품 등록 처리 테스트")
    void create() throws Exception {
        // given
        Item savedItem = new Item();
        savedItem.setItemId(1L);
        savedItem.setName("반팔티");
        savedItem.setPrice(10000);

        given(itemService.save(any(Item.class), eq(1L))).willReturn(savedItem);

        // when & then
        mockMvc.perform(post("/items")
                        .param("name", "반팔티")
                        .param("price", "10000")
                        .param("brandId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"));
    }
} 