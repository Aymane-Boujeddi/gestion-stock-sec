package com.gestion.stock.dto.response;

import com.gestion.stock.enums.StatutCommande;
import com.gestion.stock.dto.response.DetailsCommandeResponseDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CommandeResponseDTO {

    private Double montantTotale;
    private LocalDateTime dateCommande;
    private StatutCommande statutCommande;
    private String fournisseurNom;
    private List<DetailsCommandeResponseDTO> detailsCommandes;

    @Getter
    @Setter
    public static class DetailsCommandeResponseDTO {
        private int quantite;
        private Double prix;
        private String produitNom;
    }


}
