package com.pharmacy.repository;

import com.pharmacy.model.StockEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockEntryRepository extends JpaRepository<StockEntry, Long> {
    List<StockEntry> findTop20ByOrderByEntryDateDesc();
}
