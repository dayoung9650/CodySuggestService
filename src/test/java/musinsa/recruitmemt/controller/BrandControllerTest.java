package musinsa.recruitmemt.controller;

import musinsa.recruitmemt.model.Brand;
import musinsa.recruitmemt.service.BrandService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class BrandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BrandService brandService;

    @Test
    @DisplayName("브랜드 등록 폼 페이지 테스트")
    void createBrandForm() throws Exception {
        mockMvc.perform(get("/items/brands/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("item/brand-form"))
                .andExpect(model().attributeExists("brand"));
    }

    @Test
    @DisplayName("브랜드 등록 처리 테스트")
    void createBrand() throws Exception {
        // given
        Brand savedBrand = new Brand();
        savedBrand.setBrandId(1L);
        savedBrand.setBrandName("나이키");

        given(brandService.save(any(Brand.class))).willReturn(savedBrand);

        // when & then
        mockMvc.perform(post("/items/brands")
                        .param("brandName", "나이키"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"));
    }

    @Test
    @DisplayName("브랜드명이 비어있는 경우 테스트")
    void createBrandWithEmptyName() throws Exception {
        mockMvc.perform(post("/items/brands")
                        .param("brandName", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items/brands/new"));
    }
} 