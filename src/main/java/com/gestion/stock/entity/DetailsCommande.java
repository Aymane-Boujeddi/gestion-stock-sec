package com.gestion.stock.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "details_commandes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetailsCommande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column (name = "quantite")
    private int quantite;

    @Column (name = "prix")
    private Double prix;

    @ManyToOne
    @JoinColumn(name = "produit_id")
    private Produit produit;

    @ManyToOne
    @JoinColumn(name = "commande_id")
    @JsonBackReference
    private Commande commande;



}
