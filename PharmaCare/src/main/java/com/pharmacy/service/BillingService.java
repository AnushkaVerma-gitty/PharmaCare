package com.pharmacy.service;

import com.pharmacy.model.Medicine;
import com.pharmacy.model.Sale;
import com.pharmacy.model.SaleItem;
import com.pharmacy.repository.MedicineRepository;
import com.pharmacy.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class BillingService {

    private final MedicineRepository medicineRepo;
    private final SaleRepository saleRepo;

    public BillingService(MedicineRepository medicineRepo, SaleRepository saleRepo) {
        this.medicineRepo = medicineRepo;
        this.saleRepo = saleRepo;
    }

    /**
     * Creates a bill, reduces stock, and saves the sale.
     * @Transactional = if anything fails, nothing is saved (stock stays correct).
     */
    @Transactional
    public Sale createBill(String customerName, String customerPhone, String paymentMode,
                           List<Long> medicineIds, List<Integer> quantities,
                           double discountPercent, double gstPercent) {

        if (medicineIds == null || medicineIds.isEmpty()) {
            throw new IllegalArgumentException("Add at least one medicine to the bill.");
        }

        Sale sale = new Sale();
        sale.setCustomerName(isBlank(customerName) ? "Walk-in Customer" : customerName.trim());
        sale.setCustomerPhone(customerPhone);
        sale.setPaymentMode(paymentMode);
        sale.setSaleDate(LocalDateTime.now());

        double subTotal = 0;
        for (int i = 0; i < medicineIds.size(); i++) {
            Long medId = medicineIds.get(i);
            Integer qty = quantities.get(i);
            if (medId == null || qty == null || qty <= 0) continue;   // skip empty rows

            Medicine med = medicineRepo.findById(medId)
                    .orElseThrow(() -> new IllegalArgumentException("Medicine not found."));

            if (med.isExpired()) {
                throw new IllegalArgumentException(med.getName() + " is expired and cannot be sold.");
            }
            if (med.getQuantity() < qty) {
                throw new IllegalArgumentException("Not enough stock for " + med.getName()
                        + ". Available: " + med.getQuantity());
            }

            // Reduce stock
            med.setQuantity(med.getQuantity() - qty);
            medicineRepo.save(med);

            SaleItem item = new SaleItem();
            item.setMedicineId(med.getId());
            item.setMedicineName(med.getName());
            item.setQuantity(qty);
            item.setUnitPrice(med.getPrice());
            item.setLineTotal(round(med.getPrice() * qty));
            sale.addItem(item);
            subTotal += item.getLineTotal();
        }

        if (sale.getItems().isEmpty()) {
            throw new IllegalArgumentException("Add at least one medicine with quantity above 0.");
        }

        // Totals: subtotal -> minus discount -> plus GST
        double discountAmount = round(subTotal * discountPercent / 100);
        double afterDiscount = subTotal - discountAmount;
        double gstAmount = round(afterDiscount * gstPercent / 100);

        sale.setSubTotal(round(subTotal));
        sale.setDiscountPercent(discountPercent);
        sale.setDiscountAmount(discountAmount);
        sale.setGstPercent(gstPercent);
        sale.setGstAmount(gstAmount);
        sale.setTotalAmount(round(afterDiscount + gstAmount));

        Sale saved = saleRepo.save(sale);
        // Invoice number like INV-20261003-0001
        saved.setInvoiceNumber("INV-" + saved.getSaleDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + "-" + String.format("%04d", saved.getId()));
        return saleRepo.save(saved);
    }

    private static double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
