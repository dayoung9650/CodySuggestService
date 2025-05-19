package musinsa.recruitmemt.controller;

import lombok.RequiredArgsConstructor;
import musinsa.recruitmemt.dto.BrandTotalPriceDto;
import musinsa.recruitmemt.dto.CategoryMinPriceDto;
import musinsa.recruitmemt.dto.CategoryPriceRangeDto;
import musinsa.recruitmemt.dto.LowestBrandResponseDto;
import musinsa.recruitmemt.model.Brand;
import musinsa.recruitmemt.service.ItemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemApiController {
    private final ItemService itemService;
    // 1. 카테고리별 최저가격 API
    @GetMapping("/lowest-prices")
    public List<CategoryMinPriceDto> getLowestPrices() {
        return itemService.findMinPricesByCategory();
    }

    // 2. 총액이 가장 작은 브랜드 API
    @GetMapping("/lowest-brand")
    public LowestBrandResponseDto getBrandTotals() {
        BrandTotalPriceDto lowestBrand = itemService.findMinTotalPricesByBrand();
        
        LowestBrandResponseDto response = new LowestBrandResponseDto();
        LowestBrandResponseDto.LowestBrand lowestBrandData = new LowestBrandResponseDto.LowestBrand();
        
        lowestBrandData.setBrandName(lowestBrand.getBrandName());
        lowestBrandData.setTotalPrice(String.format("%,d", lowestBrand.getTotalPrice()));
        
        List<LowestBrandResponseDto.CategoryPrice> categoryPrices = lowestBrand.getCategoryPrices().entrySet().stream()
            .map(entry -> {
                LowestBrandResponseDto.CategoryPrice categoryPrice = new LowestBrandResponseDto.CategoryPrice();
                categoryPrice.setCategory(entry.getKey());
                categoryPrice.setPrice(String.format("%,d", entry.getValue()));
                return categoryPrice;
            })
            .collect(Collectors.toList());
        
        lowestBrandData.setCategoryPrices(categoryPrices);
        response.setLowestBrand(lowestBrandData);
        
        return response;
    }

    // 3. 카테고리 가격 범위 API
    @GetMapping("/category-range")
    public CategoryPriceRangeDto getCategoryRange(@RequestParam String categoryName) {
        return itemService.findPriceRangeByCategory(categoryName);
    }
}
