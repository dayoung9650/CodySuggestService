package musinsa.recruitmemt.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import musinsa.recruitmemt.model.Brand;
import musinsa.recruitmemt.repository.BrandRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {
    private final BrandRepository brandRepository;

    public List<Brand> findAll() {
        return brandRepository.findAll();
    }

    public Brand findById(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("브랜드를 찾을 수 없습니다."));
    }
    public long count() {
        return brandRepository.count();
    }
    @Transactional
    public Brand save(Brand brand) {
        log.info("브랜드 저장 시작: id={}, name={}", brand.getBrandId(), brand.getBrandName());
        
        // 기존 브랜드가 있는 경우 (수정)
        if (brand.getBrandId() != null) {
            Brand existingBrand = findById(brand.getBrandId());
            String oldName = existingBrand.getBrandName();
            String newName = brand.getBrandName();
            
            // 브랜드명이 변경된 경우
            if (!oldName.equals(newName)) {
                log.info("브랜드명 변경 감지: {} -> {}", oldName, newName);
                existingBrand.setBrandName(newName);
                
                // 연관된 상품들의 브랜드 정보도 함께 업데이트
                existingBrand.getItems().forEach(item -> {
                    log.info("상품 브랜드명 업데이트: itemId={}, oldBrandName={}, newBrandName={}", 
                            item.getItemId(), oldName, newName);
                });
                
                return brandRepository.save(existingBrand);
            }
            return brandRepository.save(existingBrand);
        }
        
        // 새로운 브랜드 생성
        log.info("새 브랜드 생성: name={}", brand.getBrandName());
        return brandRepository.save(brand);
    }

    @Transactional
    public void delete(Long id) {
        log.info("브랜드 삭제 서비스 호출: id={}", id);
        try {
            Brand brand = findById(id);
            log.info("삭제할 브랜드 조회 성공: id={}, name={}, items={}", id, brand.getBrandName(), brand.getItems().size());
            brandRepository.deleteById(id);
            log.info("브랜드 삭제 완료: id={}", id);
        } catch (Exception e) {
            log.error("브랜드 삭제 서비스 실패: id={}, error={}", id, e.getMessage(), e);
            throw e;
        }
    }
}