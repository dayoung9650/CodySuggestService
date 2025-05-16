package musinsa.recruitmemt.service;

import lombok.RequiredArgsConstructor;
import musinsa.recruitmemt.dto.BrandTotalPriceDto;
import musinsa.recruitmemt.dto.CategoryMinPriceDto;
import musinsa.recruitmemt.dto.CategoryPriceRangeDto;
import musinsa.recruitmemt.model.Brand;
import musinsa.recruitmemt.model.Item;
import musinsa.recruitmemt.repository.BrandRepository;
import musinsa.recruitmemt.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {
    private final ItemRepository itemRepository;
    private final BrandRepository brandRepository;

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Item findById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
    }


    public List<Item> findByBrandId(Long brandId) {
        return itemRepository.findByBrandBrandId(brandId);
    }

    public List<Item> findByCategoryName(String categoryName) {
        return itemRepository.findByCategoryCategoryName(categoryName);
    }
    public List<Item> findByCategoryId(Long categoryId) {
        return itemRepository.findByCategoryCategoryId(categoryId);
    }

    public List<Item> findByBrandIdAndCategoryId(Long brandId, Long categoryId) {
        return itemRepository.findByBrandBrandIdAndCategoryCategoryId(brandId, categoryId);
    }

    // 가격 범위로 상품 조회
    public List<Item> findByPriceRange(int minPrice, int maxPrice) {
        return itemRepository.findByPriceBetween(minPrice, maxPrice);
    }

    @Transactional
    public Item save(Item item, Long brandId) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new RuntimeException("브랜드를 찾을 수 없습니다."));
        item.setBrand(brand);
        return itemRepository.save(item);
    }

    @Transactional
    public void delete(Long id) {
        itemRepository.deleteById(id);
    }


    // 1. 카테고리별 최저가격 브랜드와 가격 조회
    public List<CategoryMinPriceDto> findMinPricesByCategory() {
        List<Item> items = itemRepository.findAll();

        return items.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getCategory().getCategoryName(),
                        Collectors.minBy(Comparator.comparing(Item::getPrice))
                ))
                .entrySet().stream()
                .map(entry -> new CategoryMinPriceDto(
                        entry.getKey(),
                        entry.getValue().get().getBrand().getBrandName(),
                        entry.getValue().get().getPrice()
                ))
                .collect(Collectors.toList());
    }

    // 2. 브랜드별 전체 카테고리 구매 시 총액
    public List<BrandTotalPriceDto> findTotalPricesByBrand() {
        List<Item> items = itemRepository.findAll();

        Map<String, BrandTotalPriceDto> result = new HashMap<>();

        items.forEach(item -> {
            String brandName = item.getBrand().getBrandName();
            result.computeIfAbsent(brandName,
                    k -> new BrandTotalPriceDto(brandName, 0));

            BrandTotalPriceDto dto = result.get(brandName);
            dto.setTotalPrice(dto.getTotalPrice() + item.getPrice());
            dto.getCategoryPrices().put(
                    item.getCategory().getCategoryName(),
                    item.getPrice()
            );
        });

        return new ArrayList<>(result.values());
    }

    // 3. 특정 카테고리의 최저/최고 가격 브랜드 조회
    public CategoryPriceRangeDto findPriceRangeByCategory(String categoryName) {
        List<Item> items = itemRepository.findByCategoryCategoryName(categoryName);

        if (items.isEmpty()) {
            throw new RuntimeException("해당 카테고리의 상품이 없습니다.");
        }

        Item minItem = items.stream()
                .min(Comparator.comparing(Item::getPrice))
                .get();

        Item maxItem = items.stream()
                .max(Comparator.comparing(Item::getPrice))
                .get();

        CategoryPriceRangeDto dto = new CategoryPriceRangeDto();
        dto.setCategoryName(categoryName);
        dto.setLowestPrice(new CategoryPriceRangeDto.BrandPrice(
                minItem.getBrand().getBrandName(),
                minItem.getPrice()
        ));
        dto.setHighestPrice(new CategoryPriceRangeDto.BrandPrice(
                maxItem.getBrand().getBrandName(),
                maxItem.getPrice()
        ));

        return dto;
    }

    // 최신 상품 기준 브랜드별 카테고리 가격 조회
    public List<BrandTotalPriceDto> findLatestPricesByBrand() {
        List<Item> allItems = itemRepository.findAll();

        // 브랜드별, 카테고리별로 최신 상품만 필터링
        Map<String, Map<String, Item>> latestItems = allItems.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getBrand().getBrandName(),
                        Collectors.groupingBy(
                                item -> item.getCategory().getCategoryName(),
                                Collectors.collectingAndThen(
                                        Collectors.maxBy(Comparator.comparing(Item::getInsertTime)),
                                        optional -> optional.orElse(null)
                                )
                        )
                ));

        // 브랜드별 DTO 생성
        return latestItems.entrySet().stream()
                .map(brandEntry -> {
                    String brandName = brandEntry.getKey();
                    Map<String, Item> categoryItems = brandEntry.getValue();
                    
                    BrandTotalPriceDto dto = new BrandTotalPriceDto(brandName, 0);
                    Map<String, Integer> categoryPrices = new HashMap<>();
                    
                    int totalPrice = 0;
                    for (Map.Entry<String, Item> entry : categoryItems.entrySet()) {
                        if (entry.getValue() != null) {
                            categoryPrices.put(entry.getKey(), entry.getValue().getPrice());
                            totalPrice += entry.getValue().getPrice();
                        }
                    }
                    
                    dto.setTotalPrice(totalPrice);
                    dto.setCategoryPrices(categoryPrices);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public long count() {
        return itemRepository.count();
    }
}