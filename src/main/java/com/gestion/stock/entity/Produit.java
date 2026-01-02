package com.gestion.stock.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;


@Entity
@Table(name="produits")
public class Produit {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String reference;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    @Positive
    private double prixUnitaire;

    @Column(nullable = false)
    private String categorie;


    @Column(nullable = false)
    @PositiveOrZero
    private int stockActuel;

    @Column(nullable = false)
    private int pointCommande;

    @Column(name="unite_mesure" , nullable = false)
    private String UniteMesure;

    @OneToMany(mappedBy = "produit",cascade = CascadeType.ALL)
    private List<DetailsCommande> detailsCommandes;

    @OneToMany(mappedBy = "produit",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Stock> stocks;

    public Produit() {
    }

    public Produit(Long id, String reference, String nom, String description, double prixUnitaire, String categorie, int stockActuel, int pointCommande, String uniteMesure) {
        this.id = id;
        this.reference = reference;
        this.nom = nom;
        this.description = description;
        this.prixUnitaire = prixUnitaire;
        this.categorie = categorie;
        this.stockActuel = stockActuel;
        this.pointCommande = pointCommande;
        this.UniteMesure = uniteMesure;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public int getStockActuel() {
        return stockActuel;
    }

    public void setStockActuel(int stockActuel) {
        this.stockActuel = stockActuel;
    }

    public int getPointCommande() {
        return pointCommande;
    }

    public void setPointCommande(int pointCommande) {
        this.pointCommande = pointCommande;
    }

    public String getUniteMesure() {
        return UniteMesure;
    }

    public void setUniteMesure(String uniteMesure) {
        UniteMesure = uniteMesure;
    }
}
