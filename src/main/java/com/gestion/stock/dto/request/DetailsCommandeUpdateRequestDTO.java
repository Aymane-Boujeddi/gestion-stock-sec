package com.gestion.stock.dto.request;


import com.gestion.stock.enums.StatutCommande;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class DetailsCommandeUpdateRequestDTO {

    @NotNull(message = "Details ID is required for update")
   private Long id;

   private Long produitId;

   @Positive(message = "Prix cannot be negative")
   private Double prix;

   @Positive(message = "Quantite cannot be negative")
   private int quantite;
}
