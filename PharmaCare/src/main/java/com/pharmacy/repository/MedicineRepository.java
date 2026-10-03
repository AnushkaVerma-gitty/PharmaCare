package com.pharmacy.repository;

import com.pharmacy.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    // Search by name, category, manufacturer or batch number
    @Query("SELECT m FROM Medicine m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "OR LOWER(m.category) LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "OR LOWER(m.manufacturer) LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "OR LOWER(m.batchNumber) LIKE LOWER(CONCAT('%', :kw, '%')) ORDER BY m.name")
    List<Medicine> search(@Param("kw") String keyword);

    @Query("SELECT m FROM Medicine m WHERE m.quantity <= m.reorderLevel ORDER BY m.quantity")
    List<Medicine> findLowStock();

    List<Medicine> findByExpiryDateBeforeOrderByExpiryDate(LocalDate date);

    List<Medicine> findByQuantityGreaterThanOrderByName(int qty);

    List<Medicine> findAllByOrderByNameAsc();
}
