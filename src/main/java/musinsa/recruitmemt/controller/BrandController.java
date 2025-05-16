package musinsa.recruitmemt.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import musinsa.recruitmemt.model.Brand;
import musinsa.recruitmemt.service.BrandService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/brands")
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;

    @GetMapping
    public String list(Model model) {
        List<Brand> brands = brandService.findAll();
        model.addAttribute("brands", brands);
        return "brand/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("brand", new Brand());
        return "brand/form";
    }

    @PostMapping
    public String create(@ModelAttribute Brand brand) {
        brandService.save(brand);
        return "redirect:/brands";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Brand brand = brandService.findById(id);
        model.addAttribute("brand", brand);
        return "brand/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Brand brand) {
        brand.setBrandId(id);
        brandService.save(brand);
        return "redirect:/brands";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        log.info("브랜드 삭제 요청: id={}", id);
        try {
            brandService.delete(id);
            log.info("브랜드 삭제 성공: id={}", id);
            return "redirect:/brands";
        } catch (Exception e) {
            log.error("브랜드 삭제 실패: id={}, error={}", id, e.getMessage(), e);
            return "redirect:/brands";
        }
    }
} 