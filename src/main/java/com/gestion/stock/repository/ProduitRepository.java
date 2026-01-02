package com.gestion.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.gestion.stock.entity.Produit;

import java.util.List;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {





}
