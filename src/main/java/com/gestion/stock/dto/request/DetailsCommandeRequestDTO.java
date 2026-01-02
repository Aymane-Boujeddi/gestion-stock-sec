package com.gestion.stock.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public  class DetailsCommandeRequestDTO {

    @NotNull(message = "Produit ID cannot be null")
    private Long produitId;

    @Positive(message = "Quantite must be positive")
    private int quantite;

    @NotNull(message = "Prix cannot be null")
    @Positive(message = "Prix must be positive")
    private Double prix;
}
