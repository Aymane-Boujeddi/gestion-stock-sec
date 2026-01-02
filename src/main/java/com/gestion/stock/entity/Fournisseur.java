package com.gestion.stock.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "fournisseurs")
public class Fournisseur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom")
    private String nom;

    @Column(name = "adresse" , nullable = false)
    private String adresse;

    @Column(name = "personne_contact" , nullable = false)
    private String personneContact;

    @Column(name= "email" , nullable = false , unique = true)
    private String email;

    @Column(name = "telephone" , nullable = false, length = 20)
    private String telephone;

    @Column(name = "ville",nullable = false)
    private String ville;

    @Column(name= "raison_sociale", nullable = false)
    private String raisonSociale;

    @Column(name= "ICE" , nullable = false)
    private String ICE;

    @OneToMany(mappedBy = "fournisseur",cascade = CascadeType.ALL)
    private List<Commande> commandes;

    @Column(name = "created_at", nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;






    public Fournisseur() {
    }

    public Fournisseur(Long id, String nom, String adresse, String personneContact, String email, String telephone, String ville, String raisonSociale, String ICE) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.personneContact = personneContact;
        this.email = email;
        this.telephone = telephone;
        this.ville = ville;
        this.raisonSociale = raisonSociale;
        this.ICE = ICE;
    }

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate(){
        updatedAt = LocalDateTime.now();
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getPersonneContact() {
        return personneContact;
    }

    public void setPersonneContact(String personneContact) {
        this.personneContact = personneContact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getRaisonSociale() {
        return raisonSociale;
    }

    public void setRaisonSociale(String raisonSociale) {
        this.raisonSociale = raisonSociale;
    }

    public String getICE() {
        return ICE;
    }

    public void setICE(String ICE) {
        this.ICE = ICE;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
