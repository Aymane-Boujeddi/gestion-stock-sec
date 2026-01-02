package com.gestion.stock.dto.request;

import com.gestion.stock.enums.StatutCommande;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;


@Data
public class CommandeUpdateRequestDTO {

    private Long fournisseurId;
    private StatutCommande statutCommande;

    @Valid
    private List<DetailsCommandeUpdateRequestDTO> detailsCommande;
}
