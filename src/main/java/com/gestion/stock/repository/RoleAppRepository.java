package com.gestion.stock.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.gestion.stock.enums.Role;
import com.gestion.stock.entity.Permission;
import com.gestion.stock.entity.RoleApp;

@Repository
public interface RoleAppRepository extends JpaRepository<RoleApp, Long> {
    Optional<RoleApp> findByName(Role name);

    /**
     * Fetch role with its default permissions
     */
    @Query("SELECT DISTINCT r FROM RoleApp r " +
           "LEFT JOIN FETCH r.defaultPermissions " +
           "WHERE r.id = :roleId")
    Optional<RoleApp> findRoleWithPermissions(@Param("roleId") Long roleId);

    /**
     * Get list of permissions for a specific role
     */
    @Query("SELECT p FROM RoleApp r " +
           "JOIN r.defaultPermissions p " +
           "WHERE r.name = :roleName")
    List<Permission> findPermissionsByRole(@Param("roleName") Role roleName);
}


