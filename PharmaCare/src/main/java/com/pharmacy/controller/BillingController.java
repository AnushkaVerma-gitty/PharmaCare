package com.pharmacy.controller;

import com.pharmacy.model.Sale;
import com.pharmacy.repository.MedicineRepository;
import com.pharmacy.service.BillingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/billing")
public class BillingController {

    private final MedicineRepository medicineRepo;
    private final BillingService billingService;

    public BillingController(MedicineRepository medicineRepo, BillingService billingService) {
        this.medicineRepo = medicineRepo;
        this.billingService = billingService;
    }

    @GetMapping
    public String billingPage(Model model) {
        // Only show medicines that are in stock
        model.addAttribute("medicines", medicineRepo.findByQuantityGreaterThanOrderByName(0));
        model.addAttribute("active", "billing");
        return "billing/new";
    }

    @PostMapping("/create")
    public String createBill(@RequestParam(required = false) String customerName,
                             @RequestParam(required = false) String customerPhone,
                             @RequestParam(defaultValue = "Cash") String paymentMode,
                             @RequestParam(name = "medicineId", required = false) List<Long> medicineIds,
                             @RequestParam(name = "qty", required = false) List<Integer> quantities,
                             @RequestParam(defaultValue = "0") double discountPercent,
                             @RequestParam(defaultValue = "0") double gstPercent,
                             RedirectAttributes ra) {
        try {
            Sale sale = billingService.createBill(customerName, customerPhone, paymentMode,
                    medicineIds, quantities, discountPercent, gstPercent);
            ra.addFlashAttribute("success", "Bill " + sale.getInvoiceNumber() + " created.");
            return "redirect:/sales/" + sale.getId();   // open the invoice
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/billing";
        }
    }
}
