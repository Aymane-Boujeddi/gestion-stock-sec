package com.gestion.stock.dto.response;

import com.gestion.stock.entity.Commande;
import com.gestion.stock.entity.Produit;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StockResponseDTO {

    private String numeroLot;

    private LocalDateTime dateEntre;

    private int quantiteActuel;

    private Double prixAchat;

    private String nomProduit;

    private Long commandeId;
}
