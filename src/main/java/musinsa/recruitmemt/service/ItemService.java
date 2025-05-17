package musinsa.recruitmemt.service;

import lombok.RequiredArgsConstructor;
import musinsa.recruitmemt.dto.BrandTotalPriceDto;
import musinsa.recruitmemt.dto.CategoryMinPriceDto;
import musinsa.recruitmemt.dto.CategoryPriceRangeDto;
import musinsa.recruitmemt.exception.*;
import musinsa.recruitmemt.model.Brand;
import musinsa.recruitmemt.model.Category;
import musinsa.recruitmemt.model.Item;
import musinsa.recruitmemt.repository.BrandRepository;
import musinsa.recruitmemt.repository.CategoryRepository;
import musinsa.recruitmemt.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {
    private final ItemRepository itemRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;

    public List<Item> findAll() {
        List<Item> items = itemRepository.findAll();
        if (items.isEmpty()) {
            throw new NoItemsFoundException("등록된 상품이 없습니다.");
        }
        return items;
    }

    public Item findById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("상품을 찾을 수 없습니다. (ID: " + id + ")"));
    }

    public List<Item> findByBrandId(Long brandId) {
        List<Item> items = itemRepository.findByBrandBrandId(brandId);
        if (items.isEmpty()) {
            throw new NoItemsFoundException("해당 브랜드의 상품이 없습니다. (브랜드 ID: " + brandId + ")");
        }
        return items;
    }

    public List<Item> findByCategoryName(String categoryName) {
        List<Item> items = itemRepository.findByCategoryCategoryName(categoryName);
        if (items.isEmpty()) {
            throw new NoItemsFoundException("해당 카테고리의 상품이 없습니다. (카테고리: " + categoryName + ")");
        }
        return items;
    }

    public List<Item> findByCategoryId(Long categoryId) {
        List<Item> items = itemRepository.findByCategoryCategoryId(categoryId);
        if (items.isEmpty()) {
            throw new NoItemsFoundException("해당 카테고리의 상품이 없습니다. (카테고리 ID: " + categoryId + ")");
        }
        return items;
    }

    public List<Item> findByBrandIdAndCategoryId(Long brandId, Long categoryId) {
        List<Item> items = itemRepository.findByBrandBrandIdAndCategoryCategoryId(brandId, categoryId);
        if (items.isEmpty()) {
            throw new NoItemsFoundException(
                String.format("해당 브랜드와 카테고리의 상품이 없습니다. (브랜드 ID: %d, 카테고리 ID: %d)", brandId, categoryId)
            );
        }
        return items;
    }

    public List<Item> findByPriceRange(int minPrice, int maxPrice) {
        if (minPrice > maxPrice) {
            throw new IllegalArgumentException("최소 가격이 최대 가격보다 클 수 없습니다.");
        }
        List<Item> items = itemRepository.findByPriceBetween(minPrice, maxPrice);
        if (items.isEmpty()) {
            throw new NoItemsFoundException(
                String.format("해당 가격 범위의 상품이 없습니다. (최소: %d원, 최대: %d원)", minPrice, maxPrice)
            );
        }
        return items;
    }

    @Transactional
    public Item save(Item item, Long brandId) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new BrandNotFoundException("브랜드를 찾을 수 없습니다. (ID: " + brandId + ")"));
        
        if (item.getPrice() == null || item.getPrice() <= 0) {
            throw new PriceNotDefinedException("상품 가격은 0보다 커야 합니다.");
        }
        
        item.setBrand(brand);
        item.setUpdateTime(LocalDateTime.now());
        return itemRepository.save(item);
    }

    @Transactional
    public void delete(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ItemNotFoundException("삭제할 상품을 찾을 수 없습니다. (ID: " + id + ")");
        }
        itemRepository.deleteById(id);
    }

    public List<CategoryMinPriceDto> findMinPricesByCategory() {
        List<Item> items = itemRepository.findAll();
        if (items.isEmpty()) {
            throw new NoItemsFoundException("등록된 상품이 없습니다.");
        }

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

    public List<BrandTotalPriceDto> findTotalPricesByBrand() {
        List<Item> items = itemRepository.findAll();
        if (items.isEmpty()) {
            throw new NoItemsFoundException("등록된 상품이 없습니다.");
        }

        Map<String, BrandTotalPriceDto> result = new HashMap<>();

        items.forEach(item -> {
            if (item.getPrice() == null) {
                throw new PriceNotDefinedException("가격이 정의되지 않은 상품이 있습니다. (상품 ID: " + item.getItemId() + ")");
            }

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

    public BrandTotalPriceDto findMinTotalPricesByBrand() {
        List<BrandTotalPriceDto> items = findTotalPricesByBrand();
        if (items == null || items.isEmpty()) {
            throw new NoItemsFoundException("브랜드별 가격 정보를 찾을 수 없습니다.");
        }
        return items.stream()
                .filter(dto -> dto.getTotalPrice() != 0)
                .min(Comparator.comparing(BrandTotalPriceDto::getTotalPrice))
                .orElseThrow(() -> new PriceNotDefinedException("가격이 정의되지 않은 브랜드가 있습니다."));
    }

    public CategoryPriceRangeDto findPriceRangeByCategory(String categoryName) {

        List<Item> items = itemRepository.findByCategoryCategoryName(categoryName);
        if (items.isEmpty()) {
            throw new NoItemsFoundException("해당 카테고리에 등록된 상품이 없습니다. (카테고리: " + categoryName + ")");
        }

        Optional<Item> minItem = items.stream()
                .filter(item -> item.getPrice() != null)
                .min(Comparator.comparing(Item::getPrice));
                
        Optional<Item> maxItem = items.stream()
                .filter(item -> item.getPrice() != null)
                .max(Comparator.comparing(Item::getPrice));

        if (!minItem.isPresent() || !maxItem.isPresent()) {
            throw new PriceNotDefinedException("해당 카테고리의 상품 중 가격이 정의되지 않은 상품이 있습니다.");
        }

        CategoryPriceRangeDto categoryPrice = new CategoryPriceRangeDto();
        categoryPrice.setCategoryName(categoryName);
        categoryPrice.setLowestPrice(new CategoryPriceRangeDto.BrandPrice(
                minItem.get().getBrand().getBrandName(),
                minItem.get().getPrice()
        ));
        categoryPrice.setHighestPrice(new CategoryPriceRangeDto.BrandPrice(
                maxItem.get().getBrand().getBrandName(),
                maxItem.get().getPrice()
        ));

        return categoryPrice;
    }

    public List<BrandTotalPriceDto> findLatestPricesByBrand() {
        List<Item> latestItems = itemRepository.findLatestItemsByBrandAndCategory();
        if (latestItems.isEmpty()) {
            throw new NoItemsFoundException("등록된 상품이 없습니다.");
        }

        // 브랜드별로 그룹화
        Map<String, List<Item>> itemsByBrand = latestItems.stream()
                .collect(Collectors.groupingBy(item -> item.getBrand().getBrandName()));

        // 각 브랜드별로 BrandTotalPriceDto 생성
        return itemsByBrand.entrySet().stream()
                .map(entry -> {
                    String brandName = entry.getKey();
                    List<Item> brandItems = entry.getValue();
                    // 첫 번째 아이템으로 기본 DTO 생성
                    BrandTotalPriceDto dto = BrandTotalPriceDto.of(brandItems.get(0));

                    // 나머지 아이템들의 카테고리 가격 정보 추가
                    brandItems.stream()
                            .skip(1) // 첫 번째 아이템은 이미 처리했으므로 건너뜀
                            .forEach(item -> {
                                dto.getCategoryPrices().put(
                                    item.getCategory().getCategoryName(),
                                    item.getPrice()
                                );
                                // 총액 업데이트
                                dto.setTotalPrice(dto.getTotalPrice() + item.getPrice());
                            });

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public long count() {
        return itemRepository.count();
    }

    public BrandTotalPriceDto findLowestTotalPriceBrand() {
        List<Item> latestItems = itemRepository.findLatestItemsByBrandAndCategory();
        if (latestItems.isEmpty()) {
            throw new NoItemsFoundException("등록된 상품이 없습니다");
        }

        // 브랜드별로 그룹화하여 총액 계산
        return latestItems.stream()
            .collect(Collectors.groupingBy(
                Item::getBrand,
                Collectors.collectingAndThen(
                    Collectors.toMap(
                        item -> item.getCategory().getCategoryName(),
                        Item::getPrice,
                        (price1, price2) -> price1 // 동일 카테고리의 경우 첫 번째 값 사용 (이미 최신 데이터만 조회됨)
                    ),
                    (categoryPrices) -> {
                        Brand brand = latestItems.stream()
                            .filter(item -> item.getBrand().getBrandName().equals(categoryPrices.keySet().iterator().next()))
                            .map(Item::getBrand)
                            .findFirst()
                            .orElseThrow(() -> new NoItemsFoundException("브랜드 정보를 찾을 수 없습니다"));
                            
                        int totalPrice = categoryPrices.values().stream()
                            .mapToInt(Integer::intValue)
                            .sum();
                            
                        return new BrandTotalPriceDto(
                            brand.getBrandName(),
                            categoryPrices,
                            totalPrice
                        );
                    }
                )
            ))
            .values()
            .stream()
            .min(Comparator.comparing(BrandTotalPriceDto::getTotalPrice))
            .orElseThrow(() -> new NoItemsFoundException("브랜드 정보를 찾을 수 없습니다"));
    }
}