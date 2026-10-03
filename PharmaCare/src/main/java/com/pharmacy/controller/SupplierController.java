package com.pharmacy.controller;

import com.pharmacy.model.Supplier;
import com.pharmacy.repository.SupplierRepository;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/suppliers")
public class SupplierController {

    private final SupplierRepository supplierRepo;

    public SupplierController(SupplierRepository supplierRepo) {
        this.supplierRepo = supplierRepo;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String keyword, Model model) {
        boolean hasKeyword = keyword != null && !keyword.isBlank();
        model.addAttribute("suppliers", hasKeyword
                ? supplierRepo.findByNameContainingIgnoreCaseOrderByName(keyword.trim())
                : supplierRepo.findAllByOrderByNameAsc());
        model.addAttribute("keyword", keyword);
        model.addAttribute("active", "suppliers");
        return "suppliers/list";
    }

    @GetMapping("/new")
    public String addForm(Model model) {
        model.addAttribute("supplier", new Supplier());
        model.addAttribute("active", "suppliers");
        return "suppliers/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("supplier", supplierRepo.findById(id).orElseThrow());
        model.addAttribute("active", "suppliers");
        return "suppliers/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("supplier") Supplier supplier, BindingResult result,
                       Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("active", "suppliers");
            return "suppliers/form";
        }
        supplierRepo.save(supplier);
        ra.addFlashAttribute("success", "Supplier '" + supplier.getName() + "' saved.");
        return "redirect:/suppliers";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            supplierRepo.deleteById(id);
            supplierRepo.flush();
            ra.addFlashAttribute("success", "Supplier deleted.");
        } catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("error", "Cannot delete: medicines are linked to this supplier.");
        }
        return "redirect:/suppliers";
    }
}
