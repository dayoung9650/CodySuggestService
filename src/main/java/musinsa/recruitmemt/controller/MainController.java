package musinsa.recruitmemt.controller;

import lombok.RequiredArgsConstructor;
import musinsa.recruitmemt.dto.BrandTotalPriceDto;
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
    public String main(Model model) {
        List<Brand> brands = brandService.findAll();
        List<Category> categories = categoryService.findAll();
        List<BrandTotalPriceDto> priceTable = itemService.findLatestPricesByBrand();

        model.addAttribute("priceTable", priceTable);
        model.addAttribute("categories", categories);
        model.addAttribute("brands", brands);
        return "main";
    }
}
