package com.gestion.stock.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Audit entity - Système d'audit simple
 * Trace: QUI a fait QUOI et QUAND
 */
@Entity
@Table(name = "audits")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // QUI - Relation vers l'utilisateur
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // QUI - Qui a fait l'action (username pour référence rapide)
    @Column(length = 100)
    private String username;

    // QUOI - Quelle action (LOGIN, LOGOUT, GRANT_PERMISSION, etc.)
    @Column(nullable = false, length = 100)
    private String action;

    // QUAND - Quand l'action a été faite
    @Column(nullable = false)
    private LocalDateTime timestamp;

    // Détails de l'action (optionnel)
    @Column(columnDefinition = "TEXT")
    private String details;
    private String errorMessage;
}
