package com.gestion.stock.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gestion.stock.mapper.FournisseurMapper;
import jakarta.validation.constraints.*;

public class FournisseurCreateDTO {

    @NotBlank (message = "Nom is required")
    @Size(min = 2,max = 50, message = "Name must between 2 and 50 characters")
    private String nom;

    @NotBlank (message = "Adresse is required ")
    @Size(max = 100,message = "Adresse cannot be over 100 characters")
    private String adresse;

    @NotBlank (message = "personneContact is required")
    private String personneContact;

    @NotBlank (message = "Email is required")
    @Email (message = "Email format should be valid")
    private String email;

    @NotBlank (message = "Telephone is required")
    private String telephone;

    @NotBlank (message = "Ville est requise")
    private String ville;

    @NotBlank (message = "Raison Sociale is required")
    private String raisonSociale;

    @NotBlank (message = "ICE is required")
    @Size(min = 15, max = 15, message = "ICE must be 15 characters")
    @JsonProperty("ICE")
    private String ICE;




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
}
