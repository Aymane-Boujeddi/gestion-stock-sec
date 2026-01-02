package com.gestion.stock.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BonSortieRequestDto {

    private String numeroBon;


    @NotBlank(message = "L'atelier destinataire est obligatoire")
    @Size(max = 100, message = "Le nom de l'atelier ne doit pas dépasser 100 caractères")
    private String atelier;

    @NotBlank(message = "Le motif est obligatoire")
    @Size(max = 50, message = "Le motif ne doit pas dépasser 50 caractères")
    private String motif;

    @Size(max = 255, message = "Les détails du motif ne doivent pas dépasser 255 caractères")
    private String motifDetails;




    @NotEmpty(message = "La liste des produits ne peut pas être vide")
    private List<BonSortieItemRequestDTO> items;
}
