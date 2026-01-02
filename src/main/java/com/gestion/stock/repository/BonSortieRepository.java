package com.gestion.stock.repository;

import com.gestion.stock.entity.BonSortie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BonSortieRepository extends JpaRepository<BonSortie, Long> {
}
