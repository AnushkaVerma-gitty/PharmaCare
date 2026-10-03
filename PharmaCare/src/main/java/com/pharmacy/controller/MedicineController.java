package com.pharmacy.controller;

import com.pharmacy.model.Medicine;
import com.pharmacy.repository.MedicineRepository;
import com.pharmacy.repository.SupplierRepository;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/medicines")
public class MedicineController {

    private final MedicineRepository medicineRepo;
    private final SupplierRepository supplierRepo;

    public MedicineController(MedicineRepository medicineRepo, SupplierRepository supplierRepo) {
        this.medicineRepo = medicineRepo;
        this.supplierRepo = supplierRepo;
    }

    // List + search: /medicines?keyword=para
    @GetMapping
    public String list(@RequestParam(required = false) String keyword, Model model) {
        boolean hasKeyword = keyword != null && !keyword.isBlank();
        model.addAttribute("medicines", hasKeyword
                ? medicineRepo.search(keyword.trim())
                : medicineRepo.findAllByOrderByNameAsc());
        model.addAttribute("keyword", keyword);
        model.addAttribute("active", "medicines");
        return "medicines/list";
    }

    @GetMapping("/new")
    public String addForm(Model model) {
        return showForm(model, new Medicine());
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        return showForm(model, medicineRepo.findById(id).orElseThrow());
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("medicine") Medicine medicine, BindingResult result,
                       Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return showForm(model, medicine);   // show the form again with error messages
        }
        // Empty supplier dropdown -> no supplier
        if (medicine.getSupplier() != null && medicine.getSupplier().getId() == null) {
            medicine.setSupplier(null);
        }
        medicineRepo.save(medicine);
        ra.addFlashAttribute("success", "Medicine '" + medicine.getName() + "' saved.");
        return "redirect:/medicines";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            medicineRepo.deleteById(id);
            ra.addFlashAttribute("success", "Medicine deleted.");
        } catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("error", "Cannot delete: this medicine has stock history.");
        }
        return "redirect:/medicines";
    }

    private String showForm(Model model, Medicine medicine) {
        // The supplier dropdown needs a (possibly empty) supplier object to bind to
        if (medicine.getSupplier() == null) medicine.setSupplier(new com.pharmacy.model.Supplier());
        model.addAttribute("medicine", medicine);
        model.addAttribute("suppliers", supplierRepo.findAllByOrderByNameAsc());
        model.addAttribute("active", "medicines");
        return "medicines/form";
    }
}
