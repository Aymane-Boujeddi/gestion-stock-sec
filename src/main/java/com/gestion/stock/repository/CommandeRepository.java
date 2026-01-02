package com.gestion.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.gestion.stock.entity.Commande;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommandeRepository extends JpaRepository<Commande , Long> {
    List<Commande> findAllByFournisseurId(Long fournisseurId);



}
