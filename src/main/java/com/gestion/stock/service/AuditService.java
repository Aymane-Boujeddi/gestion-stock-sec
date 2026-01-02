package com.gestion.stock.service;

import com.gestion.stock.entity.Audit;

import java.util.List;


public interface AuditService {
    

    void logAction(String username, String action, String details);
    

    void logLogin(String username);

    void logLogout(String username);
    

    void logPermissionChange(String adminUsername, String targetUsername, 
                            String permissionName, boolean granted);
    

    List<Audit> getUserHistoryById(Long userId);
    

    List<Audit> getUserHistory(String username);

    List<Audit> getRecentAudits(int limit);
}
