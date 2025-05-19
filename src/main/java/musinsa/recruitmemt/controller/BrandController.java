package musinsa.recruitmemt.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import musinsa.recruitmemt.model.Brand;
import musinsa.recruitmemt.service.BrandService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
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
    public String create(@Valid @ModelAttribute Brand brand, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.error("브랜드 등록 실패: {}", bindingResult.getAllErrors());
            return "brand/form";
        }

        try {
            brandService.save(brand);
            redirectAttributes.addFlashAttribute("successMessage", "브랜드가 성공적으로 등록되었습니다.");
            return "redirect:/brands";
        } catch (Exception e) {
            log.error("브랜드 등록 실패: {}", e.getMessage(), e);
            bindingResult.rejectValue("brandName", "error.brand", "브랜드 등록 중 오류가 발생했습니다.");
            return "brand/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String updateForm(@PathVariable Long id, Model model) {
        model.addAttribute("brand", brandService.findById(id));
        return "brand/form";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute Brand brand, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.error("브랜드 수정 실패: {}", bindingResult.getAllErrors());
            return "brand/form";
        }

        try {
            brand.setBrandId(id);
            brandService.save(brand);
            redirectAttributes.addFlashAttribute("successMessage", "브랜드가 성공적으로 수정되었습니다.");
            return "redirect:/brands";
        } catch (Exception e) {
            log.error("브랜드 수정 실패: {}", e.getMessage(), e);
            bindingResult.rejectValue("brandName", "error.brand", "브랜드 수정 중 오류가 발생했습니다.");
            return "brand/form";
        }
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("브랜드 삭제 요청: id={}", id);
        try {
            brandService.delete(id);
            log.info("브랜드 삭제 성공: id={}", id);
            redirectAttributes.addFlashAttribute("successMessage", "브랜드가 성공적으로 삭제되었습니다.");
            return "redirect:/brands";
        } catch (Exception e) {
            log.error("브랜드 삭제 실패: id={}, error={}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "브랜드 삭제 중 오류가 발생했습니다.");
            return "redirect:/brands";
        }
    }
} 