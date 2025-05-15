package musinsa.recruitmemt.service;
import lombok.RequiredArgsConstructor;
import musinsa.recruitmemt.model.Brand;
import musinsa.recruitmemt.repository.BrandRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

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
        return brandRepository.save(brand);
    }

    @Transactional
    public void delete(Long id) {
        brandRepository.deleteById(id);
    }
}