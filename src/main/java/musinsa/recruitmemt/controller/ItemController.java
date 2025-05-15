package musinsa.recruitmemt.controller;


import lombok.RequiredArgsConstructor;
import musinsa.recruitmemt.dto.BrandCreateDto;
import musinsa.recruitmemt.dto.BrandTotalPriceDto;
import musinsa.recruitmemt.dto.CategoryMinPriceDto;
import musinsa.recruitmemt.dto.CategoryPriceRangeDto;
import musinsa.recruitmemt.dto.PriceTableDto;
import musinsa.recruitmemt.model.Brand;
import musinsa.recruitmemt.model.Category;
import musinsa.recruitmemt.model.Item;
import musinsa.recruitmemt.service.BrandService;
import musinsa.recruitmemt.service.CategoryService;
import musinsa.recruitmemt.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final BrandService brandService;
    private final CategoryService categoryService;

    @GetMapping
    public String list(Model model) {
        List<Brand> brands = brandService.findAll();
        List<Category> categories = categoryService.findAll();
        List<Item> items = itemService.findAll();

        // 가격표 데이터 생성
        List<PriceTableDto> priceTableDto = brands.stream()
                .map(brand -> {
                    PriceTableDto dto = new PriceTableDto(brand.getBrandName());
                    Map<String, Integer> prices = items.stream()
                            .filter(item -> item.getBrand().getBrandId().equals(brand.getBrandId()))
                            .collect(Collectors.toMap(
                                    item -> item.getCategory().getCategoryName(),
                                    Item::getPrice
                            ));
                    dto.setCategoryPrices(prices);
                    return dto;
                })
                .collect(Collectors.toList());

        model.addAttribute("priceTable", priceTableDto);
        model.addAttribute("categories", categories);
        return "item/list";
    }

    // 1. 카테고리별 최저가격 UI
    @GetMapping("/lowest-prices")
    public String getLowestPrices(Model model) {
        List<CategoryMinPriceDto> lowestPrices = itemService.findMinPricesByCategory();
        int totalPrice = lowestPrices.stream()
                .mapToInt(CategoryMinPriceDto::getPrice)
                .sum();

        model.addAttribute("lowestPrices", lowestPrices);
        model.addAttribute("totalPrice", totalPrice);
        return "item/summary/lowest-prices";
    }

    // 2. 브랜드별 총액 UI
    @GetMapping("/brand-totals")
    public String getBrandTotals(Model model) {
        List<BrandTotalPriceDto> brandTotals = itemService.findTotalPricesByBrand();
        model.addAttribute("brandTotals", brandTotals);
        return "item/summary/brand-totals";
    }

    // 3. 카테고리 가격 범위 UI
    @GetMapping("/category-range")
    public String getCategoryRange(@RequestParam String categoryName, Model model) {
        CategoryPriceRangeDto priceRange = itemService.findPriceRangeByCategory(categoryName);
        model.addAttribute("priceRange", priceRange);
        return "item/summary/category-range";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("item", new Item());
        model.addAttribute("brands", brandService.findAll());
        return "item/form";
    }

    @PostMapping
    public String create(@ModelAttribute Item item, @RequestParam Long brandId) {
        itemService.save(item, brandId);
        return "redirect:/items";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("item", itemService.findById(id));
        model.addAttribute("brands", brandService.findAll());
        return "item/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Item item, @RequestParam Long brandId) {
        item.setItemId(id);
        itemService.save(item, brandId);
        return "redirect:/items";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        itemService.delete(id);
        return "redirect:/items";
    }

    @PostMapping("/brands")
    public String createBrandWithItems(@ModelAttribute BrandCreateDto brandCreateDto) {
        // 브랜드 생성
        Brand brand = new Brand();
        brand.setBrandName(brandCreateDto.getBrandName());
        brandService.save(brand);

        // 각 카테고리별 상품 생성
        brandCreateDto.getItems().forEach((categoryName, itemInfo) -> {
            if (itemInfo.getName() != null && !itemInfo.getName().trim().isEmpty() 
                && itemInfo.getPrice() != null) {
                Category category = categoryService.findByCategoryName(categoryName);
                
                Item item = new Item();
                item.setName(itemInfo.getName());
                item.setPrice(itemInfo.getPrice());
                item.setBrand(brand);
                item.setCategory(category);
                
                itemService.save(item, brand.getBrandId());
            }
        });

        return "redirect:/items";
    }
}