package com.pharmacy;

import com.pharmacy.model.Medicine;
import com.pharmacy.model.Supplier;
import com.pharmacy.repository.MedicineRepository;
import com.pharmacy.repository.SupplierRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

// Adds some sample data the FIRST time you run the app (only if the database is empty)
@Component
public class DataLoader implements CommandLineRunner {

    private final SupplierRepository supplierRepo;
    private final MedicineRepository medicineRepo;

    public DataLoader(SupplierRepository supplierRepo, MedicineRepository medicineRepo) {
        this.supplierRepo = supplierRepo;
        this.medicineRepo = medicineRepo;
    }

    @Override
    public void run(String... args) {
        if (supplierRepo.count() > 0) return;

        Supplier s1 = supplier("MediCorp Distributors", "Ravi Kumar", "9876543210", "ravi@medicorp.com", "Mumbai");
        Supplier s2 = supplier("HealthPlus Pharma", "Anita Sharma", "9123456780", "anita@healthplus.com", "Delhi");

        medicine("Paracetamol 500mg", "Tablet", "Cipla", "PCM101", 2.5, 200, LocalDate.now().plusYears(1), s1);
        medicine("Amoxicillin 250mg", "Capsule", "Sun Pharma", "AMX202", 8.0, 8, LocalDate.now().plusMonths(8), s1);
        medicine("Cough Syrup 100ml", "Syrup", "Dabur", "CS303", 95.0, 40, LocalDate.now().plusDays(20), s2);
        medicine("Cetirizine 10mg", "Tablet", "Dr. Reddy's", "CTZ404", 3.0, 150, LocalDate.now().plusYears(2), s2);
        medicine("Vitamin C 500mg", "Tablet", "Abbott", "VTC505", 5.0, 5, LocalDate.now().plusMonths(14), s1);
    }

    private Supplier supplier(String name, String contact, String phone, String email, String address) {
        Supplier s = new Supplier();
        s.setName(name);
        s.setContactPerson(contact);
        s.setPhone(phone);
        s.setEmail(email);
        s.setAddress(address);
        return supplierRepo.save(s);
    }

    private void medicine(String name, String cat, String mfr, String batch, double price, int qty,
                          LocalDate expiry, Supplier supplier) {
        Medicine m = new Medicine();
        m.setName(name);
        m.setCategory(cat);
        m.setManufacturer(mfr);
        m.setBatchNumber(batch);
        m.setPrice(price);
        m.setQuantity(qty);
        m.setReorderLevel(10);
        m.setExpiryDate(expiry);
        m.setSupplier(supplier);
        medicineRepo.save(m);
    }
}
