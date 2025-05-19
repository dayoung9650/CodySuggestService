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

    @Transactional(readOnly = true)
    public List<CategoryMinPriceDto> findMinPricesByCategory() {
        List<Item> items = itemRepository.findAll();
        if (items.isEmpty()) {
            throw new NoItemsFoundException("등록된 상품이 없습니다");
        }

        return items.stream()
            .collect(Collectors.groupingBy(
                item -> item.getCategory().getCategoryName(),
                Collectors.collectingAndThen(
                    Collectors.minBy(Comparator.comparing(Item::getPrice)),
                    optionalItem -> optionalItem.map(item -> new CategoryMinPriceDto(
                        item.getCategory().getCategoryName(),
                        item.getBrand().getBrandName(),
                        item.getPrice()
                    )).orElseThrow(() -> new NoItemsFoundException("해당 카테고리의 상품을 찾을 수 없습니다"))
                )
            ))
            .values()
            .stream()
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

    @Transactional(readOnly = true)
    public CategoryPriceRangeDto findPriceRangeByCategory(String categoryName) {
        List<Item> items = itemRepository.findByCategoryCategoryName(categoryName);
        if (items.isEmpty()) {
            throw new NoItemsFoundException("해당 카테고리의 상품을 찾을 수 없습니다");
        }

        Item lowestPriceItem = items.stream()
            .min(Comparator.comparing(Item::getPrice))
            .orElseThrow(() -> new NoItemsFoundException("해당 카테고리의 상품을 찾을 수 없습니다"));

        Item highestPriceItem = items.stream()
            .max(Comparator.comparing(Item::getPrice))
            .orElseThrow(() -> new NoItemsFoundException("해당 카테고리의 상품을 찾을 수 없습니다"));

        CategoryPriceRangeDto.BrandPrice lowestPrice = new CategoryPriceRangeDto.BrandPrice(
            lowestPriceItem.getBrand().getBrandName(),
            lowestPriceItem.getPrice()
        );

        CategoryPriceRangeDto.BrandPrice highestPrice = new CategoryPriceRangeDto.BrandPrice(
            highestPriceItem.getBrand().getBrandName(),
            highestPriceItem.getPrice()
        );

        CategoryPriceRangeDto result = new CategoryPriceRangeDto();
        result.setCategoryName(categoryName);
        result.setLowestPrice(lowestPrice);
        result.setHighestPrice(highestPrice);

        return result;
    }

    @Transactional(readOnly = true)
    public List<BrandTotalPriceDto> findLatestPricesByBrand() {
        List<Item> latestItems = itemRepository.findLatestItemsByBrandAndCategory();
        if (latestItems.isEmpty()) {
            throw new NoItemsFoundException("등록된 상품이 없습니다");
        }

        Map<String, Map<String, Integer>> brandCategoryPrices = latestItems.stream()
            .collect(Collectors.groupingBy(
                item -> item.getBrand().getBrandName(),
                Collectors.groupingBy(
                    item -> item.getCategory().getCategoryName(),
                    Collectors.collectingAndThen(
                        Collectors.maxBy(Comparator.comparing(Item::getUpdateTime)),
                        optItem -> optItem.map(Item::getPrice)
                            .orElseThrow(() -> new NoItemsFoundException("해당 브랜드/카테고리의 상품을 찾을 수 없습니다"))
                    )
                )
            ));

        return brandCategoryPrices.entrySet().stream()
            .map(entry -> {
                String brandName = entry.getKey();
                Map<String, Integer> categoryPrices = entry.getValue();
                int totalPrice = categoryPrices.values().stream().mapToInt(Integer::intValue).sum();
                return new BrandTotalPriceDto(brandName, totalPrice, categoryPrices);
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

        // 브랜드별로 카테고리 가격을 저장할 맵
        Map<Brand, Map<String, Integer>> brandCategoryPrices = new HashMap<>();

        // 각 상품을 브랜드별, 카테고리별로 정리
        for (Item item : latestItems) {
            Brand brand = item.getBrand();
            String categoryName = item.getCategory().getCategoryName();
            int price = item.getPrice();

            brandCategoryPrices.computeIfAbsent(brand, k -> new HashMap<>());
            // 이미 해당 카테고리의 가격이 있다면 무시 (최신 데이터만 사용)
            brandCategoryPrices.get(brand).putIfAbsent(categoryName, price);
        }

        // 브랜드별 총액을 계산하고 DTO 생성
        List<BrandTotalPriceDto> brandTotalPrices = new ArrayList<>();
        for (Map.Entry<Brand, Map<String, Integer>> entry : brandCategoryPrices.entrySet()) {
            Brand brand = entry.getKey();
            Map<String, Integer> categoryPrices = entry.getValue();
            
            // 카테고리별 가격 총합 계산
            int totalPrice = 0;
            for (int price : categoryPrices.values()) {
                totalPrice += price;
            }

            brandTotalPrices.add(new BrandTotalPriceDto(
                brand.getBrandName(),
                totalPrice,
                categoryPrices
            ));
        }

        // 최저가 브랜드 찾기
        if (brandTotalPrices.isEmpty()) {
            throw new NoItemsFoundException("브랜드 정보를 찾을 수 없습니다");
        }

        BrandTotalPriceDto lowestPriceBrand = brandTotalPrices.get(0);
        for (BrandTotalPriceDto dto : brandTotalPrices) {
            if (dto.getTotalPrice() < lowestPriceBrand.getTotalPrice()) {
                lowestPriceBrand = dto;
            }
        }

        return lowestPriceBrand;
    }
}