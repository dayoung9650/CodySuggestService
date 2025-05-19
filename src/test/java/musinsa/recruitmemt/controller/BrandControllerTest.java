package musinsa.recruitmemt.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import musinsa.recruitmemt.dto.BrandCreateDto;
import musinsa.recruitmemt.model.Brand;
import musinsa.recruitmemt.service.BrandService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BrandController.class)
class BrandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BrandService brandService;

    @Test
    @DisplayName("브랜드 목록 조회")
    void getBrandList() throws Exception {
        // given
        Brand nike = createBrand(1L, "나이키");
        Brand adidas = createBrand(2L, "아디다스");
        List<Brand> brands = Arrays.asList(nike, adidas);
        
        given(brandService.findAll()).willReturn(brands);

        // when & then
        mockMvc.perform(get("/brands"))
                .andExpect(status().isOk())
                .andExpect(view().name("brand/list"))
                .andExpect(model().attribute("brands", brands));
    }

    @Test
    @DisplayName("브랜드 생성 폼")
    void createBrandForm() throws Exception {
        mockMvc.perform(get("/brands/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("brand/form"))
                .andExpect(model().attributeExists("brand"));
    }

    @Test
    @DisplayName("브랜드 생성")
    void createBrand() throws Exception {
        // given
        Brand brand = createBrand(1L, "나이키");
        given(brandService.save(any(Brand.class))).willReturn(brand);

        // when & then
        mockMvc.perform(post("/brands")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("brandName", "나이키"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/brands"));

        verify(brandService).save(any(Brand.class));
    }

    @Test
    @DisplayName("브랜드 수정 폼")
    void updateBrandForm() throws Exception {
        // given
        Brand brand = createBrand(1L, "나이키");
        given(brandService.findById(1L)).willReturn(brand);

        // when & then
        mockMvc.perform(get("/brands/{id}/edit", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("brand/form"))
                .andExpect(model().attribute("brand", brand));
    }

    @Test
    @DisplayName("브랜드 수정")
    void updateBrand() throws Exception {
        // given
        Brand brand = createBrand(1L, "나이키");
        given(brandService.save(any(Brand.class))).willReturn(brand);

        // when & then
        mockMvc.perform(put("/brands/{id}", 1L)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("brandName", "나이키 코리아"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/brands"));

        verify(brandService).save(any(Brand.class));
    }

    @Test
    @DisplayName("브랜드 삭제")
    void deleteBrand() throws Exception {
        mockMvc.perform(delete("/brands/{id}", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/brands"));

        verify(brandService).delete(1L);
    }

    private Brand createBrand(Long id, String name) {
        Brand brand = new Brand();
        brand.setBrandId(id);
        brand.setBrandName(name);
        return brand;
    }
} 