package com.gestion.stock.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table (name = "stocks")
@Data
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "numero_lot")
    private String numeroLot;

    @Column(name = "date_entre")
    private LocalDateTime dateEntre;

    @Column(name = "quantite_actuel")
    private int quantiteActuel;

    @Column(name = "prix_achat")
    private Double prixAchat;


    @ManyToOne
    @JoinColumn(name = "produit_id")
    private Produit produit;

    @ManyToOne
    @JoinColumn(name = "commande_id")
    private Commande commande ;

    @OneToMany(mappedBy = "stock")
    private List<MouvementStock> mouvementStock;



}
