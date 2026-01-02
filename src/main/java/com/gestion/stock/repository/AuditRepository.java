package com.gestion.stock.repository;

import com.gestion.stock.entity.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour les audits
 */
@Repository
public interface AuditRepository extends JpaRepository<Audit, Long> {
    
    /**
     * Trouver tous les audits d'un utilisateur par ID
     */
    List<Audit> findByUserIdOrderByTimestampDesc(Long userId);
    
    /**
     * Trouver tous les audits d'un utilisateur par username
     */
    List<Audit> findByUsernameOrderByTimestampDesc(String username);
    
    /**
     * Trouver tous les audits par type d'action
     */
    List<Audit> findByActionOrderByTimestampDesc(String action);
}
