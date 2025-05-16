package musinsa.recruitmemt.service;

import musinsa.recruitmemt.model.Brand;
import musinsa.recruitmemt.model.Item;
import musinsa.recruitmemt.repository.BrandRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;

    @Test
    @DisplayName("브랜드 저장 테스트")
    void saveBrand() {
        // given
        Brand brand = new Brand();
        brand.setBrandName("나이키");

        Brand savedBrand = new Brand();
        savedBrand.setBrandId(1L);
        savedBrand.setBrandName("나이키");

        given(brandRepository.save(any(Brand.class))).willReturn(savedBrand);

        // when
        Brand result = brandService.save(brand);

        // then
        assertThat(result.getBrandId()).isEqualTo(1L);
        assertThat(result.getBrandName()).isEqualTo("나이키");
        verify(brandRepository).save(any(Brand.class));
    }

    @Test
    @DisplayName("모든 브랜드 조회 테스트")
    void findAllBrands() {
        // given
        Brand brand1 = new Brand();
        brand1.setBrandId(1L);
        brand1.setBrandName("나이키");

        Brand brand2 = new Brand();
        brand2.setBrandId(2L);
        brand2.setBrandName("아디다스");

        given(brandRepository.findAll()).willReturn(Arrays.asList(brand1, brand2));

        // when
        List<Brand> brands = brandService.findAll();

        // then
        assertThat(brands).hasSize(2);
        assertThat(brands).extracting("brandName")
                .containsExactlyInAnyOrder("나이키", "아디다스");
        verify(brandRepository).findAll();
    }

    @Test
    @DisplayName("ID로 브랜드 조회 테스트")
    void findBrandById() {
        // given
        Brand brand = new Brand();
        brand.setBrandId(1L);
        brand.setBrandName("나이키");

        given(brandRepository.findById(1L)).willReturn(Optional.of(brand));

        // when
        Brand foundBrand = brandService.findById(1L);

        // then
        assertThat(foundBrand).isNotNull();
        assertThat(foundBrand.getBrandName()).isEqualTo("나이키");
        verify(brandRepository).findById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 브랜드 조회 시 null 반환 테스트")
    void findNonExistentBrand() {
        // given
        given(brandRepository.findById(999L)).willReturn(Optional.empty());

        // when
        Brand foundBrand = brandService.findById(999L);

        // then
        assertThat(foundBrand).isNull();
        verify(brandRepository).findById(999L);
    }

    @Test
    @DisplayName("브랜드 삭제 테스트")
    void deleteBrand() {
        // given
        Long brandId = 1L;

        // when
        brandService.delete(brandId);

        // then
        verify(brandRepository).deleteById(brandId);
    }

    @Test
    @DisplayName("브랜드 삭제 시 연관된 상품도 함께 삭제")
    void deleteBrandWithItems() {
        // given
        Brand brand = new Brand();
        brand.setBrandId(1L);
        brand.setBrandName("나이키");

        Item item = new Item();
        item.setName("나이키 티셔츠");
        item.setPrice(50000);
        item.setBrand(brand);
        brand.getItems().add(item);

        given(brandRepository.findById(1L)).willReturn(Optional.of(brand));

        // when
        brandService.delete(1L);

        // then
        verify(brandRepository).deleteById(1L);
    }
} 