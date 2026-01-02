package com.gestion.stock.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.gestion.stock.enums.StatutCommande;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "commandes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name= "montant_totale")
    private Double montantTotale;

    @Column(name = "date_commande")
    private LocalDateTime dateCommande;

    @Column(name = "statut_commande")
    @Enumerated(EnumType.STRING)
    private StatutCommande statutCommande;


    @ManyToOne
    @JoinColumn(name= "fournisseur_id")
    private Fournisseur fournisseur;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<DetailsCommande> detailsCommandes;

    @OneToMany(mappedBy = "commande",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Stock> stocks;






}
