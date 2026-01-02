package com.gestion.stock.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BonSortieItemRequestDTO {

    @NotNull(message = "Produit ID cannot be null")
    private Long produitId;


    @Positive(message = "Quantite must be positive")
    private int quantite;
}
