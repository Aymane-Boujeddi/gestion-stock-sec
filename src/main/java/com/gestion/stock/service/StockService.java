package com.gestion.stock.service;

import com.gestion.stock.dto.response.MouvementStockResponseDTO;
import com.gestion.stock.dto.response.ProduitResponseDTO;
import com.gestion.stock.dto.response.StockResponseDTO;
import com.gestion.stock.entity.Commande;
import com.gestion.stock.entity.DetailsCommande;

import java.util.List;
import java.util.Map;

public interface StockService {

    public List<StockResponseDTO> createStockLotsAndMouvement(List<DetailsCommande> detailsCommandeList);
    public List<StockResponseDTO> allStock();
    public Map<String , List<StockResponseDTO>> stocksForProductSortedFifo(Long id);
    public List<MouvementStockResponseDTO> historiqueMouvement();
    public List<MouvementStockResponseDTO> historiqueMouvementStockProduit(Long id);
    public List<ProduitResponseDTO> produitUnderThreshold();
    public String valorisationStock();
}
