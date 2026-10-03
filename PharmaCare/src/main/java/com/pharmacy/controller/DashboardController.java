package com.pharmacy.controller;

import com.pharmacy.model.Sale;
import com.pharmacy.repository.MedicineRepository;
import com.pharmacy.repository.SaleRepository;
import com.pharmacy.repository.SupplierRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
public class DashboardController {

    private final MedicineRepository medicineRepo;
    private final SupplierRepository supplierRepo;
    private final SaleRepository saleRepo;

    public DashboardController(MedicineRepository m, SupplierRepository s, SaleRepository sa) {
        this.medicineRepo = m;
        this.supplierRepo = s;
        this.saleRepo = sa;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        LocalDate today = LocalDate.now();
        List<Sale> todaySales = saleRepo.findBySaleDateBetweenOrderBySaleDateDesc(
                today.atStartOfDay(), today.plusDays(1).atStartOfDay());

        model.addAttribute("totalMedicines", medicineRepo.count());
        model.addAttribute("totalSuppliers", supplierRepo.count());
        model.addAttribute("todaySalesCount", todaySales.size());
        model.addAttribute("todayRevenue", todaySales.stream().mapToDouble(Sale::getTotalAmount).sum());
        model.addAttribute("lowStock", medicineRepo.findLowStock());
        model.addAttribute("expiring", medicineRepo.findByExpiryDateBeforeOrderByExpiryDate(today.plusDays(30)));
        model.addAttribute("recentSales", saleRepo.findTop5ByOrderBySaleDateDesc());
        model.addAttribute("active", "dashboard");
        return "dashboard";
    }
}
