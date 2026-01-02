package com.gestion.stock.entity;

import com.gestion.stock.enums.MotifType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bons_sortie")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BonSortie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numeroBon;

    @Column(name = "date_sortie", nullable = false)
    private LocalDateTime dateSortie;

    @Column(nullable = false)
    private String atelier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MotifType motif;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutBonSortie statut;

    @Column(name = "motif_details")
    private String motifDetails;

    @OneToMany(mappedBy = "bonSortie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BonSortieItem> items;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {

        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }
}
