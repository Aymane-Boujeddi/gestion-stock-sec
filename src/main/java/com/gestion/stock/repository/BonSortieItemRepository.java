package com.gestion.stock.repository;

import com.gestion.stock.entity.BonSortieItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BonSortieItemRepository extends JpaRepository<BonSortieItem, Long> {
}
