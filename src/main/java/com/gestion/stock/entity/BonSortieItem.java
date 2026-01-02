package com.gestion.stock.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bons_sortie_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BonSortieItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bon_sortie_id")
    private BonSortie bonSortie;

    @ManyToOne
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @Column(nullable = false)
    private int quantite;
}
