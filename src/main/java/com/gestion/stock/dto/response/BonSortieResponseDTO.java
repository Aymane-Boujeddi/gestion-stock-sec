package com.gestion.stock.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BonSortieResponseDTO {


    private String numeroBon;

    private LocalDateTime dateSortie;

    private String atelierDestinataire;

    private String motif;

    private String motifDetails;

    private String statutBonSortie;

    private List<BonSortieItemResponseDTO> items;

    private LocalDateTime createdAt;
}
