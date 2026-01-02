package com.gestion.stock.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetailsCommandeResponseDTO {
    private int quantite;
    private Double prix;
    private String produitNom;
}
