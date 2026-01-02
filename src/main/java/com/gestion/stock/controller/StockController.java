package com.gestion.stock.controller;


import com.gestion.stock.dto.response.MouvementStockResponseDTO;
import com.gestion.stock.dto.response.ProduitResponseDTO;
import com.gestion.stock.dto.response.StockResponseDTO;
import com.gestion.stock.entity.MouvementStock;
import com.gestion.stock.entity.Stock;
import com.gestion.stock.repository.MouvementStockRepository;
import com.gestion.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;
    private final MouvementStockRepository mouvementStockRepository;


    @GetMapping
    @PreAuthorize("hasAuthority('STOCK_READ')")
    public ResponseEntity<List<StockResponseDTO>> getAllTheStock(){
        return ResponseEntity.ok(stockService.allStock());
    }

    @GetMapping("/produit/{id}")
    @PreAuthorize("hasAuthority('STOCK_VIEW_FIFO')")
    public ResponseEntity<Map<String , List<StockResponseDTO>>> getStocksByProductSortedFifo(@PathVariable Long id){
        return ResponseEntity.ok(stockService.stocksForProductSortedFifo(id));
    }

    @GetMapping("/mouvements")
    @PreAuthorize("hasAuthority('STOCK_VIEW_HISTORY')")
    public ResponseEntity<List<MouvementStockResponseDTO>> historiqueMouvementStock(){
        return ResponseEntity.ok(stockService.historiqueMouvement());
    }

    @GetMapping("/mouvements/produit/{id}")
    @PreAuthorize("hasAuthority('STOCK_VIEW_HISTORY')")
    public ResponseEntity<List<MouvementStockResponseDTO>> historiqueMouvementProduit(@PathVariable Long id){
        return ResponseEntity.ok(stockService.historiqueMouvementStockProduit(id));
    }

    @GetMapping("/alertes")
    @PreAuthorize("hasAuthority('PRODUIT_CONFIGURE_ALERT')")
    public ResponseEntity<List<ProduitResponseDTO>> produitUnderThreshold(){
        return ResponseEntity.ok(stockService.produitUnderThreshold());
    }

/// /////////////////////////////////////////////////////////////////////////
    @GetMapping("/test")
    public ResponseEntity<List<Object[]>> test(){
        return ResponseEntity.ok(mouvementStockRepository.findProductWithCount());
    }

}
