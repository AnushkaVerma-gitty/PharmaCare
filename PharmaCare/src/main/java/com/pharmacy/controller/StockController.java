package com.pharmacy.controller;

import com.pharmacy.model.Medicine;
import com.pharmacy.model.StockEntry;
import com.pharmacy.repository.MedicineRepository;
import com.pharmacy.repository.StockEntryRepository;
import com.pharmacy.repository.SupplierRepository;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/stock")
public class StockController {

    private final MedicineRepository medicineRepo;
    private final SupplierRepository supplierRepo;
    private final StockEntryRepository stockRepo;

    public StockController(MedicineRepository m, SupplierRepository s, StockEntryRepository st) {
        this.medicineRepo = m;
        this.supplierRepo = s;
        this.stockRepo = st;
    }

    @GetMapping
    public String stockPage(Model model) {
        model.addAttribute("medicines", medicineRepo.findAllByOrderByNameAsc());
        model.addAttribute("suppliers", supplierRepo.findAllByOrderByNameAsc());
        model.addAttribute("lowStock", medicineRepo.findLowStock());
        model.addAttribute("expiring", medicineRepo.findByExpiryDateBeforeOrderByExpiryDate(LocalDate.now().plusDays(30)));
        model.addAttribute("history", stockRepo.findTop20ByOrderByEntryDateDesc());
        model.addAttribute("active", "stock");
        return "stock/index";
    }

    // Add or remove stock. Use a negative number to remove (e.g. damaged items).
    @PostMapping("/update")
    @Transactional
    public String updateStock(@RequestParam Long medicineId,
                              @RequestParam int quantity,
                              @RequestParam(required = false) Long supplierId,
                              @RequestParam(required = false) String note,
                              RedirectAttributes ra) {
        Medicine med = medicineRepo.findById(medicineId).orElseThrow();
        int newQty = med.getQuantity() + quantity;
        if (quantity == 0 || newQty < 0) {
            ra.addFlashAttribute("error", "Invalid quantity. Current stock of " + med.getName() + " is " + med.getQuantity());
            return "redirect:/stock";
        }
        med.setQuantity(newQty);
        medicineRepo.save(med);

        StockEntry entry = new StockEntry();
        entry.setMedicine(med);
        entry.setQuantity(quantity);
        entry.setNote(note);
        entry.setEntryDate(LocalDateTime.now());
        if (supplierId != null) entry.setSupplier(supplierRepo.findById(supplierId).orElse(null));
        stockRepo.save(entry);

        ra.addFlashAttribute("success", "Stock updated. " + med.getName() + " now has " + newQty + " units.");
        return "redirect:/stock";
    }
}
