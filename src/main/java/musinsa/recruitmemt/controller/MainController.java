package musinsa.recruitmemt.controller;

import lombok.RequiredArgsConstructor;
import musinsa.recruitmemt.service.BrandService;
import musinsa.recruitmemt.service.CategoryService;
import musinsa.recruitmemt.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
@RequiredArgsConstructor
public class MainController {
    private final BrandService brandService;
    private final ItemService itemService;
    private final CategoryService categoryService;
    @GetMapping("/")
    public String home() {
        return "redirect:/items";
    }
}
