package musinsa.recruitmemt.controller;

import lombok.RequiredArgsConstructor;
import musinsa.recruitmemt.dto.PriceTableDto;
import musinsa.recruitmemt.model.Brand;
import musinsa.recruitmemt.model.Category;
import musinsa.recruitmemt.model.Item;
import musinsa.recruitmemt.service.BrandService;
import musinsa.recruitmemt.service.CategoryService;
import musinsa.recruitmemt.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final BrandService brandService;
    private final ItemService itemService;
    private final CategoryService categoryService;

    @GetMapping("/")
    public String home(Model model) {
        List<Brand> brands = brandService.findAll();
        List<Category> categories = categoryService.findAll();
        List<Item> items = itemService.findAll();

        // 가격표 데이터 생성 (각 브랜드/카테고리 조합별 최신 상품만)
        List<PriceTableDto> priceTableDto = brands.stream()
                .map(brand -> {
                    PriceTableDto dto = new PriceTableDto(brand.getBrandName());
                    Map<String, Integer> prices = items.stream()
                            .filter(item -> item.getBrand().getBrandId().equals(brand.getBrandId()))
                            .collect(Collectors.groupingBy(
                                    item -> item.getCategory().getCategoryName(),
                                    Collectors.collectingAndThen(
                                            Collectors.maxBy((i1, i2) -> i1.getInsertTime().compareTo(i2.getInsertTime())),
                                            optionalItem -> optionalItem.map(Item::getPrice).orElse(null)
                                    )
                            ));
                    dto.setCategoryPrices(prices);
                    return dto;
                })
                .collect(Collectors.toList());

        model.addAttribute("priceTable", priceTableDto);
        model.addAttribute("categories", categories);
        return "home";
    }
}
