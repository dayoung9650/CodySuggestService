package musinsa.recruitmemt.repository;

import musinsa.recruitmemt.model.Brand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BrandRepositoryTest {

    @Autowired
    private BrandRepository brandRepository;

    @Test
    @DisplayName("브랜드 저장 테스트")
    void saveBrand() {
        // given
        Brand brand = new Brand();
        brand.setBrandName("나이키");

        // when
        Brand savedBrand = brandRepository.save(brand);

        // then
        assertThat(savedBrand.getBrandId()).isNotNull();
        assertThat(savedBrand.getBrandName()).isEqualTo("나이키");
    }

    @Test
    @DisplayName("브랜드 조회 테스트")
    void findBrand() {
        // given
        Brand brand = new Brand();
        brand.setBrandName("아디다스");
        brandRepository.save(brand);

        // when
        Brand foundBrand = brandRepository.findById(brand.getBrandId()).orElse(null);

        // then
        assertThat(foundBrand).isNotNull();
        assertThat(foundBrand.getBrandName()).isEqualTo("아디다스");
    }

    @Test
    @DisplayName("존재하지 않는 브랜드 조회 테스트")
    void findNonExistentBrand() {
        // when
        Brand foundBrand = brandRepository.findById(999L).orElse(null);

        // then
        assertThat(foundBrand).isNull();
    }
} 