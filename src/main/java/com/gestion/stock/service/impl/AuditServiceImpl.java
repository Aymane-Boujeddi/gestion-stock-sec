package com.gestion.stock.service.impl;

import com.gestion.stock.entity.Audit;
import com.gestion.stock.entity.User;
import com.gestion.stock.repository.AuditRepository;
import com.gestion.stock.repository.UserRepository;
import com.gestion.stock.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditRepository auditRepository;
    private final UserRepository userRepository;


    @Override
    @Transactional
    public void logAction(String username, String action, String details) {

            User user = userRepository.findUserByUsername(username).orElse(null);

            Audit audit = Audit.builder()
                    .user(user)
                    .username(username)
                    .action(action)
                    .timestamp(LocalDateTime.now())
                    .details(details)
                    .build();
            
            auditRepository.save(audit);

    }


    @Override
    @Transactional
    public void logLogin(String username) {
        logAction(username, "LOGIN", "Connexion");
    }


    @Override
    @Transactional
    public void logLogout(String username) {
        logAction(username, "LOGOUT", "Déconnexion");
    }


    @Override
    @Transactional
    public void logPermissionChange(String adminUsername, String targetUsername, 
                                   String permissionName, boolean granted) {
        String action = granted ? "GRANT_PERMISSION" : "REVOKE_PERMISSION";
        String details = String.format("%s la permission %s pour %s",
                granted ? "Accordé" : "Révoqué",
                permissionName, 
                targetUsername);
        
        logAction(adminUsername, action, details);
    }


    @Override
    @Transactional(readOnly = true)
    public List<Audit> getUserHistoryById(Long userId) {
        return auditRepository.findByUserIdOrderByTimestampDesc(userId);
    }


    @Override
    @Transactional(readOnly = true)
    public List<Audit> getUserHistory(String username) {
        return auditRepository.findByUsernameOrderByTimestampDesc(username);
    }


    @Override
    @Transactional(readOnly = true)
    public List<Audit> getRecentAudits(int limit) {
        return auditRepository.findAll(PageRequest.of(0, limit)).getContent();
    }
}
