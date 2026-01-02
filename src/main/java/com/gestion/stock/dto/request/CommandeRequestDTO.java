package com.gestion.stock.dto.request;

import com.gestion.stock.enums.StatutCommande;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import com.gestion.stock.dto.request.DetailsCommandeRequestDTO;

@Getter
@Setter
public class CommandeRequestDTO {

    @NotNull(message = "Fournisseur ID cannot be null")
    private Long fournisseurId;

    @NotNull(message = "Statut commande cannot be null")
    private StatutCommande statutCommande;

    @NotEmpty(message = "Details produits cannot be empty")
    @Valid
    private List<DetailsCommandeRequestDTO> detailsCommande;

    @Getter
    @Setter
    public static class DetailsCommandeRequestDTO {

        @NotNull(message = "Produit ID cannot be null")
        private Long produitId;

        @Positive(message = "Quantite must be positive")
        private int quantite;

        @NotNull(message = "Prix cannot be null")
        @Positive(message = "Prix must be positive")
        private Double prix;
    }

}
