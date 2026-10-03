package com.pharmacy.controller;

import com.pharmacy.model.Sale;
import com.pharmacy.repository.SaleRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/sales")
public class SalesController {

    private final SaleRepository saleRepo;

    public SalesController(SaleRepository saleRepo) {
        this.saleRepo = saleRepo;
    }

    // Sales history, optional date filter: /sales?from=2026-10-01&to=2026-10-03
    @GetMapping
    public String history(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                          Model model) {
        List<Sale> sales;
        if (from != null || to != null) {
            LocalDate start = (from != null) ? from : LocalDate.of(2000, 1, 1);
            LocalDate end = (to != null) ? to : LocalDate.now();
            sales = saleRepo.findBySaleDateBetweenOrderBySaleDateDesc(start.atStartOfDay(), end.plusDays(1).atStartOfDay());
        } else {
            sales = saleRepo.findAllByOrderBySaleDateDesc();
        }
        model.addAttribute("sales", sales);
        model.addAttribute("totalRevenue", sales.stream().mapToDouble(Sale::getTotalAmount).sum());
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("active", "sales");
        return "sales/list";
    }

    // Single invoice (printable)
    @GetMapping("/{id}")
    public String invoice(@PathVariable Long id, Model model) {
        model.addAttribute("sale", saleRepo.findById(id).orElseThrow());
        model.addAttribute("active", "sales");
        return "sales/invoice";
    }
}
