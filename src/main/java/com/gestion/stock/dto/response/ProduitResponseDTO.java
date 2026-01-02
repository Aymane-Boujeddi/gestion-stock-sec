package com.gestion.stock.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProduitResponseDTO {

    private String nom;
    private String description;
    private double prixUnitaire;
    private String categorie;
    private int stockActuel;
    private int pointCommande;
    private String UniteMesure;

}
