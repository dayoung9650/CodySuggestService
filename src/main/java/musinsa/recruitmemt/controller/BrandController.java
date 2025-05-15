package musinsa.recruitmemt.controller;

import lombok.RequiredArgsConstructor;
import musinsa.recruitmemt.model.Brand;
import musinsa.recruitmemt.service.BrandService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/brands")
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("brands", brandService.findAll());
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
        model.addAttribute("brand", brandService.findById(id));
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
        brandService.delete(id);
        return "redirect:/brands";
    }
}