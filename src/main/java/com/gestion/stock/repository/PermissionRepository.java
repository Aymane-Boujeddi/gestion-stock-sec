package com.gestion.stock.repository;

import com.gestion.stock.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    
    // Find permission by name
    Optional<Permission> findByName(String name);
    
    // Check if permission exists by name
    boolean existsByName(String name);
}
