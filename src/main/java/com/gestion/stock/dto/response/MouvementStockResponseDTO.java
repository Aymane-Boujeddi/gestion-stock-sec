package com.gestion.stock.dto.response;

import com.gestion.stock.enums.TypeMouvement;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class MouvementStockResponseDTO {

    private LocalDateTime dateMouvement;
    private String produitNom;
    private int quantite;
    private TypeMouvement typeMouvement;
    private String fournisseurNom;
    private Long commandeId;
    private Long stockId;
}
