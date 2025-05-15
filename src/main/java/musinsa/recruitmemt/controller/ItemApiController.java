package musinsa.recruitmemt.controller;

import lombok.RequiredArgsConstructor;
import musinsa.recruitmemt.dto.BrandTotalPriceDto;
import musinsa.recruitmemt.dto.CategoryMinPriceDto;
import musinsa.recruitmemt.dto.CategoryPriceRangeDto;
import musinsa.recruitmemt.service.ItemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    // 2. 브랜드별 총액 API
    @GetMapping("/brand-totals")
    public List<BrandTotalPriceDto> getBrandTotals() {
        return itemService.findTotalPricesByBrand();
    }

    // 3. 카테고리 가격 범위 API
    @GetMapping("/category-range")
    public CategoryPriceRangeDto getCategoryRange(@RequestParam String categoryName) {
        return itemService.findPriceRangeByCategory(categoryName);
    }
}
